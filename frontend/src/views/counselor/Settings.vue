<template>
  <div class="settings">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">个人设置</h1>
      <div class="header-actions">
        <button @click="refreshData" class="refresh-btn">
          <span class="btn-icon">🔄</span>
          刷新
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-spinner">⏳</div>
      <p>正在加载个人信息...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <div class="error-icon">❌</div>
      <h3>加载失败</h3>
      <p>{{ error }}</p>
      <button @click="loadCounselorInfo" class="retry-btn">重试</button>
    </div>

    <!-- 个人信息展示 -->
    <div v-else class="settings-content">
      <!-- 头像区域 -->
      <div class="avatar-section">
      <div class="avatar-container">
        <img 
          :src="avatarUrlComputed" 
          :alt="counselorInfo.counselorId"
          class="avatar-image"
        />
          <div class="avatar-overlay">
            <button class="avatar-edit-btn">📷</button>
          </div>
        </div>
        <div class="avatar-info">
          <h2 class="counselor-name">{{ counselorInfo.userName }}</h2>
          <p class="counselor-title">{{ counselorInfo.title }}</p>
        </div>
      </div>

      <!-- 基本信息卡片 -->
      <div class="info-cards">
        <!-- 基本信息 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">基本信息</h3>
          </div>
          <div class="card-content">
            <div class="info-item">
              <span class="info-label">咨询师ID：</span>
              <span class="info-value readonly">{{ counselorInfo.counselorId }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">专业领域：</span>
              <div class="info-value-container">
                <div v-if="!editingFields.specialty" class="info-value-row">
                  <span class="info-value">{{ counselorInfo.specialty }}</span>
                  <button @click="startEdit('specialty')" class="edit-btn">✏️ 编辑</button>
                </div>
                <div v-else class="edit-container">
                  <textarea 
                    v-model="editingValues.specialty" 
                    class="edit-input"
                    rows="2"
                    placeholder="请输入专业领域"
                  ></textarea>
                  <div class="edit-actions">
                    <button @click="saveField('specialty')" class="save-btn" :disabled="saving">保存</button>
                    <button @click="cancelEdit('specialty')" class="cancel-btn">取消</button>
                  </div>
                </div>
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">咨询取向：</span>
              <div class="info-value-container">
                <div v-if="!editingFields.orientation" class="info-value-row">
                  <span class="info-value">{{ counselorInfo.orientation }}</span>
                  <button @click="startEdit('orientation')" class="edit-btn">✏️ 编辑</button>
                </div>
                <div v-else class="edit-container">
                  <textarea 
                    v-model="editingValues.orientation" 
                    class="edit-input"
                    rows="2"
                    placeholder="请输入咨询取向"
                  ></textarea>
                  <div class="edit-actions">
                    <button @click="saveField('orientation')" class="save-btn" :disabled="saving">保存</button>
                    <button @click="cancelEdit('orientation')" class="cancel-btn">取消</button>
                  </div>
                </div>
              </div>
            </div>
            <div class="info-item">
              <span class="info-label">职称：</span>
              <span class="info-value readonly">{{ counselorInfo.title }}</span>
            </div>
          </div>
        </div>

        <!-- 工作信息 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">工作信息</h3>
          </div>
          <div class="card-content">
            <div class="info-item">
              <span class="info-label">工作地点：</span>
              <span class="info-value readonly">{{ counselorInfo.location }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">服务时间：</span>
              <span class="info-value readonly">{{ counselorInfo.serviceTime }}</span>
            </div>
          </div>
        </div>

        <!-- 个人格言 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">个人格言</h3>
            <button v-if="!editingFields.motto" @click="startEdit('motto')" class="edit-btn">✏️ 编辑</button>
          </div>
          <div class="card-content">
            <div class="motto-section">
              <div v-if="!editingFields.motto" class="motto-text">{{ counselorInfo.motto }}</div>
              <div v-else class="edit-container">
                <textarea 
                  v-model="editingValues.motto" 
                  class="edit-input motto-input"
                  rows="3"
                  placeholder="请输入个人格言"
                ></textarea>
                <div class="edit-actions">
                  <button @click="saveField('motto')" class="save-btn" :disabled="saving">保存</button>
                  <button @click="cancelEdit('motto')" class="cancel-btn">取消</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button class="action-btn secondary">重置密码</button>
        <button class="action-btn danger">注销账号</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { counselorInfoAPI } from '../../api/index.js'

// 响应式数据
const loading = ref(false)
const error = ref(null)

// 咨询师信息
const counselorInfo = reactive({
  counselorId: '',
  userName: '',
  specialty: '',
  orientation: '',
  title: '',
  location: '',
  serviceTime: '',
  motto: '',
  avatarUrl: ''
})

// 计算属性：将后端相对路径作为前端相对路径使用
const avatarUrlComputed = computed(() => {
  if (!counselorInfo.avatarUrl) {
    return defaultAvatar
  }
  
  // 如果已经是完整URL（以http://或https://开头），直接返回
  if (counselorInfo.avatarUrl.startsWith('http://') || counselorInfo.avatarUrl.startsWith('https://')) {
    return counselorInfo.avatarUrl
  }
  
  // 直接使用后端返回的相对路径，如 /static/avatar/counselor_11001.jpg
  // Vite会将这个路径代理到后端服务器
  return counselorInfo.avatarUrl
})

// 编辑状态
const editingFields = reactive({
  specialty: false,
  orientation: false,
  motto: false
})

// 编辑中的值
const editingValues = reactive({
  specialty: '',
  orientation: '',
  motto: ''
})

// 保存状态
const saving = ref(false)

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTIwIiBoZWlnaHQ9IjEyMCIgdmlld0JveD0iMCAwIDEyMCAxMjAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxjaXJjbGUgY3g9IjYwIiBjeT0iNjAiIHI9IjYwIiBmaWxsPSIjNjY3ZWVhIi8+CjxjaXJjbGUgY3g9IjYwIiBjeT0iNDgiIHI9IjE4IiBmaWxsPSJ3aGl0ZSIvPgo8cGF0aCBkPSJNMzYgODRDMzYgNzIgODQgNzIgODQgODRWNzJIMzZWNzJaIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4='

// 加载咨询师信息
const loadCounselorInfo = async () => {
  try {
    loading.value = true
    error.value = null

    // 使用咨询师ID 11001 调用API
    const response = await counselorInfoAPI.getCounselorInfo('C001')

    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        // 更新咨询师信息
        Object.assign(counselorInfo, apiData.data)
        console.log('获取到的咨询师信息:', counselorInfo)
      } else {
        error.value = apiData.message || '获取咨询师信息失败'
      }
    } else {
      error.value = response.error || '获取咨询师信息失败'
    }
  } catch (err) {
    error.value = '网络错误，请稍后重试'
    console.error('获取咨询师信息失败:', err)
  } finally {
    loading.value = false
  }
}

// 刷新数据
const refreshData = () => {
  loadCounselorInfo()
}

// 开始编辑字段
const startEdit = (field) => {
  editingFields[field] = true
  editingValues[field] = counselorInfo[field]
}

// 取消编辑
const cancelEdit = (field) => {
  editingFields[field] = false
  editingValues[field] = ''
}

// 保存单个字段
const saveField = async (field) => {
  try {
    saving.value = true
    
    // 保存原值，用于失败时恢复
    const originalValue = counselorInfo[field]
    
    // 临时更新值用于构建完整的更新数据
    counselorInfo[field] = editingValues[field]
    
    // 构建完整的更新数据，包含所有字段
    const updateData = {
      counselorId: counselorInfo.counselorId,
      specialty: counselorInfo.specialty,
      orientation: counselorInfo.orientation,
      title: counselorInfo.title,
      location: counselorInfo.location,
      serviceTime: counselorInfo.serviceTime,
      motto: counselorInfo.motto,
      avatarUrl: counselorInfo.avatarUrl
    }

    const response = await counselorInfoAPI.updateCounselorInfo(updateData)
    
    if (response.success) {
      const apiData = response.data
      if (apiData && apiData.code === 200) {
        // 关闭编辑状态
        editingFields[field] = false
        ElMessage.success('保存成功')
      } else {
        // 恢复原值
        counselorInfo[field] = originalValue
        ElMessage.error(apiData.message || '保存失败')
      }
    } else {
      // 恢复原值
      counselorInfo[field] = originalValue
      ElMessage.error(response.error || '保存失败')
    }
  } catch (err) {
    // 恢复原值
    counselorInfo[field] = originalValue
    ElMessage.error('网络错误，请稍后重试')
    console.error('保存失败:', err)
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  // 初始加载数据
  loadCounselorInfo()
})
</script>

<style scoped>
.settings {
  max-width: 1000px;
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
  transition: all 0.2s ease;
}

.refresh-btn:hover {
  background: #5a6fd8;
  transform: translateY(-1px);
}

.btn-icon {
  font-size: 1rem;
}

/* 加载和错误状态 */
.loading-state, .error-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.loading-spinner {
  font-size: 2rem;
  margin-bottom: 16px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.error-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.retry-btn {
  margin-top: 16px;
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

/* 设置内容 */
.settings-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  align-items: center;
  gap: 30px;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.avatar-container {
  position: relative;
  width: 120px;
  height: 120px;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #e9ecef;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 36px;
  height: 36px;
  background: #667eea;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.avatar-overlay:hover {
  background: #5a6fd8;
  transform: scale(1.1);
}

.avatar-edit-btn {
  background: none;
  border: none;
  color: white;
  font-size: 1rem;
  cursor: pointer;
}

.avatar-info {
  flex: 1;
}

.counselor-name {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.counselor-title {
  font-size: 1.1rem;
  color: #6c757d;
  margin: 0;
}

/* 信息卡片 */
.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.card-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.edit-btn {
  background: none;
  border: none;
  color: #667eea;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.edit-btn:hover {
  background: #e9ecef;
}

.card-content {
  padding: 24px;
}

.info-item {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-weight: 600;
  color: #495057;
  min-width: 100px;
  margin-right: 12px;
}

.info-value {
  color: #6c757d;
  flex: 1;
  line-height: 1.5;
}

.info-value.readonly {
  color: #495057;
  font-weight: 500;
}

.info-value-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-value-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.edit-container {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.edit-input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 0.9rem;
  color: #495057;
  resize: vertical;
  min-height: 60px;
}

.edit-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.edit-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.save-btn, .cancel-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.save-btn {
  background: #667eea;
  color: white;
}

.save-btn:hover:not(:disabled) {
  background: #5a6fd8;
}

.save-btn:disabled {
  background: #adb5bd;
  cursor: not-allowed;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
}

/* 格言区域 */
.motto-section {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.motto-text {
  font-size: 1.1rem;
  font-style: italic;
  color: #495057;
  line-height: 1.6;
}

.motto-input {
  text-align: center;
  font-style: italic;
  font-size: 1.1rem;
  background: white;
  border: 2px solid #667eea;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  padding: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.action-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  font-size: 1rem;
  transition: all 0.2s ease;
}

.action-btn.primary {
  background: #667eea;
  color: white;
}

.action-btn.primary:hover {
  background: #5a6fd8;
  transform: translateY(-1px);
}

.action-btn.secondary {
  background: #6c757d;
  color: white;
}

.action-btn.secondary:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.action-btn.danger {
  background: #dc3545;
  color: white;
}

.action-btn.danger:hover {
  background: #c82333;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .info-cards {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
}
</style>
