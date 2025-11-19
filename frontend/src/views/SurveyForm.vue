<template>
  <div class="survey-container">
    <div class="survey-header">
      <h1 class="survey-title">心理健康问卷</h1>
      <p class="survey-description">请根据您的真实感受填写以下问卷，这将帮助我们更好地了解您的心理状态</p>
    </div>

    <!-- 填写状态提示（基于最新测评时间自动判断） -->
    <div class="fill-status" v-if="!isLoading">
      <span v-if="isFirstFill" class="badge first">首次填写</span>
      <span v-else class="badge retest">再次填写</span>
      <span v-if="!isFirstFill && latestAssessmentTime" class="last-time">上次测评时间：{{ formatDate(latestAssessmentTime) }}</span>
    </div>
    <div class="fill-status loading" v-else>
      加载测评状态中...
    </div>

    <div class="survey-form">
      <form @submit.prevent="submitSurvey">
        <!-- 首次填写需要的基础信息 -->
        <div class="first-info-section" v-if="!isLoading && isFirstFill && currentStep === 1">
          <h2 class="section-title">基础信息</h2>
          <div class="first-info-grid">
            <!-- 1. 姓名 -->
            <div class="form-item">
              <label>姓名<span class="required">*</span></label>
              <input type="text" v-model="basicSurvey.name" placeholder="请输入姓名" />
            </div>

            <!-- 2. 性别 -->
            <div class="form-item">
              <label>性别<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="male" v-model="basicSurvey.gender" /> 男</label>
                <label><input type="radio" value="female" v-model="basicSurvey.gender" /> 女</label>
                <label><input type="radio" value="other" v-model="basicSurvey.gender" /> 其他（LGBTQ等性少数群体）</label>
              </div>
            </div>

            <!-- 3. 年龄 -->
            <div class="form-item">
              <label>年龄<span class="required">*</span></label>
              <input type="number" v-model="basicSurvey.age" placeholder="请输入年龄" min="1" max="120" />
            </div>

            <!-- 4. 民族 -->
            <div class="form-item">
              <label>民族<span class="required">*</span></label>
              <select v-model="basicSurvey.ethnicity">
                <option value="">请选择民族</option>
                <option v-for="ethnic in ethnicities" :key="ethnic" :value="ethnic">{{ ethnic }}</option>
              </select>
            </div>

            <!-- 5. 生源地 -->
            <div class="form-item">
              <label>生源地<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="urban" v-model="basicSurvey.birthplace" /> 城市</label>
                <label><input type="radio" value="rural" v-model="basicSurvey.birthplace" /> 农村</label>
              </div>
            </div>

            <!-- 6. 宗教信仰 -->
            <div class="form-item">
              <label>宗教信仰<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="none" v-model="basicSurvey.religion" /> 无</label>
                <label><input type="radio" value="yes" v-model="basicSurvey.religion" /> 有</label>
              </div>
            </div>

            <!-- 7. 在读学历 -->
            <div class="form-item">
              <label>在读学历<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="bachelor" v-model="basicSurvey.education" /> 本科</label>
                <label><input type="radio" value="master" v-model="basicSurvey.education" /> 硕士</label>
                <label><input type="radio" value="doctor" v-model="basicSurvey.education" /> 博士</label>
                <label><input type="radio" value="other" v-model="basicSurvey.education" /> 其他</label>
              </div>
            </div>

            <!-- 8. 在读专业 -->
            <div class="form-item">
              <label>在读专业<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="liberal" v-model="basicSurvey.majorType" /> 文科类</label>
                <label><input type="radio" value="science" v-model="basicSurvey.majorType" /> 理科类</label>
                <label><input type="radio" value="engineering" v-model="basicSurvey.majorType" /> 工科类</label>
                <label><input type="radio" value="art" v-model="basicSurvey.majorType" /> 艺术类</label>
                <label><input type="radio" value="sports" v-model="basicSurvey.majorType" /> 体育类</label>
                <label><input type="radio" value="other" v-model="basicSurvey.majorType" /> 其他</label>
              </div>
            </div>

            <!-- 9. 目前的学业情况 -->
            <div class="form-item">
              <label>目前的学业情况<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="excellent" v-model="basicSurvey.academicStatus" /> 很好</label>
                <label><input type="radio" value="good" v-model="basicSurvey.academicStatus" /> 较好</label>
                <label><input type="radio" value="average" v-model="basicSurvey.academicStatus" /> 一般</label>
                <label><input type="radio" value="poor" v-model="basicSurvey.academicStatus" /> 较差</label>
                <label><input type="radio" value="very_poor" v-model="basicSurvey.academicStatus" /> 很差</label>
              </div>
            </div>

            <!-- 10. 家庭经济水平 -->
            <div class="form-item">
              <label>家庭经济水平<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="excellent" v-model="basicSurvey.familyIncome" /> 很好</label>
                <label><input type="radio" value="good" v-model="basicSurvey.familyIncome" /> 较好</label>
                <label><input type="radio" value="average" v-model="basicSurvey.familyIncome" /> 一般</label>
                <label><input type="radio" value="poor" v-model="basicSurvey.familyIncome" /> 较差</label>
                <label><input type="radio" value="very_difficult" v-model="basicSurvey.familyIncome" /> 非常困难</label>
              </div>
            </div>

            <!-- 11. 家庭结构 -->
            <div class="form-item">
              <label>家庭结构<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="both_parents" v-model="basicSurvey.familyStructure" /> 双亲</label>
                <label><input type="radio" value="single_parent" v-model="basicSurvey.familyStructure" /> 单亲</label>
                <label><input type="radio" value="reconstituted" v-model="basicSurvey.familyStructure" /> 重组</label>
                <label><input type="radio" value="other" v-model="basicSurvey.familyStructure" /> 其他</label>
              </div>
            </div>

            <!-- 12. 是否有过留守经历 -->
            <div class="form-item">
              <label>是否有过留守经历（即不在父母身边学习和生活）<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="yes" v-model="basicSurvey.leftBehindExperience" /> 是</label>
                <label><input type="radio" value="no" v-model="basicSurvey.leftBehindExperience" /> 否</label>
              </div>
            </div>

            <!-- 13. 兄弟姐妹人数 -->
            <div class="form-item">
              <label>兄弟姐妹人数<span class="required">*</span></label>
              <input type="number" v-model="basicSurvey.siblingsNum" placeholder="请输入人数" min="0" max="20" />
            </div>

            <!-- 14. 你排行第几 -->
            <div class="form-item">
              <label>你排行第几？<span class="required">*</span></label>
              <input type="number" v-model="basicSurvey.siblingsRank" placeholder="请输入排行" min="1" max="20" />
            </div>

            <!-- 15. 你与父亲的关系如何 -->
            <div class="form-item">
              <label>你与父亲的关系如何？<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="excellent" v-model="basicSurvey.relationFather" /> 很好</label>
                <label><input type="radio" value="good" v-model="basicSurvey.relationFather" /> 较好</label>
                <label><input type="radio" value="average" v-model="basicSurvey.relationFather" /> 一般</label>
                <label><input type="radio" value="poor" v-model="basicSurvey.relationFather" /> 较差</label>
                <label><input type="radio" value="very_poor" v-model="basicSurvey.relationFather" /> 很差</label>
                <label><input type="radio" value="none" v-model="basicSurvey.relationFather" /> 无该成员</label>
              </div>
            </div>

            <!-- 16. 你与母亲的关系如何 -->
            <div class="form-item">
              <label>你与母亲的关系如何？<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="excellent" v-model="basicSurvey.relationMother" /> 很好</label>
                <label><input type="radio" value="good" v-model="basicSurvey.relationMother" /> 较好</label>
                <label><input type="radio" value="average" v-model="basicSurvey.relationMother" /> 一般</label>
                <label><input type="radio" value="poor" v-model="basicSurvey.relationMother" /> 较差</label>
                <label><input type="radio" value="very_poor" v-model="basicSurvey.relationMother" /> 很差</label>
                <label><input type="radio" value="none" v-model="basicSurvey.relationMother" /> 无该成员</label>
              </div>
            </div>

            <!-- 17. 你与兄弟姐妹的关系如何 -->
            <div class="form-item">
              <label>你与兄弟姐妹的关系如何？<span class="required">*</span></label>
              <div class="inline-options">
                <label><input type="radio" value="excellent" v-model="basicSurvey.relationSiblings" /> 很好</label>
                <label><input type="radio" value="good" v-model="basicSurvey.relationSiblings" /> 较好</label>
                <label><input type="radio" value="average" v-model="basicSurvey.relationSiblings" /> 一般</label>
                <label><input type="radio" value="poor" v-model="basicSurvey.relationSiblings" /> 较差</label>
                <label><input type="radio" value="very_poor" v-model="basicSurvey.relationSiblings" /> 很差</label>
                <label><input type="radio" value="none" v-model="basicSurvey.relationSiblings" /> 无该成员</label>
              </div>
            </div>
          </div>
        </div>

        <!-- 问卷题目区域（组件化 + v-for） -->
        <div class="questions-section" ref="questionsSectionRef" v-if="!isLoading && (!isFirstFill || currentStep === 2)">
          <QuestionItem
            v-for="(q, index) in questions"
            :key="q.id"
            :name="q.id"
            :title="q.title"
            :options="q.options"
            :question-number="index + 1"
            v-model="answers[index]"
          />
        </div>

        <!-- 分步/提交按钮区域 -->
        <div class="submit-section">
          <!-- 首次填写：第1步 显示“下一步” -->
          <template v-if="isFirstFill">
            <template v-if="currentStep === 1">
              <button type="button" class="submit-btn" @click="goNext" :disabled="!isbasicSurveyValid">
                下一步
              </button>
              <button type="button" class="save-btn" @click="saveDraft">
                保存草稿
              </button>
            </template>
            <template v-else>
              <button type="button" class="save-btn" @click="goBack">
                上一步
              </button>
              <button type="submit" class="submit-btn" :disabled="!isFormValid">
                提交问卷
              </button>
            </template>
          </template>
          <!-- 再次填写：直接提交 -->
          <template v-else>
            <button type="submit" class="submit-btn" :disabled="!isFormValid">
              提交问卷
            </button>
            <button type="button" class="save-btn" @click="saveDraft">
              保存草稿
            </button>
          </template>
        </div>
      </form>
    </div>

    <!-- 进度指示器 -->
    <div class="progress-section">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
      </div>
      <span class="progress-text">{{ completedQuestions }}/{{ totalQuestions }} 题已完成</span>
    </div>

    <!-- 返回首页 -->
    <div class="back-container">
      <router-link to="/home" class="back-button">
        ← 返回首页
      </router-link>
    </div>
  </div>
  
  <!-- 提交成功对话框（模仿预约成功样式） -->
  <div v-if="showSuccessDialog" class="success-overlay" @click="goHomeNow">
    <div class="success-dialog" @click.stop>
      <div class="success-icon">✅</div>
      <h3 class="success-title">提交成功</h3>
      <p class="success-desc">感谢您的配合，系统将根据结果提供后续服务</p>
      <button class="success-btn" @click="goHomeNow">返回首页（{{ successCountdown }}s）</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import QuestionItem from '../components/QuestionItem.vue'
import { surveyAPI, apiUtils } from '../api/index.js'

// 辅助函数：从localStorage获取用户信息
const getUserInfo = () => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (!userInfoStr) {
    return null
  }
  try {
    return JSON.parse(userInfoStr)
  } catch (e) {
    console.error('解析用户信息失败:', e)
    return null
  }
}

// 提交成功对话框与倒计时
const router = useRouter()
const showSuccessDialog = ref(false)
const successCountdown = ref(3)
let countdownTimer = null

const startSuccessCountdown = () => {
  // 清理可能存在的计时器
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  successCountdown.value = 3
  countdownTimer = setInterval(() => {
    successCountdown.value -= 1
    if (successCountdown.value <= 0) {
      clearInterval(countdownTimer)
      countdownTimer = null
      showSuccessDialog.value = false
      router.push('/home')
    }
  }, 1000)
}

const goHomeNow = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  showSuccessDialog.value = false
  router.push('/home')
}

// SCL-90量表统一选项
const scl90Options = [
  { label: '从无', value: '1' },
  { label: '轻度', value: '2' },
  { label: '中度', value: '3' },
  { label: '偏重', value: '4' },
  { label: '严重', value: '5' }
]

// SCL-90量表题目配置
const questions = ref([
  { id: 'q1', title: '头痛', options: scl90Options },
  { id: 'q2', title: '神经过敏，心中不踏实', options: scl90Options },
  { id: 'q3', title: '头脑中有不必要的想法或字句盘旋', options: scl90Options },
  { id: 'q4', title: '头昏或昏倒', options: scl90Options },
  { id: 'q5', title: '对异性的兴趣减退', options: scl90Options },
  { id: 'q6', title: '对旁人责备求全', options: scl90Options },
  { id: 'q7', title: '感到别人能控制你的思想', options: scl90Options },
  { id: 'q8', title: '责怪别人制造麻烦', options: scl90Options },
  { id: 'q9', title: '忘记性大', options: scl90Options },
  { id: 'q10', title: '担心自己的衣饰整齐及仪态的端正', options: scl90Options },
  { id: 'q11', title: '容易烦恼和激动', options: scl90Options },
  { id: 'q12', title: '胸痛', options: scl90Options },
  { id: 'q13', title: '害怕空旷的场所或街道', options: scl90Options },
  { id: 'q14', title: '感到自己的精力下降，活动减慢', options: scl90Options },
  { id: 'q15', title: '想结束自己的生命', options: scl90Options },
  { id: 'q16', title: '听到旁人听不到的声音', options: scl90Options },
  { id: 'q17', title: '发抖', options: scl90Options },
  { id: 'q18', title: '感到大多数人都不可信任', options: scl90Options },
  { id: 'q19', title: '胃口不好', options: scl90Options },
  { id: 'q20', title: '容易哭泣', options: scl90Options },
  { id: 'q21', title: '同异性相处时感到害羞不自在', options: scl90Options },
  { id: 'q22', title: '感到受骗，中了圈套或有人想抓您', options: scl90Options },
  { id: 'q23', title: '无缘无故地突然感到害怕', options: scl90Options },
  { id: 'q24', title: '自己不能控制地大发脾气', options: scl90Options },
  { id: 'q25', title: '怕单独出门', options: scl90Options },
  { id: 'q26', title: '经常责怪自己', options: scl90Options },
  { id: 'q27', title: '腰痛', options: scl90Options },
  { id: 'q28', title: '感到难以完成任务', options: scl90Options },
  { id: 'q29', title: '感到孤独', options: scl90Options },
  { id: 'q30', title: '感到苦闷', options: scl90Options },
  { id: 'q31', title: '过分担忧', options: scl90Options },
  { id: 'q32', title: '对事物不感兴趣', options: scl90Options },
  { id: 'q33', title: '感到害怕', options: scl90Options },
  { id: 'q34', title: '我的感情容易受到伤害', options: scl90Options },
  { id: 'q35', title: '旁人能知道您的私下想法', options: scl90Options },
  { id: 'q36', title: '感到别人不理解您不同情您', options: scl90Options },
  { id: 'q37', title: '感到人们对你不友好，不喜欢你', options: scl90Options },
  { id: 'q38', title: '做事必须做得很慢以保证做得正确', options: scl90Options },
  { id: 'q39', title: '心跳得很厉害', options: scl90Options },
  { id: 'q40', title: '恶心或胃部不舒服', options: scl90Options },
  { id: 'q41', title: '感到比不上他人', options: scl90Options },
  { id: 'q42', title: '肌肉酸痛', options: scl90Options },
  { id: 'q43', title: '感到有人在监视您谈论您', options: scl90Options },
  { id: 'q44', title: '难以入睡', options: scl90Options },
  { id: 'q45', title: '做事必须反复检查', options: scl90Options },
  { id: 'q46', title: '难以作出决定', options: scl90Options },
  { id: 'q47', title: '怕乘电车、公共汽车、地铁或火车', options: scl90Options },
  { id: 'q48', title: '呼吸有困难', options: scl90Options },
  { id: 'q49', title: '一阵阵发冷或发热', options: scl90Options },
  { id: 'q50', title: '因为感到害怕而避开某些东西，场合或活动', options: scl90Options },
  { id: 'q51', title: '脑子变空了', options: scl90Options },
  { id: 'q52', title: '身体发麻或刺痛', options: scl90Options },
  { id: 'q53', title: '喉咙有梗塞感', options: scl90Options },
  { id: 'q54', title: '感到对前途没有希望', options: scl90Options },
  { id: 'q55', title: '不能集中注意力', options: scl90Options },
  { id: 'q56', title: '感到身体的某一部分软弱无力', options: scl90Options },
  { id: 'q57', title: '感到紧张或容易紧张', options: scl90Options },
  { id: 'q58', title: '感到手或脚发沉', options: scl90Options },
  { id: 'q59', title: '想到有关死亡的事', options: scl90Options },
  { id: 'q60', title: '吃得太多', options: scl90Options },
  { id: 'q61', title: '当别人看着您或谈论您时感到不自在', options: scl90Options },
  { id: 'q62', title: '有一些不属于您自己的想法', options: scl90Options },
  { id: 'q63', title: '有想打人或伤害他人的冲动', options: scl90Options },
  { id: 'q64', title: '醒的太早', options: scl90Options },
  { id: 'q65', title: '必须反复洗手、点数目或触摸某些东西', options: scl90Options },
  { id: 'q66', title: '睡得不稳不深', options: scl90Options },
  { id: 'q67', title: '有想摔坏或破坏东西的冲动', options: scl90Options },
  { id: 'q68', title: '有一些别人没有的想法或念头', options: scl90Options },
  { id: 'q69', title: '感到对别人神经过敏', options: scl90Options },
  { id: 'q70', title: '在商店或电影院等人多的地方感到不自在', options: scl90Options },
  { id: 'q71', title: '感到任何事情都很难做', options: scl90Options },
  { id: 'q72', title: '一阵阵恐惧或惊恐', options: scl90Options },
  { id: 'q73', title: '感到在公共场合吃东西很不舒服', options: scl90Options },
  { id: 'q74', title: '经常与人争论', options: scl90Options },
  { id: 'q75', title: '单独一人时神经很紧张', options: scl90Options },
  { id: 'q76', title: '别人对您的成绩没有作出恰当的评价', options: scl90Options },
  { id: 'q77', title: '即使和别人在一起也感到孤单', options: scl90Options },
  { id: 'q78', title: '感到坐立不安心神不宁', options: scl90Options },
  { id: 'q79', title: '感到自己没有什么价值', options: scl90Options },
  { id: 'q80', title: '感到熟悉的东西变成陌生或不像是真的', options: scl90Options },
  { id: 'q81', title: '大叫或摔东西', options: scl90Options },
  { id: 'q82', title: '害怕会在公共场合昏倒', options: scl90Options },
  { id: 'q83', title: '感到别人想占您的便宜', options: scl90Options },
  { id: 'q84', title: '为一些有关"性"的想法而苦恼', options: scl90Options },
  { id: 'q85', title: '认为应该因为自己的过错而受到惩罚', options: scl90Options },
  { id: 'q86', title: '感到要赶快把事情做完', options: scl90Options },
  { id: 'q87', title: '感到自己的身体有严重问题', options: scl90Options },
  { id: 'q88', title: '从未感到和其他人很亲近', options: scl90Options },
  { id: 'q89', title: '感到自己有罪', options: scl90Options },
  { id: 'q90', title: '感到自己的脑子有毛病', options: scl90Options }
])

// 初始化答案容器 - 改为数组存储，值为number类型
const answers = ref(new Array(questions.value.length).fill(0))

// 基础信息（首次填写必填）
const basicSurvey = reactive({
  name: '',
  gender: '',
  age: '',
  ethnicity: '',
  birthplace: '',
  religion: '',
  education: '',
  majorType: '',
  academicStatus: '',
  familyIncome: '',
  familyStructure: '',
  leftBehindExperience: '',
  siblingsNum: '',
  siblingsRank: '',
  relationFather: '',
  relationMother: '',
  relationSiblings: ''
})

// 56个民族选项
const ethnicities = [
  '汉族', '壮族', '满族', '回族', '苗族', '维吾尔族', '土家族', '彝族', '蒙古族', '藏族',
  '布依族', '侗族', '瑶族', '白族', '朝鲜族', '哈尼族', '哈萨克族', '黎族', '傣族', '畲族',
  '傈僳族', '佤族', '拉祜族', '景颇族', '高山族', '水族', '东乡族', '纳西族', '柯尔克孜族', '土族',
  '达斡尔族', '仫佬族', '羌族', '布朗族', '撒拉族', '毛南族', '仡佬族', '锡伯族', '阿昌族', '普米族',
  '塔吉克族', '怒族', '乌孜别克族', '俄罗斯族', '鄂温克族', '鄂伦春族', '德昂族', '保安族', '裕固族', '京族',
  '塔塔尔族', '独龙族', '赫哲族', '门巴族', '珞巴族', '基诺族'
]

// 基于localStorage中的两个time字段判断是否首次
const isLoading = ref(true)
const isFirstFill = ref(true)
const latestAssessmentTime = ref(null)

onMounted(async () => {
  try {
    // 从localStorage获取用户信息（统一从对象中读取）
    const userInfoStr = localStorage.getItem('userInfo')
    if (!userInfoStr) {
      alert('未找到用户信息，请重新登录')
      isLoading.value = false
      return
    }
    
    let userInfo
    try {
      userInfo = JSON.parse(userInfoStr)
    } catch (e) {
      console.error('解析用户信息失败:', e)
      alert('用户信息格式错误，请重新登录')
      isLoading.value = false
      return
    }
    
    const studentId = userInfo.userId
    if (!studentId) {
      alert('未找到学生ID，请重新登录')
      isLoading.value = false
      return
    }

    // 从用户信息对象中获取两个time字段
    const lastEvaluateTime = userInfo.lastEvaluateTime
    const lastCounselTime = userInfo.lastCounselTime

    // 判断两个time是否均为空
    const isBothTimeEmpty = (!lastEvaluateTime || lastEvaluateTime === 'null' || lastEvaluateTime === '') && 
                             (!lastCounselTime || lastCounselTime === 'null' || lastCounselTime === '')

    // 如果两个time均为空，代表第一次填写问卷
    isFirstFill.value = isBothTimeEmpty

    // 设置最新测评时间（用于显示）
    if (lastEvaluateTime && lastEvaluateTime !== 'null' && lastEvaluateTime !== '') {
      latestAssessmentTime.value = lastEvaluateTime
    } else if (lastCounselTime && lastCounselTime !== 'null' && lastCounselTime !== '') {
      latestAssessmentTime.value = lastCounselTime
    } else {
      latestAssessmentTime.value = null
    }
  } catch (e) {
    console.error('获取问卷状态失败:', e)
    // 异常时默认视为首次，避免阻塞填写
    isFirstFill.value = true
  } finally {
    isLoading.value = false
  }
})

const requiredBasicFields = [
  'name', 'gender', 'age', 'ethnicity', 'birthplace', 'religion', 
  'education', 'majorType', 'academicStatus', 'familyIncome', 'familyStructure',
  'leftBehindExperience', 'siblingsNum', 'siblingsRank', 'relationFather',
  'relationMother', 'relationSiblings'
]

// 数字字段校验
const isNumericFieldValid = (field, min = 0, max = 999) => {
  const value = basicSurvey[field]
  return value !== '' && !isNaN(value) && Number(value) >= min && Number(value) <= max
}

// 分步逻辑
const currentStep = ref(1) // 1: 基础信息, 2: 问卷题目
const questionsSectionRef = ref(null) // 问卷题目区域的引用
const isbasicSurveyValid = computed(() => {
  return requiredBasicFields.every((k) => {
    if (k === 'age') return isNumericFieldValid(k, 1, 120)
    if (k === 'siblingsNum') return isNumericFieldValid(k, 0, 20)
    if (k === 'siblingsRank') return isNumericFieldValid(k, 1, 20)
    return !!basicSurvey[k]
  })
})

const goNext = async () => {
  if (isbasicSurveyValid.value) {
    currentStep.value = 2
    // 等待 DOM 更新后滚动到问题区域开头
    await nextTick()
    if (questionsSectionRef.value) {
      questionsSectionRef.value.scrollIntoView({ 
        behavior: 'smooth', 
        block: 'start' 
      })
    }
  }
}

const goBack = () => {
  currentStep.value = 1
}

// 计算属性
const completedQuestions = computed(() => {
  const answered = answers.value.filter(answer => answer > 0).length
  if (!isFirstFill.value) return answered
  
  // 首次填写时，统计必填基础信息
  const completedBasics = requiredBasicFields.filter((k) => {
    const v = basicSurvey[k]
    if (k === 'age') return isNumericFieldValid(k, 1, 120)
    if (k === 'siblingsNum') return isNumericFieldValid(k, 0, 20)
    if (k === 'siblingsRank') return isNumericFieldValid(k, 1, 20)
    return !!v
  }).length
  return answered + completedBasics
})

const progressPercentage = computed(() => {
  return (completedQuestions.value / totalQuestions.value) * 100
})

const isFormValid = computed(() => {
  return completedQuestions.value === totalQuestions.value
})

const totalQuestions = computed(() => {
  const base = questions.value.length
  return isFirstFill.value ? base + requiredBasicFields.length : base
})

// 方法
const submitSurvey = async () => {
  if (!isFormValid.value) {
    alert('请完成所有必填项！')
    return
  }
  
  try {
    // 从localStorage获取用户信息（统一从对象中读取）
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      alert('未找到学生ID，请重新登录')
      return
    }
    const studentId = userInfo.userId

    // 构建提交数据
    const submitData = apiUtils.buildSurveyData(
      isFirstFill.value ? basicSurvey : null,
      answers.value,
      studentId
    )
    
    console.log('提交问卷数据:', submitData)
    
    // 提交问卷
    let response
    if (isFirstFill.value) {
      response = await surveyAPI.submitFirstSurvey(submitData)
    } else {
      // 重测问卷只需要SCL-90数据
      const retestData = apiUtils.buildRetestSurveyData(answers.value, studentId)
      response = await surveyAPI.submitRetestSurvey(retestData)
    }
    
    if (response.success) {
      console.log('问卷提交结果:', response.data)
      localStorage.removeItem('draftData')
      // 显示提交成功对话框并开始倒计时
      showSuccessDialog.value = true
      startSuccessCountdown()
    } else {
      const errorMsg = apiUtils.getErrorMessage(response)
      alert(`问卷提交失败：${errorMsg}`)
    }
  } catch (error) {
    console.error('问卷提交错误:', error)
    alert('问卷提交失败，请检查网络连接后重试！')
  }
}

const saveDraft = async () => {
  try {
    // 从localStorage获取用户信息（统一从对象中读取）
    const userInfo = getUserInfo()
    if (!userInfo || !userInfo.userId) {
      alert('未找到学生ID，请重新登录')
      return
    }
    const studentId = userInfo.userId

    const draftData = {
      studentId: studentId,
      isFirstFill: isFirstFill.value,
      currentStep: currentStep.value,
      latestAssessmentTime: latestAssessmentTime.value,
      basicSurvey: isFirstFill.value ? { ...basicSurvey } : null,
      answers: [...answers.value] // 数组格式
    }
    
    console.log('保存草稿数据:', draftData)
    
    // 模拟保存草稿接口调用
    // const response = await surveyAPI.saveDraft(draftData)
    
    // 暂时使用本地存储保存草稿
    localStorage.setItem('survey_draft', JSON.stringify(draftData))
    alert('草稿已保存到本地！')
  } catch (error) {
    console.error('保存草稿失败:', error)
    alert('保存草稿失败！')
  }
}

const formatDate = (iso) => {
  try {
    const d = new Date(iso)
    const y = d.getFullYear()
    const m = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    return `${y}-${m}-${day}`
  } catch (e) {
    return iso
  }
}
</script>

<style scoped>
.survey-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.fill-status {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 10px 0 20px;
  color: #6c757d;
}

/* 提交成功对话框样式（模仿预约成功） */
.success-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 3000;
}

.success-dialog {
  width: 90%;
  max-width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 28px 24px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  text-align: center;
}

.success-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.success-title {
  margin: 0 0 8px;
  font-size: 1.4rem;
  font-weight: 700;
  color: #2c3e50;
}

.success-desc {
  margin: 0 0 20px;
  color: #6c757d;
}

.success-btn {
  padding: 10px 18px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.2s ease;
}

.success-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.fill-status.loading {
  color: #6c757d;
}

.badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.badge.first {
  background: #e9f5ff;
  color: #0b6bcb;
}

.badge.retest {
  background: #f3f0ff;
  color: #6c3bd7;
}

.last-time {
  font-size: 12px;
}

.survey-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1.2rem;
  margin-bottom: 16px;
  color: #2c3e50;
}

.survey-title {
  font-size: 2.5rem;
  margin-bottom: 15px;
  font-weight: 600;
}

.survey-description {
  font-size: 1.1rem;
  opacity: 0.9;
  line-height: 1.6;
}

.survey-form {
  background: white;
  border-radius: 15px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.first-info-section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  background-color: #fafbfc;
}

.first-info-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item input[type="text"],
.form-item input[type="tel"],
.form-item input[type="number"],
.form-item select {
  padding: 10px 12px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  outline: none;
  font-size: 14px;
}

.form-item input[type="text"]:focus,
.form-item input[type="tel"]:focus,
.form-item input[type="number"]:focus,
.form-item select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.form-item select {
  background-color: white;
  cursor: pointer;
}

.inline-options {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.required { color: #e03131; margin-left: 4px; }
.field-error { color: #e03131; font-size: 12px; }

.questions-section {
  margin-bottom: 40px;
}

.question-item {
  margin-bottom: 40px;
  padding: 25px;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  background-color: #fafbfc;
  transition: all 0.3s ease;
}

.question-item:hover {
  border-color: #667eea;
  box-shadow: 0 2px 10px rgba(102, 126, 234, 0.1);
}

.question-title {
  font-size: 1.3rem;
  margin-bottom: 20px;
  color: #2c3e50;
  font-weight: 500;
}

.question-options {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: space-between;
}

.option-item {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 12px 20px;
  border: 2px solid #e9ecef;
  border-radius: 25px;
  background: white;
  transition: all 0.3s ease;
  flex: 1;
  min-width: 120px;
  justify-content: center;
}

.option-item:hover {
  border-color: #667eea;
  background-color: #f8f9ff;
}

.option-item input[type="radio"] {
  margin-right: 8px;
  transform: scale(1.2);
}

.option-item input[type="radio"]:checked + .option-text {
  color: #667eea;
  font-weight: 600;
}

.option-item:has(input[type="radio"]:checked) {
  border-color: #667eea;
  background-color: #f0f2ff;
}

.option-text {
  font-size: 0.95rem;
  color: #495057;
}

.submit-section {
  display: flex;
  gap: 20px;
  justify-content: center;
  padding-top: 30px;
  border-top: 1px solid #e9ecef;
}

.submit-btn, .save-btn {
  padding: 15px 40px;
  border: none;
  border-radius: 25px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 150px;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.submit-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.save-btn {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
}

.save-btn:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.2);
}

.progress-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.9rem;
  color: #6c757d;
  text-align: center;
  display: block;
}

.back-container {
  text-align: center;
  margin-top: 30px;
}

.back-button {
  display: inline-block;
  padding: 12px 24px;
  background: #6c757d;
  color: #fff;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 500;
  transition: background 0.3s ease;
}

.back-button:hover {
  background: #5a6268;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .survey-container {
    padding: 10px;
  }
  
  .survey-form {
    padding: 20px;
  }
  
  .question-options {
    flex-direction: column;
  }
  
  .option-item {
    min-width: auto;
  }
  
  .submit-section {
    flex-direction: column;
    align-items: center;
  }
  
  .survey-title {
    font-size: 2rem;
  }
}
</style>
