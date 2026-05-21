<template>
  <article class="room-type-card">
    <div class="room-header">
      <div class="room-image-wrap">
        <img class="room-image" :src="image" :alt="name" />
        <span v-if="hobby!=null || hobby!=''" class="image-tag">{{ hobby }}</span>
      </div>

      <div class="room-summary">
        <div class="room-title-row">
          <h3 class="room-name">{{ name }}</h3>
          <span class="arrow">›</span>
        </div>
        <div class="room-meta">
          {{ area }}m²&nbsp;&nbsp;|&nbsp;&nbsp;{{ bed }}&nbsp;&nbsp;|&nbsp;&nbsp;{{ info.windowText }}
        </div>
      </div>

    </div>

    <div class="room-plan">
      <div class="plan-info">
        <div class="plan-title" v-if="cardLevel!=''">{{ cardLevel }}价 <span class="arrow">›</span></div>
        <div class="cancel-line">
          不含早餐 <span>| {{ cancelText }}</span>
        </div>
        <div class="point-line">
          <span>{{ info.points }}</span>
        </div>
      </div>

      <div class="price-panel">
        <div class="limit-tag">贵即降</div>
        <div class="price-line">
          <span class="old-price">¥{{ price }}</span>
          <span class="currency">¥</span>
          <span class="price">{{ info.newprice }}</span>
        </div>
        <div class="discount-line">{{ cardLevel }} {{ info.discount }}折</div>
      </div>

      <button class="book-button" @click="handleRoom" type="button">订</button>
    </div>
  </article>
</template>

<script setup>
import { onMounted, ref } from 'vue';


const props = defineProps({
  name: { type: String, default: '' },
  price: { type: [String, Number], default: 0 },
  image: { type: String, required: true },
  area: { type: [String, Number], default: 0 },
  bed: { type: String, default: '1张2*1.8米床' },
  hobby: { type: String, default: '' },
  cardLevel: { type: String, default: '' },
  imageTag: { type: String, default: '' },
  cancelText: { type: String, default: '18:00前可免费取消' }
})

const emit = defineEmits(['Room'])

const info = ref({
  newprice: '',
  discount: '',
  points:'',
  windowText: '外景窗',
  pointsadd:''
})

const Discountprice = () => {
  const map = {
    '星会员':   { discount: '9.5', rate: 0.95 ,points:'1倍积分',pointstext:1},
    '二星会员': { discount: '9.0', rate: 0.90 ,points:'1.2倍积分',pointstext:1.2},
    '三星会员': { discount: '8.5', rate: 0.85 ,points:'1.5倍积分',pointstext:1.5},
    '四星会员': { discount: '8.0', rate: 0.80 ,points:'2倍积分',pointstext:2},
    '五星会员': { discount: '7.5', rate: 0.75 ,points:'2.5倍积分',pointstext:2.5}
  }
  const matched = map[props.cardLevel]
  if (matched) {
    info.value.discount = matched.discount
    info.value.newprice = (props.price * matched.rate).toFixed(1)
    info.value.points = matched.points
    info.value.pointsadd = (props.price * matched.pointstext).toFixed(0)
  }
}

Discountprice()

const handleRoom = () => {
  // 处理订房逻辑
  emit('Room', {
    type: props.name,
    price: props.price,
    newprice: info.value.newprice,
    points: info.value.pointsadd
  })
}



</script>

<style scoped>
.room-type-card {
  width: 100%;
  box-sizing: border-box;
  background: #fff;
  border-radius: 0.5rem;
  overflow: hidden;
  color: #1f2937;
  text-align: left;
}

.room-header {
  position: relative;
  display: flex;
  gap: 0.5rem;
  padding: 0.75rem 0.25rem;
}

.room-image-wrap {
  position: relative;
  flex: none;
  width: 5rem;
  height: 5rem;
  border-radius: 0.25rem;
  overflow: hidden;
  background: #f3f4f6;
}

.room-image {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-tag {
  position: absolute;
  left: 0;
  top: 0;
  max-width: 100%;
  box-sizing: border-box;
  padding: 0.125rem 0.375rem;
  border-bottom-right-radius: 0.25rem;
  background: rgba(101, 67, 162, 0.92);
  color: #fff;
  font-size: 0.625rem;
  line-height: 1.3;
}

.play-dot {
  position: absolute;
  right: 0.375rem;
  bottom: 0.375rem;
  display: grid;
  place-items: center;
  width: 1.125rem;
  height: 1.125rem;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  color: #b78942;
  font-size: 0.625rem;
  line-height: 1;
}

.room-summary {
  flex: 1;
  min-width: 0;
  padding-right: 1.5rem;
}

.room-title-row {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  min-width: 0;
}

.room-name {
  margin: 0;
  overflow: hidden;
  color: #222;
  font-size: 1rem;
  font-weight: 500;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow {
  color: #222;
  font-size: 1.125rem;
  line-height: 1;
}

.room-meta {
  margin-top: 0.25rem;
  color: #666;
  font-size: 0.8125rem;
  line-height: 1.4;
}

.collapse-button {
  position: absolute;
  top: 0.875rem;
  right: 0.25rem;
  display: grid;
  place-items: center;
  width: 1rem;
  height: 1rem;
  padding: 0;
  border: 0.0625rem solid #111;
  border-radius: 50%;
  background: #fff;
  color: #111;
  font-size: 0.75rem;
  line-height: 1;
}

.room-plan {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto auto;
  align-items: end;
  column-gap: 0.375rem;
  padding: 0.875rem 0.25rem;
  border-top: 0.0625rem solid #f1f1f1;
}

.plan-title {
  color: #111;
  font-size: 0.9375rem;
  font-weight: 500;
  line-height: 1.3;
}

.cancel-line {
  margin-top: 0.25rem;
  color: #333;
  font-size: 0.8125rem;
  line-height: 1.35;
}

.cancel-line span {
  color: #594194;
}

.point-line {
  display: inline-flex;
  align-items: center;
  gap: 0.125rem;
  margin-top: 0.375rem;
  padding-right: 0.25rem;
  background: #f4ecff;
  color: #7a54a7;
  font-size: 0.6875rem;
  line-height: 1.2;
}

.point-badge {
  display: inline-grid;
  place-items: center;
  width: 0.875rem;
  height: 0.875rem;
  background: #8c61ba;
  color: #fff;
  font-size: 0.5rem;
}

.price-panel {
  min-width: 4.75rem;
  text-align: right;
}

.limit-tag {
  display: inline-block;
  margin-bottom: 0.125rem;
  padding: 0.0625rem 0.1875rem;
  border-radius: 0.1875rem;
  background: #6c48a8;
  color: #fff;
  font-size: 0.5625rem;
  line-height: 1.2;
}

.price-line {
  white-space: nowrap;
}

.old-price {
  margin-right: 0.125rem;
  color: #333;
  font-size: 0.75rem;
  text-decoration: line-through;
}

.currency {
  color: #f5222d;
  font-size: 0.75rem;
}

.price {
  color: #f5222d;
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1;
}

.discount-line {
  margin-top: 0.1875rem;
  color: #f5222d;
  font-size: 0.625rem;
  white-space: nowrap;
}

.book-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.875rem;
  height: 3rem;
  padding: 0;
  border: 0;
  border-radius: 0.5rem;
  background: #6543a2;
  color: #fff;
  font-size: 1.125rem;
  font-weight: 600;
  line-height: 1;
  text-align: center;
}
</style>
