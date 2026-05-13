<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/api/axios'
import AppHeader from '@/components/AppHeader.vue'

const route = useRoute()
const router = useRouter()

const doctors = ref([])
const specialization = ref(null)

onMounted(async () => {
  const specializationId = route.query.specializationId

  const res = await http.get(`/api/specialization/${specializationId}/doctors`)
  doctors.value = res.data.doctors
  specialization.value = res.data.specialization
})

function goToAppointment(doctorId) {
  router.push(`/appointments/create?doctorId=${doctorId}`)
}
</script>

<template>
  <AppHeader />

  <div class="container">

    <div class="page-header">
      <h1>Врачи</h1>

      <p v-if="specialization">
        {{ specialization.name }}
      </p>
    </div>

    <!-- empty state -->
    <div v-if="!doctors.length" class="empty-state">
      <p>Нет доступных врачей</p>
    </div>

    <!-- doctors grid -->
    <div v-else class="doctors-grid">

      <div
        v-for="d in doctors"
        :key="d.id"
        class="doctor-card"
      >

        <div class="doctor-info">

          <div class="doctor-header">
            <h3 class="doctor-name">
              {{ d.lastName }} {{ d.firstName }} {{ d.middleName }}
            </h3>

            <span class="price-badge">
              {{ d.consultationPrice ?? '—' }} ₽
            </span>
          </div>

          <p class="doctor-desc">
            {{ d.description || 'Описание отсутствует' }}
          </p>

        </div>

        <div class="doctor-actions">
          <button
            class="btn primary"
            @click="goToAppointment(d.id)"
          >
            Записаться
          </button>
        </div>

      </div>

    </div>

  </div>
</template>

<style scoped src="@/assets/doctors.css"></style>