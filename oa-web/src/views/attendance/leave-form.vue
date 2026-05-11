<template>
  <div class="leave-form-page">
    <div class="page-title">请假申请单</div>

    <el-card class="form-card">
      <el-row :gutter="40">
        <!-- 左列 -->
        <el-col :xs="24" :md="12">
          <el-form label-width="100px">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="请输入请假申请单标题" />
            </el-form-item>
            <el-form-item label="申请人">
              <el-input v-model="form.applicant" />
            </el-form-item>
            <el-form-item label="请假类型">
              <el-select v-model="form.leaveType" style="width:100%">
                <el-option label="年假" value="annual" />
                <el-option label="事假" value="personal" />
                <el-option label="病假" value="sick" />
                <el-option label="婚假" value="marriage" />
                <el-option label="产假" value="maternity" />
                <el-option label="丧假" value="funeral" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="form.startDate"
                type="datetime"
                placeholder="选择开始日期"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width:100%"
              />
            </el-form-item>
            <el-form-item label="请假原因">
              <el-input v-model="form.reason" type="textarea" :rows="4" placeholder="请输入请假原因" />
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
            <el-form-item label="结束日期">
              <el-date-picker
                v-model="form.endDate"
                type="datetime"
                placeholder="选择结束日期"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width:100%"
              />
            </el-form-item>
            <el-form-item label="请假天数">
              <span class="duration-display">{{ calculatedDays }} 天</span>
            </el-form-item>
            <el-form-item label="审核人员">
              <div style="display:flex;gap:8px;width:100%">
                <el-input v-model="form.auditor" placeholder="请选择自己的上级" style="flex:1" />
                <el-button type="success" size="small" class="btn-green" @click="handleOpenAddressBook">
                  <el-icon><User /></el-icon>+通讯录
                </el-button>
              </div>
            </el-form-item>
            <el-form-item label="相关附件">
              <el-upload :auto-upload="false" multiple>
                <el-button text>
                  <el-icon><Upload /></el-icon>上传附件
                </el-button>
              </el-upload>
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
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
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
  applicant: '罗密欧',
  leaveType: 'annual',
  startDate: '',
  endDate: '',
  reason: '',
  urgency: 'normal',
  auditor: ''
})

// 计算请假天数
const calculatedDays = computed(() => {
  if (!form.startDate || !form.endDate) return 0
  const start = dayjs(form.startDate)
  const end = dayjs(form.endDate)
  if (end.isBefore(start)) return 0
  const days = end.diff(start, 'day') + 1
  return days
})

async function handleSave() {
  try {
    await submitProcess({
      title: form.title,
      processName: '请假申请单',
      businessType: 'leave',
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
.leave-form-page {
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

  .duration-display {
    color: #00a65a;
    font-weight: bold;
    font-size: 16px;
  }
}

.btn-primary-green {
  background-color: #00a65a !important;
  border-color: #00a65a !important;
  color: #fff !important;
  &:hover { background-color: #008d4c !important; }
}
</style>
