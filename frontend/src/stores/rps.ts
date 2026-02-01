import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import type { RPSData, JenisMK, GenerateType } from '@/types/rps'
import { createEmptyRPS } from '@/types/rps'
import api from '@/services/api'

const STORAGE_KEY = 'rps-editor-data'

export const useRpsStore = defineStore('rps', () => {
  // State
  const rpsData = ref<RPSData>(createEmptyRPS())
  const jenisMK = ref<JenisMK>('campuran')
  const promptRpsMantap = ref('')
  const cplContext = ref('')
  const cpmkContext = ref('')
  const weeklyPlanContext = ref('')
  const referencesContext = ref('')
  
  const isLoaded = ref(false)
  const isGenerating = ref(false)
  const isExporting = ref(false)
  const generatingType = ref<GenerateType | 'sample' | null>(null)
  const error = ref<string | null>(null)
  const success = ref<string | null>(null)
  const expandedWeeks = ref<boolean[]>(Array(16).fill(false))

  // Computed
  const hasCourseName = computed(() => !!rpsData.value.identitas?.nama?.trim())
  const totalBobot = computed(() => 
    rpsData.value.minggu.reduce((sum, w) => sum + (w.bobot || 0), 0)
  )
  const isBobotValid = computed(() => totalBobot.value === 100)

  // Actions
  function updateRPS(updates: Partial<RPSData>) {
    rpsData.value = { ...rpsData.value, ...updates }
  }

  function loadFromStorage() {
    try {
      const savedState = localStorage.getItem(STORAGE_KEY)
      if (savedState) {
        const parsed = JSON.parse(savedState)
        rpsData.value = normalizeRpsData(parsed.rpsData)
        jenisMK.value = parsed.jenisMK || 'campuran'
        promptRpsMantap.value = parsed.promptRpsMantap || ''
        cplContext.value = parsed.cplContext || ''
        cpmkContext.value = parsed.cpmkContext || ''
        weeklyPlanContext.value = parsed.weeklyPlanContext || ''
        referencesContext.value = parsed.referencesContext || ''
      }
    } catch (err) {
      console.error('Failed to load from localStorage:', err)
    }
    isLoaded.value = true
  }

  function saveToStorage() {
    if (!isLoaded.value) return
    try {
      const state = {
        rpsData: rpsData.value,
        jenisMK: jenisMK.value,
        promptRpsMantap: promptRpsMantap.value,
        cplContext: cplContext.value,
        cpmkContext: cpmkContext.value,
        weeklyPlanContext: weeklyPlanContext.value,
        referencesContext: referencesContext.value,
      }
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state))
    } catch (err) {
      console.error('Failed to save to localStorage:', err)
    }
  }

  // Watch for changes and auto-save
  watch([rpsData, jenisMK, promptRpsMantap, cplContext, cpmkContext, weeklyPlanContext, referencesContext], 
    () => saveToStorage(), 
    { deep: true }
  )

  async function loadSample() {
    try {
      error.value = null
      isGenerating.value = true
      generatingType.value = 'sample'

      const response = await api.getSample()
      if (response.success && response.data) {
        rpsData.value = normalizeRpsData(response.data)
        jenisMK.value = 'campuran'
        promptRpsMantap.value = response.data.deskripsi || ''
        success.value = '✅ Data contoh berhasil dimuat'
        setTimeout(() => success.value = null, 4000)
      }
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Gagal memuat sample'
    } finally {
      isGenerating.value = false
      generatingType.value = null
    }
  }

  function resetForm() {
    rpsData.value = createEmptyRPS()
    jenisMK.value = 'campuran'
    promptRpsMantap.value = ''
    cplContext.value = ''
    cpmkContext.value = ''
    weeklyPlanContext.value = ''
    referencesContext.value = ''
    localStorage.removeItem(STORAGE_KEY)
    success.value = 'Form berhasil direset'
    setTimeout(() => success.value = null, 3000)
  }

  async function generate(type: GenerateType, customContext?: string) {
    if (!hasCourseName.value) {
      error.value = '⚠️ Nama mata kuliah harus diisi terlebih dahulu!'
      return
    }

    if (isGenerating.value && generatingType.value === 'full' && type !== 'full') {
      error.value = 'Tidak bisa generate saat Generate RPS Lengkap sedang berjalan'
      return
    }

    isGenerating.value = true
    generatingType.value = type
    error.value = null

    try {
      const response = await api.generate({
        type,
        identitas: rpsData.value.identitas,
        institusi: rpsData.value.institusi,
        jenisMK: jenisMK.value,
        additionalContext: customContext || promptRpsMantap.value,
        deskripsi: rpsData.value.deskripsi,
        cpl: rpsData.value.cpl,
        cpmk: rpsData.value.cpmk,
      })

      if (!response.success || !response.data) {
        throw new Error(response.error || 'Gagal generate konten')
      }

      const data = response.data

      if (type === 'full') {
        const updates: Partial<RPSData> = {}
        if (data.deskripsi) updates.deskripsi = data.deskripsi
        if (data.cpl) updates.cpl = data.cpl
        if (data.ik) updates.ik = data.ik
        if (data.cpmk) updates.cpmk = data.cpmk
        if (data.minggu) updates.minggu = data.minggu
        if (data.referensi) updates.referensi = data.referensi
        updateRPS(updates)
      } else if (type === 'cpl') {
        const updates: Partial<RPSData> = { cpl: data.cpl || [] }
        if (data.ik) updates.ik = data.ik
        updateRPS(updates)
      } else if (type === 'cpmk') {
        const updates: Partial<RPSData> = { cpmk: data.cpmk || [] }
        if (data.ik) updates.ik = data.ik
        updateRPS(updates)
      } else if (type === 'weeklyPlan') {
        updateRPS({ minggu: data.minggu || [] })
      } else if (type === 'references') {
        updateRPS({ referensi: data.referensi || [] })
      } else if (type === 'description') {
        updateRPS({ deskripsi: data.deskripsi || '' })
      }

      success.value = `✅ Berhasil generate ${type === 'full' ? 'RPS lengkap' : type.toUpperCase()}`
      setTimeout(() => success.value = null, 3000)

    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Terjadi kesalahan'
    } finally {
      isGenerating.value = false
      generatingType.value = null
    }
  }

  async function exportDocx() {
    if (!hasCourseName.value) {
      error.value = 'Nama mata kuliah harus diisi terlebih dahulu'
      return
    }

    isExporting.value = true
    error.value = null

    try {
      const blob = await api.exportDocx(rpsData.value)
      
      const filename = `RPS_${rpsData.value.identitas.kode || 'draft'}_${rpsData.value.identitas.nama.replace(/\s+/g, '_')}.docx`
      
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = filename
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      a.remove()

      success.value = '📄 Dokumen DOCX berhasil didownload'
      setTimeout(() => success.value = null, 3000)

    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Gagal export dokumen'
    } finally {
      isExporting.value = false
    }
  }

  function saveAsJson() {
    const json = JSON.stringify(rpsData.value, null, 2)
    const blob = new Blob([json], { type: 'application/json' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `RPS_${rpsData.value.identitas.kode || 'draft'}.json`
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    a.remove()
  }

  function loadFromJson(file: File) {
    const reader = new FileReader()
    reader.onload = (e) => {
      try {
        const data = JSON.parse(e.target?.result as string)
        rpsData.value = normalizeRpsData(data)
        success.value = '✅ Data berhasil dimuat dari JSON'
        setTimeout(() => success.value = null, 3000)
      } catch {
        error.value = 'Format JSON tidak valid'
      }
    }
    reader.readAsText(file)
  }

  function clearError() {
    error.value = null
  }

  function clearSuccess() {
    success.value = null
  }

  // Helper for generating sections
  async function generateSection(type: GenerateType) {
    await generate(type)
  }

  return {
    // State
    rpsData,
    jenisMK,
    promptRpsMantap,
    cplContext,
    cpmkContext,
    weeklyPlanContext,
    referencesContext,
    isLoaded,
    isGenerating,
    isExporting,
    generatingType,
    error,
    success,
    expandedWeeks,
    
    // Computed
    hasCourseName,
    totalBobot,
    isBobotValid,
    
    // Actions
    updateRPS,
    loadFromStorage,
    loadSample,
    resetForm,
    generate,
    generateSection,
    exportDocx,
    saveAsJson,
    loadFromJson,
    clearError,
    clearSuccess,
  }
})

// Helper function to normalize RPS data
function normalizeRpsData(input: unknown): RPSData {
  const base = createEmptyRPS()
  const data = (input && typeof input === 'object') ? (input as Record<string, unknown>) : {}

  const identitasSource = (data.identitas || data.identity || {}) as Record<string, unknown>
  const institusiSource = (data.institusi || data.institution || {}) as Record<string, unknown>
  const otoritasSource = (data.otoritas || data.authority || {}) as Record<string, unknown>

  const cplSource = Array.isArray(data.cpl) ? data.cpl : []
  const cpmkSource = Array.isArray(data.cpmk) ? data.cpmk : []
  const ikSource = Array.isArray(data.ik) ? data.ik : []
  const referensiSource = Array.isArray(data.referensi) ? data.referensi : []
  const mingguSource = Array.isArray(data.minggu) ? data.minggu : []

  return {
    ...base,
    id: data.id as string || base.id,
    createdAt: data.createdAt as string || base.createdAt,
    updatedAt: data.updatedAt as string || base.updatedAt,
    identitas: { ...base.identitas, ...identitasSource },
    institusi: { ...base.institusi, ...institusiSource },
    otoritas: {
      ...base.otoritas,
      koordinatorMK: { ...base.otoritas.koordinatorMK, ...(otoritasSource.koordinatorMK as object || {}) },
      koordinatorGPM: { ...base.otoritas.koordinatorGPM, ...(otoritasSource.koordinatorGPM as object || {}) },
      ketuaProdi: { ...base.otoritas.ketuaProdi, ...(otoritasSource.ketuaProdi as object || {}) },
      dekan: { ...base.otoritas.dekan, ...(otoritasSource.dekan as object || {}) },
    },
    deskripsi: data.deskripsi as string || base.deskripsi,
    cpl: cplSource,
    ik: ikSource,
    cpmk: cpmkSource,
    minggu: mingguSource.length === 16 ? mingguSource : base.minggu,
    referensi: referensiSource,
  }
}
