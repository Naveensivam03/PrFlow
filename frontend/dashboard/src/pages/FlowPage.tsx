import { Panel } from '@/components/Panel'
import { Badge } from '@/components/Badge'

export function FlowPage() {
  return (
    <div className="flex flex-col gap-6 select-none">
      <div className="flex items-center justify-between">
        <h1 className="font-display text-text-primary">Flow Engine</h1>
        <Badge variant="success">READY</Badge>
      </div>
      <Panel className="flex flex-col gap-4">
        <p className="text-text-secondary font-body">
          Configure linear routing loops, complexity barriers, and reviewer allocation graphs.
        </p>
      </Panel>
    </div>
  )
}
export default FlowPage
