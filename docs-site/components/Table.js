export default function Table({ headers, rows }) {
  return (
    <div className="overflow-x-auto mb-6 rounded-lg border border-gray-700">
      <table className="w-full text-sm">
        <thead>
          <tr className="bg-gray-800">
            {headers.map((h, i) => (
              <th key={i} className="px-4 py-2.5 text-left text-gray-200 font-semibold border-b border-gray-700">
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, ri) => (
            <tr key={ri} className={ri % 2 === 0 ? 'bg-gray-900/30' : 'bg-transparent'}>
              {row.map((cell, ci) => (
                <td key={ci} className="px-4 py-2.5 text-gray-300 border-b border-gray-800 align-top">
                  {cell}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
