<script setup lang="ts">
import { XMarkIcon, CheckCircleIcon, ExclamationTriangleIcon, InformationCircleIcon, XCircleIcon } from '@heroicons/vue/24/outline'

defineProps<{
  type: 'success' | 'error' | 'warning' | 'info'
  message: string
  dismissible?: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const icons = {
  success: CheckCircleIcon,
  error: XCircleIcon,
  warning: ExclamationTriangleIcon,
  info: InformationCircleIcon,
}

const colors = {
  success: 'bg-emerald-50 text-emerald-800 border-emerald-200',
  error: 'bg-red-50 text-red-800 border-red-200',
  warning: 'bg-amber-50 text-amber-800 border-amber-200',
  info: 'bg-blue-50 text-blue-800 border-blue-200',
}

const iconColors = {
  success: 'text-emerald-500',
  error: 'text-red-500',
  warning: 'text-amber-500',
  info: 'text-blue-500',
}
</script>

<template>
  <div 
    :class="['alert animate-slide-up border', colors[type]]"
    role="alert"
  >
    <component :is="icons[type]" :class="['w-5 h-5 flex-shrink-0', iconColors[type]]" />
    <p class="flex-1 text-sm">{{ message }}</p>
    <button 
      v-if="dismissible !== false"
      @click="emit('close')"
      class="p-1 rounded-lg hover:bg-black/5 transition-colors"
    >
      <XMarkIcon class="w-4 h-4" />
    </button>
  </div>
</template>
