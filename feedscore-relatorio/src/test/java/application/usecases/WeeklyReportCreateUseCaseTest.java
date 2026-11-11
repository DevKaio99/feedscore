package application.usecases;

import application.gateways.EmailReport;
import application.gateways.EmailSender;
import domain.enums.Urgency;
import domain.models.FeedbackSummary;
import domain.repositories.FeedbackReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeeklyReportCreateUseCaseTest {

    // Segunda-feira, 16/11/2026, 8h em São Paulo: a semana anterior é a 2026-W46
    private static final Clock CLOCK_FIXO = Clock.fixed(Instant.parse("2026-11-16T11:00:00Z"), ZoneOffset.UTC);
    private static final String SEMANA_ANTERIOR = "2026-W46";

    @Mock
    private FeedbackReader feedbackReader;

    @Mock
    private EmailSender emailSender;

    @Captor
    private ArgumentCaptor<EmailReport> emailCaptor;

    private WeeklyReportCreateUseCase weeklyReportCreateUseCase;

    @BeforeEach
    void setUp() {
        weeklyReportCreateUseCase = new WeeklyReportCreateUseCase(feedbackReader, emailSender, CLOCK_FIXO);
    }

    @Test
    @DisplayName("Deve buscar os feedbacks da semana anterior")
    void deveBuscarFeedbacksDaSemanaAnterior() {
        when(feedbackReader.findByWeek(any())).thenReturn(List.of());

        weeklyReportCreateUseCase.execute();

        verify(feedbackReader).findByWeek(SEMANA_ANTERIOR);
    }

    @Test
    @DisplayName("Deve montar o relatório com média, contagens e lista de feedbacks")
    void deveMontarRelatorioComMediaEContagens() {
        List<FeedbackSummary> feedbacks = List.of(
                new FeedbackSummary("Sem áudio no módulo 3", 2, Urgency.CRITICAL, Instant.parse("2026-11-09T13:00:00Z")),
                new FeedbackSummary("Ritmo um pouco lento", 6, Urgency.MEDIUM, Instant.parse("2026-11-10T15:00:00Z")),
                // 01h de segunda em UTC, mas ainda domingo 22h em São Paulo: deve contar no dia 15/11
                new FeedbackSummary("Aula excelente", 10, Urgency.LOW, Instant.parse("2026-11-16T01:00:00Z"))
        );
        when(feedbackReader.findByWeek(SEMANA_ANTERIOR)).thenReturn(feedbacks);

        weeklyReportCreateUseCase.execute();

        verify(emailSender).send(emailCaptor.capture());
        EmailReport email = emailCaptor.getValue();
        String corpo = email.emailBody();

        assertTrue(email.subject().contains(SEMANA_ANTERIOR));

        assertTrue(corpo.contains("Total de avaliações: 3"));
        assertTrue(corpo.contains("Média de avaliações: 6,0"));

        assertTrue(corpo.contains("- 09/11/2026: 1"));
        assertTrue(corpo.contains("- 10/11/2026: 1"));
        assertTrue(corpo.contains("- 15/11/2026: 1"));

        assertTrue(corpo.contains("- Crítica: 1"));
        assertTrue(corpo.contains("- Média: 1"));
        assertTrue(corpo.contains("- Baixa: 1"));

        assertTrue(corpo.contains("Sem áudio no módulo 3"));
        assertTrue(corpo.contains("15/11/2026 22:00"));
    }

    @Test
    @DisplayName("Deve enviar relatório informando que não houve avaliações")
    void deveEnviarRelatorioVazio() {
        when(feedbackReader.findByWeek(SEMANA_ANTERIOR)).thenReturn(List.of());

        weeklyReportCreateUseCase.execute();

        verify(emailSender).send(emailCaptor.capture());
        EmailReport email = emailCaptor.getValue();

        assertTrue(email.subject().contains(SEMANA_ANTERIOR));
        assertTrue(email.emailBody().contains("Nenhuma avaliação foi recebida no período."));
    }
}