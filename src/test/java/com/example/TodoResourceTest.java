package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class TodoResourceTest {

    @Test
    public void shouldAddTodo() {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"title\": \"dog\", \"description\": \"walk the dog\"}")
                .when().post("/todos");
        response.then().statusCode(200);

        assertThat(response.jsonPath().getLong("id")).isNotNull();
    }

    @Test
    public void shouldListTodos() {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"title\": \"cat\", \"description\": \"feed the cat\"}")
                .when().post("/todos");
        response.then().statusCode(200);

        long id = response.jsonPath().getLong("id");

        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/todos");

        assertThat(response.statusCode()).isEqualTo(200);

        assertThat(
                response.jsonPath()
                        .getString(String.format("find {it.id == %s}.todoState", id)))
                .isEqualTo("NEW");
    }

    @Test
    public void shouldMarkTodoDone() {
        Response response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"title\": \"cat\", \"description\": \"feed the cat\"}")
                .when().post("/todos");
        response.then().statusCode(200);

        long id = response.jsonPath().getLong("id");

        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"title\": \"cat\", \"description\": \"feed the cat\", \"id\": " + id + ", \"todoState\" :\"DONE\"}")
                .when().put("/todos/" + id);
        response.then().statusCode(200);


        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get("/todos");

        assertThat(response.statusCode()).isEqualTo(200);

        assertThat(
                response.jsonPath()
                        .getString(String.format("find {it.id == %s}.todoState", id)))
                .isEqualTo("DONE");
    }

}