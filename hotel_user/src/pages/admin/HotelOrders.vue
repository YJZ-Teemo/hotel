<template>
  <main class="orders-page">
    <header class="page-header">
      <button type="button" class="back-btn" @click="router.back()">
        <van-icon name="arrow-left" />
      </button>
      <button type="button" class="title-btn">
        <span>酒店订单</span>
        <van-icon name="arrow-down" />
      </button>
    </header>

    <nav class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab"
        type="button"
        :class="{ active: activeTab === tab }"
        @click="switchTab(tab)"
      >
        {{ tab }}
      </button>
    </nav>

    <section class="guarantee">
      <strong>安心订 贵即赔</strong>
      <span>降价了可以退 订贵了可以赔</span>
      <van-icon name="arrow" />
    </section>

    <section class="order-list">
      <article v-for="order in visibleOrders" :key="order.id" class="order-card">
        <img class="cover" :src="order.cover" :alt="order.hotel" />
        <div class="order-info">
          <div class="title-row">
            <h2>{{ order.hotel }}</h2>
            <span>{{ order.status }}</span>
            <span style="display:none">{{ order.checkInNum }}</span>
          </div>
          <p>{{ order.dates }}</p>
          <p class="room-line">
            <span>{{ order.room }}</span>
            <strong>￥{{ order.price }}</strong>
          </p>
          <div class="action-row">
            <button v-if="activeTab !== '全部' && order.tab !== '待入住' && !order.evaluated" type="button" class="book-btn" @click="goReview(order)">评价</button>
            <button type="button" class="delete-btn" aria-label="删除订单">
              <van-icon name="delete-o" />
            </button>
          </div>
        </div>
      </article>
    </section>

    <p class="footer-note">仅展示最近3年的订单</p>
  </main>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showNotify } from 'vant'
import api from '../../api'

const router = useRouter()
const route = useRoute()
const tabs = ['全部', '待入住', '待评价']
const activeTab = ref(tabs.includes(route.query.tab) ? route.query.tab : '全部')
const cover = new URL('../../assets/images/img_public/5.png', import.meta.url).href
const phone = localStorage.getItem('phone')
const roomTypeMap = { suite: '套房', standard: '标准间', deluxe: '豪华间' }

const orders = ref([])

const visibleOrders = computed(() => {
  if (activeTab.value === '全部') return orders.value
  return orders.value.filter((order) => order.tab === activeTab.value)
})

const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === '全部') {
    loadMyReserve()
  } else if (tab === '待入住') {
    loadMyReserve2()
  } else {
    loadHistory()
  }
}

const goReview = (order) => {
  console.log('订单详情:', order.checkInNum)
  console.log('订单详情:', order.room)
  router.push({
    path: '/order-review',
    query: {
      checkinNum: order.checkInNum,
      roomType: order.room
    }
  })
}
const loadHistory = async () => {
  try {
    const response = await api.post('/check/history', {
      cphone: phone
    })

    const data = response.data

    if (data.status === '200') {
      orders.value = (data.data || []).map((item, index) => {
        const checkIn = item.checkindate || ''
        const checkOut = item.checkoutdate || ''
        const nights = checkIn && checkOut
          ? Math.ceil((new Date(checkOut) - new Date(checkIn)) / 86400000)
          : 1
        return {
          id: item.id || index + 1,
          hotel: item.hotel || '广州xx酒店',
          status: item.status || '已完成',
          tab: item.tab || '待评价',
          dates: `${checkIn}-${checkOut}`,
          nights: `共${nights}晚`,
          room: roomTypeMap[item.rtype] || item.rtype || '',
          count: `${item.count || 1}间`,
          price: item.totalamount || '0',
          cover: item.cover || cover,
          evaluated: item.evaluated || false,
          checkInNum: item.checkinNum || ''
        }
      })
    } else {
      showNotify({ type: 'danger', message: data.message || '查询失败' })
    }
  } catch (error) {
    showNotify({ type: 'danger', message: '网络错误，请稍后重试' })
  }
}
const loadMyReserve = async () => {
  try {
    const response = await api.post('/Reserve/myReserve', {
      phone: phone
    })

    const data = response.data

    if (data.status === '200') {
      orders.value = (data.data || []).map((item, index) => {
        const checkIn = item.checkIn || ''
        const checkOut = item.checkOut || ''
        const nights = checkIn && checkOut
          ? Math.ceil((new Date(checkOut) - new Date(checkIn)) / 86400000)
          : 1
        return {
          id: item.reserveId || index + 1,
          hotel: item.hotel || '广州xx酒店',
          status: item.state === '已完成' ? '已完成' : item.state === '已确认' ? '待入住' : (item.state || ''),
          tab: item.state === '已完成' ? '待评价' : item.state === '已确认' ? '待入住' : '',
          dates: `${checkIn}-${checkOut}`,
          nights: `共${nights}晚`,
          room: roomTypeMap[item.rtype] || item.rtype || '',
          count: `${item.count || 1}间`,
          price: item.price || '0',
          cover: item.cover || cover
        }
      })
    } else {
      showNotify({ type: 'danger', message: data.message || '查询失败' })
    }
  } catch (error) {
    showNotify({ type: 'danger', message: '网络错误，请稍后重试' })
  }
}
const loadMyReserve2 = async () => {
  try {
    const response = await api.post('/Reserve/myReserve2', {
      phone: phone
    })

    const data = response.data

    if (data.status === '200') {
      orders.value = (data.data || []).map((item, index) => {
        const checkIn = item.checkIn || ''
        const checkOut = item.checkOut || ''
        const nights = checkIn && checkOut
          ? Math.ceil((new Date(checkOut) - new Date(checkIn)) / 86400000)
          : 1
        return {
          id: item.reserveId || index + 1,
          hotel: item.hotel || '广州xx酒店',
          status: item.state === '已完成' ? '已完成' : item.state === '已确认' ? '待入住' : (item.state || ''),
          tab: item.state === '已完成' ? '待评价' : item.state === '已确认' ? '待入住' : '',
          dates: `${checkIn}-${checkOut}`,
          nights: `共${nights}晚`,
          room: roomTypeMap[item.rtype] || item.rtype || '',
          count: `${item.count || 1}间`,
          price: item.price || '0',
          cover: item.cover || cover
        }
      })
    } else {
      showNotify({ type: 'danger', message: data.message || '查询失败' })
    }
  } catch (error) {
    showNotify({ type: 'danger', message: '网络错误，请稍后重试' })
  }
}

onMounted(() => {
  loadHistory()
})

</script>

<style scoped>
.orders-page {
  min-height: 100vh;
  padding: 0 1rem 2rem;
  background: #f6f6f6;
  color: #222;
  text-align: left;
}

.page-header {
  display: grid;
  grid-template-columns: 3rem 1fr 3rem;
  align-items: center;
  height: 5.2rem;
  background: #fff;
  margin: 0 -1rem;
  padding: 1rem 1rem 0;
}

.back-btn,
.title-btn,
.invoice-btn,
.tabs button,
.sort-row button,
.book-btn,
.delete-btn {
  border: 0;
  background: transparent;
  color: inherit;
}

.back-btn {
  display: grid;
  place-items: center;
  width: 2.4rem;
  height: 2.4rem;
  padding: 0;
  font-size: 1.65rem;
}

.title-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  justify-self: center;
  gap: 0.25rem;
  font-size: 1.35rem;
  font-weight: 500;
}

.invoice-btn {
  justify-self: end;
  font-size: 1.05rem;
}

.tabs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin: 0 -1rem;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.tabs button {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 4rem;
  white-space: nowrap;
  font-size: 1.05rem;
}

.tabs button.active::after {
  content: "";
  position: absolute;
  right: 1.05rem;
  bottom: 0.75rem;
  left: 1.05rem;
  height: 0.2rem;
  border-radius: 999px;
  background: #222;
}

.guarantee {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 0.8rem;
  margin-top: 1.1rem;
  padding: 1.1rem 1rem;
  border-radius: 0.55rem;
  background: #fff;
  box-shadow: 0 0.45rem 1.2rem rgba(15, 23, 42, 0.04);
}

.guarantee strong {
  color: #59459d;
  font-size: 1rem;
}

.guarantee span {
  justify-self: end;
  white-space: nowrap;
  color: #666;
  font-size: 0.95rem;
}

.guarantee .van-icon {
  color: #888;
}

.order-list {
  display: grid;
  gap: 1rem;
  margin-top: 1rem;
}

.order-card {
  display: grid;
  grid-template-columns: 7.1rem minmax(0, 1fr);
  overflow: hidden;
  border-radius: 0.55rem;
  background: #fff;
}

.cover {
  width: 100%;
  height: 8.4rem;
  object-fit: cover;
}

.order-info {
  min-width: 0;
  padding: 0.8rem 0.9rem 0.7rem;
}

.title-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 0.55rem;
  align-items: start;
}

.title-row h2 {
  margin: 0;
  overflow: hidden;
  font-size: 1.05rem;
  font-weight: 500;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.title-row span {
  color: #555;
  font-size: 0.95rem;
}

.order-info p {
  margin: 0.45rem 0 0;
  color: #666;
  font-size: 0.95rem;
}

.room-line {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  align-items: center;
  gap: 0.5rem;
}

.room-line span {
  min-width: 0;
  margin-left: 0;
  text-align: left;
}

.room-line strong {
  flex: none;
  color: #e83442;
  font-size: 1rem;
  font-weight: 400;
}

.action-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.6rem;
  margin-top: 0.8rem;
}

.book-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 4em;
  height: 1.4rem;
  padding: 0 0.5rem;
  border: 1px solid #ddd;
  border-radius: 999px;
  background: #fff;
  font-size: 0.78rem;
  line-height: 1;
}

.delete-btn {
  display: grid;
  place-items: center;
  width: 2rem;
  height: 2rem;
  padding: 0;
  color: #333;
  font-size: 1.2rem;
}

.footer-note {
  margin: 2.2rem 0 0;
  color: #999;
  font-size: 0.95rem;
  text-align: center;
}

@media (max-width: 380px) {
  .orders-page {
    padding-right: 0.75rem;
    padding-left: 0.75rem;
  }

  .page-header,
  .tabs {
    margin-right: -0.75rem;
    margin-left: -0.75rem;
  }

  .order-card {
    grid-template-columns: 6rem minmax(0, 1fr);
  }

  .cover {
    height: 8rem;
  }

  .action-row {
    gap: 0.35rem;
  }

  .room-line strong {
    font-size: 1rem;
  }

  .book-btn {
    min-width: 4em;
  }
}
</style>
