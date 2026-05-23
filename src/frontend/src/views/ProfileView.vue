<script setup>
import { ref, onMounted, computed } from 'vue'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const user = ref(null)
const appointments = ref([])

const activeTab = ref('future') // 'future' | 'history'
const tabLoading = ref(false)

const isEditing = ref(false)

const form = ref({
  firstName: '',
  middleName: '',
  lastName: '',
  birthDate: ''
})

// ======================
// загрузка профиля
// ======================

const specializationsMap = ref({})

onMounted(async () => {
  tabLoading.value = true

  const res = await http.get('/api/profile')

  setTimeout(() => {
    tabLoading.value = false
  }, 600)
  
  console.log(res.data)

  user.value = res.data.user
  appointments.value = res.data.appointments

  form.value = {
    firstName: user.value.firstName,
    middleName: user.value.middleName,
    lastName: user.value.lastName,
    birthDate: user.value.birthDate
  }

  // ======================
  // specialization ids
  // ======================

  const specializationIds = [
    ...new Set(
      appointments.value.map(
        a => a.doctor.specializationId
      )
    )
  ]

  // ======================
  // загрузка specialization
  // ======================

  const responses = await Promise.all(
    specializationIds.map(id =>
      http.get(`/api/specializations/${id}`)
    )
  )

  // ======================
  // map
  // ======================

  specializationsMap.value = responses.reduce(
    (acc, res) => {
      acc[res.data.id] = res.data.name
      return acc
    },
    {}
  )
})

// будущие записи (активные)
const futureAppointments = computed(() => {
  const now = new Date()

  return appointments.value.filter(a => {
    const start = new Date(a.timeSlot.startTime)

    return (
      (a.status === 'BOOKED' || a.status === 'CONFIRMED') &&
      start > now
    )
  })
})

// завершённые (отменённые + завершённые приёмы)
const finishedAppointments = computed(() => {
  return appointments.value.filter(a =>
    a.status === 'CANCELLED' ||
    a.status === 'COMPLETED'
  )
})

// ======================
// редактирование профиля
// ======================
function openEdit() {
  isEditing.value = true
}

function cancelEdit() {
  isEditing.value = false
}

async function save() {
  await http.put('/api/profile', form.value)

  user.value = { ...user.value, ...form.value }
  isEditing.value = false
}

// ======================
// формат даты/времени
// ======================
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

// ======================
// Платёжная логика
// ======================
function getPaymentStatus(appointment) {
  const payments = appointment.payments || []

  // бесплатно
  if (!appointment.price || appointment.price === 0) {
    return 'БЕСПЛАТНО'
  }

  // ошибка возврата
  const hasRefundFailed = payments.some(
    p => p.status === 'REFUND_FAILED'
  )

  if (hasRefundFailed) {
    return 'ОШИБКА ВОЗВРАТА'
  }

  // возврат
  const hasRefunded = payments.some(
    p => p.status === 'REFUNDED'
  )

  if (hasRefunded) {
    return 'ВОЗВРАЩЕНО'
  }

  // успешно оплачено
  const hasSucceeded = payments.some(
    p => p.status === 'SUCCEEDED'
  )

  if (hasSucceeded) {
    return 'ОПЛАЧЕНО'
  }

  return 'НЕ ОПЛАЧЕНО'
}

function getPaymentClass(appointment) {
  const payments = appointment.payments || []

  // бесплатно
  if (!appointment.price || appointment.price === 0) {
    return 'free'
  }

  // ошибка возврата
  if (
    payments.some(p => p.status === 'REFUND_FAILED')
  ) {
    return 'cancelled'
  }

  // возврат
  if (
    payments.some(p => p.status === 'REFUNDED')
  ) {
    return 'free'
  }

  // оплачено
  if (
    payments.some(p => p.status === 'SUCCEEDED')
  ) {
    return 'booked'
  }

  return 'cancelled'
}

function getAppointmentStatusLabel(status) {
  const map = {
    BOOKED: 'ЗАПЛАНИРОВАНА',
    CANCELLED: 'ОТМЕНЕНА',
    COMPLETED: 'ЗАВЕРШЕНА'
  }

  return map[status] || status
}

async function switchTab(tab) {
  if (activeTab.value === tab) return

  tabLoading.value = true

  activeTab.value = tab

  await new Promise(resolve =>
    setTimeout(resolve, 800)
  )

  tabLoading.value = false
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
        <!-- <p><strong>Роль:</strong> {{ user.role }}</p> -->
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
      
      <div class="appointments-section">

        <h2>Мои записи</h2>

        <!-- TABS -->
        <div class="tabs">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'future' }"
          @click="switchTab('future')"
        >
          Будущие
        </button>

        <button
          class="tab-btn"
          :class="{ active: activeTab === 'history' }"
          @click="switchTab('history')"
        >
          История
        </button>
      </div>

        <!-- EMPTY STATE -->
        <div v-if="appointments.length === 0" class="empty-state">
          У вас пока нет записей
        </div>

        <!-- FUTURE -->
        <div v-if="activeTab === 'future'">
          
          <!-- SKELETON -->
          <div
            v-if="tabLoading"
            class="appointments-grid"
          >
            <div
              v-for="n in 3"
              :key="n"
              class="appointment-skeleton"
            ></div>
          </div>

          <div
            v-else-if="futureAppointments.length === 0"
            class="empty-state"
          >
            Нет будущих записей
          </div>

          <div v-else class="appointments-grid">
            <div
              v-for="a in futureAppointments"
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
                  {{ specializationsMap[a.doctor.specializationId] }}
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
                  {{ getAppointmentStatusLabel(a.status) }}
                </span>

                <span class="status" :class="getPaymentClass(a)">
                  {{ getPaymentStatus(a) }}
                </span>
              </div>

            </div>
          </div>

        </div>

        <!-- HISTORY -->
        <div v-if="activeTab === 'history'">
          <!-- SKELETON -->
          <div
            v-if="tabLoading"
            class="appointments-grid"
          >
            <div
              v-for="n in 3"
              :key="n"
              class="appointment-skeleton"
            ></div>
          </div>
          <div
            v-else-if="finishedAppointments.length === 0"
            class="empty-state"
          >
            История пуста
          </div>

          <div v-else class="appointments-grid">
            <div
              v-for="a in finishedAppointments"
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
                  {{ specializationsMap[a.doctor.specializationId] }}
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
                  {{ getAppointmentStatusLabel(a.status) }}
                </span>

                <span class="status" :class="getPaymentClass(a)">
                  {{ getPaymentStatus(a) }}
                </span>
              </div>

            </div>
          </div>

        </div>

      </div>

    </div>

  </div>
</template>

<style scoped src="@/assets/profile.css"></style>