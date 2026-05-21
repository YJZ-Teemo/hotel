// router/index.js
import { compareTime } from 'element-plus/es/components/time-select/src/utils.mjs';
import component from 'element-plus/es/components/tree-select/src/tree-select-option.mjs';
import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/admin',
    name: 'admin',
    component: () => import('../pages/Admin.vue'), // 懒加载
    // meta: { requiresAuth: true },
    children: [
      {
        path: '/check',
        name: 'check-in',
        component: () => import('../pages/admin/CheckIn.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/roominfo',
        name: 'RoomInfo',
        component: () => import('../pages/admin/RoomInfo.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/reservationquery',
        name: 'ReservationQuery',
        component: () => import('../pages/admin/ReservationQuery.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/iteminventory',
        name: 'ItemInventory',
        component: () => import('../pages/admin/ItemInventory.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/schedulequery',
        name: 'ScheduleQuery',
        component: () => import('../pages/admin/ScheduleQuery.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: '/checkinquery',
        name: 'CheckInQuery',
        component: () => import('../pages/admin/CheckInQuery.vue'),
        meta: { requiresAuth: true }
      },{
        path: '/employeeaccount',
        name: 'EmployeeAccount',
        component: () => import('../pages/admin/EmployeeAccount.vue'),
        meta: { requiresAuth: true }
      },{
        path: '/customer',
        name: 'Customer',
        component: () => import('../pages/admin/Customer.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/',
    redirect: '/Login'
  },
  {
    path: '/Login',
    name: 'Login',
    component: () => import('../pages/Login.vue'),
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('username'); // 检查登录状态
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/Login'); // 未登录则跳转到登录页
  } else {
    next(); // 放行
  }
});

export default router

