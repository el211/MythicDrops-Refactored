'use client'
import { useState } from 'react'
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter'
import { vscDarkPlus } from 'react-syntax-highlighter/dist/esm/styles/prism'

export default function CodeBlock({ children, language = 'yaml', title }) {
  const [copied, setCopied] = useState(false)

  const code = typeof children === 'string' ? children.trim() : ''

  const copy = () => {
    navigator.clipboard.writeText(code)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return (
    <div className="mb-6 rounded-xl overflow-hidden border border-gray-700 bg-[#1e1e2e]">
      <div className="flex items-center justify-between px-4 py-2 bg-gray-800/80 border-b border-gray-700">
        <div className="flex items-center gap-2">
          <div className="flex gap-1.5">
            <div className="w-3 h-3 rounded-full bg-red-500/60"></div>
            <div className="w-3 h-3 rounded-full bg-yellow-500/60"></div>
            <div className="w-3 h-3 rounded-full bg-green-500/60"></div>
          </div>
          {title && <span className="text-xs text-gray-400 ml-2 font-mono">{title}</span>}
          {!title && <span className="text-xs text-gray-500 ml-2 uppercase font-mono">{language}</span>}
        </div>
        <button
          onClick={copy}
          className="text-xs text-gray-400 hover:text-white transition-colors px-2 py-1 rounded hover:bg-gray-700"
        >
          {copied ? '✓ Copied!' : 'Copy'}
        </button>
      </div>
      <SyntaxHighlighter
        language={language}
        style={vscDarkPlus}
        customStyle={{
          margin: 0,
          padding: '1.25rem',
          background: 'transparent',
          fontSize: '0.85rem',
          lineHeight: '1.6',
        }}
        showLineNumbers={code.split('\n').length > 6}
        lineNumberStyle={{ color: '#4a5568', fontSize: '0.75rem' }}
      >
        {code}
      </SyntaxHighlighter>
    </div>
  )
}
