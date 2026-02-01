<script setup lang="ts">
import { useRpsStore } from '@/stores/rps'
import BaseButton from '@/components/ui/BaseButton.vue'
import { BookOpenIcon, SparklesIcon, TrashIcon, PlusIcon } from '@heroicons/vue/24/outline'

const store = useRpsStore()

async function generateReferences() {
  await store.generateSection('references')
}

function addReference() {
  const nextNum = store.rpsData.referensi.length + 1
  store.rpsData.referensi.push({
    nomor: nextNum,
    judul: '',
    penulis: '',
    tahun: new Date().getFullYear(),
    penerbit: '',
    kota: '',
    isbn: '',
    jenis: 'Utama'
  })
}

function removeReference(index: number) {
  store.rpsData.referensi.splice(index, 1)
  // Renumber
  store.rpsData.referensi.forEach((ref, i) => {
    ref.nomor = i + 1
  })
}
</script>

<template>
  <div class="card">
    <div class="card-header">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="section-icon bg-gradient-to-br from-cyan-500 to-blue-500">
            <BookOpenIcon class="w-5 h-5" />
          </div>
          <h2 class="section-title">Daftar Referensi</h2>
        </div>
        <div class="flex items-center gap-2">
          <BaseButton 
            variant="primary" 
            size="sm" 
            @click="generateReferences"
            :disabled="store.isGenerating"
          >
            <SparklesIcon class="w-4 h-4" />
            Generate AI
          </BaseButton>
        </div>
      </div>
    </div>

    <div class="card-body space-y-4">
      <!-- Add Button -->
      <div class="flex items-center justify-end">
        <BaseButton variant="secondary" size="sm" @click="addReference">
          <PlusIcon class="w-4 h-4" />
          Tambah Referensi
        </BaseButton>
      </div>

      <!-- Reference List -->
      <div class="space-y-4">
        <div 
          v-for="(ref, index) in store.rpsData.referensi" 
          :key="index"
          class="bg-slate-50 p-4 rounded-xl border border-slate-200 hover:border-cyan-300 transition-colors"
        >
          <div class="flex items-start gap-4">
            <div class="flex-shrink-0">
              <span class="inline-flex items-center justify-center w-10 h-10 rounded-xl bg-gradient-to-br from-cyan-500 to-blue-500 text-white text-lg font-bold">
                {{ ref.nomor }}
              </span>
            </div>
            <div class="flex-1 space-y-3">
              <!-- Row 1 -->
              <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
                <div class="md:col-span-2">
                  <label class="text-xs font-medium text-slate-500 mb-1 block">Judul</label>
                  <input
                    v-model="ref.judul"
                    type="text"
                    class="input text-sm"
                    placeholder="Judul buku/artikel..."
                  />
                </div>
                <div>
                  <label class="text-xs font-medium text-slate-500 mb-1 block">Jenis</label>
                  <select v-model="ref.jenis" class="select text-sm">
                    <option value="Utama">Utama</option>
                    <option value="Pendukung">Pendukung</option>
                  </select>
                </div>
              </div>

              <!-- Row 2 -->
              <div class="grid grid-cols-1 md:grid-cols-4 gap-3">
                <div class="md:col-span-2">
                  <label class="text-xs font-medium text-slate-500 mb-1 block">Penulis</label>
                  <input
                    v-model="ref.penulis"
                    type="text"
                    class="input text-sm"
                    placeholder="Nama penulis..."
                  />
                </div>
                <div>
                  <label class="text-xs font-medium text-slate-500 mb-1 block">Tahun</label>
                  <input
                    v-model.number="ref.tahun"
                    type="number"
                    min="1900"
                    max="2100"
                    class="input text-sm"
                    placeholder="2024"
                  />
                </div>
                <div>
                  <label class="text-xs font-medium text-slate-500 mb-1 block">ISBN</label>
                  <input
                    v-model="ref.isbn"
                    type="text"
                    class="input text-sm"
                    placeholder="978-xxx-xxx"
                  />
                </div>
              </div>

              <!-- Row 3 -->
              <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div>
                  <label class="text-xs font-medium text-slate-500 mb-1 block">Penerbit</label>
                  <input
                    v-model="ref.penerbit"
                    type="text"
                    class="input text-sm"
                    placeholder="Nama penerbit..."
                  />
                </div>
                <div>
                  <label class="text-xs font-medium text-slate-500 mb-1 block">Kota</label>
                  <input
                    v-model="ref.kota"
                    type="text"
                    class="input text-sm"
                    placeholder="Kota terbit..."
                  />
                </div>
              </div>
            </div>
            <div class="flex-shrink-0">
              <button
                @click="removeReference(index)"
                class="btn btn-danger p-2"
                title="Hapus Referensi"
              >
                <TrashIcon class="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-if="store.rpsData.referensi.length === 0" class="text-center py-8 text-slate-500 bg-slate-50 rounded-xl">
        <BookOpenIcon class="w-12 h-12 mx-auto mb-2 text-slate-400" />
        <p>Belum ada referensi. Klik "Tambah Referensi" atau gunakan AI untuk generate.</p>
      </div>

      <!-- Summary -->
      <div v-if="store.rpsData.referensi.length > 0" class="p-4 bg-gradient-to-r from-cyan-50 to-blue-50 rounded-xl border border-cyan-200">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-4">
            <div class="text-center">
              <div class="text-2xl font-bold text-cyan-600">{{ store.rpsData.referensi.length }}</div>
              <div class="text-xs text-slate-500">Total Referensi</div>
            </div>
            <div class="h-10 w-px bg-cyan-200"></div>
            <div class="text-center">
              <div class="text-2xl font-bold text-blue-600">
                {{ store.rpsData.referensi.filter(r => r.jenis === 'Utama').length }}
              </div>
              <div class="text-xs text-slate-500">Utama</div>
            </div>
            <div class="h-10 w-px bg-cyan-200"></div>
            <div class="text-center">
              <div class="text-2xl font-bold text-slate-600">
                {{ store.rpsData.referensi.filter(r => r.jenis === 'Pendukung').length }}
              </div>
              <div class="text-xs text-slate-500">Pendukung</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
