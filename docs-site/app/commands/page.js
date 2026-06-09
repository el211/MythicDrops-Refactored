import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'Commands — MythicDrop Docs' }

function CmdCard({ cmd, permission, defaultAccess, desc, children }) {
  return (
    <div className="mb-8 rounded-xl border border-gray-700 overflow-hidden">
      <div className="bg-gray-800/80 px-5 py-3 flex flex-wrap items-center gap-3">
        <code className="text-emerald-300 font-bold text-base">{cmd}</code>
        <span className="text-xs bg-gray-700 text-gray-300 px-2 py-0.5 rounded font-mono">{permission}</span>
        <span className={`text-xs px-2 py-0.5 rounded font-medium ${defaultAccess === 'OP' ? 'bg-red-900/50 text-red-400' : 'bg-emerald-900/50 text-emerald-400'}`}>
          Default: {defaultAccess}
        </span>
      </div>
      <div className="px-5 py-4">
        <p className="text-gray-300 text-sm mb-3">{desc}</p>
        {children}
      </div>
    </div>
  )
}

export default function Commands() {
  return (
    <div className="doc-content">
      <h1>Commands & Permissions</h1>
      <p>Every command available in MythicDrop, who can use them, and what they do.</p>

      <h2>Permission Summary</h2>
      <div className="overflow-x-auto mb-8 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Permission</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Command</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Default</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['mythicdrop.reload', '/mythicdrop reload', 'OP'],
              ['mythicdrop.arena', '/marena', 'OP'],
              ['mythicdrop.quests', '/mquests', 'Everyone'],
              ['mythicdrop.mmobs', '/mmobs', 'OP'],
            ].map(([p, c, d], i) => (
              <tr key={i} className={i % 2 === 0 ? 'bg-gray-900/30' : ''}>
                <td className="px-4 py-2 text-gray-300 border-b border-gray-800 font-mono text-xs">{p}</td>
                <td className="px-4 py-2 text-emerald-400 border-b border-gray-800 font-mono text-xs">{c}</td>
                <td className="px-4 py-2 text-gray-400 border-b border-gray-800 text-xs">{d}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Callout type="tip">
        To grant a permission to a non-OP player with LuckPerms:<br/>
        <code>/lp user &lt;playername&gt; permission set mythicdrop.quests true</code>
      </Callout>

      <h2>Command Reference</h2>

      <CmdCard cmd="/mythicdrop reload" permission="mythicdrop.reload" defaultAccess="OP" desc="Reloads all configuration files without restarting the server. Always run this after editing any config file.">
        <p className="text-sm text-gray-400 mb-2">What gets reloaded:</p>
        <ul className="text-sm text-gray-400 space-y-0.5">
          {['config.yml (mob drops)', 'quests.yml (quests)', 'top3damage.yml, top5damage.yml, any custom topNdamage.yml', 'announcement.yml', 'effects.yml', 'debug.yml'].map(f => <li key={f}>{f}</li>)}
        </ul>
      </CmdCard>

      <CmdCard cmd="/marena" permission="mythicdrop.arena" defaultAccess="OP" desc="Manages boss arenas — locations where a MythicMob automatically spawns and respawns.">
        <h3 style={{marginTop: '0.5rem'}}>/marena setspawn</h3>
        <CodeBlock language="bash">{`/marena setspawn <name> <mob> <respawn> <despawn> <radius>`}</CodeBlock>
        <p className="text-sm text-gray-300 mb-3">Creates or updates an arena at your current standing position.</p>
        <div className="overflow-x-auto rounded-lg border border-gray-700 mb-4">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-800">
                <th className="px-3 py-2 text-left text-gray-300 font-semibold border-b border-gray-700">Argument</th>
                <th className="px-3 py-2 text-left text-gray-300 font-semibold border-b border-gray-700">Description</th>
                <th className="px-3 py-2 text-left text-gray-300 font-semibold border-b border-gray-700">Example</th>
              </tr>
            </thead>
            <tbody>
              {[
                ['name','Unique arena identifier (no spaces)','skeleton_boss_room'],
                ['mob','MythicMobs internal mob name (case-sensitive)','SkeletonKing'],
                ['respawn','Seconds before mob respawns after dying','60'],
                ['despawn','Seconds before mob auto-despawns if not killed (0 = never)','600'],
                ['radius','Arena radius (informational)','15'],
              ].map(([a,b,c],i) => (
                <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                  <td className="px-3 py-2 text-emerald-300 border-b border-gray-800 font-mono text-xs">{a}</td>
                  <td className="px-3 py-2 text-gray-400 border-b border-gray-800 text-xs">{b}</td>
                  <td className="px-3 py-2 text-gray-300 border-b border-gray-800 font-mono text-xs">{c}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <CodeBlock language="bash" title="Example">{`/marena setspawn skeleton_boss_room SkeletonKing 60 600 15`}</CodeBlock>

        <h3>/marena delete</h3>
        <CodeBlock language="bash">{`/marena delete <name>`}</CodeBlock>
        <p className="text-sm text-gray-400">Removes the arena and kills any currently spawned mob for it.</p>

        <h3>/marena list</h3>
        <CodeBlock language="bash">{`/marena list`}</CodeBlock>
        <p className="text-sm text-gray-400">Shows all configured arenas with their details.</p>
      </CmdCard>

      <CmdCard cmd="/mquests" permission="mythicdrop.quests" defaultAccess="Everyone" desc="Opens the Quest GUI. Players can browse all quests, filter by In Progress or Completed, and click a quest to see their progress and rewards.">
      </CmdCard>

      <CmdCard cmd="/mmobs" permission="mythicdrop.mmobs" defaultAccess="OP" desc="Opens a GUI showing all MythicMobs configured on the server. Useful for admins to browse available mobs when setting up arenas or quests.">
      </CmdCard>

      <h2>Color Codes</h2>
      <p>Use <code>&</code> followed by a letter/number for colors in messages and lore:</p>
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 mb-6">
        {[['&a','#55FF55','Green'],['&b','#55FFFF','Aqua'],['&c','#FF5555','Red'],['&e','#FFFF55','Yellow'],['&f','#FFFFFF','White'],['&6','#FFAA00','Gold'],['&d','#FF55FF','Pink'],['&l','—','Bold']].map(([code, color, name]) => (
          <div key={code} className="bg-gray-800 rounded-lg px-3 py-2 flex items-center gap-2">
            <code className="text-emerald-300 font-mono text-sm">{code}</code>
            {color !== '—' && <div className="w-3 h-3 rounded-full flex-shrink-0" style={{backgroundColor: color}}></div>}
            <span className="text-gray-400 text-xs">{name}</span>
          </div>
        ))}
      </div>

      <DocNav prev={{ href: '/installation', label: 'Installation' }} next={{ href: '/configuration/drops', label: 'Drops (config.yml)' }} />
    </div>
  )
}
