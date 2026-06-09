'use client'
import Link from 'next/link'
import { usePathname } from 'next/navigation'

const nav = [
  {
    label: 'Getting Started',
    items: [
      { href: '/', label: 'Overview' },
      { href: '/installation', label: 'Installation' },
      { href: '/commands', label: 'Commands & Permissions' },
    ],
  },
  {
    label: 'Configuration',
    items: [
      { href: '/configuration/drops', label: 'Drops (config.yml)' },
      { href: '/configuration/quests', label: 'Quest System' },
      { href: '/configuration/top-rewards', label: 'Top Damage Rewards' },
      { href: '/configuration/arenas', label: 'Arena System' },
      { href: '/configuration/effects', label: 'Death Effects' },
      { href: '/configuration/announcements', label: 'Announcements' },
    ],
  },
  {
    label: 'Reference',
    items: [
      { href: '/placeholders', label: 'PlaceholderAPI' },
      { href: '/faq', label: 'FAQ & Troubleshooting' },
    ],
  },
]

export default function Sidebar() {
  const pathname = usePathname()

  return (
    <aside className="fixed top-0 left-0 h-full w-[280px] bg-[#0d1117] border-r border-gray-800 overflow-y-auto z-30 flex flex-col">
      {/* Logo */}
      <div className="px-6 py-5 border-b border-gray-800">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-emerald-500 rounded-lg flex items-center justify-center text-black font-bold text-sm">
            MD
          </div>
          <div>
            <div className="font-bold text-white text-sm">MythicDrop</div>
            <div className="text-gray-500 text-xs">Documentation</div>
          </div>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 px-4 py-4 space-y-6">
        {nav.map((section) => (
          <div key={section.label}>
            <div className="text-xs font-semibold text-gray-500 uppercase tracking-wider px-2 mb-2">
              {section.label}
            </div>
            <ul className="space-y-0.5">
              {section.items.map((item) => {
                const active = pathname === item.href
                return (
                  <li key={item.href}>
                    <Link
                      href={item.href}
                      className={`block px-3 py-2 rounded-md text-sm transition-colors ${
                        active
                          ? 'bg-emerald-500/15 text-emerald-400 font-medium'
                          : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800'
                      }`}
                    >
                      {item.label}
                    </Link>
                  </li>
                )
              })}
            </ul>
          </div>
        ))}
      </nav>

      {/* Version badge */}
      <div className="px-6 py-4 border-t border-gray-800">
        <span className="text-xs text-gray-600">API Version: 1.21</span>
      </div>
    </aside>
  )
}
