import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/StudentHome.vue')
    },
    {
      path: '/survey',
      name: 'survey',
      component: () => import('../views/SurveyForm.vue')
    },
    {
      path: '/reservation',
      name: 'reservation',
      component: () => import('../views/ReservationForm.vue')
    },
    {
      path: '/reservation/confirm/:counselorId',
      name: 'confirmReservation',
      component: () => import('../views/ConfirmReservation.vue'),
      props: true
    },
    {
      path: '/reservation-list',
      name: 'reservationList',
      component: () => import('../views/ReservationList.vue')
    },
    // 咨询师端路由
    {
      path: '/counselor',
      component: () => import('../views/CounselorLayout.vue'),
      children: [
        {
          path: '',
          redirect: '/counselor/dashboard'
        },
        {
          path: 'dashboard',
          name: 'counselorDashboard',
          component: () => import('../views/counselor/Dashboard.vue')
        },
        {
          path: 'appointments',
          name: 'counselorAppointments',
          component: () => import('../views/counselor/Appointments.vue')
        },
        {
          path: 'reports',
          name: 'counselorReports',
          component: () => import('../views/counselor/Reports.vue')
        }
      ]
    }
  ],
})

export default router
