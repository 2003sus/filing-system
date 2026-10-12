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
      <el-button @click="$router.push('/tasks')">返回任务列表</el-button>

      <div v-if="task" class="detail">
        <h2>任务详情 #{{ task.id }}</h2>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="案件ID">{{ task.caseId }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag>{{ task.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请编号">{{ task.applyNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="失败原因">{{ task.failReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ task.submitTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="复核人ID">{{ task.reviewerId || '-' }}</el-descriptions-item>
        </el-descriptions>

        <h3>任务快照</h3>
        <pre class="snapshot">{{ formatSnapshot(task.snapshot) }}</pre>

        <div v-if="task.status === 'WAIT_REVIEW' && (role === 'REVIEWER' || role === 'ADMIN')" class="actions">
          <el-button type="success" @click="submit">确认提交</el-button>
          <el-button type="danger" @click="rejectDialog = true">退回修改</el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="rejectDialog" title="退回原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" rows="3" placeholder="请输入退回原因" />
      <template #footer>
        <el-button @click="rejectDialog = false">取消</el-button>
        <el-button type="primary" @click="doReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { taskApi } from '@/api'

const task = ref<any>(null)
const rejectDialog = ref(false)
const rejectReason = ref('')
const role = localStorage.getItem('role') || ''
const realName = localStorage.getItem('realName') || ''
const route = useRoute()
const router = useRouter()

const taskId = Number(route.params.id)

async function loadTask() {
  const res: any = await taskApi.detail(taskId)
  if (res.code === 200) task.value = res.data
}

function formatSnapshot(s: string) {
  try { return JSON.stringify(JSON.parse(s), null, 2) } catch { return s }
}

async function submit() {
  try {
    const res: any = await taskApi.submit(taskId)
    if (res.code === 200) {
      ElMessage.success('提交成功')
      loadTask()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '提交失败')
  }
}

async function doReject() {
  if (!rejectReason.value) {
    ElMessage.warning('请输入退回原因')
    return
  }
  try {
    const res: any = await taskApi.reject(taskId, rejectReason.value)
    if (res.code === 200) {
      ElMessage.success('已退回')
      rejectDialog.value = false
      loadTask()
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '退回失败')
  }
}

function logout() {
  localStorage.clear()
  router.push('/login')
}

onMounted(loadTask)
</script>

<style scoped>
.layout { min-height: 100vh; background: #f5f7fa; }
.header { display: flex; justify-content: space-between; align-items: center; padding: 0 30px; height: 60px; background: #1a4d8f; color: white; }
.logo { font-size: 20px; font-weight: bold; }
.user-info { display: flex; align-items: center; gap: 15px; }
.main { padding: 20px 30px; }
.detail { margin-top: 20px; background: white; padding: 20px; border-radius: 8px; }
.detail h2 { color: #1a4d8f; }
.detail h3 { margin-top: 20px; color: #1a4d8f; }
.snapshot { background: #f5f7fa; padding: 15px; border-radius: 4px; font-size: 12px; overflow-x: auto; max-height: 300px; }
.actions { margin-top: 20px; display: flex; gap: 10px; }
</style>