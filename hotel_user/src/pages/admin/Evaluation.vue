<template>
  <main class="evaluation-page">
    <section class="review-card" v-for="review in reviews" :key="review.id">
      <header class="order-head">
        <img class="cover" :src="review.cover" :alt="review.title" />
        <div class="order-info">
          <div class="title-row">
            <h2>{{ review.title }}</h2>
            <van-icon name="arrow" />
          </div>
          <div class="score-row">
            <span>订单号 {{ review.checkid }}</span>
            <span>{{ review.star }}星</span>
          </div>
        </div>
      </header>

      <p class="content">{{ review.paragraph }}</p>
    </section>
  </main>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import api from '../../api'

const standardCover = new URL('../../assets/images/biaozhun/1.png', import.meta.url).href
const deluxeCover = new URL('../../assets/images/haohua/1.png', import.meta.url).href
const suiteCover = new URL('../../assets/images/taofang/1.png', import.meta.url).href

const typeMap = {
  standard: '标准间',
  deluxe: '豪华间',
  suite: '套房'
}

const getRoomName = (roomtype = '') => {
  return typeMap[roomtype] || roomtype || '标准间'
}

const getCoverByRoomType = (roomtype = '') => {
  const name = getRoomName(roomtype)
  if (name.includes('标准间') || roomtype === 'standard') return standardCover
  if (name.includes('豪华间') || roomtype === 'deluxe') return deluxeCover
  if (name.includes('套房') || roomtype === 'suite') return suiteCover
  return standardCover
}

const normalizeEvaluation = (item) => {
  const roomName = getRoomName(item.roomtype)
  return {
    id: item.id,
    checkid: item.checkid,
    roomtype: item.roomtype,
    title: `广州xx酒店 · ${roomName}`,
    cover: getCoverByRoomType(item.roomtype),
    star: item.star,
    paragraph: item.paragraph
  }
}

const reviews = ref([])

const fetchEvaluation = async () => {
  try {
    const response = await api.get('/Evaluation/list')
    const data = Array.isArray(response.data) ? response.data : response.data?.data
    reviews.value = Array.isArray(data) ? data.map(normalizeEvaluation) : []
  } catch (error) {
    console.error('获取评价数据失败:', error)
    reviews.value = []
  }
}

onMounted(() => {
  fetchEvaluation()
})
</script>

<style scoped>
.evaluation-page {
  min-height: 100vh;
  padding: 1rem 0.85rem 6rem;
  background: #f4f4f5;
  color: #222;
  text-align: left;
}

.review-card {
  margin-bottom: 1rem;
  padding: 0.9rem;
  border-radius: 0.45rem;
  background: #fff;
  box-shadow: 0 0.5rem 1.2rem rgba(15, 23, 42, 0.06);
}

.order-head {
  display: grid;
  grid-template-columns: 3.75rem minmax(0, 1fr);
  gap: 0.65rem;
  align-items: start;
}

.cover {
  width: 3.75rem;
  height: 3.75rem;
  border-radius: 0.35rem;
  object-fit: cover;
}

.title-row {
  display: flex;
  align-items: center;
  min-width: 0;
  color: #333;
}

.title-row h2 {
  margin: 0;
  overflow: hidden;
  font-size: 1rem;
  font-weight: 600;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.title-row .van-icon {
  flex: none;
  margin-left: 0.25rem;
  color: #999;
}

.score-row {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-top: 0.15rem;
  color: #999;
  font-size: 0.8rem;
  line-height: 1.5;
}

.content {
  margin: 0.95rem 0 0;
  color: #333;
  font-size: 0.95rem;
  line-height: 1.6;
}

@media (max-width: 380px) {
  .order-head {
    grid-template-columns: 3.25rem minmax(0, 1fr);
  }

  .cover {
    width: 3.25rem;
    height: 3.25rem;
  }
}
</style>
