# My Application

A Spring Boot + Vaadin project. Build your UI in pure Java — no HTML, no JavaScript.

> **New to Vaadin?** The 5-minute [Quickstart](https://vaadin.com/quickstart) walks you from here to your first running app, a live code change, and an AI-assisted edit with Copilot.

---

## Fastest start — no plugin needed

From the project folder:

```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

No system Maven required — the wrapper is included. Then open **http://localhost:8080**.

The first start takes ~30 seconds while Maven downloads dependencies. You'll get a runnable **Task List** app: a data grid (Description / Due Date / Creation Date), a Create button, and an empty-state message. When you see that, you're running.

> **Port 8080 already in use?** Stop the other process, or set `server.port=8081` in `src/main/resources/application.properties` and open that port instead.
>
> **To stop the app:** press `Ctrl+C` in the terminal (or the red Stop button if you launched from your IDE).

## Optional upgrade — instant hotswap

Running with `spring-boot:run` works, but Java code changes need a restart. For **live reload** — edit Java, see it in the browser without restarting — install the **Vaadin plugin** and start the app through it:

- **IntelliJ IDEA:** install *Vaadin* from the JetBrains Marketplace → **Debug using Hotswap Agent** (dropdown next to Run). *Just installed it? Let IntelliJ finish indexing, or restart it, if the menu item isn't there yet.*
- **VS Code:** install the *Vaadin* extension → **Vaadin: Debug using Hotswap Agent** from the command palette.
- **Eclipse:** install the *Vaadin* plugin → right-click the project → **Run As → Vaadin Application**.

This is what makes the edit-and-see-it loop feel instant — and it's required for the AI edits in [Vaadin Copilot](https://vaadin.com/docs/latest/tools/copilot).

---

## Day 2: make your task list interactive (~10 min)

Your app lists tasks. Let's make a row do something when you click it.

**1. Add a click listener (by hand).** In `src/main/java/com/example/application/examplefeature/ui/TaskListView.java`, add this after the `taskGrid.addColumn(...)` block:

```java
taskGrid.addItemClickListener(event ->
    Notification.show("Due: " + Optional.ofNullable(event.getItem().getDueDate())
        .map(LocalDate::toString)
        .orElse("no due date")));
```

Add the import: `import java.time.LocalDate;`

Save and click a task — a notification shows its due date. That's a server-side event handler, in pure Java.

**2. Let Copilot finish it.** Open Copilot (bottom-right toolbar → **Edit mode**), click the AI assistant, and try:

> When a task row is clicked, open a dialog showing its description, due date, and creation date, with a Close button.

Copilot writes the dialog into `TaskListView.java` for you. Open the file — your new code is right there.

---

## Ask your AI assistant about Vaadin (optional)

If you use Claude Code, Cursor, or another AI coding assistant, connect it to the **Vaadin MCP server** so it answers against real Vaadin docs and the exact API of your installed version — instead of guessing from outdated training data.

The one-time setup is described in [Vaadin MCP server](https://vaadin.com/docs/latest/building-apps/mcp).

---

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

---

## Build for production

```bash
./mvnw package
java -jar target/*.jar
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

## Learn more

- [Vaadin Quickstart](https://vaadin.com/quickstart) — the 5-minute getting-started path
- [Components](https://vaadin.com/docs/latest/components) — 50+ UI components, all callable from Java
- [Vaadin Copilot](https://vaadin.com/docs/latest/tools/copilot) — visual + AI editing in the browser
- [Full documentation](https://vaadin.com/docs)
