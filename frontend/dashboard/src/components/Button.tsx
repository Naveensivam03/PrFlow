import React from 'react'
import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
}

export function Button({
  className,
  variant = 'secondary',
  size = 'md',
  children,
  ...props
}: ButtonProps) {
  const baseStyle =
    'inline-flex items-center justify-center font-medium transition-all duration-200 outline-none disabled:opacity-50 disabled:pointer-events-none active:scale-[0.98]'

  const variants = {
    primary:
      'bg-accent/10 text-accent hover:bg-accent/20 border border-accent/20 mac-glow',
    secondary:
      'bg-surface-elevated/70 text-text-primary hover:bg-surface-elevated border border-border/80 mac-glow',
    danger:
      'bg-danger/10 text-danger hover:bg-danger/20 border border-danger/20 mac-glow',
    ghost: 'bg-transparent text-text-secondary hover:text-text-primary hover:bg-surface-elevated/35',
  }

  const sizes = {
    sm: 'h-9 px-4 text-caption rounded-button',
    md: 'h-10 px-5 text-body rounded-button',
    lg: 'h-11 px-6 text-body font-semibold rounded-button',
  }

  return (
    <button
      className={twMerge(clsx(baseStyle, variants[variant], sizes[size], className))}
      {...props}
    >
      {children}
    </button>
  )
}
export default Button
