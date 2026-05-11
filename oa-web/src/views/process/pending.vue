<template>
  <div class="process-pending-page">
    <el-card class="box-card">
      <template #header><span>待我审批</span></template>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="processName" label="申请类型" width="120" />
        <el-table-column label="内容" min-width="240">
          <template #default="{ row }">
            <div class="content-detail">
              <div class="content-row">
                <span class="content-label">标题：</span>{{ row.title }}
              </div>
              <template v-if="parsedForm(row).businessType === 'leave'">
                <div class="content-row">
                  <span class="content-label">请假类型：</span>{{ formatLeaveType(parsedForm(row).leaveType) }}
                </div>
                <div class="content-row">
                  <span class="content-label">日期：</span>{{ parsedForm(row).startDate }} ~ {{ parsedForm(row).endDate }}
                </div>
              </template>
              <template v-else-if="parsedForm(row).businessType === 'overtime'">
                <div class="content-row">
                  <span class="content-label">加班类型：</span>{{ formatOvertimeType(parsedForm(row).overtimeType) }}
                </div>
                <div class="content-row">
                  <span class="content-label">时间：</span>{{ parsedForm(row).startTime }} ~ {{ parsedForm(row).endTime }}
                </div>
              </template>
              <template v-else-if="parsedForm(row).businessType === 'trip'">
                <div class="content-row">
                  <span class="content-label">外出类型：</span>{{ parsedForm(row).tripType }}
                </div>
                <div class="content-row">
                  <span class="content-label">日期：</span>{{ parsedForm(row).startDate }} ~ {{ parsedForm(row).endDate }}
                </div>
              </template>
              <template v-else-if="parsedForm(row).businessType === 'regular'">
                <div class="content-row">
                  <span class="content-label">开始日期：</span>{{ parsedForm(row).startDate }}
                </div>
                <div class="content-row">
                  <span class="content-label">结束日期：</span>{{ parsedForm(row).endDate }}
                </div>
              </template>
              <template v-else-if="parsedForm(row).businessType === 'resignation'">
                <div class="content-row">
                  <span class="content-label">审核人员：</span>{{ parsedForm(row).auditor }}
                </div>
                <div class="content-row">
                  <span class="content-label">交接人员：</span>{{ parsedForm(row).handoverPerson }}
                </div>
              </template>
              <template v-else-if="parsedForm(row).businessType === 'expense'">
                <div class="content-row">
                  <span class="content-label">相关客户：</span>{{ parsedForm(row).client }}
                </div>
                <div class="content-row">
                  <span class="content-label">证明人：</span>{{ parsedForm(row).witness }}
                </div>
              </template>
              <div class="content-row" v-if="parsedForm(row).reason">
                <span class="content-label">原因：</span>{{ parsedForm(row).reason }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="100" />
        <el-table-column label="申请时间" width="170">
          <template #default="{ row }">{{ row.startTime ? dayjs(row.startTime).format('MM-DD HH:mm') : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button type="success" size="small" :loading="approvingId === row.id" @click="handleApprove(row, 'approve')">通过</el-button>
            <el-button type="danger" size="small" :loading="approvingId === row.id" @click="handleApprove(row, 'reject')">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import { getPendingProcessList, approveProcess } from '@/api/process'

const tableData = ref([])
const loading = ref(false)
const approvingId = ref(null)

const leaveTypeMap = {
  annual: '年假',
  personal: '事假',
  sick: '病假',
  marriage: '婚假',
  maternity: '产假',
  funeral: '丧假',
  other: '其他'
}

const overtimeTypeMap = {
  weekday: '工作日加班',
  weekend: '周末加班',
  holiday: '节假日加班'
}

function formatLeaveType(type) {
  return leaveTypeMap[type] || type || '-'
}

function formatOvertimeType(type) {
  return overtimeTypeMap[type] || type || '-'
}

// 缓存解析后的表单数据
const formDataCache = {}

function parsedForm(row) {
  if (!row.id) return {}
  if (formDataCache[row.id]) return formDataCache[row.id]
  try {
    const data = typeof row.formData === 'string' ? JSON.parse(row.formData) : (row.formData || {})
    data.businessType = data.businessType || row.businessType || ''
    formDataCache[row.id] = data
    return data
  } catch {
    formDataCache[row.id] = { businessType: row.businessType || '' }
    return formDataCache[row.id]
  }
}

async function loadData() {
  loading.value = true
  // 清空缓存
  Object.keys(formDataCache).forEach(k => delete formDataCache[k])
  try {
    const res = await getPendingProcessList()
    if (res.code === 200) tableData.value = res.data || []
  } catch (e) { console.error('加载失败', e) }
  finally { loading.value = false }
}

async function handleApprove(row, action) {
  const text = action === 'approve' ? '通过' : '拒绝'
  try {
    const { value: comment } = await ElMessageBox.prompt(`请输入${text}意见`, `${text}审批`, { inputType: 'textarea', inputPlaceholder: '选填', confirmButtonText: '确定', cancelButtonText: '取消' })
    approvingId.value = row.id
    await approveProcess(row.id, { action, comment: comment || '' })
    ElMessage.success(`${text}成功`)
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(`${text}失败`)
  } finally {
    approvingId.value = null
  }
}

onMounted(() => loadData())
</script>

<style scoped lang="scss">
.process-pending-page {
  .box-card { border-top: 3px solid #00a65a; }

  .content-detail {
    font-size: 13px;
    line-height: 1.8;

    .content-row {
      .content-label {
        color: #909399;
        font-weight: 500;
      }
    }
  }
}
</style>
