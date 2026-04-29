<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/api/axios'

const router = useRouter()

const form = ref({
  email: '',
  password: '',
  firstName: '',
  middleName: '',
  lastName: ''
})

const error = ref('')
const loading = ref(false)

async function register() {
  error.value = ''
  loading.value = true

  try {
    await http.post('/api/auth/register', form.value)

    router.push('/login')
  } catch (e) {
    error.value =
      e.response?.data?.message || 'Ошибка регистрации'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">

    <div class="register-container">

      <h2>Регистрация</h2>

      <div v-if="error" class="error">
        {{ error }}
      </div>

      <form @submit.prevent="register">

        <div class="form-group">
          <label>Email</label>
          <input v-model="form.email" type="text" required />
        </div>

        <div class="form-group">
          <label>Пароль</label>
          <input v-model="form.password" type="password" required />
        </div>

        <div class="form-group">
          <label>Имя</label>
          <input v-model="form.firstName" type="text" required />
        </div>

        <div class="form-group">
          <label>Отчество</label>
          <input v-model="form.middleName" type="text" />
        </div>

        <div class="form-group">
          <label>Фамилия</label>
          <input v-model="form.lastName" type="text" required />
        </div>

        <button class="btn" type="submit" :disabled="loading">
          {{ loading ? 'Регистрация...' : 'Зарегистрироваться' }}
        </button>

      </form>

      <router-link to="/login" class="login-link">
        Уже есть аккаунт? Войти
      </router-link>

    </div>

  </div>
</template>
<style scoped src="@/assets/register.css"></style>