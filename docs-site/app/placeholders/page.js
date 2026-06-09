import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'PlaceholderAPI — MythicDrop Docs' }

function PlaceholderRow({ placeholder, desc, example }) {
  return (
    <tr>
      <td className="px-4 py-2.5 text-emerald-300 border-b border-gray-800 font-mono text-xs align-top">{placeholder}</td>
      <td className="px-4 py-2.5 text-gray-400 border-b border-gray-800 text-xs align-top">{desc}</td>
      <td className="px-4 py-2.5 text-gray-300 border-b border-gray-800 font-mono text-xs align-top">{example}</td>
    </tr>
  )
}

export default function Placeholders() {
  return (
    <div className="doc-content">
      <h1>PlaceholderAPI Placeholders</h1>
      <p>If you have <strong>PlaceholderAPI</strong> installed, MythicDrop provides these placeholders for scoreboards, GUIs, holograms, and chat formats.</p>

      <Callout type="note">After installing PlaceholderAPI, run <code>/mythicdrop reload</code> once to register MythicDrop&#39;s placeholders.</Callout>

      <h2>Quest Placeholders (Per-Player)</h2>
      <p>Replace <code>[id]</code> with the quest ID from <code>quests.yml</code> (e.g., <code>cave_spider_slayer</code>).</p>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Placeholder</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Description</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Example</th>
            </tr>
          </thead>
          <tbody>
            <PlaceholderRow placeholder="%mythicdrop_quest_[id]_progress%" desc="Current kill count" example="7" />
            <PlaceholderRow placeholder="%mythicdrop_quest_[id]_required%" desc="Total kills needed" example="10" />
            <PlaceholderRow placeholder="%mythicdrop_quest_[id]_remaining%" desc="Kills still needed (0 when done)" example="3" />
            <PlaceholderRow placeholder="%mythicdrop_quest_[id]_percent%" desc="Completion percentage (0-100)" example="70" />
            <PlaceholderRow placeholder="%mythicdrop_quest_[id]_completed%" desc="Whether quest is done" example="true / false" />
            <PlaceholderRow placeholder="%mythicdrop_quest_[id]_name%" desc="Quest display name (colors stripped)" example="Cave Spider Slayer" />
            <PlaceholderRow placeholder="%mythicdrop_quest_[id]_mob%" desc="Target mob name" example="CaveSpider" />
          </tbody>
        </table>
      </div>

      <h2>Quest Summary Placeholders (Per-Player)</h2>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Placeholder</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Description</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Example</th>
            </tr>
          </thead>
          <tbody>
            <PlaceholderRow placeholder="%mythicdrop_quests_total%" desc="Total quests defined in config" example="5" />
            <PlaceholderRow placeholder="%mythicdrop_quests_completed%" desc="Quests this player completed" example="2" />
            <PlaceholderRow placeholder="%mythicdrop_quests_remaining%" desc="Quests not yet completed" example="3" />
            <PlaceholderRow placeholder="%mythicdrop_quests_percent%" desc="Overall completion % (0-100)" example="40" />
          </tbody>
        </table>
      </div>

      <h2>Arena Placeholders</h2>
      <p>Replace <code>[name]</code> with your arena name.</p>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Placeholder</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Description</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Example</th>
            </tr>
          </thead>
          <tbody>
            <PlaceholderRow placeholder="%mythicdrop_arena_count%" desc="Total configured arenas" example="3" />
            <PlaceholderRow placeholder="%mythicdrop_arena_live_count%" desc="Arenas with a live boss right now" example="2" />
            <PlaceholderRow placeholder="%mythicdrop_arena_[name]_exists%" desc="Does this arena exist?" example="true" />
            <PlaceholderRow placeholder="%mythicdrop_arena_[name]_mob%" desc="Mob type for this arena" example="SkeletonKing" />
            <PlaceholderRow placeholder="%mythicdrop_arena_[name]_world%" desc="World name" example="world" />
            <PlaceholderRow placeholder="%mythicdrop_arena_[name]_alive%" desc="Is the boss currently alive?" example="true" />
          </tbody>
        </table>
      </div>

      <h2>Live Damage Placeholders</h2>
      <p>These update in real-time as players fight. Perfect for boss fight scoreboards.</p>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Placeholder</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Description</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Example</th>
            </tr>
          </thead>
          <tbody>
            <PlaceholderRow placeholder="%mythicdrop_live_damage%" desc="This player's total damage to all active mobs" example="1250" />
            <PlaceholderRow placeholder="%mythicdrop_live_rank%" desc="This player's current damage rank" example="1" />
            <PlaceholderRow placeholder="%mythicdrop_live_top1_name%" desc="Name of #1 damage dealer" example="Steve" />
            <PlaceholderRow placeholder="%mythicdrop_live_top1_damage%" desc="Damage dealt by #1" example="1250" />
            <PlaceholderRow placeholder="%mythicdrop_live_top2_name%" desc="Name of #2 damage dealer" example="Alex" />
            <PlaceholderRow placeholder="%mythicdrop_live_top2_damage%" desc="Damage dealt by #2" example="980" />
          </tbody>
        </table>
      </div>

      <h2>LuckPerms Group</h2>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Placeholder</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Description</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Example</th>
            </tr>
          </thead>
          <tbody>
            <PlaceholderRow placeholder="%mythicdrop_group%" desc="Player's primary LuckPerms group (returns 'offline' if not logged in)" example="vip" />
          </tbody>
        </table>
      </div>

      <h2>Example Scoreboard Setup</h2>
      <CodeBlock language="yaml" title="Scoreboard during a boss fight">{`lines:
  - "&6&l--- BOSS FIGHT ---"
  - "&fYour damage: &c%mythicdrop_live_damage%"
  - "&fYour rank: &e#%mythicdrop_live_rank%"
  - ""
  - "&e#1 &f%mythicdrop_live_top1_name%: &c%mythicdrop_live_top1_damage%"
  - "&e#2 &f%mythicdrop_live_top2_name%: &c%mythicdrop_live_top2_damage%"
  - "&e#3 &f%mythicdrop_live_top3_name%: &c%mythicdrop_live_top3_damage%"`}</CodeBlock>

      <CodeBlock language="yaml" title="Scoreboard for quest progress">{`lines:
  - "&6Quest Progress"
  - ""
  - "&fSpider Slayer: &a%mythicdrop_quest_cave_spider_slayer_progress%&7/&f%mythicdrop_quest_cave_spider_slayer_required%"
  - "&fSkeleton King: &a%mythicdrop_quest_skeleton_king_slayer_progress%&7/&f%mythicdrop_quest_skeleton_king_slayer_required%"
  - ""
  - "&fTotal: &a%mythicdrop_quests_completed%&f/&a%mythicdrop_quests_total% &7complete"`}</CodeBlock>

      <DocNav prev={{ href: '/configuration/announcements', label: 'Announcements' }} next={{ href: '/faq', label: 'FAQ & Troubleshooting' }} />
    </div>
  )
}
