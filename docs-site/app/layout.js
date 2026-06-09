import './globals.css'
import { Inter } from 'next/font/google'
import Sidebar from '@/components/Sidebar'
import TopBar from '@/components/TopBar'

const inter = Inter({ subsets: ['latin'] })

export const metadata = {
  title: 'MythicDrop Documentation',
  description: 'Complete beginner-friendly documentation for the MythicDrop Minecraft plugin',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <div className="flex min-h-screen">
          <Sidebar />
          <div className="flex-1 flex flex-col min-w-0 ml-[280px]">
            <TopBar />
            <main className="flex-1 px-8 py-10 max-w-4xl mx-auto w-full">
              {children}
            </main>
            <footer className="border-t border-gray-800 px-8 py-4 text-center text-gray-600 text-sm">
              MythicDrop Documentation — Built for Minecraft server admins
            </footer>
          </div>
        </div>
      </body>
    </html>
  )
}
