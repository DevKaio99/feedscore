package domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UrgencyTest {

    @ParameterizedTest(name = "nota {0} deve ser {1}")
    @DisplayName("Classificação de urgência por nota")
    @CsvSource({
            "0, CRITICAL",
            "4, CRITICAL",
            "5, MEDIUM",
            "7, MEDIUM",
            "8, LOW",
            "10, LOW"
    })
    void deveClassificarUrgenciaPelaNota(int nota, Urgency esperada) {
        assertEquals(esperada, Urgency.fromScore(nota));
    }

    @ParameterizedTest(name = "nota {0} deve ser rejeitada")
    @DisplayName("Notas fora da faixa devem lançar exceção")
    @ValueSource(ints = {-1, 11})
    void deveRejeitarNotaForaDaFaixa(int nota) {
        assertThrows(IllegalArgumentException.class, () -> Urgency.fromScore(nota));
    }
}

