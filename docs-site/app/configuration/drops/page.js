import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'Drops Configuration — MythicDrop Docs' }

export default function Drops() {
  return (
    <div className="doc-content">
      <h1>Drops Configuration</h1>
      <p>File: <code>plugins/MythicDropRefactored/config.yml</code></p>
      <p>This is the main configuration file. It controls what happens when a player kills a MythicMob — what commands run, what items they get, and which player is eligible.</p>

      <h2>Basic Structure</h2>
      <CodeBlock language="yaml" title="config.yml">{`reward-processing:
  most-damage: false
  last-hit: true

SkeletonKing:
  flexible-reward-mode: false
  drops:
    default:
      drop1:
        command: "give %player% diamond 1"
        chance: 1.0
        message: "&b&lYou got a diamond!"`}</CodeBlock>

      <h2>reward-processing — Who Gets the Reward?</h2>
      <p>This section controls <strong>which player</strong> receives the drop when the mob dies.</p>
      <CodeBlock language="yaml">{`reward-processing:
  most-damage: false   # true = player who dealt the most total damage
  last-hit: true       # true = player who landed the killing blow`}</CodeBlock>

      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Setting</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Value</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Effect</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['most-damage','true','Highest total damage dealer gets the reward'],
              ['most-damage','false','Ignore total damage'],
              ['last-hit','true','Killing blow player gets the reward'],
              ['last-hit','false','Ignore the killing blow'],
            ].map(([a,b,c],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 font-mono text-xs">{a}</td>
                <td className="px-4 py-2.5 text-emerald-400 border-b border-gray-800 font-mono text-xs">{b}</td>
                <td className="px-4 py-2.5 text-gray-400 border-b border-gray-800 text-xs">{c}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <Callout type="tip">For solo mobs, use <code>last-hit: true</code>. For boss fights with multiple players, use the <a href="/configuration/top-rewards">Top Damage system</a> instead.</Callout>

      <h2>Mob Drop Sections</h2>
      <p>Each MythicMob gets its own section, using its <strong>exact internal MythicMobs name</strong> as the key (case-sensitive).</p>
      <CodeBlock language="yaml">{`SkeletonKing:         # Must match the mob name in MythicMobs exactly
  flexible-reward-mode: false
  drops:
    default:          # Player group from LuckPerms
      drop1:          # Any unique name for this reward
        command: "give %player% diamond 1"
        chance: 1.0
        message: "&b&lYou got a diamond!"`}</CodeBlock>

      <h2>flexible-reward-mode</h2>
      <div className="overflow-x-auto mb-4 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Value</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Behavior</th>
            </tr>
          </thead>
          <tbody>
            <tr className="bg-gray-900/30">
              <td className="px-4 py-2.5 text-emerald-400 border-b border-gray-800 font-mono text-xs">false</td>
              <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 text-xs">Only the first drop that passes its chance is given. Stops there.</td>
            </tr>
            <tr>
              <td className="px-4 py-2.5 text-emerald-400 border-b border-gray-800 font-mono text-xs">true</td>
              <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 text-xs">Every drop rolls its own chance independently. Player can get multiple drops.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <h2>Drop Entry Fields</h2>
      <CodeBlock language="yaml">{`drop1:                              # Unique name (can be anything)
  command: "give %player% stone 1"  # Console command (%player% = player name)
  chance: 0.5                       # 0.0 = never, 1.0 = always
  message: "&aYou got stone!"       # Optional chat message`}</CodeBlock>

      <h3>chance values</h3>
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-6">
        {[['1.0','100%','Always'],['0.5','50%','Half the time'],['0.25','25%','1-in-4'],['0.0','0%','Never']].map(([v,p,l]) => (
          <div key={v} className="bg-gray-800/60 border border-gray-700 rounded-lg p-3 text-center">
            <div className="text-emerald-400 font-mono font-bold">{v}</div>
            <div className="text-white font-semibold text-sm">{p}</div>
            <div className="text-gray-500 text-xs">{l}</div>
          </div>
        ))}
      </div>

      <h2>LuckPerms Groups</h2>
      <p>Inside <code>drops</code>, you define <strong>player groups</strong>. Different groups get different rewards.</p>
      <CodeBlock language="yaml">{`drops:
  default:     # Players in the "default" LuckPerms group (everyone without a rank)
    drop1:
      command: "give %player% diamond 1"
      chance: 1.0
      message: "&aYou got a diamond!"

  vip:         # Players in the "vip" LuckPerms group
    drop1:
      command: "give %player% diamond 3"
      chance: 1.0
      message: "&b[VIP] You got 3 diamonds!"`}</CodeBlock>
      <Callout type="note">If you do not use LuckPerms, just use <code>default</code> as your only group. All players will use it.</Callout>

      <h2>Full Working Example</h2>
      <CodeBlock language="yaml" title="config.yml">{`reward-processing:
  most-damage: false
  last-hit: true

# Skeleton King Boss
SkeletonKing:
  flexible-reward-mode: true    # All drops roll independently
  drops:
    default:                    # Non-VIP players
      gold_reward:
        command: "give %player% gold_ingot 5"
        chance: 1.0
        message: "&eYou got 5 gold ingots!"
      diamond_bonus:
        command: "give %player% diamond 1"
        chance: 0.25            # 25% chance to also get a diamond
        message: "&bBonus! You got a diamond!"
      money_reward:
        command: "eco give %player% 200"
        chance: 1.0
        message: "&a+$200"

    vip:                        # VIP players get better rewards
      gold_reward:
        command: "give %player% gold_ingot 10"
        chance: 1.0
        message: "&eVIP: You got 10 gold ingots!"
      diamond_bonus:
        command: "give %player% diamond 3"
        chance: 0.5
        message: "&bVIP Bonus! You got 3 diamonds!"
      money_reward:
        command: "eco give %player% 500"
        chance: 1.0
        message: "&a+$500"

# Cave Spider
CaveSpider:
  flexible-reward-mode: false   # Stop after first successful drop
  drops:
    default:
      common:
        command: "give %player% string 4"
        chance: 0.8
        message: "&7You got some string."
      rare:
        command: "give %player% spider_eye 1"
        chance: 0.2
        message: "&cRare drop! Spider Eye!"`}</CodeBlock>

      <Callout type="warning">After editing <code>config.yml</code>, always run <code>/mythicdrop reload</code> to apply changes!</Callout>

      <h2>Common Mistakes</h2>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Problem</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Solution</th>
            </tr>
          </thead>
          <tbody>
            {[
              ["Mob name doesn't match","Copy the exact name from MythicMobs config — it's case-sensitive"],
              ['Players not getting drops','Check reward-processing settings; make sure mob name matches'],
              ['Wrong group rewards','Check LuckPerms group names — they must match exactly'],
              ['Command not working','Test the command in console first, replacing %player% with a real name'],
            ].map(([a,b],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 text-xs">{a}</td>
                <td className="px-4 py-2.5 text-gray-400 border-b border-gray-800 text-xs">{b}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <DocNav prev={{ href: '/commands', label: 'Commands & Permissions' }} next={{ href: '/configuration/quests', label: 'Quest System' }} />
    </div>
  )
}
