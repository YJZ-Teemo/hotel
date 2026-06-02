<template>
  <div class="clean-page">
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-num orange">{{ pendingCount }}</div>
        <div class="stat-label">待打扫</div>
      </div>
      <div class="stat-item">
        <div class="stat-num blue">{{ cleaningCount }}</div>
        <div class="stat-label">打扫中</div>
      </div>
      <div class="stat-item">
        <div class="stat-num green">{{ doneCount }}</div>
        <div class="stat-label">已完成</div>
      </div>
    </div>

    <div class="task-list">
      <TaskCard
        v-for="task in filteredTasks"
        :key="task.id"
        :task="task"
        :current-user="currentUser"
        :has-cleaning="hasCleaning"
        @finish="finishClean"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { showNotify } from 'vant'
import TaskCard from '../../components/TaskCard.vue'
import api from '../../api'

const tasks = ref([])

const pendingCount = computed(() => tasks.value.filter(t => t.status === '待打扫').length)
const cleaningCount = computed(() => tasks.value.filter(t => t.status === '打扫中' && t.assignee === currentUser).length)
const doneCount = computed(() => tasks.value.filter(t => t.status === '已完成' && t.assignee === currentUser).length)

const filteredTasks = computed(() => tasks.value)

const currentUser = localStorage.getItem('username') || ''
const hasCleaning = computed(() => tasks.value.filter(t => t.status === '打扫中').length >= 2)

const getcleanlist = async () => {
  try {
    const response = await api.get('/cleaning/list')
    const list = response.data
    tasks.value = (Array.isArray(list) ? list : []).map(t => ({
      id: t.id,
      roomNumber: t.rnumber,
      floor: t.rnumber ? t.rnumber.slice(0, -2) : '',
      roomType: t.roomType,
      cleanType: t.cleanType,
      urgent: t.urgent === '1',
      estimateMin: t.estimateMin,
      note: t.remark,
      assignee: t.assignee || '',
      time: t.assignTime || '',
      status: t.status,
      createTime: t.createTime,
      completionTime: t.completionTime
    }))
  } catch (error) {
    console.error('获取打扫任务失败:', error)
    showNotify({ type: 'danger', message: '获取打扫任务失败，请稍后重试' })
  }
}

onMounted(() => {
  getcleanlist()
})

// function finishClean(task) {
//   api.post('/cleaning/completed', { id: task.id }).then(() => {
//     task.status = '已完成'
//     showNotify({ type: 'success', message: `${task.roomNumber} 打扫完成` })
//   }).catch(() => {
//     showNotify({ type: 'danger', message: '操作失败，请重试' })
//   })
// }
</script>

<style scoped>
.clean-page {
  min-height: 100vh;
  background: #f6f6f6;
  padding: 0.8rem 1rem 6rem;
}

.stats-bar {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.6rem;
  margin-bottom: 1rem;
}

.stat-item {
  background: #fff;
  border-radius: 0.65rem;
  padding: 0.9rem 0.5rem;
  text-align: center;
  box-shadow: 0 0.4rem 1rem rgba(15, 23, 42, 0.05);
}

.stat-num {
  font-size: 1.6rem;
  font-weight: 700;
}

.stat-num.orange { color: #ff7800; }
.stat-num.blue { color: #1989fa; }
.stat-num.green { color: #07c160; }

.stat-label {
  font-size: 0.75rem;
  color: #999;
  margin-top: 0.2rem;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}
</style>
