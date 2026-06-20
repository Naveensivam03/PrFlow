import React from 'react'
import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export interface PanelProps extends React.HTMLAttributes<HTMLDivElement> {
  glass?: boolean
}

export function Panel({ className, glass = true, children, ...props }: PanelProps) {
  return (
    <div
      className={twMerge(
        clsx(
          'rounded-panel p-8 transition-all duration-300 mac-glow',
          glass ? 'glass-panel' : 'bg-surface-elevated border border-border',
          className,
        ),
      )}
      {...props}
    >
      {children}
    </div>
  )
}
export default Panel
