<template>
  <div class="report-detail-container">
    <div class="card summary-card">
      <h2 class="title">SCL-90症状自评量表</h2>
      <p class="subtitle">测试结果仅供参考，不作诊断使用</p>

      <div class="total-wrap">
        <div class="total-label">您的SCL-90总分</div>
        <div class="total-score">{{ detail.totalScore }}</div>
        <div class="avg">单项平均分：{{ formatNumber(detail.totalAvg) }}</div>
        <div class="extra-row">
          <span class="extra-label">阳性项目数：</span>
          <span class="extra-value">{{ detail.positiveItemNum }}</span>
        </div>
        <div class="extra-row">
          <span class="extra-label">阳性症状均分：</span>
          <span class="extra-value">{{ formatNumber(detail.positiveAvg) }}</span>
        </div>
        <div class="warn-tags" v-if="warnings.length">
          <span v-for="(w, i) in warnings" :key="i" class="warn-tag">{{ w }}</span>
        </div>
      </div>
    </div>

    <div class="card factors-card">
      <h3 class="section-title">各因子得分及测试情况</h3>
      <table class="factors-table">
        <thead>
          <tr>
            <th>因子名称</th>
            <th>总分</th>
            <th>平均分</th>
            <th>测试情况</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in factorRows" :key="row.key">
            <td>{{ row.name }}</td>
            <td>{{ row.score }}</td>
            <td>{{ row.avg }}</td>
            <td>
              <span :class="['level-tag', row.levelClass]">{{ row.levelText }}</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="card text-card">
      <h3 class="section-title">结果分析</h3>
      <pre class="text-block">{{ detail.analysis }}</pre>
    </div>

    <div class="card text-card">
      <h3 class="section-title">专业建议</h3>
      <pre class="text-block">{{ detail.suggestion }}</pre>
    </div>

    <!-- 返回首页按钮（与 ReservationList 保持一致） -->
    <div class="back-container">
      <router-link to="/home" class="back-button">
        ← 返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { surveyAPI } from '../api/index.js'

const route = useRoute()
const router = useRouter()
const resultId = route.params.id

const loading = ref(false)
const detail = ref({
  resultId: Number(resultId) || undefined,
  assessmentId: undefined,
  studentId: '',
  totalScore: 0,
  totalAvg: 0,
  positiveItemNum: 0,
  positiveAvg: 0,
  somatizationScore: 0,
  obsessiveScore: 0,
  interpersonalScore: 0,
  depressionScore: 0,
  anxietyScore: 0,
  hostilityScore: 0,
  phobicScore: 0,
  paranoidScore: 0,
  psychoticScore: 0,
  otherScore: 0,
  createdAt: '',
  analysis: '',
  suggestion: ''
})

const getUserInfo = () => {
  const s = localStorage.getItem('userInfo')
  if (!s) return null
  try { return JSON.parse(s) } catch { return null }
}

const fetchDetail = async () => {
  const user = getUserInfo()
  if (!user || !user.userId) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  loading.value = true
  try {
    const resp = await surveyAPI.getSurveyResultDetail({ studentId: user.userId, resultId })
    if (resp.success) {
      // API直接返回详情对象，包含所有字段
      const data = resp.data
      if (data && data.resultId) {
        detail.value = {
          resultId: data.resultId || Number(resultId),
          assessmentId: data.assessmentId,
          studentId: data.studentId || '',
          totalScore: data.totalScore || 0,
          totalAvg: data.totalAvg || 0,
          positiveItemNum: data.positiveItemNum || 0,
          positiveAvg: data.positiveAvg || 0,
          somatizationScore: data.somatizationScore || 0,
          obsessiveScore: data.obsessiveScore || 0,
          interpersonalScore: data.interpersonalScore || 0,
          depressionScore: data.depressionScore || 0,
          anxietyScore: data.anxietyScore || 0,
          hostilityScore: data.hostilityScore || 0,
          phobicScore: data.phobicScore || 0,
          paranoidScore: data.paranoidScore || 0,
          psychoticScore: data.psychoticScore || 0,
          otherScore: data.otherScore || 0,
          createdAt: data.createdAt || '',
          analysis: data.analysis || '',
          suggestion: data.suggestion || ''
        }
      } else {
        ElMessage.error('获取报告详情失败')
      }
    } else {
      ElMessage.error(resp.error || '获取报告详情失败')
    }
  } catch (e) {
    console.error('获取报告详情错误', e)
    ElMessage.error('获取报告详情失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
onMounted(fetchDetail)

// SCL-90各因子对应的题目数量
const factorMeta = [
  { key: 'somatizationScore', name: '躯体化', items: 12 },
  { key: 'obsessiveScore', name: '强迫症状', items: 10 },
  { key: 'interpersonalScore', name: '人际关系敏感', items: 9 },
  { key: 'depressionScore', name: '抑郁症状', items: 13 },
  { key: 'anxietyScore', name: '焦虑症状', items: 10 },
  { key: 'hostilityScore', name: '敌对症状', items: 6 },
  { key: 'phobicScore', name: '恐怖症状', items: 7 },
  { key: 'paranoidScore', name: '偏执症状', items: 6 },
  { key: 'psychoticScore', name: '精神病性', items: 10 },
  { key: 'otherScore', name: '其他', items: 7 }
]

// 评分规则（按中国常模单项均分）：
// 单项均分 > 2 建议进一步检查；2-2.9 轻度；3-3.8 中度；≥3.9 重度
function levelByAvg(avg) {
  if (avg >= 3.9) return { levelText: '重度', levelClass: 'level-high' }
  if (avg >= 3.0) return { levelText: '中度', levelClass: 'level-mid' }
  if (avg >= 2.0) return { levelText: '轻度', levelClass: 'level-low' }
  return { levelText: '正常', levelClass: 'level-normal' }
}

const factorRows = computed(() => {
  return factorMeta.map(m => {
    const score = detail.value[m.key] || 0
    const items = m.items || 1
    const avgNum = items > 0 ? score / items : 0
    const avg = avgNum.toFixed(2)
    const { levelText, levelClass } = levelByAvg(avgNum)
    return { key: m.key, name: m.name, score, avg, levelText, levelClass }
  }).filter(row => row.score > 0) // 只显示有得分的因子
})

// 顶部风险提示
const warnings = computed(() => {
  const list = []
  if (detail.value.totalScore > 160) {
    list.push('总分提示：存在阳性症状')
  }
  if (detail.value.positiveItemNum > 43) {
    list.push('阳性项目数提示：建议考虑筛查阳性')
  }
  if (detail.value.totalAvg > 2) {
    list.push('单项均分提示：建议做进一步检查')
  }
  return list
})

const formatNumber = (n) => (n == null ? '-' : Number(n).toFixed(2))

</script>

<style scoped>
.report-detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
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

.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0,0,0,.08);
  padding: 18px;
  margin-bottom: 18px;
}

.summary-card {
  border-top: 6px solid #69c27f;
  text-align: center;
}
.title {
  margin: 0 0 4px;
  font-size: 1.4rem;
  color: #2c3e50;
}
.subtitle {
  margin: 0 0 16px;
  color: #6c757d;
}
.total-wrap {
  background: #f6faf7;
  border-radius: 12px;
  padding: 20px 10px;
}
.total-label { color:#6c757d; margin-bottom:6px; }
.total-score { color:#2ecc71; font-size: 2.2rem; font-weight: 800; }
.avg { color:#6c757d; margin-top: 6px; }
.extra-row { margin-top: 6px; color:#6c757d; }
.extra-label { opacity: .9; }
.extra-value { margin-left: 4px; }
.warn-tags { margin-top: 10px; display: flex; flex-wrap: wrap; gap: 8px; justify-content: center; }
.warn-tag { background:#fff8e1; color:#b36b00; padding: 4px 10px; border-radius: 12px; font-weight: 600; font-size: .85rem; }

.section-title {
  margin: 0 0 12px;
  color: #2c3e50;
}

.factors-table { width: 100%; border-collapse: collapse; }
.factors-table thead th {
  background: #f8fafc; color:#2c3e50; text-align:left; padding: 12px; border-bottom: 2px solid #e9ecef;
}
.factors-table tbody td { padding: 12px; border-bottom: 1px solid #f1f3f5; }

.level-tag { padding: 4px 10px; border-radius: 12px; font-size: .85rem; font-weight: 600; }
.level-normal { background:#e9f7ef; color:#1e7e34; }
.level-low { background:#e8f5e9; color:#2e7d32; }
.level-mid { background:#fff4e5; color:#b36b00; }
.level-high { background:#fdecea; color:#c0392b; }

.text-card .text-block {
  font-size: 1rem;
  white-space: pre-wrap;
  color:#495057;
  line-height: 1.6;
}
</style>


