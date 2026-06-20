import React from 'react'
import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type = 'text', ...props }, ref) => {
    return (
      <input
        type={type}
        className={twMerge(
          clsx(
            'flex w-full h-10 px-4 text-body bg-surface-elevated/30 border border-border/60 rounded-input text-text-primary placeholder:text-text-muted transition-all duration-200 focus:border-accent/40 focus:outline-none disabled:cursor-not-allowed disabled:opacity-50 mac-glow',
            className,
          ),
        )}
        ref={ref}
        {...props}
      />
    )
  },
)
Input.displayName = 'Input'
export default Input
