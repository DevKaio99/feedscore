package application.usecases;

import application.gateways.CriticalFeedbackPublisher;
import application.inputs.FeedbackCreateInput;
import domain.entities.Feedback;
import domain.repositories.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitFeedbackUseCaseTest {

    @Mock
    private FeedbackRepository feedbackRepository;
    @Mock
    private CriticalFeedbackPublisher criticalFeedbackPublisher;

    private SubmitFeedbackUseCase submitFeedbackUseCase;

    @BeforeEach void setUp () {
        submitFeedbackUseCase = new SubmitFeedbackUseCase(feedbackRepository, criticalFeedbackPublisher);
    }

    @Test
    @DisplayName("Cria feedback sem publicar")
    void deveCriarFeedbackSemPublicar () {
        FeedbackCreateInput feedbackCreateInput = new FeedbackCreateInput(
            "Descrição teste",
            8);

        when(feedbackRepository.saveFeedback(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Feedback resultado = submitFeedbackUseCase.execute(feedbackCreateInput);

        assertNotNull(resultado);
        assertEquals(8, resultado.getScore());

        verify(feedbackRepository).saveFeedback(any());
        verify(criticalFeedbackPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Cria feedback e publica para a fila")
    void deveCriarFeedbackEPublicar () {
        FeedbackCreateInput feedbackCreateInput = new FeedbackCreateInput(
                "Descrição teste",
                4);

        when(feedbackRepository.saveFeedback(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Feedback resultado = submitFeedbackUseCase.execute(feedbackCreateInput);

        assertNotNull(resultado);
        assertEquals(4, resultado.getScore());

        verify(feedbackRepository).saveFeedback(any());
        verify(criticalFeedbackPublisher).publish(any());
    }

    @Test
    @DisplayName("Não deve criar Feedback quando nota fora da faixa")
    void naoDeveCriarFeedbackComNotaForaDaFaixa () {
        FeedbackCreateInput feedbackCreateInput = new FeedbackCreateInput(
                "Descrição teste",
                15);
        assertThrows(IllegalArgumentException.class, () -> submitFeedbackUseCase.execute(feedbackCreateInput));

        verify(feedbackRepository, never()).saveFeedback(any());
        verify(criticalFeedbackPublisher, never()).publish(any());
    }

}
