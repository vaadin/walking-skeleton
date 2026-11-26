package com.example.application.examplefeature.ui;

import com.example.application.examplefeature.Task;
import com.example.application.examplefeature.TaskService;
import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
class TaskListViewTest {

    @Autowired
    BrowserlessApplicationContext app;

    @Autowired
    TaskService taskService;

    BrowserlessUIContext window;

    @BeforeEach
    void openWindow() {
        window = app.newUser().newWindow();
        window.navigate(TaskListView.class);
    }

    @AfterEach
    void closeUser() {
        window.getUser().close();
    }

    @Test
    void empty_grid_shows_no_tasks() {
        var grid = window.findGrid(Task.class);

        assertThat(grid.size()).isZero();
        assertThat(grid.component().getEmptyStateText()).isEqualTo("You have no tasks to complete");
    }

    @Test
    void create_task_with_empty_description_shows_error() {
        window.findButton().withText("Create").click();

        var description = window.findTextField().withAriaLabel("Task description").component();
        assertThat(description.isInvalid()).isTrue();
        assertThat(description.getErrorMessage()).isEqualTo("Description is required");
        assertThat(window.findNotification().exists()).isFalse();
        assertThat(window.findGrid(Task.class).size()).isZero();
        assertThat(taskService.list(Pageable.unpaged())).isEmpty();
    }

    @Test
    void create_task_without_due_date() {
        window.findTextField().withAriaLabel("Task description").setValue("Buy groceries");
        window.findButton().withText("Create").click();

        assertThat(window.findNotification().getText()).isEqualTo("Task added");

        var grid = window.findGrid(Task.class);
        assertThat(grid.size()).isEqualTo(1);
        assertThat(grid.getCellText(0, 0)).isEqualTo("Buy groceries");
        assertThat(grid.getCellText(0, 1)).isEqualTo("Never");
        assertThat(grid.getCellText(0, 2)).isNotEmpty();

        assertThat(taskService.list(Pageable.unpaged())).singleElement().satisfies(task -> {
            assertThat(task.getDescription()).isEqualTo("Buy groceries");
            assertThat(task.getDueDate()).isNull();
        });
    }

    @Test
    void create_task_with_due_date() {
        var dueDate = LocalDate.of(2026, 3, 15);

        window.findTextField().withAriaLabel("Task description").setValue("File taxes");
        window.findDatePicker().withAriaLabel("Due date").setValue(dueDate);
        window.findButton().withText("Create").click();

        var grid = window.findGrid(Task.class);
        assertThat(grid.size()).isEqualTo(1);
        assertThat(grid.getCellText(0, 0)).isEqualTo("File taxes");
        assertThat(grid.getCellText(0, 1)).isNotEqualTo("Never");

        // The form is cleared for the next task
        assertThat(window.findTextField().withAriaLabel("Task description").component().getValue()).isEmpty();
        assertThat(window.findDatePicker().withAriaLabel("Due date").component().getValue()).isNull();

        assertThat(taskService.list(Pageable.unpaged())).singleElement()
                .satisfies(task -> assertThat(task.getDueDate()).isEqualTo(dueDate));
    }

    @Test
    void create_multiple_tasks() {
        var description = window.findTextField().withAriaLabel("Task description");
        var createButton = window.findButton().withText("Create");

        description.setValue("First task");
        createButton.click();
        description.setValue("Second task");
        createButton.click();

        var grid = window.findGrid(Task.class);
        assertThat(grid.size()).isEqualTo(2);
        assertThat(grid.getCellText(0, 0)).isEqualTo("First task");
        assertThat(grid.getCellText(1, 0)).isEqualTo("Second task");
    }
}
