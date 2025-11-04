<template>
  <header class="app-header">
    <div class="header-container">
      <!-- 左侧标题 -->
      <div class="header-left">
        <h1 class="app-title">心理预约系统</h1>
      </div>

      <!-- 右侧用户区域 -->
      <div class="header-right">
        <div class="user-dropdown" @click="toggleDropdown" ref="dropdownRef">
          <!-- 用户头像 -->
          <div class="user-avatar">
            <img 
              :src="userInfo.avatar || defaultAvatar" 
              :alt="userInfo.name || '用户'"
              @error="handleAvatarError"
            />
          </div>
          
          <!-- 用户名 -->
          <span class="user-name">{{ userInfo.name || '用户' }}</span>
          
          <!-- 下拉箭头 -->
          <div class="dropdown-arrow" :class="{ 'rotated': isDropdownOpen }">
            ▼
          </div>
        </div>

        <!-- 下拉菜单 -->
        <div v-if="isDropdownOpen" class="dropdown-menu">
          <div class="dropdown-item" @click="handleChangePassword">
            <div class="item-icon">🔒</div>
            <span>修改密码</span>
          </div>
          <div class="dropdown-divider"></div>
          <div class="dropdown-item" @click="handleLogout">
            <div class="item-icon">🚪</div>
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <div v-if="showChangePasswordDialog" class="dialog-overlay" @click="closeChangePasswordDialog">
      <div class="dialog" @click.stop>
        <div class="dialog-header">
          <h3 class="dialog-title">修改密码</h3>
          <button @click="closeChangePasswordDialog" class="close-button">×</button>
        </div>
        <div class="dialog-content">
          <div class="form-group">
            <label for="currentPassword">当前密码：</label>
            <input 
              type="password" 
              id="currentPassword"
              v-model="passwordForm.currentPassword"
              class="form-input"
              placeholder="请输入当前密码"
            />
          </div>
          <div class="form-group">
            <label for="newPassword">新密码：</label>
            <input 
              type="password" 
              id="newPassword"
              v-model="passwordForm.newPassword"
              class="form-input"
              placeholder="请输入新密码"
            />
          </div>
          <div class="form-group">
            <label for="confirmPassword">确认密码：</label>
            <input 
              type="password" 
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              class="form-input"
              placeholder="请再次输入新密码"
            />
          </div>
        </div>
        <div class="dialog-actions">
          <button @click="closeChangePasswordDialog" class="cancel-btn">
            取消
          </button>
          <button @click="confirmChangePassword" class="confirm-btn" :disabled="changingPassword">
            {{ changingPassword ? '修改中...' : '确认修改' }}
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { systemAPI } from '../api/index.js'

// 路由
const router = useRouter()

// 响应式数据
const isDropdownOpen = ref(false)
const showChangePasswordDialog = ref(false)
const changingPassword = ref(false)
const dropdownRef = ref(null)

// 用户信息
const userInfo = reactive({
  name: '张三',
  avatar: null
})

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiM2NjdlZWEiLz4KPGNpcmNsZSBjeD0iMjAiIGN5PSIxNiIgcj0iNiIgZmlsbD0id2hpdGUiLz4KPHBhdGggZD0iTTEyIDI4QzEyIDI0IDI4IDI0IDI4IDI4VjMySDEyVjI4WiIgZmlsbD0id2hpdGUiLz4KPC9zdmc+'

// 密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 切换下拉菜单
const toggleDropdown = () => {
  isDropdownOpen.value = !isDropdownOpen.value
}

// 关闭下拉菜单
const closeDropdown = () => {
  isDropdownOpen.value = false
}

// 处理修改密码
const handleChangePassword = () => {
  closeDropdown()
  showChangePasswordDialog.value = true
  // 重置表单
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

// 关闭修改密码对话框
const closeChangePasswordDialog = () => {
  showChangePasswordDialog.value = false
  changingPassword.value = false
}

// 确认修改密码
const confirmChangePassword = async () => {
  // 验证表单
  if (!passwordForm.currentPassword) {
    ElMessage.warning('请输入当前密码')
    return
  }
  
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }

  try {
    changingPassword.value = true
    
    // 从localStorage获取userId
    const userInfoStr = localStorage.getItem('userInfo')
    if (!userInfoStr) {
      ElMessage.error('未找到用户信息，请重新登录')
      return
    }

    let userInfo
    try {
      userInfo = JSON.parse(userInfoStr)
    } catch (e) {
      ElMessage.error('用户信息格式错误，请重新登录')
      return
    }

    const userId = userInfo.userId
    if (!userId) {
      ElMessage.error('未找到用户ID，请重新登录')
      return
    }

    // 调用修改密码API（使用query参数）
    const requestParams = {
      userId: userId,
      oldPassword: passwordForm.currentPassword,
      newPassword: passwordForm.newPassword
    }

    const response = await systemAPI.changePassword(requestParams)
    
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        ElMessage.success(apiData.data || '密码修改成功')
        closeChangePasswordDialog()
      } else {
        ElMessage.error(apiData.message || '密码修改失败')
      }
    } else {
      ElMessage.error(response.error || '密码修改失败，请重试')
    }
  } catch (error) {
    ElMessage.error('密码修改失败，请重试')
    console.error('修改密码失败:', error)
  } finally {
    changingPassword.value = false
  }
}

// 处理退出登录
const handleLogout = async () => {
  closeDropdown()
  
  try {
    await ElMessageBox.confirm(
      '确定要退出登录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: false
      }
    )
    
    // 这里调用退出登录的API
    // await userAPI.logout()
    
    // 清除本地存储的用户信息（统一清除对象）
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 跳转到登录页面
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 用户点击取消，不做任何操作
  }
}

// 处理头像加载错误
const handleAvatarError = (event) => {
  event.target.src = defaultAvatar
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    closeDropdown()
  }
}

// 生命周期
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
  
  // 从本地存储获取用户信息（统一从对象中读取）
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const userInfoData = JSON.parse(userInfoStr)
      if (userInfoData.userName) {
        userInfo.name = userInfoData.userName
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
}

.app-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
}

.header-right {
  position: relative;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.user-dropdown:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 500;
  color: white;
}

.dropdown-arrow {
  font-size: 0.8rem;
  transition: transform 0.2s ease;
  color: rgba(255, 255, 255, 0.8);
}

.dropdown-arrow.rotated {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  min-width: 160px;
  padding: 8px 0;
  margin-top: 4px;
  animation: dropdownAppear 0.2s ease-out;
}

@keyframes dropdownAppear {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  color: #333;
  font-size: 0.95rem;
}

.dropdown-item:hover {
  background-color: #f8f9fa;
}

.item-icon {
  font-size: 1.1rem;
}

.dropdown-divider {
  height: 1px;
  background-color: #e9ecef;
  margin: 4px 0;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(3px);
}

.dialog {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  width: 90%;
  overflow: hidden;
  animation: dialogAppear 0.3s ease-out;
}

@keyframes dialogAppear {
  from {
    opacity: 0;
    transform: scale(0.8) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e9ecef;
  background: #f8f9fa;
}

.dialog-title {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6c757d;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-button:hover {
  background: #e9ecef;
  color: #495057;
}

.dialog-content {
  padding: 25px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #495057;
  font-size: 0.95rem;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 25px;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

.cancel-btn {
  padding: 10px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s ease;
}

.cancel-btn:hover {
  background: #5a6268;
}

.confirm-btn {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s ease;
  min-width: 100px;
}

.confirm-btn:hover:not(:disabled) {
  background: #5a6fd8;
}

.confirm-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
  opacity: 0.7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-container {
    padding: 0 15px;
    height: 56px;
  }
  
  .app-title {
    font-size: 1.3rem;
  }
  
  .user-name {
    display: none;
  }
  
  .dropdown-menu {
    right: -10px;
    min-width: 140px;
  }
  
  .dialog {
    width: 95%;
    margin: 20px;
  }
  
  .dialog-header,
  .dialog-content,
  .dialog-actions {
    padding: 15px 20px;
  }
}
</style>
