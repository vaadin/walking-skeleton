package com.example.application.base.ui;

import com.vaadin.flow.component.Component;

import java.io.Serializable;

public interface HasNavbarContent extends Serializable {

    Component createNavbarContent();
}
