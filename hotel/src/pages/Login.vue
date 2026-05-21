<template>
  <div id="box">
	  <h2>账号登陆</h2>
    <input type="text" placeholder="请输入账号" v-model="username">
    <input type="password" placeholder="请输入密码" v-model="password" @keyup.enter="login">
	<el-button type="primary" @click="login">登陆</el-button>
	<br />
	<router-link to="/">忘记密码</router-link>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import api from '../api';

const router = useRouter();

const username = ref('');
const password = ref('');

const login = async () => {
  try {
    const response = await api.post('/Employeeaccount/login', {
      username: username.value,
      password: password.value
    });

    const data = response.data;

    if (data.status === '200') {
      localStorage.setItem('username', data.Username);
      localStorage.setItem('management', data.Management);
      router.push('/admin');

      console.log('router 类型:', typeof router);  // ← 加这行
      console.log('router:', router);   

      ElMessage.success('登录成功');
    } else {
      ElMessage.error('登录失败: ' + data.message);
    }
  } catch (error) {
    console.error('网络错误:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};
</script>

<style scoped>
	*{
		padding: 0;
		margin: 0;
		box-sizing: border-box;
	}
body{
	/* background-color: #f5f5f5; */
}
#box{
	width: 400px;
	/* background-color: #09f; */
	margin: 0 auto;
	text-align: center;
}
input{
	width: 300px;
	padding: 8px 20px;
	margin: 25px auto;
	display: block;
	background-color: #f2f2f2;
	border: 0;
	border-radius: 5px;
	
}
.el-button--primary{
	width: 290px;
	padding: 0px 50px;
	margin-bottom: 20px;
}
.router-link-active{
	border-right: 1px solid #ccc;
}
a{
	padding: 0 10px;
}
</style>