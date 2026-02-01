<script setup lang="ts">
import { useRpsStore } from '@/stores/rps'
import BaseButton from '@/components/ui/BaseButton.vue'
import { AcademicCapIcon, SparklesIcon, TrashIcon, PlusIcon } from '@heroicons/vue/24/outline'

const store = useRpsStore()

function addCPL() {
  const nextNum = store.rpsData.cpl.length + 1
  store.rpsData.cpl.push({ kode: `CPL ${nextNum}`, pernyataan: '' })
}

function removeCPL(index: number) {
  if (store.rpsData.cpl.length <= 1) return
  store.rpsData.cpl.splice(index, 1)
}
</script>

<template>
  <div class="card">
    <div class="card-header">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="section-icon bg-gradient-to-br from-emerald-500 to-teal-500">
            <AcademicCapIcon class="w-5 h-5" />
          </div>
          <h2 class="section-title">Capaian Pembelajaran Lulusan (CPL)</h2>
        </div>
      </div>
    </div>

    <div class="card-body space-y-4">
      <!-- Generate Section -->
      <div class="p-4 bg-gradient-to-r from-emerald-50 to-teal-50 rounded-xl border border-emerald-200">
        <label class="label">💡 Konteks untuk Generate CPL (Opsional)</label>
        <textarea
          v-model="store.cplContext"
          class="textarea bg-white/80 mb-3"
          rows="2"
          placeholder="Contoh: Fokus pada kemampuan analisis data, pemrograman Python, dan machine learning..."
        ></textarea>
        <BaseButton
          variant="primary"
          size="sm"
          :loading="store.isGenerating && store.generatingType === 'cpl'"
          :disabled="store.isGenerating || !store.hasCourseName"
          @click="store.generate('cpl', store.cplContext)"
        >
          <SparklesIcon class="w-4 h-4" />
          Generate CPL dengan AI
        </BaseButton>
      </div>

      <!-- CPL List Header -->
      <div class="flex items-center justify-between">
        <div>
          <h3 class="font-semibold text-slate-800">🎯 Daftar CPL</h3>
          <p class="text-sm text-slate-500 mt-1">CPL yang dibebankan pada mata kuliah ini</p>
        </div>
        <BaseButton variant="secondary" size="sm" @click="addCPL">
          <PlusIcon class="w-4 h-4" />
          Tambah CPL
        </BaseButton>
      </div>

      <!-- CPL Items -->
      <div v-if="store.rpsData.cpl.length === 0" class="text-center py-8 text-slate-500 bg-slate-50 rounded-xl">
        <p>Belum ada CPL. Klik "Generate CPL dengan AI" atau "Tambah CPL" untuk memulai.</p>
      </div>

      <div v-else class="space-y-3">
        <div 
          v-for="(cpl, index) in store.rpsData.cpl" 
          :key="index"
          class="bg-slate-50 p-4 rounded-xl border border-slate-200 hover:border-emerald-300 transition-colors"
        >
          <div class="flex gap-4 items-start">
            <div class="w-28">
              <label class="text-xs font-medium text-slate-500 mb-1 block">Kode CPL</label>
              <input
                v-model="cpl.kode"
                type="text"
                class="input text-center font-semibold text-sm"
                placeholder="CPL 1"
              />
            </div>
            <div class="flex-1">
              <label class="text-xs font-medium text-slate-500 mb-1 block">Pernyataan CPL</label>
              <textarea
                v-model="cpl.pernyataan"
                class="textarea text-sm"
                rows="2"
                placeholder="Contoh: Mampu menganalisis dan memecahkan permasalahan rekayasa..."
              ></textarea>
            </div>
            <button
              @click="removeCPL(index)"
              :disabled="store.rpsData.cpl.length <= 1"
              class="btn btn-danger p-2 self-end"
              title="Hapus CPL"
            >
              <TrashIcon class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      <!-- Tips -->
      <div class="p-4 bg-blue-50 rounded-xl border border-blue-200">
        <h4 class="font-medium text-blue-800 mb-2">💡 Tips Menyusun CPL</h4>
        <ul class="text-sm text-blue-700 space-y-1 list-disc list-inside">
          <li>CPL harus selaras dengan profil lulusan program studi</li>
          <li>Gunakan kata kerja operasional yang terukur (mampu, menguasai, memiliki)</li>
          <li>Cakup aspek pengetahuan, keterampilan, dan sikap</li>
          <li>Biasanya 3-5 CPL yang dibebankan pada satu mata kuliah</li>
        </ul>
      </div>
    </div>
  </div>
</template>
