<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()

const appointmentId = route.params.id

const loading = ref(true)
const saving = ref(false)
const error = ref('')

const form = ref({
  status: 'BOOKED',
  consultationResult: ''
})

async function loadAppointment() {
  try {
    const res = await http.get(`/api/doctor/appointments/${appointmentId}`)

    form.value.status = res.data.status ?? 'BOOKED'
    form.value.consultationResult = res.data.consultationResult ?? ''
  } catch (e) {
    error.value = 'Не удалось загрузить запись'
  } finally {
    loading.value = false
  }
}

async function save() {
  error.value = ''
  saving.value = true

  try {
    console.log(form.value)
    await http.put(`/api/doctor/appointments/${appointmentId}`, form.value)

    router.push('/doctor/appointments')
  } catch (e) {
    error.value = 'Не удалось сохранить изменения'
  } finally {
    saving.value = false
  }
}

onMounted(loadAppointment)
</script>

<template>
  <AppHeader />

  <div class="container">
    <div class="page-header">
      <h1>Консультация</h1>
    </div>

    <div class="form-card">

      <div v-if="loading" class="empty-state">
        Загрузка...
      </div>

      <form v-else @submit.prevent="save">

        <div v-if="error" class="error-box">
          {{ error }}
        </div>

        <div class="form-group">
          <label>Статус</label>
          
          <select v-model="form.status">
            <option value="BOOKED">BOOKED</option>
            <option value="COMPLETED">COMPLETED</option>
            <option value="CANCELLED">CANCELLED</option>
          </select>
        </div>

        <div class="form-group">
          <label>Результат консультации</label>

          <textarea
            v-model="form.consultationResult"
            rows="5"
            placeholder="Введите заключение врача..."
          />
        </div>

        <button
          type="submit"
          class="btn primary"
          :disabled="saving"
        >
          {{ saving ? 'Сохранение...' : 'Сохранить' }}
        </button>

      </form>
    </div>
  </div>
</template>

<style scoped src="@/assets/doctor-appointment-details.css"></style>