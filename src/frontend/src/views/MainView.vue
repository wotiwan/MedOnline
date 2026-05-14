<script setup>
import { ref, onMounted, computed } from 'vue'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'
import '@/assets/base.css'

const specializations = ref([])
const loading = ref(true)
const search = ref('')

onMounted(async () => {
  try {
    const res = await http.get('/api/specializations')
    specializations.value = res.data
  } finally {
    loading.value = false
  }
})

// ======================
// фильтрация на фронте
// ======================
const filteredSpecializations = computed(() => {
  if (!search.value.trim()) {
    return specializations.value
  }

  return specializations.value.filter(s =>
    s.name.toLowerCase().includes(search.value.toLowerCase())
  )
})
</script>

<template>
  <AppHeader />

  <div class="container">

    <!-- HERO -->
    <div class="hero">
      <div class="hero-icon">🩺</div>

      <div class="hero-text">
        <h1>Запись к врачу</h1>
        <p>Выберите или найдите нужную специализацию</p>
      </div>
    </div>

    <!-- SEARCH -->
    <div class="search-block">
      <input
        v-model="search"
        type="text"
        placeholder="Поиск специализации..."
        class="search-input"
      />
    </div>

    <!-- LOADING -->
    <div v-if="loading" class="loading-grid">
      <div class="spec-skeleton" v-for="n in 6" :key="n"></div>
    </div>

    <!-- EMPTY -->
    <div v-else-if="!filteredSpecializations.length" class="empty-state">
      Ничего не найдено
    </div>

    <!-- GRID -->
    <div v-else class="specializations-grid">

      <a
        v-for="s in filteredSpecializations"
        :key="s.id"
        :href="`/doctors?specializationId=${s.id}`"
        class="spec-card"
      >

        <div class="spec-icon">+</div>

        <div class="spec-card-content">
          <h3>{{ s.name }}</h3>
          <span class="spec-link">Выбрать →</span>
        </div>

      </a>

    </div>

  </div>
</template>

<style scoped src="@/assets/mainpage.css"></style>