<script setup>
import AppHeader from '@/components/AppHeader.vue'

</script>

<template>
  <AppHeader />

  <div class="container">
    <div class="page-header">
      <h1>Админ-панель</h1>
      <p>Функции администрирования системы</p>
    </div>

    <div class="admin-grid">

      <div class="admin-card">
        <h3>Пользователи</h3>
        <p>Просмотр и управление всеми пользователями системы.</p>

        <router-link to="/admin/users">
          <button class="btn primary">
            Список всех пользователей
          </button>
        </router-link>
      </div>

      <div class="admin-card">
        <h3>Врачи</h3>
        <p>Добавление новых врачей и редактирование существующих.</p>

        <router-link to="/admin/doctors/full/create">
          <button class="btn primary">
            Создать врача с нуля
          </button>
        </router-link>
      </div>

    </div>
  </div>
</template>

<style scoped src="@/assets/admin.css"></style>