<template>
  <div class="reservation-list-container">
    <div class="header">
      <h1 class="page-title">我的预约记录</h1>
      <p class="page-description">查看和管理您的所有预约记录</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>正在加载预约记录...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <div class="error-icon">⚠️</div>
      <h3>加载失败</h3>
      <p>{{ error }}</p>
      <button @click="loadReservations" class="retry-button">重试</button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="reservations.length === 0" class="empty-container">
      <div class="empty-icon">📅</div>
      <h3>暂无预约记录</h3>
      <p>您还没有任何预约记录，快去预约心理咨询师吧！</p>
      <router-link to="/reservation" class="reserve-button">
        立即预约
      </router-link>
    </div>

    <!-- 预约列表 -->
    <div v-else class="reservations-container">
      <div class="reservation-card" v-for="reservation in reservations" :key="reservation.id">
        <div class="reservation-header">
          <div class="time-info">
            <div class="time-details">
              <h3 class="reservation-time">预约时间：{{ reservation.reserveDate }} {{ reservation.reserveTime }}</h3>
            </div>
          </div>
          <div class="status-badge" :class="getStatusClass(reservation.reserveStatus)">
            {{ getStatusText(reservation.reserveStatus) }}
          </div>
        </div>

        <div class="reservation-details">
          <div class="detail-item">
            <span class="detail-label">咨询师姓名：</span>
            <span class="detail-value">{{ reservation.counselorName || '咨询师' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">咨询主题：</span>
            <span class="detail-value">{{ reservation.consultTopic || '心理健康咨询' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">咨询地点：</span>
            <span class="detail-value">{{ reservation.consultRoom || '待确认' }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">创建时间：</span>
            <span class="detail-value">{{ formatDateTime(reservation.createTime) }}</span>
          </div>
        </div>

        <div class="reservation-actions" v-if="canCancel(reservation.reserveStatus)">
          <button @click="showCancelDialog(reservation.reserveId)" class="cancel-button">
            取消预约
          </button>
        </div>
      </div>
    </div>

    <!-- 取消预约对话框 -->
    <div v-if="showCancelDialogFlag" class="cancel-dialog-overlay" @click="closeCancelDialog">
      <div class="cancel-dialog" @click.stop>
        <div class="dialog-header">
          <h3 class="dialog-title">取消预约</h3>
          <button @click="closeCancelDialog" class="close-button">×</button>
        </div>
        <div class="dialog-content">
          <p class="dialog-message">您确定要取消这个预约吗？</p>
          <div class="cancel-reason-section">
            <label for="cancelReason" class="reason-label">取消原因（可选）：</label>
            <textarea 
              id="cancelReason"
              v-model="cancelReason"
              class="reason-textarea"
              placeholder="请输入取消原因..."
              rows="3"
            ></textarea>
          </div>
        </div>
        <div class="dialog-actions">
          <button @click="closeCancelDialog" class="cancel-btn">
            取消
          </button>
          <button @click="confirmCancel" class="confirm-btn" :disabled="cancelling">
            {{ cancelling ? '提交中...' : '确认取消' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 返回首页按钮 -->
    <div class="back-container">
      <router-link to="/home" class="back-button">
        ← 返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { reservationAPI } from '../api/index.js'

// 响应式数据
const loading = ref(true)
const error = ref(null)
const reservations = ref([])

// 学生ID（从本地登录信息获取）
const studentId = ref('')

// 取消预约对话框相关
const showCancelDialogFlag = ref(false)
const cancelReason = ref('')
const currentReservationId = ref(null)
const cancelling = ref(false)

// 加载预约记录
const loadReservations = async () => {
  try {
    loading.value = true
    error.value = null
    
    const response = await reservationAPI.getReservationList(studentId.value)
    
    if (response.success) {
      // 根据API响应格式，预约列表在 response.data.data.list 中
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        reservations.value = apiData.data.list || []
      } else {
        error.value = apiData.msg || '加载预约记录失败'
      }
    } else {
      error.value = response.error || '加载预约记录失败'
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试'
    console.error('加载预约记录失败:', err)
  } finally {
    loading.value = false
  }
}

// 显示取消预约对话框
const showCancelDialog = (reservationId) => {
  currentReservationId.value = reservationId
  cancelReason.value = ''
  showCancelDialogFlag.value = true
}

// 关闭取消预约对话框
const closeCancelDialog = () => {
  showCancelDialogFlag.value = false
  currentReservationId.value = null
  cancelReason.value = ''
  cancelling.value = false
}

// 确认取消预约
const confirmCancel = async () => {
  if (!currentReservationId.value) return

  try {
    cancelling.value = true
    
    // 构建取消预约的请求数据
    const cancelData = {
      reserveId: currentReservationId.value,
      cancelReason: cancelReason.value || null
    }
    
    const response = await reservationAPI.cancelReservation(cancelData)
    
    if (response.success) {
      // 处理API响应格式
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        alert('预约已成功取消')
        // 关闭对话框
        closeCancelDialog()
        // 重新加载预约列表
        loadReservations()
      } else {
        alert(apiData.msg || '取消预约失败')
      }
    } else {
      alert(response.error || '取消预约失败')
    }
  } catch (err) {
    alert('网络错误，请稍后重试')
    console.error('取消预约失败:', err)
  } finally {
    cancelling.value = false
  }
}

// 获取状态样式类
const getStatusClass = (status) => {
  const statusMap = {
    1: 'status-reserved',   // 已预约
    2: 'status-cancelled',  // 已取消
    3: 'status-completed',  // 已完成
    4: 'status-no-show'     // 已爽约
  }
  return statusMap[status] || 'status-reserved'
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

// 判断是否可以取消
const canCancel = (status) => {
  return status === 1  // 只有已预约状态可以取消
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '未知'
  
  try {
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (err) {
    return '日期格式错误'
  }
}

// 组件挂载时加载数据
onMounted(() => {
  try {
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      if (userInfo && userInfo.userId) {
        studentId.value = userInfo.userId
      }
    }
  } catch {}
  loadReservations()
})
</script>

<style scoped>
.reservation-list-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
}

.header {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 700;
}

.page-description {
  font-size: 1.1rem;
  opacity: 0.9;
}

/* 加载状态 */
.loading-container {
  text-align: center;
  padding: 60px 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误状态 */
.error-container {
  text-align: center;
  padding: 60px 20px;
  background: #fff5f5;
  border-radius: 15px;
  border: 1px solid #fed7d7;
}

.error-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.retry-button {
  padding: 12px 24px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.3s ease;
}

.retry-button:hover {
  background: #5a6fd8;
}

/* 空状态 */
.empty-container {
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-container h3 {
  color: #2c3e50;
  margin-bottom: 10px;
}

.empty-container p {
  color: #6c757d;
  margin-bottom: 30px;
}

.reserve-button {
  display: inline-block;
  padding: 15px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.reserve-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

/* 预约列表 */
.reservations-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.reservation-card {
  background: white;
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.reservation-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.reservation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.counselor-info {
  display: flex;
  align-items: center;
  gap: 15px;
}



.reservation-time {
  margin: 0 0 5px 0;
  color: #2c3e50;
  font-size: 1.2rem;
  font-weight: 600;
}

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
}

.status-reserved {
  background: #e3f2fd;
  color: #1565c0;
}

.status-cancelled {
  background: #f8d7da;
  color: #721c24;
}

.status-completed {
  background: #d4edda;
  color: #155724;
}

.status-no-show {
  background: #fff3e0;
  color: #e65100;
}

.reservation-details {
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  margin-bottom: 10px;
  align-items: center;
}

.detail-label {
  font-weight: 600;
  color: #495057;
  min-width: 100px;
  margin-right: 10px;
}

.detail-value {
  color: #6c757d;
  flex: 1;
}

.reservation-actions {
  text-align: right;
  padding-top: 15px;
  border-top: 1px solid #e9ecef;
}

.cancel-button {
  padding: 8px 16px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s ease;
}

.cancel-button:hover {
  background: #c82333;
}

.back-container {
  text-align: center;
  margin-top: 40px;
}

.back-button {
  display: inline-block;
  padding: 12px 24px;
  background: #6c757d;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
  transition: background 0.3s ease;
}

.back-button:hover {
  background: #5a6268;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .reservation-list-container {
    padding: 10px;
  }
  
  .header {
    padding: 30px 15px;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .reservation-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .detail-label {
    min-width: auto;
  }
}

/* 取消预约对话框样式 */
.cancel-dialog-overlay {
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

.cancel-dialog {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 500px;
  width: 90%;
  max-height: 90vh;
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
  font-size: 1.3rem;
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

.dialog-message {
  margin: 0 0 20px 0;
  color: #495057;
  font-size: 1rem;
  line-height: 1.5;
}

.cancel-reason-section {
  margin-top: 20px;
}

.reason-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #495057;
  font-size: 0.95rem;
}

.reason-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  font-size: 0.95rem;
  line-height: 1.4;
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
  transition: border-color 0.2s ease;
}

.reason-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.reason-textarea::placeholder {
  color: #6c757d;
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
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s ease;
  min-width: 100px;
}

.confirm-btn:hover:not(:disabled) {
  background: #c82333;
}

.confirm-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
  opacity: 0.7;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cancel-dialog {
    width: 95%;
    margin: 20px;
  }
  
  .dialog-header,
  .dialog-content,
  .dialog-actions {
    padding: 15px 20px;
  }
  
  .dialog-title {
    font-size: 1.1rem;
  }
}
</style>
