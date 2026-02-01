<script setup lang="ts">
import { useRpsStore } from '@/stores/rps'
import BaseButton from '@/components/ui/BaseButton.vue'
import { Square3Stack3DIcon, TrashIcon, PlusIcon } from '@heroicons/vue/24/outline'

const store = useRpsStore()

function addIK() {
  const nextNum = store.rpsData.ik.length + 1
  store.rpsData.ik.push({
    kode: `IK ${nextNum}`,
    mapping_cpl: '',
    mapping_cpmk: '',
    pernyataan: ''
  })
}

function removeIK(index: number) {
  store.rpsData.ik.splice(index, 1)
}
</script>

<template>
  <div class="card">
    <div class="card-header">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="section-icon bg-gradient-to-br from-amber-500 to-orange-500">
            <Square3Stack3DIcon class="w-5 h-5" />
          </div>
          <h2 class="section-title">Pernyataan Indikator Kinerja (IK)</h2>
        </div>
      </div>
    </div>

    <div class="card-body space-y-4">
      <div class="alert alert-info">
        💡 <strong>Catatan:</strong> Jumlah Indikator Kinerja harus sama dengan jumlah CPMK ({{ store.rpsData.cpmk.length }} IK)
      </div>

      <!-- IK List Header -->
      <div class="flex items-center justify-end">
        <BaseButton variant="secondary" size="sm" @click="addIK">
          <PlusIcon class="w-4 h-4" />
          Tambah IK
        </BaseButton>
      </div>

      <!-- IK Items -->
      <div class="space-y-3">
        <div 
          v-for="(ik, index) in store.rpsData.ik" 
          :key="index"
          class="bg-slate-50 p-4 rounded-xl border border-slate-200 hover:border-amber-300 transition-colors"
        >
          <div class="grid grid-cols-1 md:grid-cols-12 gap-4">
            <div class="md:col-span-2">
              <label class="text-xs font-medium text-slate-500 mb-1 block">Kode IK</label>
              <input
                v-model="ik.kode"
                type="text"
                class="input text-center font-semibold text-sm"
                :placeholder="`IK ${index + 1}`"
              />
            </div>
            <div class="md:col-span-2">
              <label class="text-xs font-medium text-slate-500 mb-1 block">CPL</label>
              <select v-model="ik.mapping_cpl" class="select text-sm">
                <option value="">Pilih CPL</option>
                <option v-for="cpl in store.rpsData.cpl" :key="cpl.kode" :value="cpl.kode">
                  {{ cpl.kode }}
                </option>
              </select>
            </div>
            <div class="md:col-span-2">
              <label class="text-xs font-medium text-slate-500 mb-1 block">CPMK</label>
              <select v-model="ik.mapping_cpmk" class="select text-sm">
                <option value="">Pilih CPMK</option>
                <option v-for="cpmk in store.rpsData.cpmk" :key="cpmk.kode" :value="cpmk.kode">
                  {{ cpmk.kode }}
                </option>
              </select>
            </div>
            <div class="md:col-span-5">
              <label class="text-xs font-medium text-slate-500 mb-1 block">Pernyataan IK</label>
              <textarea
                v-model="ik.pernyataan"
                class="textarea text-sm"
                rows="2"
                placeholder="Pernyataan indikator kinerja..."
              ></textarea>
            </div>
            <div class="md:col-span-1 flex items-end justify-center">
              <button
                @click="removeIK(index)"
                class="btn btn-danger p-2"
                title="Hapus IK"
              >
                <TrashIcon class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="store.rpsData.ik.length === 0" class="text-center py-8 text-slate-500 bg-slate-50 rounded-xl">
        <p>Belum ada Indikator Kinerja. Klik "Tambah IK" untuk memulai.</p>
      </div>
    </div>
  </div>
</template>
