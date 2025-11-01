import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
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
    {
      path: '/report-results',
      name: 'reportResults',
      component: () => import('../views/ReportResults.vue')
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
        },
        {
          path: 'settings',
          name: 'counselorSettings',
          component: () => import('../views/counselor/Settings.vue')
        }
      ]
    },
    // 管理员端路由
    {
      path: '/admin',
      component: () => import('../views/AdminLayout.vue'),
      children: [
        {
          path: '',
          redirect: '/admin/dashboard'
        },
        {
          path: 'dashboard',
          name: 'adminDashboard',
          component: () => import('../views/admin/Dashboard.vue')
        },
        {
          path: 'schedule',
          name: 'adminSchedule',
          component: () => import('../views/admin/ScheduleManage.vue')
        },
        {
          path: 'counselors',
          name: 'adminCounselors',
          component: () => import('../views/admin/CounselorManage.vue')
        },
        {
          path: 'students',
          name: 'adminStudents',
          component: () => import('../views/admin/StudentManage.vue')
        }
      ]
    }
  ],
})

export default router
