<template>
  <div class="sidebar" :class="{ 'collapsed': isCollapsed }">
    <!-- 侧边栏头部 -->
    <div class="sidebar-header">
      <div class="logo" v-if="!isCollapsed">
        <div class="logo-icon">👨‍⚕️</div>
        <span class="logo-text">咨询师端</span>
      </div>
      <div class="logo-collapsed" v-else>
        <div class="logo-icon">👨‍⚕️</div>
      </div>
      <button @click="toggleCollapse" class="collapse-btn">
        <span v-if="isCollapsed">▶</span>
        <span v-else>◀</span>
      </button>
    </div>

    <!-- 导航菜单 -->
    <nav class="sidebar-nav">
      <ul class="nav-list">
        <li 
          v-for="item in menuItems" 
          :key="item.path"
          class="nav-item"
        >
          <router-link 
            :to="item.path" 
            class="nav-link"
            :class="{ 'active': $route.path === item.path }"
          >
            <div class="nav-icon">{{ item.icon }}</div>
            <span class="nav-text" v-if="!isCollapsed">{{ item.name }}</span>
            <div class="nav-tooltip" v-if="isCollapsed">{{ item.name }}</div>
          </router-link>
        </li>
      </ul>
    </nav>

    <!-- 侧边栏底部 -->
    <div class="sidebar-footer">
      <div class="user-info" v-if="!isCollapsed">
        <div class="user-avatar">
          <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name" />
        </div>
        <div class="user-details">
          <div class="user-name">{{ userInfo.name || '咨询师' }}</div>
          <div class="user-role">{{ userInfo.role || '心理咨询师' }}</div>
        </div>
      </div>
      <div class="user-avatar-collapsed" v-else>
        <img :src="userInfo.avatar || defaultAvatar" :alt="userInfo.name" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

// 侧边栏折叠状态
const isCollapsed = ref(false)

// 用户信息
const userInfo = reactive({
  name: '李老师',
  role: '心理咨询师',
  avatar: null
})

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiM0Y2FmNTAiLz4KPGNpcmNsZSBjeD0iMjAiIGN5PSIxNiIgcj0iNiIgZmlsbD0id2hpdGUiLz4KPHBhdGggZD0iTTEyIDI4QzEyIDI0IDI4IDI0IDI4IDI4VjMySDEyVjI4WiIgZmlsbD0id2hpdGUiLz4KPC9zdmc+'

// 菜单项
const menuItems = [
  {
    path: '/counselor/dashboard',
    name: '工作台',
    icon: '📊'
  },
  {
    path: '/counselor/appointments',
    name: '预约管理',
    icon: '📅'
  },
  {
    path: '/counselor/schedule',
    name: '时间安排',
    icon: '⏰'
  },
  {
    path: '/counselor/reports',
    name: '报告管理',
    icon: '📋'
  },
  {
    path: '/counselor/settings',
    name: '个人设置',
    icon: '⚙️'
  }
]

// 切换折叠状态
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 处理头像加载错误
const handleAvatarError = (event) => {
  event.target.src = defaultAvatar
}
</script>

<style scoped>
.sidebar {
  width: 250px;
  height: 100vh;
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  color: white;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  transition: width 0.3s ease;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.sidebar.collapsed {
  width: 70px;
}

/* 侧边栏头部 */
.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-icon {
  font-size: 2rem;
}

.logo-text {
  font-size: 1.2rem;
  font-weight: 700;
  color: white;
}

.logo-collapsed {
  display: flex;
  justify-content: center;
}

.logo-collapsed .logo-icon {
  font-size: 2rem;
}

.collapse-btn {
  position: absolute;
  top: 20px;
  right: -12px;
  width: 24px;
  height: 24px;
  background: #3498db;
  border: none;
  border-radius: 50%;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.collapse-btn:hover {
  background: #2980b9;
  transform: scale(1.1);
}

/* 导航菜单 */
.sidebar-nav {
  flex: 1;
  padding: 20px 0;
}

.nav-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-item {
  margin-bottom: 4px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: all 0.2s ease;
  position: relative;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-link.active {
  background-color: #3498db;
  color: white;
}

.nav-link.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: #2ecc71;
}

.nav-icon {
  font-size: 1.2rem;
  min-width: 20px;
  text-align: center;
}

.nav-text {
  font-size: 0.95rem;
  font-weight: 500;
}

.nav-tooltip {
  position: absolute;
  left: 80px;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 0.85rem;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s ease;
  z-index: 1001;
}

.collapsed .nav-link:hover .nav-tooltip {
  opacity: 1;
}

/* 侧边栏底部 */
.sidebar-footer {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: white;
  margin-bottom: 2px;
}

.user-role {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.7);
}

.user-avatar-collapsed {
  display: flex;
  justify-content: center;
}

.user-avatar-collapsed img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255, 255, 255, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 70px;
  }
  
  .sidebar:not(.collapsed) {
    width: 250px;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .sidebar.show {
    transform: translateX(0);
  }
}
</style>
