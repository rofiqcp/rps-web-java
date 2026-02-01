<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRpsStore } from '@/stores/rps'
import RpsEditor from '@/components/RpsEditor.vue'
import AlertMessage from '@/components/ui/AlertMessage.vue'

const store = useRpsStore()
const apiStatus = ref<'checking' | 'online' | 'offline'>('checking')

onMounted(async () => {
  store.loadFromStorage()
  
  // Check API health
  try {
    const response = await fetch('/api/health')
    if (response.ok) {
      apiStatus.value = 'online'
    } else {
      apiStatus.value = 'offline'
    }
  } catch {
    apiStatus.value = 'offline'
  }
})
</script>

<template>
  <div class="space-y-6 animate-fade-in">
    <!-- Hero Section -->
    <div class="card overflow-visible">
      <div class="relative px-8 py-10 bg-gradient-to-br from-primary-500 via-primary-600 to-accent-600 rounded-2xl text-white overflow-hidden">
        <!-- Background decoration -->
        <div class="absolute top-0 right-0 w-64 h-64 bg-white/5 rounded-full -translate-y-1/2 translate-x-1/2"></div>
        <div class="absolute bottom-0 left-0 w-48 h-48 bg-white/5 rounded-full translate-y-1/2 -translate-x-1/2"></div>
        
        <div class="relative z-10">
          <div class="flex items-start justify-between">
            <div>
              <h1 class="text-3xl font-bold mb-2">🎓 Buat RPS Baru</h1>
              <p class="text-primary-100 max-w-2xl">
                Gunakan sistem ini untuk membuat Rencana Pembelajaran Semester dengan bantuan AI. 
                Isi form di bawah atau gunakan AI untuk generate konten otomatis.
              </p>
            </div>
            <div class="hidden lg:block">
              <div :class="[
                'px-4 py-2 rounded-full text-sm font-medium flex items-center gap-2',
                apiStatus === 'online' ? 'bg-emerald-500/20 text-emerald-100' :
                apiStatus === 'offline' ? 'bg-red-500/20 text-red-100' :
                'bg-white/20 text-white'
              ]">
                <span :class="[
                  'w-2 h-2 rounded-full',
                  apiStatus === 'online' ? 'bg-emerald-400 animate-pulse' :
                  apiStatus === 'offline' ? 'bg-red-400' :
                  'bg-white animate-pulse'
                ]"></span>
                {{ apiStatus === 'online' ? 'API Online' : apiStatus === 'offline' ? 'API Offline' : 'Checking...' }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Alerts -->
    <AlertMessage 
      v-if="store.error" 
      type="error" 
      :message="store.error" 
      @close="store.clearError" 
    />
    <AlertMessage 
      v-if="store.success" 
      type="success" 
      :message="store.success" 
      @close="store.clearSuccess" 
    />

    <!-- Main Editor -->
    <RpsEditor />
  </div>
</template>
