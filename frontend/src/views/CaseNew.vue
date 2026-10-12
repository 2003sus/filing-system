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

      <div class="form-box">
        <h2>新建案件</h2>
        <el-form :model="form" label-width="100px">
          <el-form-item label="案由" required>
            <el-input v-model="form.title" placeholder="例如：买卖合同纠纷" />
          </el-form-item>
          <el-form-item label="原告" required>
            <el-input v-model="form.plaintiff" />
          </el-form-item>
          <el-form-item label="被告" required>
            <el-input v-model="form.defendant" />
          </el-form-item>
          <el-form-item label="金额">
            <el-input-number v-model="form.amount" :min="0" :precision="2" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="form.description" type="textarea" rows="3" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submit" :loading="loading">提交</el-button>
            <el-button @click="$router.push('/cases')">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { caseApi } from '@/api'

const form = ref({
  title: '',
  plaintiff: '',
  defendant: '',
  amount: 0,
  description: ''
})
const loading = ref(false)
const role = localStorage.getItem('role') || ''
const realName = localStorage.getItem('realName') || ''
const router = useRouter()

async function submit() {
  if (!form.value.title || !form.value.plaintiff || !form.value.defendant) {
    ElMessage.warning('请填写案由、原告、被告')
    return
  }
  loading.value = true
  try {
    const res: any = await caseApi.create(form.value)
    if (res.code === 200) {
      ElMessage.success('案件创建成功')
      router.push('/cases')
    } else {
      ElMessage.error(res.message)
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '创建失败')
  } finally {
    loading.value = false
  }
}

function logout() {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.layout { min-height: 100vh; background: #f5f7fa; }
.header { display: flex; justify-content: space-between; align-items: center; padding: 0 30px; height: 60px; background: #1a4d8f; color: white; }
.logo { font-size: 20px; font-weight: bold; }
.user-info { display: flex; align-items: center; gap: 15px; }
.main { padding: 20px 30px; }
.form-box { margin-top: 20px; background: white; padding: 30px; border-radius: 8px; max-width: 700px; }
.form-box h2 { color: #1a4d8f; margin-bottom: 20px; }
</style>