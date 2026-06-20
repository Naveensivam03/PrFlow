import { Panel } from '@/components/Panel'
import { Badge } from '@/components/Badge'

export function OverviewPage() {
  return (
    <div className="flex flex-col gap-6 select-none">
      <div className="flex items-center justify-between">
        <h1 className="font-display text-text-primary">System Overview</h1>
        <Badge variant="primary">INITIALIZED</Badge>
      </div>
      <Panel className="flex flex-col gap-4">
        <p className="text-text-secondary font-body">
          Welcome to the PRFlow Orchestration Console. The workflow engines and relational state buses are running in standby mode.
        </p>
      </Panel>
    </div>
  )
}
export default OverviewPage
