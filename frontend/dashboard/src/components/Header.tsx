import { useTheme } from '@/hooks/use-theme'
import { Sun, Moon, Cpu, Terminal } from 'lucide-react'
import { useLocation } from 'react-router-dom'

export function Header() {
  const { theme, setTheme } = useTheme()
  const location = useLocation()

  const getPageTitle = (pathname: string) => {
    switch (pathname) {
      case '/overview':
        return 'System Overview'
      case '/flow':
        return 'Flow Control'
      case '/knowledge':
        return 'Knowledge Graph'
      case '/escalations':
        return 'SLA Escalations'
      case '/settings':
        return 'Platform Settings'
      default:
        return 'PRFlow Console'
    }
  }

  return (
    <header className="z-20 flex h-16 flex-shrink-0 items-center justify-between border-b border-border bg-transparent px-8">
      {/* Title / Breadcrumb */}
      <div className="flex items-center gap-2 select-none">
        <span className="font-mono text-caption text-text-muted">core /</span>
        <h2 className="text-body font-semibold leading-none text-text-primary">
          {getPageTitle(location.pathname)}
        </h2>
      </div>

      {/* Action Toolbar */}
      <div className="flex items-center gap-4">
        {/* System Diagnostics Metrics */}
        <div className="flex items-center gap-2 border border-border bg-surface-elevated/20 px-3 py-1 font-mono text-[11px] text-text-muted select-none rounded-full">
          <Terminal className="h-3 w-3 text-accent/80" />
          <span>latency: 12ms</span>
        </div>

        {/* Theme Toggle Button */}
        <button
          onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
          className="flex h-9 w-9 cursor-pointer items-center justify-center border border-border bg-surface text-text-secondary transition-all duration-200 active:scale-95 rounded-button hover:bg-surface-elevated hover:text-text-primary mac-glow"
          aria-label="Toggle theme"
        >
          {theme === 'dark' ? (
            <Sun className="h-4 w-4 text-warning/80" />
          ) : (
            <Moon className="h-4 w-4 text-accent/80" />
          )}
        </button>

        {/* Diagnostic Activity Node */}
        <div className="flex h-9 w-9 select-none items-center justify-center border border-border bg-surface text-text-muted rounded-button mac-glow">
          <Cpu className="h-4 w-4 text-success/60" />
        </div>
      </div>
    </header>
  )
}
export default Header
