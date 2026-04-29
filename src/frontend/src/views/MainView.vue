<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'
import '@/assets/base.css'

const specializations = ref([])

onMounted(async () => {
  const res = await http.get('/api/main')
  specializations.value = res.data
})
</script>

<template>
  <AppHeader />
  <div class="container">
    <div class="page-header">
      <h1>Запись к врачу</h1>
      <p>Выберите специализацию, чтобы продолжить</p>
    </div>
    <div class="specializations-grid">
      <a
        v-for="s in specializations"
        :key="s.id"
        :href="`/doctors?specializationId=${s.id}`"
        class="spec-card"
      >
        <div class="spec-card-content">
          <h3>{{ s.name }}</h3>
          <span class="spec-link">Выбрать →</span>
        </div>
      </a>
    </div>
  </div>
</template>

<style scoped src="@/assets/mainpage.css"></style>