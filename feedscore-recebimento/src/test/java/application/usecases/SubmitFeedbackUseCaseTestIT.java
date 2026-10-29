package application.usecases;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@QuarkusTest
public class SubmitFeedbackUseCaseTestIT {

    @Test
    @DisplayName("Deve registrar um feedback válido e retornar 201")
    void deveRegistrarFeedback () {
        given()
                .contentType(ContentType.JSON)
                .body("""
                     {
                      "description": "Aula ótima",
                      "score": "9"
                     }
                     """)
                .when()
                .post("/avaliacao")
                .then().statusCode(201).body("id", notNullValue()).body("urgency", equalTo("Baixa"));

    }

    @ParameterizedTest(name = "body inválido: {0}")
    @DisplayName("Deve rejeitar feedbacks inválidos com 400")
    @ValueSource(strings = {
            "{ \"descricao\": \"Aula boa\", \"nota\": 15 }",
            "{ \"descricao\": \"Aula boa\", \"nota\": -1 }",
            "{ \"descricao\": \"Aula boa\" }",
            "{ \"descricao\": \"   \", \"nota\": 8 }"
    })
    void deveRejeitarFeedbackInvalido(String body) {
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/avaliacao")
                .then()
                .statusCode(400);
    }
}
