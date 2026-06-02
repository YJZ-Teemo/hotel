<template>
  <div class="settings-page">
    <van-nav-bar title="设置" left-arrow @click-left="router.back()" />

    <section class="group">
      <div class="cell" @click="router.push('/hotel-orders')">
        <span>我的订单</span>
        <van-icon name="arrow" />
      </div>
      <div class="cell" @click="router.push('/evaluation')">
        <span>我的评价</span>
        <van-icon name="arrow" />
      </div>
    </section>

    <section class="group">
      <div class="cell">
        <span>清除缓存</span>
        <van-icon name="arrow" />
      </div>
      <div class="cell">
        <span>关于我们</span>
        <van-icon name="arrow" />
      </div>
      <div class="cell">
        <span>版本号</span>
        <span class="sub">v0.1.0</span>
      </div>
    </section>

    <section class="group">
      <div class="cell logout" @click="handleLogout">
        <span>退出登录</span>
      </div>
    </section>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { showConfirmDialog, showNotify } from 'vant'

const router = useRouter()

const handleLogout = async () => {
  try {
    await showConfirmDialog({ title: '确认退出？' })
    localStorage.clear()
    router.push('/login')
  } catch (error) {
    showNotify({ type: 'danger', message: error+'，请稍后重试' })
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.group {
  margin: 0.75rem 0;
  background: #fff;
}

.cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.1rem;
  border-bottom: 1px solid #f5f5f5;
  font-size: 0.95rem;
  color: #333;
}

.cell:last-child {
  border-bottom: 0;
}

.sub {
  color: #999;
  font-size: 0.85rem;
}

.logout {
  justify-content: center;
  color: #ee0a24;
}
</style>
