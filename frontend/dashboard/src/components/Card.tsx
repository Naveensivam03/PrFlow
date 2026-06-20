import React from 'react'
import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  hoverable?: boolean
  glass?: boolean
}

export function Card({
  className,
  hoverable = false,
  glass = true,
  children,
  ...props
}: CardProps) {
  return (
    <div
      className={twMerge(
        clsx(
          'transition-all duration-300 mac-glow rounded-card p-6',
          glass
            ? 'bg-white/[0.012] backdrop-blur-md border border-white/[0.045]'
            : 'bg-surface-elevated/40 border border-border/80',
          hoverable &&
            'hover:bg-white/[0.025] hover:border-white/[0.08] hover:scale-[1.002] cursor-pointer',
          className,
        ),
      )}
      {...props}
    >
      {children}
    </div>
  )
}
export default Card
