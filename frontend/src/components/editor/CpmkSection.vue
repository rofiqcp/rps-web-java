<script setup lang="ts">
import { computed } from 'vue'
import { useRpsStore } from '@/stores/rps'
import BaseButton from '@/components/ui/BaseButton.vue'
import { ChartBarIcon, SparklesIcon, TrashIcon, PlusIcon } from '@heroicons/vue/24/outline'

const store = useRpsStore()

const canGenerate = computed(() => 
  store.hasCourseName && 
  store.rpsData.cpl.some(c => c.pernyataan) && 
  store.rpsData.deskripsi
)

function addCPMK() {
  const nextNum = store.rpsData.cpmk.length + 1
  const newCpmkKode = `CPMK ${nextNum}`
  store.rpsData.cpmk.push({
    kode: newCpmkKode,
    pernyataan: '',
    mapping_cpl: [],
    n1: 5, n2: 8, n3: 2, n4: 5, n5: 0
  })
  store.rpsData.ik.push({
    kode: `IK ${nextNum}`,
    mapping_cpl: '',
    mapping_cpmk: newCpmkKode,
    pernyataan: ''
  })
}

function removeCPMK(index: number) {
  if (store.rpsData.cpmk.length <= 1) return
  store.rpsData.cpmk.splice(index, 1)
  store.rpsData.ik.splice(index, 1)
}

function toggleCplMapping(cpmkIndex: number, cplKode: string) {
  const cpmk = store.rpsData.cpmk[cpmkIndex]
  if (!cpmk.mapping_cpl) cpmk.mapping_cpl = []
  
  const idx = cpmk.mapping_cpl.indexOf(cplKode)
  if (idx >= 0) {
    cpmk.mapping_cpl.splice(idx, 1)
  } else {
    cpmk.mapping_cpl.push(cplKode)
  }
}

function isCplMapped(cpmkIndex: number, cplKode: string): boolean {
  const cpmk = store.rpsData.cpmk[cpmkIndex]
  return cpmk.mapping_cpl?.includes(cplKode) || false
}
</script>

<template>
  <div class="card">
    <div class="card-header">
      <div class="flex items-center gap-3">
        <div class="section-icon bg-gradient-to-br from-violet-500 to-purple-500">
          <ChartBarIcon class="w-5 h-5" />
        </div>
        <h2 class="section-title">Capaian Pembelajaran Mata Kuliah (CPMK)</h2>
      </div>
    </div>

    <div class="card-body space-y-4">
      <!-- Generate Section -->
      <div class="p-4 bg-gradient-to-r from-violet-50 to-purple-50 rounded-xl border border-violet-200">
        <label class="label">💡 Konteks untuk Generate CPMK (Opsional)</label>
        <textarea
          v-model="store.cpmkContext"
          class="textarea bg-white/80 mb-3"
          rows="2"
          placeholder="Contoh: Mahasiswa harus mampu membuat aplikasi web full-stack, desain database, dan deploy ke cloud..."
        ></textarea>
        <BaseButton
          variant="accent"
          size="sm"
          :loading="store.isGenerating && store.generatingType === 'cpmk'"
          :disabled="store.isGenerating || !canGenerate"
          :title="!canGenerate ? 'Isi CPL dan deskripsi terlebih dahulu' : ''"
          @click="store.generate('cpmk', store.cpmkContext)"
        >
          <SparklesIcon class="w-4 h-4" />
          Generate CPMK dengan AI
        </BaseButton>
      </div>

      <div v-if="!canGenerate" class="alert alert-warning">
        ⚠️ Untuk generate CPMK dengan AI, pastikan CPL dan deskripsi mata kuliah sudah diisi.
      </div>

      <!-- CPMK List Header -->
      <div class="flex items-center justify-between">
        <div>
          <h3 class="font-semibold text-slate-800">📊 Daftar CPMK</h3>
          <p class="text-sm text-slate-500 mt-1">Setelah menyelesaikan pembelajaran, mahasiswa diharapkan mampu:</p>
        </div>
        <BaseButton variant="secondary" size="sm" @click="addCPMK">
          <PlusIcon class="w-4 h-4" />
          Tambah CPMK
        </BaseButton>
      </div>

      <!-- CPMK Items -->
      <div class="space-y-3">
        <div 
          v-for="(cpmk, index) in store.rpsData.cpmk" 
          :key="index"
          class="bg-slate-50 p-4 rounded-xl border border-slate-200 hover:border-violet-300 transition-colors"
        >
          <div class="flex gap-4 items-start">
            <div class="w-28">
              <label class="text-xs font-medium text-slate-500 mb-1 block">Kode</label>
              <input
                v-model="cpmk.kode"
                type="text"
                class="input text-center font-semibold text-sm"
                placeholder="CPMK 1"
              />
            </div>
            <div class="flex-1">
              <label class="text-xs font-medium text-slate-500 mb-1 block">Pernyataan CPMK</label>
              <textarea
                v-model="cpmk.pernyataan"
                class="textarea text-sm"
                rows="2"
                placeholder="Contoh: Merakit rangkaian sensor–aktuator dan melakukan pengukuran dasar serta troubleshooting."
              ></textarea>
            </div>
            <button
              @click="removeCPMK(index)"
              :disabled="store.rpsData.cpmk.length <= 1"
              class="btn btn-danger p-2 self-end"
              title="Hapus CPMK"
            >
              <TrashIcon class="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      <!-- Mapping Table -->
      <div class="bg-slate-100 p-4 rounded-xl">
        <h4 class="font-medium text-slate-800 mb-3">📌 Pemetaan CPMK ke CPL</h4>
        <div class="overflow-x-auto">
          <table class="table bg-white rounded-lg overflow-hidden">
            <thead>
              <tr>
                <th>CPMK</th>
                <th v-for="cpl in store.rpsData.cpl" :key="cpl.kode" class="text-center">
                  {{ cpl.kode }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(cpmk, cpmkIdx) in store.rpsData.cpmk" :key="cpmk.kode">
                <td class="font-medium">{{ cpmk.kode }}</td>
                <td v-for="cpl in store.rpsData.cpl" :key="cpl.kode" class="text-center">
                  <input
                    type="checkbox"
                    class="w-4 h-4 cursor-pointer accent-violet-600"
                    :checked="isCplMapped(cpmkIdx, cpl.kode)"
                    @change="toggleCplMapping(cpmkIdx, cpl.kode)"
                  />
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <p class="text-xs text-slate-500 mt-2">* Centang untuk menunjukkan CPMK mendukung pencapaian CPL tersebut</p>
      </div>

      <!-- Tips -->
      <div class="p-4 bg-blue-50 rounded-xl border border-blue-200">
        <h4 class="font-medium text-blue-800 mb-2">💡 Tips Menyusun CPMK</h4>
        <ul class="text-sm text-blue-700 space-y-1 list-disc list-inside">
          <li>CPMK harus spesifik, terukur, dan dapat dicapai dalam satu semester</li>
          <li>Gunakan kata kerja operasional Taksonomi Bloom (menganalisis, menerapkan, mengevaluasi)</li>
          <li>Setiap CPMK harus terhubung dengan minimal satu CPL</li>
          <li>Biasanya 3-6 CPMK per mata kuliah</li>
        </ul>
      </div>
    </div>
  </div>
</template>
