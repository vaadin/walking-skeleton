package com.example.application.todo.service;

import com.example.application.todo.domain.Todo;
import com.example.application.todo.domain.TodoRepository;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.hilla.BrowserCallable;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@BrowserCallable
@AnonymousAllowed
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TodoService {

    private final TodoRepository todoRepository;

    private final Clock clock;

    TodoService(TodoRepository todoRepository, Clock clock) {
        this.todoRepository = todoRepository;
        this.clock = clock;
    }

    public void createTodo(String description, @Nullable LocalDate dueDate) {
        if ("fail".equals(description)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var todo = new Todo();
        todo.setDescription(description);
        todo.setCreationDate(clock.instant());
        todo.setDueDate(dueDate);
        todoRepository.saveAndFlush(todo);
    }

    public List<Todo> list(Pageable pageable) {
        return todoRepository.findAllBy(pageable).toList();
    }

}
