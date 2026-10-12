<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <div class="header">
      <div class="logo">网上立案系统</div>
      <div class="user-info">
        <span>{{ realName }}（{{ role }}）</span>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
    </div>

    <!-- 主体 -->
    <div class="main">
      <div class="toolbar">
        <h2>案件列表</h2>
        <div>
          <el-button type="primary" @click="$router.push('/cases/new')">
            新建案件
          </el-button>
          <el-button @click="$router.push('/tasks')">查看任务</el-button>
          <el-button v-if="role === 'ADMIN'" @click="$router.push('/users')">
            用户管理
          </el-button>
        </div>
      </div>

      <el-table :data="cases" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="caseNo" label="案件编号" width="240" />
        <el-table-column prop="title" label="案由" />
        <el-table-column prop="plaintiff" label="原告" width="120" />
        <el-table-column prop="defendant" label="被告" width="120" />
        <el-table-column prop="amount" label="金额" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUBMITTED' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button size="small" @click="$router.push(`/cases/${row.id}`)">
              详情
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="createTask(row.id)"
              :disabled="row.status === 'SUBMITTED'"
            >
              创建立案任务
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { caseApi, taskApi } from '@/api'

const cases = ref([])
const loading = ref(false)
const role = localStorage.getItem('role') || ''
const realName = localStorage.getItem('realName') || ''
const router = useRouter()

async function loadCases() {
  loading.value = true
  try {
    const res: any = await caseApi.list()
    if (res.code === 200) cases.value = res.data
  } finally {
    loading.value = false
  }
}

async function createTask(caseId: number) {
  try {
    const res: any = await taskApi.create(caseId)
    if (res.code === 200) {
      ElMessage.success('任务创建成功，已进入队列')
      router.push('/tasks')
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '创建失败')
  }
}

function logout() {
  localStorage.clear()
  router.push('/login')
}

onMounted(loadCases)
</script>

<style scoped>
.layout {
  min-height: 100vh;
  background: #f5f7fa;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  height: 60px;
  background: #1a4d8f;
  color: white;
}
.logo {
  font-size: 20px;
  font-weight: bold;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.main {
  padding: 20px 30px;
}
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.toolbar h2 {
  margin: 0;
  color: #1a4d8f;
}
</style>