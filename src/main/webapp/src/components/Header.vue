<template>
  <div class="header">
    <div class="header-content">
      <div class="header-left">
        <router-link to="/" class="logo">
          <span class="logo-text">漫推荐</span>
        </router-link>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索漫画..."
          class="search-input"
          @input="debouncedHandleSearch"
          @keyup.enter="handleSearch"
          autocomplete="off"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" aria-label="搜索" />
          </template>
        </el-input>
      </div>

      <div class="header-nav" v-if="!isMobile">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/category" class="nav-item">分类</router-link>
        <router-link to="/leaderboard" class="nav-item">排行榜</router-link>
        <router-link to="/recommend" class="nav-item">推荐</router-link>
        <router-link to="/forum" class="nav-item">论坛</router-link>
        <router-link v-if="userStore.isAdmin" to="/admin" class="nav-item admin-link">
          管理后台
        </router-link>
      </div>

      <div class="header-right">
        <el-dropdown v-if="isMobile" @command="handleMobileMenuCommand">
          <el-button :icon="Menu" aria-label="菜单" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="home">首页</el-dropdown-item>
              <el-dropdown-item command="category">分类</el-dropdown-item>
              <el-dropdown-item command="leaderboard">排行榜</el-dropdown-item>
              <el-dropdown-item command="recommend">推荐</el-dropdown-item>
              <el-dropdown-item command="forum">论坛</el-dropdown-item>
              <el-dropdown-item v-if="userStore.isAdmin" command="admin">管理后台</el-dropdown-item>
              <el-dropdown-item v-if="userStore.isLoggedIn" command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item v-if="userStore.isLoggedIn" command="logout" divided>退出登录</el-dropdown-item>
              <el-dropdown-item v-else command="login">登录/注册</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <template v-else>
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleUserCommand">
              <span class="user-info">
                <el-avatar :size="32" :icon="UserFilled" />
                <span class="username">{{ userStore.currentUser?.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" @click="showLoginDialog = true">登录</el-button>
          </template>
        </template>
      </div>
    </div>

    <el-dialog v-model="showLoginDialog" title="登录" width="400px">
      <el-form :model="loginForm" label-width="60px">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" autocomplete="current-password" inputmode="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showLoginDialog = false">取消</el-button>
        <el-button type="primary" @click="handleLogin">登录</el-button>
        <el-button text @click="showRegisterDialog = true; showLoginDialog = false">没有账号？去注册</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showRegisterDialog" title="注册" width="400px">
      <el-form :model="registerForm" label-width="60px">
        <el-form-item label="用户名">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" autocomplete="new-password" inputmode="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegisterDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Search, UserFilled, Menu } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const showLoginDialog = ref(false)
const showRegisterDialog = ref(false)

const isMobile = computed(() => {
  return window.innerWidth < 768
})

function handleMobileMenuCommand(command) {
  switch (command) {
    case 'home':
      router.push('/')
      break
    case 'category':
      router.push('/category')
      break
    case 'leaderboard':
      router.push('/leaderboard')
      break
    case 'recommend':
      router.push('/recommend')
      break
    case 'forum':
      router.push('/forum')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      break
    case 'login':
      showLoginDialog.value = true
      break
  }
}

let searchTimer = null

function debounce(func, delay) {
  return function() {
    clearTimeout(searchTimer)
    searchTimer = setTimeout(() => {
      func.apply(this, arguments)
    }, delay)
  }
}

const debouncedHandleSearch = debounce(handleSearch, 300)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: ''
})

function handleSearch() {
  const keyword = searchKeyword.value.trim()
  if (keyword) {
    router.push({ name: 'Manga', query: { keyword } })
  }
}

function handleUserCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
  } else if (command === 'profile') {
    router.push({ name: 'Profile' })
  }
}

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  
  if (loginForm.username.length < 3 || loginForm.username.length > 20) {
    ElMessage.warning('用户名长度应在3-20个字符之间')
    return
  }
  
  if (loginForm.password.length < 6) {
    ElMessage.warning('密码长度至少为6个字符')
    return
  }
  
  try {
    const res = await request.post('/user/login', loginForm)
    userStore.setUser(res.data)
    ElMessage.success('登录成功')
    showLoginDialog.value = false
    loginForm.username = ''
    loginForm.password = ''
  } catch (e) {
    ElMessage.error('登录失败，请检查用户名和密码')
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  
  if (registerForm.username.length < 3 || registerForm.username.length > 20) {
    ElMessage.warning('用户名长度应在3-20个字符之间')
    return
  }
  
  if (registerForm.password.length < 6) {
    ElMessage.warning('密码长度至少为6个字符')
    return
  }
  
  try {
    const res = await request.post('/user/register', registerForm)
    userStore.setUser(res.data)
    ElMessage.success('注册成功')
    showRegisterDialog.value = false
    registerForm.username = ''
    registerForm.password = ''
  } catch (e) {
    ElMessage.error('注册失败，用户名可能已存在')
  }
}

onUnmounted(() => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
})
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.logo {
  text-decoration: none;
  margin-right: 20px;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.search-input {
  width: 260px;
}

.header-nav {
  display: flex;
  align-items: center;
  margin: 0 40px;
}

.nav-item {
  padding: 0 16px;
  color: #333;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.3s;
}

.nav-item:hover {
  color: #409eff;
}

.nav-item.router-link-active {
  color: #409eff;
}

.admin-link {
  color: #f56c6c;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #333;
}

@media screen and (max-width: 768px) {
  .header-content {
    padding: 0 10px;
  }
  
  .logo-text {
    font-size: 20px;
  }
  
  .search-input {
    width: 180px;
  }
  
  .header-nav {
    display: none;
  }
  
  .header-right {
    margin-left: 10px;
  }
}

@media screen and (min-width: 769px) {
  .header-nav {
    display: flex;
  }
}
</style>
