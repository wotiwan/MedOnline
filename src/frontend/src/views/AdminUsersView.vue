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

// редактирование цены
const editingPriceUserId = ref(null)
const priceDraft = ref('')

const doctorPrices = ref({})

const rolesFilter = ['ALL', 'PATIENT', 'DOCTOR', 'ADMIN']

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

    await loadDoctorPrices()
    
  } catch {
    errors.value = ['Ошибка загрузки пользователей']
  }
}

async function loadDoctorPrices() {
  const doctorUsers = users.value.filter(u => u.role === 'DOCTOR')

  await Promise.all(
    doctorUsers.map(async (u) => {
      const res = await http.get(`/api/doctors/${u.id}`)
      doctorPrices.value[u.id] = res.data.consultationPrice
    })
  )
}

// ======================
// роли
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
// цена врача
// ======================
function startEditPrice(user) {
  editingPriceUserId.value = user.id
  priceDraft.value = user.consultationPrice ?? ''
}

function cancelEditPrice() {
  editingPriceUserId.value = null
  priceDraft.value = ''
}

async function savePrice(userId) {
  const value = Number(priceDraft.value)

  if (Number.isNaN(value) || value < 0) {
    errors.value = ['Цена не может быть отрицательной']
    return
  }

  try {
    await http.patch(`/api/doctors/${userId}/price`, null, {
      params: { price: value }
    })

    editingPriceUserId.value = null
    priceDraft.value = ''
    await loadUsers()
  } catch {
    errors.value = ['Ошибка обновления цены']
  }
}

// ======================
// label
// ======================
function roleLabel(role) {
  return role
}

// ======================
// lifecycle
// ======================
watch(selectedRole, loadUsers)
onMounted(loadUsers)

// ======================
// search
// ======================
const filteredUsers = computed(() => {
  if (!search.value.trim()) return users.value

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

    <!-- errors -->
    <div v-if="errors.length" class="alert error">
      <div v-for="err in errors" :key="err">
        {{ err }}
      </div>
    </div>

    <!-- filters -->
    <div class="filter-row">

      <div class="filter-form">
        <label>Роль:</label>
        <select v-model="selectedRole">
          <option v-for="role in rolesFilter" :key="role" :value="role">
            {{ role }}
          </option>
        </select>
      </div>

      <div class="search-block">
        <input
          v-model="search"
          class="search-input"
          placeholder="Поиск..."
        />
      </div>

    </div>

    <!-- list -->
    <div class="users-grid">

      <div
        v-for="user in filteredUsers"
        :key="user.id"
        class="user-card"
      >

        <div class="user-info">
          <div class="user-email">{{ user.email }}</div>
          <div class="user-name">
            {{ user.lastName }} {{ user.firstName }} {{ user.middleName }}
          </div>
          <div class="user-role">{{ roleLabel(user.role) }}</div>
        </div>

        <!-- PRICE BLOCK (ТОЛЬКО ДЛЯ ВРАЧЕЙ) -->
        <div v-if="user.role === 'DOCTOR'" class="price-block">

          <div v-if="editingPriceUserId !== user.id" class="price-view">
            <span>
              Цена:
              <b>
                {{ doctorPrices[user.id] ?? '—' }} ₽
              </b>
            </span>

            <button class="btn secondary" @click="startEditPrice(user)">
              Изменить
            </button>
          </div>

          <div v-else class="price-edit">
            <input
              v-model="priceDraft"
              type="number"
              min="0"
              class="price-input"
            />

            <div class="price-actions">
              <button class="btn primary" @click="savePrice(user.id)">
                Сохранить
              </button>

              <button class="btn secondary" @click="cancelEditPrice">
                Отмена
              </button>
            </div>
          </div>

        </div>

        <!-- actions -->
        <div class="user-actions">

          <div class="btn-row">

            <router-link
              v-if="user.role === 'DOCTOR'"
              :to="`/admin/schedule/doctor/${user.id}`"
            >
              <button class="btn primary">Расписание</button>
            </router-link>

            <router-link
              v-if="user.role === 'PATIENT'"
              :to="`/admin/doctors/create?userId=${user.id}`"
            >
              <button class="btn primary">Сделать врачом</button>
            </router-link>

            <router-link :to="`/admin/users/${user.id}/edit`">
              <button class="btn secondary">Редактировать</button>
            </router-link>

          </div>

          <div class="btn-row">

            <button
              v-if="user.role === 'PATIENT'"
              class="btn secondary"
              @click="changeRole(user.id, 'ADMIN')"
            >
              Сделать админом
            </button>

            <button class="btn danger" @click="deleteUser(user.id)">
              Удалить
            </button>

          </div>

        </div>

      </div>

    </div>

  </div>
</template>

<style scoped src="@/assets/users.css"></style>