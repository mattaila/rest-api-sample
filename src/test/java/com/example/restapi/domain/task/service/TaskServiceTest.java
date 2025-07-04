package com.example.restapi.domain.task.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.example.restapi.domain.task.repository.TaskRecord;
import com.example.restapi.domain.task.repository.TaskRepository;

public class TaskServiceTest {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository repository;

    private AutoCloseable closable;
    private TaskRecord record = new TaskRecord(1L, "test record");
    private long taskId = 1L;

    @BeforeEach
    void setUp() {
        closable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closable.close();
    }

    @Test
    void find_レコードあり() {
        Mockito.when(repository.select(anyLong()))
            .thenReturn(Optional.of(record));

        TaskEntity actual = service.find(taskId);
        assertAll(
            () -> assertEquals(record.getId(), actual.getId()),
            () -> assertEquals(record.getTitle(), actual.getTitle())
        );
    }

    @Test
    void find_レコードなし() {
        Mockito.when(repository.select(anyLong()))
            .thenReturn(Optional.ofNullable(null));

        assertThrows(TaskEntityNotFoundException.class, () -> service.find(taskId));
    }

    @Test
    void find_list() {
        Mockito.when(repository.selectList(anyInt(), anyLong()))
            .thenReturn(List.of(
                    new TaskRecord(1L, "record1"),
                    new TaskRecord(2L, "record2")));

        List<TaskEntity> recordList = service.find(anyInt(), anyLong());
        assertAll(
            () -> assertEquals(2, recordList.size()),
            () -> assertEquals(1L, recordList.get(0).getId()),
            () -> assertEquals("record1", recordList.get(0).getTitle()),
            () -> assertEquals(2L, recordList.get(1).getId()),
            () -> assertEquals("record2", recordList.get(1).getTitle())
        );

    }

    @Test
    void create() {
        TaskEntity entity = service.create("title");
        verify(repository, times(1)).insert(any());

        assertEquals("title", entity.getTitle());
    }

    @Test
    void update_レコードあり() {
        TaskRecord updatedRecord = new TaskRecord(1L, "updated");

        Mockito.when(repository.select(anyLong()))
            .thenReturn(Optional.of(record))
            .thenReturn(Optional.of(updatedRecord));
        
        TaskEntity actual = service.update(taskId, "updated");
        assertAll(
            () -> assertEquals(record.getId(), actual.getId()),
            () -> assertEquals("updated", actual.getTitle())
        );
    }

    @Test
    void update_レコードなし() {
        Mockito.when(repository.select(anyLong()))
            .thenReturn(Optional.ofNullable(null));
        
        assertThrows(TaskEntityNotFoundException.class,
            () -> service.update(taskId, "updated"));
    }

    @Test
    void delete_レコードあり() {
        Mockito.when(repository.select(anyLong()))
            .thenReturn(Optional.of(record));
        
        service.delete(taskId);
        verify(repository, times(1)).delete(anyLong());
    }

    @Test
    void delete_レコードなし() {
        Mockito.when(repository.select(anyLong()))
            .thenReturn(Optional.ofNullable(null));
        
        assertThrows(TaskEntityNotFoundException.class,
            () -> service.delete(taskId));
    }

}
