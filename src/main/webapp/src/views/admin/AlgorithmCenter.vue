<template>
  <div class="algorithm-center">
    <div class="center-card">
      <div class="icon-section">
        <div class="icon-circle">
          <span class="icon-emoji">⚙️</span>
        </div>
      </div>

      <h1 class="title">Item-CF 协同过滤算法</h1>
      <p class="subtitle">基于用户评分行为数据，计算漫画物品相似度矩阵，生成个性化推荐清单</p>

      <div class="algo-steps">
        <div class="step">
          <span class="step-num">01</span>
          <span class="step-text">全量加载用户评分记录</span>
        </div>
        <div class="step-arrow">→</div>
        <div class="step">
          <span class="step-num">02</span>
          <span class="step-text">计算漫画间余弦相似度</span>
        </div>
        <div class="step-arrow">→</div>
        <div class="step">
          <span class="step-num">03</span>
          <span class="step-text">预测用户评分并 Top-N 排序</span>
        </div>
      </div>

      <div class="execute-section">
        <button
          class="execute-btn"
          :class="{ loading: executing }"
          :disabled="executing"
          @click="handleExecute"
        >
          <span v-if="!executing" class="btn-content">
            <span class="btn-icon">🚀</span>
            <span class="btn-text">立即执行 Item-CF 协同过滤计算</span>
          </span>
          <span v-else class="btn-content">
            <span class="btn-icon spinning">⚙️</span>
            <span class="btn-text">算法执行中，请稍候...</span>
          </span>
        </button>

        <div v-if="lastResult" class="result-box" :class="{ success: lastResult.success, error: !lastResult.success }">
          <div class="result-header">
            <span v-if="lastResult.success" class="result-icon">✅</span>
            <span v-else class="result-icon">❌</span>
            <span class="result-title">{{ lastResult.success ? '执行成功' : '执行失败' }}</span>
          </div>
          <p v-if="lastResult.success" class="result-detail">
            算法执行完毕，推荐矩阵已更新。本次计算耗时 <strong>{{ lastResult.durationMs }}ms</strong>
          </p>
          <p v-else class="result-detail">
            {{ lastResult.message }}
          </p>
        </div>

        <div class="tip-text">
          <p>💡 算法默认每日凌晨 2:00 自动执行一次，您也可手动触发重新计算</p>
          <p>⚠️ 重新计算将覆盖所有用户的推荐结果</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElLoading, ElMessage } from 'element-plus'
import request from '@/utils/request'

const executing = ref(false)
const lastResult = ref(null)

async function handleExecute() {
  if (executing.value) return

  const loadingTexts = [
    '正在拉取全量评分数据...',
    '正在计算漫画物品相似度矩阵...',
    '正在生成个性化预测清单...'
  ]

  let textIndex = 0
  let timer = null

  const loadingInstance = ElLoading.service({
    fullscreen: true,
    text: loadingTexts[0],
    background: 'rgba(0, 0, 0, 0.75)',
    customClass: 'algorithm-loading-mask'
  })

  timer = setInterval(() => {
    textIndex = (textIndex + 1) % loadingTexts.length
    loadingInstance.setText(loadingTexts[textIndex])
  }, 800)

  executing.value = true

  try {
    const res = await request.post('/admin/algorithm/run')
    lastResult.value = res.data
    clearInterval(timer)
    loadingInstance.close()

    if (res.data.success) {
      ElMessage.success(`✅ 算法执行成功！耗时 ${res.data.durationMs}ms，推荐矩阵已更新`)
    } else {
      ElMessage.error(`❌ 执行失败: ${res.data.message}`)
    }
  } catch (e) {
    clearInterval(timer)
    loadingInstance.close()
    lastResult.value = { success: false, message: e.message || '网络异常，请稍后重试' }
    ElMessage.error('❌ 算法执行失败')
  } finally {
    executing.value = false
    if (timer) clearInterval(timer)
  }
}
</script>

<style scoped>
.algorithm-center {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.center-card {
  background: white;
  border-radius: 20px;
  padding: 50px 40px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
}

.icon-section {
  margin-bottom: 24px;
}

.icon-circle {
  width: 90px;
  height: 90px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.icon-emoji {
  font-size: 42px;
}

.title {
  font-size: 30px;
  font-weight: bold;
  color: #333;
  margin-bottom: 12px;
}

.subtitle {
  font-size: 15px;
  color: #999;
  margin-bottom: 36px;
  line-height: 1.6;
}

.algo-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.step-num {
  width: 36px;
  height: 36px;
  background: #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: bold;
  color: #666;
}

.step-text {
  font-size: 12px;
  color: #999;
  max-width: 100px;
}

.step-arrow {
  font-size: 20px;
  color: #ccc;
}

.execute-section {
  border-top: 1px solid #f0f0f0;
  padding-top: 36px;
}

.execute-btn {
  width: 100%;
  max-width: 420px;
  padding: 18px 32px;
  font-size: 17px;
  font-weight: bold;
  color: white;
  background: linear-gradient(135deg, #409eff 0%, #3078d8 100%);
  border: none;
  border-radius: 50px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.35);
  margin-bottom: 24px;
}

.execute-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 24px rgba(64, 158, 255, 0.45);
}

.execute-btn:active:not(:disabled) {
  transform: translateY(0);
}

.execute-btn:disabled {
  cursor: not-allowed;
  opacity: 0.85;
}

.execute-btn.loading {
  background: linear-gradient(135deg, #9099a4 0%, #78889a 100%);
  box-shadow: 0 4px 16px rgba(144, 153, 164, 0.3);
}

.btn-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.btn-icon {
  font-size: 20px;
}

.btn-icon.spinning {
  display: inline-block;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.btn-text {
  font-size: 16px;
}

.result-box {
  padding: 16px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  text-align: left;
}

.result-box.success {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
}

.result-box.error {
  background: #fef0f0;
  border: 1px solid #fde2e2;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.result-icon {
  font-size: 18px;
}

.result-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.result-detail {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.result-detail strong {
  color: #409eff;
}

.tip-text {
  font-size: 12px;
  color: #bbb;
  line-height: 1.8;
}

.tip-text p {
  margin: 0;
}
</style>

<style>
.algorithm-loading-mask .el-loading-text {
  font-size: 16px;
  color: white;
  font-weight: 500;
}
</style>
