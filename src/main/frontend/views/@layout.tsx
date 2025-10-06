import { Outlet, useLocation, useNavigate } from 'react-router';
import '@vaadin/icons';
import { AppLayout, Icon, ProgressBar, Scroller, SideNav, SideNavItem, VerticalLayout } from '@vaadin/react-components';
import { Suspense, useMemo } from 'react';
import { createMenuItems } from '@vaadin/hilla-file-router/runtime.js';

function Header() {
  // TODO Replace with real application logo and name
  return (
    <VerticalLayout slot="drawer" theme="spacing padding" style={{ alignItems: 'center' }}>
      <Icon icon="vaadin:cubes" style={{ width: '48px', height: '48px' }} />
      <span style={{ fontWeight: 'bold' }}>Walking Skeleton</span>
    </VerticalLayout>
  );
}

function MainMenu() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <SideNav onNavigate={({ path }) => path != null && navigate(path)} location={location}>
      {createMenuItems().map(({ to, icon, title }) => (
        <SideNavItem path={to} key={to}>
          {icon && <Icon icon={icon} slot="prefix" />}
          {title}
        </SideNavItem>
      ))}
    </SideNav>
  );
}

export default function MainLayout() {
  return (
    <AppLayout primarySection="drawer">
      <Header />
      <Scroller slot="drawer">
        <MainMenu />
      </Scroller>
      <Suspense fallback={<ProgressBar indeterminate={true} />}>
        <Outlet />
      </Suspense>
    </AppLayout>
  );
}
