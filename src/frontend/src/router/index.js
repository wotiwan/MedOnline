import { createRouter, createWebHistory } from 'vue-router'
import MainView from '../views/MainView.vue'
import LoginView from '../views/LoginView.vue'
import ErrorView from '../views/ErrorView.vue'
import ForbiddenView from '../views/ForbiddenView.vue'
import NotFoundView from '../views/NotFoundView.vue'
import RegisterView from '@/views/RegisterView.vue'
import ProfileView from '@/views/ProfileView.vue'
import AppointmentDetailsView from '@/views/AppointmentDetailsView.vue'
import DoctorsView from '@/views/DoctorsView.vue'
import DoctorSlotsView from '@/views/DoctorSlotsView.vue'
import AdminView from '@/views/AdminView.vue'
import AdminUsersView  from '@/views/AdminUsersView.vue'
import DoctorCreateView from '@/views/DoctorCreateView.vue'
import DoctorFullCreateView from '@/views/DoctorFullCreateView.vue'
import DoctorScheduleView from '@/views/DoctorScheduleView.vue'
import DoctorAppointmentsView from '@/views/DoctorAppointmentsView.vue'
import DoctorAppointmentDetailsView from '@/views/DoctorAppointmentDetailsView.vue'
import AdminUserEdit from '@/views/AdminUserEdit.vue'

function parseToken() {
  const token = localStorage.getItem('token')
  if (!token) return null

  try {
    return JSON.parse(atob(token.split('.')[1]))
  } catch {
    return null
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/main",
      name: "main",
      component: MainView
    },
    {
      path: '/login',
      name: "login",
      component: LoginView
    },
    {
      path: '/error',
      component: ErrorView
    },
    {
      path: '/forbidden',
      component: ForbiddenView
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: NotFoundView
    },
    {
      path: '/register',
      component: RegisterView
    },
    {
      path: '/profile',
      component: ProfileView
    },
    {
      path: '/appointments/:id',
      name: 'appointment-details',
      component: AppointmentDetailsView
    },
    {
      path: '/doctors',
      name: 'doctors',
      component: DoctorsView
    },
    {
      path: '/appointments/create',
      name: 'appointment-create',
      component: DoctorSlotsView
    },
    {
      path: '/admin',
      name: 'admin',
      component: AdminView,
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: AdminUsersView,
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/users/:id/edit',
      name: 'admin-user-edit',
      component: AdminUserEdit,
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/doctors/create',
      name: 'admin-doctor-create',
      component: DoctorCreateView,
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/doctors/full/create',
      name: 'admin-doctor-full-create',
      component: DoctorFullCreateView,
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/schedule/doctor/:id',
      name: 'doctor-schedule',
      component: DoctorScheduleView,
      meta: { requiresAdmin: true }
    },
    {
      path: '/doctor/appointments',
      name: 'doctor-appointments',
      component: DoctorAppointmentsView,
      meta: { requiresDoctor: true }
    },
    {
      path: '/doctor/appointments/:id',
      name: 'doctor-appointment-details',
      component: DoctorAppointmentDetailsView,
      meta: { requiresDoctor: true }
    }
  ],
})

router.beforeEach((to, from, next) => {
  const payload = parseToken()

  if (to.meta.requiresAdmin) {
    if (!payload || payload.role !== 'ROLE_ADMIN') {
      return next('/forbidden')
    }
  }
  if (to.meta.requiresDoctor) {
    if (!payload || payload.role !== 'ROLE_DOCTOR') {
      return next('/forbidden')
    }
  }
  next()
})

export default router
