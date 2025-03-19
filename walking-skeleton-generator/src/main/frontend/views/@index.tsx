import { Button, Notification, RadioButton, RadioGroup, TextField } from '@vaadin/react-components';
import { useSignal } from '@vaadin/hilla-react-signals';

type UiFramework = 'FLOW' | 'REACT'

const getContextPath = (): string => {
  return document.querySelector('meta[name="context-path"]')?.getAttribute('content') || '';
};

const buildUrl = (path: string): string => {
  const contextPath = getContextPath();
  const cleanPath = path.startsWith('/') ? path.substring(1) : path;
  return `${contextPath}/${cleanPath}`;
};

const DEFAULT_ARTIFACT_ID = 'my-application';
const DEFAULT_GROUP_ID = 'com.example.application';
const DEFAULT_BASE_PACKAGE = 'com.example.application';

export default function ProjectGeneratorView() {
  const groupId = useSignal('');
  const artifactId = useSignal('');
  const basePackage = useSignal('');
  const uiFramework = useSignal<UiFramework>('FLOW');
  const error = useSignal(false);

  // TODO Should also validate the input of the fields

  const downloadProject = async () => {
    error.value = false;

    const projectConfiguration = {
      artifactId: artifactId.value || DEFAULT_ARTIFACT_ID,
      groupId: groupId.value || DEFAULT_GROUP_ID,
      basePackage: basePackage.value || DEFAULT_BASE_PACKAGE,
      uiFramework: uiFramework.value
    };

    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(projectConfiguration)
    };

    const response = await fetch(buildUrl('/generate'), requestOptions);

    if (!response.ok) {
      error.value = true;
      return;
    }

    const blob = await response.blob();
    await downloadFile(blob, projectConfiguration.artifactId);
  };

  const downloadFile = async (blob: Blob, artifactId: string) => {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `${artifactId}.zip`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  };

  return (
    <div className="w-full h-full flex justify-center items-center bg-contrast-10">
      <div className="bg-base shadow-m p-l rounded-l" style={{ 'max-width': '550px' }}>
        <h1>Walking Skeleton Generator</h1>
        <p>
          Please fill in the following fields to create your new Vaadin project:
        </p>
        <div className="flex flex-col">
          <TextField label="Maven Project Group ID" placeholder={DEFAULT_GROUP_ID}
                     value={groupId.value}
                     onValueChanged={evt => groupId.value = evt.detail.value}></TextField>
          <TextField label="Maven Project Artifact ID" placeholder={DEFAULT_ARTIFACT_ID}
                     value={artifactId.value}
                     onValueChanged={evt => artifactId.value = evt.detail.value}></TextField>
          <TextField label="Java Base Package" placeholder={DEFAULT_BASE_PACKAGE}
                     value={basePackage.value}
                     onValueChanged={evt => basePackage.value = evt.detail.value}></TextField>
          <RadioGroup label="User Interface Framework" value={uiFramework.value}
                      onValueChanged={evt => uiFramework.value = evt.detail.value as UiFramework}>
            <RadioButton value="FLOW" label="Vaadin Flow" />
            <RadioButton value="REACT" label="React" />
          </RadioGroup>
          <div className="text-secondary text-s my-l">
            The generated project is configured to use <b>Java 21</b>. Unzip it, and start the application by running
            the command: <code>./mvnw
            spring-boot:run</code>
          </div>
          <Button theme="primary" onClick={downloadProject}>Download Project</Button>
          <Notification opened={error.value} theme="error" position="top-center" onClosed={evt => error.value = false}>
            Whops, this did not go as planned. Please try again later.
          </Notification>
        </div>
      </div>
    </div>
  );
}