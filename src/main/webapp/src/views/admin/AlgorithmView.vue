<template>
  <div class="algorithm-view">
    <h1>算法控制台</h1>
    <div class="card">
      <h2>Item-CF 协同过滤算法</h2>
      <p class="desc">基于用户评分记录，计算漫画相似度矩阵，生成个性化推荐列表</p>
      <div class="actions">
        <el-button type="primary" size="large" :disabled="running" @click="runAlgorithm">
          {{ running ? '算法执行中...' : '手动触发推荐计算' }}
        </el-button>
      </div>
      <div v-if="lastResult" class="result">
        <p v-if="lastResult.success" class="success">✅ 执行成功，耗时 {{ lastResult.durationMs }} ms</p>
        <p v-else class="error">❌ 执行失败: {{ lastResult.message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { ElLoading } from 'element-plus'
import request from '@/utils/request'

const running = ref(false)
const lastResult = ref(null)

async function runAlgorithm() {
  if (running.value) {
    return
  }

  const loadingTextList = [
    '正在拉取全量评分数据...',
    '正在计算漫画物品相似度矩阵...',
    '正在生成个性化预测清单...'
  ]

  let textIndex = 0
  let timer = null

  const loadingInstance = ElLoading.service({
    fullscreen: true,
    text: loadingTextList[0],
    background: 'rgba(0, 0, 0, 0.7)'
  })

  timer = setInterval(() => {
    textIndex = (textIndex + 1) % loadingTextList.length
    loadingInstance.setText(loadingTextList[textIndex])
  }, 800)

  running.value = true

  try {
    const res = await request.post('/admin/algorithm/run')
    lastResult.value = res.data
    clearInterval(timer)
    loadingInstance.close()

    if (res.data.success) {
      ElMessage.success(`✅ 算法执行成功！总耗时 ${res.data.durationMs}ms`)
    } else {
      ElMessage.error(`❌ 算法执行失败: ${res.data.message}`)
    }
  } catch (e) {
    clearInterval(timer)
    loadingInstance.close()
    lastResult.value = { success: false, message: e.message || '网络异常' }
    ElMessage.error('❌ 算法执行失败，请稍后重试')
  } finally {
    running.value = false
    if (timer) {
      clearInterval(timer)
    }
  }
}
</script>

<style scoped>
.algorithm-view {
  padding: 0 10px;
}

h1 {
  font-size: 24px;
  margin-bottom: 24px;
}

.card {
  background: white;
  padding: 30px;
  border-radius: 12px;
}

h2 {
  font-size: 20px;
  margin-bottom: 12px;
}

.desc {
  color: #666;
  margin-bottom: 24px;
}

.actions {
  margin-bottom: 20px;
}

.result {
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 14px;
}

.success {
  color: #67c23a;
}

.error {
  color: #f56c6c;
}
</style>
