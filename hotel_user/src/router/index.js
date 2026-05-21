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
