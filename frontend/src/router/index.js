import { createRouter, createWebHistory } from 'vue-router'
import SurveyForm from '../views/SurveyForm.vue'
import ReservationForm from '../views/ReservationForm.vue'
import ConfirmReservation from '../views/ConfirmReservation.vue'
import ReservationList from '../views/ReservationList.vue'
import StudentHome from '../views/StudentHome.vue'

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
    }
  ],
})

export default router
