<template>
  <div class="schedule-query-container">
    <el-card class="schedule-query-card">
      <template #header>
        <div class="card-header">
          <span>日程信息查询</span>
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="searchForm" label-width="120px" class="search-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="日程标题" prop="title">
              <el-input v-model="searchForm.title" placeholder="请输入日程标题" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="日程类型" prop="type">
              <el-select v-model="searchForm.type" placeholder="选择日程类型" clearable>
                <el-option label="全部" value="" />
                <el-option v-for="type in scheduleTypes" :key="type.value" :label="type.label" :value="type.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="日期范围" prop="dateRange">
              <el-date-picker
                v-model="searchForm.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                clearable
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <!-- <el-button type="primary" @click="searchSchedules">查询</el-button> -->
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 日程列表 -->
      <el-table 
        :data="filteredSchedules" 
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="日程编号" width="150" />
        <el-table-column prop="title" label="日程标题" width="200" />
        <el-table-column prop="type" label="日程类型" width="120">
          <template #default="scope">
            <el-tag :type="getTypeTagType(scope.row.type)" :style="{ borderRadius: '0px' }">
              {{ getTypeLabel(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="150" />
        <el-table-column prop="time" label="时间" width="120" />
        <el-table-column prop="location" label="地点" width="150" />
        <el-table-column prop="participants" label="参与者" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { ElMessage } from 'element-plus';

// 日程类型列表
const scheduleTypes = [
  { label: '会议', value: 'meeting' },
  { label: '活动', value: 'activity' },
  { label: '维护', value: 'maintenance' },
  { label: '其他', value: 'other' }
];

// 查询表单
const searchForm = reactive({
  title: '',
  type: '',
  dateRange: []
});

// 模拟日程数据
const schedules = ref([
  {
    id: 'SCH001',
    title: '周例会',
    type: 'meeting',
    date: '2024-01-22',
    time: '10:00-11:00',
    location: '会议室A',
    participants: '全体员工',
    description: '每周例行工作会议，讨论本周工作进展和下周计划'
  },
  {
    id: 'SCH002',
    title: '房间维护',
    type: 'maintenance',
    date: '2024-01-23',
    time: '09:00-12:00',
    location: '101-105房间',
    participants: '维修人员',
    description: '对1楼部分房间进行设施维护和清洁'
  },
  {
    id: 'SCH003',
    title: '客户活动',
    type: 'activity',
    date: '2024-01-24',
    time: '14:00-16:00',
    location: '宴会厅',
    participants: 'VIP客户',
    description: '举办客户答谢活动'
  },
  {
    id: 'SCH004',
    title: '月度总结会',
    type: 'meeting',
    date: '2024-01-25',
    time: '14:00-17:00',
    location: '会议室B',
    participants: '部门经理',
    description: '月度工作总结和绩效评估'
  },
  {
    id: 'SCH005',
    title: '网络升级',
    type: 'maintenance',
    date: '2024-01-26',
    time: '22:00-02:00',
    location: '机房',
    participants: 'IT人员',
    description: '对酒店网络系统进行升级和维护'
  }
]);

// 根据搜索条件过滤日程数据
const filteredSchedules = computed(() => {
  return schedules.value.filter(schedule => {
    // 日程标题过滤
    if (searchForm.title && !schedule.title.includes(searchForm.title)) {
      return false;
    }
    // 日程类型过滤
    if (searchForm.type && schedule.type !== searchForm.type) {
      return false;
    }
    // 日期范围过滤
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      const [startDate, endDate] = searchForm.dateRange;
      const scheduleDate = new Date(schedule.date);
      if (scheduleDate < startDate || scheduleDate > endDate) {
        return false;
      }
    }
    return true;
  });
});

// 获取日程类型标签
const getTypeLabel = (type) => {
  const typeMap = {
    meeting: '会议',
    activity: '活动',
    maintenance: '维护',
    other: '其他'
  };
  return typeMap[type] || type;
};

// 获取日程类型标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    meeting: 'primary',
    activity: 'success',
    maintenance: 'warning',
    other: 'info'
  };
  return typeMap[type] || 'info';
};

// 查询日程
// const searchSchedules = () => {
//   console.log('查询条件:', searchForm);
//   ElMessage.info('查询完成');
// };

// 重置查询条件
const resetSearch = () => {
  Object.assign(searchForm, {
    title: '',
    type: '',
    dateRange: []
  });
  ElMessage.info('查询条件已重置');
};
</script>

<style scoped>
.schedule-query-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #2c3e50;
  padding-bottom: 12px;
  border-bottom: 2px solid #409eff;
}

.schedule-query-card {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.search-form {
  margin-bottom: 20px;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 表格样式优化 */
.schedule-query-container .el-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.schedule-query-container .el-table__header-wrapper {
  background-color: #f7f9fc;
}

/* 确保所有表头单元格居中对齐 */
.schedule-query-container .el-table__header-wrapper th {
  text-align: center !important;
  font-weight: bold;
  color: #333;
  padding: 16px 0 !important;
  background-color: #f7f9fc;
  border-bottom: 2px solid #e6e8eb;
}

/* 确保所有表格单元格（包括body）居中对齐 */
.schedule-query-container .el-table__body-wrapper td {
  text-align: center !important;
  padding: 14px 0 !important;
}

.schedule-query-container .el-table .el-table__cell {
  text-align: center !important;
  padding: 14px 0 !important;
  box-sizing: border-box;
}

/* 移除默认的单元格内边距，使用我们自定义的内边距 */
.schedule-query-container .el-table__cell > .cell {
  padding: 0 !important;
  margin: 0 !important;
  text-align: center !important;
}

.schedule-query-container .el-table__body-wrapper tr {
  transition: all 0.3s ease;
}

.schedule-query-container .el-table__body-wrapper tr:hover {
  background-color: #f5f7fa;
}

/* 状态标签样式 */
.schedule-query-container .el-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 0px;
}

/* 操作按钮样式 */
.schedule-query-container .el-button {
  margin: 0 4px;
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 4px;
  transition: all 0.3s ease;
}
</style>