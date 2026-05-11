<template>
  <div class="regular-page">
    <div class="page-title">转正申请单</div>

    <el-card class="form-card">
      <el-row :gutter="40">
        <!-- 左列 -->
        <el-col :xs="24" :md="12">
          <el-form label-width="120px">
            <el-form-item label="标题">
              <el-input v-model="form.title" placeholder="请输入转正申请单标题" />
            </el-form-item>
            <el-form-item label="开始日期">
              <el-date-picker
                v-model="form.startDate"
                type="datetime"
                placeholder="选择入职日期"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width:100%"
              />
            </el-form-item>
            <el-form-item label="申请人">
              <el-input v-model="form.applicant" />
            </el-form-item>
            <el-form-item label="实习/试用心得">
              <el-input v-model="form.experience" type="textarea" :rows="4" placeholder="请输入试用期工作心得与收获" />
            </el-form-item>
            <el-form-item label="实习/试用成长">
              <el-input v-model="form.growth" type="textarea" :rows="4" placeholder="请描述试用期内的能力提升与工作成果" />
            </el-form-item>
            <el-form-item label="如何做得更好">
              <el-input v-model="form.improvement" type="textarea" :rows="4" placeholder="请说明后续工作的改进计划与努力方向" />
            </el-form-item>
          </el-form>
        </el-col>

        <!-- 右列 -->
        <el-col :xs="24" :md="12">
          <el-form label-width="120px">
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
                placeholder="选择试用期结束日期"
                format="YYYY-MM-DD HH:mm"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width:100%"
              />
            </el-form-item>
            <el-form-item label="审核人员">
              <div style="display:flex;gap:8px;width:100%">
                <el-input v-model="form.auditor" placeholder="请选择自己的上级" style="flex:1" />
                <el-button type="success" size="small" class="btn-green" @click="handleOpenAddressBook">
                  <el-icon><User /></el-icon>+通讯录
                </el-button>
              </div>
            </el-form-item>
            <el-form-item label="本岗位职责理解">
              <el-input v-model="form.dutyUnderstanding" type="textarea" :rows="4" placeholder="请填写对当前岗位的职责认知与理解" />
            </el-form-item>
            <el-form-item label="目前存在的不足">
              <el-input v-model="form.shortcomings" type="textarea" :rows="4" placeholder="请反思试用期内的不足与待改进之处" />
            </el-form-item>
            <el-form-item label="对公司建议及意见">
              <el-input v-model="form.suggestion" type="textarea" :rows="4" placeholder="请填写对公司、部门或岗位的建议" />
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

function handleOpenAddressBook() {
  addressBookVisible.value = true
}

function handleAddressBookSelect(user) {
  form.auditor = String(user.userId)
}

const form = reactive({
  title: '',
  startDate: '2026-04-30 17:20:11',
  applicant: '罗密欧',
  experience: '',
  growth: '',
  improvement: '',
  urgency: 'normal',
  endDate: '2026-04-30 17:20:11',
  auditor: '',
  dutyUnderstanding: '',
  shortcomings: '',
  suggestion: ''
})

async function handleSave() {
  try {
    await submitProcess({
      title: form.title,
      processName: '转正申请单',
      businessType: 'regular',
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
.regular-page {
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
