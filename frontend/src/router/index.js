import { createRouter, createWebHistory } from 'vue-router'
import SurveyForm from '../components/SurveyForm.vue'
import ReservationForm from '../components/ReservationForm.vue'
import ConfirmReservation from '../views/ConfirmReservation.vue'
import ReservationList from '../views/ReservationList.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
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
