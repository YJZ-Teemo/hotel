<template>
  <div class="checkin-query-container">
    <el-card class="checkin-query-card">
      <template #header>
        <div class="card-header">
          <span>入住信息查询</span>
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="searchForm" label-width="120px" class="search-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="顾客姓名" prop="customerName">
              <el-input v-model="searchForm.customerName" placeholder="请输入顾客姓名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="入住日期范围" prop="dateRange">
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
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="入住状态" prop="status">
              <el-select v-model="searchForm.status" placeholder="选择入住状态" clearable>
                <el-option label="全部" value="" />
                <el-option label="已入住" value="checkedIn" />
                <el-option label="已退房" value="checkedOut" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <!-- <el-button type="primary" @click="searchCheckInRecords">查询</el-button> -->
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 入住记录列表 -->
      <el-table 
        :data="filteredRecords" 
        style="width: 100%"
        stripe
      >
        <el-table-column prop="checkinNum" label="记录ID" width="120" />
        <el-table-column prop="customerName" label="顾客姓名" width="150" />
        <el-table-column prop="roomNumber" label="房间号" width="120" />
        <el-table-column prop="roomType" label="房间类型" width="120">
          <template #default="scope">
            <el-tag :type="roomTypeMap[scope.row.roomType]?.color || 'info'" :style="{ borderRadius: '0px' }">
              {{ roomTypeMap[scope.row.roomType]?.label || scope.row.roomType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkinDate" label="入住日期" width="150" />
        <el-table-column prop="checkoutDate" label="退房日期" width="150" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :style="{ borderRadius: '0px' }" :type="getStatusTagType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额(元)" width="150" />
        <el-table-column prop="customerPhone" label="联系电话" width="150" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <!-- <el-button size="small" type="primary" @click="viewDetails(scope.row)">查看详情</el-button> -->
            <!-- <el-button size="small" type="success" v-if="scope.row.status === 'pending'" @click="confirmReservation(scope.row)">确认</el-button> -->
            <el-button size="small" type="danger" v-if="scope.row.status == 'checkedIn' && scope.row.status !== 'checkedOut'" @click="checkout(scope.row)">退房</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import api from '../../api';

// 取消预约
const checkout = (row) => {
  // console.log('退房', row.checkinNum);
  api.post('/check/out', {
        rnumber: row.roomNumber,
        checkinNum: row.checkinNum
      })
      .then(response => {
        const data = response.data;
        if (data.status === '200') {
          ElMessage.success('退房操作成功');
          // 刷新入住记录列表
          fetchCheckInRecords();
        } else {
          ElMessage.error('退房操作失败: ' + data.message);
        }
      })
      .catch(error => {
        console.error('网络错误:', error);
        ElMessage.error('网络错误，请稍后重试');
      });
};

// 查询表单
const searchForm = reactive({
  customerName: '',
  roomNumber: '',
  dateRange: [],
  status: ''
});

// 模拟入住记录数据
const checkInRecords = ref([]);

// 获取入住记录的函数
const fetchCheckInRecords = async () => {
  try {
    const response = await api.get('/check/list');
    const data = response.data;
    // console.log('获取入住记录数据:', data.cname);
    checkInRecords.value = data.map(checkin => ({
      checkinNum: checkin.checkinNum,
      customerName: checkin.cname,
      roomNumber: checkin.rnumber,
      roomType: checkin.rtype,
      checkinDate: checkin.checkindate,
      checkoutDate: checkin.checkoutdate,
      status: checkin.status,
      totalAmount: checkin.totalamount,
      customerPhone: checkin.cphone
    }));
  } catch (error) {
    console.error('获取入住记录失败:', error);
    checkInRecords.value = []; 
    ElMessage.error('获取入住记录失败');
  }
};

onMounted(() => {
  fetchCheckInRecords();
});



// 根据搜索条件过滤入住记录
const filteredRecords = computed(() => {
  return checkInRecords.value.filter(record => {
    // 顾客姓名过滤
    if (searchForm.customerName && !record.customerName.includes(searchForm.customerName)) {
      return false;
    }
    // 房间号过滤
    if (searchForm.roomNumber && !record.roomNumber.includes(searchForm.roomNumber)) {
      return false;
    }
    // 入住日期范围过滤
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      const [startDate, endDate] = searchForm.dateRange;
      const checkinDate = new Date(record.checkinDate);
      if (checkinDate < startDate || checkinDate > endDate) {
        return false;
      }
    }
    // 状态过滤
    if (searchForm.status && record.status !== searchForm.status) {
      return false;
    }
    return true;
  });
});

const roomTypeMap = {
  'standard': { label: '标准间', color: 'primary' },
  'deluxe': { label: '豪华间', color: 'success' },
  'suite': { label: '套房', color: 'warning' }
};


// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    checkedIn: '已入住',
    checkedOut: '已退房'
  };
  return statusMap[status] || status;
};

// 获取状态标签类型
const getStatusTagType = (status) => {
  const statusMap = {
    checkedIn: 'success',
    checkedOut: 'info'
  };
  return statusMap[status] || 'info';
};


// 重置查询条件
const resetSearch = () => {
  Object.assign(searchForm, {
    customerName: '',
    roomNumber: '',
    dateRange: [],
    status: ''
  });
  ElMessage.info('查询条件已重置');
};
</script>

<style scoped>
.checkin-query-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

.checkin-query-card {
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
.checkin-query-container .el-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.checkin-query-container .el-table__header-wrapper {
  background-color: #f7f9fc;
}

/* 确保所有表头单元格居中对齐 */
.checkin-query-container .el-table__header-wrapper th {
  text-align: center !important;
  font-weight: bold;
  color: #333;
  padding: 16px 0 !important;
  background-color: #f7f9fc;
  border-bottom: 2px solid #e6e8eb;
}

/* 确保所有表格单元格（包括body）居中对齐 */
.checkin-query-container .el-table__body-wrapper td {
  text-align: center !important;
  padding: 14px 0 !important;
}

.checkin-query-container .el-table__body-wrapper tr {
  transition: all 0.3s ease;
}

.checkin-query-container .el-table__body-wrapper tr:hover {
  background-color: #f5f7fa;
}

/* 状态标签样式 */
.checkin-query-container .el-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 0px;
}

/* 操作按钮样式 */
.checkin-query-container .el-button {
  margin: 0 4px;
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 4px;
  transition: all 0.3s ease;
}
</style>