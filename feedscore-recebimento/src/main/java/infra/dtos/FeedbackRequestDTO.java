package infra.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequestDTO(
        @NotNull
        @NotBlank(message = "Digite uma descrição")
        String description,
        @NotNull(message = "Digite uma nota")
        int score
) {
}
