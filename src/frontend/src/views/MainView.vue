<script setup>
import { ref, onMounted, computed } from 'vue'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'
import '@/assets/base.css'

const MIN_LOADING_TIME = 1200 // 1.2 секунды

const specializations = ref([])
const loading = ref(true)

const search = ref('')

const selectedProfile = ref('')
const selectedSpecialization = ref('')

// ======================
// загрузка данных
// ======================
onMounted(async () => {
  const start = Date.now()

  try {
    const res = await http.get('/api/specializations')
    specializations.value = res.data
  } finally {
    const elapsed = Date.now() - start
    const remaining = MIN_LOADING_TIME - elapsed

    setTimeout(() => {
      loading.value = false
    }, remaining > 0 ? remaining : 0)
  }
})

// ======================
// уникальные профили
// ======================
const profiles = computed(() => {
  return [...new Set(specializations.value.map(s => s.profileName))]
})

// ======================
// специализации, зависящие от профиля
// ======================
const filteredByProfile = computed(() => {
  if (!selectedProfile.value) {
    return specializations.value
  }

  return specializations.value.filter(
    s => s.profileName === selectedProfile.value
  )
})

// ======================
// список специализаций для dropdown (зависит от профиля)
// ======================
const availableSpecializations = computed(() => {
  return filteredByProfile.value
})

// ======================
// финальная фильтрация карточек
// ======================
const filteredSpecializations = computed(() => {
  return availableSpecializations.value.filter(s => {
    const matchSearch =
      s.name.toLowerCase().includes(search.value.toLowerCase())

    const matchSpec =
      !selectedSpecialization.value ||
      s.id == selectedSpecialization.value

    return matchSearch && matchSpec
  })
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

    <!-- FILTERS -->
    <div class="filters">

      <!-- SEARCH -->
      <input
        v-model="search"
        type="text"
        placeholder="Поиск специализации..."
        class="search-input"
      />

      <!-- PROFILE -->
      <select v-model="selectedProfile" class="filter-select">
        <option value="">Все профили</option>
        <option v-for="p in profiles" :key="p" :value="p">
          {{ p }}
        </option>
      </select>

      <!-- SPECIALIZATION (зависит от профиля) -->
      <select v-model="selectedSpecialization" class="filter-select">
        <option value="">Все специализации</option>
        <option
          v-for="s in availableSpecializations"
          :key="s.id"
          :value="s.id"
        >
          {{ s.name }}
        </option>
      </select>

    </div>

    <!-- LOADING -->
    <div v-if="loading" class="loading-grid">
      <div class="spec-skeleton" v-for="n in 16" :key="n"></div>
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