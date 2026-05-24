import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { fileURLToPath } from 'node:url'

const projectRoot = fileURLToPath(new URL('.', import.meta.url))

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, projectRoot, '')
  const devServerPort = env.VITE_DEV_SERVER_PORT
    ? Number(env.VITE_DEV_SERVER_PORT)
    : undefined

  return {
    plugins: [react(), tailwindcss()],
    server: {
      port: devServerPort,
    },
  }
})
