<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()

const userId = route.params.id

const loading = ref(true)
const saving = ref(false)
const error = ref('')

const user = ref(null)

const form = ref({
  firstName: '',
  middleName: '',
  lastName: '',
  birthDate: ''
})

async function loadUser() {
  try {
    const res = await http.get(`/api/users/${userId}`)

    user.value = res.data

    form.value = {
      firstName: user.value.firstName,
      middleName: user.value.middleName,
      lastName: user.value.lastName,
      birthDate: user.value.birthDate
    }
  } catch (e) {
    error.value = 'Не удалось загрузить пользователя'
  } finally {
    loading.value = false
  }
}

async function save() {
  saving.value = true
  error.value = ''

  try {
    await http.put(`/api/users/${userId}`, form.value)

    router.push('/admin/users')
  } catch (e) {
    error.value = 'Ошибка сохранения'
  } finally {
    saving.value = false
  }
}

onMounted(loadUser)
</script>

<template>
  <AppHeader />

  <div class="container">

    <div class="page-header">
      <h1>Редактирование пользователя</h1>
    </div>

    <div v-if="loading" class="empty-state">
      Загрузка...
    </div>

    <div v-else class="user-card">

      <div v-if="error" class="error-box">
        {{ error }}
      </div>

      <div class="user-info" v-if="user">
        <p><strong>Email:</strong> {{ user.email }}</p>
        <p><strong>Роль:</strong> {{ user.role }}</p>
      </div>

      <div class="edit-form">

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

          <button
            class="btn primary"
            @click="save"
            :disabled="saving"
          >
            {{ saving ? 'Сохранение...' : 'Сохранить' }}
          </button>

          <button
            class="btn secondary"
            @click="router.push('/admin/users')"
          >
            Отмена
          </button>

        </div>

      </div>

    </div>

  </div>
</template>

<style scoped src="@/assets/profile.css"></style>