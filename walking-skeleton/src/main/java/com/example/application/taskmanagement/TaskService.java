package com.example.application.taskmanagement;

//#if ui.framework == "hilla"
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
//#endif
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
//#if ui.framework == "flow"
import org.springframework.stereotype.Service;
//#endif
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

//#if ui.framework == "flow"
@Service
//#endif
//#if ui.framework == "hilla"
@BrowserCallable
@AnonymousAllowed
//#endif
public class TaskService {

    private final TaskRepository taskRepository;

    TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void createTask(String description, @Nullable LocalDate dueDate) {
        if ("fail".equals(description)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var task = new Task(description, Instant.now());
        task.setDueDate(dueDate);
        taskRepository.saveAndFlush(task);
    }

    @Transactional(readOnly = true)
    public List<Task> list(Pageable pageable) {
        return taskRepository.findAllBy(pageable).toList();
    }

}
