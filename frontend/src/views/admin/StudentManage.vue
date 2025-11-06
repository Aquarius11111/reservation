<template>
  <div class="student-manage">
    <div class="page-header">
      <h1>学生管理</h1>
      <button class="btn-primary" @click="showAddDialog = true">
        <span>➕</span> 添加学生
      </button>
    </div>
    
    <div class="page-content">
      <!-- 查询表单 -->
      <div class="search-form">
        <div class="search-form-row">
          <div class="search-item">
            <label>学生姓名：</label>
            <input 
              type="text" 
              v-model="searchForm.userName" 
              placeholder="请输入学生姓名"
              class="search-input"
            />
          </div>
          <div class="search-item">
            <label>年级：</label>
            <select v-model="searchForm.grade" class="search-select">
              <option value="">全部</option>
              <option v-for="grade in gradeOptions" :key="grade" :value="grade">
                {{ grade }}
              </option>
            </select>
          </div>
          <div class="search-item">
            <label>学院：</label>
            <select v-model="searchForm.college" class="search-select">
              <option value="">全部</option>
              <option v-for="college in collegeOptions" :key="college" :value="college">
                {{ college }}
              </option>
            </select>
          </div>
          <div class="search-item">
            <label>专业：</label>
            <input 
              type="text" 
              v-model="searchForm.major" 
              placeholder="请输入专业"
              class="search-input"
            />
          </div>
          <div class="search-item">
            <label>风险等级：</label>
            <select v-model="searchForm.riskLevel" class="search-select">
              <option value="">全部</option>
              <option value="0">低风险</option>
              <option value="1">中风险</option>
              <option value="2">高风险</option>
            </select>
          </div>
          <div class="search-item search-buttons">
            <button class="btn-search" @click="handleSearch">查询</button>
            <button class="btn-reset" @click="handleReset">重置</button>
          </div>
        </div>
      </div>

      <div class="table-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-overlay">
          <div class="loading-spinner">加载中...</div>
        </div>
        
        <!-- 学生列表表格 -->
        <table class="student-table" v-if="displayedList.length > 0">
          <thead>
            <tr>
              <th>学生姓名</th>
              <th>年级</th>
              <th>学院</th>
              <th>专业</th>
              <th>风险等级</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="(row, index) in displayedList" 
              :key="row.studentId || index" 
              :class="{ 'stripe-row': index % 2 === 1 }"
            >
              <td>{{ row.userName || '-' }}</td>
              <td>{{ row.grade || '-' }}</td>
              <td>{{ row.college || '-' }}</td>
              <td>{{ row.major || '-' }}</td>
              <td>
                <span :class="getRiskLevelClass(row.riskLevel)">
                  {{ getRiskLevelText(row.riskLevel) }}
                </span>
              </td>
              <td>
                <div class="action-buttons">
                  <button class="btn-detail" @click="handleViewDetail(row)">详情</button>
                  <button class="btn-edit" @click="handleEdit(row)">编辑</button>
                  <button class="btn-delete" @click="handleDelete(row)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- 空数据提示 -->
        <div v-else class="empty-state">
          <p>暂无学生记录</p>
        </div>
        
        <!-- 分页 -->
        <div class="pagination" v-if="totalPages > 1">
          <button 
            class="page-btn" 
            @click="goToPage(currentPage - 1)"
            :disabled="currentPage === 1"
          >
            上一页
          </button>
          <span class="page-info">
            第 {{ currentPage }} / {{ totalPages }} 页，共 {{ total }} 条
          </span>
          <button 
            class="page-btn" 
            @click="goToPage(currentPage + 1)"
            :disabled="currentPage === totalPages"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 学生详情对话框 -->
    <div v-if="showDetailDialog" class="detail-dialog-overlay" @click="closeDetailDialog">
      <div class="detail-dialog" @click.stop>
        <div class="detail-dialog-header">
          <h3 class="detail-dialog-title">{{ isEditMode ? '编辑学生信息' : '学生详情' }}</h3>
          <button @click="closeDetailDialog" class="close-button">×</button>
        </div>
        <div class="detail-dialog-content" v-if="selectedStudent">
          <div class="detail-section">
            <div class="detail-avatar-section">
              <div class="detail-avatar-placeholder">
                <span class="avatar-text">{{ selectedStudent.userName ? selectedStudent.userName.charAt(0) : '学' }}</span>
              </div>
              <div class="detail-name-section">
                <h4 class="detail-name">{{ selectedStudent.userName }}</h4>
                <p class="detail-title">{{ getRiskLevelText(selectedStudent.riskLevel) }}</p>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <div class="detail-item">
              <span class="detail-label">学生ID：</span>
              <span class="detail-value">{{ selectedStudent.studentId }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">性别：</span>
              <span class="detail-value">{{ selectedStudent.gender === 1 ? '男' : selectedStudent.gender === 2 ? '女' : '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">学院：</span>
              <span class="detail-value">{{ selectedStudent.college || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">年级：</span>
              <span class="detail-value">{{ selectedStudent.grade || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">专业：</span>
              <span class="detail-value">{{ selectedStudent.major || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">班级：</span>
              <span class="detail-value">{{ selectedStudent.className || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">手机号码：</span>
              <span class="detail-value">{{ selectedStudent.phoneNumber || '-' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">风险等级：</span>
              <span v-if="!isEditMode" class="detail-value">
                <span :class="getRiskLevelClass(selectedStudent.riskLevel)">
                  {{ getRiskLevelText(selectedStudent.riskLevel) }}
                </span>
              </span>
              <select v-else v-model="editForm.riskLevel" class="edit-input">
                <option value="0">低风险</option>
                <option value="1">中风险</option>
                <option value="2">高风险</option>
              </select>
            </div>
            <div class="detail-item">
              <span class="detail-label">最后测评时间：</span>
              <span v-if="!isEditMode" class="detail-value">{{ selectedStudent.lastEvaluateTime || '暂无' }}</span>
              <el-date-picker
                v-else
                v-model="editForm.lastEvaluateTime"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="flex: 1;"
              />
            </div>
            <div class="detail-item">
              <span class="detail-label">最后咨询时间：</span>
              <span v-if="!isEditMode" class="detail-value">{{ selectedStudent.lastCounselTime || '暂无' }}</span>
              <el-date-picker
                v-else
                v-model="editForm.lastCounselTime"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="flex: 1;"
              />
            </div>
          </div>
        </div>
        <div class="detail-dialog-actions">
          <button v-if="!isEditMode" @click="closeDetailDialog" class="close-detail-btn">关闭</button>
          <template v-else>
            <button @click="cancelEdit" class="cancel-edit-btn">取消</button>
            <button @click="saveEdit" class="save-edit-btn" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ElDatePicker } from 'element-plus'
import { studentAPI } from '../../api/index.js'

const showAddDialog = ref(false)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const studentList = ref([])
const total = ref(0)
const showDetailDialog = ref(false)
const selectedStudent = ref(null)
const isEditMode = ref(false)
const saving = ref(false)
const editForm = ref({
  riskLevel: 0,
  lastEvaluateTime: '',
  lastCounselTime: ''
})

// 查询表单
const searchForm = ref({
  userName: '',
  grade: '',
  college: '',
  major: '',
  riskLevel: ''
})

// 年级选项（固定值）
const gradeOptions = ref([
  '2021级',
  '2022级',
  '2023级',
  '2024级',
  '2025级',
  '2026级'
])

// 学院选项（固定值）
const collegeOptions = ref([
  '材料科学与工程学院',
  '交通与物流工程学院',
  '机电工程学院',
  '能源与动力工程学院',
  '土木工程与建筑学院',
  '计算机与人工智能学院',
  '自动化学院',
  '信息工程学院',
  '航运学院',
  '船舶与海洋工程学院',
  '资源与环境工程学院',
  '安全科学与应急管理学院',
  '化学化工与生命科学学院',
  '理学院',
  '管理学院',
  '经济学院',
  '艺术与设计学院',
  '马克思主义学院',
  '法学与人文社会学院',
  '外国语学院',
  '国际教育学院',
  '创业学院',
  '体育部',
  '继续教育学院',
  '汽车工程学院',
  '物流工程学院',
  '光电信息科学与工程学院',
  '生物科学与技术学院'
])

// 总页数
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 当前页显示的数据
const displayedList = computed(() => studentList.value)

// 获取风险等级文本（riskLevel: 0=低风险, 1=中风险, 2=高风险）
const getRiskLevelText = (riskLevel) => {
  if (riskLevel === null || riskLevel === undefined) return '-'
  const levelMap = {
    0: '低风险',
    1: '中风险',
    2: '高风险'
  }
  return levelMap[riskLevel] || '-'
}

// 获取风险等级样式类
const getRiskLevelClass = (riskLevel) => {
  if (riskLevel === null || riskLevel === undefined) return 'risk-level'
  if (riskLevel === 2) {
    return 'risk-level risk-high'
  } else if (riskLevel === 1) {
    return 'risk-level risk-medium'
  } else {
    return 'risk-level risk-low'
  }
}

// 加载学生列表
const loadStudentList = async () => {
  try {
    loading.value = true
    
    // 构建查询参数
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    
    // 添加搜索条件（只添加有值的条件）
    if (searchForm.value.userName) {
      params.userName = searchForm.value.userName
    }
    if (searchForm.value.grade) {
      params.grade = searchForm.value.grade
    }
    if (searchForm.value.college) {
      params.college = searchForm.value.college
    }
    if (searchForm.value.major) {
      params.major = searchForm.value.major
    }
    if (searchForm.value.riskLevel !== '') {
      params.riskLevel = parseInt(searchForm.value.riskLevel)
    }
    
    const response = await studentAPI.searchStudents(params)
    
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        studentList.value = apiData.data || []
        total.value = apiData.total || 0
      } else {
        ElMessage.error(apiData.message || '获取学生列表失败')
        studentList.value = []
        total.value = 0
      }
    } else {
      ElMessage.error(response.error || '获取学生列表失败')
      studentList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('网络错误，请稍后重试')
    studentList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 查询
const handleSearch = () => {
  currentPage.value = 1 // 重置到第一页
  loadStudentList()
}

// 重置查询
const handleReset = () => {
  searchForm.value = {
    userName: '',
    grade: '',
    college: '',
    major: '',
    riskLevel: ''
  }
  currentPage.value = 1
  loadStudentList()
}

// 跳转到指定页
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
    loadStudentList()
  }
}

// 查看详情
const handleViewDetail = (row) => {
  selectedStudent.value = { ...row }
  isEditMode.value = false
  showDetailDialog.value = true
}

// 关闭详情对话框
const closeDetailDialog = () => {
  showDetailDialog.value = false
  selectedStudent.value = null
  isEditMode.value = false
  editForm.value = {
    riskLevel: 0,
    lastEvaluateTime: '',
    lastCounselTime: ''
  }
}

// 编辑
const handleEdit = (row) => {
  selectedStudent.value = { ...row }
  isEditMode.value = true
  editForm.value = {
    riskLevel: row.riskLevel || 0,
    lastEvaluateTime: row.lastEvaluateTime || '',
    lastCounselTime: row.lastCounselTime || ''
  }
  showDetailDialog.value = true
}

// 取消编辑
const cancelEdit = () => {
  isEditMode.value = false
  editForm.value = {
    riskLevel: 0,
    lastEvaluateTime: '',
    lastCounselTime: ''
  }
}

// 保存编辑
const saveEdit = async () => {
  if (!selectedStudent.value) return
  
  try {
    saving.value = true
    
    // 构建完整的学生信息数据
    const updateData = {
      studentId: selectedStudent.value.studentId,
      gender: selectedStudent.value.gender,
      college: selectedStudent.value.college,
      grade: selectedStudent.value.grade,
      major: selectedStudent.value.major,
      className: selectedStudent.value.className,
      phoneNumber: selectedStudent.value.phoneNumber,
      riskLevel: parseInt(editForm.value.riskLevel),
      lastEvaluateTime: editForm.value.lastEvaluateTime || '',
      lastCounselTime: editForm.value.lastCounselTime || ''
    }
    
    // 调用更新学生信息的API
    const response = await studentAPI.updateStudentInfo(updateData)
    
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        // 更新本地数据
        selectedStudent.value.riskLevel = updateData.riskLevel
        selectedStudent.value.lastEvaluateTime = updateData.lastEvaluateTime || null
        selectedStudent.value.lastCounselTime = updateData.lastCounselTime || null
        
        // 更新列表中的数据
        const index = studentList.value.findIndex(item => item.studentId === selectedStudent.value.studentId)
        if (index !== -1) {
          studentList.value[index] = { ...studentList.value[index], ...updateData }
        }
        
        ElMessage.success(apiData.data || '保存成功')
        isEditMode.value = false
      } else {
        ElMessage.error(apiData.message || '保存失败')
      }
    } else {
      ElMessage.error(response.error || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除学生"${row.userName}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用删除API
    const response = await studentAPI.deleteStudent(row.studentId)
    
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        ElMessage.success('删除成功')
        loadStudentList()
      } else {
        ElMessage.error(apiData.message || '删除失败')
      }
    } else {
      ElMessage.error(response.error || '删除失败')
    }
  } catch (error) {
    // 用户取消删除或删除失败
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadStudentList()
})
</script>

<style scoped>
.student-manage {
  width: 100%;
  min-height: 100vh;
  padding-bottom: 30px;
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

/* 查询表单 */
.search-form {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 16px 20px;
  margin-bottom: 20px;
}

.search-form-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: nowrap;
}

.search-item {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.search-item label {
  font-size: 0.85rem;
  color: #495057;
  white-space: nowrap;
  flex-shrink: 0;
}

.search-input,
.search-select {
  width: 120px;
  padding: 6px 10px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 0.85rem;
  color: #495057;
  transition: border-color 0.2s ease;
}

.search-item:first-child .search-input {
  width: 140px;
}

.search-item:nth-child(3) .search-select {
  width: 180px;
}

.search-item:nth-child(4) .search-input {
  width: 140px;
}

.search-input:focus,
.search-select:focus {
  outline: none;
  border-color: #8e44ad;
}

.search-select {
  cursor: pointer;
  background: white;
}

.search-buttons {
  justify-content: flex-end;
  margin-left: auto;
}

.btn-search,
.btn-reset {
  padding: 6px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.btn-search {
  background: #8e44ad;
  color: white;
  margin-right: 12px;
}

.btn-search:hover {
  background: #9b59b6;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(142, 68, 173, 0.3);
}

.btn-reset {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
}

.btn-reset:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 24px;
  overflow: hidden;
  position: relative;
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

.student-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.student-table thead {
  background: white;
}

.student-table thead th {
  background: white;
  color: #8e44ad;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid #8e44ad;
  font-size: 0.95rem;
}

.student-table tbody td {
  padding: 12px;
  border-bottom: 1px solid #e9ecef;
  color: #495057;
  font-size: 0.9rem;
}

.student-table tbody tr {
  transition: background-color 0.2s ease;
}

.student-table tbody tr:hover {
  background-color: #f1f3f5;
}

.student-table tbody tr.stripe-row {
  background-color: #f8f9fa;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.empty-state p {
  font-size: 1rem;
  margin: 0;
}

/* 风险等级标签 */
.risk-level {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
}

.risk-high {
  background-color: #fee;
  color: #c33;
}

.risk-medium {
  background-color: #fff3cd;
  color: #856404;
}

.risk-low {
  background-color: #d4edda;
  color: #155724;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.action-buttons button {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s ease;
}

.btn-detail {
  background: #e3f2fd;
  color: #1976d2;
}

.btn-detail:hover {
  background: #bbdefb;
}

.btn-edit {
  background: #fff3e0;
  color: #f57c00;
}

.btn-edit:hover {
  background: #ffe0b2;
}

.btn-delete {
  background: #ffebee;
  color: #c62828;
}

.btn-delete:hover {
  background: #ffcdd2;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #ced4da;
  background: white;
  color: #495057;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.page-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: #8e44ad;
  color: #8e44ad;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #6c757d;
  font-size: 0.9rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-form-row {
    flex-wrap: wrap;
    gap: 10px;
  }

  .search-item {
    flex: 1 1 calc(50% - 10px);
    min-width: 150px;
  }

  .search-input,
  .search-select {
    width: 100% !important;
  }

  .search-buttons {
    margin-left: 0;
    width: 100%;
    justify-content: flex-start;
  }

  .btn-search,
  .btn-reset {
    flex: 1;
    max-width: 120px;
  }

  .student-table {
    font-size: 0.85rem;
  }

  .student-table th,
  .student-table td {
    padding: 12px 8px;
  }

  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .action-buttons button {
    width: 100%;
    padding: 8px;
  }

  .pagination {
    flex-direction: column;
    gap: 12px;
  }
}

/* 详情对话框样式 */
.detail-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(3px);
}

.detail-dialog {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
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

.detail-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e9ecef;
  background: #f8f9fa;
}

.detail-dialog-title {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.8rem;
  color: #6c757d;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
  line-height: 1;
}

.close-button:hover {
  background: #e9ecef;
  color: #495057;
}

.detail-dialog-content {
  padding: 25px;
  overflow-y: auto;
  flex: 1;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.detail-avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 20px;
}

.detail-avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 4px solid #e9ecef;
}

.avatar-text {
  font-size: 2.5rem;
  font-weight: 700;
  color: white;
}

.detail-name-section {
  flex: 1;
}

.detail-name {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.detail-title {
  font-size: 1rem;
  color: #6c757d;
  margin: 0;
}

.detail-item {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
  padding-bottom: 16px;
  border-bottom: 1px solid #f1f3f5;
}

.detail-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.detail-label {
  font-weight: 600;
  color: #495057;
  min-width: 120px;
  margin-right: 12px;
  font-size: 0.95rem;
}

.detail-value {
  color: #6c757d;
  flex: 1;
  line-height: 1.6;
  font-size: 0.95rem;
}

.detail-dialog-actions {
  display: flex;
  gap: 20px;
  justify-content: flex-end;
  padding: 20px 25px;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

.close-detail-btn {
  padding: 10px 24px;
  background: #8e44ad;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  transition: background 0.2s ease;
}

.close-detail-btn:hover {
  background: #9b59b6;
}

/* 编辑输入框样式 */
.edit-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.edit-input:focus {
  outline: none;
  border-color: #8e44ad;
  box-shadow: 0 0 0 2px rgba(142, 68, 173, 0.1);
}

.cancel-edit-btn {
  padding: 10px 24px;
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  transition: all 0.2s ease;
}

.cancel-edit-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.save-edit-btn {
  padding: 10px 24px;
  background: #8e44ad;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  transition: background 0.2s ease;
}

.save-edit-btn:hover:not(:disabled) {
  background: #9b59b6;
}

.save-edit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
