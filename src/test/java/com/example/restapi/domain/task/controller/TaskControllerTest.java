package com.example.restapi.domain.task.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.restapi.common.CustomExceptionHandler;
import com.example.restapi.domain.task.service.TaskEntity;
import com.example.restapi.domain.task.service.TaskEntityNotFoundException;
import com.example.restapi.domain.task.service.TaskService;
import com.example.restapi.generated.model.TaskForm;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TaskController.class)
@Import(CustomExceptionHandler.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private TaskController controller;

    @MockitoBean
    private TaskService service;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void showTask_ステータスコード200() throws Exception {
        Mockito.when(service.find(anyLong()))
            .thenReturn(new TaskEntity(1L, "test"));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("test"));
    }

    @Test
    void showTask_ステータスコード404() throws Exception {
        Mockito.when(service.find(anyLong()))
            .thenThrow(new TaskEntityNotFoundException(1));

        //存在しないID指定
        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/999")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void listTasks_ステータスコード200() throws Exception {
        Mockito.when(service.find(anyInt(), anyLong()))
            .thenReturn(List.of(
                    new TaskEntity(1L, "test"),
                    new TaskEntity(2L, "test2")));

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/")
            .param("limit", "10")
            .param("offset", "1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.page.limit").value(10))
            .andExpect(MockMvcResultMatchers.jsonPath("$.page.offset").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.page.size").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].title").value("test"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.results[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.results[1].title").value("test2"));
        }

    @Test
    void createTask_ステータスコード201() throws Exception {

        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("title");
        Mockito.when(service.create(anyString()))
            .thenReturn(new TaskEntity(1L, "title"));

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(taskForm)))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("title"));
    }

    @Test
    void createTask_ステータスコード400() throws Exception {
        //Bodyなしリクエスト
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void update_ステータスコード200() throws Exception {
        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("title");
        Mockito.when(service.update(anyLong(),anyString()))
            .thenReturn(new TaskEntity(1L, "title"));

        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(taskForm)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("title"));
    }

    @Test
    void updateTask_ステータスコード400() throws Exception {
        //Bodyなしリクエスト
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateTask_ステータスコード404() throws Exception {
        TaskForm taskForm = new TaskForm();
        taskForm.setTitle("title");

        Mockito.when(service.update(anyLong(), anyString()))
            .thenThrow(new TaskEntityNotFoundException(1));

        //存在しないID指定
        mockMvc.perform(MockMvcRequestBuilders.put("/tasks/999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(taskForm)))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteTask_ステータスコード204() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteTask_ステータスコード404() throws Exception {
        doThrow(new TaskEntityNotFoundException(1L))
            .when(service).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


}
