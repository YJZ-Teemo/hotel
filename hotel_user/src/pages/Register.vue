<template>
  <div class="auth-page">
    <div class="page-mask"></div>

    <header class="auth-header">
      <button type="button" class="icon-btn" @click="router.back()">
        <van-icon name="arrow-left" />
      </button>
      <RouterLink class="login-link" to="/login">登录</RouterLink>
    </header>

    <main class="auth-body">
      <section class="brand-block">
        <div class="brand-icon">H</div>
        <div class="brand-text">HOTEL</div>
      </section>

      <form class="form-card" @submit.prevent="register">
        <div class="input-row">
          <van-icon name="manager-o" />
          <input v-model.trim="username" type="text" placeholder="请输入账号" autocomplete="username" />
        </div>

        <div class="input-row">
          <van-icon name="phone-o" />
          <input v-model.trim="phone" type="tel" placeholder="请输入手机号" autocomplete="tel" />
        </div>

        <div class="input-row">
          <van-icon name="lock" />
          <input
            v-model.trim="password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            autocomplete="new-password"
          />
          <button class="eye-btn" type="button" @click="showPassword = !showPassword">
            <van-icon :name="showPassword ? 'eye-o' : 'closed-eye'" />
          </button>
        </div>

        <div class="input-row">
          <van-icon name="passed" />
          <input
            v-model.trim="confirmPassword"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请确认密码"
            autocomplete="new-password"
          />
        </div>

        <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>

        <button type="submit" class="submit-btn">注册</button>

        <div class="extra-links">
          <span>已有账号？</span>
          <RouterLink to="/login">立即登录</RouterLink>
        </div>
      </form>
    </main>
  </div>
</template>

<script setup>
import { showNotify } from 'vant'
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const username = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const errorMessage = ref('')

const register = async () => {
  if (!username.value || !phone.value || !password.value || !confirmPassword.value) {
    errorMessage.value = '请填写完整注册信息'
    return
  }

  if (password.value !== confirmPassword.value) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }

  errorMessage.value = ''

  try {
    const response = await api.post('/Customer/register', {
      name: username.value,
      phone: phone.value,
      password: password.value
    })

    const data = response.data

    if (data.status === '200') {
      showNotify({ type: 'success', message: '注册成功' })
      router.push('/login')
    } else {
      showNotify({ type: 'danger', message: '注册失败: ' + (data.message || '请稍后重试') })
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

.login-link {
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
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

.extra-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.4rem;
  color: rgba(255, 255, 255, 0.85);
  font-size: 0.92rem;
}

.extra-links a {
  color: #fff;
  font-weight: 600;
  text-decoration: none;
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
