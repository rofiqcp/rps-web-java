<script setup lang="ts">
withDefaults(defineProps<{
  variant?: 'primary' | 'secondary' | 'accent' | 'success' | 'danger' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  disabled?: boolean
}>(), {
  variant: 'primary',
  size: 'md',
  loading: false,
  disabled: false,
})

const variantClasses = {
  primary: 'btn-primary',
  secondary: 'btn-secondary',
  accent: 'btn-accent',
  success: 'btn-success',
  danger: 'btn-danger',
  ghost: 'btn-ghost',
}

const sizeClasses = {
  sm: 'px-3 py-1.5 text-xs',
  md: 'px-4 py-2.5 text-sm',
  lg: 'px-6 py-3 text-base',
}
</script>

<template>
  <button
    :class="['btn', variantClasses[variant], sizeClasses[size]]"
    :disabled="disabled || loading"
  >
    <span v-if="loading" class="spinner"></span>
    <slot />
  </button>
</template>
