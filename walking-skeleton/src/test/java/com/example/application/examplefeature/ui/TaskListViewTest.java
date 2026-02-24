package com.example.application.examplefeature.ui;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.notification.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
class TaskListViewTest extends SpringBrowserlessTest {

    @Test
    void empty_grid_shows_no_tasks() {
        var view = navigate(TaskListView.class);
        assertThat(test(view.taskGrid).size()).isZero();
        assertThat(view.taskGrid.getEmptyStateText())
                .isEqualTo("You have no tasks to complete");
    }

    @Test
    void create_task_with_empty_description_does_nothing() {
        var view = navigate(TaskListView.class);

        test(view.createBtn).click();

        assertThat(test(view.taskGrid).size()).isZero();
        assertThat(view.description.isInvalid()).isTrue();
        assertThat($(Notification.class).exists()).isFalse();
    }

    @Test
    void create_task_without_due_date() {
        var view = navigate(TaskListView.class);

        test(view.description).setValue("Buy groceries");
        test(view.createBtn).click();

        assertThat(test(view.taskGrid).size()).isEqualTo(1);
        assertThat(test(view.taskGrid).getCellText(0, 0)).isEqualTo("Buy groceries");
        assertThat(test(view.taskGrid).getCellText(0, 1)).isEqualTo("Never");

        assertThat(test(view.taskGrid).getCellText(0, 2)).isNotEmpty();

        assertThat(view.description.getValue()).isEmpty();
        assertThat(view.dueDate.getValue()).isNull();

        var notification = $(Notification.class).single();
        assertThat(test(notification).getText()).contains("Task added");
    }

    @Test
    void create_task_with_due_date() {
        var view = navigate(TaskListView.class);

        test(view.description).setValue("File taxes");
        test(view.dueDate).setValue(LocalDate.of(2026, 3, 15));
        test(view.createBtn).click();

        assertThat(test(view.taskGrid).size()).isEqualTo(1);
        assertThat(test(view.taskGrid).getCellText(0, 0)).isEqualTo("File taxes");
        assertThat(test(view.taskGrid).getCellText(0, 1)).isNotEqualTo("Never");
    }

    @Test
    void create_multiple_tasks() {
        var view = navigate(TaskListView.class);

        test(view.description).setValue("First task");
        test(view.createBtn).click();

        test(view.description).setValue("Second task");
        test(view.dueDate).setValue(LocalDate.of(2026, 6, 1));
        test(view.createBtn).click();

        assertThat(test(view.taskGrid).size()).isEqualTo(2);
    }
}
