<template>
  <div class="checkin-container">
    <el-card class="checkin-card">
      <template #header>
        <div class="card-header">
          <span>入住信息填写</span>
        </div>
      </template>
      
      <el-form ref="checkinFormRef" :model="checkinForm" label-width="120px" class="checkin-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="顾客姓名" prop="customerName">
              <el-input v-model="checkinForm.customerName" placeholder="请输入顾客姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="checkinForm.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="checkinForm.phone" placeholder="请输入手机号码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员卡等级" prop="cardLevel">
              <el-input v-model="checkinForm.cardLevel" placeholder="会员卡等级" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入住日期" prop="checkinDate">
              <el-date-picker
                v-model="checkinForm.checkinDate"
                type="date"
                placeholder="选择入住日期"
                style="width: 100%;"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退房日期" prop="checkoutDate">
              <el-date-picker
                v-model="checkinForm.checkoutDate"
                type="date"
                placeholder="选择退房日期"
                style="width: 100%;"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="checkinForm.roomType" placeholder="选择房间类型" @change="handleRoomTypeChange">
                <el-option label="全部" value="" />
                <el-option label="标准间" value="standard" />
                <el-option label="豪华间" value="deluxe" />
                <el-option label="套房" value="suite" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-select v-model="checkinForm.roomNumber" placeholder="请选择可入住房间号" clearable filterable>
                <el-option
                  v-for="room in availableRooms"
                  :key="room.roomNumber"
                  :label="room.roomNumber"
                  :value="room.roomNumber"
                >
                  {{ room.roomNumber }} - {{ getRoomTypeLabel(room.roomType) }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="积分" prop="points">
              <el-input-number v-model="checkinForm.points" :min="0" :max="99999" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房费价格" prop="totalAmount">
              <el-input-number v-model="checkinForm.totalAmount" :min="1" :max="10000" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="checkinForm.remark" type="textarea" :rows="4" placeholder="请输入备注信息" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm">提交入住</el-button>
          <el-button type="success" @click="readIdCard">读取身份证</el-button>
          <el-button type="info" @click="goToRoomInfo">查询房间</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../../api'; 
import { ElMessage } from 'element-plus';

// 表单数据
const checkinForm = reactive({
  customerName: '',
  idCard: '',
  phone: '',
  cardLevel: '', // 会员卡等级
  checkinDate: '',
  checkoutDate: '',
  roomType: '',
  roomNumber: '',
  totalAmount: 0,
  points: 0, // 积分默认值为0
  remark: '',
  rid: '' // 预留字段，用于存储预订信息的ID
});

// 获取路由对象
const route = useRoute();
const router = useRouter();

// 在组件挂载时检查是否有房间号参数
onMounted(async () => {
  await fetchRooms();
  
  if (route.query.roomNumber) {
    checkinForm.roomNumber = route.query.roomNumber;
    checkinForm.roomType = route.query.roomType;
  }
  if (route.query.customerName) {
    checkinForm.roomType = route.query.roomType;
    checkinForm.customerName = route.query.customerName;
    checkinForm.phone = route.query.phone;
    checkinForm.checkinDate = route.query.checkinDate;
    checkinForm.checkoutDate = route.query.checkoutDate;
    checkinForm.remark = route.query.remark; 
    checkinForm.cardLevel = route.query.cardLevel;
    checkinForm.points = route.query.points;
    checkinForm.totalAmount = route.query.price;
    checkinForm.rid = route.query.rid;
  }
});

// 表单引用
const checkinFormRef = ref(null);

// 房间数据
const rooms = ref([]);
const fetchRooms = async () => {
  try {
    const response = await api.get('/Room/list');
    const data = response.data;
    console.log('房间列表接口响应:', data);
    rooms.value = data.map(room => ({
      roomNumber: room.num,
      roomType: room.type,
      status: room.status,
      price: Number(room.price),
      capacity: Number(room.people),
      floor: Number(room.num) ? Math.floor(Number(room.num) / 100) : 1,
      area: Number(room.area),
      description: room.details
    }));
  } catch (error) {
    console.error('获取房间数据失败:', error);
  }
};

//房间类型映射
const roomTypeMap = {
  'standard': { label: '标准间', color: 'primary' },
  'deluxe': { label: '豪华间', color: 'success' },
  'suite': { label: '套房', color: 'warning' }
};

// 获取房间类型标签
const getRoomTypeLabel = (roomType) => {
  return roomTypeMap[roomType]?.label || roomType;
};

// 计算可入住的房间，根据房间类型过滤
const availableRooms = computed(() => {
  if (rooms.value.length === 0) {
    return [];
  }
  const filtered = rooms.value.filter(room => {
    // 只显示可入住的房间
    if (room.status !== '可入住') {
      return false;
    }
    // 如果选择了房间类型，只显示对应类型的房间
    if (checkinForm.roomType && room.roomType !== checkinForm.roomType) {
      return false;
    } 
    return true;
  });
  
  return filtered;
});


// 处理房间类型变化
const handleRoomTypeChange = () => {
  // 当房间类型变化时，清空已选择的房间号
  checkinForm.roomNumber = '';
};

// 提交表单
const submitForm = async () => {
  try{
    const response = await api.post('/check/in', {
      cname: checkinForm.customerName,
      cphone: checkinForm.phone,
      checkindate: checkinForm.checkinDate,
      checkoutdate: checkinForm.checkoutDate,
      rtype: checkinForm.roomType,
      rnumber: checkinForm.roomNumber,
      totalamount: checkinForm.totalAmount,
      points: checkinForm.points,
      remark: checkinForm.remark,
      rid: checkinForm.rid,
    });
    const data = response.data;
    if (data.status === '200') {
        ElMessage.success('提交入住成功');
        resetForm();
      }else{
        ElMessage.error('提交入住失败: ' + data.message);
      }
  }catch(error){
    console.error('提交入住信息失败:', error);
    ElMessage.error('提交入住信息失败');
    return;
  }
};

// 重置表单
const resetForm = () => {
  Object.assign(checkinForm, {
    customerName: '',
    idCard: '',
    phone: '',
    checkinDate: '',
    checkoutDate: '',
    roomType: '',
    roomNumber: '',
    guestCount: 1,
    points: 0, // 重置积分为0
    remark: ''
  });
};

// 跳转到房间信息页面
const goToRoomInfo = () => {
  // 进行跳转，确保能正确传递参数
  router.push('/roominfo');
};

// 读取身份证信息
const readIdCard = () => {
  // 模拟身份证阅读器读取过程
  ElMessage.info('正在读取身份证信息...');
  
  // 模拟API调用或硬件读取延迟
  setTimeout(() => {
    // 模拟读取到的身份证信息
    const idCardInfo = {
      customerName: '张三',
      idCard: '110101199001011234',
      cardLevel:'普通会员'
    };
    
    // 设置当前日期为入住日期，默认入住1天
    const now = new Date();
    const checkinDate = new Date(now);
    const checkoutDate = new Date(now);
    checkoutDate.setDate(now.getDate() + 1);
    
    // 填充表单
    checkinForm.customerName = idCardInfo.customerName;
    checkinForm.idCard = idCardInfo.idCard;
    checkinForm.phone = idCardInfo.phone;
    checkinForm.cardLevel = idCardInfo.cardLevel;
    checkinForm.checkinDate = checkinDate;
    checkinForm.checkoutDate = checkoutDate;
    
    // 读取成功提示
    ElMessage.success('身份证信息读取成功');
  }, 1000);
};
</script>

<style scoped>
.checkin-container {
  padding: 0 20px 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.checkin-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkin-form {
  margin-top: 20px;
}

.recent-checkins {
  margin-top: 20px;
}
</style>