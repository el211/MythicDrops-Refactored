import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'FAQ & Troubleshooting — MythicDrop Docs' }

function Q({ q, children }) {
  return (
    <div className="mb-6 border border-gray-700 rounded-xl overflow-hidden">
      <div className="bg-gray-800/60 px-5 py-3 flex items-start gap-2">
        <span className="text-emerald-400 font-bold text-sm flex-shrink-0">Q:</span>
        <span className="text-white font-medium text-sm">{q}</span>
      </div>
      <div className="px-5 py-4 text-sm text-gray-300 leading-6 space-y-2">{children}</div>
    </div>
  )
}

export default function FAQ() {
  return (
    <div className="doc-content">
      <h1>FAQ & Troubleshooting</h1>
      <p>Answers to common questions and problems. If you are stuck, this is the first place to look.</p>

      <h2>General Questions</h2>

      <Q q="Do I need LuckPerms?">
        <p>No. LuckPerms is <strong>optional</strong>. Without it, all players are treated as the <code>default</code> group. Just configure everything under <code>default</code> in your drop and quest configs.</p>
      </Q>

      <Q q="Do I need PlaceholderAPI?">
        <p>No. PlaceholderAPI is <strong>optional</strong>. Without it, all plugin features work normally — you just cannot use <code>%mythicdrop_...%</code> placeholders in scoreboards or GUIs.</p>
      </Q>

      <Q q="Do I need to restart the server to apply config changes?">
        <p>No! Run <code>/mythicdrop reload</code> after editing any config file. No restart needed.</p>
      </Q>

      <Q q="Where is player quest progress saved?">
        <p>In <code>plugins/MythicDropRefactored/quest_data.yml</code>. This is created automatically and updated as players progress. It persists across server restarts.</p>
      </Q>

      <h2>Drop / Reward Problems</h2>

      <Q q="Players aren't getting any drops when they kill a mob">
        <p>Check these things in order:</p>
        <ol>
          <li><strong>Mob name:</strong> The section name in <code>config.yml</code> must <strong>exactly</strong> match the MythicMobs internal mob name (case-sensitive). Example: <code>SkeletonKing</code> is not the same as <code>skeletonking</code></li>
          <li><strong>reward-processing:</strong> Make sure at least one of these is <code>true</code>:
            <CodeBlock language="yaml">{`reward-processing:
  most-damage: false
  last-hit: true    # At least one should be true`}</CodeBlock>
          </li>
          <li><strong>Top-damage system:</strong> If the mob is in <code>top3damage.yml</code> or similar, it will not use <code>config.yml</code> drops unless <code>use-standard-rewards: true</code> is set.</li>
          <li><strong>Enable debug mode:</strong> Set <code>activate-debug: true</code> in <code>debug.yml</code> and reload. Check your console for detailed output.</li>
        </ol>
      </Q>

      <Q q="VIP players are getting 'default' rewards instead of VIP rewards">
        <ol>
          <li>LuckPerms is installed and working (<code>/lp info</code> to verify)</li>
          <li>The player actually has the VIP group (<code>/lp user &lt;name&gt; info</code>)</li>
          <li>The group name in your config <strong>exactly</strong> matches the LuckPerms group name (case-sensitive)</li>
          <li>The player was online when the mob died (offline lookups fall back to &quot;default&quot;)</li>
        </ol>
      </Q>

      <Q q="Commands in rewards aren't working">
        <p>Test the command manually in your server console first (replace <code>%player%</code> with a real name). If it works manually but not from the plugin, check:</p>
        <ul>
          <li>The command does <strong>not</strong> start with <code>/</code></li>
          <li><code>%player%</code> is typed exactly as shown (not <code>%Player%</code> or <code>&#123;player&#125;</code>)</li>
          <li>The player is online when the reward runs</li>
        </ul>
      </Q>

      <h2>Quest Problems</h2>

      <Q q="Quest kills aren't being counted">
        <ol>
          <li><strong>Mob name:</strong> The <code>mob-name</code> in <code>quests.yml</code> must exactly match the MythicMobs mob name</li>
          <li><strong>Quest completed:</strong> Completed quests do not track further progress</li>
          <li><strong>Debug mode:</strong> Enable <code>activate-debug: true</code> in <code>debug.yml</code> to see detailed quest tracking logs</li>
        </ol>
      </Q>

      <Q q="The quest GUI shows items in wrong slots">
        <p>The <code>slot</code> field controls the position. Slots go from 0 (top-left) to 44 (bottom-right of row 5). Make sure no two quests share the same slot number, and that all slots are between 0 and 44.</p>
      </Q>

      <Q q="The quest GUI is empty or doesn't open">
        <ol>
          <li>Make sure <code>quests.yml</code> has at least one quest configured</li>
          <li>Run <code>/mythicdrop reload</code> after saving changes</li>
          <li>Check the console for YAML syntax errors (indentation mistakes are common)</li>
        </ol>
      </Q>

      <h2>Arena Problems</h2>

      <Q q="The boss isn't spawning when I create an arena">
        <ol>
          <li>The mob name in <code>/marena setspawn</code> must exactly match the MythicMobs name</li>
          <li>MythicMobs is installed and working (test with <code>/mm mobs spawn &lt;mobname&gt;</code>)</li>
          <li>Make sure you are standing in a valid location (not inside a block)</li>
        </ol>
      </Q>

      <Q q="The boss isn't respawning after it dies">
        <ol>
          <li>The <code>respawn</code> value is in <strong>seconds</strong> (e.g., <code>60</code> = 1 minute)</li>
          <li>Make sure <code>respawn</code> is greater than <code>0</code></li>
          <li>Check the console for errors related to ArenaManager</li>
        </ol>
      </Q>

      <h2>YAML Syntax Help</h2>
      <p>Most config problems are caused by incorrect YAML formatting. Here are the most common mistakes:</p>

      <h3>Use spaces, not tabs</h3>
      <CodeBlock language="yaml">{`# WRONG — TAB characters break YAML!
SkeletonKing:
	drops:         # ← TAB character

# CORRECT — 2 spaces per indent level
SkeletonKing:
  drops:`}</CodeBlock>

      <h3>Missing colon</h3>
      <CodeBlock language="yaml">{`# WRONG
mob-name "SkeletonKing"

# CORRECT
mob-name: "SkeletonKing"`}</CodeBlock>

      <h3>Wrong list format</h3>
      <CodeBlock language="yaml">{`# WRONG
rewardtop3:
  SkeletonKing

# CORRECT
rewardtop3:
  - SkeletonKing`}</CodeBlock>

      <h3>Special characters need quotes</h3>
      <p>If a value contains <code>:</code>, <code>#</code>, <code>[</code>, <code>]</code>, wrap it in quotes:</p>
      <CodeBlock language="yaml">{`# Without quotes, the [ breaks YAML parsing
message: "&a[VIP] You got a reward!"   # Quotes needed around the value`}</CodeBlock>

      <Callout type="tip">Use an online YAML validator (search &quot;YAML validator&quot;) to check your config files for syntax errors before reloading.</Callout>

      <DocNav prev={{ href: '/placeholders', label: 'PlaceholderAPI' }} />
    </div>
  )
}
