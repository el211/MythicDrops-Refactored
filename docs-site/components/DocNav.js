import Link from 'next/link'

export default function DocNav({ prev, next }) {
  return (
    <div className="flex items-center justify-between mt-12 pt-6 border-t border-gray-800">
      <div>
        {prev && (
          <Link href={prev.href} className="group flex items-center gap-2 text-gray-400 hover:text-emerald-400 transition-colors no-underline">
            <span className="text-lg">←</span>
            <div>
              <div className="text-xs text-gray-600 group-hover:text-gray-500">Previous</div>
              <div className="text-sm font-medium">{prev.label}</div>
            </div>
          </Link>
        )}
      </div>
      <div className="text-right">
        {next && (
          <Link href={next.href} className="group flex items-center gap-2 text-gray-400 hover:text-emerald-400 transition-colors no-underline">
            <div>
              <div className="text-xs text-gray-600 group-hover:text-gray-500">Next</div>
              <div className="text-sm font-medium">{next.label}</div>
            </div>
            <span className="text-lg">→</span>
          </Link>
        )}
      </div>
    </div>
  )
}
