import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'Announcements — MythicDrop Docs' }

export default function Announcements() {
  return (
    <div className="doc-content">
      <h1>Damage Announcements</h1>
      <p>File: <code>plugins/MythicDropRefactored/announcement.yml</code></p>
      <p>When a boss dies, MythicDrop can announce to <strong>all players on the server</strong> who dealt the most damage — like a mini leaderboard in chat.</p>

      <h2>Basic Structure</h2>
      <CodeBlock language="yaml" title="announcement.yml">{`announce-on-death: true

messages:
  header: "&aLIST OF PLAYERS WHO HAVE INFLICTED THE MOST DAMAGE ON %BOSSNAME%:"
  entry: "&e%position%. %player% - %damage% DAMAGE"
  no-players: "&cNo players contributed damage to the mob."

announce-specific-mob:
  SkeletonKing: true
  CaveSpider: false`}</CodeBlock>

      <h2>Settings</h2>
      <h3>announce-on-death</h3>
      <p>Global toggle. Set to <code>true</code> to announce when any mob dies, <code>false</code> to disable by default. Individual mobs can override this (see below).</p>

      <h3>messages</h3>
      <div className="overflow-x-auto mb-4 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Field</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Description</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['header','The first line of the announcement (shown once)'],
              ['entry','Repeated once per player in the ranking'],
              ['no-players','Shown if no players dealt damage to the mob'],
            ].map(([a,b],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-emerald-300 font-mono text-xs border-b border-gray-800">{a}</td>
                <td className="px-4 py-2.5 text-gray-400 text-xs border-b border-gray-800">{b}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h3>Variables</h3>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Variable</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Replaced With</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['%BOSSNAME%','The MythicMobs internal name of the mob'],
              ['%position%',"The player's rank (1, 2, 3...)"],
              ['%player%',"The player's name"],
              ['%damage%','Total damage dealt (whole number)'],
            ].map(([a,b],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-emerald-300 font-mono text-xs border-b border-gray-800">{a}</td>
                <td className="px-4 py-2.5 text-gray-400 text-xs border-b border-gray-800">{b}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h3>announce-specific-mob</h3>
      <p>Override the global setting for specific mobs. If a mob is not listed here, the global <code>announce-on-death</code> applies.</p>
      <CodeBlock language="yaml">{`announce-specific-mob:
  SkeletonKing: true     # Always announce, even if global is false
  CaveSpider: false      # Never announce, even if global is true
  AncientDragon: true`}</CodeBlock>

      <h2>How Many Players Are Shown?</h2>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Reward System</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Players Shown</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['Top-3 mob','Top 3 players'],
              ['Top-5 mob','Top 5 players'],
              ['Custom TopN mob','Top N players'],
              ['Standard drops (config.yml)','All players who dealt damage'],
            ].map(([a,b],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-gray-300 text-xs border-b border-gray-800">{a}</td>
                <td className="px-4 py-2.5 text-gray-400 text-xs border-b border-gray-800">{b}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h2>Example Chat Output</h2>
      <div className="bg-gray-900 border border-gray-700 rounded-xl p-4 mb-6 font-mono text-sm">
        <div className="text-yellow-300">--- Boss Defeated: SkeletonKing ---</div>
        <div className="text-gray-300 mt-1">  <span className="text-yellow-400">#1</span> Steve — <span className="text-red-400">1250</span> damage dealt</div>
        <div className="text-gray-300">  <span className="text-yellow-400">#2</span> Alex — <span className="text-red-400">980</span> damage dealt</div>
        <div className="text-gray-300">  <span className="text-yellow-400">#3</span> Notch — <span className="text-red-400">520</span> damage dealt</div>
      </div>

      <h2>Full Example</h2>
      <CodeBlock language="yaml" title="announcement.yml">{`announce-on-death: true

messages:
  header: "&6&l--- &eBoss Defeated: &f%BOSSNAME% &6&l---"
  entry: "  &e#%position% &f%player% &7— &c%damage% &7damage dealt"
  no-players: "&cNo one dealt damage to this mob."

announce-specific-mob:
  SkeletonKing: true
  AncientDragon: true
  TrainingDummy: false   # Don't spam chat for training mobs
  CaveSpider: false      # Too common to announce every time`}</CodeBlock>

      <Callout type="tip">Disable announcements for common/trash mobs with <code>announce-specific-mob: false</code> to avoid chat spam. Only enable it for important boss mobs.</Callout>

      <DocNav prev={{ href: '/configuration/effects', label: 'Death Effects' }} next={{ href: '/placeholders', label: 'PlaceholderAPI' }} />
    </div>
  )
}
