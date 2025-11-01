<template>
  <div class="report-results-container">
    <div class="page-header">
      <h1>测评报告结果</h1>
      <p class="page-subtitle">查看您的心理健康评估报告</p>
    </div>

    <div class="reports-grid" v-if="loading">
      <div class="loading-container">
        <div class="loading-spinner">加载中...</div>
      </div>
    </div>

    <div class="reports-grid" v-else-if="reportList.length > 0">
      <div 
        class="report-card" 
        v-for="report in reportList" 
        :key="report.resultId"
      >
        <div class="card-header">
          <div class="card-badge">报告 #{{ report.resultId }}</div>
          <div class="card-date">{{ formatDateTime(report.createdAt) }}</div>
        </div>
        
        <div class="card-body">
          <div class="score-item">
            <div class="score-label">总分</div>
            <div class="score-value">{{ report.totalScore }}</div>
          </div>
          
          <div class="score-item">
            <div class="score-label">平均分</div>
            <div class="score-value">{{ report.totalAvg }}</div>
          </div>
        </div>

        <div class="card-footer">
          <button class="btn-view" @click="handleViewDetail(report)">
            查看详情
          </button>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">📊</div>
      <p class="empty-text">暂无测评报告</p>
      <router-link to="/survey" class="btn-primary">
        前往填写问卷
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { surveyAPI } from '../api/index.js'

const router = useRouter()

const loading = ref(false)
const reportList = ref([])

// 获取报告列表
const fetchReportList = async () => {
  loading.value = true
  
  try {
    // 获取当前登录学生的ID
    const studentId = localStorage.getItem('userId')
    
    if (!studentId) {
      ElMessage.error('请先登录')
      router.push('/login')
      return
    }
    
    const response = await surveyAPI.getSurveyResult(studentId)
    
    if (response.success) {
      // 获取结果列表（直接是数组）
      const results = response.data || []
      reportList.value = results
    } else {
      ElMessage.error(response.error || '获取报告列表失败')
      reportList.value = []
    }
  } catch (error) {
    console.error('获取报告列表错误:', error)
    ElMessage.error('获取报告列表失败，请检查网络连接')
    reportList.value = []
  } finally {
    loading.value = false
  }
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  const date = new Date(dateTimeStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

// 查看详情
const handleViewDetail = (report) => {
  ElMessage.info('详情功能开发中...')
  console.log('查看报告详情:', report)
}

onMounted(() => {
  fetchReportList()
})
</script>

<style scoped>
.report-results-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: calc(100vh - 64px);
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 10px;
  font-weight: 700;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #6c757d;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.loading-spinner {
  font-size: 1.2rem;
  color: #8e44ad;
}

.reports-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.report-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.report-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-badge {
  font-size: 1.1rem;
  font-weight: 600;
}

.card-date {
  font-size: 0.9rem;
  opacity: 0.9;
}

.card-body {
  padding: 24px;
  display: flex;
  justify-content: space-around;
}

.score-item {
  text-align: center;
}

.score-label {
  font-size: 0.95rem;
  color: #6c757d;
  margin-bottom: 8px;
}

.score-value {
  font-size: 2rem;
  font-weight: 700;
  color: #8e44ad;
}

.card-footer {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
}

.btn-view {
  padding: 10px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-view:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-view:active {
  transform: translateY(0);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 5rem;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text {
  font-size: 1.3rem;
  color: #6c757d;
  margin-bottom: 30px;
}

.btn-primary {
  display: inline-block;
  padding: 15px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .report-results-container {
    padding: 20px 10px;
  }

  .page-header h1 {
    font-size: 2rem;
  }

  .reports-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .score-value {
    font-size: 1.5rem;
  }
}
</style>

