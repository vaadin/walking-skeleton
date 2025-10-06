import { PropsWithChildren } from 'react';
import { DrawerToggle, HorizontalLayout } from '@vaadin/react-components';

export function Group(props: PropsWithChildren) {
  return <HorizontalLayout theme="wrap spacing">{props.children}</HorizontalLayout>;
}

export type ViewToolbarProps = {
  title: string;
} & PropsWithChildren;

export function ViewToolbar(props: ViewToolbarProps) {
  return (
    <HorizontalLayout
      theme="padding spacing wrap"
      style={{ width: '100%', borderBottom: '1px solid var(--vaadin-border-color-secondary)' }}>
      <HorizontalLayout theme="spacing" style={{ alignItems: 'center', flexGrow: 1 }}>
        <DrawerToggle />
        <h1>{props.title}</h1>
      </HorizontalLayout>
      {props.children && (
        <HorizontalLayout theme="spacing" style={{ justifyContent: 'space-between' }}>
          {props.children}
        </HorizontalLayout>
      )}
    </HorizontalLayout>
  );
}
