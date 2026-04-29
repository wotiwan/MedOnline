<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()

const userId = route.query.userId

const user = ref(null)
const specializations = ref([])

const form = ref({
  userId: userId,
  specializationId: '',
  description: ''
})

const error = ref('')
const success = ref('')

onMounted(async () => {
  try {
    const res1 = await http.get(`/api/users/${userId}`)
    const res2 = await http.get(`/api/specializations`)
    
    user.value = res1.data
    specializations.value = res2.data

    if (specializations.value.length > 0) {
      form.value.specializationId = specializations.value[0].id
    }

  } catch (e) {
    error.value = 'Не удалось загрузить данные'
  }
})

async function createDoctor() {
  error.value = ''
  success.value = ''

  try {
    await http.post('/api/admin/doctors/create', form.value)

    success.value = 'Врач успешно создан'

    setTimeout(() => {
      router.push('/admin/users')
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
      <h1>Создание врача</h1>
    </div>

    <div class="form-card">

      <div v-if="error" class="alert error">
        {{ error }}
      </div>

      <div v-if="success" class="alert success">
        {{ success }}
      </div>

      <div v-if="user" class="user-info-card">
        <p>
          <strong>Email:</strong>
          {{ user.email }}
        </p>

        <p>
          <strong>Имя:</strong>
          {{ user.lastName + " " + user.firstName + " " + user.middleName }}
        </p>
      </div>

      <form @submit.prevent="createDoctor">

        <div class="form-group">
          <label>Специализация</label>

          <select v-model="form.specializationId" required>
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
<style scoped src="@/assets/doctor-create.css"></style>