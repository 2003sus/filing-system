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
        <h2>用户管理</h2>
        <el-button @click="$router.push('/cases')">返回案件列表</el-button>
      </div>

      <el-table :data="users" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.role }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button size="small" type="danger"
              @click="disable(row.id)"
              :disabled="!row.enabled">
              禁用
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api'

const users = ref([])
const loading = ref(false)
const role = localStorage.getItem('role') || ''
const realName = localStorage.getItem('realName') || ''
const router = useRouter()

async function loadUsers() {
  loading.value = true
  try {
    const res: any = await userApi.list()
    if (res.code === 200) users.value = res.data
  } finally {
    loading.value = false
  }
}

async function disable(id: number) {
  try {
    await ElMessageBox.confirm('确定禁用该用户？', '提示', { type: 'warning' })
    const res: any = await userApi.disable(id)
    if (res.code === 200) {
      ElMessage.success('已禁用')
      loadUsers()
    }
  } catch {}
}

function logout() {
  localStorage.clear()
  router.push('/login')
}

onMounted(loadUsers)
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