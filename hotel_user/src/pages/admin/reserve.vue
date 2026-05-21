<template>
  <main class="reserve-page">
    <van-nav-bar
      class="page-header"
      title="广州xx酒店"
      left-arrow
      fixed
      placeholder
      @click-left="goBack"
    />

    <section class="room-summary">
      <div class="date-row">
        <span>{{ roomInfo.startDateText }}</span>
        <span class="dash">-</span>
        <span>{{ roomInfo.endDateText }}</span>
      </div>
      <div class="room-name">{{ roomInfo.name }}</div>
      <div class="room-meta">{{ roomInfo.area }}m²&nbsp;&nbsp;|&nbsp;&nbsp;{{ roomInfo.bed }}&nbsp;&nbsp;|&nbsp;&nbsp;外景窗</div>
      <div class="notice-row">
      </div>
    </section>

    <section class="content">
      <article class="card order-card">
        <div class="card-title-row">
          <div>
            <span class="section-title">订房信息</span>
            <span class="speed-tag">30秒入住<van-icon name="arrow" /></span>
          </div>
        </div>

        <div class="form-row">
          <span>入住人 <van-icon name="question-o" class="question" /></span>
          <strong>{{ name }}</strong>
          <van-icon name="contact-o" class="contact-icon" />
        </div>
        <div class="form-row phone-row">
          <span>联系手机</span>
          <strong>+86 <van-icon name="arrow-down" class="down" />{{ phone }}</strong>
        </div>
      </article>

      <article class="card benefit-card">
        <h2>本人入住可享</h2>
        <van-cell :is-link="pointTotal > 0" clickable @click="openPointPopup">
          <template #title>积分抵扣</template>
          <template #value>
            <span v-if="usedPoints > 0" class="red">已用{{ usedPoints }}积分(抵￥{{ pointDiscount }})</span>
            <span v-else>最高可用<span class="red">{{ pointTotal }}积分(抵￥{{ maxPointDiscount }})</span></span>
          </template>
        </van-cell>
        <van-popup v-model:show="showPointPopup" position="bottom" round>
          <section class="point-popup">
            <h3>积分抵扣</h3>
            <p>当前可用 {{ pointTotal }} 积分，最多可抵￥{{ maxPointDiscount }}</p>
            <van-field
              v-model="pointInput"
              type="digit"
              label="使用积分"
              :placeholder="`最多输入${pointTotal}`"
            />
            <div class="point-actions">
              <van-button plain round type="default" @click="showPointPopup = false">取消</van-button>
              <van-button round type="primary" @click="confirmPointDeduction">确定</van-button>
            </div>
          </section>
        </van-popup>

        <div class="perk-grid">
          <van-button v-for="item in perks" :key="item.title" class="perk-item" plain type="default">
            <span class="perk-title">{{ item.title }} 
                <small v-if="item.badge">{{ item.badge }}</small>
            </span>
            <span class="perk-desc">{{ item.desc }}</span>
            <van-icon name="arrow" />
          </van-button>
        </div>
      </article>

      <van-cell
        class="card arrival-card"
        title="预计到店"
        :value="arrivalTimeText"
        is-link
        clickable
        @click="showArrivalPicker = true"
      />
      <van-popup v-model:show="showArrivalPicker" position="bottom" round>
        <van-time-picker
          v-model="currentTime"
          title="选择时间"
          :min-hour="14"
          :max-hour="23"
          :filter="filterArrivalTime"
          @confirm="confirmArrivalTime"
          @cancel="showArrivalPicker = false"
        />
      </van-popup>
      <article class="card request-card">
        <h2>特殊要求</h2>
        <van-field
          v-model="remark"
          type="textarea"
          maxlength="150"
          rows="3"
          placeholder="备注：可填写其他要求，150字以内"
          show-word-limit
        />
      </article>


      <article class="guarantee-card">
        <h2>安心订 贵即赔</h2>
        <div class="guarantee-top">
          <div>
            <strong>降价了可以退</strong>
            <p>赔付方式可选</p>
          </div>
          <div>
            <strong>订贵了可以赔</strong>
            <p>发现更低价赔付<span>2</span>倍积分</p>
          </div>
        </div>
        <div class="pay-options">
          <van-radio-group v-model="compensation">
            <van-cell title="赔现金" label="退回原支付渠道" clickable @click="compensation = 'cash'">
              <template #right-icon>
                <van-radio name="cash" checked-color="#6543a2" />
              </template>
            </van-cell>
            <van-cell title="赔积分" label="离店后返还积分" clickable @click="compensation = 'point'">
              <template #right-icon>
                <van-radio name="point" checked-color="#6543a2" />
              </template>
            </van-cell>
          </van-radio-group>
        </div>
      </article>

      <p class="privacy-text">
        温馨提示：我们将收集您的姓名、联系方式、所选酒店名称以及入住与退房时间，
        用于实现您的酒店预订与入住服务。
        具体入住服务由您预订的酒店门店提供。
      </p>
    </section>

    <footer class="submit-bar">
      <div class="amount">
        <span>订单金额</span>
        <strong><small>¥</small>{{ finalPrice }}</strong>
      </div>
      <van-button class="submit-button" type="primary" round @click="submitOrder">提交订单</van-button>
    </footer>
  </main>
</template>

<script setup>
import { computed, onActivated, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showNotify } from 'vant'
import api from '../../api'

const router = useRouter()
const route = useRoute()
const roomCount = ref(1)
const remark = ref('')
const needInvoice = ref(false)
const sesamePay = ref(false)
const compensation = ref('cash')
const showArrivalPicker = ref(false)
const showPointPopup = ref(false)
const pointInput = ref('')
const usedPoints = ref(0)
const currentTime = ref(['14', '00'])
const selectedArrivalTime = ref([...currentTime.value])
const arrivalTimeText = computed(() => `${selectedArrivalTime.value[0]}:${selectedArrivalTime.value[1]}`)
const name = localStorage.getItem('Name')
const phone = localStorage.getItem('phone')
const Points = ref(0)
const loadPoints = () => {
  const customerInfo = JSON.parse(localStorage.getItem('customerInfo') || '{}')
  Points.value = Number(
    localStorage.getItem('Points') ||
    localStorage.getItem('points') ||
    customerInfo.Points ||
    customerInfo.points ||
    0
  )
  if (usedPoints.value > Points.value) {
    usedPoints.value = Points.value
  }
}
const pointTotal = computed(() => Number(Points.value) || 0)
const roomPrice = computed(() => Number(roomInfo.value.newPrice) || 0)
const maxPointDiscount = computed(() => Math.min(pointTotal.value / 100, roomPrice.value).toFixed(2))
const pointDiscount = computed(() => (usedPoints.value / 100).toFixed(2))
const finalPrice = computed(() => Math.max(roomPrice.value - usedPoints.value / 100, 0).toFixed(2))
const filterArrivalTime = (type, options, values) => {
  if (type === 'minute' && values[0] === '23') {
    return options.filter((option) => Number(option.value) <= 30)
  }
  return options
}

const typeMap = {
  'suite': { 
    name_cn: '套房',
    name_en: 'suite', 
    bed: '1张2*2.2米床', 
    area: '80',
},
  'standard': { name_cn: '标准间', name_en: 'standard', bed: '1张2*1.8米床', area: '20' },
  'deluxe': { name_cn: '豪华间', name_en: 'deluxe', bed: '1张2*1.8米床', area: '40' }
};

const getTypeConfig = (type) => {
  return typeMap[type] || { name_cn: type, name_en: '', bed: '', area: '' };
};

const getQueryValue = (key, defaultValue = '') => {
  const value = route.query[key]
  return Array.isArray(value) ? value[0] || defaultValue : value || defaultValue
}

const formatDateText = (value) => {
  if (!value) return ''
  const matched = String(value).match(/^(\d{4})-(\d{2})-(\d{2})$/)
  if (!matched) return value
  return `${Number(matched[2])}月${Number(matched[3])}日`
}

const formatDateValue = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const today = new Date()
const tomorrow = new Date()
tomorrow.setDate(today.getDate() + 1)

const roomInfo = computed(() => {
  const config = getTypeConfig(getQueryValue('roomType', 'standard'))
  return {
    name: getQueryValue('roomName', config.name_cn),
    area: getQueryValue('area', config.area),
    bed: getQueryValue('bed', config.bed),
    newPrice: getQueryValue('newPrice', config.newPrice),
    startDateText: formatDateText(getQueryValue('startDate', formatDateValue(today))),
    endDateText: formatDateText(getQueryValue('endDate', formatDateValue(tomorrow)))
  }
})

const levelMap = {
    '星会员': {
        checkout: '延迟至13:00点',
        breakfast: '免费早餐一份',
    },
    '二星会员': {
        checkout: '延迟至14:00点',
        breakfast: '免费早餐两份',
    },
    '三星会员': {
        checkout: '延迟至15:00点',
        breakfast: '免费早餐两份',
    },
    '四星会员': {
        checkout: '延迟至16:00点',
        breakfast: '免费早餐两份',
    },
    '五星会员': {
        checkout: '延迟至17:00点',
        breakfast: '免费早餐两份',
    }
}
const getLevelConfig = (level) => {
    return levelMap[level] || { checkout: '', breakfast: '' };
};

const userLevel = computed(() => getQueryValue('cardLevel', '星会员'))
const levelConfig = computed(() => getLevelConfig(userLevel.value))
const roomPoints = computed(() => getQueryValue('points', '684'))
const perks = computed(() => [
  { title: '延迟退房', desc: levelConfig.value.checkout || '延迟至13:00点' },
  { title: '早餐', desc: levelConfig.value.breakfast || '可加购' },
  { title: '赠积分', badge: '限本人', desc: `离店赠${roomPoints.value}积分` },
  { title: '赠间夜', badge: '限本人', desc: '离店赠1间夜' }
])

const goBack = () => {
  if (window.history.length > 1) {
    router.back()
    return
  }
  router.push('/admin')
}

const confirmArrivalTime = ({ selectedValues }) => {
  selectedArrivalTime.value = selectedValues
  currentTime.value = selectedValues
  showArrivalPicker.value = false
}

const openPointPopup = () => {
  if (pointTotal.value <= 0) return
  pointInput.value = usedPoints.value ? String(usedPoints.value) : ''
  showPointPopup.value = true
}

const confirmPointDeduction = () => {
  const maxUsablePoints = Math.min(pointTotal.value, Math.floor(roomPrice.value * 100))
  const inputPoints = Number(pointInput.value) || 0
  usedPoints.value = Math.min(Math.max(inputPoints, 0), maxUsablePoints)
  showPointPopup.value = false
}
const submitOrder = async () => {
  try {
    const response = await api.post('/Reserve/add', {
      cname: name,
      roomType: getQueryValue('roomType'),
      price: roomInfo.value.newPrice,
      checkIn: getQueryValue('startDate'),
      checkOut: getQueryValue('endDate'),
      reservationDate: formatDateValue(new Date()),
      breakfastCount: roomPoints.value,
      remark: remark.value
    })

    const data = response.data

    if (data.status === '200') {
      showNotify ({ type: 'success', message: '订单提交成功' })
      router.push('/')
    } else {
      showNotify ({ type: 'danger', message: '订单提交失败: ' + (data.message || '请稍后重试') })
    }
  } catch (error) {
    showNotify ({ type: 'danger', message: '网络错误，请稍后重试' })
  }
}


onMounted(loadPoints)
onActivated(loadPoints)
</script>

<style scoped>
.reserve-page {
  min-height: 100vh;
  padding-bottom: 5.75rem;
  background: #eef7f5;
  color: #1f1f1f;
  text-align: left;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif;
}

.page-header {
  z-index: 10;
  background: #fff;
}

.page-header :deep(.van-nav-bar) {
  height: 5.5rem;
  padding-top: 1.75rem;
  box-sizing: border-box;
}

.page-header :deep(.van-nav-bar__title) {
  max-width: 72%;
  color: #222;
  font-size: 1.15rem;
  font-weight: 500;
}

.page-header :deep(.van-icon) {
  color: #222;
  font-size: 1.8rem;
}

.room-summary {
  padding: 1.35rem 1.25rem 0;
  background: #fff;
}

.date-row,
.notice-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #111;
  font-size: 1.12rem;
  line-height: 1.45;
}

.dash {
  color: #777;
}

.night {
  margin-left: 0.25rem;
}

.link-button {
  margin-left: auto;
  padding: 0;
  border: 0;
  background: transparent;
  color: #18627a;
  font-size: 0.95rem;
  font-weight: 600;
  height: auto;
}

.room-name {
  margin-top: 0.65rem;
  color: #161616;
  font-size: 1.08rem;
  line-height: 1.5;
}

.room-name span {
  color: #d9d9d9;
  margin: 0 0.4rem;
}

.room-meta {
  margin-top: 0.35rem;
  color: #9a9a9a;
  font-size: 0.9rem;
}

.notice-row {
  margin-top: 1.15rem;
  padding: 0.95rem 0;
  border-top: 1px solid #f0f0f0;
  font-size: 0.9rem;
}

.content {
  padding: 0.85rem 1.15rem 1.25rem;
}

.card,
.guarantee-card {
  margin-bottom: 0.85rem;
  border-radius: 0.5rem;
  background: #fff;
  box-shadow: 0 0.5rem 1.4rem rgba(24, 50, 45, 0.04);
}

.order-card,
.benefit-card,
.request-card {
  padding: 1rem;
}

.card-title-row,
.benefit-line,
.arrival-card,
.switch-card,
.green-card,
.sesame-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title,
.benefit-card h2,
.request-card h2 {
  margin: 0;
  color: #161616;
  font-size: 1.35rem;
  font-weight: 500;
}

.speed-tag {
  display: inline-flex;
  align-items: center;
  margin-left: 0.45rem;
  padding: 0.12rem 0.35rem;
  border: 1px solid #f0f0f0;
  border-radius: 0.15rem;
  color: #333;
  font-size: 0.75rem;
  vertical-align: middle;
}

.room-count :deep(.van-stepper__input) {
  width: 2.1rem;
  background: transparent;
  color: #151515;
  font-size: 1.18rem;
}

.room-count :deep(.van-stepper__minus),
.room-count :deep(.van-stepper__plus) {
  border-color: #5c3d91;
  color: #5c3d91;
}

.room-count :deep(.van-stepper__minus--disabled) {
  border-color: #d9d9d9;
  color: #cfcfcf;
}

.form-row {
  position: relative;
  display: grid;
  grid-template-columns: 6.4rem 1fr auto;
  align-items: center;
  min-height: 3rem;
  margin-top: 0.65rem;
  color: #171717;
  font-size: 1.05rem;
}

.form-row strong {
  font-weight: 500;
}

.question {
  color: #bbb;
  font-size: 1rem;
  vertical-align: -0.08rem;
}

.contact-icon {
  color: #5c3d91;
  font-size: 2.15rem;
}

.down {
  color: #999;
  font-size: 0.9rem;
}

.benefit-card h2 {
  margin-bottom: 1rem;
}

.benefit-card :deep(.van-cell) {
  padding: 0.75rem 0;
  color: #222;
  font-size: 1.02rem;
}

.benefit-card :deep(.van-cell__title) {
  flex: none;
}

.benefit-card :deep(.van-cell::after) {
  border: 0;
}

.benefit-card :deep(.van-cell__value) {
  flex: 1;
  color: #9b9b9b;
  font-size: 0.92rem;
  white-space: nowrap;
}

.red {
  color: #cf3f43;
}

.member-line {
  margin-top: 0.2rem;
}

.diamond {
  display: inline-block;
  width: 1.05rem;
  height: 1.05rem;
  margin-right: 0.38rem;
  transform: rotate(45deg);
  border: 0.25rem solid #8b97a6;
  border-left-color: #c6d0dc;
  border-bottom-color: #c6d0dc;
  vertical-align: -0.12rem;
}

.perk-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.65rem;
  margin-top: 0.9rem;
}

.perk-item {
  position: relative;
  min-height: 5.2rem;
  padding: 0.85rem 1.75rem 0.75rem 0.8rem;
  border: 0;
  border-radius: 0.28rem;
  background: #f8f6fc;
  color: #1f1f1f;
  text-align: left;
  height: auto;
}

.perk-item :deep(.van-button__content) {
  display: block;
  width: 100%;
  text-align: left;
}

.perk-title {
  display: block;
  color: #61428f;
  font-size: 1.05rem;
  line-height: 1.2;
}

.perk-title small {
  margin-left: 0.3rem;
  padding: 0.05rem 0.25rem;
  border: 1px solid #e8e2ee;
  color: #555;
  font-size: 0.7rem;
}

.perk-desc {
  display: block;
  margin-top: 0.6rem;
  color: #555;
  font-size: 0.92rem;
  line-height: 1.25;
}

.perk-item .van-icon {
  position: absolute;
  top: 1.1rem;
  right: 0.7rem;
  color: #555;
}

.switch-card {
  min-height: 4.25rem;
  padding: 0 1rem;
  color: #111;
  font-size: 1.1rem;
}

.arrival-card {
  padding: 1.15rem 1rem;
  color: #111;
  font-size: 1.1rem;
}

.arrival-card :deep(.van-cell__value) {
  color: #111;
  font-size: 1.15rem;
  font-weight: 500;
}

.request-card h2 {
  margin-bottom: 0.85rem;
  font-size: 1.12rem;
}

.request-card :deep(.van-cell) {
  border: 1px solid #ececec;
  border-radius: 0.25rem;
  padding: 0.55rem 0.75rem;
  color: #333;
  font-size: 0.94rem;
}

.request-card :deep(.van-field__control::placeholder) {
  color: #aaa;
}

.green-card {
  padding: 1rem;
}

.leaf-icon,
.sesame-title i {
  display: grid;
  place-items: center;
  flex: none;
  width: 1.45rem;
  height: 1.45rem;
  margin-right: 0.62rem;
  border-radius: 50%;
  background: #20c37a;
  color: #fff;
  font-size: 1rem;
  font-style: normal;
}

.green-card div:nth-child(2) {
  flex: 1;
}

.green-card h2 {
  margin: 0;
  font-size: 1.15rem;
  font-weight: 500;
}

.green-card p,
.sesame-title p {
  margin: 0.15rem 0 0;
  color: #9a9a9a;
  font-size: 0.92rem;
}

.sesame-title {
  display: flex;
  align-items: center;
}

.sesame-title i {
  background: #17a7df;
}

.guarantee-card {
  padding: 1.1rem 0.95rem 1rem;
  background: linear-gradient(180deg, #f7f2ff 0%, #fff 34%);
}

.guarantee-card h2 {
  margin: 0 0 1.2rem;
  color: #594194;
  font-size: 1.35rem;
  font-weight: 700;
  text-align: center;
}

.guarantee-top {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.guarantee-top div + div {
  border-left: 1px solid #eee;
  padding-left: 1rem;
}

.guarantee-top strong {
  color: #171717;
  font-size: 1.08rem;
  font-weight: 500;
}

.guarantee-top p {
  margin: 0.35rem 0 0;
  color: #777;
  font-size: 0.9rem;
}

.guarantee-top span {
  color: #cf3f43;
}

.pay-options {
  padding: 0.45rem 0.85rem;
  border-radius: 0.28rem;
  background: #fff;
  box-shadow: 0 0.35rem 1.3rem rgba(40, 30, 65, 0.08);
}

.pay-options :deep(.van-cell) {
  padding: 0.35rem 0;
}

.pay-options :deep(.van-cell::after) {
  border: 0;
}

.pay-options :deep(.van-cell__title) {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  color: #222;
  font-size: 0.98rem;
}

.pay-options :deep(.van-cell__label) {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.point-popup {
  padding: 1.25rem 1rem 1.5rem;
  background: #fff;
}

.point-popup h3 {
  margin: 0 0 0.65rem;
  color: #222;
  font-size: 1.15rem;
  text-align: center;
}

.point-popup p {
  margin: 0 0 1rem;
  color: #666;
  font-size: 0.92rem;
  text-align: center;
}

.point-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.8rem;
  margin-top: 1.1rem;
}

.privacy-text {
  margin: 1rem 0 7.2rem;
  color: #9b9b9b;
  font-size: 0.9rem;
  line-height: 1.65;
}

.submit-bar {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 8.5rem;
  align-items: center;
  gap: 0.85rem;
  min-height: 5.4rem;
  padding: 0.8rem 1.15rem max(0.8rem, env(safe-area-inset-bottom));
  background: #fff;
  box-shadow: 0 -0.25rem 1rem rgba(0, 0, 0, 0.05);
}

.amount {
  position: relative;
  display: flex;
  align-items: baseline;
  gap: 0.28rem;
  min-width: 0;
  color: #111;
}

.amount span {
  flex: none;
  font-size: 1.08rem;
}

.amount strong {
  color: #f0292f;
  font-size: 1.95rem;
  line-height: 1;
}

.amount small,
.amount strong span {
  font-size: 0.9rem;
  font-weight: 500;
}

.amount em {
  position: absolute;
  left: 8.3rem;
  top: -0.9rem;
  padding: 0.08rem 0.28rem;
  border-radius: 0.22rem;
  background: #54327f;
  color: #fff;
  font-size: 0.72rem;
  font-style: normal;
  white-space: nowrap;
}

.detail-button {
  padding: 0;
  border: 0;
  background: transparent;
  color: #111;
  font-size: 1rem;
  white-space: nowrap;
  height: auto;
}

.submit-button {
  height: 3.5rem;
  border: 0;
  border-radius: 1.8rem;
  background: #6543a2;
  color: #fff;
  font-size: 1.15rem;
  font-weight: 500;
}

@media (max-width: 380px) {
  .content,
  .room-summary {
    padding-left: 0.85rem;
    padding-right: 0.85rem;
  }

  .date-row {
    flex-wrap: wrap;
  }

  .form-row {
    grid-template-columns: 5.4rem 1fr auto;
  }

  .submit-bar {
    grid-template-columns: minmax(0, 1fr) 7.4rem;
    gap: 0.5rem;
  }

  .amount strong {
    font-size: 1.65rem;
  }

  .submit-button {
    font-size: 1rem;
  }
}
</style>
