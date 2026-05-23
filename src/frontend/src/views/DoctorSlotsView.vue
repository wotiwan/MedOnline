<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()

const isGuest = ref(false)

const guestForm = ref({
  firstName: '',
  lastName: '',
  birthDate: '',
  phone: ''
})

const doctorId = route.query.doctorId

const doctor = ref(null)

const date = ref(new Date().toISOString().split('T')[0])
const slots = ref([])

const selectedSlot = ref(null)

const showModal = ref(false)

const appointmentId = ref(null)
const loading = ref(false)

const slotsLoading = ref(true)

const MIN_LOADING_TIME = 1200

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
  slotsLoading.value = true

  const start = Date.now()

  try {
    const res = await http.get(`/api/doctors/${doctorId}/slots`, {
      params: { date: date.value }
    })

    slots.value = res.data.timeSlots

  } finally {
    const elapsed = Date.now() - start
    const remaining = MIN_LOADING_TIME - elapsed

    setTimeout(() => {
      slotsLoading.value = false
    }, remaining > 0 ? remaining : 0)
  }
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
// quick dates
// ======================
function setToday() {
  date.value = new Date().toISOString().split('T')[0]
}

function setTomorrow() {
  const d = new Date()

  d.setDate(d.getDate() + 1)

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

    // Гость
    if (isGuest.value) {
      setTimeout(() => {
        appointmentId.value = 'GUEST_FAKE_' + Date.now()

        selectedSlot.value.isBooked = true
        loadSlots()

        loading.value = false
      }, 600)

      return
    }

    // обычный пользователь
    const res = await http.post('/api/appointments/book', null, {
      params: {
        slotId: selectedSlot.value.id
      }
    })

    appointmentId.value = res.data

    selectedSlot.value.isBooked = true
    await loadSlots()

  } finally {
    loading.value = false
  }
}

// ======================
// оплата
// ======================
async function pay() {
  const returnUrl =
    window.location.origin + `/appointments/${appointmentId.value}`

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
  checkAuth()
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

// ======================
// прошедший слот
// ======================
function isPastSlot(slot) {
  return new Date(slot.startTime) <= new Date()
}

// ======================
// Проверка авторизации
// ======================
function checkAuth() {
  const token = localStorage.getItem('token')
  isGuest.value = !token
}
</script>

<template>
  <AppHeader />

  <div class="container">

    <!-- HERO -->
    <div v-if="doctor" class="doctor-hero">

      <div class="doctor-avatar">
        👨‍⚕️
      </div>

      <div class="doctor-meta">

        <h1>
          {{ doctor.lastName }}
          {{ doctor.firstName }}
          {{ doctor.middleName }}
        </h1>

        <p class="doctor-price">
          Стоимость приёма:
          <strong>
            {{
              doctor.consultationPrice
                ? doctor.consultationPrice + ' ₽'
                : 'Бесплатно'
            }}
          </strong>
        </p>

        <p
          v-if="doctor.description"
          class="doctor-description"
        >
          {{ doctor.description }}
        </p>

      </div>

    </div>

    <!-- SKELETON -->
    <div v-else class="doctor-skeleton"></div>

    <!-- CONTROLS -->
    <div class="booking-controls">

      <div>

        <input
          type="date"
          v-model="date"
          class="date-input"
        />

        <div class="quick-dates">

          <button class="quick-btn" @click="setToday">
            Сегодня
          </button>

          <button class="quick-btn" @click="setTomorrow">
            Завтра
          </button>

        </div>

      </div>

      <div class="date-nav">

        <button class="quick-btn" @click="changeDate(-1)">
          ←
        </button>

        <span>{{ date }}</span>

        <button class="quick-btn" @click="changeDate(1)">
          →
        </button>

      </div>

    </div>

    <!-- SLOTS -->
    <div class="slots-grid">

      <!-- SKELETON -->
      <template v-if="slotsLoading">

        <div
          v-for="n in 32"
          :key="n"
          class="slot-skeleton"
        ></div>

      </template>

      <!-- EMPTY -->
      <div
        v-else-if="!slots.length"
        class="empty-state"
      >
        Нет доступных слотов
      </div>

      <!-- REAL SLOTS -->
      <template v-else v-for="slot in slots" :key="slot.id">

        <!-- свободный -->
        <button
          v-if="!slot.isBooked && !isPastSlot(slot)"
          class="slot free"
          @click="openModal(slot)"
        >
          {{ formatTime(slot.startTime) }}
          —
          {{ formatTime(slot.endTime) }}
        </button>

        <!-- прошедший -->
        <div
          v-else-if="isPastSlot(slot)"
          class="slot expired"
        >
          {{ formatTime(slot.startTime) }}
          —
          {{ formatTime(slot.endTime) }}
        </div>

        <!-- занятый -->
        <div
          v-else
          class="slot booked"
        >
          {{ formatTime(slot.startTime) }}
          —
          {{ formatTime(slot.endTime) }}
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

      <template v-if="!appointmentId">

        <h3>Подтверждение записи</h3>

        <p>
          <strong>Врач:</strong>
          {{ doctor.lastName }}
          {{ doctor.firstName }}
        </p>

        <p>
          <strong>Время:</strong>
          {{ formatTime(selectedSlot.startTime) }}
          —
          {{ formatTime(selectedSlot.endTime) }}
        </p>

        <p>
          <strong>Стоимость:</strong>
          {{
            doctor.consultationPrice
              ? doctor.consultationPrice + ' ₽'
              : 'Бесплатно'
          }}
        </p>
        
        <!-- Гость -->
        <div v-if="isGuest" class="guest-form">

          <h4>Введите данные для записи</h4>

          <input v-model="guestForm.firstName" placeholder="Имя" />
          <input v-model="guestForm.lastName" placeholder="Фамилия" />
          <input v-model="guestForm.birthDate" type="date" />
          <input v-model="guestForm.phone" placeholder="Телефон" />
          
        </div>

        <button
          class="btn"
          :disabled="loading || (isGuest && (
            !guestForm.firstName ||
            !guestForm.lastName ||
            !guestForm.birthDate ||
            !guestForm.phone
          ))"
          @click="book"
        >
          {{
            loading
              ? 'Создание...'
              : 'Подтвердить запись'
          }}
        </button>

      </template>

      <!-- SUCCESS -->
      <template v-else>

        <div class="success-state">

          <div class="success-icon">
            ✓
          </div>

          <h3>Запись успешно создана</h3>

          <p>
            Ваш приём успешно забронирован
          </p>

          <button
            class="btn primary"
            @click="pay"
          >
            Оплатить приём
          </button>

        </div>

      </template>

      <button
        class="btn secondary"
        @click="closeModal"
      >
        Закрыть
      </button>

    </div>
  </div>

</template>

<style scoped src="@/assets/DoctorSlots.css"></style>