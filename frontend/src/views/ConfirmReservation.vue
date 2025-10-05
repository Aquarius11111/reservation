<template>
  <div class="confirm-reservation">
    <div class="container">
      <h2 class="page-title">确认预约信息</h2>
      
      <div v-if="loading" class="loading">
        <p>正在加载咨询师信息...</p>
      </div>
      
      <div v-else-if="!counselorInfo" class="error">
        <p>获取咨询师信息失败，请返回重新选择。</p>
        <button @click="goBack" class="back-btn">返回</button>
      </div>
      
      <div v-else class="reservation-summary">
        <!-- 咨询师信息 -->
        <div class="counselor-section">
          <h3>选择的咨询师</h3>
          <div class="counselor-card">
            <div class="counselor-info">
              <div class="avatar">
                <img 
                  :src="counselorInfo.avatarUrl || '/avatar-default.png'" 
                  :alt="counselorInfo.counselorName"
                />
              </div>
              <div class="details">
                <h4>{{ counselorInfo.counselorName }}</h4>
                <p class="title">{{ counselorInfo.title }}</p>
                <p class="specialty">专业方向：{{ counselorInfo.specialty }}</p>
                <p class="orientation">咨询取向：{{ counselorInfo.orientation }}</p>
                <p class="location">咨询地点：{{ counselorInfo.location }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 预约时间 -->
        <div class="time-section">
          <h3>预约时间</h3>
          <div class="time-slots">
            <div 
              v-for="(timeSlot, index) in selectedTimes" 
              :key="index"
              class="time-slot"
            >
              <span class="date">{{ timeSlot.reserveDate }}</span>
              <span class="time">{{ formatTimeDisplay(timeSlot.startTime) }} - {{ formatTimeDisplay(timeSlot.endTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 咨询原因 -->
        <div class="reason-section">
          <h3>咨询原因</h3>
          <div class="reason-content">
            {{ consultReason }}
          </div>
        </div>

        <!-- 选择具体时段 -->
        <div class="time-selection-section" v-if="counselorInfo && counselorInfo.matchedTimeList">
          <h3>选择具体预约时段</h3>
          <div class="time-options">
            <div 
              v-for="(timeSlot, index) in counselorInfo.matchedTimeList" 
              :key="index"
              class="time-option"
              :class="{ selected: selectedTimeSlot && selectedTimeSlot.reserveTimeId === timeSlot.reserveTimeId }"
              @click="selectTimeSlot(timeSlot)"
            >
              <div class="time-info">
                <span class="date">{{ timeSlot.reserveDate }}</span>
                <span class="time">{{ formatTimeDisplay(timeSlot.startTime) }} - {{ formatTimeDisplay(timeSlot.endTime) }}</span>
              </div>
              <div class="time-id">时段ID: {{ timeSlot.reserveTimeId }}</div>
            </div>
          </div>
        </div>

        <!-- 确认按钮 -->
        <div class="actions">
          <button @click="goBack" class="cancel-btn">取消</button>
          <button 
            @click="confirmReservation" 
            :disabled="submitting || !selectedTimeSlot"
            class="confirm-btn"
          >
            {{ submitting ? '提交中...' : '确认预约' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { counselorAPI } from '../api/index.js'
import { useReservationStore } from '@/stores/reservation'

export default {
  name: 'ConfirmReservation',
  data() {
    return {
      counselorId: null,
      consultReason: '',
      selectedTimes: [],
      counselorInfo: null,
      loading: true,
      submitting: false,
      selectedTimeSlot: null // 选中的具体时段
    }
  },
  async mounted() {
    // 从Pinia store获取预约数据
    const reservationStore = useReservationStore()
    
    if (reservationStore.hasReservationData()) {
      const data = reservationStore.getReservationData()
      
      this.counselorId = data.counselorId
      this.consultReason = data.consultReason
      this.selectedTimes = data.selectedTimes || []
      this.counselorInfo = data.counselorInfo
      
      this.loading = false
    } else {
      console.error('未找到预约数据，请重新选择咨询师')
      this.loading = false
    }
  },
  methods: {
    async loadCounselorInfo() {
      try {
        const response = await counselorAPI.getCounselorDetail(this.counselorId)
        if (response.success && response.data.code === 200) {
          this.counselorInfo = response.data.data
        }
      } catch (error) {
        console.error('获取咨询师信息失败:', error)
      } finally {
        this.loading = false
      }
    },
    
    // 格式化时间显示（HH:mm:ss -> HH:mm）
    formatTimeDisplay(timeString) {
      if (!timeString) return ''
      
      // 如果是HH:mm:ss格式，去掉秒数
      if (/^\d{2}:\d{2}:\d{2}$/.test(timeString)) {
        return timeString.substring(0, 5)
      }
      
      // 如果是HH:mm格式，直接返回
      if (/^\d{1,2}:\d{2}$/.test(timeString)) {
        return timeString
      }
      
      return timeString
    },
    
    // 选择具体时段
    selectTimeSlot(timeSlot) {
      this.selectedTimeSlot = timeSlot
      console.log('选中的时段:', timeSlot)
    },
    
    async confirmReservation() {
      if (!this.selectedTimeSlot) {
        alert('请先选择一个具体时段')
        return
      }
      
      this.submitting = true
      
      try {
        // 构建创建预约的请求数据
        const requestData = {
          studentId: "S2023001", // 可以从token解析或用户信息获取
          counselorId: this.counselorId,
          reserveTimeId: this.selectedTimeSlot.reserveTimeId,
          consultTopic: this.consultReason
        }
        
        console.log('创建预约请求数据:', requestData)
        
        // 调用创建预约API
        const response = await this.createReservationAPI(requestData)
        
        if (response.success && response.data.code === 200) {
          alert('预约创建成功！')
          // 清理Pinia store中的数据
          const reservationStore = useReservationStore()
          reservationStore.clearReservationData()
          // 成功后跳转到首页
          this.$router.push({ name: 'home' })
        } else {
          console.error('创建预约失败:', response)
          alert(response.data?.message || '创建预约失败，请重试')
        }
      } catch (error) {
        console.error('创建预约失败:', error)
        alert('网络错误，请检查连接后重试')
      } finally {
        this.submitting = false
      }
    },
    
    // 创建预约API调用
    async createReservationAPI(data) {
      const API_CONFIG = {
        baseURL: 'https://m1.apifoxmock.com/m1/7202211-6928258-6287371',
        timeout: 10000,
        headers: {
          'Content-Type': 'application/json'
        }
      }
      
      const config = {
        method: 'POST',
        headers: API_CONFIG.headers,
        body: JSON.stringify(data)
      }
      
      const response = await fetch(`${API_CONFIG.baseURL}/api/reserve/create`, config)
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      
      return {
        success: true,
        data: result,
        status: response.status
      }
    },
    
    goBack() {
      // 清理Pinia store中的数据
      const reservationStore = useReservationStore()
      reservationStore.clearReservationData()
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.confirm-reservation {
  padding: 20px 0;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
}

.loading,
.error {
  text-align: center;
  padding: 40px;
  background: white;
  border-radius: 10px;
  color: #666;
}

.back-btn {
  padding: 10px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 15px;
}

.reservation-summary {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.counselor-section,
.time-section,
.reason-section {
  background: white;
  padding: 25px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.counselor-section h3,
.time-section h3,
.reason-section h3 {
  color: #333;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
}

.counselor-card {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

.counselor-info {
  display: flex;
  gap: 20px;
  align-items: center;
}

.avatar img {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e9ecef;
}

.details h4 {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 10px;
}

.details p {
  margin: 8px 0;
  color: #666;
  font-size: 16px;
}

.time-slots {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.time-slot {
  background: #f8f9fa;
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
}

.date {
  font-weight: 600;
  color: #333;
}

.time {
  color: #666;
}

.reason-content {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  font-size: 16px;
  color: #333;
}

.time-selection-section {
  background: white;
  padding: 25px;
  margin-bottom: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.time-selection-section h3 {
  color: #333;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
}

.time-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.time-option {
  background: #f8f9fa;
  padding: 15px 20px;
  border-radius: 8px;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all 0.3s;
}

.time-option:hover {
  background: #e9ecef;
  border-color: #dee2e6;
}

.time-option.selected {
  background: #e3f2fd;
  border-color: #2196F3;
}

.time-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.time-info .date {
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.time-info .time {
  color: #666;
  font-size: 16px;
}

.time-id {
  font-size: 12px;
  color: #999;
}

.actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding: 25px;
}

.cancel-btn,
.confirm-btn {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
}

.confirm-btn {
  background: #28a745;
  color: white;
}

.confirm-btn:hover:not(:disabled) {
  background: #218838;
}

.confirm-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .counselor-info {
    flex-direction: column;
    text-align: center;
  }
  
  .time-slot {
    flex-direction: column;
    gap: 5px;
    text-align: center;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style>
