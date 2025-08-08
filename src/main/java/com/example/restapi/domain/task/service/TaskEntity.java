package com.example.restapi.domain.task.service;

import java.time.LocalDate;

import com.example.restapi.generated.model.TaskDTO;

import lombok.Value;

@Value
public class TaskEntity {
    private Long id;
    private String title;
    private String comment;
    private int progress;
    private LocalDate deadline;

    public TaskDTO toTaskDTO() {
        var taskDto = new TaskDTO();
        taskDto.setId(this.getId());
        taskDto.setTitle(this.getTitle());
        taskDto.setComment(this.getComment());
        taskDto.setProgress(this.getProgress());
        taskDto.setDeadline(this.getDeadline());
        return taskDto;
    }
}
