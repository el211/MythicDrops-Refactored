export default function TopBar() {
  return (
    <header className="sticky top-0 z-20 border-b border-gray-800 bg-[#0f1117]/90 backdrop-blur px-8 py-3 flex items-center justify-between">
      <div className="text-sm text-gray-500">
        Minecraft Plugin Documentation
      </div>
      <div className="flex items-center gap-3">
        <span className="text-xs bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 px-2.5 py-0.5 rounded-full font-medium">
          MythicMobs Required
        </span>
        <span className="text-xs bg-blue-500/20 text-blue-400 border border-blue-500/30 px-2.5 py-0.5 rounded-full font-medium">
          API 1.21+
        </span>
      </div>
    </header>
  )
}
