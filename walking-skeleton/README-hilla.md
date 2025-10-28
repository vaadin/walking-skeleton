# Walking Skeleton README

- [ ] TODO Replace or update this README with instructions relevant to your application

## Project Structure

The sources of your Walking Skeleton have the following structure:

```
src
├── main/frontend
│   ├── components
│   │    └── ViewToolbar.tsx
│   └── views
│       ├── @index.tsx
│       └── @layout.tsx
├── main/java
│   └── [application package]
│       ├── examplefeature
│       │   ├── Task.java
│       │   ├── TaskRepository.java
│       │   └── TaskService.java                
│       └── Application.java       
├── main/resources
│   ├── META-INF
│   │   └── resources
│   │       └── styles.css
│   └── application.properties 
└── test/java
    └── [application package]
        └── examplefeature
           └── TaskServiceTest.java                 
```

The main entry point into the application is `Application.java`. This class contains the `main()` method that start up 
the Spring Boot application.

The skeleton follows a *feature-based package structure*, organizing code by *functional units* rather than traditional
architectural layers. It includes one example feature package - `examplefeature` - that demonstrates the structure. It
represents a *self-contained unit of functionality*, including business logic, data access, and an integration test. 
Once you create your own features, *you'll remove this package*.

The `src/main/frontend` directory contains the following:

* a `ViewToolbar` component that you can reuse in your application.
* an example view `@index.tsx` that communicates with the `TaskService`. Once you create your own features,
  *you'll replace this*.
* a main layout `@layout.tsx` that you can reuse and expand in your application.


## Starting in Development Mode

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

## Building for Production

To build the application in production mode, run:

```bash
./mvnw package
```

To build a Docker image, run:

```bash
docker build -t my-application:latest .
```

If you use commercial components, pass the license key as a build secret:

```bash
docker build --secret id=proKey,src=$HOME/.vaadin/proKey .
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
Walking Skeleton implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.
