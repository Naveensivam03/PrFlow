import React from 'react'
import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export interface BadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  variant?: 'primary' | 'secondary' | 'success' | 'warning' | 'danger'
}

export function Badge({ className, variant = 'secondary', children, ...props }: BadgeProps) {
  const baseStyle =
    'inline-flex items-center px-2.5 py-0.5 text-caption font-semibold rounded-full border transition-colors duration-200'

  const variants = {
    primary: 'bg-accent/10 text-accent border-accent/20',
    secondary: 'bg-surface-elevated text-text-secondary border-border',
    success: 'bg-success/10 text-success border-success/20',
    warning: 'bg-warning/10 text-warning border-warning/20',
    danger: 'bg-danger/10 text-danger border-danger/20',
  }

  return (
    <span className={twMerge(clsx(baseStyle, variants[variant], className))} {...props}>
      {children}
    </span>
  )
}
