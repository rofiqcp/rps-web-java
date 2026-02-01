# RPS Generator - Java + Vue.js

Sistem Generator Rencana Pembelajaran Semester (RPS) dengan AI untuk Universitas Diponegoro.

## 🏗️ Struktur Proyek

```
rps-web-java/
├── backend/                 # Spring Boot Backend
│   ├── src/main/java/
│   │   └── id/ac/undip/rps/
│   │       ├── config/      # Konfigurasi CORS, OpenAI
│   │       ├── controller/  # REST Controllers
│   │       ├── dto/         # Data Transfer Objects
│   │       └── service/     # Business Logic
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
└── frontend/                # Vue.js Frontend
    ├── src/
    │   ├── components/      # Vue Components
    │   ├── stores/          # Pinia State Management
    │   ├── services/        # API Services
    │   ├── types/           # TypeScript Types
    │   └── assets/          # Static Assets
    ├── package.json
    └── vite.config.ts
```

## 🚀 Cara Menjalankan

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+
- npm 9+

### Backend (Spring Boot)

```bash
cd backend

# Set environment variable untuk OpenAI
export OPENAI_API_KEY=your_api_key_here

# Build dan jalankan
mvn clean install
mvn spring-boot:run
```

Backend berjalan di: `http://localhost:8080`

### Frontend (Vue.js)

```bash
cd frontend

# Install dependencies
npm install

# Development mode
npm run dev

# Production build
npm run build
```

Frontend berjalan di: `http://localhost:5173`

## 📋 Fitur

### 1. Generator AI
- **Generate RPS Lengkap**: Membuat seluruh dokumen RPS secara otomatis
- **Generate CPL**: Membuat Capaian Pembelajaran Lulusan
- **Generate CPMK**: Membuat Capaian Pembelajaran Mata Kuliah
- **Generate Rencana Mingguan**: Membuat 16 pertemuan semester
- **Generate Referensi**: Membuat daftar pustaka yang relevan

### 2. Editor Interaktif
- Form Identitas Mata Kuliah
- Manajemen CPL dan CPMK
- Indikator Kinerja (IK)
- Tabel Rencana Mingguan (16 minggu)
- Matriks Penilaian (N1-N5)
- Daftar Referensi

### 3. Export & Import
- **Export DOCX**: Download dokumen RPS dalam format Word
- **Save JSON**: Simpan data ke file JSON
- **Load JSON**: Muat data dari file JSON
- **Preview JSON**: Lihat data dalam format JSON

## 🔧 API Endpoints

### Generate API
```
POST /api/generate
Content-Type: application/json

{
  "type": "full|cpl|cpmk|weeklyPlan|references|description",
  "identitas": {...},
  "institusi": {...},
  "jenisMK": "teori|praktikum|campuran",
  "additionalContext": "..."
}
```

### Export API
```
POST /api/export/docx
Content-Type: application/json

{
  "rpsData": {...}
}
```

### Health Check
```
GET /api/health
```

### Sample Data
```
GET /api/sample
```

## 🎨 Tech Stack

### Backend
- Spring Boot 3.2.1
- OpenAI Java SDK (theokanning)
- Apache POI 5.2.5 (DOCX export)
- Lombok
- Maven

### Frontend
- Vue 3.4 (Composition API)
- TypeScript
- Vite 5.0
- Pinia (State Management)
- Tailwind CSS 3.4
- HeadlessUI
- Heroicons
- Vue Router 4
- Axios

## 📝 Environment Variables

### Backend (.env atau application.properties)
```properties
OPENAI_API_KEY=sk-xxx
OPENAI_MODEL=gpt-4o-mini
```

### Frontend (.env)
```env
VITE_API_URL=http://localhost:8080
```

## 📄 License

MIT License - Universitas Diponegoro

---

Developed with ❤️ for UNDIP
