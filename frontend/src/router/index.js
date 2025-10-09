import { createRouter, createWebHistory } from 'vue-router'
import SurveyForm from '../views/SurveyForm.vue'
import ReservationForm from '../views/ReservationForm.vue'
import ConfirmReservation from '../views/ConfirmReservation.vue'
import ReservationList from '../views/ReservationList.vue'
import StudentHome from '../views/StudentHome.vue'
import CounselorLayout from '../views/CounselorLayout.vue'
import CounselorDashboard from '../views/counselor/Dashboard.vue'
import CounselorAppointments from '../views/counselor/Appointments.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: StudentHome
    },
    {
      path: '/survey',
      name: 'survey',
      component: SurveyForm
    },
    {
      path: '/reservation',
      name: 'reservation',
      component: ReservationForm
    },
    {
      path: '/reservation/confirm/:counselorId',
      name: 'confirmReservation',
      component: ConfirmReservation,
      props: true
    },
    {
      path: '/reservation-list',
      name: 'reservationList',
      component: ReservationList
    },
    // 咨询师端路由
    {
      path: '/counselor',
      component: CounselorLayout,
      children: [
        {
          path: '',
          redirect: '/counselor/dashboard'
        },
        {
          path: 'dashboard',
          name: 'counselorDashboard',
          component: CounselorDashboard
        },
        {
          path: 'appointments',
          name: 'counselorAppointments',
          component: CounselorAppointments
        // },
        // {
        //   path: 'schedule',
        //   name: 'counselorSchedule',
        //   component: () => import('../views/counselor/Schedule.vue')
        // },
        // {
        //   path: 'students',
        //   name: 'counselorStudents',
        //   component: () => import('../views/counselor/Students.vue')
        // },
        // {
        //   path: 'reports',
        //   name: 'counselorReports',
        //   component: () => import('../views/counselor/Reports.vue')
        // },
        // {
        //   path: 'settings',
        //   name: 'counselorSettings',
        //   component: () => import('../views/counselor/Settings.vue')
        }
      ]
    }
  ],
})

export default router
