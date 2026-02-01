<script setup lang="ts">
import { useRpsStore } from '@/stores/rps'
import BaseButton from '@/components/ui/BaseButton.vue'
import { 
  CalendarDaysIcon, 
  SparklesIcon, 
  ChevronDownIcon,
  ChevronUpIcon 
} from '@heroicons/vue/24/outline'

const store = useRpsStore()

async function generateWeeklyPlan() {
  await store.generateSection('weeklyPlan')
}

function toggleRow(index: number) {
  store.expandedWeeks[index] = !store.expandedWeeks[index]
}
</script>

<template>
  <div class="card">
    <div class="card-header">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="section-icon bg-gradient-to-br from-green-500 to-teal-500">
            <CalendarDaysIcon class="w-5 h-5" />
          </div>
          <h2 class="section-title">Rencana Pembelajaran Mingguan</h2>
        </div>
        <BaseButton 
          variant="primary" 
          size="sm" 
          @click="generateWeeklyPlan"
          :disabled="store.isGenerating"
        >
          <SparklesIcon class="w-4 h-4" />
          Generate AI
        </BaseButton>
      </div>
    </div>

    <div class="card-body">
      <!-- Info Banner -->
      <div class="alert alert-info mb-4">
        💡 <strong>Catatan:</strong> 16 pertemuan dalam satu semester. Klik baris untuk melihat/mengedit detail.
      </div>

      <!-- Weekly Plan Table -->
      <div class="overflow-x-auto">
        <table class="w-full border-collapse">
          <thead>
            <tr class="bg-gradient-to-r from-green-600 to-teal-600 text-white">
              <th class="table-header-cell w-20">Minggu</th>
              <th class="table-header-cell w-24">CPMK</th>
              <th class="table-header-cell">Materi/Pokok Bahasan</th>
              <th class="table-header-cell w-28">Bobot (%)</th>
              <th class="table-header-cell w-12"></th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(week, index) in store.rpsData.minggu" :key="index">
              <!-- Summary Row -->
              <tr 
                class="cursor-pointer hover:bg-green-50 transition-colors"
                :class="{ 'bg-green-50': store.expandedWeeks[index] }"
                @click="toggleRow(index)"
              >
                <td class="table-cell text-center">
                  <span class="inline-flex items-center justify-center w-8 h-8 rounded-full bg-gradient-to-br from-green-500 to-teal-500 text-white text-sm font-bold">
                    {{ index + 1 }}
                  </span>
                </td>
                <td class="table-cell">
                  <select 
                    v-model="week.mapping_cpmk"
                    class="select text-sm py-1"
                    @click.stop
                  >
                    <option value="">-</option>
                    <option v-for="cpmk in store.rpsData.cpmk" :key="cpmk.kode" :value="cpmk.kode">
                      {{ cpmk.kode }}
                    </option>
                  </select>
                </td>
                <td class="table-cell">
                  <input
                    v-model="week.materi"
                    type="text"
                    class="input text-sm py-1"
                    placeholder="Materi pokok bahasan..."
                    @click.stop
                  />
                </td>
                <td class="table-cell text-center">
                  <input
                    v-model.number="week.bobot"
                    type="number"
                    min="0"
                    max="100"
                    step="0.5"
                    class="input text-sm py-1 text-center w-20"
                    @click.stop
                  />
                </td>
                <td class="table-cell text-center">
                  <component 
                    :is="store.expandedWeeks[index] ? ChevronUpIcon : ChevronDownIcon" 
                    class="w-5 h-5 text-slate-400"
                  />
                </td>
              </tr>

              <!-- Expanded Details -->
              <tr v-if="store.expandedWeeks[index]">
                <td colspan="5" class="p-4 bg-slate-50 border-b border-slate-200">
                  <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <!-- Left Column -->
                    <div class="space-y-4">
                      <div>
                        <label class="text-xs font-medium text-slate-500 mb-1 block">Kemampuan Akhir</label>
                        <textarea
                          v-model="week.kemampuan_akhir"
                          class="textarea text-sm"
                          rows="2"
                          placeholder="Kemampuan akhir yang diharapkan..."
                        ></textarea>
                      </div>
                      <div>
                        <label class="text-xs font-medium text-slate-500 mb-1 block">Indikator</label>
                        <textarea
                          v-model="week.indikator"
                          class="textarea text-sm"
                          rows="2"
                          placeholder="Indikator pencapaian..."
                        ></textarea>
                      </div>
                      <div>
                        <label class="text-xs font-medium text-slate-500 mb-1 block">Metode Pembelajaran</label>
                        <input
                          v-model="week.metode"
                          type="text"
                          class="input text-sm"
                          placeholder="Metode pembelajaran..."
                        />
                      </div>
                    </div>

                    <!-- Right Column -->
                    <div class="space-y-4">
                      <div class="grid grid-cols-2 gap-4">
                        <div>
                          <label class="text-xs font-medium text-slate-500 mb-1 block">Waktu (menit)</label>
                          <input
                            v-model.number="week.waktu"
                            type="number"
                            min="0"
                            class="input text-sm"
                            placeholder="150"
                          />
                        </div>
                        <div>
                          <label class="text-xs font-medium text-slate-500 mb-1 block">Referensi</label>
                          <input
                            v-model="week.referensi"
                            type="text"
                            class="input text-sm"
                            placeholder="1, 2, 3"
                          />
                        </div>
                      </div>
                      <div>
                        <label class="text-xs font-medium text-slate-500 mb-1 block">Pengalaman Belajar</label>
                        <textarea
                          v-model="week.pengalaman_belajar"
                          class="textarea text-sm"
                          rows="2"
                          placeholder="Pengalaman belajar mahasiswa..."
                        ></textarea>
                      </div>
                      <div>
                        <label class="text-xs font-medium text-slate-500 mb-1 block">Penilaian</label>
                        <input
                          v-model="week.penilaian"
                          type="text"
                          class="input text-sm"
                          placeholder="Jenis penilaian..."
                        />
                      </div>
                    </div>
                  </div>
                </td>
              </tr>
            </template>
          </tbody>
          <tfoot>
            <tr class="bg-slate-100 font-semibold">
              <td colspan="3" class="table-cell text-right">Total Bobot:</td>
              <td class="table-cell text-center">
                <span 
                  :class="[
                    'px-3 py-1 rounded-full text-sm',
                    store.totalBobot === 100 
                      ? 'bg-green-100 text-green-700' 
                      : 'bg-red-100 text-red-700'
                  ]"
                >
                  {{ store.totalBobot }}%
                </span>
              </td>
              <td class="table-cell"></td>
            </tr>
          </tfoot>
        </table>
      </div>

      <div v-if="store.totalBobot !== 100" class="alert alert-warning mt-4">
        ⚠️ <strong>Peringatan:</strong> Total bobot saat ini {{ store.totalBobot }}%. Total bobot harus 100%.
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
