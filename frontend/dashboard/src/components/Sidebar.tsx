import { NavLink } from 'react-router-dom'
import { LayoutDashboard, GitFork, Brain, Settings, Flame } from 'lucide-react'
import { clsx } from 'clsx'

const navItems = [
  { name: 'Overview', to: '/overview', icon: LayoutDashboard },
  { name: 'Flow Control', to: '/flow', icon: GitFork },
  { name: 'Knowledge Graph', to: '/knowledge', icon: Brain },
  { name: 'Escalations', to: '/escalations', icon: Flame },
  { name: 'Settings', to: '/settings', icon: Settings },
]

export function Sidebar() {
  return (
    <aside className="z-30 flex h-full w-64 select-none flex-col justify-between p-6 glass-sidebar rounded-[32px]">
      <div className="flex flex-col gap-8">
        {/* Modern Branding Header */}
        <div className="flex items-center gap-3 px-2">
          <div className="relative flex h-8 w-8 items-center justify-center rounded-lg bg-surface-elevated text-text-primary font-bold border border-border select-none mac-glow">
            <span className="text-caption font-semibold font-mono tracking-tighter">PF</span>
          </div>
          <div>
            <h1 className="text-caption font-semibold tracking-tight text-text-primary">PRFlow</h1>
            <p className="font-mono text-[9px] leading-none text-text-muted">v1.0.0-beta</p>
          </div>
        </div>

        {/* Organization Workspace Selector */}
        <div className="flex items-center justify-between px-3 py-2 text-caption rounded-xl bg-surface/10 border border-border mac-glow select-none">
          <div className="flex items-center gap-2">
            <div className="flex h-4 w-4 items-center justify-center rounded bg-text-muted/10 text-text-secondary text-[9px] font-mono">
              O
            </div>
            <span className="font-medium text-text-secondary text-[12px]">Naveensivam03</span>
          </div>
          <span className="rounded font-mono text-[8px] bg-text-muted/10 text-text-muted px-1.5 py-0.5">
            standby
          </span>
        </div>

        {/* Navigation List */}
        <nav className="flex flex-col gap-0.5">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                clsx(
                  'group relative flex items-center gap-3 px-4 py-2.5 text-caption font-medium transition-all duration-300 rounded-button border',
                  isActive
                    ? 'text-text-primary bg-surface border-border/80 shadow-premium-light font-semibold'
                    : 'text-text-secondary border-transparent hover:text-text-primary hover:bg-surface-elevated/20',
                )
              }
            >
              {({ isActive }) => (
                <>
                  <item.icon
                    className={clsx(
                      'h-4.5 w-4.5 transition-colors duration-300',
                      isActive ? 'text-accent' : 'text-text-muted group-hover:text-text-secondary',
                    )}
                  />
                  <span>{item.name}</span>
                  {isActive && (
                    <div className="absolute left-1.5 h-4 w-0.5 rounded-full bg-accent/80" />
                  )}
                </>
              )}
            </NavLink>
          ))}
        </nav>
      </div>

      {/* Footer / System Status */}
      <div className="flex flex-col gap-3 px-2">
        <div className="flex flex-col gap-1.5 select-none">
          <div className="flex items-center justify-between text-[10px] text-text-muted">
            <span>State Synchronizer</span>
            <span className="flex items-center gap-1.5 text-text-secondary">
              <span className="relative flex h-1 w-1">
                <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-accent opacity-50"></span>
                <span className="relative inline-flex h-1 w-1 rounded-full bg-accent"></span>
              </span>
              active
            </span>
          </div>
        </div>
      </div>
    </aside>
  )
}
export default Sidebar
