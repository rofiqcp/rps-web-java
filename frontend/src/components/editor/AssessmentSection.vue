<script setup lang="ts">
import { computed } from 'vue'
import { useRpsStore } from '@/stores/rps'
import { ClipboardDocumentCheckIcon } from '@heroicons/vue/24/outline'

const store = useRpsStore()

// Assessment types N1-N5
const assessmentTypes = [
  { key: 'n1', label: 'N1', desc: 'Tugas' },
  { key: 'n2', label: 'N2', desc: 'Kuis' },
  { key: 'n3', label: 'N3', desc: 'UTS' },
  { key: 'n4', label: 'N4', desc: 'UAS' },
  { key: 'n5', label: 'N5', desc: 'Praktikum' },
]

// Calculate total for each CPMK
function getCpmkTotal(cpmkIndex: number) {
  const cpmk = store.rpsData.cpmk[cpmkIndex]
  return (cpmk.n1 || 0) + (cpmk.n2 || 0) + (cpmk.n3 || 0) + (cpmk.n4 || 0) + (cpmk.n5 || 0)
}

// Calculate total for each assessment type across all CPMK
function getAssessmentTotal(key: string) {
  return store.rpsData.cpmk.reduce((sum, cpmk) => sum + (Number((cpmk as any)[key]) || 0), 0)
}

// Calculate grand total
const grandTotal = computed(() => {
  return assessmentTypes.reduce((sum, type) => sum + getAssessmentTotal(type.key), 0)
})
</script>

<template>
  <div class="card">
    <div class="card-header">
      <div class="flex items-center gap-3">
        <div class="section-icon bg-gradient-to-br from-rose-500 to-pink-500">
          <ClipboardDocumentCheckIcon class="w-5 h-5" />
        </div>
        <h2 class="section-title">Matriks Penilaian</h2>
      </div>
    </div>

    <div class="card-body">
      <!-- Info Banner -->
      <div class="alert alert-info mb-4">
        💡 <strong>Catatan:</strong> Masukkan nilai bobot (%) untuk setiap komponen penilaian. Total keseluruhan harus 100%.
      </div>

      <!-- Assessment Matrix Table -->
      <div class="overflow-x-auto">
        <table class="w-full border-collapse">
          <thead>
            <tr class="bg-gradient-to-r from-rose-600 to-pink-600 text-white">
              <th class="table-header-cell w-28">CPMK</th>
              <th 
                v-for="type in assessmentTypes" 
                :key="type.key"
                class="table-header-cell w-24 text-center"
              >
                <div class="font-bold">{{ type.label }}</div>
                <div class="text-xs font-normal opacity-80">{{ type.desc }}</div>
              </th>
              <th class="table-header-cell w-24 text-center">Total</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="(cpmk, index) in store.rpsData.cpmk" 
              :key="cpmk.kode"
              class="hover:bg-rose-50 transition-colors"
            >
              <td class="table-cell">
                <span class="inline-flex items-center gap-2">
                  <span class="w-8 h-8 rounded-full bg-gradient-to-br from-rose-500 to-pink-500 text-white text-xs font-bold flex items-center justify-center">
                    {{ index + 1 }}
                  </span>
                  <span class="font-medium text-sm">{{ cpmk.kode }}</span>
                </span>
              </td>
              <td v-for="type in assessmentTypes" :key="type.key" class="table-cell text-center">
                <input
                  v-model.number="(cpmk as any)[type.key]"
                  type="number"
                  min="0"
                  max="100"
                  step="1"
                  class="input text-sm py-1 text-center w-16 mx-auto"
                  :placeholder="'0'"
                />
              </td>
              <td class="table-cell text-center">
                <span 
                  class="inline-block px-3 py-1 rounded-full text-sm font-semibold"
                  :class="getCpmkTotal(index) > 0 ? 'bg-rose-100 text-rose-700' : 'bg-slate-100 text-slate-500'"
                >
                  {{ getCpmkTotal(index) }}%
                </span>
              </td>
            </tr>
          </tbody>
          <tfoot>
            <tr class="bg-slate-100 font-semibold">
              <td class="table-cell text-right">Total per Komponen:</td>
              <td 
                v-for="type in assessmentTypes" 
                :key="type.key"
                class="table-cell text-center"
              >
                <span class="inline-block px-3 py-1 rounded-full text-sm bg-slate-200 text-slate-700">
                  {{ getAssessmentTotal(type.key) }}%
                </span>
              </td>
              <td class="table-cell text-center">
                <span 
                  class="inline-block px-3 py-1 rounded-full text-sm font-bold"
                  :class="[
                    grandTotal === 100 
                      ? 'bg-green-100 text-green-700' 
                      : 'bg-red-100 text-red-700'
                  ]"
                >
                  {{ grandTotal }}%
                </span>
              </td>
            </tr>
          </tfoot>
        </table>
      </div>

      <div v-if="store.rpsData.cpmk.length === 0" class="text-center py-8 text-slate-500 bg-slate-50 rounded-xl mt-4">
        <p>Tambahkan CPMK terlebih dahulu di bagian CPMK untuk mengisi matriks penilaian.</p>
      </div>

      <div v-if="grandTotal !== 100 && store.rpsData.cpmk.length > 0" class="alert alert-warning mt-4">
        ⚠️ <strong>Peringatan:</strong> Total bobot penilaian saat ini {{ grandTotal }}%. Total harus 100%.
      </div>

      <!-- Assessment Description -->
      <div class="mt-6 p-4 bg-slate-50 rounded-xl border border-slate-200">
        <h4 class="font-semibold text-slate-700 mb-3">Keterangan Komponen Penilaian:</h4>
        <div class="grid grid-cols-2 md:grid-cols-5 gap-4">
          <div v-for="type in assessmentTypes" :key="type.key" class="flex items-center gap-2">
            <span class="w-8 h-8 rounded-lg bg-gradient-to-br from-rose-500 to-pink-500 text-white text-xs font-bold flex items-center justify-center">
              {{ type.label }}
            </span>
            <span class="text-sm text-slate-600">{{ type.desc }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.table-header-cell {
  @apply px-4 py-3 text-left text-sm font-semibold;
}

.table-cell {
  @apply px-4 py-3 border-b border-slate-200;
}
</style>
