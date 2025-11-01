<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'
import Header from './components/Header.vue'

const route = useRoute()

// 判断是否在登录页面
const isLoginPage = computed(() => route.path === '/login')
</script>

<template>
  <div id="app">
    <Header v-if="!isLoginPage" />
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
    'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
    sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f8f9fa;
}

#app {
  min-height: 100vh;
}

.main-content {
  flex: 1;
}
</style>
