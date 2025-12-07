<template>
  <div class="home-container">
    <div class="hero-section">
      <h1 class="hero-title">心理健康预约系统</h1>
      <p class="hero-description">专业的心理健康服务，为您提供贴心的心理咨询</p>
    </div>

    <div class="action-cards">
      <div class="card">
        <div class="card-icon">📋</div>
        <h3 class="card-title">填写问卷</h3>
        <p class="card-description">完成心理健康评估问卷，帮助我们更好地了解您的需求</p>
        <router-link to="/survey" class="card-button">
          开始填写
        </router-link>
      </div>

      <div class="card">
        <div class="card-icon">👨‍⚕️</div>
        <h3 class="card-title">预约咨询</h3>
        <p class="card-description">预约专业心理咨询师，获得个性化的心理支持</p>
        <router-link to="/reservation" class="card-button">
          立即预约
        </router-link>
      </div>

      <div class="card">
        <div class="card-icon">📅</div>
        <h3 class="card-title">查看预约</h3>
        <p class="card-description">查看您的预约记录，管理预约状态</p>
        <router-link to="/reservation-list" class="card-button">
          查看记录
        </router-link>
      </div>

      <div class="card">
        <div class="card-icon">📊</div>
        <h3 class="card-title">查看报告</h3>
        <p class="card-description">查看您的心理健康评估报告和建议</p>
        <router-link to="/report-results" class="card-button">
          查看报告
        </router-link>
      </div>

      <div class="card">
        <div class="card-icon">💬</div>
        <h3 class="card-title">智能对话</h3>
        <p class="card-description">与AI助手进行对话，获取心理健康建议和支持</p>
        <router-link to="/chat" class="card-button">
          开始对话
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
// 这里可以添加首页的逻辑
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: calc(100vh - 64px);
}

.hero-section {
  text-align: center;
  margin-bottom: 60px;
  padding: 60px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.hero-title {
  font-size: 3rem;
  margin-bottom: 20px;
  font-weight: 700;
}

.hero-description {
  font-size: 1.3rem;
  opacity: 0.9;
  line-height: 1.6;
}

.action-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.card {
  background: white;
  padding: 40px 30px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 20px;
}

.card-title {
  font-size: 1.5rem;
  margin-bottom: 15px;
  color: #2c3e50;
  font-weight: 600;
}

.card-description {
  color: #6c757d;
  line-height: 1.6;
  margin-bottom: 30px;
}

.card-button {
  display: inline-block;
  padding: 15px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.card-button:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.card-button.disabled {
  background: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-container {
    padding: 20px 10px;
  }
  
  .hero-section {
    padding: 40px 20px;
  }
  
  .hero-title {
    font-size: 2.2rem;
  }
  
  .hero-description {
    font-size: 1.1rem;
  }
  
  .action-cards {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .card {
    padding: 30px 20px;
  }
}
</style>
