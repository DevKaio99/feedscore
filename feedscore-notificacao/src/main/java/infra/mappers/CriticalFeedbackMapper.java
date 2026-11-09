package infra.mappers;

import application.inputs.FeedbackNotificationCreateInput;
import infra.dtos.CriticalFeedbackMessageDTO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CriticalFeedbackMapper {

    public FeedbackNotificationCreateInput toInput(CriticalFeedbackMessageDTO dto) {
        return new FeedbackNotificationCreateInput(
                dto.description(),
                dto.urgency(),
                dto.createdAt()
                );
    }
}
