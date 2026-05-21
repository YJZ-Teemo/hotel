<template>
  <main class="review-page">
    <header class="review-header">
      <button type="button" class="back-btn" @click="router.back()">
        <van-icon name="arrow-left" />
      </button>
      <h1>{{ title }}</h1>
    </header>

    <section class="score-panel">
      <div class="main-score">
        <button
          v-for="(item, index) in scoreLabels"
          :key="item"
          type="button"
          :class="{ active: overallScore === index + 1 }"
          @click="overallScore = index + 1"
        >
          <Rate
            :model-value="overallScore >= index + 1 ? 1 : 0"
            :count="1"
            color="#ff6b00"
            void-color="#eee"
            size="2.6rem"
            readonly
          />
          <span>
            {{ item }}
          </span>
        </button>
      </div>

      <div v-for="item in detailScores" :key="item.name" class="score-row">
        <span class="score-name">{{ item.name }}</span>
        <Rate v-model="item.value" color="#ff6b00" void-color="#eee" size="2.25rem" />
        <strong>{{ getScoreText(item.value) }}</strong>
      </div>
    </section>

    <textarea
      v-model="content"
      class="review-input"
      rows="5"
      placeholder="说说房间、服务、环境怎么样，给大家参考～"
    ></textarea>

    <button type="button" class="photo-box">
      <van-icon name="photograph" />
      <span>添加图片</span>
    </button>

    <footer class="publish-bar">
      <button type="button" @click="publishReview">发布</button>
    </footer>
  </main>
</template>

<script setup>
import { Rate, showNotify } from 'vant'
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../../api'

const route = useRoute()
const router = useRouter()
const content = ref('')
const overallScore = ref(5)
const scoreLabels = ['很差', '较差', '一般', '满意', '超赞']
const detailScores = ref([
  { name: '房间', value: 5 },
  { name: '服务', value: 5 },
  { name: '环境', value: 5 }
])

const title = computed(() => route.query.hotel || '酒店评价')
const checkinNum = computed(() => route.query.checkinNum || '')
const roomType = computed(() => route.query.roomType || '')

const getScoreText = (value) => {
  return scoreLabels[value - 1] || '超赞'
}

// const publishReview2 = () => {
//   console.log('checkinNum:', checkinNum.value)
//   console.log('roomType:', roomType.value)
//   const detailText = detailScores.value.map((item) => `${item.name}${item.value}星`).join('  ')
//   console.log(`总体评分${overallScore.value}星  ${detailText}`)
//   console.log('评价内容:', content.value)
// }
const detailText = detailScores.value.map((item) => `${item.name}${item.value}星`).join('  ')
const publishReview = async () => {
  try {
    const response = await api.post('/Evaluation/add', {
      checkid: checkinNum.value,
      roomtype: roomType.value,
      star: detailText,
      paragraph: content.value
    })

    const data = response.data

    if (data.status === '200') {
      showNotify({ type: 'success', message: '评价发布成功' })
      router.back() 
    } else {
      showNotify({ type: 'danger', message: data.message || '查询失败' })
    }
  } catch (error) {
    showNotify({ type: 'danger', message: '网络错误，请稍后重试' })
  }
}
</script>

<style scoped>
.review-page {
  min-height: 100vh;
  padding: 0 1.4rem 6.5rem;
  background: #fff;
  color: #222;
  text-align: left;
}

.review-header {
  display: grid;
  grid-template-columns: 2.6rem minmax(0, 1fr);
  align-items: center;
  height: 4.4rem;
}

.back-btn {
  display: grid;
  place-items: center;
  width: 2.4rem;
  height: 2.4rem;
  padding: 0;
  border: 0;
  background: transparent;
  color: inherit;
  font-size: 1.8rem;
}

.review-header h1 {
  margin: 0;
  overflow: hidden;
  font-size: 1.35rem;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.score-panel {
  margin-top: 1rem;
}

.main-score {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 0.35rem;
  text-align: center;
}

.main-score button {
  display: grid;
  justify-items: center;
  gap: 0.55rem;
  padding: 0;
  border: 0;
  background: transparent;
  color: #999;
  font-size: 0.92rem;
}

.main-score button.active span {
  color: #222;
  font-weight: 700;
}

.score-row {
  display: grid;
  grid-template-columns: 4.2rem minmax(0, 1fr) 3.2rem;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1.25rem;
}

.score-name {
  color: #999;
  font-size: 1.05rem;
}

.score-row strong {
  justify-self: end;
  font-size: 1rem;
}

.review-input {
  box-sizing: border-box;
  width: 100%;
  margin-top: 2rem;
  border: 0;
  outline: 0;
  resize: none;
  color: #222;
  font-size: 1.05rem;
  line-height: 1.6;
}

.review-input::placeholder {
  color: #bbb;
}

.photo-box {
  display: grid;
  place-items: center;
  gap: 0.6rem;
  width: 100%;
  min-height: 9rem;
  margin-top: 6rem;
  border: 0;
  border-radius: 0.7rem;
  background: #f7f7f7;
  color: #222;
  font-size: 1.05rem;
}

.photo-box .van-icon {
  font-size: 2.4rem;
}

.publish-bar {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  padding: 0.9rem 1.4rem 1.6rem;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.publish-bar button {
  width: 100%;
  height: 3.3rem;
  border: 0;
  border-radius: 999px;
  background: linear-gradient(90deg, #ff7800, #ff3b12);
  color: #fff;
  font-size: 1.15rem;
  font-weight: 600;
}

@media (max-width: 380px) {
  .review-page {
    padding-right: 1rem;
    padding-left: 1rem;
  }

  .score-row {
    grid-template-columns: 3.7rem minmax(0, 1fr) 2.8rem;
  }

  .photo-box {
    margin-top: 4rem;
  }
}
</style>
