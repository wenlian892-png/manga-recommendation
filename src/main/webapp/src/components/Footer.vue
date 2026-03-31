<template>
  <div class="footer">
    <div class="footer-content">
      <div class="footer-info">
        <p class="footer-title">漫画推荐系统</p>
        <p class="footer-desc">基于 Item-CF 协同过滤算法的个性化漫画推荐平台</p>
      </div>
      <div class="footer-links">
        <div class="link-group">
          <p class="link-title">快捷链接</p>
          <router-link to="/" class="footer-link">首页</router-link>
          <router-link to="/manga" class="footer-link">漫画分类</router-link>
          <router-link to="/leaderboard" class="footer-link">排行榜</router-link>
        </div>
        <div class="link-group">
          <p class="link-title">社区</p>
          <router-link to="/recommend" class="footer-link">个性化推荐</router-link>
          <router-link to="/forum" class="footer-link">交流论坛</router-link>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      <p>© 2024 漫画推荐系统 - 基于 Item-CF 协同过滤算法</p>
    </div>
  </div>
</template>

<style scoped>
.footer {
  background: #2c3e50;
  color: #ecf0f1;
  margin-top: 60px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  display: flex;
  justify-content: space-between;
}

.footer-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.footer-desc {
  font-size: 14px;
  color: #bdc3c7;
}

.footer-links {
  display: flex;
  gap: 60px;
}

.link-group {
  display: flex;
  flex-direction: column;
}

.link-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 12px;
}

.footer-link {
  color: #bdc3c7;
  text-decoration: none;
  font-size: 14px;
  margin-bottom: 8px;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #409eff;
}

.footer-bottom {
  background: #1a252f;
  padding: 16px 20px;
  text-align: center;
  font-size: 13px;
  color: #7f8c8d;
}
</style>
