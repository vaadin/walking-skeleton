package com.example.application.base.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * This view shows up when a user navigates to the root ('/') of the application.
 */
@Route
public class MainView extends Main {

    // TODO Replace with your own main view.

    public MainView() {
        addClassName(LumoUtility.Padding.MEDIUM);
        add(new Div("Please select a view from the menu on the left."));
    }
}
