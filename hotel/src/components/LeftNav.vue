<template>
     <!-- 侧边栏导航 -->
      <el-aside width="200px" class="admin-sidebar">
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          @select="handleMenuSelect"
          router
        >
          <el-menu-item index="/check">
            <el-icon><icon-menu /></el-icon>
            <span>入住登记</span>
          </el-menu-item>
          <el-menu-item index="/reservationquery">
            <el-icon><icon-menu /></el-icon>
            <span>预约查询</span>
          </el-menu-item>
          <!-- <el-menu-item index="/schedulequery">
              <el-icon><Calendar /></el-icon>
            <span>日程查询</span>
          </el-menu-item> -->
          <el-menu-item index="/checkinquery">
              <el-icon><icon-menu /></el-icon>
            <span>入住查询</span>
          </el-menu-item>
          <el-sub-menu index="3">
              <template #title>
                <el-icon><el-icon-ShoppingCart /></el-icon>
                <span>房间管理</span>
              </template>
              <el-menu-item index="/roominfo">房间信息</el-menu-item>
              <el-menu-item index="/iteminventory">物品库存</el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/customer">
              <el-icon><icon-menu /></el-icon>
            <span>会员名单</span>
          </el-menu-item>
          <el-sub-menu index="4">
              <template #title>
                <el-icon><el-icon-ShoppingCart /></el-icon>
                <span>人员管理</span>
              </template>
              <el-menu-item index="/employeeaccount">人员名单</el-menu-item>
          </el-sub-menu>
          
       
          
         
        </el-menu>
      </el-aside>
</template>  

<script setup>
import { ref } from 'vue';
import { Calendar, Search } from '@element-plus/icons-vue';

// 响应式数据
const activeMenu = ref('/');

// 菜单选择事件处理
const handleMenuSelect = (key, keyPath) => {
  console.log('当前选中的菜单:', key, keyPath);
  activeMenu.value = key;
};
</script>

<style scoped>
/* 侧边栏样式 */
.admin-sidebar {
  background-color: #fff;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.08);
  margin: 0;
  top: 0;
  left: 0;
}

.admin-sidebar .el-menu {
  background-color: #fff;
  border-right: none;
}

.admin-sidebar .el-menu-item {
  color: #606266;
  font-size: 14px;
  height: 50px;
  line-height: 50px;
}

.admin-sidebar .el-sub-menu .el-menu-item {
  padding-left: 60px;
}

.admin-sidebar .el-menu-item.is-active {
  color: #1890ff;
  background-color: #e6f7ff;
}

.admin-sidebar .el-sub-menu__title {
  color: #606266;
  font-size: 14px;
  height: 50px;
  line-height: 50px;
}

/* 侧边栏容器样式 */
.el-aside {
  margin: 0;
  padding: 0;
}

/* 垂直菜单样式 */
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>