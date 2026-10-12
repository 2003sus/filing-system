<template>
  <div class="layout">
    <div class="header">
      <div class="logo">网上立案系统</div>
      <div class="user-info">
        <span>{{ realName }}（{{ role }}）</span>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
    </div>

    <div class="main">
      <div class="toolbar">
        <h2>任务列表</h2>
        <div>
          <el-button @click="$router.push('/cases')">返回案件</el-button>
          <el-button @click="loadTasks">刷新</el-button>
        </div>
      </div>

      <el-table :data="tasks" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="caseId" label="案件ID" width="80" />
        <el-table-column prop="status" label="状态" width="140">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyNo" label="申请编号" />
        <el-table-column prop="failReason" label="失败原因" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/tasks/${row.id}`)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { taskApi } from '@/api'

const tasks = ref([])
const loading = ref(false)
const role = localStorage.getItem('role') || ''
const realName = localStorage.getItem('realName') || ''
const router = useRouter()
let timer: any = null

function statusType(status: string) {
  if (status === 'SUBMITTED') return 'success'
  if (status === 'FAILED') return 'danger'
  if (status === 'WAIT_REVIEW') return 'warning'
  if (status === 'FILLING') return 'primary'
  return 'info'
}

async function loadTasks() {
  loading.value = true
  try {
    const res: any = await taskApi.list()
    if (res.code === 200) tasks.value = res.data
  } finally {
    loading.value = false
  }
}

function logout() {
  localStorage.clear()
  router.push('/login')
}

onMounted(() => {
  loadTasks()
  timer = setInterval(loadTasks, 3000)  // 每3秒自动刷新
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.layout { min-height: 100vh; background: #f5f7fa; }
.header { display: flex; justify-content: space-between; align-items: center; padding: 0 30px; height: 60px; background: #1a4d8f; color: white; }
.logo { font-size: 20px; font-weight: bold; }
.user-info { display: flex; align-items: center; gap: 15px; }
.main { padding: 20px 30px; }
.toolbar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.toolbar h2 { margin: 0; color: #1a4d8f; }
</style>