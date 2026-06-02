<template>
  <div class="auth-page">
    <div class="page-mask"></div>

    <header class="auth-header">
      <button type="button" class="icon-btn" @click="router.back()">
        <van-icon name="cross" />
      </button>
      <span class="title">员工登录</span>
      <span></span>
    </header>

    <main class="auth-body">
      <section class="brand-block">
        <div class="brand-icon">S</div>
        <div class="brand-text">STAFF</div>
      </section>

      <form class="form-card" @submit.prevent="handleLogin">
        <div class="input-row">
          <van-icon name="manager-o" />
          <input v-model.trim="username" type="text" placeholder="请输入账号" autocomplete="username" />
        </div>

        <div class="input-row">
          <van-icon name="lock" />
          <input
            v-model.trim="password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
          <button class="eye-btn" type="button" @click="showPassword = !showPassword">
            <van-icon :name="showPassword ? 'eye-o' : 'closed-eye'" />
          </button>
        </div>

        <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>

        <button type="submit" class="submit-btn">登录</button>
      </form>
    </main>
  </div>
</template>

<script setup>
import { showNotify } from 'vant'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const errorMessage = ref('')

const handleLogin = async () => {
  if (!username.value || !password.value) {
    errorMessage.value = '请输入账号和密码'
    return
  }

  errorMessage.value = ''

  try {
    const response = await api.post('/Employeeaccount/login', {
      username: username.value,
      password: password.value
    })

    const data = response.data

    if (data.status === '200') {
      localStorage.setItem('username', data.Username)
      localStorage.setItem('management', data.Management)
      localStorage.setItem('accessToken', data.accessToken)
      localStorage.setItem('refreshToken', data.refreshToken)
      showNotify({ type: 'success', message: '登录成功' })
      router.push('/ehome')
    } else {
      showNotify({ type: 'danger', message: '登录失败: ' + (data.message || '账号或密码错误') })
    }
  } catch (error) {
    console.error('网络错误:', error)
    showNotify({ type: 'danger', message: '网络错误，请稍后重试' })
  }
}
</script>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  padding: 1.4rem 1.1rem 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: linear-gradient(180deg, #4d4b61 0%, #20202b 42%, #0f0e14 100%);
}

.page-mask {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 50% 15%, rgba(255, 255, 255, 0.16), transparent 30%),
    linear-gradient(180deg, rgba(0, 0, 0, 0.18), rgba(0, 0, 0, 0.58));
  pointer-events: none;
}

.auth-header,
.auth-body {
  position: relative;
  z-index: 1;
}

.auth-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.75rem;
  height: 2.75rem;
  padding: 0;
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  line-height: 1;
}

.icon-btn .van-icon {
  font-size: 1.35rem;
  line-height: 1;
}

.title {
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
}

.auth-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.65rem;
  margin-bottom: 2rem;
}

.brand-icon {
  width: 4.5rem;
  height: 4.5rem;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  display: grid;
  place-items: center;
  font-size: 1.8rem;
  letter-spacing: 0.14em;
  text-shadow: 0.125rem 0.25rem 0.25rem rgba(0, 0, 0, 0.5);
    padding-left: 0.25rem;
}

.brand-text {
  color: #fff;
  font-size: 1.35rem;
  letter-spacing: 0.05em;
}

.form-card {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.25rem;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 1.5rem;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(1.1rem);
}

.input-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  box-sizing: border-box;
  height: 3.25rem;
  padding: 0 1rem;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 1rem;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.88);
}

.input-row .van-icon {
  flex: none;
  font-size: 1.2rem;
}

.input-row input {
  flex: 1;
  min-width: 0;
  border: 0;
  outline: 0;
  background: transparent;
  color: #fff;
  font-size: 1rem;
}

.input-row input::placeholder {
  color: rgba(255, 255, 255, 0.45);
}

.eye-btn {
  display: grid;
  place-items: center;
  flex: none;
  width: 2rem;
  height: 2rem;
  margin-right: -0.25rem;
  padding: 0;
  border: 0;
  background: transparent;
  color: rgba(255, 255, 255, 0.72);
}

.error-text {
  margin: -0.25rem 0 0;
  color: #ffb3b3;
  font-size: 0.86rem;
}

.submit-btn {
  width: 100%;
  min-height: 3.4rem;
  border: 0;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.88);
  color: #1c1c1c;
  font-size: 1.08rem;
  font-weight: 700;
}

@media (max-width: 420px) {
  .auth-page {
    padding: 1.1rem 0.9rem 1.25rem;
  }

  .form-card {
    border-radius: 1.25rem;
    padding: 1rem;
    gap: 0.8rem;
  }

  .brand-icon {
    width: 4rem;
    height: 4rem;
    font-size: 1.6rem;
  }

  .brand-text {
    font-size: 1.2rem;
  }
}
</style>
