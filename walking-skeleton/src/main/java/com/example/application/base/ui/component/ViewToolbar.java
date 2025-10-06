package com.example.application.base.ui.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public final class ViewToolbar extends Composite<HorizontalLayout> {

    public ViewToolbar(@Nullable String viewTitle, Component... components) {
        var layout = getContent();
        layout.setPadding(true);
        layout.setWrap(true);
        layout.setWidthFull();
        layout.getStyle().setBorderBottom("1px solid var(--vaadin-border-color-secondary)");

        var drawerToggle = new DrawerToggle();
        var title = new H1(viewTitle);

        var toggleAndTitle = new HorizontalLayout(drawerToggle, title);
        toggleAndTitle.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.add(toggleAndTitle);
        layout.setFlexGrow(1, toggleAndTitle);

        if (components.length > 0) {
            var actions = new HorizontalLayout(components);
            actions.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            layout.add(actions);
        }
    }

    public static Component group(Component... components) {
        var group = new HorizontalLayout(components);
        group.setWrap(true);
        return group;
    }
}
