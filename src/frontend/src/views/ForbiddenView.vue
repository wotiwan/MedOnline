<script setup>
import AppHeader from '@/components/AppHeader.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/main')
}

</script>

<template>
  <AppHeader />

  <div class="container error-container">
    <div class="error-card">

      <div class="error-code">403</div>

      <h1>Доступ запрещен</h1>
      <p>
        У вас нет прав для доступа к этой странице.
      </p>

      <div class="actions">
        <button class="btn secondary" @click="goHome">
          На главную
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped src="@/assets/error.css"></style>