<script setup lang="ts">
import { BookOpenIcon, AcademicCapIcon } from '@heroicons/vue/24/outline'
</script>

<template>
  <header class="bg-gradient-to-r from-primary-600 via-primary-700 to-accent-600 text-white shadow-xl sticky top-0 z-50">
    <div class="container mx-auto px-4 py-4 max-w-7xl">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <div class="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center backdrop-blur-sm">
            <BookOpenIcon class="w-7 h-7 text-white" />
          </div>
          <div>
            <h1 class="text-xl font-bold tracking-tight">RPS Generator</h1>
            <p class="text-primary-100 text-sm">Sistem Pembuatan Rencana Pembelajaran Semester</p>
          </div>
        </div>
        <div class="hidden md:flex items-center gap-3 text-right">
          <div class="text-sm">
            <p class="font-medium">Universitas Diponegoro</p>
            <p class="text-primary-200 text-xs">Sekolah Vokasi</p>
          </div>
          <div class="w-10 h-10 bg-white/20 rounded-xl flex items-center justify-center backdrop-blur-sm">
            <AcademicCapIcon class="w-6 h-6 text-white" />
          </div>
        </div>
      </div>
    </div>
    <!-- Decorative gradient line -->
    <div class="h-1 bg-gradient-to-r from-cyan-400 via-emerald-400 to-violet-400"></div>
  </header>
</template>
