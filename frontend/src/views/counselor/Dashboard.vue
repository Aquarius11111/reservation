<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎回来，{{ userInfo.name }}！</h1>
      <p class="welcome-subtitle">今天是 {{ currentDate }}，您有 {{ todayAppointments.length + pendingUnfilledCount }} 项待处理事项</p>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-grid">
      <!-- 今日预约 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">今日预约</h3>
          <router-link to="/counselor/appointments" class="view-all-link">查看全部</router-link>
        </div>
        <div class="card-content">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <div class="loading-spinner"></div>
            <p>正在加载今日预约...</p>
          </div>
          
          <!-- 错误状态 -->
          <div v-else-if="error" class="error-state">
            <div class="error-icon">⚠️</div>
            <p>{{ error }}</p>
            <button @click="loadTodayAppointments" class="retry-btn">重试</button>
          </div>
          
          <!-- 空状态 -->
          <div v-else-if="todayAppointments.length === 0" class="empty-state">
            <div class="empty-icon">📅</div>
            <p>今日暂无预约</p>
          </div>
          
          <!-- 预约列表 -->
          <div v-else class="appointment-list">
            <div 
              v-for="appointment in todayAppointments.slice(0, 3)" 
              :key="appointment.id"
              class="appointment-item"
            >
              <div class="appointment-time">{{ appointment.time }}</div>
              <div class="appointment-info">
                <div class="student-name">{{ appointment.studentName }}</div>
                <div class="appointment-type">{{ appointment.type }}</div>
              </div>
              <div class="appointment-status" :class="appointment.status">
                {{ appointment.statusText }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 待处理事项 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">待处理事项</h3>
        </div>
        <div class="card-content">
          <div class="todo-list">
            <div class="todo-item">
              <div class="todo-icon">📝</div>
              <div class="todo-content">
                <div class="todo-text">完成学生咨询预约</div>
                <div class="todo-count"> {{ todayAppointments.length }} 项待完成</div>
              </div>
            </div>
            <div class="todo-item">
              <div class="todo-icon">📋</div>
              <div class="todo-content">
                <div class="todo-text">填写咨询结果报告</div>
                <div class="todo-count">{{ pendingUnfilledCount }} 项待填写</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 快速操作 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">快速操作</h3>
        </div>
        <div class="card-content">
          <div class="quick-actions">
            <router-link to="/counselor/schedule" class="quick-action-btn">
              <div class="action-icon">⏰</div>
              <span>安排时间</span>
            </router-link>
            <router-link to="/counselor/students" class="quick-action-btn">
              <div class="action-icon">👥</div>
              <span>学生档案</span>
            </router-link>
            <router-link to="/counselor/reports" class="quick-action-btn">
              <div class="action-icon">📊</div>
              <span>查看报告</span>
            </router-link>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { reservationAPI } from '@/api/index.js'

// 用户信息
const userInfo = reactive({
  name: '咨询师'
})

// 咨询师ID（从用户登录状态获取）
const counselorId = ref('')

// 统计数据
const totalStudents = ref(156)
const monthlySessions = ref(42)
const averageRating = ref(4.8)

// 今日预约数据
const todayAppointments = ref([])
const loading = ref(false)
const error = ref(null)



// 未填写咨询记录数量
const pendingUnfilledCount = ref(0)

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 获取今日日期字符串（YYYY-MM-DD格式）
const getTodayDateString = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 加载今日预约数据
const loadTodayAppointments = async () => {
  try {
    loading.value = true
    error.value = null

    const params = {
      counselorId: counselorId.value,
      reserveDate: getTodayDateString()
    }

    const response = await reservationAPI.getCounselorReservationList(params)

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        // 转换API数据格式为UI需要的格式
        todayAppointments.value = (apiData.data.list || []).map(appointment => ({
          id: appointment.reserveId,
          time: appointment.reserveTime,
          studentName: appointment.studentName,
          type: appointment.consultTopic,
          status: getStatusClass(appointment.reserveStatus),
          statusText: getStatusText(appointment.reserveStatus)
        }))
      } else {
        error.value = apiData.message || apiData.msg || '加载今日预约数据失败'
      }
    } else {
      error.value = response.error || '加载今日预约数据失败'
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试'
    console.error('加载今日预约数据失败:', err)
  } finally {
    loading.value = false
  }
}

// 加载未填写咨询记录数量
const loadPendingUnfilledCount = async () => {
  try {
    const params = {
      counselorId: counselorId.value,
      pageNum: 1,
      pageSize: 1
    }
    const response = await reservationAPI.getPendingConsultRecords(params)
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        pendingUnfilledCount.value = apiData.data?.total || (apiData.data?.list?.length || 0)
      }
    }
  } catch (e) {
    console.error('加载未填写记录数量失败:', e)
  }
}

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 1: return 'confirmed'
    case 2: return 'cancelled'
    case 3: return 'completed'
    case 4: return 'no-show'
    default: return 'pending'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 1: return '已预约'
    case 2: return '已取消'
    case 3: return '已完成'
    case 4: return '已爽约'
    default: return '未知'
  }
}

onMounted(() => {
  // 从localStorage获取用户信息（统一从对象中读取）
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    try {
      const userInfoData = JSON.parse(userInfoStr)
      // 设置用户姓名
      if (userInfoData.userName) {
        userInfo.name = userInfoData.userName
      }
      // 设置咨询师ID
      if (userInfoData.userId) {
        counselorId.value = userInfoData.userId
        // 只有在获取到咨询师ID后才加载数据
        loadTodayAppointments()
        loadPendingUnfilledCount()
      } else {
        console.warn('未找到咨询师ID，无法加载数据')
      }
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  } else {
    console.warn('未找到用户信息，请重新登录')
  }
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 30px;
}

.welcome-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
}

.welcome-subtitle {
  font-size: 1.1rem;
  color: #6c757d;
  margin: 0;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 2.5rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: #6c757d;
  font-weight: 500;
}

/* 主要内容区域 */
.main-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  overflow: hidden;
}

.card-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.view-all-link {
  color: #667eea;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 500;
}

.view-all-link:hover {
  text-decoration: underline;
}

.card-content {
  padding: 24px;
}

/* 加载状态 */
.loading-state {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e9ecef;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 错误状态 */
.error-state {
  text-align: center;
  padding: 40px 20px;
  color: #dc3545;
}

.error-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.retry-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  margin-top: 12px;
  transition: background-color 0.2s ease;
}

.retry-btn:hover {
  background: #5a6fd8;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

/* 预约列表 */
.appointment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.appointment-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.appointment-item:hover {
  background: #e9ecef;
}

.appointment-time {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  min-width: 60px;
}

.appointment-info {
  flex: 1;
}

.student-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.appointment-type {
  font-size: 0.9rem;
  color: #6c757d;
}

.appointment-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.appointment-status.confirmed {
  background: #d4edda;
  color: #155724;
}

.appointment-status.pending {
  background: #fff3cd;
  color: #856404;
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
  background: #e2e3e5;
  color: #383d41;
}

/* 待办事项 */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.todo-item:hover {
  background: #f8f9fa;
}

.todo-icon {
  font-size: 1.2rem;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e3f2fd;
  border-radius: 6px;
}

.todo-content {
  flex: 1;
}

.todo-text {
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 2px;
}

.todo-count {
  font-size: 0.85rem;
  color: #6c757d;
}

/* 快速操作 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.quick-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 12px;
  background: #f8f9fa;
  border-radius: 8px;
  text-decoration: none;
  color: #2c3e50;
  transition: all 0.2s ease;
}

.quick-action-btn:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 1.5rem;
}

.quick-action-btn span {
  font-size: 0.9rem;
  font-weight: 500;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .quick-actions {
    grid-template-columns: 1fr;
  }
  
  .welcome-title {
    font-size: 1.5rem;
  }
  
  .stat-card {
    padding: 16px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    font-size: 2rem;
  }
}
</style>
