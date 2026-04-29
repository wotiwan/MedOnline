<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()

const specializations = ref([])

const error = ref('')
const success = ref('')

const form = ref({
  email: '',
  password: '',
  firstName: '',
  middleName: '',
  lastName: '',
  birthDate: '',
  specializationId: '',
  description: ''
})

onMounted(async () => {
  try {
    const res = await http.get(`/api/specializations`)
    specializations.value = res.data

    if (specializations.value.length > 0) {
      form.value.specializationId = specializations.value[0].id
    }
  } catch (e) {
    error.value = 'Не удалось загрузить специализации'
  }
})

async function createDoctor() {
  error.value = ''
  success.value = ''

  try {
    await http.post('/api/admin/doctors/full/create', form.value)

    success.value = 'Врач успешно создан'

    setTimeout(() => {
      router.push('/admin')
    }, 1000)

  } catch (e) {
    error.value =
      e.response?.data?.message ||
      'Ошибка при создании врача'
  }
}
</script>

<template>
  <AppHeader />

  <div class="container">
    <div class="page-header">
      <h1>Создать врача с нуля</h1>
    </div>

    <div class="form-card">

      <div v-if="error" class="alert error">
        {{ error }}
      </div>

      <div v-if="success" class="alert success">
        {{ success }}
      </div>

      <form @submit.prevent="createDoctor">

        <div class="form-group">
          <label>Email</label>
          <input
            v-model="form.email"
            type="email"
            required
          >
        </div>

        <div class="form-group">
          <label>Пароль</label>
          <input
            v-model="form.password"
            type="password"
            required
          >
        </div>

        <div class="form-group">
          <label>Имя</label>
          <input
            v-model="form.firstName"
            type="text"
            required
          >
        </div>

        <div class="form-group">
          <label>Отчество</label>
          <input
            v-model="form.middleName"
            type="text"
          >
        </div>

        <div class="form-group">
          <label>Фамилия</label>
          <input
            v-model="form.lastName"
            type="text"
            required
          >
        </div>

        <div class="form-group">
          <label>Дата рождения</label>
          <input
            v-model="form.birthDate"
            type="date"
            required
          >
        </div>

        <div class="form-group">
          <label>Специализация</label>

          <select
            v-model="form.specializationId"
            required
          >
            <option
              v-for="s in specializations"
              :key="s.id"
              :value="s.id"
            >
              {{ s.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label>Описание</label>

          <textarea
            v-model="form.description"
            placeholder="Введите описание врача"
          ></textarea>
        </div>

        <button type="submit" class="btn primary">
          Создать врача
        </button>

      </form>
    </div>
  </div>
</template>
<style scoped src="@/assets/doctor-full-create.css"></style>