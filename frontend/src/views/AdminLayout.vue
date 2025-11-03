<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <AdminSidebar />
    
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
import AdminSidebar from '../components/AdminSidebar.vue'
</script>

<style scoped>
.admin-layout {
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
  transition: margin-left 0.3s ease;
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
  
  .content-area {
    padding: 20px;
  }
}
</style>
