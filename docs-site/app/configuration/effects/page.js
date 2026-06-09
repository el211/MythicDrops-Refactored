import CodeBlock from '@/components/CodeBlock'
import Callout from '@/components/Callout'
import DocNav from '@/components/DocNav'

export const metadata = { title: 'Death Effects — MythicDrop Docs' }

export default function Effects() {
  return (
    <div className="doc-content">
      <h1>Death Effects</h1>
      <p>File: <code>plugins/MythicDropRefactored/effects.yml</code></p>
      <p>Play <strong>fireworks</strong> and <strong>sounds</strong> at the location where a MythicMob dies. Makes boss kills feel epic and rewarding.</p>

      <h2>Basic Structure</h2>
      <CodeBlock language="yaml" title="effects.yml">{`SkeletonKing:
  effects:
    firework_display:
      type: FIREWORK
      power: 2
      color: BLUE
      flicker: true
      trail: true

    death_roar:
      type: SOUND
      sound: ENTITY_ENDER_DRAGON_DEATH
      volume: 1.0
      pitch: 0.7`}</CodeBlock>
      <p>Each mob can have <strong>multiple effects</strong>. Give each one a unique name.</p>

      <h2>FIREWORK Effect</h2>
      <CodeBlock language="yaml">{`firework_display:
  type: FIREWORK
  power: 2        # Flight height (1 = low, 2 = medium, 3 = high)
  color: BLUE     # Burst color
  flicker: true   # Sparkle effect
  trail: true     # Trail behind the rocket`}</CodeBlock>

      <h3>Available Colors</h3>
      <div className="grid grid-cols-3 sm:grid-cols-5 gap-2 mb-6">
        {[
          ['WHITE','#FFFFFF'],['SILVER','#C0C0C0'],['GRAY','#808080'],['RED','#FF0000'],['MAROON','#800000'],
          ['YELLOW','#FFFF00'],['LIME','#00FF00'],['GREEN','#008000'],['AQUA','#00FFFF'],['TEAL','#008080'],
          ['BLUE','#0000FF'],['NAVY','#000080'],['FUCHSIA','#FF00FF'],['PURPLE','#800080'],['ORANGE','#FFA500'],
        ].map(([name,hex]) => (
          <div key={name} className="rounded-lg border border-gray-700 overflow-hidden">
            <div className="h-8" style={{backgroundColor: hex}}></div>
            <div className="bg-gray-800 px-2 py-1 text-center text-xs text-gray-400 font-mono">{name}</div>
          </div>
        ))}
      </div>

      <h2>SOUND Effect</h2>
      <CodeBlock language="yaml">{`death_sound:
  type: SOUND
  sound: ENTITY_ENDER_DRAGON_DEATH   # Minecraft sound name
  volume: 1.0                        # 0.0–2.0+ (higher = louder + more range)
  pitch: 1.0                         # 0.5 = lower tone, 2.0 = higher tone`}</CodeBlock>

      <h3>Common Sound Names</h3>
      <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800">
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Sound</th>
              <th className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">Description</th>
            </tr>
          </thead>
          <tbody>
            {[
              ['ENTITY_ENDER_DRAGON_DEATH','Dragon death roar'],
              ['ENTITY_WITHER_DEATH','Wither explosion death'],
              ['ENTITY_LIGHTNING_BOLT_THUNDER','Thunder crack'],
              ['ENTITY_GENERIC_EXPLODE','Explosion sound'],
              ['UI_TOAST_CHALLENGE_COMPLETE','Achievement fanfare'],
              ['ENTITY_PLAYER_LEVELUP','Level-up chime'],
              ['BLOCK_BEACON_POWER_SELECT','Beacon activation'],
              ['ENTITY_ENDER_DRAGON_GROWL','Dragon growl'],
            ].map(([s,d],i) => (
              <tr key={i} className={i%2===0?'bg-gray-900/30':''}>
                <td className="px-4 py-2.5 text-emerald-300 border-b border-gray-800 font-mono text-xs">{s}</td>
                <td className="px-4 py-2.5 text-gray-400 border-b border-gray-800 text-xs">{d}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <h2>Full Example</h2>
      <CodeBlock language="yaml" title="effects.yml">{`# Skeleton King — blue fireworks + wither death sound
SkeletonKing:
  effects:
    blue_firework:
      type: FIREWORK
      power: 2
      color: BLUE
      flicker: true
      trail: true
    white_firework:
      type: FIREWORK
      power: 3
      color: WHITE
      flicker: false
      trail: true
    death_sound:
      type: SOUND
      sound: ENTITY_WITHER_DEATH
      volume: 1.0
      pitch: 1.0

# Ancient Dragon — red/purple fireworks + dragon death
AncientDragon:
  effects:
    red_firework:
      type: FIREWORK
      power: 3
      color: RED
      flicker: true
      trail: true
    purple_firework:
      type: FIREWORK
      power: 2
      color: PURPLE
      flicker: true
      trail: false
    dragon_death:
      type: SOUND
      sound: ENTITY_ENDER_DRAGON_DEATH
      volume: 2.0
      pitch: 0.7
    victory:
      type: SOUND
      sound: UI_TOAST_CHALLENGE_COMPLETE
      volume: 1.0
      pitch: 1.0`}</CodeBlock>

      <Callout type="tip">Combine a low-pitched dragon death sound (<code>pitch: 0.7</code>) with colorful fireworks for epic boss kill moments. Set <code>volume</code> higher than <code>1.0</code> to broadcast the sound over a wider area.</Callout>

      <DocNav prev={{ href: '/configuration/arenas', label: 'Arena System' }} next={{ href: '/configuration/announcements', label: 'Announcements' }} />
    </div>
  )
}
