import { useNavigate } from 'react-router-dom'
import { Terminal, GitPullRequest, ArrowRight } from 'lucide-react'
import { Button } from '@/components/Button'

export function LandingPage() {
  const navigate = useNavigate()

  return (
    <div className="relative flex h-screen w-screen flex-col items-center justify-center bg-background p-6 overflow-hidden">
      {/* Background Soft Depth Ambient Aura */}
      <div className="absolute right-[10%] top-[10%] -z-10 h-[50%] w-[50%] rounded-full bg-accent/[0.015] filter blur-[180px] pointer-events-none"></div>
      <div className="absolute bottom-[10%] left-[10%] -z-10 h-[55%] w-[55%] rounded-full bg-success/[0.012] filter blur-[200px] pointer-events-none"></div>

      {/* Main Centered Content Container */}
      <div className="z-10 flex w-full max-w-[640px] flex-col items-center text-center gap-8 select-none animate-fade-in">
        
        {/* Glowing Brand Badge */}
        <div className="relative flex h-14 w-14 items-center justify-center rounded-2xl bg-surface border border-border select-none mac-glow">
          <GitPullRequest className="h-6 w-6 text-accent/80" />
        </div>

        {/* Dynamic Display Typography */}
        <div className="flex flex-col gap-4">
          <h1 className="font-display font-semibold tracking-tight text-text-primary">
            PRFlow
          </h1>
          <p className="text-text-secondary font-subheading leading-relaxed max-w-[480px] mx-auto font-medium">
            Active pull request orchestration and workflow intelligence for modern engineering organizations.
          </p>
        </div>

        {/* Diagnostic System Stats Row */}
        <div className="flex items-center gap-2 border border-border bg-surface-elevated/25 px-4 py-1.5 font-mono text-[11px] text-text-muted select-none rounded-full">
          <Terminal className="h-3.5 w-3.5 text-accent/80" />
          <span>platform: online</span>
        </div>

        {/* Tactile Call To Action Button */}
        <Button
          variant="primary"
          onClick={() => navigate('/onboarding')}
          className="group px-8 h-12 text-[14px] font-semibold w-full max-w-[200px] flex items-center justify-center gap-2 mt-4"
        >
          <span>Start Journey</span>
          <ArrowRight className="h-4 w-4 transition-transform duration-300 group-hover:translate-x-1" />
        </Button>
      </div>

      {/* Footer Branding Copyright */}
      <div className="absolute bottom-8 left-0 right-0 text-center font-mono text-[10px] text-text-muted select-none">
        <span>© 2026 PRFlow. All rights reserved.</span>
      </div>
    </div>
  )
}
export default LandingPage
