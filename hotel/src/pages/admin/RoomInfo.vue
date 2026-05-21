<template>
  <div class="room-info-container">
    <el-card class="room-info-card">
      <template #header>
        <div class="card-header">
          <span>房间信息查询</span>
            <el-button type="primary" @click="showAddRoomDialog">添加房间</el-button>
        </div>
      </template>

    <el-form :model="searchQuery" label-width="120px" class="search-form">
      <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="房号查询" prop="roomNumber">
              <el-input v-model="searchQuery.roomNumber" placeholder="请输入房间号码" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房间类型" prop="roomType">
              <el-select v-model="searchQuery.roomType" placeholder="选择房间类型" clearable>
                <el-option label="全部" value="" />
                <el-option label="标准间" value="标准间" />
                <el-option label="豪华间" value="豪华间" />
                <el-option label="套房" value="套房" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房间状态" prop="status">
              <el-select v-model="searchQuery.status" placeholder="选择房间状态" clearable>
                <el-option label="全部" value="" />
                <el-option label="可入住" value="可入住" />
                <el-option label="待收拾" value="待收拾" />
                <el-option label="已入住" value="已入住" />
                <el-option label="维护中" value="维护中" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <!-- <el-button type="primary" @click="searchItems">查询</el-button> -->
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
    </el-form>
      
      <el-table 
        :data="filteredRooms" 
        style="width: 100%"
        @row-click="selectRoom"
        stripe
      >
        <el-table-column prop="roomNumber" label="房间号" width="120" />
        <el-table-column prop="roomType" label="房间类型" width="120">
          <template #default="scope">
            <el-tag :type="roomTypeMap[scope.row.roomType]?.color || 'info'" :style="{ borderRadius: '0px' }">
              {{ roomTypeMap[scope.row.roomType]?.label || scope.row.roomType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="房间状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === '可入住' ? 'success' : 'danger'" :style="{ borderRadius: '0px' }">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格(元/晚)" width="150" />
        <el-table-column prop="capacity" label="可住人数" width="120" />
        <el-table-column prop="area" label="面积(㎡)" width="120" />
        <el-table-column prop="description" label="房间描述" min-width="200" />
        <el-table-column v-if="management == 0" label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button type="danger" size="small" @click.stop="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 添加房间对话框 -->
    <el-dialog
      v-model="addRoomDialogVisible"
      title="添加房间"
      width="500px"
    >
      <el-form :model="addRoomForm" label-width="120px" class="add-room-form">
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="addRoomForm.roomNumber" placeholder="请输入房间号" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="房间类型" prop="roomType">
          <el-select v-model="addRoomForm.roomType" placeholder="请选择房间类型" style="width: 200px;">
            <el-option label="标准间" value="标准间" />
            <el-option label="豪华间" value="豪华间" />
            <el-option label="套房" value="套房" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格(元/晚)" prop="price">
          <el-input-number v-model="addRoomForm.price" :min="0" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="可住人数" prop="capacity">
          <el-input-number v-model="addRoomForm.capacity" :min="1" :max="10" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="area">
          <el-input v-model="addRoomForm.area" placeholder="请输入面积" style="width: 200px;" />
        </el-form-item>
        <el-form-item label="房间描述" prop="description">
          <el-input v-model="addRoomForm.description" type="textarea" :rows="3" placeholder="请输入房间描述" style="width: 300px;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="addRoomDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addRoom">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter, useRoute } from 'vue-router';
import api from '../../api';

const router = useRouter();
const route = useRoute();
const management = localStorage.getItem('management');

// 房间类型映射
const roomTypeMap = {
  'standard': { label: '标准间', color: 'primary' },
  'deluxe': { label: '豪华间', color: 'success' },
  'suite': { label: '套房', color: 'warning' }
};

// 搜索查询
const searchQuery = reactive({
  roomNumber: '',
  roomType: '',
  status: ''
});

// 添加房间对话框显示状态
const addRoomDialogVisible = ref(false);

// 添加房间表单数据
const addRoomForm = reactive({
  roomNumber: '',
  roomType: '标准间',
  price: 0,
  capacity: 2,
  floor: 1,
  area: 0,
  description: ''
});

// 房间数据
const rooms = ref([]);

// 获取房间数据
const fetchRooms = async () => {
  try {
    const response = await api.get('/Room/list');
    const data = response.data;
    rooms.value = data.map(room => ({
      roomNumber: room.num,
      roomType: room.type,
      status: room.status,
      price: Number(room.price),
      capacity: Number(room.people),
      floor: Number(room.floor) ? Math.floor(Number(room.floor) / 100) : 1,
      area: Number(room.area),
      description: room.details
    }));
  } catch (error) {
    console.error('获取房间数据失败:', error);
    rooms.value = []; // 确保是数组
    ElMessage.error('获取房间数据失败');
  }
};

onMounted(() => {
  fetchRooms();
});

const getRoomTypeLabel = (type) => {
  return roomTypeMap[type]?.label || type;
};

const normalizeStatus = (status) => {
  if (!status) return '';
  return status.replace('待打扫', '待收拾');
};

// 过滤后的房间列表
const filteredRooms = computed(() => {
  return rooms.value.filter(room => {
    const roomNumberMatch = !searchQuery.roomNumber || room.roomNumber.includes(searchQuery.roomNumber);
    const roomTypeLabel = getRoomTypeLabel(room.roomType);
    const roomTypeMatch = !searchQuery.roomType || room.roomType === searchQuery.roomType || roomTypeLabel === searchQuery.roomType;
    const statusMatch = !searchQuery.status || normalizeStatus(room.status) === normalizeStatus(searchQuery.status);
    return roomNumberMatch && roomTypeMatch && statusMatch;
  });
});

// 重置查询条件
const resetSearch = () => {
  searchQuery.roomNumber = '';
  searchQuery.roomType = '';
  searchQuery.status = '';
  ElMessage.success('查询条件已重置');
};

// 选择房间
const selectRoom = (row) => {
  if (row.status === '已入住') {
    ElMessage.warning('该房间已入住，无法选择');
    return;
  }else if (row.status === '待收拾') {
    ElMessage.warning('该房间未打扫，无法选择');
    return;
  }else if (row.status === '维护中') {
    ElMessage.warning('该房间维护中，无法选择');
    return;
  }
  
  // 将房间号传递回入住页面
  ElMessage.success(`已选择房间 ${row.roomNumber}`);
  
  // 跳转到入住页面，并传递房间号和房间类型
  router.push({
    path: 'check',
    query: { roomNumber: row.roomNumber, roomType: row.roomType }
  });
};

// 显示添加房间对话框
const showAddRoomDialog = () => {
  addRoomDialogVisible.value = true;
};

// 添加房间
const addRoom = async () => {
  if (!addRoomForm.roomNumber) {
    ElMessage.warning('请输入房间号');
    return;
  }
  
  // 检查房间号是否已存在
  const existingRoom = rooms.value.find(room => room.roomNumber === addRoomForm.roomNumber);
  if (existingRoom) {
    ElMessage.error('房间号已存在');
    return;
  }
  const roomTypeValue = addRoomForm.roomType === '标准间' ? 'standard' : addRoomForm.roomType === '豪华间' ? 'deluxe' : addRoomForm.roomType === '套房' ? 'suite' : addRoomForm.roomType;
  try {
    const response = await api.post('/Room/add', {
      num: addRoomForm.roomNumber,
      type: roomTypeValue,
      price: addRoomForm.price,
      people: addRoomForm.capacity,
      area: addRoomForm.area,
      details: addRoomForm.description
    });
    const data = response.data;
    if (data.status === '200') {
      ElMessage.success('房间添加成功');
      addRoomDialogVisible.value = false;
      await fetchRooms();
      Object.assign(addRoomForm, {
        roomNumber: '',
        roomType: '标准间',
        price: 0,
        capacity: 2,
        floor: 1,
        area: 0,
        description: ''
      });
    } else {
      ElMessage.error('房间添加失败: ' + data.text);
    }
  } catch (error) {
    console.error('网络错误:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

// 处理删除房间
const handleDelete = async (room) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除房间 ${room.roomNumber} 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );

    const response = await api.post('/Room/del', {
      num: room.roomNumber
    });
    const data = response.data;
    if (data.status === '200') {
      ElMessage.success('房间删除成功');
      await fetchRooms();
    } else {
      ElMessage.error('房间删除失败: ' + data.text);
    }
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消删除
      return;
    }
    console.error('网络错误:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

</script>
<style scoped>
.room-info-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}
:deep(.el-scrollbar__view) {
  display: block;
}
.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 24px;
  color: #2c3e50;
  padding-bottom: 12px;
  border-bottom: 2px solid #409eff;
}

.room-info-card {
  margin-bottom: 20px;
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
.room-info-card {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.room-info-container .el-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.room-info-container .el-table__header-wrapper {
  background-color: #f7f9fc;
}

/* 确保所有表头单元格居中对齐 */
.room-info-container .el-table__header-wrapper th {
  text-align: center !important;
  font-weight: bold;
  color: #333;
  padding: 16px 0 !important;
  background-color: #f7f9fc;
  border-bottom: 2px solid #e6e8eb;
}

/* 确保所有表格单元格（包括body）居中对齐 */
.room-info-container .el-table__body-wrapper td {
  text-align: center !important;
  padding: 14px 0 !important;
}

.room-info-container .el-table .el-table__cell {
  text-align: center !important;
  padding: 14px 0 !important;
  box-sizing: border-box;
}

/* 移除默认的单元格内边距，使用我们自定义的内边距 */
.room-info-container .el-table__cell > .cell {
  padding: 0 !important;
  margin: 0 !important;
  text-align: center !important;
}

.room-info-container .el-table__body-wrapper tr {
  transition: all 0.3s ease;
}

.room-info-container .el-table__body-wrapper tr:hover {
  background-color: #f5f7fa;
}

/* 状态标签样式 */
.room-info-container .el-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 0px;
}

/* 操作按钮样式 */
.room-info-container .el-button {
  margin: 0 4px;
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.room-info-container .el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}

.room-info-container .el-button--primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.room-info-container .el-button--success {
  background-color: #67c23a;
  border-color: #67c23a;
}

.room-info-container .el-button--success:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}

.room-info-container .el-button--danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.room-info-container .el-button--danger:hover {
  background-color: #f78989;
  border-color: #f78989;
}
</style>