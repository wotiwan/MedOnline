<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const doctorId = route.params.id

const templates = ref([])
const hasTemplates = ref(false)

const success = ref('')
const error = ref('')

const daysOfWeek = [
  'MONDAY',
  'TUESDAY',
  'WEDNESDAY',
  'THURSDAY',
  'FRIDAY',
  'SATURDAY',
  'SUNDAY'
]

const newTemplate = ref({
  doctorId: doctorId,
  dayOfWeek: 'MONDAY',
  startTime: '',
  endTime: '',
  slotDuration: 15
})

const daysAhead = ref(7)

async function loadData() {
  try {
    const res = await http.get(`/api/schedules/doctor/templates`, {
      params: { doctorId }
    })

    templates.value = res.data
    hasTemplates.value = templates.value.length > 0

  } catch (e) {
    error.value = 'Не удалось загрузить расписание'
  }
}

async function createTemplate() {
  error.value = ''
  success.value = ''

  try {
    await http.post('/api/schedules/template/create', newTemplate.value)

    success.value = 'Шаблон создан'
    await loadData()

  } catch (e) {
    error.value = 'Ошибка при создании шаблона'
  }
}

async function deleteTemplate(templateId) {
  error.value = ''
  success.value = ''

  try {
    await http.delete(`/api/schedules/template/${templateId}`)

    success.value = 'Шаблон удалён'
    await loadData()

  } catch (e) {
    error.value = 'Ошибка удаления шаблона'
  }
}

async function generateSchedule() {
  error.value = ''
  success.value = ''

  try {
    await http.post(`/api/schedules/doctor/create`, {
      daysAhead: daysAhead.value,
        doctorId: doctorId
    })

    success.value = 'Расписание создано'

  } catch (e) {
    error.value = 'Ошибка генерации расписания'
  }
}

onMounted(loadData)
</script>

<template>
  <AppHeader />

  <div class="container">

    <div class="page-header">
      <h1>Расписание врача {{ doctorId }}</h1>
    </div>

    <div v-if="success" class="alert success">
      {{ success }}
    </div>

    <div v-if="error" class="alert error">
      {{ error }}
    </div>

    <!-- Таблица шаблонов -->
    <div v-if="hasTemplates" class="form-card">
      <h3>Текущие шаблоны</h3>

      <table class="schedule-table">
        <thead>
          <tr>
            <th>День недели</th>
            <th>Начало</th>
            <th>Конец</th>
            <th>Длительность</th>
            <th>Действия</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="t in templates"
            :key="t.id"
          >
            <td>{{ t.dayOfWeek }}</td>
            <td>{{ t.startTime }}</td>
            <td>{{ t.endTime }}</td>
            <td>{{ t.slotDuration }}</td>
            <td>
              <button
                class="btn danger"
                @click="deleteTemplate(t.id)"
              >
                Удалить
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="generate-form">
        <label>Сгенерировать расписание на дней вперед:</label>

        <input
          v-model="daysAhead"
          type="number"
          min="1"
        >

        <button
          class="btn primary"
          @click="generateSchedule"
        >
          Создать расписание
        </button>
      </div>
    </div>

    <!-- Создание шаблона -->
    <div class="form-card" style="margin-top: 30px;">
      <h3>Добавить новый шаблон</h3>

      <form @submit.prevent="createTemplate">

        <div class="form-group">
          <label>День недели</label>

          <select v-model="newTemplate.dayOfWeek">
            <option
              v-for="d in daysOfWeek"
              :key="d"
              :value="d"
            >
              {{ d }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Начало работы</label>

          <input
            v-model="newTemplate.startTime"
            type="time"
            required
          >
        </div>

        <div class="form-group">
          <label>Конец работы</label>

          <input
            v-model="newTemplate.endTime"
            type="time"
            required
          >
        </div>

        <div class="form-group">
          <label>Длительность слота</label>

          <input
            v-model="newTemplate.slotDuration"
            type="number"
            min="5"
            step="5"
            required
          >
        </div>

        <button type="submit" class="btn primary">
          Создать шаблон
        </button>

      </form>
    </div>

  </div>
</template>

<style scoped src="@/assets/doctor-schedule.css"></style>