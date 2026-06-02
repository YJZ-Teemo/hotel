<template>
  <div class="top">
    <van-swipe :autoplay="3000" lazy-render>
      <van-swipe-item v-for="image in images" :key="image">
        <img :src="image" class="swipe-image" />
      </van-swipe-item>
    </van-swipe>
  </div>
  <div class="main">
    <div class="hotel-name">
      <p>广州xx酒店</p>
      <span>舒适型 | 2024年装修</span>
    </div>
    <div class="card">
        <span class="badge">自助洗衣</span>
        <span class="badge">行李寄存</span>
        <span class="badge">免费停车</span>
        <span class="badge">会议室</span>
    </div>
    <div class="card indate-select">
      <DatePickerRange v-model="dateRange" />
      <span class="badge2">全日房</span>
    </div>
  </div>
  <div class="card" v-for="room in rooms" :key="room.type">
    <div>
      <RoomCard
        :name="getTypeConfig(room.type).name"
        :price="room.price"
        :image="imageSrc"
        :bed="getTypeConfig(room.type).bed"
        :area="room.area"
        :cardLevel="cardLevel"
        hobby=''
        :cancelText="cancelText"
        @Room="handleRoom($event, room)"
      />
    </div>
  </div>
  <div class="footer">

  </div>


</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import DatePickerRange from '../../components/DatePickerRange.vue';
import RoomCard from '../../components/RoomType.vue'
import api from '../../api';
import { showNotify } from 'vant';

const router = useRouter();
const route = useRoute();
const roomList = ref([])
const rooms = ref([]);
const countList = ref([])
const cardLevel = localStorage.getItem('cardlevel') || '待登录';
const reserve = ref({
  start: '',
  end: ''
})
const typeMap = {
  '套房': { name: '套房', bed: '1张2*2.2米床' },
  '标准间': { name: '标准间', bed: '1张2*1.8米床' },
  '豪华间': { name: '豪华间', bed: '1张2*1.8米床' }
};

const getTypeConfig = (type) => {
  return typeMap[type] || { name: type, bed: '' };
};
const handleRoom = (data, room) => {
  const roomConfig = getTypeConfig(room.type)
  console.log('预订房型:', data)
   router.push({
    path: '/reserve',
    query: {
      roomType: room.type,
      roomName: roomConfig.name,
      area: room.area,
      bed: roomConfig.bed,
      price: data.price,
      newPrice: data.newprice,
      points: data.points,
      cardLevel: cardLevel.value,
      startDate: dateRange.value.start,
      endDate: dateRange.value.end
    }
  })
}

const imageSrc = new URL('../../assets/images/biaozhun/1.png', import.meta.url).href

const formatDateValue = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const today = new Date()
const tomorrow = new Date()
tomorrow.setDate(today.getDate() + 1)

const dateRange = ref({
  start: formatDateValue(today),
  end: formatDateValue(tomorrow)
})

const cancelText = computed(() => {
  const dateStr = dateRange.value.start
  const month = parseInt(dateStr.slice(5, 7), 10)
  const day = parseInt(dateStr.slice(8), 10)
  return `${month}月${day}日 18:00前可免费取消`
})

// 获取房间数据（最多重试3次）
const fetchRoomsType = async (retries = 3) => {
  try {
    const response = await api.get('/Room/gettypelist');
    rooms.value = response.data;
  } catch (error) {
    // console.error('获取房间数据失败:', error);
    if (retries > 1) {
      setTimeout(() => fetchRoomsType(retries - 1), 2000);
    } else {
      rooms.value = [];
      showNotify({ type: 'danger', message: '获取房间数据失败' });
    }
  }
};

onMounted(() => {
  fetchRoomsType();
  // fetchRoomsCount();
});

const images = [
  new URL('../../assets/images/img_public/1.png', import.meta.url).href,
  new URL('../../assets/images/img_public/2.png', import.meta.url).href,
  new URL('../../assets/images/img_public/3.png', import.meta.url).href,
  new URL('../../assets/images/img_public/4.png', import.meta.url).href
]

</script>

<style scoped>
.footer{
  height: 5rem;
}
.indate-select {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
  margin-top: 1rem;
}
.hotel-name {
  text-align: left;
  padding: 0 1rem;
}
.hotel-name p {
  margin: 0;
  text-align: left;
  line-height: 1.4;
}
.hotel-name p + p {
  margin-top: 0.25rem;
}
.hotel-name span {
  display: inline-block;
  margin-top: 0.25rem;
  color: #6b7280;
  font-size: 0.92rem;
}
.main {
  margin-top: 0.75rem;
}
.top{
  position: relative;
  width: 100%;
  height: 15rem;
  overflow: hidden;
}
.swipe-image {
  display: block;
  width: 100%;
  height: 15rem;
  object-fit: cover;
}

.my-swipe .van-swipe-item {
  color: #fff;
  font-size: 1.25rem;
  line-height: 9.375rem;
  text-align: center;
  background-color: #39a9ed;
}

.hotel-page {
  min-height: 100vh;
  background: #f5f5f7;
  color: #1a1a1a;
  padding: 0 0.75rem 1.5rem;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Helvetica Neue", Arial, sans-serif;
}

.hero {
  position: relative;
  border-radius: 1.5rem;
  overflow: hidden;
  min-height: 13.75rem;
  margin: 1rem 0 0.625rem;
  background: linear-gradient(160deg, #2a5df5, #7f8aff);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 1.25rem;
  color: white;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at top left, rgba(255, 255, 255, 0.18), transparent 45%);
}

.hero-text,
.hero-action {
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 1.3rem;
  font-weight: 700;
  line-height: 1.4;
}

.hero-subtitle {
  margin-top: 0.5rem;
  font-size: 0.95rem;
  opacity: 0.92;
}

.hero-action {
  align-self: flex-end;
  background: rgba(255, 255, 255, 0.18);
  padding: 0.5rem 0.75rem;
  border-radius: 62.4375rem;
  font-size: 0.92rem;
}

.card {
  background: white;
  border-radius: 1.25rem;
  box-shadow: 0 0.625rem 1.5625rem rgba(15, 23, 42, 0.08);
  padding: 1rem;
  margin-bottom: 0.875rem;
}

.badge-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.badge {
  background: #eef2ff;
  color: #3b4de0;
  padding: 0.375rem 0.625rem;
  margin-right: 0.5rem;
  border-radius: 62.4375rem;
  font-size: 0.8rem;
}
.badge2 {
  background: #fff;
  color: #000;
  padding: 0.375rem 0.625rem;
  flex: none;
  border-radius: 62.4375rem;
  border: 0.0625rem solid #cacaca;
  font-size: 0.8rem;
}
.score-row {
  display: flex;
  align-items: center;
  gap: 0.875rem;
  margin-bottom: 0.875rem;
}

.score-box {
  display: flex;
  align-items: baseline;
  gap: 0.625rem;
}

.score-value {
  font-size: 1.6rem;
  font-weight: 700;
}

.score-label {
  font-size: 0.95rem;
  color: #666;
}

.score-detail {
  color: #4f4f4f;
  font-size: 0.95rem;
  flex: 1;
}

.location-row {
  font-size: 0.92rem;
  color: #57606a;
  line-height: 1.6;
}

.location-city {
  margin-top: 0.375rem;
  color: #8a94a6;
}

.booking-form {
  margin-bottom: 0.875rem;
}

.booking-header {
  display: flex;
  justify-content: space-between;
  gap: 0.75rem;
  margin-bottom: 0.875rem;
}

.booking-date {
  flex: 1;
  min-width: 0;
}

.booking-label {
  display: block;
  margin-bottom: 0.375rem;
  color: #6b7280;
  font-size: 0.88rem;
}

.date-input {
  box-sizing: border-box;
  width: 100%;
  border: 0.0625rem solid #e5e7eb;
  border-radius: 0.875rem;
  background: #f8fafc;
  color: #111827;
  font-size: 1rem;
  font-weight: 700;
  padding: 0.625rem 0.75rem;
}

.search-button {
  width: 100%;
  border: 0;
  border-radius: 0.875rem;
  background: #2563eb;
  color: #fff;
  font-size: 0.98rem;
  font-weight: 700;
  padding: 0.6875rem 0;
}

.tab-row {
  display: flex;
  gap: 0.5rem;
}

.tab {
  flex: 1;
  border: 0.0625rem solid #e5e7eb;
  background: #f8fafc;
  color: #374151;
  border-radius: 0.875rem;
  padding: 0.625rem 0;
  font-size: 0.95rem;
}

.tab.active {
  background: #ffffff;
  border-color: #3b82f6;
  color: #0f172a;
  font-weight: 600;
}

.room-card {
  display: flex;
}

.room-info {
  flex: 1;
  min-width: 0;
}

.room-title {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 0.375rem;
}

.room-meta {
  color: #6b7280;
  font-size: 0.92rem;
}

.room-price {
  color: #ef4444;
  font-size: 1.2rem;
  font-weight: 700;
}

.offer-title {
  font-size: 1rem;
  font-weight: 700;
}

.offer-note {
  margin-top: 0.5rem;
  color: #6b7280;
  font-size: 0.93rem;
}

@media (max-width: 23.75rem) {
  .hero {
    padding: 1.125rem 1rem;
    min-height: 12.5rem;
  }

  .booking-header {
    flex-direction: column;
  }

  .tab-row {
    flex-wrap: wrap;
  }
}
</style>
