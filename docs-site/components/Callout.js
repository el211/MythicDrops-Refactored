const types = {
  tip: {
    icon: '💡',
    label: 'Tip',
    bg: 'bg-emerald-950/40',
    border: 'border-emerald-500/40',
    text: 'text-emerald-300',
    label_color: 'text-emerald-400',
  },
  warning: {
    icon: '⚠️',
    label: 'Warning',
    bg: 'bg-yellow-950/40',
    border: 'border-yellow-500/40',
    text: 'text-yellow-200',
    label_color: 'text-yellow-400',
  },
  note: {
    icon: 'ℹ️',
    label: 'Note',
    bg: 'bg-blue-950/40',
    border: 'border-blue-500/40',
    text: 'text-blue-200',
    label_color: 'text-blue-400',
  },
  danger: {
    icon: '🚫',
    label: 'Important',
    bg: 'bg-red-950/40',
    border: 'border-red-500/40',
    text: 'text-red-200',
    label_color: 'text-red-400',
  },
}

export default function Callout({ type = 'note', children }) {
  const t = types[type]
  return (
    <div className={`my-5 rounded-lg border ${t.bg} ${t.border} px-4 py-3`}>
      <div className={`font-semibold text-sm mb-1 ${t.label_color}`}>
        {t.icon} {t.label}
      </div>
      <div className={`text-sm leading-6 ${t.text}`}>{children}</div>
    </div>
  )
}
