import { createApp } from 'vue'
import './style.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import api from './api'

const app = createApp(App)
app.use(ElementPlus).use(router)

app.config.globalProperties.$api = api

app.mount('#app')
