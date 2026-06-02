<template>
  <nav class="nav">
    <button
      v-for="item in navItems"
      :key="item.key"
      class="nav-item"
      :class="{ active: isActive(item) }"
      type="button"
      @click="router.push(item.path)"
    >
      <van-icon :name="item.icon" />
      <span>{{ item.text }}</span>
    </button>
  </nav>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const navItems = [
  { key: 'home', icon: 'home-o', text: '首页', path: '/clean', activeNames: ['Clean'] },
  { key: 'level', icon: 'cart-circle-o', text: '物品申请', path: '/ehome', activeNames: ['Evaluation'] },
  { key: 'mine', icon: 'user-o', text: '我的', path: '/ehome', activeNames: ['EHome'] }
]

const isActive = (item) => item.activeNames.includes(route.name)
</script>

<style scoped>
.nav {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 30;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.5rem;
  padding: 0.5rem 0.75rem max(0.5rem, env(safe-area-inset-bottom));
  border-top: 1px solid #edf0f4;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 -0.25rem 1rem rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(0.75rem);
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  min-width: 0;
  min-height: 3.75rem;
  padding: 0.45rem 0.25rem;
  border: 0;
  border-radius: 0.75rem;
  background: #fff;
  color: #243244;
  font-size: 0.86rem;
}

.nav-item .van-icon {
  font-size: 1.65rem;
}

.nav-item.active {
  background: #6543a2;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 0.35rem 0.9rem rgba(101, 67, 162, 0.28);
}

.nav-item.active .van-icon {
  color: #fff;
}
</style>
