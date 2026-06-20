import { createBrowserRouter, Navigate } from 'react-router-dom'
import AppLayout from '@/components/AppLayout'
import OverviewPage from '@/pages/OverviewPage'
import FlowPage from '@/pages/FlowPage'
import KnowledgePage from '@/pages/KnowledgePage'
import EscalationsPage from '@/pages/EscalationsPage'
import SettingsPage from '@/pages/SettingsPage'
import OnboardingPage from '@/pages/OnboardingPage'
import LandingPage from '@/pages/LandingPage'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <LandingPage />,
  },
  {
    path: '/onboarding',
    element: <OnboardingPage />,
  },
  {
    element: <AppLayout />,
    children: [
      {
        path: 'overview',
        element: <OverviewPage />,
      },
      {
        path: 'flow',
        element: <FlowPage />,
      },
      {
        path: 'knowledge',
        element: <KnowledgePage />,
      },
      {
        path: 'escalations',
        element: <EscalationsPage />,
      },
      {
        path: 'settings',
        element: <SettingsPage />,
      },
      {
        path: '*',
        element: <Navigate to="/overview" replace />,
      },
    ],
  },
])
