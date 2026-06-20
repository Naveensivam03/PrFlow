import { Panel } from '@/components/Panel'
import { Badge } from '@/components/Badge'
import { Button } from '@/components/Button'
import { Input } from '@/components/Input'

export function SettingsPage() {
  return (
    <div className="flex flex-col gap-6 select-none">
      <div className="flex items-center justify-between">
        <h1 className="font-display text-text-primary">Platform Settings</h1>
        <Badge variant="secondary">CONFIG</Badge>
      </div>
      <Panel className="flex flex-col gap-6">
        <div className="flex flex-col gap-2">
          <label className="font-subheading text-text-primary">Webhook Endpoint</label>
          <p className="text-text-muted font-caption">Set the security endpoint for synchronizing GitHub App Webhooks.</p>
          <div className="flex gap-4 items-center">
            <Input defaultValue="https://api.prflow.dev/events/ingress" className="flex-1" readOnly />
            <Button variant="primary">Update</Button>
          </div>
        </div>
      </Panel>
    </div>
  )
}
export default SettingsPage
