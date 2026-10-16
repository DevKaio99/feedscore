package infra.controllers;

import application.inputs.FeedbackCreateInput;
import application.usecases.SubmitFeedbackUseCase;
import infra.dtos.FeedbackRequestDTO;
import infra.dtos.FeedbackResponseDTO;
import infra.mappers.FeedbackMapper;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/avaliacao")
public class ReceiveFeedbackController {

    private final FeedbackMapper feedbackMapper;
    private final SubmitFeedbackUseCase submitFeedbackUseCase;

    public ReceiveFeedbackController(FeedbackMapper feedbackMapper, SubmitFeedbackUseCase submitFeedbackUseCase) {
        this.feedbackMapper = feedbackMapper;
        this.submitFeedbackUseCase = submitFeedbackUseCase;
    }

    @POST
    @ResponseStatus(201)
    @Produces (MediaType.APPLICATION_JSON)
    public FeedbackResponseDTO create (@Valid FeedbackRequestDTO dto) {
        FeedbackCreateInput feedbackCreateInput = feedbackMapper.toInput(dto);

        var feedbackCreated = submitFeedbackUseCase.execute(feedbackCreateInput);

        return feedbackMapper.toDTO(feedbackCreated);
    }

}
