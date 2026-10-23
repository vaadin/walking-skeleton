# My Application README

- [ ] TODO Replace or update this README with instructions relevant to your application

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

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
docker build --secret id=proKey,src=$HOME/.vaadin/proKey -t my-application:latest .
```

To run the image, run:

```bash
docker run -p 8080:8080 -m 2g my-application:latest
```

The application is then available at http://localhost:8080. The `-m` option sets the memory limit of the container. The
application uses up to 75% of it for the Java heap, which holds the UI state of all users, so adjust the limit to fit
your application.

## Agentic Development

If you develop with an AI coding agent, such as Claude Code or Codex, enable the Vaadin Dev Loop. It lets the agent make
its changes live in the running application with a single command, and tells it whether they took effect, without
restarting the application or checking screenshots. To enable it, run:

```bash
./mvnw vaadin:install-dev-cli
```

This installs the Dev Loop CLI into `.vaadin/`, and skills that teach your agent how to use it into `.agents/skills/` and
`.claude/skills/`. Commit these files with the rest of the project, and run the command again after upgrading Vaadin.

The Dev Loop needs to own the application process, so start the application with `.vaadin/vaadin-dev start` instead of
`./mvnw` or your IDE.

The Dev Loop is a preview feature. To learn more, including how to give your agent access to the Vaadin documentation,
see the [Dev Loop tutorial](https://vaadin.com/docs/v25/building-apps/agentic-development/dev-loop-tutorial).

## Getting Started

The [Quick Start](https://vaadin.com/docs/v25/getting-started/quick-start) tutorial helps you get started with Vaadin in 
around 10 minutes. This tutorial walks you through building a simple application, introducing the core concepts along 
the way.
