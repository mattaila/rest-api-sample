package com.example.restapi.domain.task.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.restapi.domain.task.repository.TaskRecord;
import com.example.restapi.domain.task.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskEntity find(Long taskId) {
        return taskRepository.select(taskId)
            .map(record -> new TaskEntity(record.getId(), record.getTitle()))
            .orElseThrow(() -> new TaskEntityNotFoundException(taskId));
    }

    public List<TaskEntity> find(int limit, long offset) {
        var recordList = taskRepository.selectList(limit, offset);
        return recordList.stream().map(record -> {
            return new TaskEntity(record.getId(), record.getTitle());
        }).collect(Collectors.toList());
    }

    public TaskEntity create(String title) {
        var record = new TaskRecord(null, title);
        taskRepository.insert(record);
        return new TaskEntity(record.getId(), record.getTitle());
    }

    public TaskEntity update(Long taskId, String title) {
        taskRepository.select(taskId).orElseThrow(() -> new TaskEntityNotFoundException(taskId));
        taskRepository.update(new TaskRecord(taskId, title));
        return find(taskId);
    }

    public void delete(Long taskId) {
        taskRepository.select(taskId).orElseThrow(() -> new TaskEntityNotFoundException(taskId));
        taskRepository.delete(taskId);
    }
    
}
