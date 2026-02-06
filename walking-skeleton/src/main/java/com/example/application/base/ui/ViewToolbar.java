package com.example.application.base.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.jspecify.annotations.Nullable;

public final class ViewToolbar extends Composite<HorizontalLayout> {

    public ViewToolbar(@Nullable String viewTitle, Component... components) {
        var layout = getContent();
        layout.setPadding(true);
        layout.setWrap(true);
        layout.setWidthFull();

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
