// router/index.js
import { createRouter, createWebHashHistory } from 'vue-router'


const routes = [
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../pages/Admin.vue'),
    // meta: { requiresAuth: true },
    children: [
      {
        path: '/',
        name: 'Room',
        component: () => import('../pages/admin/Room.vue'),
        
      },
      {
        path: '/home',
        name: 'Home',
        component: () => import('../pages/admin/Home.vue'),
        meta: { requiresAuth: true }
      },{
        path: '/evaluation',
        name: 'Evaluation',
        component: () => import('../pages/admin/Evaluation.vue'),
        meta: { requiresAuth: true }
      }]
  },{
    path: '/Empladmin',
    name: 'Empladmin',
    component: () => import('../pages/Empladmin.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '/ehome',
        name: 'EHome',
        component: () => import('../pages/employee/EHome.vue'),
        meta: { requiresAuth: true }
      },{
        path: '/clean',
        name: 'Clean',
        component: () => import('../pages/employee/Clean.vue'),
        meta: { requiresAuth: true }
      }
  ]},
  {
    path: '/reserve',
    name: 'Reserve',
    component: () => import('../pages/admin/reserve.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/hotel-orders',
    name: 'HotelOrders',
    component: () => import('../pages/admin/HotelOrders.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/order-review',
    name: 'OrderReview',
    component: () => import('../pages/admin/OrderReview.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('../pages/admin/Settings.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../pages/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/employee-login',
    name: 'EmployeeLogin',
    component: () => import('../pages/EmployeeLogin.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('username')
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
