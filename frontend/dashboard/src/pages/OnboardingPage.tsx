import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { Search, Check, GitBranch, Users, Brain, Activity, RefreshCw, ExternalLink } from 'lucide-react'
import { Button } from '@/components/Button'
import { Input } from '@/components/Input'
import { Panel } from '@/components/Panel'
import { Badge } from '@/components/Badge'

// Type definitions for onboarding stages
type OnboardingStage = 'WELCOME' | 'REPO_SELECTION' | 'SYNCHRONIZING'

interface Repository {
  id: number
  name: string
  isActive: boolean
}

const BACKEND_BASE = 'http://localhost:8080/api/onboarding'

export function OnboardingPage() {
  const navigate = useNavigate()
  const [stage, setStage] = useState<OnboardingStage>('WELCOME')
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedRepos, setSelectedRepos] = useState<number[]>([])

  // Live repositories loaded from backend
  const [repositories, setRepositories] = useState<Repository[]>([])
  const [connectedOrg, setConnectedOrg] = useState('Naveensivam03')
  const [isLoading, setIsLoading] = useState(false)
  const [apiError, setApiError] = useState(false)
  const [isRefreshing, setIsRefreshing] = useState(false)

  // Real-time Sync Status State Machine
  const [syncStates, setSyncStates] = useState({
    contributors: { status: 'PENDING' as 'PENDING' | 'ACTIVE' | 'COMPLETE', count: 0 },
    pullRequests: { status: 'PENDING' as 'PENDING' | 'ACTIVE' | 'COMPLETE', count: 0 },
    expertise: { status: 'PENDING' as 'PENDING' | 'ACTIVE' | 'COMPLETE', count: 0 },
    reviews: { status: 'PENDING' as 'PENDING' | 'ACTIVE' | 'COMPLETE', count: 0 },
  })

  const [isSyncComplete, setIsSyncComplete] = useState(false)

  // Fetch repositories from active backend
  const fetchRepositories = async (showLoading = true) => {
    if (showLoading) setIsLoading(true)
    setApiError(false)
    try {
      const response = await fetch(`${BACKEND_BASE}/repositories`)
      if (!response.ok) throw new Error('API unreachable')
      const data = await response.json()
      if (data.status === 'SUCCESS' && data.data) {
        setConnectedOrg(data.data.organization)
        // Map backend schema (id, name, isActive)
        const mapped = data.data.repositories.map((r: any) => ({
          id: Number(r.id),
          name: String(r.name),
          isActive: Boolean(r.is_active),
        }))
        setRepositories(mapped)
      }
    } catch (error) {
      console.warn('[PRFlow] Backend offline. Using simulation engine fallback.', error)
      setApiError(true)
      // Fallback preview simulation repos
      setRepositories([
        { id: 1, name: 'prflow-core', isActive: true },
        { id: 2, name: 'github-webhook-service', isActive: true },
        { id: 3, name: 'spring-api', isActive: false },
        { id: 4, name: 'infra-deployments', isActive: false },
        { id: 5, name: 'frontend-dashboard', isActive: false },
      ])
    } finally {
      setIsLoading(false)
    }
  }

  // Load repositories on connection transition
  useEffect(() => {
    if (stage === 'REPO_SELECTION') {
      fetchRepositories()
    }
  }, [stage])

  // Filter repositories based on search input
  const filteredRepos = repositories.filter((repo) =>
    repo.name.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  // Toggle Checkbox
  const toggleRepo = (id: number) => {
    setSelectedRepos((prev) =>
      prev.includes(id) ? prev.filter((repoId) => repoId !== id) : [...prev, id],
    )
  }

  // Action: Connect with GitHub
  const handleGitHubConnect = () => {
    setStage('REPO_SELECTION')
  }

  // Action: Refresh connection status (empty state check)
  const handleCheckConnection = async () => {
    setIsRefreshing(true)
    await fetchRepositories(false)
    setTimeout(() => setIsRefreshing(false), 500)
  }

  // Action: Simulate Webhook GitHub App installation
  const handleSimulateInstallation = async () => {
    setIsRefreshing(true)
    setApiError(false)
    try {
      const response = await fetch('http://localhost:3001/webhook/github/simulate-installation', {
        method: 'POST'
      })
      if (!response.ok) throw new Error('Simulation failed')
      // Immediately pull fresh repository list from active backend
      await fetchRepositories(false)
    } catch (error) {
      console.warn('[PRFlow] Webhook service offline or simulation failed, falling back to mock.', error)
      // Fallback: If webhook service is down, simply fake the load
      setApiError(true)
      setRepositories([
        { id: 1, name: 'prflow-core', isActive: true },
        { id: 2, name: 'github-webhook-service', isActive: true },
        { id: 3, name: 'spring-api', isActive: false },
        { id: 4, name: 'infra-deployments', isActive: false },
        { id: 5, name: 'frontend-dashboard', isActive: false },
      ])
    } finally {
      setIsRefreshing(false)
    }
  }

  // Action: Initialize Analysis (Post back selected repo IDs)
  const handleInitializeAnalysis = async () => {
    setIsLoading(true)
    try {
      if (!apiError) {
        // 1. Post to Spring Boot backend to activate repos
        const response = await fetch(`${BACKEND_BASE}/repositories/initialize`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ repositoryIds: selectedRepos }),
        })
        if (!response.ok) throw new Error('API initialization failed')

        // 2. Trigger simulated background sync on the webhook service to populate postgres DB in real-time
        try {
          await fetch('http://localhost:3001/webhook/github/simulate-sync', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ repositoryIds: selectedRepos }),
          })
        } catch (webhookErr) {
          console.warn('[PRFlow] Webhook service offline or unreachable for sync simulation', webhookErr)
        }
      }
      setStage('SYNCHRONIZING')
    } catch (error) {
      console.error('[PRFlow] Failed to initialize repositories', error)
      // Fallback gracefully anyway
      setStage('SYNCHRONIZING')
    } finally {
      setIsLoading(false)
    }
  }

  // Real-time Database Polling Sync Loop
  useEffect(() => {
    if (stage !== 'SYNCHRONIZING') return

    // If API is down, run the high-fidelity mock stream simulation
    if (apiError) {
      setSyncStates((prev) => ({ ...prev, contributors: { status: 'ACTIVE', count: 0 } }))
      const t1 = setTimeout(() => {
        setSyncStates((prev) => ({ ...prev, contributors: { status: 'COMPLETE', count: 18 } }))
        setSyncStates((prev) => ({ ...prev, pullRequests: { status: 'ACTIVE', count: 0 } }))
      }, 1500)

      const t2 = setTimeout(() => {
        setSyncStates((prev) => ({ ...prev, pullRequests: { status: 'COMPLETE', count: 142 } }))
        setSyncStates((prev) => ({ ...prev, expertise: { status: 'ACTIVE', count: 0 } }))
      }, 3500)

      const t3 = setTimeout(() => {
        setSyncStates((prev) => ({ ...prev, expertise: { status: 'COMPLETE', count: 85 } }))
        setSyncStates((prev) => ({ ...prev, reviews: { status: 'ACTIVE', count: 0 } }))
      }, 5700)

      const t4 = setTimeout(() => {
        setSyncStates((prev) => ({ ...prev, reviews: { status: 'COMPLETE', count: 320 } }))
        setIsSyncComplete(true)
      }, 7500)

      return () => {
        clearTimeout(t1)
        clearTimeout(t2)
        clearTimeout(t3)
        clearTimeout(t4)
      }
    }

    // If API is active, poll the database metrics directly
    let pollInterval: Timer
    let durationSeconds = 0

    const pollDatabase = async () => {
      durationSeconds += 1.5
      try {
        const response = await fetch(`${BACKEND_BASE}/sync/status`)
        if (!response.ok) throw new Error('Sync status unreachable')
        const data = await response.json()
        if (data.status === 'SUCCESS' && data.data) {
          const { contributors, pullRequests, expertise, reviews } = data.data

          const isContribComplete = contributors >= 15
          const isPrComplete = pullRequests >= 8
          const isExpertiseComplete = expertise >= 20
          const isReviewsComplete = reviews >= 10

          // Map polling data to state progress machines
          setSyncStates({
            contributors: {
              status: isContribComplete ? 'COMPLETE' : contributors > 0 ? 'ACTIVE' : 'PENDING',
              count: Number(contributors),
            },
            pullRequests: {
              status: isPrComplete ? 'COMPLETE' : pullRequests > 0 ? 'ACTIVE' : 'PENDING',
              count: Number(pullRequests),
            },
            expertise: {
              status: isExpertiseComplete ? 'COMPLETE' : expertise > 0 ? 'ACTIVE' : 'PENDING',
              count: Number(expertise),
            },
            reviews: {
              status: isReviewsComplete ? 'COMPLETE' : reviews > 0 ? 'ACTIVE' : 'PENDING',
              count: Number(reviews),
            },
          })

          // Finish Sync once review history database calculations complete
          if (isContribComplete && isPrComplete && isExpertiseComplete && isReviewsComplete) {
            setIsSyncComplete(true)
            clearInterval(pollInterval)
          }
        }
      } catch (error) {
        console.error('[PRFlow] Sync polling error', error)
      }
    }

    // Start polling immediately
    pollDatabase()
    pollInterval = setInterval(pollDatabase, 1500)

    return () => clearInterval(pollInterval)
  }, [stage, apiError])

  return (
    <div className="relative flex h-screen w-screen items-center justify-center bg-background p-6 overflow-hidden">
      {/* Background Soft Depth Radial Aura */}
      <div className="absolute right-[10%] top-[10%] -z-10 h-[50%] w-[50%] rounded-full bg-accent/[0.015] filter blur-[180px] pointer-events-none"></div>
      <div className="absolute bottom-[10%] left-[10%] -z-10 h-[55%] w-[55%] rounded-full bg-success/[0.012] filter blur-[200px] pointer-events-none"></div>

      {/* Screen Container */}
      <div className="w-full max-w-[640px] z-10 transition-all duration-500">
        
        {/* ================================================= */}
        {/* SCREEN 1: WELCOME */}
        {/* ================================================= */}
        {stage === 'WELCOME' && (
          <div className="flex flex-col items-center text-center animate-fade-in gap-8 select-none">
            {/* Emblem */}
            <div className="relative flex h-14 w-14 items-center justify-center rounded-2xl bg-surface border border-border mac-glow">
              <span className="text-body font-semibold font-mono tracking-tighter text-text-primary">PF</span>
            </div>
            
            <div className="flex flex-col gap-2.5">
              <h1 className="font-display font-semibold tracking-tight text-text-primary">PRFlow</h1>
              <p className="text-text-secondary font-body font-medium">
                Review Intelligence for Engineering Teams
              </p>
            </div>

            <Button 
              variant="secondary" 
              onClick={handleGitHubConnect}
              className="mt-6 px-8 h-12 text-[14px] bg-white/[0.012] border-white/[0.08] hover:bg-white/[0.03] select-none tracking-wide"
            >
              Continue with GitHub
            </Button>
          </div>
        )}

        {/* ================================================= */}
        {/* SCREEN 2: REPOSITORY SELECTION */}
        {/* ================================================= */}
        {stage === 'REPO_SELECTION' && (
          <div className="flex flex-col gap-8 select-none">
            {/* Header info */}
            <div className="flex flex-col gap-2">
              <span className="font-mono text-caption text-text-muted">connection /</span>
              <div className="flex items-center justify-between">
                <h1 className="font-heading font-semibold text-text-primary">Select Repositories</h1>
                <Badge variant="secondary" className="bg-text-muted/10 border-transparent text-[11px] font-mono text-text-muted font-normal select-none">
                  {connectedOrg}
                </Badge>
              </div>
            </div>

            {isLoading ? (
              <div className="flex flex-col items-center justify-center py-20 gap-3">
                <RefreshCw className="h-6 w-6 text-accent/80 animate-spin" />
                <span className="text-caption text-text-muted font-mono">Fetching connection state...</span>
              </div>
            ) : repositories.length === 0 ? (
              /* EMPTY STATE: GitHub App installation required */
              <div className="flex flex-col items-center justify-center py-12 text-center gap-6">
                <div className="flex flex-col gap-2 max-w-[420px]">
                  <span className="text-body font-medium text-text-primary">Integrate PRFlow GitHub App</span>
                  <p className="text-caption text-text-muted leading-relaxed">
                    Install the PRFlow App on your GitHub account or organization scope to securely sync contributors, commits, and pull requests.
                  </p>
                </div>
                <div className="flex flex-col items-center gap-4 w-full">
                  <div className="flex items-center gap-4">
                    <a
                      href="https://github.com/apps/prflow/installations/new"
                      target="_blank"
                      rel="noreferrer"
                      className="flex items-center gap-2 px-5 h-10 border border-white/[0.06] bg-white/[0.01] hover:bg-white/[0.03] rounded-button text-caption font-semibold transition-all duration-200"
                    >
                      <span>Install GitHub App</span>
                      <ExternalLink className="h-3.5 w-3.5" />
                    </a>
                    <Button
                      variant="secondary"
                      onClick={handleCheckConnection}
                      className="h-10 px-5 flex items-center gap-2"
                    >
                      <RefreshCw className={`h-3.5 w-3.5 ${isRefreshing ? 'animate-spin' : ''}`} />
                      <span>Check Connection</span>
                    </Button>
                  </div>
                  
                  <div className="w-full max-w-[320px] border-t border-white/[0.06] pt-5 mt-2 flex flex-col gap-2">
                    <span className="text-[11px] text-text-muted font-mono tracking-wide uppercase">Developer sandbox</span>
                    <Button
                      variant="secondary"
                      onClick={handleSimulateInstallation}
                      className="w-full h-10 text-[12px] bg-accent/5 border-accent/15 hover:bg-accent/10 text-accent font-medium flex items-center justify-center gap-2 cursor-pointer transition-all duration-200"
                    >
                      <Activity className="h-3.5 w-3.5" />
                      <span>Simulate GitHub Webhook Connection</span>
                    </Button>
                  </div>
                </div>
              </div>
            ) : (
              /* Active Repositories Selection Form */
              <div className="flex flex-col gap-4">
                <div className="relative flex items-center">
                  <Search className="absolute left-3.5 h-4 w-4 text-text-muted pointer-events-none" />
                  <Input
                    placeholder="Search repositories..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    className="pl-11"
                  />
                </div>

                <Panel className="p-0 border border-border/80 bg-surface-elevated/15 overflow-hidden">
                  <div className="max-h-[300px] overflow-y-auto">
                    {filteredRepos.map((repo, idx) => (
                      <div
                        key={repo.id}
                        onClick={() => toggleRepo(repo.id)}
                        className={`flex items-center justify-between px-6 py-4 cursor-pointer hover:bg-white/[0.01] transition-colors duration-200 ${
                          idx !== filteredRepos.length - 1 ? 'border-b border-border/60' : ''
                        }`}
                      >
                        <div className="flex items-center gap-4">
                          <div
                            className={`flex h-5 w-5 items-center justify-center rounded border transition-all duration-200 ${
                              selectedRepos.includes(repo.id)
                                ? 'bg-accent/15 border-accent/40 text-accent'
                                : 'border-border bg-transparent'
                            }`}
                          >
                            {selectedRepos.includes(repo.id) && <Check className="h-3.5 w-3.5" />}
                          </div>
                          <div>
                            <span className="text-body font-medium text-text-primary">{repo.name}</span>
                          </div>
                        </div>

                        {repo.isActive && (
                          <Badge variant="primary" className="bg-accent/5 border-accent/10 text-accent font-mono text-[9px] font-normal px-2">
                            active
                          </Badge>
                        )}
                      </div>
                    ))}
                  </div>
                </Panel>

                <div className="flex justify-end mt-4">
                  <Button
                    variant="primary"
                    onClick={handleInitializeAnalysis}
                    disabled={selectedRepos.length === 0}
                    className="px-6 h-11 text-[13px] font-semibold"
                  >
                    Initialize Analysis
                  </Button>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ================================================= */}
        {/* SCREEN 3: INITIAL SYNCHRONIZATION */}
        {/* ================================================= */}
        {stage === 'SYNCHRONIZING' && (
          <div className="flex flex-col gap-8 select-none">
            {/* Sync Header */}
            <div className="flex flex-col gap-2">
              <span className="font-mono text-caption text-text-muted">indexing /</span>
              <h1 className="font-heading font-semibold text-text-primary">
                {isSyncComplete ? 'Synchronization Complete' : 'Synchronizing Metadata...'}
              </h1>
              <p className="text-text-secondary font-body">
                {isSyncComplete
                  ? 'Organizational knowledge graph built successfully.'
                  : 'PRFlow is constructing a structural memory graph of the organization.'}
              </p>
            </div>

            {/* Sync Matrix */}
            <Panel className="flex flex-col gap-5 border border-border/80 bg-surface-elevated/15">
              
              {/* Item 1: Contributors */}
              <div className="flex items-center justify-between text-body">
                <div className="flex items-center gap-3.5">
                  <div className="flex h-5 w-5 items-center justify-center">
                    <Users className={`h-4.5 w-4.5 ${
                      syncStates.contributors.status === 'COMPLETE' ? 'text-success/80' : 
                      syncStates.contributors.status === 'ACTIVE' ? 'text-accent/80' : 'text-text-muted'
                    }`} />
                  </div>
                  <span className={syncStates.contributors.status === 'COMPLETE' ? 'text-text-primary font-medium' : 'text-text-secondary'}>
                    Contributors
                  </span>
                </div>
                <div className="flex items-center gap-3 font-mono text-caption">
                  {syncStates.contributors.status === 'COMPLETE' && (
                    <>
                      <span className="text-text-muted text-[12px]">{syncStates.contributors.count} indexed</span>
                      <span className="text-success text-[12px]">Sync</span>
                    </>
                  )}
                  {syncStates.contributors.status === 'ACTIVE' && (
                    <span className="text-accent text-[12px] animate-pulse">Syncing...</span>
                  )}
                  {syncStates.contributors.status === 'PENDING' && (
                    <span className="text-text-muted text-[12px]">Queue</span>
                  )}
                </div>
              </div>

              {/* Item 2: Pull Requests */}
              <div className="flex items-center justify-between text-body">
                <div className="flex items-center gap-3.5">
                  <div className="flex h-5 w-5 items-center justify-center">
                    <GitBranch className={`h-4.5 w-4.5 ${
                      syncStates.pullRequests.status === 'COMPLETE' ? 'text-success/80' : 
                      syncStates.pullRequests.status === 'ACTIVE' ? 'text-accent/80' : 'text-text-muted'
                    }`} />
                  </div>
                  <span className={syncStates.pullRequests.status === 'COMPLETE' ? 'text-text-primary font-medium' : 'text-text-secondary'}>
                    Pull Requests
                  </span>
                </div>
                <div className="flex items-center gap-3 font-mono text-caption">
                  {syncStates.pullRequests.status === 'COMPLETE' && (
                    <>
                      <span className="text-text-muted text-[12px]">{syncStates.pullRequests.count} indexed</span>
                      <span className="text-success text-[12px]">Sync</span>
                    </>
                  )}
                  {syncStates.pullRequests.status === 'ACTIVE' && (
                    <span className="text-accent text-[12px] animate-pulse">Ingesting...</span>
                  )}
                  {syncStates.pullRequests.status === 'PENDING' && (
                    <span className="text-text-muted text-[12px]">Queue</span>
                  )}
                </div>
              </div>

              {/* Item 3: Expertise Graphs */}
              <div className="flex items-center justify-between text-body">
                <div className="flex items-center gap-3.5">
                  <div className="flex h-5 w-5 items-center justify-center">
                    <Brain className={`h-4.5 w-4.5 ${
                      syncStates.expertise.status === 'COMPLETE' ? 'text-success/80' : 
                      syncStates.expertise.status === 'ACTIVE' ? 'text-accent/80' : 'text-text-muted'
                    }`} />
                  </div>
                  <span className={syncStates.expertise.status === 'COMPLETE' ? 'text-text-primary font-medium' : 'text-text-secondary'}>
                    Expertise Graphs
                  </span>
                </div>
                <div className="flex items-center gap-3 font-mono text-caption">
                  {syncStates.expertise.status === 'COMPLETE' && (
                    <>
                      <span className="text-text-muted text-[12px]">{syncStates.expertise.count} scopes mapped</span>
                      <span className="text-success text-[12px]">Sync</span>
                    </>
                  )}
                  {syncStates.expertise.status === 'ACTIVE' && (
                    <span className="text-accent text-[12px] animate-pulse">Building...</span>
                  )}
                  {syncStates.expertise.status === 'PENDING' && (
                    <span className="text-text-muted text-[12px]">Queue</span>
                  )}
                </div>
              </div>

              {/* Item 4: Review History */}
              <div className="flex items-center justify-between text-body">
                <div className="flex items-center gap-3.5">
                  <div className="flex h-5 w-5 items-center justify-center">
                    <Activity className={`h-4.5 w-4.5 ${
                      syncStates.reviews.status === 'COMPLETE' ? 'text-success/80' : 
                      syncStates.reviews.status === 'ACTIVE' ? 'text-accent/80' : 'text-text-muted'
                    }`} />
                  </div>
                  <span className={syncStates.reviews.status === 'COMPLETE' ? 'text-text-primary font-medium' : 'text-text-secondary'}>
                    Review History
                  </span>
                </div>
                <div className="flex items-center gap-3 font-mono text-caption">
                  {syncStates.reviews.status === 'COMPLETE' && (
                    <>
                      <span className="text-text-muted text-[12px]">{syncStates.reviews.count} reviews analyzed</span>
                      <span className="text-success text-[12px]">Sync</span>
                    </>
                  )}
                  {syncStates.reviews.status === 'ACTIVE' && (
                    <span className="text-accent text-[12px] animate-pulse">Analyzing...</span>
                  )}
                  {syncStates.reviews.status === 'PENDING' && (
                    <span className="text-text-muted text-[12px]">Queue</span>
                  )}
                </div>
              </div>
            </Panel>

            {/* Launch Console Trigger */}
            <div className={`flex justify-center transition-all duration-[600ms] ${
              isSyncComplete ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4 pointer-events-none'
            }`}>
              <Button
                variant="primary"
                onClick={() => navigate('/overview')}
                className="px-8 h-12 text-[14px] font-semibold w-full max-w-[200px]"
              >
                Launch Console
              </Button>
            </div>
          </div>
        )}

      </div>
    </div>
  )
}
export default OnboardingPage
