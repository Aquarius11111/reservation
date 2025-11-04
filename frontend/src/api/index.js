/**
 * API接口封装文件
 * 包含所有后端接口的统一管理
 */
import axios from 'axios'

// 创建axios实例
const apiClient = axios.create({
  // baseURL: 'http://192.168.43.187:9090',
  baseURL: 'https://m1.apifoxmock.com/m1/7202211-6928258-6287865',
  timeout: 10000, // 10秒超时
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等认证信息
    // const token = localStorage.getItem('token')
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`
    // }
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    // 直接返回响应数据
    return {
      success: true,
      data: response.data,
      status: response.status
    }
  },
  (error) => {
    console.error('API请求失败:', error)
    
    // 处理错误响应
    if (error.response) {
      // 服务器返回了错误状态码
      return {
        success: false,
        error: error.response.data?.message || `HTTP error! status: ${error.response.status}`,
        data: error.response.data,
        status: error.response.status
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      return {
        success: false,
        error: '网络错误，请检查网络连接',
        data: null
      }
    } else {
      // 其他错误
      return {
        success: false,
        error: error.message || '请求失败',
        data: null
      }
    }
  }
)

/**
 * 通用的API请求函数
 * @param {string} url 请求路径
 * @param {Object} options 请求选项
 * @returns {Promise} API响应
 */
export async function request(url, options = {}) {
  const { method = 'GET', data, params, ...restOptions } = options
  
  const response = await apiClient.request({
    url,
    method,
    data,
    params,
    ...restOptions
  }).catch(error => {
    // 错误已经在拦截器中处理并返回对象
    return error
  })
  
  return response
}

/**
 * 问卷相关API接口
 */
export const surveyAPI = {
  /**
   * 获取最近测评时间
   * @param {string} studentId 学生ID
   */
  getLatestAssessmentTime(studentId) {
    return request('/api/survey/latest', {
      method: 'GET',
      params: { studentId }
    })
  },

  /**
   * 提交首次问卷（包含基础信息 + SCL-90）
   * @param {Object} data 问卷数据
   */
  submitFirstSurvey(data) {
    return request('/api/survey/first', {
      method: 'POST',
      data
    })
  },

  /**
   * 提交重测问卷（仅SCL-90）
   * @param {Object} data 问卷数据
   */
  submitRetestSurvey(data) {
    return request('/api/survey/retest', {
      method: 'POST',
      data
    })
  },

  /**
   * 获取学生的测评结果
   * @param {string} studentId 学生ID
   */
  getSurveyResult(studentId) {
    return request(`/api/survey/result/${studentId}`, {
      method: 'GET'
    })
  },

  /**
   * 获取测评结果详情
   * @param {Object} params 查询参数：{ studentId, resultId }
   */
  getSurveyResultDetail(params) {
    return request('/api/survey/result/detail', {
    method: 'GET',
      params
    })
  }
}

/**
 * 学生信息相关API接口
 */
export const studentAPI = {
  /**
   * 获取学生基本信息
   * @param {string} studentId 学生ID
   */
  getStudentInfo(studentId) {
    return request(`/api/student/info/${studentId}`, {
      method: 'GET'
    })
  },

  /**
   * 更新学生信息
   * @param {string} studentId 学生ID
   * @param {Object} studentInfo 学生信息
   */
  updateStudentInfo(studentId, studentInfo) {
    return request(`/api/student/info/${studentId}`, {
      method: 'PUT',
      data: studentInfo
    })
  },

  /**
   * 获取学生名单
   * @param {Object} params 查询参数
   */
  getStudentList(params = {}) {
    return request('/api/student/list', {
      method: 'GET',
      params
    })
  }
}

/**
 * 预约相关API接口
 */
export const reservationAPI = {
  /**
   * 匹配推荐咨询师
   * @param {Object} matchData 匹配数据：selectedTimeList, consultReason, pageNum, pageSize
   */
  matchCounselor(matchData) {
    return request('/api/reserve/match', {
      method: 'POST',
      data: matchData
    })
  },

  /**
   * 创建预约
   * @param {Object} reservationData 预约数据
   */
  createReservation(reservationData) {
    return request('/api/reserve/create', {
      method: 'POST',
      data: reservationData
    })
  },

  /**
   * 获取预约列表
   * @param {string} studentId 学生ID
   */
  getReservationList(studentId) {
    return request('/api/reserve/student/list', {
      method: 'GET',
      params: { studentId }
    })
  },

  /**
   * 取消预约
   * @param {Object} cancelData 取消预约数据 {reserveId, cancelReason}
   */
  cancelReservation(cancelData) {
    return request('/api/reserve/cancel', {
      method: 'POST',
      data: cancelData
    })
  },

  /**
   * 完成预约
   * @param {string} reservationId 预约ID
   */
  completeReservation(reservationId) {
    return request(`/api/reservation/${reservationId}/complete`, {
      method: 'POST'
    })
  },

  /**
   * 获取咨询师预约列表
   * @param {Object} params 查询参数：counselorId, reserveDate, reserveStatus, pageNum, pageSize
   */
  getCounselorReservationList(params) {
    return request('/api/reserve/counselor/list', {
      method: 'GET',
      params: params
    })
  },

  /**
   * 标记咨询完成
   * @param {Object} data 完成数据：{reserveId, counselorId}
   */
  markCompleted(data) {
    return request('/api/reserve/counselor/markCompleted', {
      method: 'PUT',
      data: data
    })
  },

  /**
   * 标记学生爽约
   * @param {Object} data 爽约数据：{reserveId, counselorId}
   */
  markNoShow(data) {
    return request('/api/reserve/counselor/markNoShow', {
      method: 'PUT',
      data: data
    })
  },

  /**
   * 创建咨询记录
   * @param {Object} data 咨询记录数据：{reserveId, counselorId, consultDate, studentPerformance, consultContent, advicePlan}
   */
  createConsultRecord(data) {
    return request('/api/reserve/counselor/createConsultRecord', {
      method: 'POST',
      data: data
    })
  },

  /**
   * 获取咨询师未填写的记录列表
   * @param {Object} params 查询参数：{counselorId, startDate?, endDate?}
   */
  getPendingConsultRecords(params) {
    return request('/api/reserve/counselor/unfilled', {
      method: 'GET',
      params: params
    })
  },

  /**
   * 获取咨询记录列表
   * @param {Object} params 查询参数：{counselorId, pageNum, pageSize, startDate?, endDate?, studentName?}
   */
  getConsultRecords(params) {
    return request('/api/reserve/counselor/records', {
      method: 'GET',
      params: params
    })
  },

  /**
   * 更新咨询记录
   * @param {Object} data 咨询记录数据
   */
  updateConsultRecord(data) {
    return request('/api/reserve/counselor/updateConsultRecord', {
      method: 'PUT',
      data: data
    })
  }
}

/**
 * 咨询师相关API接口
 */
export const counselorAPI = {
  /**
   * 获取咨询师列表
   * @param {Object} params 查询参数
   */
  getCounselorList(params = {}) {
    return request('/api/counselor/list', {
      method: 'GET',
      params
    })
  },

  /**
   * 获取咨询师详情
   * @param {string} counselorId 咨询师ID
   */
  getCounselorDetail(counselorId) {
    return request(`/api/counselor/${counselorId}`, {
      method: 'GET'
    })
  },

  /**
   * 获取咨询师可预约时间
   * @param {string} counselorId 咨询师ID
   * @param {string} date 预约日期
   */
  getAvailableTimeSlots(counselorId, date) {
    return request(`/api/counselor/${counselorId}/available-slots`, {
      method: 'GET',
      params: { date }
    })
  }
}

/**
 * 系统相关API接口
 */
export const systemAPI = {
  /**
   * 用户登录
   * @param {Object} credentials 登录凭证
   */
  login(credentials) {
    return request('/api/login', {
      method: 'POST',
      data: credentials
    })
  },

  /**
   * 用户登出
   */
  logout() {
    return request('/api/auth/logout', {
      method: 'POST'
    })
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser() {
    return request('/api/auth/me', {
      method: 'GET'
    })
  },

  /**
   * 健康检查
   */
  healthCheck() {
    return request('/api/health', {
      method: 'GET'
    })
  },

  /**
   * 修改密码
   * @param {Object} params 参数：{userId, oldPassword, newPassword}
   */
  changePassword(params) {
    return request('/sysUser/changePassword', {
      method: 'PUT',
      params: params
    })
  }

}

/**
 * 咨询师信息相关API接口
 */
export const counselorInfoAPI = {
  /**
   * 获取咨询师信息
   * @param {string} counselorId 咨询师ID
   */
  getCounselorInfo(counselorId) {
    return request(`/counselor/info/get/${counselorId}`, {
      method: 'GET'
    })
  },

  /**
   * 分页查询咨询师列表
   * @param {Object} params 查询参数：{ pageNum, pageSize, counselorId(可选) }
   */
  getCounselorList(params) {
    return request('/counselor/info/page', {
      method: 'GET',
      params: params
    })
  },

  /**
   * 更新咨询师信息
   * @param {Object} counselorInfo 咨询师信息
   */
  updateCounselorInfo(counselorInfo) {
    return request('/counselor/info/update', {
      method: 'PUT',
      data: counselorInfo
    })
  },

  /**
   * 删除咨询师
   * @param {string} counselorId 咨询师ID
   */
  deleteCounselor(counselorId) {
    return request(`/counselor/info/delete/${counselorId}`, {
      method: 'DELETE'
    })
  },

  /**
   * 添加咨询师
   * @param {Object} counselorInfo 咨询师信息
   */
  addCounselor(counselorInfo) {
    return request('/counselor/info/add', {
      method: 'POST',
      data: counselorInfo
    })
  }
}

/**
 * 统计报表相关API接口
 */
export const reportAPI = {
  /**
   * 获取问卷统计数据
   * @param {Object} params 查询参数
   */
  getSurveyStatistics(params = {}) {
    return request('/api/report/survey-statistics', {
      method: 'GET',
      params
    })
  },

  /**
   * 获取预约统计数据
   * @param {Object} params 查询参数
   */
  getReservationStatistics(params = {}) {
    return request('/api/report/reservation-statistics', {
      method: 'GET',
      params
    })
  },

  /**
   * 导出Excel报告
   * @param {Object} params 导出参数
   */
  exportReport(params = {}) {
    return request('/api/report/export', {
      method: 'GET',
      params
    })
  }
}

/**
 * 排班管理相关API接口
 */
export const scheduleAPI = {
  /**
   * 添加排班
   * @param {Object}  params scheduleData 排班数据：{counselorId, reserveDate, startTime, endTime}
   */
  addSchedule(scheduleData) {
    return request('/reserveTimeSlot/add', {
      method: 'POST',
      params: scheduleData
    })
  },

  /**
   * 获取排班列表
   * @param {Object} params 查询参数：{counselorId?, pageNum?, pageSize?}（均为可选）
   */
  getScheduleList(params) {
    return request('/reserveTimeSlot/listByCounselor', {
      method: 'GET',
      params
    })
  },

  /**
   * 更新排班
   * @param {Object} scheduleData 排班数据：{reserveTimeId, counselorId, reserveDate, startTime, endTime, isOccupied, studentId}
   */
  updateSchedule(scheduleData) {
    return request('/reserveTimeSlot/update', {
      method: 'PUT',
      data: scheduleData
    })
  },

  /**
   * 获取未来7天排班列表
   * @param {Object} params 查询参数：{currentDate}
   */
  getNext7DaysSchedule(params) {
    return request('/reserveTimeSlot/listNext7Days', {
      method: 'GET',
      params
    })
  },

  /**
   * 删除排班
   * @param {number} reserveTimeId 排班ID
   */
  deleteSchedule(reserveTimeId) {
    return request('/reserveTimeSlot/delete', {
      method: 'DELETE',
      data: [reserveTimeId]
    })
  }
}

/**
 * 通用API工具函数
 */
export const apiUtils = {
  /**
   * 构建问卷提交数据结构
   * @param {Object} basicSurvey 基础信息
   * @param {Array} answers SCL-90答案
   * @param {string} studentId 学生ID
   */
  buildSurveyData(basicSurvey, answers, studentId) {
    return {
      basicSurvey: {
        studentId,
        ...basicSurvey
      },
      scl90Assessment: {
        studentId,
        answers: Array.isArray(answers) ? answers : []
      }
    }
  },

  /**
   * 构建重测问卷提交数据结构
   * @param {Array} answers SCL-90答案
   * @param {string} studentId 学生ID
   */
  buildRetestSurveyData(answers, studentId) {
    return {
      scl90Assessment: {
        studentId,
        answers: Array.isArray(answers) ? answers : []
      }
    }
  },

  /**
   * 验证SCL-90答案格式
   * @param {Array} answers 答案数组
   */
  validateAnswers(answers) {
    if (!Array.isArray(answers)) return false
    if (answers.length !== 90) return false
    
    return answers.every(answer => 
      typeof answer === 'number' && 
      answer >= 0 && 
      answer <= 5
    )
  },

  /**
   * 格式化日期为ISO字符串
   * @param {Date} date 日期对象
   */
  formatDateToISO(date) {
    if (!date) return null
    return new Date(date).toISOString()
  },

  /**
   * 解析API响应中的错误信息
   * @param {Object} response API响应
   */
  getErrorMessage(response) {
    if (response.success) return null
    
    if (response.error) return response.error
    
    if (response.data && response.data.message) {
      return response.data.message
    }
    
    return '请求失败，请稍后重试'
  }
}

// 默认导出所有API
export default {
  surveyAPI,
  studentAPI,
  reservationAPI,
  counselorAPI,
  systemAPI,
  reportAPI,
  scheduleAPI,
  apiUtils,
  request
}
