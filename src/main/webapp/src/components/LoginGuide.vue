<template>
  <div class="login-guide">
    <div class="login-guide-content">
      <el-icon :size="48" class="login-guide-icon"><Lock /></el-icon>
      <h3 class="login-guide-title">{{ title }}</h3>
      <p class="login-guide-desc">{{ description }}</p>
      <el-button type="primary" size="large" @click="handleLogin">
        {{ buttonText }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

defineProps({
  title: {
    type: String,
    default: '登录解锁更多精彩'
  },
  description: {
    type: String,
    default: '登录后即可获得基于你的阅读偏好的个性化推荐'
  },
  buttonText: {
    type: String,
    default: '立即登录'
  }
})

const userStore = useUserStore()

function handleLogin() {
  userStore.showLoginDialog = true
}
</script>

<style scoped>
.login-guide {
  background: linear-gradient(135deg, var(--color-primary-light), #f0f9ff);
  border-radius: var(--radius-xl);
  padding: 48px;
  border: 1px solid #d9ecff;
}

.login-guide-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.login-guide-icon {
  color: var(--color-primary);
  margin-bottom: 16px;
}

.login-guide-title {
  font-size: 20px;
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  margin: 0 0 8px 0;
}

.login-guide-desc {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin: 0 0 24px 0;
  max-width: 320px;
}
</style>
