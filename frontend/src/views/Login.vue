<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1 class="login-title">心情e约</h1>
        <p class="login-subtitle">心理健康咨询预约平台</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        label-position="top"
      >
        <el-form-item label="用户ID" prop="userId">
          <el-input
            v-model="loginForm.userId"
            placeholder="请输入用户ID"
            size="large"
            clearable
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
            clearable
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>


      
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { request } from '../api/index.js'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

// 表单数据
const loginForm = reactive({
  userId: '',
  password: ''
})

// 页面加载时，如果有保存的userId则自动填充
onMounted(() => {
  const savedUserId = localStorage.getItem('userId')
  if (savedUserId) {
    loginForm.userId = savedUserId
    rememberMe.value = true
  }
})

// 表单验证规则
const loginRules = {
  userId: [
    { required: true, message: '请输入用户ID', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
  ]
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return

  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) {
    ElMessage.error('请完善登录信息')
    return
  }

  // 开始登录
  loading.value = true

  try {
    // 调用登录接口
    const response = await request('/api/login', {
      method: 'POST',
      data: {
        userId: loginForm.userId,
        password: loginForm.password
      }
    })

    loading.value = false

    if (response.success) {
      // 后端返回的数据结构: { code, message, data }
      const result = response.data
      
      if (result.code === 200) {
        ElMessage.success(result.message || '登录成功！')
        
        // 保存用户信息到localStorage
        if (result.data) {
          localStorage.setItem('userId', result.data.userId)
          localStorage.setItem('userName', result.data.userName)
          localStorage.setItem('userRole', result.data.userRole)
        }

        // 根据userRole跳转到不同页面
        // userRole = 1: 学生 -> / (home)
        // userRole = 2: 咨询师 -> /counselor/dashboard
        if (result.data && result.data.userRole === 2) {
          router.push('/counselor/dashboard')
        } else {
          router.push('/')
        }
      } else {
        ElMessage.error(result.message || '登录失败，请重试')
      }
    } else {
      ElMessage.error(response.error || '登录失败，请重试')
    }
  } catch (error) {
    loading.value = false
    ElMessage.error('登录失败，请检查网络连接')
    console.error('登录错误:', error)
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 450px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  padding: 48px 40px;
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.login-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #333;
  padding-bottom: 8px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.login-form :deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 1px #667eea inset;
}

.forgot-password {
  float: right;
  font-size: 14px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
  margin-top: 20px;
}

.login-button:hover {
  opacity: 0.9;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.login-footer {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid #eee;
}

.register-hint {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.register-hint .el-link {
  margin-left: 4px;
}
</style>

