package infra.dtos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CriticalFeedbackMessageContractTest {

    private static final String CONTRATO = """
            {
              "description": "Sem áudio no módulo 3",
              "urgency": "Crítica",
              "createdAt": "2026-10-16T20:04:00Z"
            }
            """;

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @Test
    @DisplayName("A mensagem da fila deve ser lida conforme o contrato")
    void deveDesserializarConformeOContrato() throws Exception {
        CriticalFeedbackMessageDTO mensagem =
                objectMapper.readValue(CONTRATO, CriticalFeedbackMessageDTO.class);

        assertEquals("Sem áudio no módulo 3", mensagem.description());
        assertEquals("Crítica", mensagem.urgency());
        assertEquals(Instant.parse("2026-10-16T20:04:00Z"), mensagem.createdAt());
    }
}