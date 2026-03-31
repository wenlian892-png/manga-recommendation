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
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <div class="header-nav">
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
      </div>
    </div>

    <el-dialog v-model="showLoginDialog" title="登录" width="400px">
      <el-form :model="loginForm" label-width="60px">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
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
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Search, UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const searchKeyword = ref('')
const showLoginDialog = ref(false)
const showRegisterDialog = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: ''
})

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ name: 'Manga', query: { keyword: searchKeyword.value } })
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
  try {
    const res = await request.post('/user/login', loginForm)
    userStore.setUser(res.data)
    ElMessage.success('登录成功')
    showLoginDialog.value = false
    loginForm.username = ''
    loginForm.password = ''
  } catch (e) {
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password) {
    ElMessage.warning('请填写用户名和密码')
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
  }
}
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
</style>
