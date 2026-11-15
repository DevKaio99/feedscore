package infra.controllers;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class ReceiveFeedbackControllerTest {

    @Test
    @DisplayName("Deve aceitar o JSON exatamente como definido no enunciado")
    void deveAceitarContratoDoEnunciado() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                  { "descricao": "Aula muito boa", "nota": 9 }
                  """)
                .when()
                .post("/avaliacao")
                .then()
                .statusCode(201)
                .body("score", equalTo(9));
    }

    @Test
    @DisplayName("Deve recusar avaliação sem nota")
    void deveRecusarSemNota() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                  { "descricao": "Esqueci a nota" }
                  """)
                .when()
                .post("/avaliacao")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deve recusar nota acima de 10")
    void deveRecusarNotaForaDaFaixa() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                  { "descricao": "Nota inválida", "nota": 11 }
                  """)
                .when()
                .post("/avaliacao")
                .then()
                .statusCode(400);
    }
}