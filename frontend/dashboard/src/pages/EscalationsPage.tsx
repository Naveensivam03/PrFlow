import { Panel } from '@/components/Panel'
import { Badge } from '@/components/Badge'

export function EscalationsPage() {
  return (
    <div className="flex flex-col gap-6 select-none">
      <div className="flex items-center justify-between">
        <h1 className="font-display text-text-primary">SLA Escalations</h1>
        <Badge variant="warning">MONITORING</Badge>
      </div>
      <Panel className="flex flex-col gap-4">
        <p className="text-text-secondary font-body">
          Observe active pull request review delay thresholds, self-healing reassignment loops, and manager reminders.
        </p>
      </Panel>
    </div>
  )
}
export default EscalationsPage
