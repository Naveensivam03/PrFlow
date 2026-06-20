import { Panel } from '@/components/Panel'
import { Badge } from '@/components/Badge'

export function KnowledgePage() {
  return (
    <div className="flex flex-col gap-6 select-none">
      <div className="flex items-center justify-between">
        <h1 className="font-display text-text-primary">Familiarity & Expertise</h1>
        <Badge variant="primary">INDEXED</Badge>
      </div>
      <Panel className="flex flex-col gap-4">
        <p className="text-text-secondary font-body">
          Track touch recency decay curves, review scoring multipliers, and knowledge directory heatmaps.
        </p>
      </Panel>
    </div>
  )
}
export default KnowledgePage
