<template>
  <div class="item-inventory-container">
    <el-card class="item-inventory-card">
      <template #header>
        <div class="card-header">
          <span>物品库存信息</span>
          <el-button type="primary" @click="dialogVisible2 = true">添加物品</el-button>
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="searchForm" label-width="120px" class="search-form">
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="物品名称" prop="itemName">
              <el-input v-model="searchForm.itemName" placeholder="请输入物品名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="物品类型" prop="itemType">
              <el-select v-model="searchForm.itemType" placeholder="选择物品类型" clearable>
                <el-option label="全部" value="" />
                <el-option v-for="type in itemTypes" :key="type.value" :label="type.label" :value="type.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存状态" prop="status">
              <el-select v-model="searchForm.status" placeholder="选择库存状态" clearable>
                <el-option label="全部" value="" />
                <el-option label="充足" value="sufficient" />
                <el-option label="不足" value="insufficient" />
                <el-option label="缺货" value="out" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <!-- <el-button type="primary" @click="searchItems">查询</el-button> -->
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 物品库存列表 -->
      <el-table 
        :data="filteredItems" 
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="物品编号" width="200" />
        <el-table-column prop="itemName" label="物品名称" width="250" />
        <el-table-column prop="itemType" label="物品类型" width="200">
          <template #default="scope">
            <el-tag :type="getItemTypeTagType(scope.row.itemType)">
              {{ scope.row.itemType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存数量" width="200" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column label="库存状态" width="200">
          <template #default="scope">
            <el-tag :type="getStatusTagType(calculateStatus(scope.row.stock))">
              {{ getStatusText(calculateStatus(scope.row.stock)) }}
            </el-tag>
          </template>
        </el-table-column>


        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="editItem(scope.row)">查看详情</el-button>
            <el-button v-if="management ==0" size="small" type="danger" @click="deleteItem(scope.row)">删除</el-button>
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
        <el-form-item label="物品编号">
          <el-input v-model="form.id" disabled />
        </el-form-item>
        <el-form-item label="物品类型">
          <el-select v-model="form.itemType" placeholder="请选择物品类型">
            <el-option v-for="type in itemTypes" :key="type.value" :label="type.label" :value="type.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="物品名称">
          <el-input v-model="form.itemName" placeholder="请输入物品名称" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="库存数量">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveItem">保存</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 添加物品对话框 -->
    <el-dialog
      v-model="dialogVisible2"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form2" label-width="100px">
        <el-form-item label="物品类型">
          <el-select v-model="form2.itemType" placeholder="请选择物品类型">
            <el-option v-for="type in itemTypes" :key="type.value" :label="type.label" :value="type.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="物品名称">
          <el-input v-model="form2.itemName" placeholder="请输入物品名称" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form2.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="库存数量">
          <el-input-number v-model="form2.stock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible2 = false">取消</el-button>
          <el-button type="primary" @click="addItem">添加</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed ,onMounted } from 'vue';
import { ElMessage,ElMessageBox } from 'element-plus';
import api from '../../api';

// 响应式数据
const dialogVisible = ref(false);
const dialogVisible2 = ref(false);
const dialogTitle = ref('添加物品');
const dialogTitle2 = ref('查看物品');
const items = ref([]); // 物品数据列表
const management = localStorage.getItem('management');

// 查询表单
const searchForm = reactive({
  itemName: '',
  itemType: '',
  status: ''
});

// 物品类型列表
const itemTypes = [
  { label: '饮品', value: '饮品' },
  { label: '办公用品', value: '办公用品' },
  { label: '客房用品', value: '客房用品' },
  { label: '清洁用品', value: '清洁用品' },
  { label: '其他', value: '其他' }
];

// 表单数据
const form = reactive({
  id: '',
  itemName: '',
  itemType: '',
  stock: 0,
  unit: '',
  status: 'sufficient'
});
const form2 = reactive({
  id: '',
  itemName: '',
  itemType: '',
  stock: 0,
  unit: '',
  status: 'sufficient'
});
//获取库存信息
const getitems = async () => {
  try {
    const response = await api.get('/Inventory/list');
    const data = response.data;
    // items.value = Array.isArray(data.data) ? data.data : [];
    items.value = data.map(items => ({
    id: items.num,
    itemType: items.type,
    stock: Number(items.quantity),
    unit: items.unit,
    itemName: items.name
  }));
  } catch (error) {
    console.error('获取物品数据失败:', error);
    items.value = [];
    ElMessage.error('获取物品数据失败');
  }
};

onMounted(() => {
  getitems();
});

// 计算库存状态
const calculateStatus = (stock) => {
  if (stock === 0) {
    return 'out';
  } else if (stock < 20) {
    return 'insufficient';
  } else {
    return 'sufficient';
  }
};

// 根据搜索条件过滤物品数据
const filteredItems = computed(() => {
  return items.value.filter(item => {
    // 计算动态状态
    const dynamicStatus = calculateStatus(item.stock);
    
    // 物品名称过滤
    if (searchForm.itemName && !item.itemName.includes(searchForm.itemName)) {
      return false;
    }
    // 物品类型过滤
    if (searchForm.itemType && item.itemType !== searchForm.itemType) {
      return false;
    }
    // 状态过滤
    if (searchForm.status && dynamicStatus !== searchForm.status) {
      return false;
    }
    return true;
  });
});


// 获取物品类型标签类型
const getItemTypeTagType = (type) => {
  const typeMap = {
    饮品: 'info',
    办公用品: 'success',
    客房用品: 'warning',
    清洁用品: 'danger',
    其他: 'primary'
  };
  return typeMap[type] || 'info';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    sufficient: '充足',
    insufficient: '不足',
    out: '缺货'
  };
  return statusMap[status] || status;
};

// 获取状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    sufficient: 'success',
    insufficient: 'warning',
    out: 'danger'
  };
  return typeMap[status] || 'info';
};

// 查询物品
// const searchItems = () => {
//   console.log('查询条件:', searchForm);
//   ElMessage.info('查询完成');
// };

// 重置查询条件
const resetSearch = () => {
  Object.assign(searchForm, {
    itemName: '',
    itemType: '',
    status: ''
  });
  ElMessage.info('查询条件已重置');
};

// 编辑物品
const editItem = (row) => {
  console.log('编辑物品:', row);
  dialogTitle2.value = '编辑物品';
  Object.assign(form, row);
  dialogVisible.value = true;
};

// 删除物品
const deleteItem = async (row) => {
  try{
    await ElMessageBox.confirm(
      `确定要删除物品 ${row.itemName} 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    const response = await api.post('/Inventory/del', {
      num: row.id
    });
    const data = response.data;
    if (data.status === '200') {
      ElMessage.success('物品删除成功');
      await getitems();
    } else {      
      ElMessage.error('物品删除失败: ' + data.msg);
    }
  } catch (error) {
    console.error('删除物品失败:', error);
    ElMessage.error('删除物品失败，请稍后重试');
  }
};

// 库存修改
const saveItem = async () => {
  try {
    const response = await api.post('/Inventory/update', {
    num: form.id,
    name: form.itemName,
    type: form.itemType,
    quantity: form.stock,
    unit: form.unit
    })
    if (response.data.status === '200') {
      ElMessage.success('物品保存成功');
      dialogVisible.value = false;
      await getitems();
    } else {
      ElMessage.error('物品保存失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('保存物品失败:', error);
    ElMessage.error('保存物品失败，请稍后重试');
    return;
  }
};
const addItem = async () => {
  try {
    const response = await api.post('/Inventory/add', {
    name: form2.itemName,
    type: form2.itemType,
    quantity: form2.stock,
    unit: form2.unit
    })
    if (response.data.status === '200') {
      ElMessage.success('物品添加成功');
      dialogVisible2.value = false;
      await getitems();
    } else {
      ElMessage.error('物品添加失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('添加物品失败:', error);
    ElMessage.error('添加物品失败，请稍后重试');
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