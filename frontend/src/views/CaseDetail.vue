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
      <el-button @click="$router.push('/cases')">返回案件列表</el-button>

      <div v-if="caseData" class="detail">
        <h2>案件详情 #{{ caseData.id }}</h2>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="案件编号">{{ caseData.caseNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="caseData.status === 'SUBMITTED' ? 'success' : 'info'">
              {{ caseData.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="案由">{{ caseData.title }}</el-descriptions-item>
          <el-descriptions-item label="金额">{{ caseData.amount }}</el-descriptions-item>
          <el-descriptions-item label="原告">{{ caseData.plaintiff }}</el-descriptions-item>
          <el-descriptions-item label="被告">{{ caseData.defendant }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="2">{{ caseData.description }}</el-descriptions-item>
        </el-descriptions>

        <h3>材料列表</h3>
        <div class="upload-row">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            name="file"
            :show-file-list="false"
            :on-success="onUploadSuccess"
            :on-error="onUploadError"
            :before-upload="beforeUpload"
          >
            <el-button type="primary" :disabled="caseData.status === 'SUBMITTED'">
              上传材料
            </el-button>
          </el-upload>
        </div>

        <el-table :data="materials" border style="margin-top:15px">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="fileName" label="文件名" />
          <el-table-column prop="fileType" label="类型" width="100" />
          <el-table-column prop="fileSize" label="大小(字节)" width="130" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="danger"
                @click="deleteMaterial(row.id)"
                :disabled="caseData.status === 'SUBMITTED'">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="actions">
          <el-button type="success" @click="createTask"
            :disabled="caseData.status === 'SUBMITTED'">
            创建立案任务
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { caseApi, taskApi } from '@/api'
import request from '@/api/request'

const caseData = ref<any>(null)
const materials = ref([])
const role = localStorage.getItem('role') || ''
const realName = localStorage.getItem('realName') || ''
const route = useRoute()
const router = useRouter()
const caseId = Number(route.params.id)

const uploadUrl = computed(() => `/api/cases/${caseId}/materials`)
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
}))

async function loadCase() {
  const res: any = await caseApi.detail(caseId)
  if (res.code === 200) caseData.value = res.data
}

async function loadMaterials() {
  try {
    const res: any = await request.get(`/cases/${caseId}/materials`)
    if (res.code === 200) materials.value = res.data
  } catch {}
}

function beforeUpload(file: File) {
  const allowed = ['pdf', 'jpeg', 'jpg', 'png']
  const ext = file.name.split('.').pop()?.toLowerCase() || ''
  if (!allowed.includes(ext)) {
    ElMessage.error('只允许 PDF/JPEG/PNG')
    return false
  }
  if (file.size > 20 * 1024 * 1024) {
    ElMessage.error('文件超过20MB')
    return false
  }
  return true
}

function onUploadSuccess(res: any) {
  if (res.code === 200) {
    ElMessage.success('上传成功')
    loadMaterials()
  } else {
    ElMessage.error(res.message)
  }
}

function onUploadError() {
  ElMessage.error('上传失败')
}

async function deleteMaterial(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该材料？', '提示')
    const res: any = await request.delete(`/cases/${caseId}/materials/${id}`)
    if (res.code === 200) {
      ElMessage.success('已删除')
      loadMaterials()
    }
  } catch {}
}

async function createTask() {
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

onMounted(() => {
  loadCase()
  loadMaterials()
})
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
.upload-row { margin-top: 10px; }
.actions { margin-top: 20px; }
</style>