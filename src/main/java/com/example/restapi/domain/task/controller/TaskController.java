package com.example.restapi.domain.task.controller;

import java.net.URI;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.restapi.domain.task.service.TaskEntity;
import com.example.restapi.domain.task.service.TaskService;
import com.example.restapi.generated.api.TasksApi;
import com.example.restapi.generated.model.PageDTO;
import com.example.restapi.generated.model.TaskDTO;
import com.example.restapi.generated.model.TaskForm;
import com.example.restapi.generated.model.TaskListDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TaskController implements TasksApi {

    private final TaskService taskService;

    @Override
    public ResponseEntity<TaskDTO> showTask(Long taskId) {
        var entity = taskService.find(taskId);

        TaskDTO dto = toTaskDTO(entity);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<TaskListDTO> listTasks(Integer limit, Long offset) {
        var entityList = taskService.find(limit, offset);
        var dtoList = entityList.stream().map(this::toTaskDTO).collect(Collectors.toList());

        var pageDTO = new PageDTO();
        pageDTO.setLimit(limit);
        pageDTO.setOffset(offset);
        pageDTO.setSize(dtoList.size());

        TaskListDTO dto = new TaskListDTO();
        dto.setResults(dtoList);
        dto.setPage(pageDTO);
        return ResponseEntity.ok().body(dto);
    }

    private TaskDTO toTaskDTO(TaskEntity entity) {
        var taskDto = new TaskDTO();
        taskDto.setId(entity.getId());
        taskDto.setTitle(entity.getTitle());
        return taskDto;
    }

    @Override
    public ResponseEntity<TaskDTO> createTask(TaskForm form) {
        var entity = taskService.create(form.getTitle());
        var dto = toTaskDTO(entity);
        return ResponseEntity
                .created(URI.create("/tasks/" + dto.getId()))
                .body(dto);
    }

    @Override
    public ResponseEntity<TaskDTO> updateTask(Long taskId, TaskForm taskForm) {
        var entity = taskService.update(taskId, taskForm.getTitle());
        var dto = toTaskDTO(entity);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Void> deleteTask(Long taskId) {
        taskService.delete(taskId);
        return ResponseEntity.noContent().build();
    }
}
