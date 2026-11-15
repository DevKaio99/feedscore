package infra.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FeedbackRequestDTO(
        @JsonProperty("descricao")
        @NotBlank(message = "Digite uma descrição")
        String description,

        @JsonProperty("nota")
        @NotNull(message = "Digite uma nota")
        @Min(value = 0, message = "A nota deve ser no mínimo 0")
        @Max(value = 10, message = "A nota deve ser no máximo 10")
        Integer score
) {
}
