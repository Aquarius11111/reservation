<template>
  <div class="appointments">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">预约管理</h1>
      <div class="header-actions">
        <button @click="refreshData" class="refresh-btn">
          <span class="btn-icon">🔄</span>
          刷新
        </button>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filters-section">
      <div class="filter-group">
        <label>状态筛选：</label>
        <select v-model="statusFilter" class="filter-select" @change="handleFilterChange">
          <option value="">全部</option>
          <option value="1">已预约</option>
          <option value="2">已取消</option>
          <option value="3">已完成</option>
          <option value="4">已爽约</option>
        </select>
      </div>
      
      <div class="filter-group">
        <label>日期筛选：</label>
        <el-date-picker
          v-model="dateFilter"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 180px"
          @change="handleFilterChange"
        />
      </div>
      
    </div>

    <!-- 预约列表 -->
    <div class="appointments-list">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner">⏳</div>
        <p>正在加载预约数据...</p>
      </div>
      
      <!-- 错误状态 -->
      <div v-else-if="error" class="error-state">
        <div class="error-icon">❌</div>
        <h3>加载失败</h3>
        <p>{{ error }}</p>
        <button @click="loadAppointments" class="retry-btn">重试</button>
      </div>
      
      <!-- 空状态 -->
      <div v-else-if="filteredAppointments.length === 0" class="empty-state">
        <div class="empty-icon">📅</div>
        <h3>暂无预约记录</h3>
        <p>当前筛选条件下没有找到预约记录</p>
      </div>
      
      <div v-else class="appointment-cards">
        <div 
          v-for="appointment in filteredAppointments" 
          :key="appointment.reserveId"
          class="appointment-card"
        >
          <div class="card-header">
            <div class="appointment-time">
              <div class="date">{{ appointment.reserveDate }}</div>
              <div class="time">{{ appointment.reserveTime }}</div>
            </div>
            <div class="appointment-status" :class="getStatusClass(appointment.reserveStatus)">
              {{ getStatusText(appointment.reserveStatus) }}
            </div>
          </div>
          
          <div class="card-content">
            <div class="student-info">
              <div class="student-avatar">
                <img :src="defaultAvatar" :alt="appointment.studentName" />
              </div>
              <div class="student-details">
                <h3 class="student-name">{{ appointment.studentName }}</h3>
                <p class="student-id">学号：{{ appointment.studentId }}</p>
              </div>
            </div>
            
            <div class="appointment-details">
              <div class="detail-item">
                <span class="label">学院：</span>
                <span class="value">{{ appointment.studentCollege }}</span>
              </div>
              <div class="detail-item">
                <span class="label">咨询主题：</span>
                <span class="value">{{ appointment.consultTopic }}</span>
              </div>
              <div class="detail-item">
                <span class="label">创建时间：</span>
                <span class="value">{{ appointment.createTime }}</span>
              </div>
            </div>
          </div>
          
          <div class="card-actions" v-if="canOperate(appointment.reserveStatus)">
            <button 
              @click="showCompleteDialog(appointment.reserveId)" 
              class="action-btn complete-btn"
              v-if="appointment.reserveStatus === 1"
            >
              完成咨询
            </button>
            <button 
              @click="showMarkNoShowDialog(appointment.reserveId)" 
              class="action-btn no-show-btn"
              v-if="appointment.reserveStatus === 1"
            >
              标记爽约
            </button>
            <button 
              @click="showCancelDialog(appointment.reserveId)" 
              class="action-btn cancel-btn"
            >
              取消预约
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
      <div class="pagination-info">
        <span>共 {{ total }} 条记录，第 {{ currentPage }} / {{ totalPages }} 页</span>
      </div>
      
      <div class="pagination-controls">
        <button 
          @click="goToPage(currentPage - 1)" 
          :disabled="currentPage === 1"
          class="page-btn prev-btn"
        >
          上一页
        </button>
        
        <div class="page-numbers">
          <button 
            v-for="page in visiblePages" 
            :key="page"
            @click="goToPage(page)"
            :class="['page-btn', { 'active': page === currentPage }]"
          >
            {{ page }}
          </button>
        </div>
        
        <button 
          @click="goToPage(currentPage + 1)" 
          :disabled="currentPage === totalPages"
          class="page-btn next-btn"
        >
          下一页
        </button>
      </div>
    </div>
  </div>

  <!-- 确认对话框 -->
  <div v-if="showConfirmDialog" class="confirm-dialog-overlay" @click="closeConfirmDialog">
    <div class="confirm-dialog" @click.stop>
      <div class="dialog-header">
        <h3 class="dialog-title">{{ dialogConfig.title }}</h3>
        <button @click="closeConfirmDialog" class="close-button">×</button>
      </div>
      <div class="dialog-content">
        <div class="dialog-icon">{{ dialogConfig.icon }}</div>
        <p class="dialog-message">{{ dialogConfig.message }}</p>
      </div>
      <div class="dialog-actions">
        <button @click="closeConfirmDialog" class="cancel-btn">
          取消
        </button>
        <button @click="confirmAction" class="confirm-btn" :disabled="processing">
          {{ processing ? '处理中...' : dialogConfig.confirmText }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { reservationAPI } from '../../api/index.js'
// 响应式数据
const statusFilter = ref('')
const dateFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const error = ref(null)

// 咨询师ID（实际应用中应该从用户登录状态获取）
const counselorId = ref('11001')

// 对话框相关
const showConfirmDialog = ref(false)
const processing = ref(false)
const currentReserveId = ref(null)
const dialogConfig = reactive({
  title: '',
  icon: '',
  message: '',
  confirmText: '',
  action: null
})

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAiIGhlaWdodD0iNDAiIHZpZXdCb3g9IjAgMCA0MCA0MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPGNpcmNsZSBjeD0iMjAiIGN5PSIyMCIgcj0iMjAiIGZpbGw9IiM2NjdlZWEiLz4KPGNpcmNsZSBjeD0iMjAiIGN5PSIxNiIgcj0iNiIgZmlsbD0id2hpdGUiLz4KPHBhdGggZD0iTTEyIDI4QzEyIDI0IDI4IDI0IDI4IDI4VjMySDEyVjI4WiIgZmlsbD0id2hpdGUiLz4KPC9zdmc+'

// 预约数据
const appointments = ref([])
const total = ref(0)

// 预约列表（直接使用API返回的数据）
const filteredAppointments = computed(() => {
  return appointments.value
})

// 分页相关
const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value)
})

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 获取状态样式类
const getStatusClass = (status) => {
  const statusMap = {
    1: 'reserved',   // 已预约
    2: 'cancelled',  // 已取消
    3: 'completed',  // 已完成
    4: 'no-show'     // 已爽约
  }
  return statusMap[status] || 'reserved'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '已预约',
    2: '已取消',
    3: '已完成',
    4: '已爽约'
  }
  return statusMap[status] || '未知状态'
}

// 获取今日日期字符串（YYYY-MM-DD）
const getTodayDateString = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 判断是否可以操作
const canOperate = (status) => {
  return status === 1  // 只有已预约状态可以操作
}

// 显示完成咨询对话框
const showCompleteDialog = (id) => {
  currentReserveId.value = id
  dialogConfig.title = '完成咨询'
  dialogConfig.message = '确定要标记该咨询为已完成吗？'
  dialogConfig.confirmText = '确认完成'
  dialogConfig.action = 'complete'
  showConfirmDialog.value = true
}

// 显示标记爽约对话框
const showMarkNoShowDialog = (id) => {
  currentReserveId.value = id
  dialogConfig.title = '标记爽约'
  dialogConfig.message = '确定要标记该学生为爽约吗？'
  dialogConfig.confirmText = '确认标记'
  dialogConfig.action = 'noShow'
  showConfirmDialog.value = true
}

// 显示取消预约对话框
const showCancelDialog = (id) => {
  currentReserveId.value = id
  dialogConfig.title = '取消预约'
  dialogConfig.message = '确定要取消这个预约吗？'
  dialogConfig.confirmText = '确认取消'
  dialogConfig.action = 'cancel'
  showConfirmDialog.value = true
}

// 关闭确认对话框
const closeConfirmDialog = () => {
  showConfirmDialog.value = false
  processing.value = false
  currentReserveId.value = null
}

// 确认操作
const confirmAction = async () => {
  if (!currentReserveId.value) return

  try {
    processing.value = true

    switch (dialogConfig.action) {
      case 'complete':
        await completeAppointment(currentReserveId.value)
        break
      case 'noShow':
        await markNoShow(currentReserveId.value)
        break
      case 'cancel':
        await cancelAppointment(currentReserveId.value)
        break
    }

    closeConfirmDialog()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 完成咨询
const completeAppointment = async (id) => {
  try {
    const completeData = {
      reserveId: id,
      counselorId: counselorId.value
    }

    const response = await reservationAPI.markCompleted(completeData)

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        ElMessage.success(apiData.message || '咨询已标记为完成')
        // 重新加载数据以确保同步
        await loadAppointments()
      } else {
        ElMessage.error(apiData.message || apiData.msg || '标记完成失败')
      }
    } else {
      ElMessage.error(response.error || '标记完成失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('标记完成失败:', error)
  }
}

// 标记爽约
const markNoShow = async (id) => {
  try {
    const noShowData = {
      reserveId: id,
      counselorId: counselorId.value
    }

    const response = await reservationAPI.markNoShow(noShowData)

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        ElMessage.success(apiData.message || '已标记为爽约')
        // 重新加载数据以确保同步
        await loadAppointments()
      } else {
        ElMessage.error(apiData.message || apiData.msg || '标记爽约失败')
      }
    } else {
      ElMessage.error(response.error || '标记爽约失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('标记爽约失败:', error)
  }
}

// 取消预约
const cancelAppointment = async (id) => {
  try {
    const cancelData = {
      reserveId: id,
      cancelReason: null // 咨询师取消可以不填写原因
    }

    const response = await reservationAPI.cancelReservation(cancelData)

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        ElMessage.success('预约已成功取消')
        // 重新加载数据以确保同步
        await loadAppointments()
      } else {
        ElMessage.error(apiData.message || apiData.msg || '取消预约失败')
      }
    } else {
      ElMessage.error(response.error || '取消预约失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('取消预约失败:', error)
  }
}

// 加载预约数据
const loadAppointments = async () => {
  try {
    loading.value = true
    error.value = null

    const params = {
      counselorId: counselorId.value
    }

    // 添加分页参数（可选）
    if (currentPage.value && currentPage.value > 1) {
      params.pageNum = currentPage.value
    }
    if (pageSize.value && pageSize.value !== 10) {
      params.pageSize = pageSize.value
    }

    // 添加筛选参数
    if (dateFilter.value) {
      params.reserveDate = dateFilter.value
    }
    if (statusFilter.value) {
      params.reserveStatus = statusFilter.value
    }

    const response = await reservationAPI.getCounselorReservationList(params)

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        appointments.value = apiData.data.list || []
        total.value = apiData.data.total || 0
      } else {
        error.value = apiData.message || apiData.msg || '加载预约数据失败'
      }
    } else {
      error.value = response.error || '加载预约数据失败'
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试'
    console.error('加载预约数据失败:', err)
  } finally {
    loading.value = false
  }
}

// 刷新数据
const refreshData = () => {
  loadAppointments()
}

// 分页操作
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadAppointments()
  }
}

// 监听筛选条件变化
const handleFilterChange = () => {
  currentPage.value = 1 // 重置到第一页
  loadAppointments()
}

onMounted(() => {
  // 默认将日期筛选设为今天
  dateFilter.value = getTodayDateString()
  // 初始加载数据
  loadAppointments()
})
</script>

<style scoped>
.appointments {
  max-width: 1200px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s ease;
}

.refresh-btn:hover {
  background: #5a6fd8;
}

.btn-icon {
  font-size: 0.9rem;
}

/* 筛选区域 */
.filters-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  margin-bottom: 30px;
  display: flex;
  gap: 20px;
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  font-weight: 500;
  color: #495057;
  font-size: 0.9rem;
}

.filter-select,
.filter-input {
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease;
}

.filter-select:focus,
.filter-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

/* 预约列表 */
.appointments-list {
  margin-bottom: 30px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin-bottom: 10px;
  color: #495057;
}

.appointment-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.appointment-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  overflow: hidden;
  transition: transform 0.2s ease;
}

.appointment-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.appointment-time {
  text-align: left;
}

.date {
  font-size: 0.9rem;
  color: #6c757d;
  margin-bottom: 4px;
}

.time {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
}

.appointment-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.appointment-status.reserved {
  background: #d4edda;
  color: #155724;
}

.appointment-status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

.appointment-status.completed {
  background: #d1ecf1;
  color: #0c5460;
}

.appointment-status.no-show {
  background: #fff3cd;
  color: #856404;
}

.card-content {
  padding: 20px;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.student-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #e9ecef;
}

.student-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.student-details {
  flex: 1;
}

.student-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.student-id {
  font-size: 0.9rem;
  color: #6c757d;
  margin: 0;
}

.appointment-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  gap: 8px;
}

.label {
  font-weight: 500;
  color: #495057;
  min-width: 80px;
}

.value {
  color: #6c757d;
  flex: 1;
}

.card-actions {
  padding: 20px;
  border-top: 1px solid #e9ecef;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s ease;
}

.no-show-btn {
  background: #ffc107;
  color: #212529;
}

.no-show-btn:hover {
  background: #e0a800;
}

.complete-btn {
  background: #17a2b8;
  color: white;
}

.complete-btn:hover {
  background: #138496;
}

.cancel-btn {
  background: #dc3545;
  color: white;
}

.cancel-btn:hover {
  background: #c82333;
}

/* 分页 */
.pagination {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-top: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.pagination-info {
  font-size: 0.9rem;
  color: #6c757d;
  font-weight: 500;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-btn {
  padding: 8px 12px;
  border: 1px solid #ced4da;
  background: white;
  color: #495057;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
  font-weight: 500;
}

.page-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #adb5bd;
  transform: translateY(-1px);
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.page-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.page-btn.active:hover {
  background: #5a6fd8;
  border-color: #5a6fd8;
}

.prev-btn, .next-btn {
  min-width: 70px;
  font-weight: 600;
}

.page-numbers {
  display: flex;
  gap: 4px;
  margin: 0 8px;
}

.page-numbers .page-btn {
  min-width: 36px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filters-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .appointment-cards {
    grid-template-columns: 1fr;
  }
  
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .pagination-controls {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .page-numbers {
    margin: 8px 0;
  }
  
  .pagination-info {
    text-align: center;
    font-size: 0.8rem;
  }
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.loading-spinner {
  font-size: 3rem;
  margin-bottom: 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 60px 20px;
  color: #dc3545;
}

.error-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.error-state h3 {
  margin-bottom: 10px;
  color: #dc3545;
}

.retry-btn {
  margin-top: 20px;
  padding: 10px 20px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s ease;
}

.retry-btn:hover {
  background: #c82333;
}

/* 确认对话框样式 */
.confirm-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
}

.confirm-dialog {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  width: 90%;
  max-height: 90vh;
  overflow: hidden;
  animation: dialogAppear 0.3s ease-out;
}

@keyframes dialogAppear {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.dialog-header {
  padding: 20px 24px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6c757d;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.close-button:hover {
  background: #f8f9fa;
  color: #495057;
}

.dialog-content {
  padding: 20px 24px;
  text-align: center;
}


.dialog-message {
  font-size: 1.2rem;
  color: #495057;
  padding:10px 0;
  margin: 0;
  line-height: 1.5;
}

.dialog-actions {
  padding: 0 24px 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.dialog-actions .cancel-btn,
.dialog-actions .confirm-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s ease;
  min-width: 80px;
}

.dialog-actions .cancel-btn {
  background: #f8f9fa;
  color: #6c757d;
  border: 1px solid #e9ecef;
}

.dialog-actions .cancel-btn:hover {
  background: #e9ecef;
  color: #495057;
}

.dialog-actions .confirm-btn {
  background: #dc3545;
  color: white;
}

.dialog-actions .confirm-btn:hover:not(:disabled) {
  background: #c82333;
}

.dialog-actions .confirm-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>