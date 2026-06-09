import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'
import Link from 'next/link'

export default function Home() {
  return (
    <div className="doc-content">
      <div className="mb-8 p-6 rounded-2xl bg-gradient-to-br from-emerald-950/60 to-gray-900 border border-emerald-800/40">
        <div className="flex items-center gap-3 mb-3">
          <div className="w-10 h-10 bg-emerald-500 rounded-xl flex items-center justify-center text-black font-bold">
            MD
          </div>
          <div>
            <h1 className="text-2xl font-bold text-white" style={{borderBottom: 'none', marginBottom: 0, marginTop: 0, paddingBottom: 0}}>MythicDrop</h1>
            <p className="text-emerald-400 text-sm" style={{marginBottom: 0}}>Complete Documentation</p>
          </div>
        </div>
        <p className="text-slate-300 text-sm leading-6" style={{marginBottom: 0}}>
          A Minecraft server plugin that works alongside MythicMobs to give you powerful boss reward systems, quests, arenas, and more.
        </p>
      </div>

      <h2>What is MythicDrop?</h2>
      <p>
        MythicDrop is a Bukkit/Spigot plugin that hooks into <strong>MythicMobs</strong> and lets you fully control what happens when players kill custom mobs — from giving items and running commands, to ranking players by damage and running quests.
      </p>

      <h2>Key Features</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 my-6">
        {[
          { icon: '🎁', title: 'Custom Drops', desc: 'Give items, run commands, or award money when a mob dies. Supports group-based rewards via LuckPerms.' },
          { icon: '📜', title: 'Quest System', desc: 'Players track kill goals in a GUI and earn rewards on completion. Progress is saved permanently.' },
          { icon: '🏆', title: 'Top Damage Rewards', desc: 'Rank players by damage dealt and give tiered rewards to Top 3, Top 5, or any custom number.' },
          { icon: '🏟️', title: 'Arena System', desc: 'Auto-respawning boss arenas. Set a location and the mob spawns, dies, and respawns automatically.' },
          { icon: '🎆', title: 'Death Effects', desc: 'Play fireworks and sounds when a boss dies to make kills feel epic.' },
          { icon: '📊', title: 'PlaceholderAPI', desc: 'Live damage rankings, quest progress, arena status — all available as placeholders for scoreboards.' },
        ].map((f) => (
          <div key={f.title} className="bg-gray-800/50 border border-gray-700 rounded-xl p-4">
            <div className="text-2xl mb-2">{f.icon}</div>
            <div className="font-semibold text-white mb-1">{f.title}</div>
            <div className="text-sm text-gray-400 leading-5">{f.desc}</div>
          </div>
        ))}
      </div>

      <h2>Requirements</h2>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Software</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Required?</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Notes</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['Spigot / Paper 1.21+', '✅ Yes', 'Your server software'],
              ['MythicMobs', '✅ Yes', 'The mob plugin MythicDrop hooks into'],
              ['LuckPerms', '⬜ Optional', 'Needed for group-based reward tiers (VIP, Legend, etc.)'],
              ['PlaceholderAPI', '⬜ Optional', 'Needed for scoreboard/GUI placeholders'],
            ].map(([a, b, c], i) => (
              <tr key={i} className={i % 2 === 0 ? 'bg-gray-900/30' : ''}>
                <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 font-mono text-xs">{a}</td>
                <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800">{b}</td>
                <td className="px-4 py-2.5 text-gray-400 border-b border-gray-800 text-xs">{c}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h2>Quick Start</h2>
      <ol className="space-y-3 mb-6" style={{listStyleType: 'none', paddingLeft: 0}}>
        {[
          <>Drop the plugin <code>.jar</code> into your <code>plugins/</code> folder</>,
          <>Restart your server — config files are created automatically</>,
          <>Open <code>plugins/MythicDropRefactored/config.yml</code></>,
          <>Add a reward for one of your MythicMobs</>,
          <>Reload with <code>/mythicdrop reload</code></>,
        ].map((step, i) => (
          <li key={i} className="flex items-start gap-3">
            <span className="w-6 h-6 bg-emerald-500/20 text-emerald-400 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0 mt-0.5">{i + 1}</span>
            <span className="text-slate-300 text-sm leading-6">{step}</span>
          </li>
        ))}
      </ol>

      <h2>Folder Structure</h2>
      <p>After the plugin runs for the first time, your <code>plugins/</code> folder will contain:</p>
      <CodeBlock language="text" title="plugins/MythicDropRefactored/">
{`plugins/
  MythicDropRefactored/
    config.yml          ← Main drops configuration
    quests.yml          ← Quest definitions
    top3damage.yml      ← Top-3 boss reward system
    top5damage.yml      ← Top-5 boss reward system
    announcement.yml    ← Chat announcements after boss death
    effects.yml         ← Fireworks & sounds on boss death
    debug.yml           ← Turn on debug logging
    arenas.yml          ← Auto-created when you make arenas
    quest_data.yml      ← Player quest progress (auto-created)`}
      </CodeBlock>

      <Callout type="tip">
        You can create additional files like <code>top7damage.yml</code>, <code>top10damage.yml</code>, etc. for custom ranking systems.
      </Callout>

      <h2>Where to Start?</h2>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 mt-4">
        {[
          { href: '/installation', label: 'Installation Guide', desc: 'Install the plugin step by step', icon: '📦' },
          { href: '/configuration/drops', label: 'Configure Drops', desc: 'Give rewards when mobs die', icon: '🎁' },
          { href: '/configuration/quests', label: 'Set Up Quests', desc: 'Create kill-tracking quests', icon: '📜' },
        ].map((card) => (
          <Link key={card.href} href={card.href} className="block p-4 bg-gray-800/60 border border-gray-700 hover:border-emerald-600/50 rounded-xl transition-colors group no-underline">
            <div className="text-xl mb-1">{card.icon}</div>
            <div className="font-medium text-white group-hover:text-emerald-400 transition-colors text-sm">{card.label}</div>
            <div className="text-xs text-gray-500 mt-0.5">{card.desc}</div>
          </Link>
        ))}
      </div>

      <DocNav next={{ href: '/installation', label: 'Installation' }} />
    </div>
  )
}
