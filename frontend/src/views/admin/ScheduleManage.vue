<template>
  <div class="schedule-manage">
    <div class="page-header">
      <h1>排班管理</h1>
      <button class="btn-primary" @click="showAddDialog = true">
        <span>➕</span> 添加排班
      </button>
    </div>
    
    <div class="page-content">
      <!-- 排班记录表 -->
      <div class="schedule-table-container">
        <!-- 搜索筛选栏 -->
        <div class="search-bar">
          <div class="search-input-group">
            <label for="searchCounselorId" class="search-label">咨询师ID：</label>
            <input 
              id="searchCounselorId"
              type="text" 
              v-model="searchCounselorId"
              class="search-input"
              placeholder="请输入咨询师ID"
              @keyup.enter="handleSearch"
            />
            <button class="btn-search" @click="handleSearch">查询</button>
            <button class="btn-reset" @click="handleReset">重置</button>
          </div>
        </div>
        
        <div v-if="loading" class="loading-overlay">
          <div class="loading-spinner">加载中...</div>
        </div>
        
        <table class="schedule-table" v-if="scheduleList.length > 0">
          <thead>
            <tr>
              <th>排班ID</th>
              <th>咨询师ID</th>
              <th>排班日期</th>
              <th>开始时间</th>
              <th>结束时间</th>
              <th>状态</th>
              <th>学生ID</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, index) in scheduleList" :key="row.reserveTimeId" :class="{ 'stripe-row': index % 2 === 1 }">
              <td>{{ row.reserveTimeId }}</td>
              <td>{{ row.counselorId }}</td>
              <td>{{ formatDate(row.reserveDate) }}</td>
              <td>{{ formatTime(row.startTime) }}</td>
              <td>{{ formatTime(row.endTime) }}</td>
              <td>
                <span :class="['status-tag', row.isOccupied === 0 ? 'status-free' : 'status-occupied']">
                  {{ row.isOccupied === 0 ? '空闲' : '已预约' }}
                </span>
              </td>
              <td>{{ row.studentId || '-' }}</td>
              <td>{{ formatDateTime(row.createdAt) }}</td>
              <td>
                <div class="action-buttons">
                  <button 
                    class="btn-edit" 
                    @click="handleEdit(row)"
                  >
                    更新
                  </button>
                  <button 
                    class="btn-delete" 
                    @click="handleDelete(row)"
                    :disabled="deletingIds.includes(row.reserveTimeId)"
                  >
                    {{ deletingIds.includes(row.reserveTimeId) ? '删除中...' : '删除' }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- 空数据提示 -->
        <div v-else class="empty-state">
          <p>暂无排班记录</p>
        </div>
        
        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <div class="pagination-info">
            共 {{ total }} 条记录，每页 
            <select v-model.number="pageSize" @change="handleSizeChange(pageSize)" class="page-size-select">
              <option value="10">10</option>
              <option value="20">20</option>
              <option value="50">50</option>
              <option value="100">100</option>
            </select>
            条
          </div>
          <div class="pagination-controls">
            <button 
              class="pagination-btn" 
              :disabled="currentPage === 1" 
              @click="handlePageChange(currentPage - 1)"
            >
              上一页
            </button>
            <span class="pagination-page">{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
            <button 
              class="pagination-btn" 
              :disabled="currentPage >= Math.ceil(total / pageSize)" 
              @click="handlePageChange(currentPage + 1)"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加排班对话框 -->
    <div v-if="showAddDialog" class="dialog-overlay" @click="closeDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h2>添加排班</h2>
          <button class="close-btn" @click="closeDialog">✕</button>
        </div>
        
        <div class="dialog-body">
          <form @submit.prevent="handleSubmit">
            <!-- 咨询师ID -->
            <div class="form-group">
              <label for="counselorId" class="form-label">咨询师ID</label>
              <input 
                id="counselorId"
                type="text" 
                v-model="scheduleForm.counselorId" 
                class="form-input"
                placeholder="请输入咨询师ID"
                required
              />
            </div>

            <!-- 日期选择 -->
            <div class="form-group">
              <label class="form-label">排班日期</label>
              <el-date-picker
                v-model="scheduleForm.reserveDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
                style="width: 100%"
              />
            </div>

            <!-- 开始时间 -->
            <div class="form-group">
              <label class="form-label">开始时间</label>
              <el-time-picker
                v-model="scheduleForm.startTime"
                placeholder="开始时间"
                format="HH:mm"
                value-format="HH:mm"
                :disabled-hours="disabledHours"
                :disabled-minutes="disabledMinutes"
                style="width: 100%"
              />
            </div>

            <!-- 结束时间 -->
            <div class="form-group">
              <label class="form-label">结束时间</label>
              <el-time-picker
                v-model="scheduleForm.endTime"
                placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
                :disabled-hours="disabledHours"
                :disabled-minutes="disabledMinutes"
                style="width: 100%"
              />
            </div>

            <div class="dialog-footer">
              <button type="button" class="btn-cancel" @click="closeDialog" :disabled="submitting">取消</button>
              <button type="submit" class="btn-submit" :disabled="!canSubmit || submitting">
                {{ submitting ? '提交中...' : '提交' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 编辑排班对话框 -->
    <div v-if="showEditDialog" class="dialog-overlay" @click="closeDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h2>编辑排班</h2>
          <button class="close-btn" @click="closeDialog">✕</button>
        </div>
        
        <div class="dialog-body">
          <form @submit.prevent="handleSubmit">
            <!-- 咨询师ID -->
            <div class="form-group">
              <label for="editCounselorId" class="form-label">咨询师ID</label>
              <input 
                id="editCounselorId"
                type="text" 
                v-model="scheduleForm.counselorId" 
                class="form-input"
                placeholder="请输入咨询师ID"
                readonly
                style="background-color: #f8f9fa; cursor: not-allowed;"
              />
            </div>

            <!-- 日期选择 -->
            <div class="form-group">
              <label class="form-label">排班日期</label>
              <el-date-picker
                v-model="scheduleForm.reserveDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
                style="width: 100%"
              />
            </div>

            <!-- 开始时间 -->
            <div class="form-group">
              <label class="form-label">开始时间</label>
              <el-time-picker
                v-model="scheduleForm.startTime"
                placeholder="开始时间"
                format="HH:mm"
                value-format="HH:mm"
                :disabled-hours="disabledHours"
                :disabled-minutes="disabledMinutes"
                @change="updateEndTime"
                style="width: 100%"
              />
            </div>

            <!-- 结束时间 -->
            <div class="form-group">
              <label class="form-label">结束时间</label>
              <el-time-picker
                v-model="scheduleForm.endTime"
                placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
                :disabled-hours="disabledHours"
                :disabled-minutes="disabledMinutes"
                disabled
                style="width: 100%"
              />
            </div>

            <div class="dialog-footer">
              <button type="button" class="btn-cancel" @click="closeDialog" :disabled="submitting">取消</button>
              <button type="submit" class="btn-submit" :disabled="!canSubmit || submitting">
                {{ submitting ? '更新中...' : '更新' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { scheduleAPI } from '../../api/index.js'

const showAddDialog = ref(false)
const showEditDialog = ref(false)
const submitting = ref(false)
const loading = ref(false)
const scheduleList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const deletingIds = ref([])
const editingRecord = ref(null)
const searchCounselorId = ref('')

// 表单数据
const scheduleForm = reactive({
  counselorId: '',
  reserveDate: '',
  startTime: '',
  endTime: ''
})

// 验证表单是否可以提交
const canSubmit = computed(() => {
  return scheduleForm.counselorId && 
         scheduleForm.reserveDate && 
         scheduleForm.startTime && 
         scheduleForm.endTime
})

// 禁用过去的日期和周末
const disabledDate = (time) => {
  const now = new Date()
  now.setHours(0, 0, 0, 0)
  const selected = new Date(time)
  selected.setHours(0, 0, 0, 0)
  
  // 禁用过去的日期
  const isPastDate = selected < now
  
  // 获取选中日期是星期几 (0=周日, 1=周一, ..., 6=周六)
  const dayOfWeek = selected.getDay()
  
  // 禁用周末 (周六=6, 周日=0)
  const isWeekend = dayOfWeek === 0 || dayOfWeek === 6
  
  return isPastDate || isWeekend
}

// 禁用不在工作时间的小时 (工作时间: 8:00-12:00,14:00-17:00)
const disabledHours = () => {
  return [0, 1, 2, 3, 4, 5, 6, 7, 13, 17, 18, 19, 20, 21, 22, 23]
}

// 禁用非准点分钟 (只能选择整点)
const disabledMinutes = (selectedHour) => {
  return Array.from({length: 60}, (_, i) => i).filter(minute => minute !== 0)
}

// 关闭对话框
const closeDialog = () => {
  showAddDialog.value = false
  showEditDialog.value = false
  editingRecord.value = null
  // 重置表单
  scheduleForm.counselorId = ''
  scheduleForm.reserveDate = ''
  scheduleForm.startTime = ''
  scheduleForm.endTime = ''
}

// 编辑排班
const handleEdit = (row) => {
  editingRecord.value = row
  // 填充表单数据
  scheduleForm.counselorId = row.counselorId
  scheduleForm.reserveDate = new Date(row.reserveDate).toISOString().split('T')[0]
  scheduleForm.startTime = row.startTime.substring(0, 5) // HH:mm
  scheduleForm.endTime = row.endTime.substring(0, 5) // HH:mm
  showEditDialog.value = true
}

// 更新结束时间（基于开始时间+1小时）
const updateEndTime = () => {
  if (scheduleForm.startTime && showEditDialog.value) {
    const [hours, minutes] = scheduleForm.startTime.split(':')
    let nextHour = parseInt(hours) + 1
    if (nextHour >= 24) {
      nextHour = 0
    }
    scheduleForm.endTime = `${nextHour.toString().padStart(2, '0')}:${minutes}`
  }
}

// Mock数据
const mockScheduleList = [
  {
    reserveTimeId: 1,
    counselorId: '11001',
    reserveDate: '2025-01-20T16:00:00.000+00:00',
    startTime: '08:00:00',
    endTime: '09:00:00',
    isOccupied: 0,
    studentId: null,
    createdAt: '2025-01-15T21:06:31.000+00:00',
    updatedAt: '2025-01-15T21:06:31.000+00:00'
  },
  {
    reserveTimeId: 2,
    counselorId: '11001',
    reserveDate: '2025-01-20T16:00:00.000+00:00',
    startTime: '09:00:00',
    endTime: '10:00:00',
    isOccupied: 1,
    studentId: '10001',
    createdAt: '2025-01-15T21:06:31.000+00:00',
    updatedAt: '2025-01-15T21:06:31.000+00:00'
  },
  {
    reserveTimeId: 3,
    counselorId: '11002',
    reserveDate: '2025-01-21T16:00:00.000+00:00',
    startTime: '10:00:00',
    endTime: '11:00:00',
    isOccupied: 0,
    studentId: null,
    createdAt: '2025-01-15T21:06:31.000+00:00',
    updatedAt: '2025-01-15T21:06:31.000+00:00'
  },
  {
    reserveTimeId: 4,
    counselorId: '11002',
    reserveDate: '2025-01-21T16:00:00.000+00:00',
    startTime: '14:00:00',
    endTime: '15:00:00',
    isOccupied: 1,
    studentId: '10002',
    createdAt: '2025-01-15T21:06:31.000+00:00',
    updatedAt: '2025-01-15T21:06:31.000+00:00'
  },
  {
    reserveTimeId: 5,
    counselorId: '11001',
    reserveDate: '2025-01-22T16:00:00.000+00:00',
    startTime: '15:00:00',
    endTime: '16:00:00',
    isOccupied: 0,
    studentId: null,
    createdAt: '2025-01-15T21:06:31.000+00:00',
    updatedAt: '2025-01-15T21:06:31.000+00:00'
  }
]

// 获取排班列表
const fetchScheduleList = async () => {
  loading.value = true
  
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 如果填写了咨询师ID，添加到查询参数
    if (searchCounselorId.value.trim()) {
      params.counselorId = searchCounselorId.value.trim()
    }
    
    const response = await scheduleAPI.getScheduleList(params)
    
    if (response.success) {
      const result = response.data
      
      if (result.code === 200) {
        scheduleList.value = result.data || []
        total.value = result.total || 0
      } else {
        ElMessage.error(result.message || '获取排班列表失败')
        scheduleList.value = []
        total.value = 0
      }
    } else {
      ElMessage.error(response.error || '获取排班列表失败')
      scheduleList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取排班列表错误:', error)
    ElMessage.error('获取排班列表失败，请检查网络连接')
    scheduleList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  currentPage.value = 1 // 重置到第一页
  fetchScheduleList()
}

// 重置
const handleReset = () => {
  searchCounselorId.value = ''
  currentPage.value = 1
  fetchScheduleList()
}

// 提交表单
const handleSubmit = async () => {
  if (!canSubmit.value) {
    return
  }
  
  submitting.value = true
  
  try {
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    if (showEditDialog.value && editingRecord.value) {
      // 更新现有记录
      const index = mockScheduleList.findIndex(r => r.reserveTimeId === editingRecord.value.reserveTimeId)
      if (index > -1) {
        mockScheduleList[index] = {
          ...mockScheduleList[index],
          counselorId: scheduleForm.counselorId,
          reserveDate: `${scheduleForm.reserveDate}T16:00:00.000+00:00`,
          startTime: `${scheduleForm.startTime}:00`,
          endTime: `${scheduleForm.endTime}:00`,
          updatedAt: new Date().toISOString()
        }
      }
      ElMessage.success('排班更新成功')
    } else {
      // 创建新的排班记录
      const newRecord = {
        reserveTimeId: mockScheduleList.length > 0 ? Math.max(...mockScheduleList.map(r => r.reserveTimeId)) + 1 : 1,
        counselorId: scheduleForm.counselorId,
        reserveDate: `${scheduleForm.reserveDate}T16:00:00.000+00:00`,
        startTime: `${scheduleForm.startTime}:00`,
        endTime: `${scheduleForm.endTime}:00`,
        isOccupied: 0,
        studentId: null,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      
      // 添加到mock数据
      mockScheduleList.unshift(newRecord)
      ElMessage.success('排班添加成功')
    }
    
    closeDialog()
    fetchScheduleList()
  } catch (error) {
    console.error('提交排班失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 删除排班
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除排班ID为 ${row.reserveTimeId} 的记录吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    deletingIds.value.push(row.reserveTimeId)
    
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 从mock数据中删除
    const index = mockScheduleList.findIndex(r => r.reserveTimeId === row.reserveTimeId)
    if (index > -1) {
      mockScheduleList.splice(index, 1)
    }
    
    ElMessage.success('删除成功')
    fetchScheduleList()
  } catch (error) {
    if (error === 'cancel') {
      // 用户取消，不做操作
      return
    }
    console.error('删除排班失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    deletingIds.value = deletingIds.value.filter(id => id !== row.reserveTimeId)
  }
}

// 分页改变
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchScheduleList()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchScheduleList()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit' 
  })
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return '-'
  return timeString.substring(0, 5) // 从 HH:mm:ss 中提取 HH:mm
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchScheduleList()
})
</script>

<style scoped>
.schedule-manage {
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

.schedule-table-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: relative;
  min-height: 200px;
}

/* 搜索栏样式 */
.search-bar {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.search-input-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #495057;
  white-space: nowrap;
}

.search-input {
  flex: 1;
  max-width: 250px;
  padding: 10px 14px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: all 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: #8e44ad;
  box-shadow: 0 0 0 3px rgba(142, 68, 173, 0.1);
}

.btn-search, .btn-reset {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-search {
  background: #8e44ad;
  color: white;
}

.btn-search:hover {
  background: #7d3c98;
}

.btn-search:active {
  transform: scale(0.98);
}

.btn-reset {
  background: #6c757d;
  color: white;
}

.btn-reset:hover {
  background: #5a6268;
}

.btn-reset:active {
  transform: scale(0.98);
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.loading-spinner {
  font-size: 1rem;
  color: #8e44ad;
}

.schedule-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.95rem;
}

.schedule-table thead {
  background: white;
  color: #8e44ad;
}

.schedule-table th {
  padding: 14px 12px;
  text-align: center;
  font-weight: 600;
  border-bottom: 2px solid #8e44ad;
}

.schedule-table tbody tr {
  border-bottom: 1px solid #e9ecef;
  transition: background-color 0.2s ease;
}

.schedule-table tbody tr:hover {
  background-color: #f8f9fa;
}

.schedule-table tbody tr.stripe-row {
  background-color: #f8f9fa;
}

.schedule-table tbody tr.stripe-row:hover {
  background-color: #e9ecef;
}

.schedule-table td {
  padding: 12px;
  text-align: center;
  color: #495057;
}

.status-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
}

.status-tag.status-free {
  background-color: #d4edda;
  color: #155724;
}

.status-tag.status-occupied {
  background-color: #fff3cd;
  color: #856404;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.btn-edit {
  padding: 6px 16px;
  background-color: #c471e8;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-edit:hover {
  background-color: #7d3c98;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(142, 68, 173, 0.3);
}

.btn-delete {
  padding: 6px 16px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-delete:hover:not(:disabled) {
  background-color: #c82333;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(220, 53, 69, 0.3);
}

.btn-delete:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
  opacity: 0.7;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
  font-size: 1rem;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.pagination-info {
  font-size: 0.95rem;
  color: #495057;
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-size-select {
  padding: 6px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 0.95rem;
  cursor: pointer;
  transition: border-color 0.2s ease;
}

.page-size-select:focus {
  outline: none;
  border-color: #8e44ad;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination-btn {
  padding: 8px 16px;
  background-color: white;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  color: #495057;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #8e44ad;
  color: white;
  border-color: #8e44ad;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-page {
  font-size: 0.95rem;
  color: #495057;
  font-weight: 500;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.dialog-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
}

.dialog-header h2 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 1.5rem;
  color: #6c757d;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: #f0f0f0;
  color: #333;
}

.dialog-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 0.95rem;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  font-size: 0.95rem;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  transition: border-color 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #8e44ad;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.btn-cancel {
  padding: 10px 20px;
  font-size: 0.95rem;
  font-weight: 500;
  color: #6c757d;
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel:hover {
  background: #f0f0f0;
  border-color: #c0c4cc;
}

.btn-submit {
  padding: 10px 20px;
  font-size: 0.95rem;
  font-weight: 500;
  color: white;
  background: #8e44ad;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-submit:hover:not(:disabled) {
  background: #9b59b6;
}

.btn-submit:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}
</style>
