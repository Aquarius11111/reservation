<template>
  <div class="reservation-form">
    <div class="container">
      <h2 class="form-title">心理咨询预约</h2>
      
      <!-- 时间段选择 - 使用Element Plus组件 -->
      <div class="time-selection-section">
        <h3 class="section-title">选择预约时间段（最多可选择5个，每次咨询1小时）</h3>
        <p class="time-tips">📅 开放时间：周一至周五工作日</p>
        <p class="time-tips">⏰ 预约时间：9:00-17:00 每整点时段</p>
        <div class="time-input-group">
          <div class="date-time-input" v-for="(timeSlot, index) in selectedTimeSlots" :key="index">
            <div class="time-slot">
              <label class="time-label">日期{{ index + 1 }}</label>
              <el-date-picker
                v-model="timeSlot.reserveDate"
                type="date"
                placeholder="选择日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                :disabled-date="disabledDate"
                @change="updateEndTime(index)"
                :clearable="true"
                style="width: 100%"
              />
            </div>
            
            <div class="time-slot">
              <label class="time-label">开始时间</label>
              <el-time-picker
                v-model="timeSlot.startTime"
                placeholder="开始时间"
                format="HH:mm"
                value-format="HH:mm:ss"
                :disabled-hours="disabledHours"
                :disabled-minutes="disabledMinutes"
                @change="updateEndTime(index)"
                :clearable="true"
                style="width: 100%"
              />
            </div>
            
            <div class="time-slot">
              <label class="time-label">结束时间</label>
              <el-time-picker
                v-model="timeSlot.endTime"
                placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm:ss"
                :disabled="true"
                :clearable="true"
                style="width: 100%"
              />
            </div>
            
            <div class="time-slot remove-slot">
              <el-button 
                v-if="selectedTimeSlots.length > 1"
                type="danger"
                :icon="Delete"
                size="small"
                @click="removeTimeSlot(index)"
              >
                删除
              </el-button>
            </div>
          </div>
          
          <!-- 添加时间段按钮 -->
          <el-button 
            v-if="selectedTimeSlots.length < 5"
            type="primary"
            :icon="Plus"
            @click="addTimeSlot"
            class="add-time-btn"
          >
            添加时间段 ({{ selectedTimeSlots.length }}/5)
          </el-button>
        </div>
      </div>

      <!-- 咨询原因 -->
      <div class="consult-reason-section">
        <h3 class="section-title">咨询原因</h3>
        <select v-model="consultReason" class="reason-select">
          <option value="">请选择咨询原因</option>
          <option value="学业压力">学业压力</option>
          <option value="人际关系">人际关系</option>
          <option value="情感困扰">情感困扰</option>
          <option value="家庭问题">家庭问题</option>
          <option value="职业规划">职业规划</option>
          <option value="焦虑情绪">焦虑情绪</option>
          <option value="抑郁情绪">抑郁情绪</option>
          <option value="睡眠问题">睡眠问题</option>
          <option value="其他">其他</option>
        </select>
      </div>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <button 
          type="submit" 
          class="submit-btn"
          @click="submitReservation"
          :disabled="!canSubmit || loading"
        >
          {{ loading ? '匹配中...' : '匹配咨询师' }}
        </button>
      </div>
    </div>

    <!-- 匹配结果 -->
    <div v-if="showResults" class="match-results">
      <div class="container">
        <h3 class="results-title">推荐咨询师</h3>
        
        <div v-if="matchResults.length === 0" class="no-results">
          <p>暂无可匹配的咨询师，请尝试选择其他时间段。</p>
        </div>
        
        <div v-else class="counselor-list">
          <div 
            v-for="counselor in matchResults" 
            :key="counselor.counselorId" 
            class="counselor-card"
          >
            <div class="counselor-info">
              <div class="avatar">
                <span class="avatar-initial">{{ getCounselorInitial(counselor.counselorName) }}</span>
              </div>
              <div class="details">
                <h4 class="counselor-name">{{ counselor.counselorName }}</h4>
                <p class="title">{{ counselor.title }}</p>
                <p class="specialty">专业方向：{{ counselor.specialty }}</p>
                <p class="orientation">咨询取向：{{ counselor.orientation }}</p>
                <p class="location">咨询地点：{{ counselor.location }}</p>
              </div>
            </div>
            
            <div class="matched-times">
              <h5>可预约时间：</h5>
              <ul class="time-list">
                <li 
                  v-for="timeItem in counselor.matchedTimeList" 
                  :key="`${timeItem.reserveDate}-${timeItem.startTime}`"
                  class="matched-time"
                >
                  {{ timeItem.reserveDate }} {{ timeItem.startTime.substring(0,5) }}-{{ timeItem.endTime.substring(0,5) }}
                </li>
              </ul>
            </div>
            
            <div class="actions">
              <button 
                class="select-btn"
                @click="selectCounselor(counselor)"
              >
                选择咨询师
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 返回首页按钮 -->
    <div class="back-container">
      <router-link to="/home" class="back-button">← 返回首页</router-link>
    </div>
  </div>
</template>

<script>
import { reservationAPI } from '../api/index.js'
import { Delete, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useReservationStore } from '@/stores/reservation'

export default {
  name: 'ReservationForm',
  components: {
    Delete, Plus
  },
  data() {
    return {
      selectedTimeSlots: [
        {
          reserveDate: '',
          startTime: '',
          endTime: ''
        }
      ],
      consultReason: '',
      loading: false,
      matchResults: [],
      showResults: false,
      todayDate: new Date().toISOString().split('T')[0]
    }
  },
  computed: {
    canSubmit() {
      // 检查是否有有效的时间段和咨询服务原因
      const hasValidTimeSlots = this.selectedTimeSlots.some(slot => 
        slot.reserveDate && slot.startTime && slot.endTime
      )
      return hasValidTimeSlots && this.consultReason.trim() !== ''
    }
  },
  methods: {
    // 禁用过去的日期和周末 (只允许周一到周五)
    disabledDate(time) {
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
    },
    
    // 禁用不在工作时间的小时 (工作时间: 8:00-12:00,14:00-17:00)
    // 允许的开始时间：8, 9, 10, 11, 14, 15, 16（17点是结束时间，不能作为开始时间）
    disabledHours() {
      return [0, 1, 2, 3, 4, 5, 6, 7, 12, 13, 17, 18, 19, 20, 21, 22, 23]
    },
    
    // 禁用非准点分钟 (只能选择整点)
    disabledMinutes(selectedHour) {
      return Array.from({length: 60}, (_, i) => i).filter(minute => minute !== 0)
    },
    
    // 根据开始时间自动计算结束时间 (1小时后)
    updateEndTime(index) {
      const slot = this.selectedTimeSlots[index]
      if (slot.startTime) {
        const start = new Date(`2000-01-01 ${slot.startTime}`)
        start.setHours(start.getHours() + 1) // 加1小时
        slot.endTime = start.toTimeString().substring(0, 8) // HH:mm:ss格式
      } else {
        slot.endTime = ''
      }
    },
    
    addTimeSlot() {
      if (this.selectedTimeSlots.length < 5) {
        this.selectedTimeSlots.push({
          reserveDate: '',
          startTime: '',
          endTime: ''
        })
      }
    },
    
    removeTimeSlot(index) {
      if (this.selectedTimeSlots.length > 1) {
        this.selectedTimeSlots.splice(index, 1)
      }
    },
    
    // 格式化时间，确保秒数为00
    formatTimeForAPI(timeString) {
      if (!timeString) return ''
      
      // 正则匹配HH:mm:ss格式，提取小时和分钟，秒数设为00
      const timeMatch = timeString.match(/(\d{1,2}):(\d{2})(:\d{2})?/)
      if (timeMatch) {
        const hours = timeMatch[1].padStart(2, '0')
        const minutes = timeMatch[2]
        return `${hours}:${minutes}:00`
      }
      
      return timeString
    },
    
    async submitReservation() {
      if (!this.canSubmit) {
        ElMessage.error('请完善预约信息')
        return
      }
      
      this.loading = true
      
      try {
        // 构建请求数据
        const requestData = {
          selectedTimeList: this.selectedTimeSlots.filter(slot => 
            slot.reserveDate && slot.startTime && slot.endTime
          ).map(slot => ({
            reserveDate: slot.reserveDate,
            startTime: this.formatTimeForAPI(slot.startTime),
            endTime: this.formatTimeForAPI(slot.endTime)
          })),
          consultReason: this.consultReason,
          pageNum: 1,
          pageSize: 10
        }
        
        console.log('发送请求数据:', requestData)
        
        // 调用API
        const response = await reservationAPI.matchCounselor(requestData)
        
        if (response.success && response.data.code === 200) {
          // 根据实际API返回结构解析数据
          this.matchResults = response.data.data?.list || []
          console.log('解析到的咨询师列表:', this.matchResults)
          this.showResults = true
          ElMessage.success(response.data.message || '匹配成功')
        } else {
          console.error('匹配失败:', response)
          ElMessage.error(response.data?.message || '匹配失败，请重试')
        }
      } catch (error) {
        console.error('请求失败:', error)
        ElMessage.error('连接失败，请检查网络后重试')
      } finally {
        this.loading = false
      }
    },
    
    selectCounselor(counselor) {
      // 跳转到预约确认页面或直接创建预约
      const selectedTimes = this.selectedTimeSlots.filter(slot => 
        slot.reserveDate && slot.startTime && slot.endTime
      ).map(slot => ({
        reserveDate: slot.reserveDate,
        startTime: this.formatTimeForAPI(slot.startTime),
        endTime: this.formatTimeForAPI(slot.endTime)
      }))
      
      // 将数据存储到Pinia store中
      const reservationStore = useReservationStore()
      reservationStore.setReservationData({
        counselorId: counselor.counselorId,
        consultReason: this.consultReason,
        selectedTimes: selectedTimes,
        counselorInfo: counselor
      })
      
      this.$router.push({
        name: 'confirmReservation',
        params: {
          counselorId: counselor.counselorId
        }
      })
    },

    getCounselorInitial(name) {
      const text = (name || '').trim()
      return text ? text.charAt(0) : '咨'
    }
  }
}
</script>

<style scoped>
.reservation-form {
  padding: 20px 0;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.back-container { text-align: center; margin-top: 20px; }
.back-button {
  display: inline-block;
  padding: 12px 24px;
  background: #6c757d;
  color: #fff;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
  transition: background .3s ease;
}
.back-button:hover { background: #5a6268; }

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.form-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 28px;
  font-weight: 600;
}

.section-title {
  color: #555;
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: 500;
}

.time-tips {
  color: #999;
  margin-bottom: 10px;
  font-size: 14px;
  line-height: 1.5;
}

.time-selection-section,
.consult-reason-section,
.submit-section {
  background: white;
  padding: 25px;
  margin-bottom: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.time-input-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.date-time-input {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: end;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  position: relative;
}

.time-slot {
  display: flex;
  flex-direction: column;
  gap: 5px;
  min-width: 120px;
}

.time-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.remove-slot {
  align-items: center;
  justify-content: center;
}

.add-time-btn {
  margin-top: 10px;
  width: 280px;
}

.reason-select {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  background: white;
  transition: border-color 0.3s;
}

.reason-select:focus {
  outline: none;
  border-color: #4CAF50;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background: #2196F3;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background: #1976D2;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.match-results {
  margin-top: 30px;
  padding: 20px 0;
}

.results-title {
  text-align: center;
  color: #333;
  margin-bottom: 25px;
  font-size: 24px;
  font-weight: 600;
}

.no-results {
  text-align: center;
  padding: 40px;
  color: #666;
  background: white;
  border-radius: 10px;
}

.counselor-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.counselor-card {
  background: white;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.counselor-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.15);
}

.counselor-info {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.avatar {
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid #e9ecef;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f6f8;
}

.avatar-initial {
  font-size: 2rem;
  font-weight: 600;
  color: #4a5568;
}

.details {
  flex: 1;
}

.counselor-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.title,
.specialty,
.orientation,
.location {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.matched-times {
  margin-bottom: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.matched-times h5 {
  margin: 0 0 10px 0;
  color: #555;
  font-size: 16px;
}

.time-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.matched-time {
  padding: 5px 10px;
  margin: 5px 0;
  background: white;
  border-radius: 5px;
  color: #333;
  font-size: 14px;
}

.actions {
  text-align: right;
}

.select-btn {
  padding: 10px 25px;
  background: #FF9800;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: background-color 0.3s;
}

.select-btn:hover {
  background: #F57C00;
}

@media (max-width: 768px) {
  .container {
    padding: 0 15px;
  }
  
  .date-time-input {
    flex-direction: column;
    align-items: stretch;
  }
  
  .time-slot {
    min-width: unset;
  }
  
  .counselor-info {
    flex-direction: column;
    text-align: center;
  }
  
  .avatar {
    align-self: center;
  }
  
  .add-time-btn {
    width: 100%;
  }
}
</style>