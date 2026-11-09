package infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import infra.dtos.CriticalFeedbackMessageDTO;
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
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Test
    @DisplayName("A mensagem publicada deve seguir o contrato da fila")
    void deveSerializarConformeOContrato() throws Exception {
        CriticalFeedbackMessageDTO mensagem = new CriticalFeedbackMessageDTO(
                "Sem áudio no módulo 3",
                "Crítica",
                Instant.parse("2026-10-16T20:04:00Z")
        );

        String jsonGerado = objectMapper.writeValueAsString(mensagem);

        assertEquals(objectMapper.readTree(CONTRATO), objectMapper.readTree(jsonGerado));
    }
}
