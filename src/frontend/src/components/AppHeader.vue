<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const token = localStorage.getItem('token')

function parseToken() {
  if (!token) return null

  try {
    return JSON.parse(atob(token.split('.')[1]))
  } catch {
    return null
  }
}

const payload = parseToken()

const role = payload?.role

const isAdmin = role === 'ROLE_ADMIN'
const isDoctor = role === 'ROLE_DOCTOR'

function logout() {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<template>
  <header class="app-header">
    <div class="header-inner">

      <!-- Логотип -->
      <div class="logo">
        <router-link to="/main">MedOnline</router-link>
      </div>

      <!-- Навигация -->
      <nav class="nav-links">
        <router-link to="/main" class="nav-link">
          Главная
        </router-link>

        <router-link v-if="isAdmin" to="/admin" class="nav-link">
          Админ
        </router-link>

        <router-link v-if="isDoctor" to="/doctor/appointments" class="nav-link">
          Расписание
        </router-link>

        <router-link to="/profile" class="nav-link">
          Профиль
        </router-link>
      </nav>

      <!-- Правая часть -->
      <div class="nav-actions">
        <button class="logout-btn" @click="logout">
          Выйти
        </button>
      </div>

    </div>
  </header>
</template>