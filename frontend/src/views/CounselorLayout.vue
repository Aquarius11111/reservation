<template>
  <div class="counselor-layout">
    <!-- 侧边栏 -->
    <CounselorSidebar />
    
    <!-- 主内容区域 -->
    <div class="main-content">
     
      <!-- 页面内容 -->
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CounselorSidebar from '../components/CounselorSidebar.vue'

// 路由
const route = useRoute()
const router = useRouter()

// 响应式数据
const isUserDropdownOpen = ref(false)
const userDropdownRef = ref(null)
const notificationCount = ref(3)

// 用户信息
const userInfo = reactive({
  name: '李老师',
  avatar: null
})

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiM0Y2FmNTAiLz4KPGNpcmNsZSBjeD0iMjAiIGN5PSIxNiIgcj0iNiIgZmlsbD0id2hpdGUiLz4KPHBhdGggZD0iTTEyIDI4QzEyIDI0IDI4IDI0IDI4IDI4VjMySDEyVjI4WiIgZmlsbD0id2hpdGUiLz4KPC9zdmc+'

// 页面标题映射
const pageTitles = {
  '/counselor/dashboard': '工作台',
  '/counselor/appointments': '预约管理',
  '/counselor/schedule': '时间安排',
  '/counselor/students': '学生档案',
  '/counselor/reports': '报告管理',
  '/counselor/settings': '个人设置'
}

// 计算当前页面标题
const currentPageTitle = computed(() => {
  return pageTitles[route.path] || '咨询师端'
})

// 切换用户下拉菜单
const toggleUserDropdown = () => {
  isUserDropdownOpen.value = !isUserDropdownOpen.value
}

// 关闭用户下拉菜单
const closeUserDropdown = () => {
  isUserDropdownOpen.value = false
}

// 处理个人资料
const handleProfile = () => {
  closeUserDropdown()
  router.push('/counselor/profile')
}

// 处理设置
const handleSettings = () => {
  closeUserDropdown()
  router.push('/counselor/settings')
}

// 处理退出登录
const handleLogout = () => {
  closeUserDropdown()
  
  if (confirm('确定要退出登录吗？')) {
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 跳转到登录页面
    router.push('/login')
    alert('已退出登录')
  }
}

// 点击外部关闭下拉菜单
const handleClickOutside = (event) => {
  if (userDropdownRef.value && !userDropdownRef.value.contains(event.target)) {
    closeUserDropdown()
  }
}

// 生命周期
onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.counselor-layout {
  display: flex;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.main-content {
  flex: 1;
  margin-left: 250px;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 顶部导航栏 */
.top-header {
  background: white;
  padding: 0 30px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border-bottom: 1px solid #e9ecef;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-left {
  display: flex;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 通知按钮 */
.notification-btn {
  position: relative;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.notification-btn:hover {
  background-color: #f8f9fa;
}

.notification-icon {
  font-size: 1.2rem;
  color: #6c757d;
}

.notification-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  background: #dc3545;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

/* 用户下拉菜单 */
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
  background-color: #f8f9fa;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e9ecef;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 500;
  color: #2c3e50;
}

.dropdown-arrow {
  font-size: 0.8rem;
  transition: transform 0.2s ease;
  color: #6c757d;
}

.dropdown-arrow.rotated {
  transform: rotate(180deg);
}

.user-dropdown-menu {
  position: absolute;
  top: 100%;
  right: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  min-width: 160px;
  padding: 8px 0;
  margin-top: 4px;
  animation: dropdownAppear 0.2s ease-out;
  z-index: 1000;
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

/* 内容区域 */
.content-area {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    margin-left: 70px;
  }
  
  .top-header {
    padding: 0 20px;
    height: 56px;
  }
  
  .page-title {
    font-size: 1.3rem;
  }
  
  .content-area {
    padding: 20px;
  }
  
  .user-name {
    display: none;
  }
}
</style>
