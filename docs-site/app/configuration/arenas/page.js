import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'Arena System — MythicDrop Docs' }

export default function Arenas() {
  return (
    <div className="doc-content">
      <h1>Arena System</h1>
      <p>Arenas are locations where a MythicMob <strong>automatically spawns and respawns</strong> after being killed. Perfect for boss rooms that should always have an active boss.</p>

      <Callout type="note">Arenas are managed through in-game commands. The plugin saves them automatically to <code>arenas.yml</code> — you do not need to edit that file manually.</Callout>

      <h2>Creating an Arena</h2>
      <p>Stand at the exact location you want the boss to spawn, then run:</p>
      <CodeBlock language="bash">{`/marena setspawn <name> <mob> <respawn> <despawn> <radius>`}</CodeBlock>

      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Argument</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Description</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Example</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['name','Unique name (no spaces — use _)','skeleton_boss_room'],
              ['mob','MythicMobs internal mob name (exact, case-sensitive)','SkeletonKing'],
              ['respawn','Seconds before mob respawns after dying','60'],
              ['despawn','Seconds before mob auto-despawns if not killed (0 = never)','600'],
              ['radius','Arena radius (informational only)','15'],
            ].map(([a,b,c],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-emerald-300 border-b border-gray-800 font-mono text-xs">{a}</td>
                <td className="px-4 py-2.5 text-gray-400 border-b border-gray-800 text-xs">{b}</td>
                <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 font-mono text-xs">{c}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <CodeBlock language="bash" title="Example">{`/marena setspawn skeleton_boss_room SkeletonKing 60 600 15`}</CodeBlock>
      <p>This spawns the boss immediately and configures it to respawn 60 seconds after dying, or auto-despawn after 10 minutes if not killed.</p>

      <h2>Arena Lifecycle</h2>
      <div className="my-6 relative">
        <div className="space-y-3">
          {[
            {c:'bg-emerald-500', t:'Boss Spawns', d:'Mob appears at the saved location'},
            {c:'bg-yellow-500', t:'Players Fight', d:'Damage is tracked for rankings and rewards'},
            {c:'bg-red-500', t:'Boss Dies', d:'Rewards are distributed, respawn timer starts'},
            {c:'bg-blue-500', t:'Respawn Timer', d:'Waiting the configured number of seconds'},
            {c:'bg-emerald-500', t:'Boss Respawns', d:'Cycle repeats automatically'},
          ].map((s,i) => (
            <div key={i} className="flex items-center gap-4">
              <div className={`w-3 h-3 rounded-full flex-shrink-0 ${s.c}`}></div>
              <div className="bg-gray-800/50 border border-gray-700 rounded-lg px-4 py-2.5 flex-1">
                <span className="font-medium text-white text-sm">{s.t}</span>
                <span className="text-gray-500 text-xs ml-2">— {s.d}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      <h2>Other Arena Commands</h2>
      <h3>/marena list</h3>
      <CodeBlock language="bash">{`/marena list`}</CodeBlock>
      <p>Shows all configured arenas with their current settings.</p>

      <h3>/marena delete</h3>
      <CodeBlock language="bash">{`/marena delete <name>`}</CodeBlock>
      <p>Removes an arena and kills any currently spawned mob. The mob will not respawn.</p>

      <h2>Tips</h2>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Tip</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Details</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['Respawn ≥ 30s','Give players time to collect drops before the next boss appears'],
              ['Despawn = 0','Boss stays alive indefinitely until killed (never auto-despawns)'],
              ['Multiple arenas','You can have as many arenas as you want across any world'],
              ['Arena rewards','Arena mobs work with all reward systems: top3, top5, config.yml drops'],
              ['Quest integration','Kills from arena mobs count toward quest progress automatically'],
            ].map(([a,b],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 text-xs font-medium">{a}</td>
                <td className="px-4 py-2.5 text-gray-400 border-b border-gray-800 text-xs">{b}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h2>PlaceholderAPI Integration</h2>
      <p>If you have PlaceholderAPI installed, you can show arena info on scoreboards:</p>
      <CodeBlock language="text">{`%mythicdrop_arena_count%                    → Total number of arenas
%mythicdrop_arena_live_count%               → How many arenas have a live boss
%mythicdrop_arena_<name>_exists%            → true or false
%mythicdrop_arena_<name>_mob%               → The mob type
%mythicdrop_arena_<name>_world%             → World name
%mythicdrop_arena_<name>_alive%             → Is the boss alive right now?`}</CodeBlock>
      <p>Replace <code>&lt;name&gt;</code> with your arena name, e.g. <code>%mythicdrop_arena_skeleton_boss_room_alive%</code></p>

      <DocNav prev={{ href: '/configuration/top-rewards', label: 'Top Damage Rewards' }} next={{ href: '/configuration/effects', label: 'Death Effects' }} />
    </div>
  )
}
