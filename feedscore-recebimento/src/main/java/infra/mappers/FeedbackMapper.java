package infra.mappers;

import application.inputs.FeedbackCreateInput;
import domain.entities.Feedback;
import infra.dtos.FeedbackRequestDTO;
import infra.dtos.FeedbackResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FeedbackMapper {

    public FeedbackCreateInput toInput (FeedbackRequestDTO feedbackRequestDTO) {
        return new FeedbackCreateInput(
                feedbackRequestDTO.description(),
                feedbackRequestDTO.score()
        );
    }

    public FeedbackResponseDTO toDTO (Feedback feedback) {
        return new FeedbackResponseDTO(
                feedback.getId(),
                feedback.getDescription(),
                feedback.getScore(),
                feedback.getUrgency().getLabel() ,
                feedback.getCreatedAt()
        );
    }
}
