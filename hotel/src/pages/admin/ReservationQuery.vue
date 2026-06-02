<template>
  <div class="reservation-query-container">
    <el-card class="reservation-query-card">
      <template #header>
        <div class="card-header">
          <span>预约信息查询</span>
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
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="searchForm.roomType" placeholder="选择房间类型" clearable>
                <el-option label="全部" value="" />
                <el-option label="标准间" value="standard" />
                <el-option label="豪华间" value="deluxe" />
                <el-option label="套房" value="suite" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="预约状态" prop="status">
              <el-select v-model="searchForm.status" placeholder="选择预约状态" clearable>
                <el-option label="全部" value="" />
                <el-option label="已确认" value="已确认" />
                <el-option label="已取消" value="已取消" />
                <el-option label="已完成" value="已完成" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预约日期" prop="reservationDate">
              <el-date-picker
                v-model="searchForm.reservationDate"
                type="date"
                placeholder="选择预约日期"
                clearable
                style="width: 100%;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <!-- <el-button type="primary" @click="searchReservations">查询</el-button> -->
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 预约列表 -->
      <el-table 
        :data="filteredReservations" 
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="预约编号" width="150" />
        <el-table-column prop="customerName" label="顾客姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="roomType" label="房间类型" width="120">
         <template #default="scope">
            <el-tag :type="roomTypeMap[scope.row.roomType]?.color || 'info'" :style="{ borderRadius: '0px' }">
              {{ roomTypeMap[scope.row.roomType]?.label || scope.row.roomType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkinDate" label="入住日期" width="180" />
        <el-table-column prop="checkoutDate" label="退房日期" width="180" />
        <el-table-column prop="status" label="预约状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewDetails(scope.row)" style="margin-left: 10px;">查看详情</el-button>
            <!-- <el-button size="small" type="success" v-if="scope.row.status === '待确认'" @click="confirmReservation(scope.row)">确认</el-button> -->
            <!-- <el-button size="small" type="danger" v-if="scope.row.status !== '已取消' && scope.row.status !== '已完成'" @click="cancelReservation(scope.row)">取消</el-button> -->
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="180" />
      </el-table>
    </el-card>
    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约编号">
              <span>{{ form.id }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="顾客姓名">
              <span>{{ form.customerName }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间类型">
              <span>{{ roomTypeMap[form.roomType]?.label || form.roomType }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间状态">
              <span>{{ getStatusText(form.status) }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约日期">
              <span>{{ form.reservationDate }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <span style="width: 90%;">{{ form.phone }}</span>
            </el-form-item>
          </el-col>
          </el-row> 
          <el-form-item label="入住日期">
            <el-date-picker
              v-model="form.checkinDate"
              type="date"
              placeholder="选择预约日期"
              style="width: 90%;"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="退房日期">
            <el-date-picker
              v-model="form.checkoutDate"
              type="date"
              placeholder="选择退房日期"
              style="width: 90%;"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请输入备注信息" style="width: 90%;" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <!-- v-if="scope.row.status !== '已取消' && scope.row.status !== '已完成'" -->
          <!-- <el-button @click="dialogVisible = false">取消</el-button> -->
          <el-button v-if="form.status === '已确认'" type="danger" @click="cancelReservation">取消预约</el-button>
          <el-button v-if="form.status === '已确认'" type="primary" @click="saveForm">保存</el-button>
          <el-button v-if="form.status === '已确认'" type="success" @click="checkinnow">办理入住</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
import api from '../../api';

const router = useRouter();
const route = useRoute();

onMounted(() => {
  reserveinfo();
});

// 查询表单
const searchForm = reactive({
  customerName: '',
  roomType: '',
  phone: '',
  status: '',
  reservationDate: ''
});
const reservations = ref([]);
const reserveinfo = async () => {
  try {
    const response = await api.get('/Reserve/list');
    const data = response.data;
    reservations.value = data.map(reservation => ({
      id: reservation.reserveId,
      customerName: reservation.cname,
      phone: reservation.phone,
      roomType: reservation.roomType,
      reservationDate: reservation.reservationDate,
      checkinDate: reservation.checkIn,
      checkoutDate: reservation.checkOut,
      status: reservation.state,
      remark: reservation.remark,
      cardlevel: reservation.cardlevel,
      points: reservation.points,
      price: reservation.price
    }));
  } catch (error) {
    console.error('获取预约数据失败:', error);
    reservations.value = [];
    ElMessage.error('获取预约数据失败');
  }
};

// 根据搜索条件过滤预约数据
const filteredReservations = computed(() => {
  return reservations.value.filter(reservation => {
    // 顾客姓名过滤
    if (searchForm.customerName && !reservation.customerName.includes(searchForm.customerName)) {
      return false;
    }
    // 房间类型过滤
    if (searchForm.roomType && reservation.roomType !== searchForm.roomType) {
      return false;
    }
    // 手机号过滤
    if (searchForm.phone && !(reservation.phone || '').includes(searchForm.phone)) {
      return false;
    }
    // 状态过滤
    if (searchForm.status && reservation.status !== searchForm.status) {
      return false;
    }
    // 预约日期过滤
    if (searchForm.reservationDate) {
      const reservationDate = new Date(reservation.reservationDate);
      const searchDate = new Date(searchForm.reservationDate);
      if (reservationDate.toDateString() !== searchDate.toDateString()) {
        return false;
      }
    }
    return true;
  });
});

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '已确认': '已确认',
    '已完成': '已完成',
    '已取消': '已取消'
  };
  return statusMap[status] || status;
};

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    '已确认': 'success',
    '已完成': 'info',
    '已取消': 'danger'
  };
  return typeMap[status] || 'info';
};

// 获取房间类型文本

const roomTypeMap = {
 'standard': { label: '标准间', color: 'primary' },
  'deluxe': { label: '豪华间', color: 'success' },
  'suite': { label: '套房', color: 'warning' }
};

// 重置查询条件
const resetSearch = () => {
  Object.assign(searchForm, {
    customerName: '',
    roomType: '',
    phone: '',
    status: '',
    reservationDate: ''
  });
  ElMessage.info('查询条件已重置');
};

// 查看详情
const dialogVisible = ref(false);
const dialogTitle = ref('预约详情');
const form = reactive({
  id: '',
  customerName: '',
  phone: '',
  roomType: '',
  reservationDate: '',
  checkinDate: '',
  checkoutDate: '',
  status: '',
  remark: '',
  cardlevel:'',
  points:'',
  price: ''
});
const saveForm = async () => {
  if(form.status != '已取消' && form.status != '已完成'){
    try {
      const response = await api.post('/Reserve/update',
      {
        reserveId: form.id,
        checkIn: form.checkinDate,
        checkOut: form.checkoutDate,
        remark: form.remark,
      });
      const data = response.data;
      if (data.status === '200') {
        // console.log('保存表单:', form);
        ElMessage.success('表单已保存');
        dialogVisible.value = false;
        await reserveinfo();
      } else {
        ElMessage.error('保存表单失败');
      }
    } catch (error) {
      console.error('保存表单失败:', error);
      ElMessage.error('保存表单失败');
    }
  }else if(form.status === '已取消'){
    ElMessage.error('已取消不能修改');
  }else if(form.status === '已完成'){
    ElMessage.error('已完成不能修改');
  }
}

const viewDetails = (row) => {
  Object.assign(form, row);
  dialogVisible.value = true;
  
};


// 取消预约
const cancelReservation = async () => {
  if(form.status !== '已取消' && form.status !== '已完成'){
    try {
      console.log('取消预约:', form.id);
      const response = await api.post('/Reserve/cancel', {
        reserveId: form.id
      });
      const data = response.data;
      if (data.status === '200') {
        ElMessage.success('预约已取消');
        dialogVisible.value = false;
        await reserveinfo();
      } else {
        ElMessage.error('预约取消失败');
      }
    } catch (error) {
      console.error('预约取消失败:', error);
      ElMessage.error('预约取消失败');
    }
  }else if(form.status === '已取消'){
    ElMessage.error('已取消的预约不能再次取消');
    return;
  }else if(form.status === '已完成'){
    ElMessage.error('已完成的预约不能取消');
    return;

  }
  
};
const checkinnow = () => {
  console.log('办理入住:', form.price);
  router.push({
    path: 'check',
    query: { 
      customerName:form.customerName,
      phone:form.phone,
      checkinDate:form.checkinDate,
      checkoutDate:form.checkoutDate,
      roomType:form.roomType,
      remark:form.remark,
      cardLevel:form.cardlevel,
      points:form.points,
      price:form.price,
      rid:form.id
    }
  });
}


</script>

<style scoped>
.reservation-query-container {
  padding: 0 20px 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.reservation-query-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
}

.search-form {
  margin-bottom: 20px;
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}


</style>