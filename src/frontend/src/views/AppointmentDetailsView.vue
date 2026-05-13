<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()

const appointment = ref(null)

// ======================
// загрузка записи
// ======================
const specialization = ref(null)

onMounted(async () => {
  const id = route.params.id

  // запись
  const appointmentRes = await http.get(`/api/appointments/${id}`)

  appointment.value = appointmentRes.data

  // specialization id из врача
  const specializationId =
    appointment.value.doctor.specializationId

  // specialization
  const specializationRes = await http.get(
    `/api/specializations/${specializationId}`
  )

  specialization.value = specializationRes.data
})

// ======================
// форматирование
// ======================
function formatDate(dateTime) {
  return new Date(dateTime).toLocaleDateString('ru-RU')
}

function formatTime(dateTime) {
  return new Date(dateTime).toLocaleTimeString('ru-RU', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// ======================
// отмена записи
// ======================
async function cancelAppointment() {
  await http.put(`/api/appointments/${appointment.value.id}/cancel`)

  appointment.value.status = 'CANCELLED'
}

// ======================
// статус оплаты
// ======================
const paymentStatus = computed(() => {
  if (!appointment.value) return ''

  const payments = appointment.value.payments || []

  if (
    !appointment.value.price ||
    Number(appointment.value.price) === 0
  ) {
    return 'Бесплатно'
  }

  const hasSucceeded = payments.some(
    p => p.status === 'SUCCEEDED'
  )

  if (hasSucceeded) {
    return 'Оплачено'
  }

  return 'Не оплачено'
})

const paymentStatusClass = computed(() => {
  if (!appointment.value) return ''

  const payments = appointment.value.payments || []

  if (
    !appointment.value.price ||
    Number(appointment.value.price) === 0
  ) {
    return 'free'
  }

  const hasSucceeded = payments.some(
    p => p.status === 'SUCCEEDED'
  )

  if (hasSucceeded) {
    return 'paid'
  }

  return 'unpaid'
})

// ======================
// оплата
// ======================
async function pay() {
  const returnUrl =
    window.location.origin + `/appointments/${appointment.value.id}`

  const res = await http.post(
    `/api/payments/${appointment.value.id}`,
    null,
    {
      params: {
        returnUrl
      }
    }
  )

  const paymentUrl = res.data

  window.location.href = paymentUrl
}
</script>

<template>
  <AppHeader />

  <div class="container" v-if="appointment">

    <div class="page-header">
      <h1>Информация о записи</h1>
    </div>

    <div class="appointment-card">

      <!-- header -->
      <div class="card-top">

        <div>
          <h2 class="doctor-name">
            {{ appointment.doctor.firstName }}
            {{ appointment.doctor.lastName }}
          </h2>

          <p class="specialization">
            {{ specialization?.name || 'Специализация не указана' }}
          </p>
        </div>

        <span
          class="status"
          :class="appointment.status.toLowerCase()"
        >
          {{ appointment.status }}
        </span>

      </div>

      <!-- body -->
      <div class="appointment-grid">

        <div class="info-block">
          <span class="label">Дата</span>
          <span class="value">
            {{ formatDate(appointment.timeSlot.startTime) }}
          </span>
        </div>

        <div class="info-block">
          <span class="label">Время</span>
          <span class="value">
            {{ formatTime(appointment.timeSlot.startTime) }}
            —
            {{ formatTime(appointment.timeSlot.endTime) }}
          </span>
        </div>

        <div class="info-block">
          <span class="label">Стоимость</span>
          <span class="value">
            {{ appointment.price }} ₽
          </span>
        </div>

        <div
          class="info-block payment-block"
          :class="paymentStatusClass"
        >
          <span class="payment-title">
            Статус оплаты
          </span>

          <span class="payment-value">
            {{ paymentStatus }}
          </span>
        </div>

      </div>

      <!-- consultation -->
      <div class="consultation-block">

        <span class="label">Заключение врача</span>

        <p class="consultation-text">
          {{
            appointment.consultationResult
              ? appointment.consultationResult
              : 'Появится после приёма'
          }}
        </p>

      </div>

      <!-- actions -->
      <div class="actions">

        <!-- оплатить -->
        <button
          v-if="paymentStatus === 'Не оплачено'"
          class="btn"
          @click="pay"
        >
          Оплатить приём
        </button>

        <!-- отменить -->
        <button
          v-if="appointment.status === 'BOOKED'"
          class="btn danger"
          @click="cancelAppointment"
        >
          Отменить запись
        </button>

      </div>

    </div>

  </div>
</template>

<style scoped src="@/assets/appointment-details.css"></style>