package com.example.restapi.domain.task.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
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
import com.example.restapi.generated.model.TaskForm;

public class TaskServiceTest {

    @InjectMocks
    private TaskService service;

    @Mock
    private TaskRepository repository;

    private AutoCloseable closable;
    private TaskRecord record = new TaskRecord(1L, "test record", "comment", 0, LocalDate.of(2025, 1, 1));
    private long taskId = 1L;
    private TaskForm form;

    @BeforeEach
    void setUp() {
        closable = MockitoAnnotations.openMocks(this);

        form = new TaskForm();
        form.setTitle("title");
        form.setComment("comment");
        form.setProgress(10);
        form.setDeadline(LocalDate.of(2025, 1, 1));
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
                    new TaskRecord(1L, "record1","comment", 0, LocalDate.of(2025, 1, 1)),
                    new TaskRecord(2L, "record2", "comment", 0, LocalDate.of(2025, 1, 1))));

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
        TaskEntity entity = service.create(form);
        verify(repository, times(1)).insert(any());

        assertEquals(form.getTitle(), entity.getTitle());
        assertEquals(form.getComment(), entity.getComment());
        assertEquals(form.getProgress(), entity.getProgress());
        assertEquals(form.getDeadline(), entity.getDeadline());
    }

    @Test
    void update_レコードあり() {
        TaskRecord updatedRecord = new TaskRecord(1L, "updated","comment", 0, LocalDate.of(2025, 1, 1));

        Mockito.when(repository.select(anyLong()))
            .thenReturn(Optional.of(record))
            .thenReturn(Optional.of(updatedRecord));
        
        TaskEntity actual = service.update(taskId, form);
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
            () -> service.update(taskId, form));
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
