<template>
  <el-container class="admin-container">
    <!-- 顶部导航 -->
    <TopNav />
    <el-container>
      <!-- 左侧导航 -->
      <LeftNav />
      <!-- 主内容区域 -->
      <div class="admin-main">
        <router-view></router-view>
      </div>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import TopNav from '../components/TopNav.vue';
import LeftNav from '../components/LeftNav.vue';

const router = useRouter();

// 响应式数据
const logout =() =>{
  localStorage.removeItem('management');
  localStorage.removeItem('username');
  router.push('/');
}
</script>

<style scoped>
/* 主容器样式 */
.admin-container {
  height: 100vh;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
}

/* 内部容器样式 - 水平布局 */
.admin-container > .el-container {
  display: flex;
  flex-direction: row;
  flex: 1;
  overflow: hidden;
}

/* 主内容区域样式 */
.admin-main {
  background-color: #f0f2f5;
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

/* 容器样式 */
.el-container {
  margin: 0;
  padding: 0;
  width: 100%;
}
</style>
