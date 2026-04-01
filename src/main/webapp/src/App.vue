<template>
  <div id="app">
    <Header v-if="!isAdminRoute" />
    <transition name="fade" mode="out-in">
      <router-view />
    </transition>
    <Footer v-if="!isAdminRoute" />
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const userStore = useUserStore()

const isAdminRoute = computed(() => route.path.startsWith('/admin'))

onMounted(() => {
  userStore.loadUser()
})
</script>

<style>
:root {
  /* 颜色体系 */
  --color-primary: #409eff;
  --color-primary-dark: #3078d8;
  --color-primary-light: #ecf5ff;
  --color-primary-lighter: #f4f9ff;
  --color-success: #67c23a;
  --color-warning: #e6a23c;
  --color-danger: #f56c6c;
  --color-info: #909399;
  --color-text-primary: #1d2129;
  --color-text-regular: #4e5969;
  --color-text-secondary: #86909c;
  --color-text-placeholder: #c9cdd4;
  --color-bg-page: #f2f3f5;
  --color-bg-white: #ffffff;
  --color-bg-secondary: #f7f8fa;
  --color-border: #e5e6eb;
  --color-border-light: #f2f3f5;

  /* 间距系统 */
  --spacing-xs: 4px;
  --spacing-sm: 8px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
  --spacing-xxl: 48px;

  /* 圆角系统 */
  --radius-sm: 4px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --radius-xxl: 20px;
  --radius-round: 999px;

  /* 阴影系统 */
  --shadow-sm: 0 1px 4px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4px 12px rgba(0, 0, 0, 0.06);
  --shadow-lg: 0 8px 24px rgba(0, 0, 0, 0.1);
  --shadow-xl: 0 12px 36px rgba(0, 0, 0, 0.14);

  /* 字体系统 */
  --font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, 'Microsoft YaHei', Arial, sans-serif;
  --font-size-xs: 12px;
  --font-size-sm: 13px;
  --font-size-base: 14px;
  --font-size-md: 15px;
  --font-size-lg: 16px;
  --font-size-xl: 18px;
  --font-size-xxl: 20px;
  --font-size-title: 24px;
  --font-size-hero: 28px;
  --font-weight-regular: 400;
  --font-weight-medium: 500;
  --font-weight-semibold: 600;
  --font-weight-bold: 700;
  --line-height-base: 1.5;
  --line-height-relaxed: 1.75;

  /* 动画系统 */
  --transition-fast: 0.15s ease;
  --transition-normal: 0.25s ease;
  --transition-slow: 0.35s ease;
  --transition-spring: 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

  /* 布局 */
  --max-width: 1200px;
  --header-height: 60px;
  --page-padding: 20px;
  --section-gap: 64px;

  /* z-index 层级 */
  --z-header: 1000;
  --z-dropdown: 1100;
  --z-dialog: 2000;
  --z-loading: 3000;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: var(--font-family);
  background: var(--color-bg-page);
}

/* 滚动条美化 */
::-webkit-scrollbar { width: 6px; height: 6px; }
::-webkit-scrollbar-track { background: transparent; }
::-webkit-scrollbar-thumb { background: #c9cdd4; border-radius: 3px; }
::-webkit-scrollbar-thumb:hover { background: #86909c; }

/* 选中色 */
::selection { background: var(--color-primary-light); color: var(--color-primary-dark); }

/* Element Plus 全局覆盖 */
.el-card { border-radius: var(--radius-lg); border: none; box-shadow: var(--shadow-sm); }
.el-dialog { border-radius: var(--radius-xl); }
.el-tag { border-radius: var(--radius-sm); }
.el-button { border-radius: var(--radius-md); }

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

a {
  color: inherit;
  text-decoration: none;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
