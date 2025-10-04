import { createRouter, createWebHistory } from 'vue-router'
import SurveyForm from '../components/SurveyForm.vue'

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
    }
  ],
})

export default router
