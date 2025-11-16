import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

const app = createApp(App)

// 开发环境下每次启动清空本地登录状态，避免“自动登录”
if (import.meta.env.DEV) {
  try {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  } catch (_) {
    // 忽略本地存储不可用的情况
  }
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
