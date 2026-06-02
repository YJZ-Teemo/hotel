<template>
  <div class="task-card">
    <div class="task-header">
      <div class="room-info">
        <div class="room-number">{{ task.roomNumber }}</div>
        <div class="room-meta">{{ task.floor }}F · {{ task.roomType }}</div>
      </div>
      <div class="status-tag" :class="statusClass">{{ statusLabel }}</div>
    </div>

    <div class="task-body">
      <div class="task-row">
        <span class="task-icon">🧹</span>
        <span class="task-type">{{ task.cleanType }}</span>
        <span v-if="task.urgent" class="urgent-tag">紧急</span>
      </div>
      <div class="task-row">
        <span class="task-icon">⏱</span>
        <span class="task-detail">预计 {{ task.estimateMin }}分钟</span>
      </div>
      <div v-if="task.note" class="task-row">
        <span class="task-icon">💬</span>
        <span class="task-note">{{ task.note }}</span>
      </div>
    </div>

    <div class="task-footer">
      <div v-if="task.status !== '待打扫'" class="assignee">
        <div class="avatar" style="background: #9a8cc6">
          {{ task.assignee.charAt(0) }}
        </div>
        <span class="assignee-name">{{ task.assignee }}</span>
        <span class="assignee-time">{{ task.time }}</span>
      </div>
      <button
        v-if="task.status === '待打扫'"
        class="start-btn"
        @click="StartClean"
      >
        开始打扫
      </button>
      <button
        v-else-if="task.status === '打扫中' && task.assignee === currentUser"
        class="finish-btn"
        @click="OverClean"
      >
        完成打扫
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { showConfirmDialog } from 'vant'
import api from '../api'
const props = defineProps({
  task: { type: Object, required: true },
  currentUser: { type: String, default: '' },
  hasCleaning: { type: Boolean, default: false }
})

const emit = defineEmits(['start', 'finish'])

const statusMap = {
  '待打扫': { cls: 'tag-orange', label: '待打扫' },
  '打扫中': { cls: 'tag-blue', label: '打扫中' },
  '已完成': { cls: 'tag-green', label: '已完成' }
}

const statusClass = computed(() => statusMap[props.task.status]?.cls || '')
const statusLabel = computed(() => statusMap[props.task.status]?.label || props.task.status || '')
const StartClean = async () => {
  if (props.hasCleaning) {
    alert('已有2个房间在打扫，请先完成后再接单')
    return
  }
  try {
    await showConfirmDialog({
      message: '确定要开始打扫吗？'
    })
    await api.post('/cleaning/start', {
      id: props.task.id,
      assignee: localStorage.getItem('username')
    })
    props.task.status = '打扫中'
    props.task.assignee = localStorage.getItem('username')
    props.task.time = new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  } catch {
    // 用户取消
  }
}
// over
const OverClean = async () => {
  try {
    await showConfirmDialog({
      message: '确定要完成打扫吗？'
    })
    await api.post('/cleaning/over', {
      id: props.task.id,
      rnumber: props.task.roomNumber
    })
    props.task.status = '已完成'
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.task-card {
  background: #fff;
  border-radius: 0.85rem;
  padding: 1rem 1.1rem;
  box-shadow: 0 0.3rem 0.9rem rgba(15, 23, 42, 0.05);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.room-number {
  font-size: 1.35rem;
  font-weight: 700;
  color: #222;
}

.room-meta {
  font-size: 0.75rem;
  color: #999;
  margin-top: 0.15rem;
}

.status-tag {
  font-size: 0.7rem;
  padding: 0.15rem 0.55rem;
  border-radius: 999px;
  font-weight: 500;
  white-space: nowrap;
}

.tag-orange {
  background: #fff7ed;
  color: #ff7800;
}

.tag-blue {
  background: #e8f4ff;
  color: #1989fa;
}

.tag-green {
  background: #e8f8ee;
  color: #07c160;
}

.task-body {
  margin-top: 0.7rem;
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}

.task-row {
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.task-icon {
  font-size: 0.85rem;
  flex-shrink: 0;
}

.task-type {
  font-size: 0.82rem;
  color: #333;
  font-weight: 500;
}

.urgent-tag {
  font-size: 0.65rem;
  background: #ff7800;
  color: #fff;
  padding: 0.1rem 0.4rem;
  border-radius: 999px;
  margin-left: 0.3rem;
}

.task-detail {
  font-size: 0.82rem;
  color: #555;
}

.task-note {
  font-size: 0.78rem;
  color: #ff7800;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.8rem;
  padding-top: 0.7rem;
  border-top: 1px solid #f5f5f5;
}

.assignee {
  display: flex;
  align-items: center;
  gap: 0.45rem;
}

.avatar {
  width: 1.6rem;
  height: 1.6rem;
  border-radius: 50%;
  color: #fff;
  font-size: 0.7rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}

.assignee-name {
  font-size: 0.82rem;
  color: #333;
}

.assignee-time {
  font-size: 0.72rem;
  color: #999;
}

.start-btn,
.finish-btn {
  border: 0;
  border-radius: 0.45rem;
  padding: 0.45rem 1.2rem;
  font-size: 0.82rem;
  font-weight: 600;
  color: #fff;
  cursor: pointer;
  margin-left: auto;
}

.start-btn {
  background: linear-gradient(135deg, #ff7800, #ff5c12);
}

.start-btn:active {
  opacity: 0.85;
}

.finish-btn {
  background: linear-gradient(135deg, #07c160, #06ad56);
}

.finish-btn:active {
  opacity: 0.85;
}

.start-btn.disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
