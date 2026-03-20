package com.example.application.examplefeature.ui;

import com.example.application.base.ui.HasNavbarContent;
import com.example.application.base.ui.MainLayout;
import com.example.application.examplefeature.Task;
import com.example.application.examplefeature.TaskService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.jspecify.annotations.NonNull;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
class TaskListView extends VerticalLayout implements HasNavbarContent {

    private final TaskService taskService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;

    TaskListView(TaskService taskService) {
        this.taskService = taskService;

        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");
        description.getStyle().setFlexGrow("1");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");
        dueDate.getStyle().setFlexGrow("1");

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creation Date");
        taskGrid.setEmptyStateText("You have no tasks to complete");
        taskGrid.setSizeFull();
        taskGrid.addThemeVariants(GridVariant.NO_BORDER);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().setOverflow(Style.Overflow.HIDDEN);

        add(taskGrid);
    }

    private void createTask() {
        if (description.getValue().isBlank()) {
            description.setInvalid(true);
            description.setErrorMessage("Description is required");
            return;
        }
        taskService.createTask(description.getValue(), dueDate.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.SUCCESS);
    }

    @Override
    public @NonNull Component createNavbarContent() {
        var navBar = new HorizontalLayout(JustifyContentMode.BETWEEN, new H2("Task list"),
                new HorizontalLayout(description, dueDate, createBtn));
        navBar.setWidthFull();
        return navBar;
    }
}
