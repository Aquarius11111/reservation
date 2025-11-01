<template>
  <div class="reports">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">咨询记录管理</h1>
      <div class="header-actions">
        <button @click="refreshData" class="refresh-btn">
          <span class="btn-icon">🔄</span>
          刷新
        </button>
      </div>
    </div>

    <!-- 待填写的咨询记录 -->
    <div class="section-container">
      <div class="section-header">
        <h2 class="section-title">
          待填写的咨询记录
        </h2>
        <div class="section-count">{{ pendingRecords.length }} 条</div>
      </div>

      <!-- 筛选区域 -->
      <div class="filters-section">
        <div class="filter-group">
          <label>日期筛选：</label>
          <el-date-picker
            v-model="pendingDateRangeFilter"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 280px"
            @change="handlePendingFilterChange"
          />
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loadingPending" class="loading-state">
        <div class="loading-spinner">⏳</div>
        <p>正在加载待填写记录...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="pendingRecords.length === 0" class="empty-state">
        <div class="empty-icon">📋</div>
        <h3>暂无待填写记录</h3>
        <p>当前筛选条件下没有找到待填写的咨询记录</p>
      </div>

      <!-- 待填写记录列表 -->
      <div v-else class="records-grid">
        <div 
          v-for="record in pendingRecords" 
          :key="record.reserveId"
          class="record-card pending"
        >
          <div class="card-header">
            <div class="record-info">
              <div class="student-name">{{ record.studentName }}</div>
              <div class="student-id">学号：{{ record.studentId }}</div>
            </div>
            <div class="record-status pending">待填写</div>
          </div>
          
          <div class="card-content">
            <div class="record-details">
              <div class="detail-item">
                <span class="label">预约日期：</span>
                <span class="value">{{ record.reserveDate }}</span>
              </div>
              <div class="detail-item">
                <span class="label">创建时间：</span>
                <span class="value">{{ record.createTime }}</span>
              </div>
            </div>
          </div>
          
          <div class="card-actions">
            <button 
              @click="showRecordDialogHandler(record)" 
              class="action-btn fill-btn"
            >
              填写记录
            </button>
          </div>
        </div>
      </div>

      <!-- 待填写记录分页 -->
      <div class="pagination" v-if="pendingTotalPages > 1">
        <div class="pagination-info">
          <span>共 {{ pendingTotal }} 条记录，第 {{ pendingCurrentPage }} / {{ pendingTotalPages }} 页</span>
        </div>
        
        <div class="pagination-controls">
          <button 
            @click="goToPendingPage(pendingCurrentPage - 1)" 
            :disabled="pendingCurrentPage === 1"
            class="page-btn prev-btn"
          >
            上一页
          </button>
          
          <div class="page-numbers">
            <button 
              v-for="page in pendingVisiblePages" 
              :key="page"
              @click="goToPendingPage(page)"
              :class="['page-btn', { 'active': page === pendingCurrentPage }]"
            >
              {{ page }}
            </button>
          </div>
          
          <button 
            @click="goToPendingPage(pendingCurrentPage + 1)" 
            :disabled="pendingCurrentPage === pendingTotalPages"
            class="page-btn next-btn"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 已填写的咨询记录 -->
    <div class="section-container">
      <div class="section-header">
        <h2 class="section-title">
          已填写的咨询记录
        </h2>
        <div class="section-count">{{ completedRecords.length }} 条</div>
      </div>

      <!-- 筛选区域 -->
      <div class="filters-section">
        <div class="filter-group">
          <label>学生姓名：</label>
          <input 
            v-model="completedStudentNameFilter" 
            type="text" 
            placeholder="输入学生姓名"
            class="filter-input"
            @input="handleCompletedFilterChange"
          />
        </div>
        
        <div class="filter-group">
          <label>日期筛选：</label>
          <el-date-picker
            v-model="completedDateRangeFilter"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 280px"
            @change="handleCompletedFilterChange"
          />
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loadingCompleted" class="loading-state">
        <div class="loading-spinner">⏳</div>
        <p>正在加载已填写记录...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="completedRecords.length === 0" class="empty-state">
        <div class="empty-icon">📄</div>
        <h3>暂无已填写记录</h3>
        <p>当前筛选条件下没有找到已填写的咨询记录</p>
      </div>

      <!-- 已填写记录列表 -->
      <div v-else class="records-grid">
        <div 
          v-for="record in completedRecords" 
          :key="record.recordId"
          class="record-card completed"
        >
          <div class="card-header">
            <div class="record-info">
              <div class="student-name">{{ record.studentName }}</div>
              <div class="student-id">学号：{{ record.studentId }}</div>
            </div>
            <div class="record-status completed">已填写</div>
          </div>
          
          <div class="card-content">
            <div class="record-details">
              <div class="detail-item">
                <span class="label">咨询日期：</span>
                <span class="value">{{ record.consultDate }}</span>
              </div>
              <div class="detail-item">
                <span class="label">填写时间：</span>
                <span class="value">{{ record.createTime }}</span>
              </div>
            </div>
            
            <div class="record-content">
              <div class="content-section">
                <h4 class="content-title">学生表现</h4>
                <div class="content-text">
                  <p class="content-text-inner">{{ record.studentPerformance }}</p>
                </div>
              </div>
              
              <div class="content-section">
                <h4 class="content-title">咨询内容</h4>
                <div class="content-text">
                  <p class="content-text-inner">{{ record.consultContent }}</p>
                </div>
              </div>
              
              <div class="content-section">
                <h4 class="content-title">后续建议</h4>
                <div class="content-text">
                  <p class="content-text-inner">{{ record.advicePlan }}</p>
                </div>
              </div>
            </div>
          </div>
          
          <div class="card-actions">
            <button 
              @click="viewRecordDetail(record)" 
              class="action-btn view-btn"
            >
              查看详情
            </button>
            <button 
              @click="editRecord(record)" 
              class="action-btn edit-btn"
            >
              编辑记录
            </button>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="completedTotalPages > 1">
        <div class="pagination-info">
          <span>共 {{ completedTotal }} 条记录，第 {{ completedCurrentPage }} / {{ completedTotalPages }} 页</span>
        </div>
        
        <div class="pagination-controls">
          <button 
            @click="goToCompletedPage(completedCurrentPage - 1)" 
            :disabled="completedCurrentPage === 1"
            class="page-btn prev-btn"
          >
            上一页
          </button>
          
          <div class="page-numbers">
            <button 
              v-for="page in completedVisiblePages" 
              :key="page"
              @click="goToCompletedPage(page)"
              :class="['page-btn', { 'active': page === completedCurrentPage }]"
            >
              {{ page }}
            </button>
          </div>
          
          <button 
            @click="goToCompletedPage(completedCurrentPage + 1)" 
            :disabled="completedCurrentPage === completedTotalPages"
            class="page-btn next-btn"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 填写/编辑咨询记录对话框 -->
    <div v-if="showRecordDialogRef" class="record-dialog-overlay" @click="closeRecordDialog">
      <div class="record-dialog" @click.stop>
        <div class="dialog-header">
          <h3 class="dialog-title">{{ isEditing ? '编辑咨询记录' : '填写咨询记录' }}</h3>
          <button @click="closeRecordDialog" class="close-button">×</button>
        </div>
        
        <div class="dialog-content">
          <div class="form-section">
            <h4 class="form-title">基本信息</h4>
            <div class="form-row">
              <div class="form-group">
                <label>学生姓名</label>
                <input v-model="recordForm.studentName" type="text" readonly class="form-input readonly" />
              </div>
              <div class="form-group">
                <label>咨询日期</label>
                <input v-model="recordForm.consultDate" type="date" readonly class="form-input readonly" />
              </div>
            </div>
          </div>
          
          <div class="form-section">
            <h4 class="form-title">咨询记录</h4>
            <div class="form-group">
              <label>学生表现</label>
              <textarea 
                v-model="recordForm.studentPerformance" 
                placeholder="描述学生在咨询过程中的表现、情绪状态、沟通方式等"
                class="form-textarea"
                rows="4"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label>咨询核心内容</label>
              <textarea 
                v-model="recordForm.consultContent" 
                placeholder="记录咨询过程中讨论的主要问题、解决方案、重要观点等"
                class="form-textarea"
                rows="5"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label>后续方案建议</label>
              <textarea 
                v-model="recordForm.advicePlan" 
                placeholder="提供后续的咨询建议、行动计划、注意事项等"
                class="form-textarea"
                rows="4"
              ></textarea>
            </div>
          </div>
        </div>
        
        <div class="dialog-actions">
          <button @click="closeRecordDialog" class="cancel-btn">
            取消
          </button>
          <button @click="saveRecord" class="save-btn" :disabled="saving">
            {{ saving ? '保存中...' : '保存记录' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 查看记录详情对话框 -->
    <div v-if="showDetailDialog" class="record-dialog-overlay" @click="closeDetailDialog">
      <div class="record-dialog detail-dialog" @click.stop>
        <div class="dialog-header">
          <h3 class="dialog-title">咨询记录详情</h3>
          <button @click="closeDetailDialog" class="close-button">×</button>
        </div>
        
        <div class="dialog-content">
          <div class="detail-section">
            <h4 class="detail-title">基本信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="label">学生姓名：</span>
                <span class="value">{{ currentRecord.studentName }}</span>
              </div>
              <div class="detail-item">
                <span class="label">学号：</span>
                <span class="value">{{ currentRecord.studentId }}</span>
              </div>
              <div class="detail-item">
                <span class="label">咨询日期：</span>
                <span class="value">{{ currentRecord.consultDate }}</span>
              </div>
              <div class="detail-item">
                <span class="label">填写时间：</span>
                <span class="value">{{ currentRecord.createTime }}</span>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <h4 class="detail-title">学生表现</h4>
            <p class="detail-content">{{ currentRecord.studentPerformance }}</p>
          </div>
          
          <div class="detail-section">
            <h4 class="detail-title">咨询核心内容</h4>
            <p class="detail-content">{{ currentRecord.consultContent }}</p>
          </div>
          
          <div class="detail-section">
            <h4 class="detail-title">后续方案建议</h4>
            <p class="detail-content">{{ currentRecord.advicePlan }}</p>
          </div>
        </div>
        
        <div class="dialog-actions">
          <button @click="closeDetailDialog" class="close-detail-btn">
            关闭
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { reservationAPI } from '../../api/index.js'

// 响应式数据
const loadingPending = ref(false)
const loadingCompleted = ref(false)
const error = ref(null)

// 咨询师ID（实际应用中应该从用户登录状态获取）
const counselorId = ref('C001')

// 待填写记录
const pendingRecords = ref([])
const pendingTotal = ref(0)
const pendingCurrentPage = ref(1)
const pendingPageSize = ref(4)
const pendingTotalPages = ref(0)

// 已填写记录
const completedRecords = ref([])
const completedTotal = ref(0)
const completedCurrentPage = ref(1)
const completedPageSize = ref(6)


// 待填写记录筛选条件
const pendingDateRangeFilter = ref([])
// 已填写记录筛选条件
const completedDateRangeFilter = ref([])
const completedStudentNameFilter = ref('')

// 对话框相关
const showRecordDialogRef = ref(false)
const showDetailDialog = ref(false)
const isEditing = ref(false)
const saving = ref(false)
const currentRecord = ref({})

// 记录表单
const recordForm = reactive({
  reserveId: null,
  studentName: '',
  consultDate: '',
  studentPerformance: '',
  consultContent: '',
  advicePlan: ''
})

// 计算属性
const pendingVisiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, pendingCurrentPage.value - 2)
  const end = Math.min(pendingTotalPages.value, pendingCurrentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

const completedTotalPages = computed(() => {
  return Math.ceil(completedTotal.value / completedPageSize.value)
})

const completedVisiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, completedCurrentPage.value - 2)
  const end = Math.min(completedTotalPages.value, completedCurrentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 加载待填写记录
const loadPendingRecords = async () => {
  try {
    loadingPending.value = true
    error.value = null

    const params = {
      counselorId: counselorId.value,
      pageNum: pendingCurrentPage.value,
      pageSize: pendingPageSize.value
    }

    // 添加日期筛选参数（可选）
    if (pendingDateRangeFilter.value && pendingDateRangeFilter.value.length === 2) {
      params.startDate = pendingDateRangeFilter.value[0]
      params.endDate = pendingDateRangeFilter.value[1]
    }

    const response = await reservationAPI.getPendingConsultRecords(params)

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        // 处理分页数据
        pendingRecords.value = apiData.data.list || []
        pendingTotal.value = apiData.data.total || 0
        pendingTotalPages.value = apiData.data.pages || 0
        console.log('获取到的待填写记录:', pendingRecords.value)
        console.log('分页信息:', {
          total: pendingTotal.value,
          pages: pendingTotalPages.value,
          currentPage: pendingCurrentPage.value,
          pageSize: pendingPageSize.value
        })
      } else {
        error.value = apiData.message || apiData.msg || '加载待填写记录失败'
      }
    } else {
      error.value = response.error || '加载待填写记录失败'
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试'
    console.error('加载待填写记录失败:', err)
  } finally {
    loadingPending.value = false
  }
}

// 加载已填写记录
const loadCompletedRecords = async () => {
  try {
    loadingCompleted.value = true
    error.value = null

    const params = {
      counselorId: counselorId.value,
      pageNum: completedCurrentPage.value,
      pageSize: completedPageSize.value
    }

    if (completedDateRangeFilter.value && completedDateRangeFilter.value.length === 2) {
      params.startDate = completedDateRangeFilter.value[0]
      params.endDate = completedDateRangeFilter.value[1]
    }

    if(completedStudentNameFilter.value) {
      params.studentName = completedStudentNameFilter.value
    }

    const response = await reservationAPI.getConsultRecords(params)

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        completedRecords.value = apiData.data.list || []
        completedTotal.value = apiData.data.total || 0
      } else {
        error.value = apiData.message || apiData.msg || '加载已填写记录失败'
      }
    } else {
      error.value = response.error || '加载已填写记录失败'
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试'
    console.error('加载已填写记录失败:', err)
  } finally{
    loadingCompleted.value = false
  }
}

// 显示填写记录对话框
const showRecordDialogHandler = (record) => {
  isEditing.value = false
  currentRecord.value = record
  
  // 初始化表单
  recordForm.reserveId = record.reserveId
  recordForm.studentName = record.studentName
  recordForm.consultDate = record.reserveDate // 使用预约日期作为默认咨询日期
  recordForm.studentPerformance = ''
  recordForm.consultContent = ''
  recordForm.advicePlan = ''
  
  showRecordDialogRef.value = true
}

// 编辑记录
const editRecord = (record) => {
  isEditing.value = true
  currentRecord.value = record
  
  // 初始化表单
  recordForm.reserveId = record.reserveId
  recordForm.studentName = record.studentName
  recordForm.consultDate = record.consultDate
  recordForm.studentPerformance = record.studentPerformance
  recordForm.consultContent = record.consultContent
  recordForm.advicePlan = record.advicePlan
  
  showRecordDialogRef.value = true
}

// 查看记录详情
const viewRecordDetail = (record) => {
  currentRecord.value = record
  showDetailDialog.value = true
}

// 关闭填写记录对话框
const closeRecordDialog = () => {
  showRecordDialogRef.value = false
  saving.value = false
  currentRecord.value = {}
}

// 关闭详情对话框
const closeDetailDialog = () => {
  showDetailDialog.value = false
  currentRecord.value = {}
}

// 保存记录
const saveRecord = async () => {
  if (!recordForm.studentPerformance.trim() || !recordForm.consultContent.trim()) {
    ElMessage.warning('请填写学生表现和咨询内容')
    return
  }

  if (!recordForm.consultDate) {
    ElMessage.warning('请选择咨询日期')
    return
  }

  try {
    saving.value = true

    const recordData = {
      reserveId: recordForm.reserveId,
      counselorId: counselorId.value,
      consultDate: recordForm.consultDate,
      studentPerformance: recordForm.studentPerformance,
      consultContent: recordForm.consultContent,
      advicePlan: recordForm.advicePlan
    }

    console.log('提交的咨询记录数据:', recordData)

    const response = await reservationAPI.createConsultRecord(recordData)

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        ElMessage.success('咨询记录保存成功')
        closeRecordDialog()
        
        // 重新加载数据
        refreshData()
      } else {
        ElMessage.error(apiData.message || apiData.msg || '保存失败')
      }
    } else {
      ElMessage.error(response.error || '保存失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('保存记录失败:', error)
  } finally {
    saving.value = false
  }
}

// 刷新数据
const refreshData = () => {
  Promise.all([
    completedCurrentPage.value = 1,
    pendingCurrentPage.value = 1,
    loadPendingRecords(),
    loadCompletedRecords()
  ])
}

// 分页操作
const goToCompletedPage = (page) => {
  if (page >= 1 && page <= completedTotalPages.value) {
    completedCurrentPage.value = page
    loadCompletedRecords()
  }
}

// 待填写记录分页操作
const goToPendingPage = (page) => {
  if (page >= 1 && page <= pendingTotalPages.value) {
    pendingCurrentPage.value = page
    loadPendingRecords()
  }
}

// 监听筛选条件变化
const handleCompletedFilterChange = () => {
  // 重置到第一页
  completedCurrentPage.value = 1
  // 重新加载已填写记录
  loadCompletedRecords()
}

// 监听待填写记录筛选条件变化
const handlePendingFilterChange = () => {
  // 重置到第一页
  pendingCurrentPage.value = 1
  // 重新加载待填写记录
  loadPendingRecords()
}

onMounted(() => {
  // 初始加载数据
  refreshData()
})
</script>

<style scoped>
.reports {
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

/* 板块容器 */
.section-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  margin-bottom: 30px;
  overflow: hidden;
}

.section-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f8f9fa;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.section-count {
  background: #667eea;
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

/* 筛选区域 */
.filters-section {
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  gap: 20px;
  align-items: end;
  background: #f8f9fa;
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

.filter-input {
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease;
  width: 200px;
}

.filter-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

/* 记录网格 */
.records-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
  padding: 24px;
}

.record-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  overflow: hidden;
  transition: transform 0.2s ease;
}

.record-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.record-card.pending {
  border-left: 4px solid #ffc107;
}

.record-card.completed {
  border-left: 4px solid #28a745;
}

.card-header {
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-info {
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

.record-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.record-status.pending {
  background: #fff3cd;
  color: #856404;
}

.record-status.completed {
  background: #d4edda;
  color: #155724;
}

.card-content {
  padding: 20px;
}

.record-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
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

.record-content {
  margin-top: 20px;
}

.content-section {
  margin-bottom: 16px;
}

.content-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #495057;
  margin: 0 0 8px 0;
}

.content-text {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.content-text-inner {
  font-size: 0.9rem;
  color: #6c757d;
  line-height: 1.5em;
  margin: 0;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  text-overflow: ellipsis;
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

.fill-btn {
  background: #17a2b8;
  color: white;
}

.fill-btn:hover {
  background: #138496;
}

.view-btn {
  background: #6c757d;
  color: white;
}

.view-btn:hover {
  background: #5a6268;
}

.edit-btn {
  background: #ffc107;
  color: #212529;
}

.edit-btn:hover {
  background: #e0a800;
}

/* 分页 */
.pagination {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-top: 30px;
  padding: 20px 24px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
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

/* 空状态 */
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

/* 对话框样式 */
.record-dialog-overlay {
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

.record-dialog {
  background: white;
  border-radius: 15px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 800px;
  width: 90%;
  max-height: 90vh;
  overflow: hidden;
  animation: dialogAppear 0.3s ease-out;
}

.detail-dialog {
  max-width: 800px;
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
  max-height: 60vh;
  overflow-y: auto;
}

.form-section {
  margin-bottom: 24px;
}

.form-title {
  font-size: 1rem;
  font-weight: 600;
  color: #495057;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e9ecef;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-group {
  flex: 1;
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-weight: 500;
  color: #495057;
  margin-bottom: 6px;
  font-size: 0.9rem;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.form-input.readonly {
  background: #f8f9fa;
  color: #6c757d;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.dialog-actions {
  padding: 0 24px 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.dialog-actions .cancel-btn,
.dialog-actions .save-btn,
.dialog-actions .close-detail-btn {
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

.dialog-actions .save-btn {
  background: #28a745;
  color: white;
}

.dialog-actions .save-btn:hover:not(:disabled) {
  background: #218838;
}

.dialog-actions .save-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.dialog-actions .close-detail-btn {
  background: #6c757d;
  color: white;
}

.dialog-actions .close-detail-btn:hover {
  background: #5a6268;
}

/* 详情对话框样式 */
.detail-section {
  margin-bottom: 24px;
}

.detail-title {
  font-size: 1rem;
  font-weight: 600;
  color: #495057;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e9ecef;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.detail-content {
  font-size: 0.95rem;
  color: #6c757d;
  line-height: 1.6;
  margin: 0;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filters-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .records-grid {
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
  
  .form-row {
    flex-direction: column;
  }
  
  .dialog-content {
    max-height: 50vh;
  }
}
</style>
