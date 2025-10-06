import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useReservationStore = defineStore('reservation', () => {
  // 预约数据状态
  const reservationData = ref({
    counselorId: null,
    consultReason: '',
    selectedTimes: [],
    counselorInfo: null
  })

  // 设置预约数据
  function setReservationData(data) {
    reservationData.value = {
      counselorId: data.counselorId,
      consultReason: data.consultReason,
      selectedTimes: data.selectedTimes || [],
      counselorInfo: data.counselorInfo
    }
  }

  // 获取预约数据
  function getReservationData() {
    return reservationData.value
  }

  // 清理预约数据
  function clearReservationData() {
    reservationData.value = {
      counselorId: null,
      consultReason: '',
      selectedTimes: [],
      counselorInfo: null
    }
  }

  // 检查是否有预约数据
  function hasReservationData() {
    return reservationData.value.counselorId && reservationData.value.counselorInfo
  }

  return {
    reservationData,
    setReservationData,
    getReservationData,
    clearReservationData,
    hasReservationData
  }
})
