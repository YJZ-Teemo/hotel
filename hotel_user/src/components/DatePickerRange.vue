<template>
  <div class="date-picker-range">
    <van-cell is-link class="date-cell" @click="show = true">
      <template #title>
        <div v-if="startDate && endDate" class="date-display">
          <div class="date-row">
            <span>{{ startText }}</span>
            <span>-</span>
            <span>{{ endText }}</span>
          </div>
          <div class="week-row">
            <span>{{ startWeek }}入住</span>
            <span>{{ endWeek }}离店</span>
          </div>
        </div>
        <div v-else class="date-placeholder">请选择入住和离店日期</div>
      </template>
    </van-cell>

    <van-calendar
      v-model:show="show"
      type="range"
      :show-confirm="false"
      @confirm="onConfirm"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  label: { type: String, default: '入离日期' },
  modelValue: { type: Object, default: () => ({ start: '', end: '' }) }
})

const emit = defineEmits(['update:modelValue'])

const show = ref(false)
const startDate = ref(null)
const endDate = ref(null)

const weekMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

const parseDateValue = (value) => {
  if (!value) return null
  const [year, month, day] = value.split('-').map(Number)
  return new Date(year, month - 1, day)
}

const syncDateRange = (value) => {
  startDate.value = parseDateValue(value?.start)
  endDate.value = parseDateValue(value?.end)
}

const formatDateText = (date) => {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}月${day}日`
}

const formatDateValue = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const startText = computed(() => startDate.value ? formatDateText(startDate.value) : '')
const endText = computed(() => endDate.value ? formatDateText(endDate.value) : '')
const startWeek = computed(() => startDate.value ? weekMap[startDate.value.getDay()] : '')
const endWeek = computed(() => endDate.value ? weekMap[endDate.value.getDay()] : '')

watch(() => props.modelValue, syncDateRange, { immediate: true, deep: true })

const onConfirm = ([start, end]) => {
  show.value = false
  startDate.value = start
  endDate.value = end
  emit('update:modelValue', {
    start: formatDateValue(start),
    end: formatDateValue(end)
  })
}
</script>

<style scoped>
.date-picker-range {
  width: fit-content;
  max-width: 100%;
}

.date-cell {
  padding: 0;
  background: transparent;
}

.date-cell :deep(.van-cell__title) {
  flex: none;
}

.date-cell :deep(.van-cell__right-icon) {
  margin-left: 0.25rem;
}

.date-display {
  display: inline-flex;
  flex-direction: column;
  align-items: flex-start;
}

.date-row {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: #1f2937;
  font-size: 0.92rem;
  font-weight: 600;
  line-height: 1.35;
}

.week-row {
  display: flex;
  gap: 1rem;
  margin-top: 0.25rem;
  color: #6b7280;
  font-size: 0.92rem;
  line-height: 1.35;
}

.date-placeholder {
  color: #6b7280;
  font-size: 0.92rem;
}
</style>
