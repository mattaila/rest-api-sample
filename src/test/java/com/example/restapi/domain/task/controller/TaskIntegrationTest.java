package com.example.restapi.domain.task.controller;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.example.restapi.generated.model.TaskForm;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskIntegrationTest {

    @LocalServerPort
    int port;

    @Test
    void タスク取得_ステータスコード200() {

        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
        .when()
            .get("/tasks/1")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", equalTo("task1"));
    }

    @Test
    void タスク取得_ステータスコード404() {

        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
        .when()
            .get("/tasks/999")
        .then()
            .statusCode(404);
    }

    @Test
    void タスク全取得_ステータスコード200() {
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
            .param("limit", "2")
            .param("offset", "0")
        .when()
            .get("/tasks/")
        .then()
            .statusCode(200)
            .body("page.limit", equalTo(2))
            .body("page.offset", equalTo(0))
            .body("page.size", equalTo(2))
            .body("results.size()", equalTo(2))
            .body("results[0].id", equalTo(1))
            .body("results[1].id", equalTo(2))
            .body("results[1].title", equalTo("task2"));
    }

    @Test
    void タスク作成_ステータスコード201() {
        TaskForm form = new TaskForm("task4");
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(form)
        .when()
            .post("/tasks/")
        .then()
            .statusCode(201)
            .body("id", equalTo(4))
            .body("title", equalTo("task4"));

        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
        .when()
            .get("/tasks/4")
        .then()
            .statusCode(200)
            .body("id", equalTo(4))
            .body("title", equalTo("task4"));
    }

    @Test
    void タスク作成_ステータスコード400() {
        TaskForm form = new TaskForm();
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(form)
        .when()
            .post("/tasks/")
        .then()
            .statusCode(400);
    }

    @Test
    void タスク更新_ステータスコード200() {
        TaskForm form = new TaskForm("updated");
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(form)
        .when()
            .put("/tasks/1")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", equalTo("updated"));

        //更新されているか確認
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
        .when()
            .get("/tasks/1")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", equalTo("updated"));
    }

    @Test
    void タスク更新_ステータスコード400() {
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .put("/tasks/1")
        .then()
            .statusCode(400);
    }

    @Test
    void タスク更新_ステータスコード404() {
        TaskForm form = new TaskForm("updated");
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
            .body(form)
        .when()
            .put("/tasks/999")
        .then()
            .statusCode(404);
    }

    @Test
    void タスク削除_ステータスコード204() {
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .delete("/tasks/3")
        .then()
            .statusCode(204);

        //削除されているか確認
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
        .when()
            .get("/tasks/3")
        .then()
            .statusCode(404);
    }

    @Test
    void タスク削除_ステータスコード404() {
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
            .contentType(ContentType.JSON)
        .when()
            .delete("/tasks/999")
        .then()
            .statusCode(404);
    }
}
