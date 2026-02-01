<script setup lang="ts">
import { ref } from 'vue'
import { useRpsStore } from '@/stores/rps'
import BaseButton from '@/components/ui/BaseButton.vue'
import { 
  SparklesIcon, 
  ArrowDownTrayIcon, 
  ArrowUpTrayIcon, 
  DocumentArrowDownIcon,
  ArrowPathIcon,
  FolderOpenIcon
} from '@heroicons/vue/24/outline'

const store = useRpsStore()
const fileInput = ref<HTMLInputElement | null>(null)

function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    store.loadFromJson(file)
    target.value = ''
  }
}

function confirmReset() {
  if (confirm('Yakin ingin mereset semua data? Ini tidak bisa dibatalkan!')) {
    store.resetForm()
  }
}
</script>

<template>
  <div class="card">
    <div class="card-body">
      <div class="flex flex-wrap gap-3 justify-between items-center">
        <!-- Left Actions -->
        <div class="flex flex-wrap gap-2">
          <BaseButton
            variant="primary"
            :loading="store.isGenerating && store.generatingType === 'full'"
            :disabled="store.isGenerating || !store.hasCourseName"
            @click="store.generate('full')"
          >
            <SparklesIcon class="w-4 h-4" />
            Generate RPS Lengkap
          </BaseButton>
          
          <BaseButton
            variant="secondary"
            :loading="store.isGenerating && store.generatingType === 'sample'"
            :disabled="store.isGenerating"
            @click="store.loadSample"
          >
            <ArrowDownTrayIcon class="w-4 h-4" />
            Muat Contoh
          </BaseButton>
          
          <BaseButton
            variant="ghost"
            @click="confirmReset"
          >
            <ArrowPathIcon class="w-4 h-4" />
            Reset
          </BaseButton>
        </div>

        <!-- Right Actions -->
        <div class="flex flex-wrap gap-2">
          <BaseButton
            variant="secondary"
            @click="store.saveAsJson"
          >
            <ArrowUpTrayIcon class="w-4 h-4" />
            Simpan JSON
          </BaseButton>
          
          <BaseButton
            variant="secondary"
            @click="fileInput?.click()"
          >
            <FolderOpenIcon class="w-4 h-4" />
            Muat JSON
          </BaseButton>
          <input
            ref="fileInput"
            type="file"
            accept=".json"
            class="hidden"
            @change="handleFileChange"
          />
          
          <BaseButton
            variant="success"
            :loading="store.isExporting"
            :disabled="store.isExporting || !store.hasCourseName"
            @click="store.exportDocx"
          >
            <DocumentArrowDownIcon class="w-4 h-4" />
            Download DOCX
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>
