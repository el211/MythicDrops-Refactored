import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'Quest System — MythicDrop Docs' }

export default function Quests() {
  return (
    <div className="doc-content">
      <h1>Quest System</h1>
      <p>File: <code>plugins/MythicDropRefactored/quests.yml</code></p>
      <p>The quest system lets players track kill goals and earn rewards on completion. Players open <code>/mquests</code> to see a GUI with all available quests.</p>

      <h2>How Quests Work</h2>
      <div className="flex flex-col gap-3 mb-8">
        {[
          ['1','Define a quest','Set target mob, required kills, and rewards in quests.yml'],
          ['2','Player opens /mquests','They see a visual GUI with quest icons and their progress'],
          ['3','Player kills the mob','Progress counter goes up automatically'],
          ['4','Quest completes','Rewards run, completion message is sent, quest is marked done'],
          ['5','Progress saved','Even after relogging or server restart, progress is kept'],
        ].map(([n,t,d]) => (
          <div key={n} className="flex items-start gap-4 bg-gray-800/40 border border-gray-700 rounded-xl p-4">
            <div className="w-7 h-7 bg-emerald-500/20 text-emerald-400 rounded-full flex items-center justify-center text-xs font-bold flex-shrink-0">{n}</div>
            <div>
              <div className="font-medium text-white text-sm">{t}</div>
              <div className="text-gray-400 text-xs mt-0.5">{d}</div>
            </div>
          </div>
        ))}
      </div>

      <h2>Basic Quest Structure</h2>
      <CodeBlock language="yaml" title="quests.yml">{`quests:
  my_first_quest:               # Unique quest ID (no spaces, use _ instead)
    mob-name: "SkeletonKing"    # MythicMobs internal mob name (exact, case-sensitive)
    message: "&aQuest complete! You slayed the Skeleton King!"

    quest-item:
      material: SKELETON_SKULL  # Item shown in the GUI
      ModelData: 0              # Custom model data (0 = default)
      displayname: "&fSkeleton King Slayer"
      lore:
        - "&7Defeat the Skeleton King."
        - "&7Required kills: &f5"

    conditions:
      kill: 5                   # Number of kills required
      slot: 10                  # Position in the GUI grid (0-44)

    reward:
      drops:
        default:
          guaranteed-rewards: 1
          reward1:
            command: "give %player% diamond 5"
            chance: 1.0
            message: "&b+5 Diamonds!"`}</CodeBlock>

      <h2>Quest Fields</h2>
      <h3>mob-name</h3>
      <p>The internal MythicMobs name of the mob to track. Must match <strong>exactly</strong> (case-sensitive).</p>
      <h3>message</h3>
      <p>The chat message sent to the player when they complete the quest. Supports <code>&</code> color codes.</p>

      <h3>quest-item — GUI Display</h3>
      <CodeBlock language="yaml">{`quest-item:
  material: SPIDER_EYE       # Any Minecraft material name (uppercase)
  ModelData: 0               # Custom model data for resource packs. Use 0 if unsure.
  displayname: "&2Cave Spider Slayer"
  lore:
    - "&7Defeat cave spiders."
    - "&7Required kills: &f10"`}</CodeBlock>

      <h3>conditions — Slot Grid</h3>
      <p>The <code>slot</code> controls where the quest appears in the GUI. Slots go from 0 (top-left) to 44 (bottom-right). The bottom row is reserved for filter buttons.</p>
      <div className="my-4 rounded-xl border border-gray-700 overflow-hidden">
        <div className="bg-gray-800 px-4 py-2 text-xs text-gray-400 font-mono">GUI Slot Layout (5 usable rows)</div>
        <div className="p-4 bg-gray-900/50">
          {[0,1,2,3,4].map(row => (
            <div key={row} className="flex gap-1 mb-1">
              {[0,1,2,3,4,5,6,7,8].map(col => {
                const slot = row * 9 + col
                return (
                  <div key={col} className="w-9 h-9 bg-gray-700 border border-gray-600 rounded flex items-center justify-center text-xs text-gray-400 font-mono">
                    {slot}
                  </div>
                )
              })}
            </div>
          ))}
          <div className="flex gap-1 mt-2">
            <div className="flex-1 h-9 bg-emerald-900/40 border border-emerald-700/40 rounded flex items-center justify-center text-xs text-emerald-500">All Quests</div>
            <div className="flex-1 h-9 bg-blue-900/40 border border-blue-700/40 rounded flex items-center justify-center text-xs text-blue-500">In Progress</div>
            <div className="flex-1 h-9 bg-purple-900/40 border border-purple-700/40 rounded flex items-center justify-center text-xs text-purple-500">Completed</div>
          </div>
          <div className="text-xs text-gray-600 mt-2">↑ Bottom row is reserved — do not use slots 45-53</div>
        </div>
      </div>

      <h3>guaranteed-rewards</h3>
      <p>The first N rewards always run, regardless of their <code>chance</code> value. Rewards after that position roll their individual chance.</p>
      <CodeBlock language="yaml">{`default:
  guaranteed-rewards: 1  # reward1 always runs; reward2 rolls chance
  reward1:
    command: "give %player% diamond 5"
    chance: 1.0          # This chance is ignored (it's guaranteed)
    message: "+5 Diamonds"
  reward2:
    command: "give %player% emerald 3"
    chance: 0.5          # 50% chance — only rolled since it's outside the guaranteed count`}</CodeBlock>

      <h2>Full Example with Multiple Quests</h2>
      <CodeBlock language="yaml" title="quests.yml">{`quests:

  cave_spider_slayer:
    mob-name: "CaveSpider"
    message: "&aQuest Complete! You defeated 10 cave spiders!"

    quest-item:
      material: SPIDER_EYE
      ModelData: 0
      displayname: "&cCave Spider Slayer"
      lore:
        - "&7Defeat cave spiders in the mines."
        - "&7Required: &f10 kills"

    conditions:
      kill: 10
      slot: 10

    reward:
      drops:
        default:
          guaranteed-rewards: 1
          base_reward:
            command: "give %player% string 8"
            chance: 1.0
            message: "&7+8 String"
          bonus_reward:
            command: "give %player% spider_eye 2"
            chance: 0.4
            message: "&cBonus! +2 Spider Eyes"
        vip:
          guaranteed-rewards: 2
          base_reward:
            command: "give %player% string 16"
            chance: 1.0
            message: "&7[VIP] +16 String"
          bonus_reward:
            command: "give %player% spider_eye 5"
            chance: 1.0
            message: "&c[VIP] +5 Spider Eyes"

  skeleton_king_slayer:
    mob-name: "SkeletonKing"
    message: "&6Quest Complete! You are a true Skeleton King Slayer!"

    quest-item:
      material: SKELETON_SKULL
      ModelData: 0
      displayname: "&fSkeleton King Slayer"
      lore:
        - "&7Defeat the mighty Skeleton King."
        - "&7Required: &f5 kills"

    conditions:
      kill: 5
      slot: 11

    reward:
      drops:
        default:
          guaranteed-rewards: 1
          money:
            command: "eco give %player% 1000"
            chance: 1.0
            message: "&a+$1,000!"
          diamond_reward:
            command: "give %player% diamond 10"
            chance: 0.5
            message: "&b50% bonus: +10 Diamonds!"
        vip:
          guaranteed-rewards: 2
          money:
            command: "eco give %player% 2500"
            chance: 1.0
            message: "&a[VIP] +$2,500!"
          diamond_reward:
            command: "give %player% diamond 25"
            chance: 1.0
            message: "&b[VIP] +25 Diamonds!"`}</CodeBlock>

      <Callout type="warning">Run <code>/mythicdrop reload</code> after editing <code>quests.yml</code>.</Callout>

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
              ['Quest not tracking kills','Check mob-name exactly matches the MythicMobs internal name'],
              ['Quest item not showing','Check slot is between 0 and 44; no two quests share the same slot'],
              ['Quest GUI is empty','Check for YAML syntax errors in the console after /mythicdrop reload'],
              ["Progress lost after restart","Shouldn't happen — check if quest_data.yml is being written correctly"],
            ].map(([a,b],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 text-xs">{a}</td>
                <td className="px-4 py-2.5 text-gray-400 border-b border-gray-800 text-xs">{b}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <DocNav prev={{ href: '/configuration/drops', label: 'Drops (config.yml)' }} next={{ href: '/configuration/top-rewards', label: 'Top Damage Rewards' }} />
    </div>
  )
}
