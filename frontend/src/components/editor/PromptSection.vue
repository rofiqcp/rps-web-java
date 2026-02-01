<script setup lang="ts">
import { useRpsStore } from '@/stores/rps'
import { LightBulbIcon } from '@heroicons/vue/24/outline'

const store = useRpsStore()
</script>

<template>
  <div class="card overflow-visible">
    <div class="p-6 bg-gradient-to-r from-violet-50 via-purple-50 to-fuchsia-50 border-b border-purple-100">
      <div class="flex items-start gap-4">
        <div class="w-12 h-12 bg-gradient-to-br from-violet-500 to-purple-600 rounded-xl flex items-center justify-center shadow-lg shadow-violet-500/25">
          <LightBulbIcon class="w-6 h-6 text-white" />
        </div>
        <div class="flex-1">
          <div class="flex items-center justify-between mb-2">
            <h3 class="text-lg font-bold text-slate-800">💡 Prompt RPS Mantap</h3>
            <span v-if="!store.hasCourseName" class="text-xs text-slate-500 italic">
              (Nama mata kuliah belum diisi)
            </span>
          </div>
          <p v-if="store.hasCourseName" class="text-sm font-medium text-purple-700 mb-2">
            {{ store.rpsData.identitas.nama }}
          </p>
          <p class="text-xs text-slate-600 mb-4">
            Masukan ide-ide, kebutuhan khusus, atau instruksi detail untuk menggenerate RPS yang lebih disesuaikan
          </p>
          <textarea
            v-model="store.promptRpsMantap"
            class="textarea bg-white/80 backdrop-blur-sm"
            rows="6"
            placeholder="Contoh prompt lengkap:
- Fokus pada praktik hands-on menggunakan Arduino dan sensor
- Proyek akhir adalah membuat robot line follower
- Gunakan metodologi PBL (Problem-Based Learning) untuk minggu 5-12
- Integrasi dengan industri: undang praktisi dari perusahaan robotika
- Sertifikasi Arduino sebagai pencapaian tambahan"
          ></textarea>
        </div>
      </div>
    </div>
  </div>
</template>
