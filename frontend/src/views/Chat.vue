<template>
  <div class="chat-container">
    <div class="chat-header">
      <h1 class="chat-title">智能对话</h1>
      <p class="chat-subtitle">与AI助手进行对话，获取心理健康建议和支持</p>
    </div>

    <div class="chat-content">
      <!-- 对话区域 -->
      <div class="chat-messages" ref="messagesContainer">
        <div class="welcome-message">
          <div class="message-avatar">🤖</div>
          <div class="message-content">
            <p>您好！我是心理健康AI助手，很高兴为您服务。您可以问我任何关于心理健康的问题，我会尽力帮助您。</p>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-area">
        <div class="input-container">
          <textarea
            v-model="inputMessage"
            placeholder="输入您的问题..."
            rows="2"
            class="message-input"
            @keydown.enter.exact.prevent="sendMessage"
            @keydown.shift.enter.exact="insertNewline"
          ></textarea>
          <button 
            class="send-button" 
            @click="sendMessage"
            :disabled="!inputMessage.trim() || isSending"
          >
            <span v-if="!isSending">发送</span>
            <span v-else>发送中...</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 返回按钮 -->
    <div class="back-container">
      <router-link to="/home" class="back-button">
        ← 返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const inputMessage = ref('')
const isSending = ref(false)
const messagesContainer = ref(null)

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isSending.value) {
    return
  }

  const userMessage = inputMessage.value.trim()
  inputMessage.value = ''

  // 这里后续可以添加实际的API调用逻辑
  isSending.value = true
  
  // 模拟发送消息
  setTimeout(() => {
    isSending.value = false
    // 后续添加消息显示逻辑
  }, 500)
}

const insertNewline = () => {
  // 允许Shift+Enter换行
  // Vue会自动处理，这里不需要额外代码
}
</script>

<style scoped>
.chat-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 64px);
  display: flex;
  flex-direction: column;
}

.chat-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 15px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.chat-title {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 600;
}

.chat-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  line-height: 1.6;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 20px;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  min-height: 400px;
  max-height: calc(100vh - 400px);
}

.welcome-message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 12px;
  line-height: 1.6;
  color: #2c3e50;
}

.message-content p {
  margin: 0;
}

.chat-input-area {
  border-top: 1px solid #e9ecef;
  padding: 20px;
  background: #fafbfc;
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e9ecef;
  border-radius: 12px;
  font-size: 1rem;
  font-family: inherit;
  resize: none;
  outline: none;
  transition: border-color 0.3s ease;
}

.message-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.send-button {
  padding: 12px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.send-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.send-button:disabled {
  background: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.back-container {
  text-align: center;
  margin-top: 20px;
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
  .chat-container {
    padding: 10px;
  }

  .chat-header {
    padding: 20px;
  }

  .chat-title {
    font-size: 2rem;
  }

  .chat-subtitle {
    font-size: 1rem;
  }

  .chat-messages {
    min-height: 300px;
    max-height: calc(100vh - 350px);
    padding: 15px;
  }

  .chat-input-area {
    padding: 15px;
  }

  .input-container {
    flex-direction: column;
    align-items: stretch;
  }

  .send-button {
    width: 100%;
  }
}
</style>

