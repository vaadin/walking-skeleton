package com.example.application.taskmanagement.service;

import com.example.application.taskmanagement.domain.Task;
import com.example.application.taskmanagement.domain.TaskRepository;
//#if ui.framework == "hilla"
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
//#endif
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
//#if ui.framework == "flow"
import org.springframework.stereotype.Service;
//#endif
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

//#if ui.framework == "flow"
@Service
//#endif
//#if ui.framework == "hilla"
@BrowserCallable
@AnonymousAllowed
//#endif
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TaskService {

    private final TaskRepository taskRepository;

    private final Clock clock;

    TaskService(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    public void createTask(String description, @Nullable LocalDate dueDate) {
        if ("fail".equals(description)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var task = new Task();
        task.setDescription(description);
        task.setCreationDate(clock.instant());
        task.setDueDate(dueDate);
        taskRepository.saveAndFlush(task);
    }

    public List<Task> list(Pageable pageable) {
        return taskRepository.findAllBy(pageable).toList();
    }

}
