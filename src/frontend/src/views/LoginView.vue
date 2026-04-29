<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/api/axios'

const router = useRouter()

const username = ref('')
const password = ref('')
const error = ref('')

async function login() {
  error.value = ''

  try {
    const res = await http.post('/api/auth/login', {
      username: username.value,
      password: password.value
    })

    localStorage.setItem('token', res.data.token)

    // если всё ок → на главную
    router.push('/main')

  } catch (e) {
    error.value = 'Неверный email или пароль'
  }
}
</script>

<style scoped src="@/assets/login.css"></style>

<template>
  <div class="main-container">
    <div class="login-container">
      <h2>Вход в портал</h2>

      <div v-if="error" class="error">
        {{ error }}
      </div>

      <form @submit.prevent="login">

        <div class="form-group">
          <label>Email</label>
          <input v-model="username" type="text" required>
        </div>

        <div class="form-group">
          <label>Пароль</label>
          <input v-model="password" type="password" required>
        </div>

        <button type="submit">Войти</button>
      </form>

      <router-link to="/register" class="register-link">
        Нет аккаунта? Зарегистрироваться
      </router-link>
    </div>
  </div>
</template>