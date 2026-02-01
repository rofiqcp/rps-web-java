<script setup lang="ts">
import { useRpsStore } from '@/stores/rps'
import { ClipboardDocumentListIcon } from '@heroicons/vue/24/outline'

const store = useRpsStore()

const statusOptions = [
  'Mata Kuliah Wajib',
  'Mata Kuliah Pilihan',
  'Mata Kuliah Peminatan'
]

const jenisMKOptions = [
  { value: 'teori', label: 'Teori' },
  { value: 'praktikum', label: 'Praktikum' },
  { value: 'campuran', label: 'Campuran (Teori + Praktikum)' }
]
</script>

<template>
  <div class="card">
    <div class="card-header">
      <div class="flex items-center gap-3">
        <div class="section-icon bg-gradient-to-br from-blue-500 to-cyan-500">
          <ClipboardDocumentListIcon class="w-5 h-5" />
        </div>
        <h2 class="section-title">Identitas Mata Kuliah</h2>
      </div>
    </div>
    
    <div class="card-body space-y-6">
      <!-- Course Identity -->
      <div>
        <h3 class="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
          <span class="w-6 h-6 bg-blue-100 text-blue-600 rounded-lg flex items-center justify-center text-xs">📋</span>
          Identitas Mata Kuliah
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <label class="label">Kode Mata Kuliah</label>
            <input
              v-model="store.rpsData.identitas.kode"
              type="text"
              class="input"
              placeholder="e.g., TRAO6251"
            />
          </div>
          <div class="md:col-span-2">
            <label class="label">Nama Mata Kuliah <span class="text-red-500">*</span></label>
            <input
              v-model="store.rpsData.identitas.nama"
              type="text"
              class="input"
              :class="{ 'input-error': !store.hasCourseName }"
              placeholder="e.g., Praktikum Mekatronika dan Robotika"
              required
            />
          </div>
          <div>
            <label class="label">SKS</label>
            <input
              v-model.number="store.rpsData.identitas.sks"
              type="number"
              min="1"
              max="6"
              class="input"
            />
          </div>
          <div>
            <label class="label">Semester</label>
            <input
              v-model.number="store.rpsData.identitas.semester"
              type="number"
              min="1"
              max="8"
              class="input"
            />
          </div>
          <div>
            <label class="label">Status MK</label>
            <select v-model="store.rpsData.identitas.status" class="select">
              <option v-for="status in statusOptions" :key="status" :value="status">
                {{ status }}
              </option>
            </select>
          </div>
          <div class="md:col-span-2">
            <label class="label">Mata Kuliah Prasyarat</label>
            <input
              v-model="store.rpsData.identitas.prasyarat"
              type="text"
              class="input"
              placeholder="e.g., Dasar Elektronika, Pemrograman Dasar"
            />
          </div>
          <div>
            <label class="label">Jenis Mata Kuliah</label>
            <select v-model="store.jenisMK" class="select">
              <option v-for="opt in jenisMKOptions" :key="opt.value" :value="opt.value">
                {{ opt.label }}
              </option>
            </select>
          </div>
        </div>
      </div>

      <!-- Institution -->
      <div>
        <h3 class="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
          <span class="w-6 h-6 bg-emerald-100 text-emerald-600 rounded-lg flex items-center justify-center text-xs">🏫</span>
          Institusi
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="label">Program Studi</label>
            <input
              v-model="store.rpsData.institusi.programStudi"
              type="text"
              class="input"
            />
          </div>
          <div>
            <label class="label">Fakultas</label>
            <input
              v-model="store.rpsData.institusi.fakultas"
              type="text"
              class="input"
            />
          </div>
          <div>
            <label class="label">Universitas</label>
            <input
              v-model="store.rpsData.institusi.universitas"
              type="text"
              class="input"
            />
          </div>
        </div>
      </div>

      <!-- Authority -->
      <div>
        <h3 class="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
          <span class="w-6 h-6 bg-violet-100 text-violet-600 rounded-lg flex items-center justify-center text-xs">👤</span>
          Otoritas
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div class="bg-slate-50 p-4 rounded-xl border border-slate-100">
            <h4 class="font-medium text-slate-700 mb-3">Koordinator Mata Kuliah</h4>
            <div class="space-y-3">
              <input
                v-model="store.rpsData.otoritas.koordinatorMK.nama"
                type="text"
                class="input"
                placeholder="Nama"
              />
              <input
                v-model="store.rpsData.otoritas.koordinatorMK.nip"
                type="text"
                class="input"
                placeholder="NIP/NPPU"
              />
            </div>
          </div>
          <div class="bg-slate-50 p-4 rounded-xl border border-slate-100">
            <h4 class="font-medium text-slate-700 mb-3">Koordinator GPM</h4>
            <div class="space-y-3">
              <input
                v-model="store.rpsData.otoritas.koordinatorGPM.nama"
                type="text"
                class="input"
                placeholder="Nama"
              />
              <input
                v-model="store.rpsData.otoritas.koordinatorGPM.nip"
                type="text"
                class="input"
                placeholder="NIP/NPPU"
              />
            </div>
          </div>
          <div class="bg-slate-50 p-4 rounded-xl border border-slate-100">
            <h4 class="font-medium text-slate-700 mb-3">Ketua Program Studi</h4>
            <div class="space-y-3">
              <input
                v-model="store.rpsData.otoritas.ketuaProdi.nama"
                type="text"
                class="input"
                placeholder="Nama"
              />
              <input
                v-model="store.rpsData.otoritas.ketuaProdi.nip"
                type="text"
                class="input"
                placeholder="NIP/NPPU"
              />
            </div>
          </div>
          <div class="bg-slate-50 p-4 rounded-xl border border-slate-100">
            <h4 class="font-medium text-slate-700 mb-3">Dekan</h4>
            <div class="space-y-3">
              <input
                v-model="store.rpsData.otoritas.dekan.nama"
                type="text"
                class="input"
                placeholder="Nama"
              />
              <input
                v-model="store.rpsData.otoritas.dekan.nip"
                type="text"
                class="input"
                placeholder="NIP/NPPU"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
