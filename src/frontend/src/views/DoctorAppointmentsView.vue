<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()

const appointments = ref([])
const loading = ref(true)

async function loadAppointments() {
  try {
    const res = await http.get('/api/doctor/appointments')
    appointments.value = res.data
    console.log(res.data)
  } finally {
    loading.value = false
  }
}

function openAppointment(id) {
  router.push(`/doctor/appointments/${id}`)
}

function formatTime(dateTime) {
  return new Date(dateTime).toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit'
  })
}

function statusText(status) {
  switch (status) {
    case 'BOOKED':
      return 'Запланирована'
    case 'CANCELLED':
      return 'Отменена'
    case 'COMPLETED':
      return 'Завершена'
    default:
      return status
  }
}

onMounted(loadAppointments)
</script>

<template>
  <AppHeader />

  <div class="container">
    <div class="page-header">
      <h1>Записи на сегодня</h1>
    </div>

    <div v-if="loading" class="empty-state">
      <p>Загрузка...</p>
    </div>

    <div v-else-if="!appointments.length" class="empty-state">
      <p>Сегодня нет записей</p>
    </div>

    <div v-else class="appointments-grid">
      <div
        v-for="a in appointments"
        :key="a.id"
        class="appointment-card"
      >
        <p>
          <b>Пациент:</b>
          {{ a.user.firstName }} {{ a.user.lastName }}
        </p>

        <p>
          <b>Время:</b>
          {{ formatTime(a.timeSlot.startTime) }}
        </p>

        <p>
          <b>Статус:</b>

          <span
            class="status"
            :class="a.status.toLowerCase()"
          >
            {{ statusText(a.status) }}
          </span>
        </p>

        <button
          class="btn primary"
          @click="openAppointment(a.id)"
        >
          Открыть
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped src="@/assets/doctor-appointments.css"></style>