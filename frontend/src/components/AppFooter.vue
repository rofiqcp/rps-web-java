<script setup lang="ts">
import { HeartIcon } from '@heroicons/vue/24/solid'
</script>

<template>
  <footer class="bg-gradient-to-r from-slate-800 to-slate-900 text-slate-400 py-6 mt-auto">
    <div class="container mx-auto px-4 max-w-7xl">
      <div class="flex flex-col md:flex-row items-center justify-between gap-4">
        <div class="flex items-center gap-2 text-sm">
          <span>© 2026 RPS Generator System</span>
          <span class="text-slate-600">•</span>
          <span class="flex items-center gap-1">
            Made with <HeartIcon class="w-4 h-4 text-red-500" /> for Education
          </span>
        </div>
        <div class="flex items-center gap-4 text-sm">
          <span class="px-3 py-1 bg-slate-700/50 rounded-full text-xs font-medium">
            Vue.js + Spring Boot
          </span>
          <span class="px-3 py-1 bg-emerald-900/50 text-emerald-400 rounded-full text-xs font-medium">
            AI Powered
          </span>
        </div>
      </div>
    </div>
  </footer>
</template>
