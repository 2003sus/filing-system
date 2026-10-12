import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('@/views/Login.vue') },
  { path: '/cases', component: () => import('@/views/CaseList.vue') },
  { path: '/cases/new', component: () => import('@/views/CaseNew.vue') },
  { path: '/cases/:id', component: () => import('@/views/CaseDetail.vue') },
  { path: '/tasks', component: () => import('@/views/TaskList.vue') },
  { path: '/tasks/:id', component: () => import('@/views/TaskDetail.vue') },
  { path: '/users', component: () => import('@/views/UserList.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    return '/login'
  }
})

export default router