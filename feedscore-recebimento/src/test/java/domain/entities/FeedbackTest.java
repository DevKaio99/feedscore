package domain.entities;

import domain.enums.Urgency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackTest {

    @Test
    @DisplayName("Deve criar um Feedback")
    void deveCriarFeedback () {
        Feedback feedback = Feedback.create(
                "Descrição Teste",
                8);

        assertNotNull(feedback.getId());
        assertEquals("Descrição Teste", feedback.getDescription());
        assertEquals(8, feedback.getScore());
    }

    @Test
    @DisplayName("Não deve criar se nota fora da faixa")
    void naoDeveCriarSeNotaForaDaFaixa () {
        assertThrows(IllegalArgumentException.class,
                () -> Feedback.create("Descrição teste", 15));
    }

    @ParameterizedTest(name = "nota {0} deve ser {1} (crítico: {2})")
    @DisplayName("Feedback deve calcular a urgência pela nota")
    @CsvSource({
            "2, CRITICAL, true",
            "6, MEDIUM, false",
            "9, LOW, false"
    })
    void deveCalcularUrgenciaPelaNota(int nota, Urgency urgenciaEsperada, boolean criticoEsperado) {
        Feedback feedback = Feedback.create("Teste", nota);

        assertEquals(urgenciaEsperada, feedback.getUrgency());
        assertEquals(criticoEsperado, feedback.isCritical());
    }
}
