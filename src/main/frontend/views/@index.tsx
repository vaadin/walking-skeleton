import { ViewConfig } from '@vaadin/hilla-file-router/types.js';
import { Button, DatePicker, Grid, GridColumn, TextField } from '@vaadin/react-components';
import { Notification } from '@vaadin/react-components/Notification';
import { TodoService } from 'Frontend/generated/endpoints';
import { useSignal } from '@vaadin/hilla-react-signals';
import handleError from 'Frontend/views/_ErrorHandler';
import { Group, ViewToolbar } from 'Frontend/components/ViewToolbar';
import Todo from 'Frontend/generated/com/example/application/todo/domain/Todo';
import { useDataProvider } from '@vaadin/hilla-react-crud';

export const config: ViewConfig = {
  title: 'Task List',
  menu: {
    icon: 'vaadin:clipboard-check',
    order: 1,
    title: 'Task List',
  },
};

const dateTimeFormatter = new Intl.DateTimeFormat(undefined, {
  dateStyle: 'medium',
  timeStyle: 'medium',
});

const dateFormatter = new Intl.DateTimeFormat(undefined, {
  dateStyle: 'medium',
});

type TodoEntryFormProps = {
  onTodoCreated?: () => void;
};

function TodoEntryForm(props: TodoEntryFormProps) {
  const description = useSignal('');
  const dueDate = useSignal<string | undefined>('');
  const createTodo = async () => {
    try {
      await TodoService.createTodo(description.value, dueDate.value);
      if (props.onTodoCreated) {
        props.onTodoCreated();
      }
      description.value = '';
      dueDate.value = undefined;
      Notification.show('Task added', { duration: 3000, position: 'bottom-end', theme: 'success' });
    } catch (error) {
      handleError(error);
    }
  };
  return (
    <>
      <TextField
        placeholder="What do you want to do?"
        aria-label="Task description"
        maxlength={255}
        style={{ minWidth: '20em' }}
        value={description.value}
        onValueChanged={(evt) => (description.value = evt.detail.value)}
      />
      <DatePicker
        placeholder="Due date"
        aria-label="Due date"
        value={dueDate.value}
        onValueChanged={(evt) => (dueDate.value = evt.detail.value)}
      />
      <Button onClick={createTodo} theme="primary">
        Create
      </Button>
    </>
  );
}

export default function TodoView() {
  const dataProvider = useDataProvider<Todo>({
    list: (pageable) => TodoService.list(pageable),
  });

  return (
    <main className="w-full h-full flex flex-col box-border gap-s p-m">
      <ViewToolbar title="Task List">
        <Group>
          <TodoEntryForm onTodoCreated={dataProvider.refresh} />
        </Group>
      </ViewToolbar>
      <Grid dataProvider={dataProvider.dataProvider}>
        <GridColumn path="description" />
        <GridColumn path="dueDate" header="Due Date">
          {({ item }) => (item.dueDate ? dateFormatter.format(new Date(item.dueDate)) : 'Never')}
        </GridColumn>
        <GridColumn path="creationDate" header="Creation Date">
          {({ item }) => dateTimeFormatter.format(new Date(item.creationDate))}
        </GridColumn>
      </Grid>
    </main>
  );
}
