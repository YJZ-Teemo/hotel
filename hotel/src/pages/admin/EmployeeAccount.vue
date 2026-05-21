<template>
  <div class="item-inventory-container">
    <el-card class="item-inventory-card">
      <template #header>
        <div class="card-header">
          <span>员工名单</span>
          <el-button type="primary" @click="dialogVisible2 = true">添加员工</el-button>
        </div>
      </template>
      
      <!-- 查询条件 -->
      <el-form :model="searchForm" label-width="120px" class="search-form">
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="searchForm.phone" placeholder="请输入电话" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="职务" prop="management">
              <el-select v-model="searchForm.management" placeholder="选择职务" clearable>
                <el-option label="全部" value="" />
                <el-option label="前台" value="前台" />
                <el-option label="客房" value="客房" />
                <el-option label="餐饮" value="餐饮" />
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
      
      <!-- 员工列表 -->
      <el-table 
        :data="filteredItems" 
        style="width: 100%"
        stripe
      >
        <el-table-column prop="id" label="编号" width="200" />
        <el-table-column prop="username" label="员工账号" width="250" />
        <el-table-column prop="name" label="员工姓名" width="250" />
        <el-table-column prop="phone" label="员工电话" width="250" />
        <el-table-column prop="management" label="职务" width="200" />

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
        <el-form-item label="员工账号">
          <el-input v-model="form.username" disabled placeholder="请输入员工账号" />
        </el-form-item>
        <el-form-item label="员工姓名">
          <el-input v-model="form.name" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="员工电话">
          <el-input v-model="form.phone" placeholder="请输入员工电话" />
        </el-form-item>
        <el-form-item label="职务" prop="management">
              <el-select v-model="form.management" placeholder="选择职务" clearable>
                <el-option label="全部" value="" />
                <el-option label="前台" value="前台" />
                <el-option label="客房" value="客房" />
                <el-option label="餐饮" value="餐饮" />
                <el-option label="维护中" value="维护中" />
              </el-select>
            </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="danger" size="small" @click.stop="deleteItem">
              删除
            </el-button>
          <el-button type="primary" @click="saveItem">保存</el-button>
        </span>
      </template>
    </el-dialog>
    <!-- 添加 -->
    <el-dialog
      v-model="dialogVisible2"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="form3" label-width="100px">
        <el-form-item label="员工账号">
          <el-input v-model="form3.username" placeholder="请输入员工账号" />
        </el-form-item>
        <el-form-item label="员工密码">
          <el-input v-model="form3.password" placeholder="请输入员工密码" />
        </el-form-item>
        <el-form-item label="员工姓名">
          <el-input v-model="form3.name" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="员工电话">
          <el-input v-model="form3.phone" placeholder="请输入员工电话" />
        </el-form-item>
        <el-form-item label="职务" prop="management">
              <el-select v-model="form3.management" placeholder="选择职务" clearable>
                <el-option label="全部" value="" />
                <el-option label="前台" value="前台" />
                <el-option label="客房" value="客房" />
                <el-option label="餐饮" value="餐饮" />
                <el-option label="维护中" value="维护中" />
              </el-select>
            </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible2 = false">取消</el-button>
          <el-button type="primary" @click="saveItem2">添加</el-button>
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
const dialogTitle = ref('添加员工');
const dialogTitle2 = ref('查看员工');
const items = ref([]); // 员工数据列表
const management = localStorage.getItem('management');

// 查询表单
const searchForm = reactive({
  name: '',
  phone: '',
  management:''
});



// 表单数据
const form = reactive({
  id: '',
  Name: '',
  Phone: '',
  management: '',
  Username:''
});
const form3 = reactive({
  Name: '',
  Phone: '',
  management: '',
  Username:'',
  Password:''
});
//获取信息
const getitems = async () => {
  try {
    const response = await api.get('/Employeeaccount/list');
    const data = response.data;
    items.value = data.map(Emp => ({
    id: Emp.id,
    username: Emp.username,
    name: Emp.name,
    phone: Emp.phone,
    management: Emp.management,
  }));
  } catch (error) {
    console.error('获取员工数据失败:', error);
    items.value = [];
    ElMessage.error('获取员工数据失败');
  }
};

onMounted(() => {
  getitems();
});

// 根据搜索条件过滤数据
const filteredItems = computed(() => {
  return items.value.filter(item => {
    
    if (searchForm.name && !item.name.includes(searchForm.name)) {
      return false;
    }

    if (searchForm.phone && !item.phone.includes(searchForm.phone)) {
      return false;
    }
    if (searchForm.management && item.management !== searchForm.management) {
      return false;
    }

    return true;
  });
});


// 重置查询条件
const resetSearch = () => {
  Object.assign(searchForm, {
    name: '',
    phone: '',
    management:''
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

// 保存更改
const saveItem = async () => {
  try {
    const response = await api.post('/Employeeaccount/update', {
    id: form.id,
    name: form.name,
    phone: form.phone,
    management: form.management
    })
    if (response.data.status === '200') {
      dialogVisible.value = false;
      ElMessage.success('更改成功');
      await getitems();
    } else {
      ElMessage.error('更改失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('更改失败:', error);
    ElMessage.error('更改失败，请稍后重试');
    return;
  }
};
// 删除
const deleteItem = async () => {
  try {
    const response = await api.post('/Employeeaccount/del', {
    id: form.id
    })
    if (response.data.status === '200') {
      dialogVisible.value = false;
      ElMessage.success('删除成功');
      await getitems();
    } else {
      ElMessage.error('删除失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('删除失败:', error);
    ElMessage.error('删除失败，请稍后重试');
    return;
  }
};
//创建账号
const saveItem2 = async () => {
  try {
    const response = await api.post('/Employeeaccount/register', form3)
    if (response.data.status === '200') {
      dialogVisible2.value = false;
      ElMessage.success('添加成功');
      await getitems();
    } else {
      ElMessage.error('添加失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('添加失败:', error);
    ElMessage.error('添加失败，请稍后重试');
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