<template>
  <div class="trip-page">
    <div class="page-title">出差申请单</div>

    <el-card class="form-card">
      <el-row :gutter="40">
        <!-- 左列 -->
        <el-col :xs="24" :md="12">
          <el-form label-width="100px">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="请输入出差申请单标题" />
            </el-form-item>
            <el-form-item label="申请人">
              <el-input v-model="form.applicant" />
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker v-model="form.startDate" type="datetime" placeholder="选择开始日期" style="width:100%" />
            </el-form-item>
            <el-form-item label="相关资料">
              <el-upload :auto-upload="false" multiple>
                <el-button text>
                  <el-icon><Upload /></el-icon>上传资料
                </el-button>
              </el-upload>
            </el-form-item>
            <el-form-item label="出差原因">
              <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请输入出差原因" />
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
            <el-form-item label="外出类型">
              <el-select v-model="form.tripType" style="width:100%">
                <el-option label="销售拜访" value="sales" />
                <el-option label="商务洽谈" value="business" />
                <el-option label="会议参会" value="meeting" />
                <el-option label="客户维护" value="client" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            <el-form-item label="结束日期">
              <el-date-picker v-model="form.endDate" type="datetime" placeholder="选择结束日期" style="width:100%" />
            </el-form-item>
            <el-form-item label="审核人员">
              <div style="display:flex;gap:8px;width:100%">
                <el-input v-model="form.auditor" placeholder="请选自己的上级" style="flex:1" />
                <el-button type="success" size="small" class="btn-green" @click="handleOpenAddressBook">
                  <el-icon><User /></el-icon>+通讯录
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
    </el-card>

    <!-- 底部按钮 -->
    <div class="form-footer">
      <el-button type="primary" class="btn-green" @click="handleSave">保存</el-button>
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

function handleOpenAddressBook() {
  addressBookVisible.value = true
}

function handleAddressBookSelect(user) {
  form.auditor = String(user.userId)
}

const form = reactive({
  title: '',
  applicant: '',
  startDate: '',
  reason: '',
  urgency: 'normal',
  tripType: 'sales',
  endDate: '',
  auditor: ''
})

async function handleSave() {
  try {
    await submitProcess({
      title: form.title,
      processName: '出差申请单',
      businessType: 'trip',
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
.trip-page {
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
    &:hover { background-color: #008d4c !important; border-color: #008d4c !important; }
  }
}
</style>
