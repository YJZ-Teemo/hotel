 <template>
 <!-- 顶部栏 -->
    <el-header class="admin-header">
      <div class="header-left">
        <span class="logo">后台管理系统</span>
      </div>
      <div class="header-right">
        <el-dropdown>
          <span class="user-info">
            <el-avatar size="small" :src="'/src/assets/vue.svg'"></el-avatar>
            <span style="margin-left: 10px;">{{ username }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人信息</el-dropdown-item>
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item divided @click="out">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
</template>
<script setup>
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter();  
const username = localStorage.getItem('username');
const out = () => {
  localStorage.clear();
  router.push('/');
  ElMessage.success('退出登录成功');
}

</script>

<style scoped>
/* 顶部栏样式 */
.admin-header {
  background-color: #d2dae3;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  margin: 0;
  top: 0;
  left: 0;
}

/* 顶部栏左侧样式 */
.header-left {
  display: flex;
  top: 20px;
  left: 0;
  align-items: center;
}

/* Logo样式 */
.logo {
    padding-left: 15px;
    font-size: 20px;
    font-weight: bold;
    color: #1890ff;
}

/* 用户信息样式 */
.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  padding-top: 15px;
  padding-right: 15px;
}

/* 顶部栏容器样式 */
.el-header {
  height: 60px;
  line-height: 60px;
  margin: 0;
  padding: 0;
}
</style>
