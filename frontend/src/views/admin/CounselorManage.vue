<template>
  <div class="counselor-manage">
    <div class="page-header">
      <h1>咨询师管理</h1>
      <button class="btn-primary" @click="showAddDialog = true">
        <span>➕</span> 添加咨询师
      </button>
    </div>
    
    <div class="page-content">
      <div class="info-card">
        <p>咨询师管理功能正在开发中...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const showAddDialog = ref(false)
</script>

<style scoped>
.counselor-manage {
  width: 100%;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 1.8rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #8e44ad;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background: #9b59b6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(142, 68, 173, 0.3);
}

.page-content {
  width: 100%;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.info-card p {
  font-size: 1rem;
  color: #6c757d;
  margin: 0;
}
</style>
