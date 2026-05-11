<template>
  <div class="expense-page">
    <div class="page-title">费用报销单</div>

    <el-card class="form-card">
      <el-row :gutter="40">
        <!-- 左列 -->
        <el-col :xs="24" :md="12">
          <el-form label-width="100px">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="请输入报销单标题" />
            </el-form-item>
            <el-form-item label="提单人员">
              <el-input v-model="form.applicant" />
            </el-form-item>
            <el-form-item label="相关客户">
              <el-input v-model="form.client" placeholder="请输入相关客户" />
            </el-form-item>
            <el-form-item label="审核人员">
              <div style="display:flex;gap:8px;width:100%">
                <el-input v-model="form.auditor" placeholder="请选自己的上级" style="flex:1" />
                <el-button type="success" size="small" class="btn-green" @click="handleOpenAddressBook('auditor')">
                  <el-icon><User /></el-icon>+通讯录
                </el-button>
              <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请输入报销事由" />
            </el-form-item>
          </el-form>
        </el-col>

        <!-- 右列 -->
        <el-col :xs="24" :md="12">
          <el-form label-width="100px">
            <el-form-item label="紧急程度">
              <el-select v-model="form.urgency" style="width:100%">
                <el-option label="紧急" value="urgent" />
                <el-option label="正常" value="normal" />
                <el-option label="一般" value="low" />
              </el-select>
            </el-form-item>
            <el-form-item label="证明人">
              <div style="display:flex;gap:8px;width:100%">
                <el-input v-model="form.witness" placeholder="请输入证明人" style="flex:1" />
                <el-button type="success" size="small" class="btn-green" @click="handleOpenAddressBook('witness')">
                  <el-icon><User /></el-icon>+通讯录
                </el-button>
              <el-select v-model="form.method" style="width:100%">
                <el-option label="银行卡" value="bank" />
                <el-option label="现金" value="cash" />
                <el-option label="转账" value="transfer" />
              </el-select>
            </el-form-item>
            <el-form-item label="相关票据">
              <el-upload class="upload-wrap" :auto-upload="false" multiple>
                <el-button type="primary" text>
                  <el-icon><Upload /></el-icon>上传票据
                </el-button>
              </el-upload>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>

    <!-- 报销明细 -->
    <el-card class="detail-card">
      <template #header>
        <div class="detail-header">
          <span>报销明细</span>
          <div class="detail-actions">
            <el-button size="small" type="primary" circle @click="addDetailRow"><el-icon><Plus /></el-icon></el-button>
            <el-button size="small" type="danger" circle @click="removeDetailRow"><el-icon><Minus /></el-icon></el-button>
          </div>
        </div>
      </template>
      <el-table :data="detailRows" border stripe>
        <el-table-column type="selection" width="40" align="center" />
        <el-table-column label="费用日期" width="180">
          <template #default="{ row }">
            <el-date-picker v-model="row.date" type="datetime" style="width:100%" />
          </template>
        </el-table-column>
        <el-table-column label="费用科目" width="150">
          <template #default="{ row }">
            <el-input v-model="row.subject" placeholder="选择科目">
              <template #suffix><el-icon><Search /></el-icon></template>
            </el-input>
          </template>
        </el-table-column>
        <el-table-column label="费用说明" min-width="180">
          <template #default="{ row }">
            <el-input v-model="row.desc" placeholder="请输入" />
          </template>
        </el-table-column>
        <el-table-column label="票据张数" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.ticketCount" :min="0" style="width:100%" />
          </template>
        </el-table-column>
        <el-table-column label="报销金额" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.amount" :min="0" :precision="2" style="width:100%" />
          </template>
        </el-table-column>
      </el-table>
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
  } else if (addressBookTarget.value === 'witness') {
    form.witness = `${user.realName}(${user.username})`
  }
}

const form = reactive({
  title: '',
  applicant: '',
  client: '',
  auditor: '',
  reason: '',
  urgency: 'normal',
  witness: '',
  method: 'bank'
})

const selectedRows = ref([])
const detailRows = ref([
  { date: '2026-04-30 16:59:01', subject: '', desc: '', ticketCount: 0, amount: 0 }
])

function addDetailRow() {
  detailRows.value.push({ date: '', subject: '', desc: '', ticketCount: 0, amount: 0 })
}

function removeDetailRow() {
  if (detailRows.value.length <= 1) return ElMessage.warning('至少保留一行')
  detailRows.value = detailRows.value.filter((_, i) => !selectedRows.value.includes(i))
}

async function handleSave() {
  try {
    await submitProcess({
      title: form.title,
      processName: '费用报销单',
      businessType: 'expense',
      auditor: form.auditor,
      formData: JSON.stringify({
        client: form.client, reason: form.reason, urgency: form.urgency,
        witness: form.witness, method: form.method, details: detailRows.value
      })
    })
    ElMessage.success('提交成功')
  } catch (error) {
    ElMessage.error('提交失败')
  }
  goBack()
}

function goBack() {
  router.push('/process/new')
}
</script>

<style scoped lang="scss">
.expense-page {
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

  .detail-card {
    border-top: 3px solid #00a65a;
    margin-bottom: 20px;

    .detail-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .detail-actions {
      display: flex;
      gap: 8px;
    }
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

  .upload-wrap {
    :deep(.el-upload-list) { margin-top: 8px; }
  }
}

.btn-primary-green {
  background-color: #00a65a !important;
  border-color: #00a65a !important;
  color: #fff !important;
  &:hover { background-color: #008d4c !important; }
}
</style>
