package infra.handlers;

import application.usecases.NotifyCriticalFeedbackUseCase;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import infra.dtos.CriticalFeedbackMessageDTO;
import infra.mappers.CriticalFeedbackMapper;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import org.jboss.logging.Logger;

public class CriticalFeedbackHandler implements RequestHandler <SQSEvent, SQSBatchResponse> {

    private static final Logger LOG = Logger.getLogger(CriticalFeedbackHandler.class);

    private final ObjectMapper objectMapper;
    private final CriticalFeedbackMapper criticalFeedbackMapper;
    private final NotifyCriticalFeedbackUseCase notifyCriticalFeedbackUseCase;

    @Inject
    public CriticalFeedbackHandler(ObjectMapper objectMapper, CriticalFeedbackMapper criticalFeedbackMessageMapper, NotifyCriticalFeedbackUseCase notifyCriticalFeedbackUseCase) {
        this.objectMapper = objectMapper;
        this.criticalFeedbackMapper = criticalFeedbackMessageMapper;
        this.notifyCriticalFeedbackUseCase = notifyCriticalFeedbackUseCase;
    }

    @Override
    public SQSBatchResponse handleRequest(SQSEvent input, Context context) {
        List<SQSBatchResponse.BatchItemFailure> fails = new ArrayList<>();

        for (SQSEvent.SQSMessage message : input.getRecords()) {
            try {
                CriticalFeedbackMessageDTO dto = objectMapper.readValue(message.getBody(), CriticalFeedbackMessageDTO.class);

                notifyCriticalFeedbackUseCase.execute(criticalFeedbackMapper.toInput(dto));

                LOG.infof("Alerta enviado para a mensagem %s", message.getMessageId());
            } catch (Exception e) {
                LOG.errorf(e, "Falha ao processar a mensagem %s", message.getMessageId());
                fails.add(new SQSBatchResponse.BatchItemFailure(message.getMessageId()));
            }
        }
        return new SQSBatchResponse(fails);
    }
}
