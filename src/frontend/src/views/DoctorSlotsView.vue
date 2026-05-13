<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()

const doctorId = route.query.doctorId

const doctor = ref(null)

const date = ref(new Date().toISOString().split('T')[0])
const slots = ref([])

const selectedSlot = ref(null)

const showModal = ref(false)

// 👇 новое состояние
const appointmentId = ref(null)
const loading = ref(false)

// ======================
// загрузка врача
// ======================
async function loadDoctor() {
  const res = await http.get(`/api/doctors/${doctorId}`)
  doctor.value = res.data
}

// ======================
// загрузка слотов
// ======================
async function loadSlots() {
  const res = await http.get(`/api/doctors/${doctorId}/slots`, {
    params: { date: date.value }
  })
  slots.value = res.data.timeSlots
}

// ======================
// смена даты
// ======================
function changeDate(offset) {
  const d = new Date(date.value)
  d.setDate(d.getDate() + offset)
  date.value = d.toISOString().split('T')[0]
}

// ======================
// открыть модалку
// ======================
function openModal(slot) {
  selectedSlot.value = slot
  showModal.value = true
}

// ======================
// закрыть модалку
// ======================
function closeModal() {
  showModal.value = false
  selectedSlot.value = null
  appointmentId.value = null
}

// ======================
// создание записи
// ======================
async function book() {
  loading.value = true

  try {
    const res = await http.post('/api/appointments/book', null, {
      params: {
        slotId: selectedSlot.value.id
      }
    })

    appointmentId.value = res.data
  } finally {
    loading.value = false
  }
}

// ======================
// оплата
// ======================
async function pay() {
  const returnUrl = window.location.origin + `/appointments/${appointmentId.value}`

  const res = await http.post(
    `/api/payments/${appointmentId.value}`,
    null,
    {
      params: {
        returnUrl
      }
    }
  )

  const url = res.data

  window.location.href = url
}

// ======================
// lifecycle
// ======================
onMounted(async () => {
  await Promise.all([
    loadDoctor(),
    loadSlots()
  ])
})

watch(date, loadSlots)

// ======================
// формат времени
// ======================
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

    <!-- HEADER -->
    <div class="page-header">
      <h1>Запись к врачу</h1>

      <p v-if="doctor">
        {{ doctor.lastName }} {{ doctor.firstName }} {{ doctor.middleName }}
      </p>

      <p v-else>
        Загрузка врача...
      </p>
    </div>

    <!-- DATE CONTROL -->
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

    <!-- SLOTS -->
    <div class="slots-grid">

      <div v-if="!slots.length" class="empty-state">
        Нет доступных слотов
      </div>

      <template v-for="slot in slots" :key="slot.id">

        <button
          v-if="!slot.isBooked"
          class="slot free"
          @click="openModal(slot)"
        >
          {{ formatTime(slot.startTime) }} — {{ formatTime(slot.endTime) }}
        </button>

        <div v-else class="slot booked">
          {{ formatTime(slot.startTime) }} — {{ formatTime(slot.endTime) }}
        </div>

      </template>

    </div>

  </div>

  <!-- MODAL -->
  <div
    v-if="showModal"
    class="modal"
    @click.self="closeModal"
  >
    <div class="modal-content">

      <h3>Запись к врачу</h3>

      <!-- STEP 1: booking -->
      <template v-if="!appointmentId">

        <p v-if="doctor">
          <strong>Врач:</strong>
          {{ doctor.lastName }} {{ doctor.firstName }}
        </p>

        <p>
          <strong>Время:</strong>
          {{ formatTime(selectedSlot.startTime) }} -
          {{ formatTime(selectedSlot.endTime) }}
        </p>
        
        <p v-if="doctor">
          <strong>Стоимость записи:</strong>
            {{ doctor.consultationPrice ? doctor.consultationPrice + ' руб.' : 'бесплатно' }}
        </p>
        
        <button
          class="btn"
          :disabled="loading"
          @click="book"
        >
          {{ loading ? 'Создание...' : 'Подтвердить запись' }}
        </button>

      </template>

      <!-- STEP 2: payment -->
      <template v-else>

        <p class="success-text">
          Запись успешно создана
        </p>

        <button
          class="btn primary"
          @click="pay"
        >
          Оплатить приём
        </button>

      </template>

      <button class="btn secondary" @click="closeModal">
        Закрыть
      </button>

    </div>
  </div>

</template>

<style scoped src="@/assets/DoctorSlots.css"></style>