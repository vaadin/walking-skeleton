package com.example.application.base.ui;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@StyleSheet("view-title.css")
public class ViewTitle extends Composite<HorizontalLayout> {

    public ViewTitle(String title) {
        addClassName("view-title");
        var h = new H1(title);
        var toggle = new DrawerToggle();
        toggle.addThemeVariants(ButtonVariant.TERTIARY);
        toggle.addClassName("aura-accent-neutral");
        getContent().add(toggle, h);
        getContent().setAlignItems(Alignment.CENTER);
    }
}
