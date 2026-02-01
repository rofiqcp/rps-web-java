import axios, { type AxiosInstance } from 'axios'
import type { RPSData, GenerateRequest, ApiResponse } from '@/types/rps'

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api'

const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 300000, // 5 minutes for AI generation
})

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    console.log(`[API] ${config.method?.toUpperCase()} ${config.url}`)
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
apiClient.interceptors.response.use(
  (response) => {
    console.log(`[API] Response ${response.status}`)
    return response
  },
  (error) => {
    console.error('[API] Error:', error.response?.data || error.message)
    return Promise.reject(error)
  }
)

export const api = {
  // Health check
  async health(): Promise<{ status: string; model: string }> {
    const response = await apiClient.get('/health')
    return response.data
  },

  // Generate RPS content
  async generate(request: GenerateRequest): Promise<ApiResponse<Partial<RPSData>>> {
    const response = await apiClient.post('/generate', request)
    return response.data
  },

  // Export to DOCX
  async exportDocx(rpsData: RPSData): Promise<Blob> {
    const response = await apiClient.post('/export', {
      rpsData,
      meta: {
        nama: rpsData.identitas.nama,
        kode: rpsData.identitas.kode,
        sks: rpsData.identitas.sks,
        semester: rpsData.identitas.semester,
        status: rpsData.identitas.status,
        prasyarat: rpsData.identitas.prasyarat,
        koordinatorMK: rpsData.otoritas.koordinatorMK,
        koordinatorGPM: rpsData.otoritas.koordinatorGPM,
        ketuaProdi: rpsData.otoritas.ketuaProdi,
        dekan: rpsData.otoritas.dekan,
      }
    }, {
      responseType: 'blob'
    })
    return response.data
  },

  // Get sample data
  async getSample(): Promise<ApiResponse<RPSData>> {
    const response = await apiClient.get('/sample')
    return response.data
  }
}

export default api
