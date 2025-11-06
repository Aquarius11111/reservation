<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎回来，{{ userInfo.name }}！</h1>
      <p class="welcome-subtitle">今天是 {{ currentDate }}，系统运行正常</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <span>📊</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statsData.totalStudents }}</div>
          <div class="stat-label">学生总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <span>👨‍⚕️</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statsData.totalCounselors }}</div>
          <div class="stat-label">咨询师总数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <span>📅</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statsData.todayAppointments }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
          <span>✅</span>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ statsData.monthlySessions }}</div>
          <div class="stat-label">完成咨询</div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-grid">
      <!-- 快速操作 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">快速操作</h3>
        </div>
        <div class="card-content">
          <div class="quick-actions">
            <router-link to="/admin/schedule" class="quick-action-btn">
              <div class="action-icon">📆</div>
              <span>排班管理</span>
            </router-link>
            <router-link to="/admin/counselors" class="quick-action-btn">
              <div class="action-icon">👨‍⚕️</div>
              <span>咨询师管理</span>
            </router-link>
            <router-link to="/admin/students" class="quick-action-btn">
              <div class="action-icon">👨‍🎓</div>
              <span>学生管理</span>
            </router-link>
          </div>
        </div>
      </div>

      <!-- 系统信息 -->
      <div class="card">
        <div class="card-header">
          <h3 class="card-title">系统信息</h3>
        </div>
        <div class="card-content">
          <div class="info-item">
            <div class="info-label">系统版本</div>
            <div class="info-value">v1.0.0</div>
          </div>
          <div class="info-item">
            <div class="info-label">最后更新</div>
            <div class="info-value">2024-01-01</div>
          </div>
          <div class="info-item">
            <div class="info-label">运行状态</div>
            <div class="info-value status-good">运行正常</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { studentAPI, counselorInfoAPI, reportAPI } from '../../api/index.js'
import { ElMessage } from 'element-plus'

// 用户信息
const userInfo = reactive({
  name: ''
})

// 统计数据
const statsData = reactive({
  totalStudents: 0,
  totalCounselors: 0,
  todayAppointments: 0,
  monthlySessions: 0
})

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 加载统计数据
const loadStats = async () => {
  try {
    // 获取学生总数
    const studentCountResponse = await studentAPI.getStudentCount()
    if (studentCountResponse.success && studentCountResponse.data.code === 200) {
      statsData.totalStudents = studentCountResponse.data.data || 0
    } else {
      console.error('获取学生总数失败:', studentCountResponse.data?.message)
      ElMessage.warning('获取学生总数失败，使用默认值')
    }
  } catch (error) {
    console.error('加载学生总数失败:', error)
  }

  try {
    // 获取咨询师总数
    const counselorCountResponse = await counselorInfoAPI.getCounselorCount()
    if (counselorCountResponse.success && counselorCountResponse.data.code === 200) {
      statsData.totalCounselors = counselorCountResponse.data.data || 0
    } else {
      console.error('获取咨询师总数失败:', counselorCountResponse.data?.message)
      ElMessage.warning('获取咨询师总数失败，使用默认值')
    }
  } catch (error) {
    console.error('加载咨询师总数失败:', error)
  }

  try {
    // 获取预约概览数据（当日预约和已完成咨询）
    const overviewResponse = await reportAPI.getReservationOverview()
    if (overviewResponse.success && overviewResponse.data.code === 200) {
      const overviewData = overviewResponse.data.data || {}
      statsData.todayAppointments = overviewData.todayCount || 0
      statsData.monthlySessions = overviewData.completedCount || 0
    } else {
      console.error('获取预约概览数据失败:', overviewResponse.data?.message)
      ElMessage.warning('获取预约概览数据失败，使用默认值')
    }
  } catch (error) {
    console.error('加载预约概览数据失败:', error)
  }
}

// 页面加载时获取用户信息（统一从对象中读取）
onMounted(() => {
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
  
  // 加载统计数据
  loadStats()
})
</script>

<style scoped>
.dashboard {
  width: 100%;
  min-height: 100vh;
  padding-bottom: 30px;
}

/* 欢迎区域 */
.welcome-section {
  margin-bottom: 30px;
}

.welcome-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.welcome-subtitle {
  font-size: 1rem;
  color: #6c757d;
  margin: 0;
}

/* 统计卡片网格 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: #6c757d;
}

/* 主要内容区域 */
.main-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
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
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.view-all-link {
  font-size: 0.9rem;
  color: #8e44ad;
  text-decoration: none;
  transition: color 0.2s ease;
}

.view-all-link:hover {
  color: #9b59b6;
}

.card-content {
  padding: 24px;
}

/* 快速操作 */
.quick-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.quick-action-btn {
  flex: 1;
  min-width: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  text-decoration: none;
  color: #2c3e50;
  transition: all 0.3s ease;
}

.quick-action-btn:hover {
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  color: white;
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(142, 68, 173, 0.3);
}

.action-icon {
  font-size: 2rem;
}

.quick-action-btn span {
  font-size: 0.95rem;
  font-weight: 500;
}

/* 系统信息 */
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 0.95rem;
  color: #6c757d;
}

.info-value {
  font-size: 0.95rem;
  font-weight: 600;
  color: #2c3e50;
}

.status-good {
  color: #28a745;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .main-grid {
    grid-template-columns: 1fr;
  }
  
  .welcome-title {
    font-size: 1.5rem;
  }
  
  .quick-actions {
    flex-direction: column;
  }
  
  .quick-action-btn {
    width: 100%;
  }
}
</style>
