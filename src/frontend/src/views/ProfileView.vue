<script setup>
import { ref, onMounted } from 'vue'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const user = ref(null)
const appointments = ref([])

const isEditing = ref(false)

const form = ref({
  firstName: '',
  middleName: '',
  lastName: '',
  birthDate: ''
})

onMounted(async () => {
  const res = await http.get('/api/profile')

  user.value = res.data.user
  appointments.value = res.data.appointments

  form.value = {
    firstName: user.value.firstName,
    middleName: user.value.middleName,
    lastName: user.value.lastName,
    birthDate: user.value.birthDate
  }
})

function openEdit() {
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
}

async function save() {
  await http.post('/api/profile', form.value)

  user.value = { ...user.value, ...form.value }
  isEditing.value = false
}
function formatDate(dateTime) {
  const date = new Date(dateTime)

  return date.toLocaleDateString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  })
}

function formatTime(dateTime) {
  const date = new Date(dateTime)

  return date.toLocaleTimeString('ru-RU', {
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>
<template>
  <AppHeader />

  <div class="container profile-page">

    <!-- HEADER -->
    <div class="page-header">
      <h1>Профиль пользователя</h1>
    </div>

    <!-- USER CARD -->
    <div class="user-card" v-if="user">

      <div class="user-info">
        <p><strong>Email:</strong> {{ user.email }}</p>
        <p><strong>Имя:</strong> {{ user.firstName }}</p>
        <p><strong>Отчество:</strong> {{ user.middleName }}</p>
        <p><strong>Фамилия:</strong> {{ user.lastName }}</p>
        <p><strong>Дата рождения:</strong> {{ user.birthDate }}</p>
        <p><strong>Роль:</strong> {{ user.role }}</p>
      </div>

      <button v-if="!isEditing" class="btn primary" @click="openEdit">
        Редактировать данные
      </button>

      <!-- EDIT FORM -->
      <div v-if="isEditing" class="edit-form">

        <div class="form-group">
          <label>Имя</label>
          <input v-model="form.firstName" type="text" />
        </div>

        <div class="form-group">
          <label>Отчество</label>
          <input v-model="form.middleName" type="text" />
        </div>

        <div class="form-group">
          <label>Фамилия</label>
          <input v-model="form.lastName" type="text" />
        </div>

        <div class="form-group">
          <label>Дата рождения</label>
          <input v-model="form.birthDate" type="date" />
        </div>

        <div class="form-actions">
          <button class="btn save-btn" @click="save">
            Сохранить
          </button>

          <button class="btn cancel-btn" @click="cancelEdit">
            Отмена
          </button>
        </div>

      </div>

    </div>

    <!-- APPOINTMENTS -->
    <div class="appointments-section">

      <h2>Мои записи</h2>

      <div v-if="appointments.length === 0" class="empty-state">
        У вас пока нет записей
      </div>

<div v-else class="appointments-grid">

    <div
        v-for="a in appointments"
        :key="a.id"
        class="appointment-card"
        @click="$router.push(`/appointments/${a.id}`)"
    >

        <div class="appointment-info">

        <p>
            <strong>Врач:</strong>
            {{ a.doctor.lastName + " " + a.doctor.firstName + " " + a.doctor.middleName }}
        </p>

        <p>
            <strong>Специализация:</strong>
            {{ a.doctor.specializationId }}
        </p>

        <p>
            <strong>Дата:</strong>
            {{ formatDate(a.timeSlot.startTime) }}
        </p>

        <p>
            <strong>Время:</strong>
            {{ formatTime(a.timeSlot.startTime) }} -
            {{ formatTime(a.timeSlot.endTime) }}
        </p>

        </div>

        <div class="appointment-status">
        <span class="status" :class="a.status.toLowerCase()">
            {{ a.status }}
        </span>
        </div>

    </div>

    </div>

    </div>

  </div>
</template>
<style scoped src="@/assets/profile.css"></style>