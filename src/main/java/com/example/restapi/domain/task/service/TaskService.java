package com.example.restapi.domain.task.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.restapi.domain.task.repository.TaskRecord;
import com.example.restapi.domain.task.repository.TaskRepository;
import com.example.restapi.generated.model.TaskForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskEntity find(Long taskId) {
        return taskRepository.select(taskId)
            .map(record -> new TaskEntity(record.getId(), record.getTitle(),
                record.getComment(), record.getProgress(), record.getDeadline()))
            .orElseThrow(() -> new TaskEntityNotFoundException(taskId));
    }

    public List<TaskEntity> find(int limit, long offset) {
        var recordList = taskRepository.selectList(limit, offset);
        return recordList.stream().map(record -> {
            return new TaskEntity(record.getId(), record.getTitle(),
                record.getComment(), record.getProgress(), record.getDeadline());
        }).collect(Collectors.toList());
    }

    public TaskEntity create(TaskForm form) {
        var record = new TaskRecord(null, form.getTitle(), form.getComment(), form.getProgress(), form.getDeadline());
        taskRepository.insert(record);
        return new TaskEntity(record.getId(), record.getTitle(),
                record.getComment(), record.getProgress(), record.getDeadline());
    }

    public TaskEntity update(Long taskId, TaskForm form) {
        taskRepository.select(taskId).orElseThrow(() -> new TaskEntityNotFoundException(taskId));
        taskRepository.update(new TaskRecord(taskId, form.getTitle(), form.getComment(), form.getProgress(), form.getDeadline()));
        return find(taskId);
    }

    public void delete(Long taskId) {
        taskRepository.select(taskId).orElseThrow(() -> new TaskEntityNotFoundException(taskId));
        taskRepository.delete(taskId);
    }
    
}
