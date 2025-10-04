<template>
  <div class="question-item">
    <h3 class="question-title">{{ questionNumber }}. {{ title }}</h3>
    <div class="question-options">
      <label class="option-item" v-for="opt in options" :key="opt.value">
        <input
          type="radio"
          :name="name"
          :value="opt.value"
          :checked="modelValue === Number(opt.value)"
          @change="$emit('update:modelValue', Number(opt.value))"
        />
        <span class="option-text">{{ opt.label }}</span>
      </label>
    </div>
  </div>
</template>

<script setup>
defineProps({
  name: { type: String, required: true },
  title: { type: String, required: true },
  options: { type: Array, required: true },
  modelValue: { type: Number, default: 0 },
  questionNumber: { type: Number, default: 1 }
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
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
  gap: 15px;
  justify-content: space-between;
  flex-wrap: nowrap;
}

.option-item {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 25px;
  background: white;
  transition: all 0.3s ease;
  flex: 1;
  justify-content: center;
  white-space: nowrap;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .question-options {
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .option-item {
    flex: 1 1 calc(50% - 5px);
    min-width: 0;
    padding: 10px 12px;
  }
  
  .option-text {
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .option-item {
    flex: 1 1 100%;
    margin-bottom: 8px;
  }
}
</style>


