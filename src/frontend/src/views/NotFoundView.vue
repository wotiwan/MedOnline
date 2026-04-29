<script setup>
import { useRouter } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
</script>

<template>
  <AppHeader />

  <div class="container not-found-wrapper">
    <div class="not-found-card">

      <div class="code">404</div>

      <h1>Страница не найдена</h1>

      <p>
        Похоже, вы перешли по несуществующему адресу.
        Проверьте ссылку или вернитесь на главную страницу.
      </p>

      <div class="actions">
        <button class="btn" @click="router.push('/main')">
          На главную
        </button>

        <button class="btn secondary" @click="router.back()">
          Назад
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>
.not-found-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 75vh;
}

/* карточка как в остальном UI */
.not-found-card {
  background: #ffffff;
  padding: 48px 56px;
  border-radius: 16px;
  max-width: 560px;
  width: 100%;
  text-align: center;

  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
  border: 1px solid #eef2f7;
}

/* код ошибки */
.code {
  font-size: 84px;
  font-weight: 700;
  color: #0f766e;
  line-height: 1;
  margin-bottom: 12px;
  letter-spacing: -2px;
}

/* заголовок */
h1 {
  font-size: 26px;
  margin-bottom: 12px;
  color: #1e293b;
}

/* текст */
p {
  color: #64748b;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 28px;
}

/* кнопки */
.actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}
</style>