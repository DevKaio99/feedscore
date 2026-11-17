# FeedScore

Plataforma serverless de feedback de aulas, desenvolvida para o Tech Challenge da Fase 4 da Pós Tech ADJ (FIAP). Estudantes avaliam as aulas, avaliações críticas geram alerta imediato por e-mail aos administradores e, toda segunda-feira, um relatório semanal com a média das avaliações é enviado automaticamente.

Tudo roda na AWS, com três funções Lambda em Java 25 + Quarkus compiladas como binários nativos, infraestrutura descrita com AWS SAM e deploy automatizado com GitHub Actions.

## Arquitetura

```
Estudante ──► API Gateway (API key) ──► Lambda Recebimento ──► DynamoDB (tabela + índice por semana)
                                               │
                                               └─(só críticas)──► SQS (+ DLQ) ──► Lambda Notificação ──► SES

EventBridge (segunda 08h) ──► Lambda Relatório ──► Query no índice ──► SES

CloudWatch (logs, alarmes, dashboard) ──► SNS ──► e-mail do administrador
```

| Função | Responsabilidade | Gatilho | Permissões |
|---|---|---|---|
| `feedscore-recebimento` | Registra a avaliação e publica as críticas na fila | API Gateway `POST /avaliacao` | `dynamodb:PutItem`, `sqs:SendMessage` |
| `feedscore-notificacao` | Envia o e-mail de urgência | SQS (lotes de até 5) | `ses:SendEmail` |
| `feedscore-relatorio` | Gera e envia o relatório da semana anterior | EventBridge (segunda, 08h) | `dynamodb:Query` no índice, `ses:SendEmail` |

Cada módulo segue Clean Architecture (`domain`, `application`, `infra`) e cada função tem uma role IAM própria, com acesso apenas ao que utiliza.

## Endpoint

`POST /avaliacao` (header `x-api-key` obrigatório na AWS)

```json
{ "descricao": "Áudio cortando no módulo 3", "nota": 2 }
```

| Nota | Urgência |
|---|---|
| 0 a 4 | Crítica (gera alerta por e-mail) |
| 5 a 7 | Média |
| 8 a 10 | Baixa |

Respostas: `201` criado, `400` dados inválidos, `403` sem API key, `429` limite de uso excedido.

## Segurança e monitoramento

- API key obrigatória com usage plan (5 req/s e 1.000 req/dia)
- Menor privilégio por função (IAM), criptografia em repouso no DynamoDB e no SQS
- Nenhum segredo no repositório; deploy via OIDC, sem access keys
- Logs com retenção de 7 dias e sem dados pessoais
- Alarmes para erros 5xx da API, falhas das funções e mensagens na DLQ, com notificação por e-mail (SNS)
- Dashboard `feedscore-dashboard` no CloudWatch

## Como rodar localmente

Pré-requisitos: JDK 25 e Docker em execução (os testes e o dev mode sobem um LocalStack automaticamente).

```bash
./mvnw clean package                                  # testes de todos os módulos
cd feedscore-recebimento && ../mvnw quarkus:dev       # dev mode (porta 8080)

curl -i -X POST http://localhost:8080/avaliacao \
  -H "Content-Type: application/json" \
  -d '{"descricao":"Aula confusa","nota":3}'
```

| Módulo | Porta |
|---|---|
| Recebimento | 8080 |
| Notificação | 8082 |
| Relatório | 8083 |

## Deploy na AWS

Pré-requisitos: AWS CLI 2.32+, SAM CLI, Docker e remetente/destinatários verificados no Amazon SES.

```bash
aws login --profile feedscore && export AWS_PROFILE=feedscore
./mvnw package -Dnative -Dquarkus.native.container-build=true
sam deploy --guided        # parâmetros: SesSender, SesRecipients, AlarmEmail
```

Deploy automatizado: a cada push na `main`, o workflow `.github/workflows/deploy.yml` roda os testes, gera o build nativo e executa o `sam deploy`, autenticando na AWS via OIDC. As roles do pipeline são criadas uma única vez pelo `pipeline.yaml`:

```bash
aws cloudformation deploy --template-file pipeline.yaml \
  --stack-name feedscore-pipeline --capabilities CAPABILITY_IAM
```

Secrets do repositório: `AWS_DEPLOY_ROLE_ARN`, `AWS_CFN_EXECUTION_ROLE_ARN`, `SES_SENDER`, `SES_RECIPIENTS` e `ALARM_EMAIL`.

Para remover tudo: `sam delete --stack-name feedscore`, depois `aws cloudformation delete-stack --stack-name feedscore-pipeline`.

## Tecnologias

- Java 25 / Quarkus 3.33 (build nativo com GraalVM/Mandrel)
- AWS Lambda, API Gateway, DynamoDB, SQS, SES, EventBridge
- Amazon CloudWatch e SNS
- AWS SAM / CloudFormation
- GitHub Actions com OIDC
- JUnit, Mockito, REST Assured e LocalStack