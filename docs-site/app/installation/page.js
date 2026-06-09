import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'Installation — MythicDrop Docs' }

export default function Installation() {
  return (
    <div className="doc-content">
      <h1>Installation Guide</h1>
      <p>This page walks you through installing MythicDrop step by step. No experience needed!</p>

      <h2>Step 1 — Install Required Plugins</h2>
      <p>MythicDrop <strong>requires</strong> MythicMobs to be installed first. Without it, MythicDrop will not load.</p>
      <ol>
        <li>Download <strong>MythicMobs</strong> and place its <code>.jar</code> in your <code>plugins/</code> folder</li>
        <li>(Optional) Download <strong>LuckPerms</strong> if you want different rewards per player rank (VIP, Legend, etc.)</li>
        <li>(Optional) Download <strong>PlaceholderAPI</strong> if you want scoreboards or GUIs showing quest/arena data</li>
        <li>Start your server once to let these plugins generate their files, then stop it</li>
      </ol>

      <h2>Step 2 — Install MythicDrop</h2>
      <ol>
        <li>Place the <code>MythicDropRefactored.jar</code> file into your <code>plugins/</code> folder</li>
        <li>Start your server</li>
      </ol>
      <p>On first launch, the plugin creates these files automatically:</p>
      <CodeBlock language="text">{`plugins/MythicDropRefactored/
  config.yml
  quests.yml
  top3damage.yml
  top5damage.yml
  announcement.yml
  effects.yml
  debug.yml`}</CodeBlock>

      <h2>Step 3 — Verify Installation</h2>
      <p>In your server console or in-game (as OP), run:</p>
      <CodeBlock language="bash">{`/mythicdrop reload`}</CodeBlock>
      <p>If you see a success message, the plugin is working correctly.</p>

      <h2>Step 4 — Configure Your First Drop</h2>
      <p>Open <code>plugins/MythicDropRefactored/config.yml</code> and add a reward for one of your MythicMobs.</p>
      <p>See the <a href="/configuration/drops">Drops Configuration Guide</a> for full instructions.</p>

      <h2>Updating the Plugin</h2>
      <ol>
        <li>Stop your server</li>
        <li>Delete the old <code>.jar</code> from your <code>plugins/</code> folder</li>
        <li>Place the new <code>.jar</code> in <code>plugins/</code></li>
        <li>Start your server</li>
      </ol>
      <Callout type="tip">Your configuration files are <strong>not</strong> deleted when updating. Only the <code>.jar</code> is replaced.</Callout>

      <h2>Uninstalling</h2>
      <ol>
        <li>Stop your server</li>
        <li>Remove <code>MythicDropRefactored.jar</code> from <code>plugins/</code></li>
        <li>(Optional) Delete the <code>plugins/MythicDropRefactored/</code> folder to remove all data</li>
      </ol>

      <h2>Troubleshooting Installation</h2>
      <p><strong>The plugin does not appear in <code>/plugins</code>:</strong></p>
      <ul>
        <li>Make sure you are running Spigot or Paper 1.21+</li>
        <li>Check that MythicMobs is installed and loaded first</li>
        <li>Look for errors in the console when the server starts</li>
      </ul>
      <p><strong>Enable debug mode</strong> to get more info in your console. Open <code>debug.yml</code> and set:</p>
      <CodeBlock language="yaml" title="debug.yml">{`activate-debug: true`}</CodeBlock>
      <p>Then run <code>/mythicdrop reload</code>. Detailed logs will appear in your console.</p>

      <DocNav prev={{ href: '/', label: 'Overview' }} next={{ href: '/commands', label: 'Commands & Permissions' }} />
    </div>
  )
}
