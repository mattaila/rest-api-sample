package com.example.restapi.domain.task.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskRepository {

    Optional<TaskRecord> select(Long taskId);

    void insert(TaskRecord record);

    List<TaskRecord> selectList(int limit, long offset);

    void update(TaskRecord taskRecord);

    void delete(Long taskId);
}
