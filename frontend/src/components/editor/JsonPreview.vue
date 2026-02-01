<script setup lang="ts">
import { computed } from 'vue'
import { useRpsStore } from '@/stores/rps'
import { CodeBracketIcon, ClipboardDocumentIcon, CheckIcon } from '@heroicons/vue/24/outline'
import { ref } from 'vue'

const store = useRpsStore()
const copied = ref(false)

const jsonString = computed(() => {
  return JSON.stringify(store.rpsData, null, 2)
})

async function copyToClipboard() {
  try {
    await navigator.clipboard.writeText(jsonString.value)
    copied.value = true
    setTimeout(() => {
      copied.value = false
    }, 2000)
  } catch (err) {
    console.error('Failed to copy:', err)
  }
}
</script>

<template>
  <div class="card">
    <div class="card-header">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="section-icon bg-gradient-to-br from-slate-600 to-slate-800">
            <CodeBracketIcon class="w-5 h-5" />
          </div>
          <h2 class="section-title">Preview Data JSON</h2>
        </div>
        <button
          @click="copyToClipboard"
          class="btn btn-secondary inline-flex items-center gap-2"
        >
          <component :is="copied ? CheckIcon : ClipboardDocumentIcon" class="w-4 h-4" />
          {{ copied ? 'Tersalin!' : 'Salin JSON' }}
        </button>
      </div>
    </div>

    <div class="card-body">
      <div class="relative">
        <pre class="bg-slate-900 text-slate-100 p-4 rounded-xl overflow-auto max-h-96 text-sm font-mono"><code>{{ jsonString }}</code></pre>
        <div class="absolute top-2 right-2 px-2 py-1 bg-slate-700 rounded text-xs text-slate-300">
          JSON
        </div>
      </div>
    </div>
  </div>
</template>
