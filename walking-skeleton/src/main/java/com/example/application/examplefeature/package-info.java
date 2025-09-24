/**
 * This is a feature package for the Task Management sample feature. Its purpose is to demonstrate how you typically
 * structure Vaadin business applications, and how the different building blocks interact.
 * <p>
 * A feature package represents a self-contained unit of functionality, including UI components, business logic, and
 * data access. It could be a subdomain or bounded context (e.g., "Billing"), a specific use case (e.g., "User
 * Registration"), or even a complex UI view (e.g., "Dashboard").
 * </p>
 * <p>
 * If your application is very small, you may not need dedicated feature packages. In that case, move the subpackages
 * directly to the application package.
 * </p>
 */
@NullMarked
//#if ui.framework == "hilla"
@NonNullApi // Until https://github.com/vaadin/hilla/issues/2612 has been fixed
//#endif
package com.example.application.examplefeature;
// TODO Remove this package once you have added real features

import org.jspecify.annotations.NullMarked;
//#if ui.framework == "hilla"
import org.springframework.lang.NonNullApi;
//#endif