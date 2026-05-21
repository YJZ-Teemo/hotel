<template>
  <div class="item-inventory-container">
    <el-card class="item-inventory-card">
      <template #header>
        <div class="card-header">
          <span>会员名单</span>
          <!-- <el-button v-if="management ==0" type="primary" @click="dialogVisible2 = true">添加物品</el-button> -->
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="searchForm" label-width="120px" class="search-form">
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="姓名" prop="Name">
              <el-input v-model="searchForm.Name" placeholder="请输入姓名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电话" prop="Phone">
              <el-input v-model="searchForm.Phone" placeholder="请输入电话" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <!-- <el-button type="primary" @click="searchItems">查询</el-button> -->
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 顾客列表 -->
      <el-table 
        :data="filteredItems" 
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="编号" width="200" />
        <el-table-column prop="Name" label="顾客姓名" width="250" />
        <el-table-column prop="Phone" label="顾客电话" width="250" />
        <el-table-column prop="Cardlevel" label="等级" width="250" />
        <el-table-column prop="Points" label="积分" width="200" />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editItem(scope.row)">查看详情</el-button>
            <!-- <el-button v-if="management ==0" size="small" type="danger" @click="deleteItem(scope.row)">删除</el-button> -->
          </template>
        </el-table-column>
      </el-table>
      

    </el-card>
    
    <!-- 编辑物品对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle2"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item label="编号">
          <p>{{ form.id }}</p>
        </el-form-item>
        <el-form-item label="姓名">
          <p>{{ form.Name }}</p>
        </el-form-item>
        <el-form-item label="电话">
          <p>{{ form.Phone }}</p>
        </el-form-item>
        <el-form-item label="会员等级">
          <p>{{ form.Cardlevel }}</p>
        </el-form-item>
        <el-form-item label="会员积分">
          <p>{{ form.Points }}</p>
        </el-form-item>
        <el-form-item label="赠送积分">
          <el-input v-model="form.newPoints" placeholder="请输入赠送积分" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveItem">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed ,onMounted } from 'vue';
import { ElMessage,ElMessageBox } from 'element-plus';
import api from '../../api';
import { el } from 'element-plus/es/locales.mjs';

// 响应式数据
const dialogVisible = ref(false);
const dialogVisible2 = ref(false);
// const dialogTitle = ref('添加顾客');
const dialogTitle2 = ref('查看顾客');
const items = ref([]); // 顾客数据列表
const management = localStorage.getItem('management');

// 查询表单
const searchForm = reactive({
  Name: '',
  Phone: ''
});



// 表单数据
const form = reactive({
  id: '',
  Name: '',
  Phone: '',
  Cardlevel: 0,
  Points: '',
  newPoints: ''
});
const form2 = reactive({
  id: '',
  Name: '',
  Phone: '',
  Cardlevel: 0,
  Points: ''
});
//获取信息
const getitems = async () => {
  try {
    const response = await api.get('/Customer/list');
    const data = response.data;
    items.value = data.map(Customer => ({
    id: Customer.id,
    Name: Customer.name,
    Phone: Customer.phone,
    Cardlevel: Customer.cardlevel,
    Points: Customer.points
  }));
  } catch (error) {
    console.error('获取顾客数据失败:', error);
    items.value = [];
    ElMessage.error('获取顾客数据失败');
  }
};

onMounted(() => {
  getitems();
});

// 根据搜索条件过滤顾客数据
const filteredItems = computed(() => {
  return items.value.filter(item => {
    
    if (searchForm.Name && !item.Name.includes(searchForm.Name)) {
      return false;
    }

    if (searchForm.Phone && !item.Phone.includes(searchForm.Phone)) {
      return false;
    }

    return true;
  });
});


// 重置查询条件
const resetSearch = () => {
  Object.assign(searchForm, {
    Name: '',
    Phone: ''
  });
  ElMessage.info('查询条件已重置');
};

// 编辑
const editItem = (row) => {
  console.log('查看详情:', row);
  dialogTitle2.value = '查看详情';
  Object.assign(form, row);
  dialogVisible.value = true;
};

// 积分赠送
const saveItem = async () => {
  try {
    const response = await api.post('/Customer/update', {
    id: form.id,
    points: form.newPoints
    })
    if (response.data.status === '200') {
      dialogVisible.value = false;
      form.newPoints = '';
      ElMessage.success('积分赠送成功');
      await getitems();
    } else {
      ElMessage.error('积分赠送失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('积分赠送失败:', error);
    ElMessage.error('积分赠送失败，请稍后重试');
    return;
  }
};

</script>

<style scoped>
.item-inventory-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

/* 使用深度选择器穿透到子组件 */
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

.item-inventory-card {
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
.item-inventory-container .el-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.item-inventory-container .el-table__header-wrapper {
  background-color: #f7f9fc;
}

/* 确保所有表头单元格居中对齐 */
.item-inventory-container .el-table__header-wrapper th {
  text-align: center !important;
  font-weight: bold;
  color: #333;
  padding: 16px 0 !important;
  background-color: #f7f9fc;
  border-bottom: 2px solid #e6e8eb;
}

/* 确保所有表格单元格（包括body）居中对齐 */
.item-inventory-container .el-table__body-wrapper td {
  text-align: center !important;
  padding: 14px 0 !important;
}

.item-inventory-container .el-table .el-table__cell {
  text-align: center !important;
  padding: 14px 0 !important;
  box-sizing: border-box;
}

/* 移除默认的单元格内边距，使用我们自定义的内边距 */
.item-inventory-container .el-table__cell > .cell {
  padding: 0 !important;
  margin: 0 !important;
  text-align: center !important;
}

.item-inventory-container .el-table__body-wrapper tr {
  transition: all 0.3s ease;
}

.item-inventory-container .el-table__body-wrapper tr:hover {
  background-color: #f5f7fa;
}

/* 状态标签样式 */
.item-inventory-container .el-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 0px;
}

/* 操作按钮样式 */
.item-inventory-container .el-button {
  margin: 0 4px;
  padding: 6px 14px;
  font-size: 13px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.item-inventory-container .el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}

.item-inventory-container .el-button--primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.item-inventory-container .el-button--success {
  background-color: #67c23a;
  border-color: #67c23a;
}

.item-inventory-container .el-button--success:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}

.item-inventory-container .el-button--danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
}

.item-inventory-container .el-button--danger:hover {
  background-color: #f78989;
  border-color: #f78989;
}


</style>