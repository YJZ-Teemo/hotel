<template>
  <main class="mine-page">
    <section class="hero" :style="{ backgroundImage: `linear-gradient(180deg, rgba(255, 255, 255, 0.4), #eef3f0), url(${bgImage})` }">
      <div class="top-actions">
        <div class="profile">
          <div class="avatar">
            <van-icon name="manager-o" />
          </div>
          <div class="name-row">
            <strong>{{ maskedName }}</strong>
            <van-icon name="arrow" />
          </div>
        </div>

        <div class="quick-actions">
          <button v-for="item in headerActions" :key="item.text" type="button" @click="item.route && router.push(item.route)">
            <van-icon :name="item.icon" />
            <span>{{ item.text }}</span>
          </button>
        </div>
      </div>
    </section>

    <section class="card order-card">
      <div class="tab-row">
        <strong>工单</strong>
      </div>
      <div class="order-grid">
        <button v-for="item in orders" :key="item.text" type="button" @click="goOrder(item)">
          <van-icon :name="item.icon" />
          <span>{{ item.text }}</span>
        </button>
      </div>
    </section>

  </main>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const bgImage = new URL('../../assets/images/img_public/5.png', import.meta.url).href
const username = localStorage.getItem('username') || ''
const maskedName = `${username.slice(0, 1)}**`

const headerActions = [
  { icon: 'setting-o', text: '设置', route: '/settings' },
]


const orders = [
  // { icon: 'balance-o', text: '待支付' },
  { icon: 'hotel-o', text: '待打扫', route: '/clean' },
  // { icon: 'chat-o', text: '待评价', route: '' },
  // { icon: 'bill-o', text: '待开票' }
]

const goOrder = (item) => {
  if (item.route) {
    router.push(item.route)
  }
}

</script>

<style scoped>
.mine-page {
  min-height: 100vh;
  padding: 0 1rem 6.2rem;
  background: #f6f6f6;
  color: #1f1f1f;
  text-align: left;
}

.hero {
  position: relative;
  margin: 0 -1rem;
  padding: 2.2rem 1rem 4.5rem;
  background-position: center;
  background-size: cover;
}

.top-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.profile {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 0.85rem;
}

.avatar {
  display: grid;
  place-items: center;
  flex: none;
  width: 4rem;
  height: 4rem;
  border-radius: 50%;
  background: #e8e4f5;
  color: #9a8cc6;
  font-size: 2.2rem;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 1.35rem;
}

.quick-actions {
  display: flex;
  gap: 0.85rem;
}

.quick-actions button,
.order-grid button {
  border: 0;
  background: transparent;
  color: inherit;
}

.quick-actions button {
  display: grid;
  gap: 0.25rem;
  justify-items: center;
  font-size: 0.82rem;
}

.quick-actions .van-icon {
  font-size: 1.65rem;
}

.card {
  margin-bottom: 1rem;
  border-radius: 0.75rem;
  background: #fff;
  box-shadow: 0 0.4rem 1.1rem rgba(15, 23, 42, 0.04);
}

.order-card {
  margin-top: 1rem;
  padding: 1.15rem 1rem;
}

.tab-row {
  display: grid;
  grid-template-columns: 1.15fr 1fr 1fr auto;
  align-items: center;
  gap: 0.8rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f0f0f0;
  color: #777;
  font-size: 0.95rem;
}

.tab-row strong {
  color: #222;
  font-size: 1.35rem;
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem 0.65rem;
  padding-top: 1.15rem;
}

.order-grid button {
  display: grid;
  justify-items: center;
  gap: 0.45rem;
  font-size: 0.9rem;
  white-space: nowrap;
}

.order-grid .van-icon {
  color: #222;
  font-size: 1.75rem;
}

@media (max-width: 380px) {
  .quick-actions {
    gap: 0.55rem;
  }

  .quick-actions .van-icon {
    font-size: 1.45rem;
  }

  .tab-row {
    gap: 0.45rem;
    font-size: 0.82rem;
  }

  .tab-row strong {
    font-size: 1.15rem;
  }
}
</style>
