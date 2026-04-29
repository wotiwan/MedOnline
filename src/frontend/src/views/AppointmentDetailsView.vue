<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()

const appointment = ref(null)

onMounted(async () => {
  const id = route.params.id
  const res = await http.get(`/api/appointments/${id}`)
  appointment.value = res.data
})

function formatDate(dateTime) {
  return new Date(dateTime).toLocaleDateString('ru-RU')
}

function formatTime(dateTime) {
  return new Date(dateTime).toLocaleTimeString('ru-RU', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function cancelAppointment() {
  await http.put(`/api/appointments/${appointment.value.id}/cancel`)
  appointment.value.status = 'CANCELLED'
}



</script>
<template>
  <AppHeader />

  <div class="container" v-if="appointment">

    <div class="page-header">
      <h1>Информация о записи</h1>
    </div>

    <div class="appointment-card">

      <p>
        <strong>Врач:</strong>
        {{ appointment.doctor.firstName }} {{ appointment.doctor.lastName }}
      </p>

      <p>
        <strong>Специализация:</strong>
        {{ appointment.doctor.specializationId }}
      </p>

      <p>
        <strong>Дата:</strong>
        {{ formatDate(appointment.timeSlot.startTime) }}
      </p>

      <p>
        <strong>Время:</strong>
        {{ formatTime(appointment.timeSlot.startTime) }}
        -
        {{ formatTime(appointment.timeSlot.endTime) }}
      </p>

      <p>
        <strong>Заключение:</strong>
        {{
          appointment.consultationResult
            ? appointment.consultationResult
            : 'Появится позже'
        }}
      </p>

      <p>
        <strong>Статус:</strong>
        <span class="status" :class="appointment.status.toLowerCase()">
          {{ appointment.status }}
        </span>
      </p>

      <button
        v-if="appointment.status === 'BOOKED'"
        class="btn danger"
        @click="cancelAppointment"
      >
        Отменить запись
      </button>

    </div>

  </div>
</template>
<style scoped src="@/assets/appointment-details.css"></style>