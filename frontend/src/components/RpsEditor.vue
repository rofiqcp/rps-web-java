<script setup lang="ts">
import { ref } from 'vue'
import ActionBar from './editor/ActionBar.vue'
import PromptSection from './editor/PromptSection.vue'
import IdentitySection from './editor/IdentitySection.vue'
import CplSection from './editor/CplSection.vue'
import CpmkSection from './editor/CpmkSection.vue'
import IkSection from './editor/IkSection.vue'
import WeeklyPlanSection from './editor/WeeklyPlanSection.vue'
import AssessmentSection from './editor/AssessmentSection.vue'
import ReferencesSection from './editor/ReferencesSection.vue'
import JsonPreview from './editor/JsonPreview.vue'
import { CodeBracketIcon, ChevronDownIcon, ChevronUpIcon } from '@heroicons/vue/24/outline'

const showJsonPreview = ref(false)
</script>

<template>
  <div class="space-y-6">
    <!-- Action Buttons -->
    <ActionBar />

    <!-- Prompt RPS Mantap -->
    <PromptSection />

    <!-- Identitas Mata Kuliah -->
    <IdentitySection />

    <!-- CPL Section -->
    <CplSection />

    <!-- CPMK Section -->
    <CpmkSection />

    <!-- IK Section -->
    <IkSection />

    <!-- Weekly Plan Section -->
    <WeeklyPlanSection />

    <!-- Assessment Section -->
    <AssessmentSection />

    <!-- References Section -->
    <ReferencesSection />

    <!-- JSON Preview -->
    <div class="card">
      <button 
        @click="showJsonPreview = !showJsonPreview"
        class="w-full card-header flex items-center justify-between cursor-pointer hover:bg-slate-100 transition-colors rounded-t-xl"
      >
        <span class="font-semibold text-slate-700 flex items-center gap-3">
          <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-slate-600 to-slate-800 text-white flex items-center justify-center">
            <CodeBracketIcon class="w-4 h-4" />
          </div>
          Preview Data JSON
        </span>
        <component :is="showJsonPreview ? ChevronUpIcon : ChevronDownIcon" class="w-5 h-5 text-slate-400" />
      </button>
      <div v-if="showJsonPreview">
        <JsonPreview />
      </div>
    </div>
  </div>
</template>
