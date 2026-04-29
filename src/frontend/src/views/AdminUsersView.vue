<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import http from '@/api/axios'

const route = useRoute()

const users = ref([])
const errors = ref([])
const selectedRole = ref(route.query.role || 'ALL')

const rolesFilter = [
  'ALL',
  'PATIENT',
  'DOCTOR',
  'ADMIN'
]

async function loadUsers() {
  try {
    errors.value = []

    const params = {}

    if (selectedRole.value !== 'ALL') {
      params.role = selectedRole.value
    }

    const res = await http.get('/api/admin/users', { params })

    users.value = res.data

  } catch (e) {
    errors.value = ['Ошибка загрузки пользователей']
  }
}

async function changeRole(userId, role) {
  try {
    await http.put(`/api/admin/users/${userId}/role/${role}`)
    await loadUsers()
  } catch {
    errors.value = ['Не удалось изменить роль']
  }
}

async function deleteUser(userId) {
  if (!confirm('Удалить пользователя?')) return

  try {
    await http.delete(`/api/users/${userId}`)
    await loadUsers()
  } catch {
    errors.value = ['Не удалось удалить пользователя']
  }
}

function roleLabel(role) {
  return role
}

watch(selectedRole, loadUsers)

onMounted(loadUsers)
</script>

<template>
  <AppHeader />

  <div class="container">

    <div class="page-header">
      <h1>Пользователи</h1>
    </div>

    <!-- ошибки -->
    <div v-if="errors.length" class="alert error">
      <div v-for="err in errors" :key="err">
        {{ err }}
      </div>
    </div>

    <!-- фильтр -->
    <div class="filter-form">
      <label>Фильтр по роли:</label>

      <select v-model="selectedRole">
        <option
          v-for="role in rolesFilter"
          :key="role"
          :value="role"
        >
          {{ role }}
        </option>
      </select>
    </div>

    <!-- список -->
    <div class="users-grid">

      <div
        v-for="user in users"
        :key="user.id"
        class="user-card"
      >

        <div class="user-info">
          <div class="user-email">
            {{ user.email }}
          </div>

          <div class="user-name">
            {{ user.lastName }} {{ user.firstName }} {{ user.middleName }}
          </div>

          <div class="user-role">
            {{ roleLabel(user.role) }}
          </div>
        </div>

        <div class="user-actions">

          <!-- patient -->
          <router-link
            v-if="user.role === 'PATIENT'"
            :to="`/admin/doctors/create?userId=${user.id}`"
          >
            <button class="btn primary">
              Сделать врачом
            </button>
          </router-link>

          <button
            v-if="user.role === 'PATIENT'"
            class="btn secondary"
            @click="changeRole(user.id, 'ADMIN')"
          >
            Сделать админом
          </button>

          <!-- doctor -->
          <router-link
            v-if="user.role === 'DOCTOR'"
            :to="`/admin/schedule/doctor/${user.id}`"
          >
            <button class="btn primary">
              Настроить расписание
            </button>
          </router-link>

          <!-- общие -->
          <div class="horizontal-buttons-container">

            <router-link :to="`/admin/users/${user.id}/edit`">
              <button class="btn primary">
                Редактировать
              </button>
            </router-link>

            <button
              class="btn danger"
              @click="deleteUser(user.id)"
            >
              Удалить
            </button>

          </div>

        </div>

      </div>

    </div>

  </div>
</template>
<style scoped src="@/assets/users.css"></style>