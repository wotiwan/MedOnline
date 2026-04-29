<script setup>
import AppHeader from '@/components/AppHeader.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/main')
}

</script>

<template>
  <div class="container error-container">
    <div class="error-card">

      <div class="error-code">500</div>

      <h1>Что-то пошло не так</h1>
      <p>
        На сервере произошла ошибка. Мы уже работаем над этим.
        Попробуйте обновить страницу или вернуться позже.
      </p>

      <div class="actions">
        <button class="btn primary" @click="router.back()">
          Назад
        </button>
        <button class="btn secondary" @click="goHome">
          На главную
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped src="@/assets/error.css"></style>