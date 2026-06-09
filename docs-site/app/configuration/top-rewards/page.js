import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'Top Damage Rewards — MythicDrop Docs' }

export default function TopRewards() {
  return (
    <div className="doc-content">
      <h1>Top Damage Reward System</h1>
      <p>Reward players based on how much damage they dealt to a boss. Perfect for multiplayer boss fights where the best damage dealers should get the best rewards.</p>

      <h2>Overview</h2>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        {[
          ['top3damage.yml','Top 3 System','Rank top 3 players + everyone else'],
          ['top5damage.yml','Top 5 System','Rank top 5 players + everyone else'],
          ['topNdamage.yml','Custom System','Create any number (top7, top10, etc.)'],
        ].map(([f,t,d]) => (
          <div key={f} className="bg-gray-800/50 border border-gray-700 rounded-xl p-4">
            <code className="text-emerald-300 text-xs block mb-2">{f}</code>
            <div className="font-semibold text-white text-sm mb-1">{t}</div>
            <div className="text-gray-400 text-xs">{d}</div>
          </div>
        ))}
      </div>

      <h2>How It Works</h2>
      <div className="flex flex-col gap-2 mb-8">
        {[
          ['Players attack a boss','DamageTracker records every hit from every player'],
          ['Boss dies','All players are ranked by total damage dealt'],
          ['Top N players rewarded','Each rank gets its own specific rewards'],
          ['Everyone else rewarded','Players outside top N who dealt enough damage get a participation reward'],
        ].map(([t,d],i) => (
          <div key={i} className="flex items-start gap-3 bg-gray-800/40 border border-gray-700 rounded-lg p-3">
            <div className="w-6 h-6 bg-emerald-500/20 text-emerald-400 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0">{i+1}</div>
            <div>
              <div className="font-medium text-white text-sm">{t}</div>
              <div className="text-gray-500 text-xs">{d}</div>
            </div>
          </div>
        ))}
      </div>

      <h2>File Structure (top3damage.yml)</h2>
      <CodeBlock language="yaml" title="top3damage.yml">{`rewardtop3:
  - SkeletonKing          # Mobs that use the top-3 system

rewardtop3-settings:
  use-flexible-rewards: false   # true = all rewards roll; false = stop at guaranteed count

SkeletonKing:
  use-standard-rewards: false   # Don't also run config.yml drops
  guaranteedperrank: true
  per-rank-group: true
  guaranteed-rewards:
    default: 1
    vip: 2

  first-place:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 10"
        chance: 1.0
        message: "&b&l[#1] Champion reward!"
    vip:
      drop1:
        command: "give %player% diamond 20"
        chance: 1.0
        message: "&b&l[VIP #1] Champion reward!"

  second-place:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 5"
        chance: 1.0
        message: "&e[#2] Silver reward!"

  third-place:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% diamond 2"
        chance: 1.0
        message: "&c[#3] Bronze reward!"

  everyone-else-who-contributed:
    min-damage: 50.0          # Minimum damage needed to qualify
    default:
      drop1:
        command: "give %player% emerald 1"
        chance: 0.8
        message: "&aThanks for helping!"`}</CodeBlock>

      <h2>Key Settings Explained</h2>

      <h3>rewardtopN list</h3>
      <p>List the <strong>exact MythicMobs names</strong> of bosses that should use this ranking system.</p>
      <CodeBlock language="yaml">{`rewardtop3:
  - SkeletonKing
  - AncientDragon`}</CodeBlock>

      <h3>use-flexible-rewards</h3>
      <div className="overflow-x-auto mb-4 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Value</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Meaning</th>
            </tr>
          </thead>
          <tbody>
            <tr className="bg-gray-900/30">
              <td className="px-4 py-2.5 text-emerald-400 font-mono text-xs border-b border-gray-800">false</td>
              <td className="px-4 py-2.5 text-gray-300 text-xs border-b border-gray-800">Only give guaranteed rewards, stop there</td>
            </tr>
            <tr>
              <td className="px-4 py-2.5 text-emerald-400 font-mono text-xs border-b border-gray-800">true</td>
              <td className="px-4 py-2.5 text-gray-300 text-xs border-b border-gray-800">All rewards roll their individual chances</td>
            </tr>
          </tbody>
        </table>
      </div>

      <h3>everyone-else-who-contributed</h3>
      <p>Players not in the top N can still earn rewards if they hit the <code>min-damage</code> threshold.</p>
      <CodeBlock language="yaml">{`everyone-else-who-contributed:
  min-damage: 50.0      # Player must deal at least 50 damage to qualify
  default:
    drop1:
      command: "give %player% emerald 1"
      chance: 0.8`}</CodeBlock>

      <h2>Custom TopX Files</h2>
      <p>Create a file named <code>topNdamage.yml</code> (replace N with any number). For custom files, use <code>position-1</code>, <code>position-2</code>, etc. instead of <code>first-place</code>, <code>second-place</code>.</p>
      <CodeBlock language="yaml" title="top7damage.yml">{`rewardtop7:
  - WorldBoss

rewardtop7-settings:
  use-flexible-rewards: true

WorldBoss:
  use-standard-rewards: false
  guaranteedperrank: true
  per-rank-group: false
  guaranteed-rewards: 1

  position-1:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% netherite_ingot 3"
        chance: 1.0
        message: "&4&l[#1] Netherite Champion!"

  position-2:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% netherite_ingot 2"
        chance: 1.0
        message: "&c[#2] Great job!"

  position-3:
    guaranteed-rewards: 1
    default:
      drop1:
        command: "give %player% netherite_ingot 1"
        chance: 1.0

  everyone-else-who-contributed:
    min-damage: 100.0
    default:
      drop1:
        command: "give %player% emerald 1"
        chance: 1.0`}</CodeBlock>

      <Callout type="note">A mob can only be in <strong>one</strong> top-damage system at a time. If a mob is in top3, it will not use top5 or custom files.</Callout>

      <h2>Priority Order</h2>
      <div className="space-y-2 mb-6">
        {[
          'Is the mob in rewardtop3? → Use Top-3 system',
          'Is the mob in rewardtop5? → Use Top-5 system',
          'Is the mob in any topNdamage.yml? → Use that TopX system',
          'Does the mob have drops in config.yml? → Use standard drops',
        ].map((s,i) => (
          <div key={i} className="flex items-center gap-3 bg-gray-800/40 border border-gray-700 rounded-lg px-4 py-2.5">
            <div className="w-5 h-5 bg-emerald-500/20 text-emerald-400 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0">{i+1}</div>
            <span className="text-gray-300 text-sm">{s}</span>
          </div>
        ))}
      </div>

      <DocNav prev={{ href: '/configuration/quests', label: 'Quest System' }} next={{ href: '/configuration/arenas', label: 'Arena System' }} />
    </div>
  )
}
