package application.gateways;

import domain.entities.Feedback;

public interface CriticalFeedbackPublisher {

    void publish (Feedback feedback);

}
