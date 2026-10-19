package application.usecase;

import application.gateways.CriticalFeedbackPublisher;
import application.inputs.FeedbackCreateInput;
import application.usecases.SubmitFeedbackUseCase;
import domain.entities.Feedback;
import domain.repositories.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void deveCriarFeedback () {
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


}
