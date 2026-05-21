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
          <button v-for="item in headerActions" :key="item.text" type="button">
            <van-icon :name="item.icon" />
            <span>{{ item.text }}</span>
          </button>
        </div>
      </div>

      <section class="member-card">
        <div class="member-head">
          <h1>{{ memberLevel }}</h1>
          <button type="button">会员中心</button>
        </div>
        <div class="member-benefits">
          <span><van-icon name="gem-o" /> 房费{{ memberConfig.discount }}折</span>
          <span><van-icon name="clock-o" /> {{ memberConfig.checkout }}</span>
          <span><van-icon name="star-o" /> {{ memberConfig.points }}</span>
        </div>
      </section>
    </section>

    <section class="asset-panel">
      <div v-for="item in assets" :key="item.label" class="asset-item">
        <strong>{{ item.value }}<small>{{ item.unit }}</small></strong>
        <span>{{ item.label }}</span>
      </div>
    </section>

    <section class="card order-card">
      <div class="tab-row">
        <strong>酒店订单</strong>
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
const memberLevel = localStorage.getItem('Cardlevel') || localStorage.getItem('memberLevel') || '星会员'
const memberMap = {
  '星会员': { discount: '9.5', rate: 0.95, points: '1倍积分', pointstext: 1 ,checkout: '延迟至13:00点'},
  '二星会员': { discount: '9.0', rate: 0.90, points: '1.2倍积分', pointstext: 1.2,checkout: '延迟至14:00点' },
  '三星会员': { discount: '8.5', rate: 0.85, points: '1.5倍积分', pointstext: 1.5 ,checkout: '延迟至15:00点'},
  '四星会员': { discount: '8.0', rate: 0.80, points: '2倍积分', pointstext: 2 ,checkout: '延迟至16:00点'},
  '五星会员': { discount: '7.5', rate: 0.75, points: '2.5倍积分', pointstext: 2.5,checkout: '延迟至17:00点' }
}
const memberConfig = memberMap[memberLevel] || memberMap['星会员']
const points = localStorage.getItem('Points') || '0'

const headerActions = [
  { icon: 'qr', text: '会员码' },
  { icon: 'setting-o', text: '设置' },
  { icon: 'service-o', text: '客服' }
]

const assets = [
  { value: '0', unit: '张', label: '优惠券' },
  { value: points, unit: '', label: '积分' },
  { value: '0', unit: '张', label: '卡包' }
]

const orders = [
  { icon: 'balance-o', text: '待支付' },
  { icon: 'hotel-o', text: '待入住' },
  { icon: 'chat-o', text: '待评价', route: '/hotel-orders?tab=待评价' },
  { icon: 'bill-o', text: '待开票' }
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
.order-grid button,
.tool-grid button,
.link-card button {
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

.member-card {
  position: absolute;
  right: 1rem;
  bottom: -2.55rem;
  left: 1rem;
  padding: 1.25rem;
  border-radius: 1.3rem;
  background: rgba(226, 230, 250, 0.9);
  box-shadow: 0 0.8rem 1.8rem rgba(80, 88, 122, 0.16);
}

.member-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.member-head h1 {
  margin: 0;
  color: #5f6f9f;
  font-size: 1.8rem;
}

.member-head button,
.notice-pill button {
  border: 0;
  border-radius: 999px;
  background: #7663b7;
  color: #fff;
  font-size: 0.9rem;
  padding: 0.45rem 0.9rem;
}

.member-benefits {
  display: flex;
  flex-wrap: wrap;
  gap: 0.9rem;
  margin-top: 1rem;
  color: #5f6680;
  font-size: 0.92rem;
}

.member-benefits span {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
}

.asset-panel {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  margin: 3.8rem -1rem 0;
  padding: 1.2rem 1rem;
  background: #fff;
}

.asset-item {
  display: grid;
  gap: 0.35rem;
  justify-items: center;
}

.asset-item strong {
  font-size: 1.65rem;
  font-weight: 500;
}

.asset-item small,
.asset-item span {
  color: #666;
  font-size: 0.92rem;
}

.notice-pill {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 1rem 0;
  padding: 0.8rem 1rem;
  border-radius: 999px;
  background: #fff3f4;
}

.notice-pill span {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
}

.notice-pill .van-icon {
  color: #8757cf;
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

.order-grid,
.tool-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem 0.65rem;
  padding-top: 1.15rem;
}

.order-grid button,
.tool-grid button {
  display: grid;
  justify-items: center;
  gap: 0.45rem;
  font-size: 0.9rem;
  white-space: nowrap;
}

.order-grid .van-icon,
.tool-grid .van-icon {
  color: #222;
  font-size: 1.75rem;
}

.link-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  padding: 1rem 0;
}

.link-card button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.45rem;
  min-width: 0;
  font-size: 1rem;
}

.link-card button + button {
  border-left: 1px solid #eee;
}

.survey-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
  padding: 1.2rem 1.15rem;
  border-radius: 0.75rem;
  background: linear-gradient(110deg, #ebe5ff 0%, #f8f4ff 58%, #eef2ff 100%);
  color: #222;
}

.survey-banner h2 {
  margin: 0;
  font-size: 1.35rem;
}

.survey-banner p {
  display: inline-block;
  margin: 0.65rem 0 0;
  padding: 0.4rem 0.75rem;
  border-radius: 999px;
  background: #895de8;
  color: #fff;
  font-size: 0.82rem;
}

.survey-banner .van-icon {
  color: #895de8;
  font-size: 3.2rem;
}

.tools-card {
  padding: 1.2rem 1rem;
}

.tools-card h2 {
  margin: 0;
  font-size: 1.35rem;
}

.tool-grid {
  row-gap: 1.3rem;
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
