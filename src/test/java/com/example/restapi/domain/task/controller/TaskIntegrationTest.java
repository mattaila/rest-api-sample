package com.example.restapi.domain.task.controller;

import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDate;

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
            .body("title", equalTo("Vue.js"))
            .body("comment", equalTo("Vue3学習"))
            .body("progress", equalTo(20))
            .body("deadline", equalTo("2025-10-30"));
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
            .body("results[1].id", equalTo(2));
    }

    @Test
    void タスク作成_ステータスコード201() {
        TaskForm form = new TaskForm("task4", 25, LocalDate.of(2025,3,30));
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
            .body("title", equalTo("task4"))
            .body("progress", equalTo(25))
            .body("deadline", equalTo("2025-03-30"));

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
        TaskForm form = new TaskForm("updated", 30, LocalDate.of(2026,4,1));
        form.setComment("comment updated");
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
            .body("title", equalTo("updated"))
            .body("comment", equalTo("comment updated"))
            .body("progress", equalTo(30))
            .body("deadline", equalTo("2026-04-01"));

        //更新されているか確認
        RestAssured.given()
            .port(port)
            .accept(ContentType.JSON)
        .when()
            .get("/tasks/1")
        .then()
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", equalTo("updated"))
            .body("comment", equalTo("comment updated"))
            .body("progress", equalTo(30))
            .body("deadline", equalTo("2026-04-01"));
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
        TaskForm form = new TaskForm("updated", 10, LocalDate.of(2025,1,1));
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
