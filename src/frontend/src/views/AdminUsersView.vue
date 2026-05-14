<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import http from '@/api/axios'

const route = useRoute()

const users = ref([])
const errors = ref([])
const selectedRole = ref(route.query.role || 'ALL')
const search = ref('')

const rolesFilter = [
  'ALL',
  'PATIENT',
  'DOCTOR',
  'ADMIN'
]

// ======================
// загрузка пользователей
// ======================
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

// ======================
// смена роли
// ======================
async function changeRole(userId, role) {
  try {
    await http.put(`/api/admin/users/${userId}/role/${role}`)
    await loadUsers()
  } catch {
    errors.value = ['Не удалось изменить роль']
  }
}

// ======================
// удаление
// ======================
async function deleteUser(userId) {
  if (!confirm('Удалить пользователя?')) return

  try {
    await http.delete(`/api/users/${userId}`)
    await loadUsers()
  } catch {
    errors.value = ['Не удалось удалить пользователя']
  }
}

// ======================
// label
// ======================
function roleLabel(role) {
  return role
}

// ======================
// фильтр роли (backend)
// ======================
watch(selectedRole, loadUsers)

// ======================
// загрузка
// ======================
onMounted(loadUsers)

// ======================
// SEARCH (frontend)
// ======================
const filteredUsers = computed(() => {
  if (!search.value.trim()) {
    return users.value
  }

  const q = search.value.toLowerCase()

  return users.value.filter(u =>
    (u.email || '').toLowerCase().includes(q) ||
    (u.firstName || '').toLowerCase().includes(q) ||
    (u.lastName || '').toLowerCase().includes(q) ||
    (u.middleName || '').toLowerCase().includes(q)
  )
})
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

    <!-- FILTERS -->
    <div class="filter-row">

      <div class="filter-form">
        <label>Роль:</label>

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

      <!-- SEARCH -->
      <div class="search-block">
        <input
          v-model="search"
          type="text"
          placeholder="Поиск по имени или email..."
          class="search-input"
        />
      </div>

    </div>

    <!-- список -->
    <div class="users-grid">

      <div
        v-for="user in filteredUsers"
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

          <!-- PRIMARY ACTIONS -->
          <div class="btn-row">

            <router-link
              v-if="user.role === 'DOCTOR'"
              :to="`/admin/schedule/doctor/${user.id}`"
            >
              <button class="btn primary">
                Расписание
              </button>
            </router-link>

            <router-link
              v-if="user.role === 'PATIENT'"
              :to="`/admin/doctors/create?userId=${user.id}`"
            >
              <button class="btn primary">
                Сделать врачом
              </button>
            </router-link>

            <router-link :to="`/admin/users/${user.id}/edit`">
              <button class="btn secondary">
                Редактировать
              </button>
            </router-link>

          </div>

          <!-- ADMIN ACTIONS -->
          <div class="btn-row">

            <button
              v-if="user.role === 'PATIENT'"
              class="btn secondary"
              @click="changeRole(user.id, 'ADMIN')"
            >
              Сделать админом
            </button>

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