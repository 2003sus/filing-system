import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'                     // ★ 加

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')   // ★ 加：@ = src
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})