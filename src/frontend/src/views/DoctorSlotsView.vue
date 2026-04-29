<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()

const doctorId = route.query.doctorId

const doctor = ref(null) // 👈 ДОБАВИЛИ
const date = ref(new Date().toISOString().split('T')[0])
const slots = ref([])
const selectedSlot = ref(null)
const showModal = ref(false)

// 👇 загрузка врача
async function loadDoctor() {
  const res = await http.get(`/api/doctors/${doctorId}`)
  doctor.value = res.data
}

// загрузка слотов
async function loadSlots() {
  const res = await http.get(`/api/doctors/${doctorId}/slots`, {
    params: { date: date.value }
  })
  slots.value = res.data.timeSlots
}

// смена даты
function changeDate(offset) {
  const d = new Date(date.value)
  d.setDate(d.getDate() + offset)
  date.value = d.toISOString().split('T')[0]
}

// открытие модалки
function openModal(slot) {
  selectedSlot.value = slot
  showModal.value = true
}

// закрытие
function closeModal() {
  showModal.value = false
}

// запись
async function book() {
  await http.post('/api/appointments/book', null, {
    params: {
      slotId: selectedSlot.value.id
    }
  })

  showModal.value = false
  loadSlots()
}

// 👇 грузим ПАРАЛЛЕЛЬНО
onMounted(async () => {
  await Promise.all([
    loadDoctor(),
    loadSlots()
  ])
})

watch(date, loadSlots)

// формат времени
function formatTime(dateTime) {
  return new Date(dateTime).toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit'
  })
}

</script>

<template>
  <AppHeader />

  <div class="container">

    <!-- заголовок -->
    <div class="page-header">
        <h1>Запись к врачу</h1>

        <p v-if="doctor">
            {{ doctor.lastName }} {{ doctor.firstName }} {{ doctor.middleName }}
        </p>

        <p v-else>
            Загрузка врача...
        </p>
    </div>

    <!-- контролы -->
    <div class="booking-controls">

      <input
        type="date"
        v-model="date"
        class="date-input"
      />

      <div class="date-nav">
        <button @click="changeDate(-1)">←</button>
        <span>{{ date }}</span>
        <button @click="changeDate(1)">→</button>
      </div>

    </div>

    <!-- слоты -->
    <div class="slots-grid">

      <div v-if="!slots.length" class="empty-state">
        Нет доступных слотов
      </div>

      <template v-for="slot in slots" :key="slot.id">

        <!-- свободный -->
        <button
          v-if="!slot.isBooked"
          class="slot free"
          :class="{ selected: selectedSlot?.id === slot.id }"
          @click="openModal(slot)"
        >
          {{ formatTime(slot.startTime) }} — {{ formatTime(slot.endTime) }}
        </button>

        <!-- занятый -->
        <div v-else class="slot booked">
          {{ formatTime(slot.startTime) }} — {{ formatTime(slot.endTime) }}
        </div>

      </template>

    </div>

  </div>

  <!-- МОДАЛКА -->
  <div
    v-if="showModal"
    class="modal"
    @click.self="closeModal"
  >
    <div class="modal-content">

      <h3>Подтверждение записи</h3>
      
        <p v-if="doctor">
            <strong>Врач:</strong>
            {{ doctor.lastName }} {{ doctor.firstName }}
        </p>
      
      <p>
        <strong>Время:</strong>
        {{ formatTime(selectedSlot.startTime) }} -
        {{ formatTime(selectedSlot.endTime) }}
      </p>

      <button class="btn" @click="book">
        Подтвердить
      </button>

      <button class="btn secondary" @click="closeModal">
        Отмена
      </button>

    </div>
  </div>

</template>
<style scoped src="@/assets/DoctorSlots.css"></style>