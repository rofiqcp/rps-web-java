/**
 * RPS (Rencana Pembelajaran Semester) Type Definitions
 */

export interface Identitas {
  kode: string
  nama: string
  sks: number
  semester: number
  status: string
  prasyarat: string
}

export interface Institusi {
  programStudi: string
  fakultas: string
  universitas: string
}

export interface Personil {
  nama: string
  nip: string
  jabatan: string
}

export interface Otoritas {
  koordinatorMK: Personil
  koordinatorGPM: Personil
  ketuaProdi: Personil
  dekan: Personil
}

export interface CPL {
  kode: string
  pernyataan: string
}

export interface IK {
  kode: string
  pernyataan: string
  mapping_cpl: string
  mapping_cpmk: string
}

export interface CPMK {
  kode: string
  pernyataan: string
  mapping_cpl: string[]
  n1: number
  n2: number
  n3: number
  n4: number
  n5: number
}

export interface Minggu {
  minggu: number
  mapping_cpmk: string
  materi: string
  kemampuan_akhir: string
  indikator: string
  metode: string
  waktu: number
  pengalaman_belajar: string
  penilaian: string
  bobot: number
  referensi: string
}

export interface Referensi {
  nomor: number
  judul: string
  penulis: string
  tahun: number
  penerbit: string
  kota: string
  isbn: string
  jenis: 'Utama' | 'Pendukung'
}

export interface RPSData {
  id?: string
  createdAt?: string
  updatedAt?: string
  identitas: Identitas
  institusi: Institusi
  otoritas: Otoritas
  deskripsi: string
  deskripsiSingkat?: string
  cpl: CPL[]
  ik: IK[]
  cpmk: CPMK[]
  minggu: Minggu[]
  referensi: Referensi[]
}

export type GenerateType = 'full' | 'description' | 'cpl' | 'cpmk' | 'weeklyPlan' | 'references'
export type JenisMK = 'teori' | 'praktikum' | 'campuran'

export interface GenerateRequest {
  type: GenerateType
  identitas: Identitas
  institusi?: Institusi
  jenisMK?: JenisMK
  additionalContext?: string
  deskripsi?: string
  cpl?: CPL[]
  cpmk?: CPMK[]
}

export interface ApiResponse<T> {
  success: boolean
  message?: string
  data?: T
  error?: string
}

export const createEmptyRPS = (): RPSData => ({
  identitas: {
    kode: '',
    nama: '',
    sks: 3,
    semester: 1,
    status: 'Mata Kuliah Wajib',
    prasyarat: '-',
  },
  institusi: {
    programStudi: 'Sarjana Terapan Teknologi Rekayasa Otomasi',
    fakultas: 'Sekolah Vokasi',
    universitas: 'Universitas Diponegoro',
  },
  otoritas: {
    koordinatorMK: { nama: '', nip: '', jabatan: 'Koordinator Mata Kuliah' },
    koordinatorGPM: { nama: '', nip: '', jabatan: 'Koordinator GPM' },
    ketuaProdi: { nama: '', nip: '', jabatan: 'Ketua Prodi' },
    dekan: { nama: '', nip: '', jabatan: 'Dekan' },
  },
  deskripsi: '',
  cpl: [],
  ik: [],
  cpmk: [],
  minggu: Array.from({ length: 16 }, (_, i) => ({
    minggu: i + 1,
    mapping_cpmk: '',
    materi: i + 1 === 8 ? 'Ujian Tengah Semester (UTS)' : i + 1 === 16 ? 'Ujian Akhir Semester (UAS)' : '',
    kemampuan_akhir: '',
    indikator: '',
    metode: '',
    waktu: 150,
    pengalaman_belajar: '',
    penilaian: '',
    bobot: i === 8 || i === 16 ? 0 : 0,
    referensi: '',
  })),
  referensi: [],
})
