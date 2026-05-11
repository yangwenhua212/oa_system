<template>
  <div class="resignation-page">
    <div class="page-title">离职申请单</div>

    <el-card class="form-card">
      <el-row :gutter="40">
        <!-- 左列 -->
        <el-col :xs="24" :md="12">
          <el-form label-width="100px">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="请输入离职申请单标题" />
            </el-form-item>
            <el-form-item label="申请人">
              <el-input v-model="form.applicant" />
            </el-form-item>
            <el-form-item label="未完成事宜">
              <el-input v-model="form.pendingTasks" type="textarea" :rows="4" placeholder="请输入未完成的工作事宜" />
            </el-form-item>
            <el-form-item label="审核人员">
              <div style="display:flex;gap:8px;width:100%">
                <el-input v-model="form.auditor" placeholder="请选择自己的上级" style="flex:1" />
                <el-button type="success" size="small" class="btn-green" @click="handleOpenAddressBook('auditor')">
                  <el-icon><User /></el-icon>+通讯录
                </el-button>
        </el-col>

        <!-- 右列 -->
        <el-col :xs="24" :md="12">
          <el-form label-width="140px">
            <el-form-item label="紧急程度">
              <el-select v-model="form.urgency" style="width:100%">
                <el-option label="紧急" value="urgent" />
                <el-option label="正常" value="normal" />
                <el-option label="一般" value="low" />
              </el-select>
            </el-form-item>
            <el-form-item label="交接人员">
              <div style="display:flex;gap:8px;width:100%">
                <el-input v-model="form.handoverPerson" placeholder="请选择交接人员" style="flex:1" />
                <el-button type="success" size="small" class="btn-green" @click="handleOpenAddressBook('handoverPerson')">
                  <el-icon><User /></el-icon>+通讯录
                </el-button>
              <el-input v-model="form.resignationReason" type="textarea" :rows="4" placeholder="请输入离职原因" />
            </el-form-item>
            <el-form-item label="是否有费用报销未完成">
              <el-checkbox v-model="form.hasPendingExpense" />
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>

    <!-- 底部按钮 -->
    <div class="form-footer">
      <el-button type="primary" class="btn-primary-green" @click="handleSave">保存</el-button>
      <el-button @click="goBack">取消</el-button>
    </div>
  </div>

  <!-- 通讯录弹窗 -->
  <AddressBookDialog v-model="addressBookVisible" @select="handleAddressBookSelect" />
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitProcess } from '@/api/process'
import AddressBookDialog from '@/components/AddressBookDialog.vue'

const router = useRouter()

const addressBookVisible = ref(false)
const addressBookTarget = ref('')

function handleOpenAddressBook(target) {
  addressBookTarget.value = target
  addressBookVisible.value = true
}

function handleAddressBookSelect(user) {
  if (addressBookTarget.value === 'auditor') {
    form.auditor = String(user.userId)
  } else if (addressBookTarget.value === 'handoverPerson') {
    form.handoverPerson = `${user.realName}(${user.username})`
  }
}

const form = reactive({
  title: '',
  applicant: '罗密欧',
  pendingTasks: '',
  auditor: '',
  urgency: 'normal',
  handoverPerson: '',
  resignationReason: '',
  hasPendingExpense: false
})

async function handleSave() {
  try {
    await submitProcess({
      title: form.title,
      processName: '离职申请单',
      businessType: 'resignation',
      auditor: form.auditor,
      formData: JSON.stringify({ ...form })
    })
    ElMessage.success('提交成功')
    goBack()
  } catch (error) {
    ElMessage.error('提交失败')
  }
}

function goBack() {
  router.push('/process/new')
}
</script>

<style scoped lang="scss">
.resignation-page {
  max-width: 1000px;
  margin: 0 auto;

  .page-title {
    text-align: center;
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
  }

  .form-card {
    border-top: 3px solid #00a65a;
    margin-bottom: 20px;
  }

  .form-footer {
    display: flex;
    gap: 12px;
    justify-content: center;
    padding: 10px 0 20px;
  }

  .btn-green {
    background-color: #00a65a !important;
    border-color: #00a65a !important;
    color: #fff !important;
    white-space: nowrap;
    &:hover {
      background-color: #008d4c !important;
      border-color: #008d4c !important;
    }
  }
}

.btn-primary-green {
  background-color: #00a65a !important;
  border-color: #00a65a !important;
  color: #fff !important;
  &:hover { background-color: #008d4c !important; }
}
</style>
