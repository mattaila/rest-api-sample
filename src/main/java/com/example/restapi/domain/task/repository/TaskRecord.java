package com.example.restapi.domain.task.repository;

import java.time.LocalDate;

import lombok.Value;

@Value
public class TaskRecord {
    private Long id;
    private String title;
    private String comment;
    private int progress;
    private LocalDate deadline;

}
