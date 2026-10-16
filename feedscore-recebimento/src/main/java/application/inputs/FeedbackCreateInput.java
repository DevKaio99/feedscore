package application.inputs;

public record FeedbackCreateInput (
        String description,
        int score
){
}
