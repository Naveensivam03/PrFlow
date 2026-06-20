import { Outlet } from 'react-router-dom'
import { Sidebar } from './Sidebar'
import { Header } from './Header'

export function AppLayout() {
  return (
    <div className="relative flex h-screen w-screen bg-background p-6 gap-6 overflow-hidden">
      {/* Background Soft Depth Lights */}
      <div className="absolute right-[5%] top-[5%] -z-10 h-[40%] w-[40%] rounded-full bg-accent/[0.012] filter blur-[140px] pointer-events-none"></div>
      <div className="absolute bottom-[5%] left-[10%] -z-10 h-[45%] w-[45%] rounded-full bg-success/[0.012] filter blur-[160px] pointer-events-none"></div>

      {/* Floating Standalone Glass Sidebar */}
      <Sidebar />

      {/* Main Content Area (sits directly on background) */}
      <div className="relative z-10 flex h-full flex-1 flex-col min-w-0 bg-transparent">
        {/* Transparent Header */}
        <Header />

        {/* Page Content Viewport */}
        <main className="flex-1 overflow-y-auto py-8 px-4">
          <div className="mx-auto w-full max-w-5xl">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  )
}
export default AppLayout
