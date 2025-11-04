<template>
  <div class="counselor-manage">
    <div class="page-header">
      <h1>咨询师管理</h1>
      <button class="btn-primary" @click="showAddDialog = true">
        <span>➕</span> 添加咨询师
      </button>
    </div>
    
    <div class="page-content">
      <div class="table-container">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-overlay">
          <div class="loading-spinner">加载中...</div>
        </div>
        
        <!-- 咨询师列表表格 -->
        <table class="counselor-table" v-if="displayedList.length > 0">
          <thead>
            <tr>
              <th>咨询师ID</th>
              <th>姓名</th>
              <th>工作地点</th>
              <th>服务时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr 
              v-for="(row, index) in displayedList" 
              :key="row.counselorId" 
              :class="{ 'stripe-row': index % 2 === 1 }"
              @click="handleViewDetail(row)"
              class="table-row-clickable"
            >
              <td>{{ row.counselorId }}</td>
              <td>{{ row.userName }}</td>
              <td>{{ row.location }}</td>
              <td>{{ row.serviceTime }}</td>
              <td @click.stop>
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
          <p>暂无咨询师记录</p>
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

    <!-- 咨询师详情对话框 -->
    <div v-if="showDetailDialog" class="detail-dialog-overlay" @click="closeDetailDialog">
      <div class="detail-dialog" @click.stop>
        <div class="detail-dialog-header">
          <h3 class="detail-dialog-title">{{ isEditMode ? '编辑咨询师信息' : '咨询师详情' }}</h3>
          <button @click="closeDetailDialog" class="close-button">×</button>
        </div>
        <div class="detail-dialog-content" v-if="selectedCounselor">
          <div class="detail-section">
            <div class="detail-avatar-section">
              <img 
                :src="selectedCounselor.avatarUrl || '/avatar-default.png'" 
                :alt="selectedCounselor.userName"
                class="detail-avatar"
                @error="handleAvatarError"
              />
              <div class="detail-name-section">
                <h4 v-if="!isEditMode" class="detail-name">{{ selectedCounselor.userName }}</h4>
                <input 
                  v-else
                  v-model="editForm.userName"
                  type="text"
                  class="edit-input-name"
                  placeholder="请输入姓名"
                />
                <p v-if="!isEditMode" class="detail-title">{{ selectedCounselor.title }}</p>
                <input 
                  v-else
                  v-model="editForm.title"
                  type="text"
                  class="edit-input-title"
                  placeholder="请输入职称"
                />
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <div class="detail-item">
              <span class="detail-label">咨询师ID：</span>
              <span class="detail-value">{{ selectedCounselor.counselorId }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">专业领域：</span>
              <span v-if="!isEditMode" class="detail-value">{{ selectedCounselor.specialty }}</span>
              <input 
                v-else
                v-model="editForm.specialty"
                type="text"
                class="edit-input"
                placeholder="请输入专业领域"
              />
            </div>
            <div class="detail-item">
              <span class="detail-label">咨询取向：</span>
              <span v-if="!isEditMode" class="detail-value">{{ selectedCounselor.orientation }}</span>
              <input 
                v-else
                v-model="editForm.orientation"
                type="text"
                class="edit-input"
                placeholder="请输入咨询取向"
              />
            </div>
            <div class="detail-item">
              <span class="detail-label">工作地点：</span>
              <span v-if="!isEditMode" class="detail-value">{{ selectedCounselor.location }}</span>
              <input 
                v-else
                v-model="editForm.location"
                type="text"
                class="edit-input"
                placeholder="请输入工作地点"
              />
            </div>
            <div class="detail-item" :class="{ 'detail-item-time': isEditMode }">
              <span class="detail-label">服务时间：</span>
              <span v-if="!isEditMode" class="detail-value">{{ selectedCounselor.serviceTime }}</span>
              <div v-else class="time-slot-selector">
                <div 
                  v-for="(slot, index) in editForm.timeSlots" 
                  :key="index"
                  class="time-slot-item"
                >
                  <select v-model="slot.weekday" class="weekday-select">
                    <option value="">选择星期</option>
                    <option value="周一">周一</option>
                    <option value="周二">周二</option>
                    <option value="周三">周三</option>
                    <option value="周四">周四</option>
                    <option value="周五">周五</option>
                  </select>
                  <select v-model="slot.timeRange" class="timerange-select">
                    <option value="">选择时间段</option>
                    <option value="8:00-12:00">8:00-12:00</option>
                    <option value="9:00-12:00">9:00-12:00</option>
                    <option value="14:00-17:00">14:00-17:00</option>
                    <option value="14:00-18:00">14:00-18:00</option>
                  </select>
                  <button 
                    v-if="editForm.timeSlots.length > 1"
                    @click="removeEditTimeSlot(index)"
                    class="remove-time-btn"
                  >
                    删除
                  </button>
                </div>
                <button 
                  v-if="editForm.timeSlots.length < 5"
                  @click="addEditTimeSlot"
                  class="add-time-btn"
                >
                  + 添加时间段
                </button>
              </div>
            </div>
            <div class="detail-item detail-item-motto">
              <span class="detail-label">个人格言：</span>
              <span v-if="!isEditMode" class="detail-value">{{ selectedCounselor.motto }}</span>
              <textarea 
                v-else
                v-model="editForm.motto"
                class="edit-textarea"
                rows="3"
                placeholder="请输入个人格言"
              ></textarea>
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

    <!-- 添加咨询师对话框 -->
    <div v-if="showAddDialog" class="detail-dialog-overlay" @click="closeAddDialog">
      <div class="detail-dialog add-dialog" @click.stop>
        <div class="detail-dialog-header">
          <h3 class="detail-dialog-title">添加咨询师</h3>
          <button @click="closeAddDialog" class="close-button">×</button>
        </div>
        <div class="detail-dialog-content">
          <div class="add-form">
            <div class="form-item">
              <label class="form-label">咨询师ID <span class="required">*</span></label>
              <input 
                v-model="addForm.counselorId"
                type="text"
                class="form-input"
                placeholder="请输入咨询师ID"
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">姓名 <span class="required">*</span></label>
              <input 
                v-model="addForm.userName"
                type="text"
                class="form-input"
                placeholder="请输入姓名"
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">职称 <span class="required">*</span></label>
              <input 
                v-model="addForm.title"
                type="text"
                class="form-input"
                placeholder="请输入职称"
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">专业领域 <span class="required">*</span></label>
              <input 
                v-model="addForm.specialty"
                type="text"
                class="form-input"
                placeholder="请输入专业领域，多个用逗号分隔"
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">咨询取向 <span class="required">*</span></label>
              <input 
                v-model="addForm.orientation"
                type="text"
                class="form-input"
                placeholder="请输入咨询取向"
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">工作地点 <span class="required">*</span></label>
              <input 
                v-model="addForm.location"
                type="text"
                class="form-input"
                placeholder="请输入工作地点"
              />
            </div>
            
            <div class="form-item">
              <label class="form-label">服务时间 <span class="required">*</span></label>
              <div class="time-slot-selector">
                <div 
                  v-for="(slot, index) in addForm.timeSlots" 
                  :key="index"
                  class="time-slot-item"
                >
                  <select v-model="slot.weekday" class="weekday-select">
                    <option value="">选择星期</option>
                    <option value="周一">周一</option>
                    <option value="周二">周二</option>
                    <option value="周三">周三</option>
                    <option value="周四">周四</option>
                    <option value="周五">周五</option>
                  </select>
                  <select v-model="slot.timeRange" class="timerange-select">
                    <option value="">选择时间段</option>
                    <option value="8:00-12:00">8:00-12:00</option>
                    <option value="9:00-12:00">9:00-12:00</option>
                    <option value="14:00-17:00">14:00-17:00</option>
                    <option value="14:00-18:00">14:00-18:00</option>
                  </select>
                  <button 
                    v-if="addForm.timeSlots.length > 1"
                    @click="removeTimeSlot(index)"
                    class="remove-time-btn"
                  >
                    删除
                  </button>
                </div>
                <button 
                  v-if="addForm.timeSlots.length < 5"
                  @click="addTimeSlot"
                  class="add-time-btn"
                >
                  + 添加时间段
                </button>
              </div>
            </div>
            
            <div class="form-item">
              <label class="form-label">个人格言</label>
              <textarea 
                v-model="addForm.motto"
                class="form-textarea"
                rows="3"
                placeholder="请输入个人格言"
              ></textarea>
            </div>
          </div>
        </div>
        <div class="detail-dialog-actions">
          <button @click="closeAddDialog" class="cancel-edit-btn">取消</button>
          <button @click="saveAdd" class="save-edit-btn" :disabled="saving">{{ saving ? '保存中...' : '保存' }}</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { counselorInfoAPI } from '../../api/index.js'

const showAddDialog = ref(false)
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const showDetailDialog = ref(false)
const selectedCounselor = ref(null)
const isEditMode = ref(false)
const saving = ref(false)
const editForm = ref({
  userName: '',
  title: '',
  specialty: '',
  orientation: '',
  location: '',
  timeSlots: [{ weekday: '', timeRange: '' }],
  motto: ''
})

// 添加咨询师表单
const addForm = ref({
  counselorId: '',
  userName: '',
  title: '',
  specialty: '',
  orientation: '',
  location: '',
  timeSlots: [{ weekday: '', timeRange: '' }],
  motto: '',
  avatarUrl: '/static/avatar/default.jpg' // 默认头像
})

// 咨询师列表数据
const counselorList = ref([])
const total = ref(0)

// 总页数
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 当前页显示的数据（直接从API返回，无需再切片）
const displayedList = computed(() => counselorList.value)

// 加载咨询师列表
const loadCounselorList = async () => {
  try {
    loading.value = true
    
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    
    const response = await counselorInfoAPI.getCounselorList(params)
    
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        counselorList.value = apiData.data || []
        total.value = apiData.total || 0
      } else {
        ElMessage.error(apiData.message || '获取咨询师列表失败')
        counselorList.value = []
        total.value = 0
      }
    } else {
      ElMessage.error(response.error || '获取咨询师列表失败')
      counselorList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取咨询师列表失败:', error)
    ElMessage.error('网络错误，请稍后重试')
    counselorList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 分页跳转
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

// 监听页码变化，重新加载数据
watch(currentPage, () => {
  loadCounselorList()
})

// 查看详情
const handleViewDetail = (row) => {
  selectedCounselor.value = { ...row }
  isEditMode.value = false
  showDetailDialog.value = true
}

// 关闭详情对话框
const closeDetailDialog = () => {
  showDetailDialog.value = false
  selectedCounselor.value = null
  isEditMode.value = false
  saving.value = false
}

// 处理头像加载错误
const handleAvatarError = (event) => {
  event.target.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTIwIiBoZWlnaHQ9IjEyMCIgdmlld0JveD0iMCAwIDEyMCAxMjAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxjaXJjbGUgY3g9IjYwIiBjeT0iNjAiIHI9IjYwIiBmaWxsPSIjNjY3ZWVhIi8+CjxjaXJjbGUgY3g9IjYwIiBjeT0iNDgiIHI9IjE4IiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNMzYgODRDMzYgNzIgODQgNzIgODQgODRWNzJIMzZWNzJaIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4='
}

// 编辑
const handleEdit = (row) => {
  selectedCounselor.value = { ...row }
  // 初始化编辑表单
  editForm.value = {
    userName: row.userName || '',
    title: row.title || '',
    specialty: row.specialty || '',
    orientation: row.orientation || '',
    location: row.location || '',
    timeSlots: parseServiceTime(row.serviceTime),
    motto: row.motto || ''
  }
  isEditMode.value = true
  showDetailDialog.value = true
}

// 取消编辑
const cancelEdit = () => {
  isEditMode.value = false
  // 恢复原始数据
  if (selectedCounselor.value) {
    editForm.value = {
      userName: selectedCounselor.value.userName || '',
      title: selectedCounselor.value.title || '',
      specialty: selectedCounselor.value.specialty || '',
      orientation: selectedCounselor.value.orientation || '',
      location: selectedCounselor.value.location || '',
      timeSlots: parseServiceTime(selectedCounselor.value.serviceTime),
      motto: selectedCounselor.value.motto || ''
    }
  }
}

// 保存编辑
const saveEdit = async () => {
  if (!selectedCounselor.value) return
  
  try {
    saving.value = true
    
    // 验证服务时间
    const validTimeSlots = editForm.value.timeSlots.filter(slot => slot.weekday && slot.timeRange)
    if (validTimeSlots.length === 0) {
      ElMessage.warning('请至少添加一个有效的服务时间段')
      return
    }
    
    // 构建更新数据
    const updateData = {
      counselorId: selectedCounselor.value.counselorId,
      userName: editForm.value.userName,
      title: editForm.value.title,
      specialty: editForm.value.specialty,
      orientation: editForm.value.orientation,
      location: editForm.value.location,
      serviceTime: formatServiceTime(editForm.value.timeSlots),
      motto: editForm.value.motto,
      avatarUrl: selectedCounselor.value.avatarUrl
    }
    
    const response = await counselorInfoAPI.updateCounselorInfo(updateData)
    
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        ElMessage.success('更新成功')
        // 更新本地数据
        Object.assign(selectedCounselor.value, updateData)
        // 更新列表中的数据
        const index = counselorList.value.findIndex(item => item.counselorId === selectedCounselor.value.counselorId)
        if (index !== -1) {
          Object.assign(counselorList.value[index], updateData)
        }
        // 退出编辑模式
        isEditMode.value = false
      } else {
        ElMessage.error(apiData.message || '更新失败')
      }
    } else {
      ElMessage.error(response.error || '更新失败')
    }
  } catch (error) {
    console.error('更新咨询师信息失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    saving.value = false
  }
}

// 关闭添加对话框
const closeAddDialog = () => {
  showAddDialog.value = false
  // 重置表单
  addForm.value = {
    counselorId: '',
    userName: '',
    title: '',
    specialty: '',
    orientation: '',
    location: '',
    timeSlots: [{ weekday: '', timeRange: '' }],
    motto: '',
    avatarUrl: '/static/avatar/default.jpg'
  }
  saving.value = false
}

// 添加时间段
const addTimeSlot = () => {
  if (addForm.value.timeSlots.length < 5) {
    addForm.value.timeSlots.push({ weekday: '', timeRange: '' })
  }
}

// 删除时间段
const removeTimeSlot = (index) => {
  if (addForm.value.timeSlots.length > 1) {
    addForm.value.timeSlots.splice(index, 1)
  }
}

// 解析服务时间字符串为时间段数组
const parseServiceTime = (serviceTimeStr) => {
  if (!serviceTimeStr || !serviceTimeStr.trim()) {
    return [{ weekday: '', timeRange: '' }]
  }
  
  // 解析格式： "周一 8:00-12:00, 周三 14:00-17:00"
  const parts = serviceTimeStr.split(',').map(s => s.trim())
  const timeSlots = []
  
  for (const part of parts) {
    // 匹配 "周一 8:00-12:00" 格式
    const match = part.match(/^(\S+)\s+(.+)$/)
    if (match) {
      timeSlots.push({
        weekday: match[1],
        timeRange: match[2]
      })
    }
  }
  
  return timeSlots.length > 0 ? timeSlots : [{ weekday: '', timeRange: '' }]
}

// 格式化服务时间
const formatServiceTime = (timeSlots) => {
  return timeSlots
    .filter(slot => slot.weekday && slot.timeRange)
    .map(slot => `${slot.weekday} ${slot.timeRange}`)
    .join(', ')
}

// 编辑模式：添加时间段
const addEditTimeSlot = () => {
  if (editForm.value.timeSlots.length < 5) {
    editForm.value.timeSlots.push({ weekday: '', timeRange: '' })
  }
}

// 编辑模式：删除时间段
const removeEditTimeSlot = (index) => {
  if (editForm.value.timeSlots.length > 1) {
    editForm.value.timeSlots.splice(index, 1)
  }
}

// 保存添加
const saveAdd = async () => {
  // 表单验证
  if (!addForm.value.counselorId.trim()) {
    ElMessage.warning('请输入咨询师ID')
    return
  }
  if (!addForm.value.userName.trim()) {
    ElMessage.warning('请输入姓名')
    return
  }
  if (!addForm.value.title.trim()) {
    ElMessage.warning('请输入职称')
    return
  }
  if (!addForm.value.specialty.trim()) {
    ElMessage.warning('请输入专业领域')
    return
  }
  if (!addForm.value.orientation.trim()) {
    ElMessage.warning('请输入咨询取向')
    return
  }
  if (!addForm.value.location.trim()) {
    ElMessage.warning('请输入工作地点')
    return
  }
  
  // 验证服务时间
  const validTimeSlots = addForm.value.timeSlots.filter(slot => slot.weekday && slot.timeRange)
  if (validTimeSlots.length === 0) {
    ElMessage.warning('请至少添加一个有效的服务时间段')
    return
  }
  
  try {
    saving.value = true
    
    // 构建添加数据
    const addData = {
      counselorId: addForm.value.counselorId.trim(),
      userName: addForm.value.userName.trim(),
      title: addForm.value.title.trim(),
      specialty: addForm.value.specialty.trim(),
      orientation: addForm.value.orientation.trim(),
      location: addForm.value.location.trim(),
      serviceTime: formatServiceTime(addForm.value.timeSlots),
      motto: addForm.value.motto.trim() || '',
      avatarUrl: addForm.value.avatarUrl
    }
    
    const response = await counselorInfoAPI.addCounselor(addData)
    
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        ElMessage.success('添加成功')
        closeAddDialog()
        // 重新加载列表
        loadCounselorList()
      } else {
        ElMessage.error(apiData.message || '添加失败')
      }
    } else {
      ElMessage.error(response.error || '添加失败')
    }
  } catch (error) {
    console.error('添加咨询师失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    saving.value = false
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除咨询师"${row.userName}"（ID: ${row.counselorId}）吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: false
      }
    )
    
    // 用户确认删除
    try {
      const response = await counselorInfoAPI.deleteCounselor(row.counselorId)
      
      if (response.success) {
        const apiData = response.data
        if (apiData && apiData.code === 200) {
          ElMessage.success('删除成功')
          // 重新加载列表
          loadCounselorList()
        } else {
          ElMessage.error(apiData.message || '删除失败')
        }
      } else {
        ElMessage.error(response.error || '删除失败')
      }
    } catch (error) {
      console.error('删除咨询师失败:', error)
      ElMessage.error('网络错误，请稍后重试')
    }
  } catch (error) {
    // 用户取消删除，不做任何操作
  }
}

onMounted(() => {
  // 初始化加载数据
  loadCounselorList()
})
</script>

<style scoped>
.counselor-manage {
  width: 100%;
  min-height: 100vh;
  padding: 20px;
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

.table-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 加载状态 */
.loading-overlay {
  text-align: center;
  padding: 40px;
}

.loading-spinner {
  color: #8e44ad;
  font-size: 1rem;
}

/* 表格样式 */
.counselor-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.counselor-table thead {
  background: white;
}

.counselor-table thead th {
  background: white;
  color: #8e44ad;
  padding: 12px;
  text-align: left;
  font-weight: 600;
  border-bottom: 2px solid #8e44ad;
  font-size: 0.95rem;
}

.counselor-table tbody td {
  padding: 12px;
  border-bottom: 1px solid #e9ecef;
  color: #495057;
  font-size: 0.9rem;
}

.counselor-table tbody tr.stripe-row {
  background-color: #f8f9fa;
}

.counselor-table tbody tr:hover {
  background-color: #f1f3f5;
}

.counselor-table tbody tr.table-row-clickable {
  cursor: pointer;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-detail, .btn-edit, .btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.btn-detail {
  background: #667eea;
  color: white;
}

.btn-detail:hover {
  background: #5a6fd8;
}

.btn-edit {
  background: #8e44ad;
  color: white;
}

.btn-edit:hover {
  background: #9b59b6;
}

.btn-delete {
  background: #e74c3c;
  color: white;
}

.btn-delete:hover {
  background: #c0392b;
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

.detail-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #e9ecef;
  flex-shrink: 0;
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

.detail-item-motto {
  flex-direction: column;
  gap: 8px;
}

.detail-item-time {
  flex-direction: column;
  gap: 12px;
  align-items: flex-start;
}

.detail-label {
  font-weight: 600;
  color: #495057;
  min-width: 100px;
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

.edit-textarea {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
  resize: vertical;
  min-height: 60px;
  font-family: inherit;
}

.edit-textarea:focus {
  outline: none;
  border-color: #8e44ad;
  box-shadow: 0 0 0 2px rgba(142, 68, 173, 0.1);
}

.edit-input-name {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 1.3rem;
  font-weight: 700;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
  margin-bottom: 8px;
}

.edit-input-name:focus {
  outline: none;
  border-color: #8e44ad;
  box-shadow: 0 0 0 2px rgba(142, 68, 173, 0.1);
}

.edit-input-title {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

.edit-input-title:focus {
  outline: none;
  border-color: #8e44ad;
  box-shadow: 0 0 0 2px rgba(142, 68, 173, 0.1);
}

.cancel-edit-btn, .save-edit-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.95rem;
  transition: background 0.2s ease;
}

.cancel-edit-btn {
  background: #6c757d;
  color: white;
}

.cancel-edit-btn:hover {
  background: #5a6268;
}

.save-edit-btn {
  background: #8e44ad;
  color: white;
}

.save-edit-btn:hover:not(:disabled) {
  background: #9b59b6;
}

.save-edit-btn:disabled {
  background: #adb5bd;
  cursor: not-allowed;
  opacity: 0.7;
}

/* 添加对话框样式 */
.add-dialog {
  max-width: 700px;
}

.add-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-weight: 600;
  color: #495057;
  font-size: 0.95rem;
}

.required {
  color: #e74c3c;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
  font-family: inherit;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #8e44ad;
  box-shadow: 0 0 0 2px rgba(142, 68, 173, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

/* 时间段选择器样式 */
.time-slot-selector {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.time-slot-item {
  display: flex;
  gap: 12px;
  align-items: center;
}

.weekday-select, .timerange-select {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.95rem;
  background: white;
  cursor: pointer;
  transition: border-color 0.2s ease;
}

.weekday-select:focus, .timerange-select:focus {
  outline: none;
  border-color: #8e44ad;
  box-shadow: 0 0 0 2px rgba(142, 68, 173, 0.1);
}

.remove-time-btn {
  padding: 8px 16px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: background 0.2s ease;
  white-space: nowrap;
}

.remove-time-btn:hover {
  background: #c0392b;
}

.add-time-btn {
  padding: 10px 16px;
  background: #8e44ad;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: background 0.2s ease;
  align-self: flex-start;
}

.add-time-btn:hover {
  background: #9b59b6;
}

@media (max-width: 768px) {
  .detail-dialog {
    width: 95%;
    max-height: 95vh;
  }
  
  .detail-avatar-section {
    flex-direction: column;
    text-align: center;
  }
  
  .detail-item {
    flex-direction: column;
    gap: 4px;
  }
  
  .detail-label {
    min-width: auto;
  }
  
  .time-slot-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .weekday-select, .timerange-select {
    width: 100%;
  }
  
  .remove-time-btn {
    width: 100%;
  }
}
</style>
