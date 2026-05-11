<template>
  <el-dialog
    v-model="visible"
    title="选择联系人"
    width="750px"
    :close-on-click-modal="false"
    @open="handleOpen"
    @closed="handleClosed"
  >
    <div class="address-book-dialog">
      <!-- 操作按钮 -->
      <div class="dialog-actions">
        <el-button type="success" size="small" class="btn-green" :disabled="!selectedUser" @click="handleConfirm">
          <el-icon><Plus /></el-icon> 新增接收人
        </el-button>
        <el-button type="success" size="small" class="btn-green" :disabled="!selectedUser" @click="handleConfirm">
          <el-icon><Plus /></el-icon> 追加接收人
        </el-button>
        <el-button size="small" @click="visible = false">取消</el-button>
      </div>

      <!-- 搜索 -->
      <div class="dialog-search">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索姓名、部门..."
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <!-- 联系人表格 -->
      <el-table
        :data="displayList"
        stripe
        v-loading="loading"
        highlight-current-row
        @current-change="handleCurrentChange"
        @row-dblclick="handleRowDblClick"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="deptName" label="部门" width="140" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="username" label="用户名" width="100" />
        <el-table-column prop="positionName" label="职位" width="100" />
        <el-table-column prop="phone" label="电话" min-width="130" />
      </el-table>

      <!-- 分页 -->
      <div class="dialog-pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handlePageChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getInternalContacts } from '@/api/address'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'select'])

// 弹窗可见性
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 状态变量
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const allContacts = ref([])
const selectedUser = ref(null)

// 处理后的显示列表 (前端分页)
const displayList = computed(() => {
  let list = allContacts.value

  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    list = list.filter(c =>
      c.realName?.toLowerCase().includes(keyword) ||
      c.username?.toLowerCase().includes(keyword) ||
      c.deptName?.toLowerCase().includes(keyword) ||
      c.phone?.includes(keyword)
    )
  }

  total.value = list.length

  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  return list.slice(start, start + pageSize.value)
})

// 弹窗打开时加载数据
async function handleOpen() {
  loading.value = true
  currentPage.value = 1
  searchKeyword.value = ''
  selectedUser.value = null
  try {
    const res = await getInternalContacts()
    allContacts.value = res.data || []
    total.value = allContacts.value.length
  } catch (error) {
    console.error('加载内部联系人失败:', error)
    ElMessage.error('加载联系人失败')
    allContacts.value = []
  } finally {
    loading.value = false
  }
}

function handleClosed() {
  allContacts.value = []
  selectedUser.value = null
}

// 搜索
function handleSearch() {
  currentPage.value = 1
}

// 选中行
function handleCurrentChange(row) {
  selectedUser.value = row
}

// 双击行确认
function handleRowDblClick(row) {
  confirmSelect(row)
}

// 确认选择
function handleConfirm() {
  if (!selectedUser.value) {
    ElMessage.warning('请选择一个联系人')
    return
  }
  confirmSelect(selectedUser.value)
}

function confirmSelect(user) {
  emit('select', {
    userId: user.userId,
    realName: user.realName,
    username: user.username,
    phone: user.phone,
    deptName: user.deptName,
    positionName: user.positionName
  })
  ElMessage.success(`已选择: ${user.realName}`)
  visible.value = false
}

function handlePageChange() {
  // computed会自动处理
}
</script>

<style scoped>
.address-book-dialog {
  .dialog-actions {
    display: flex;
    gap: 10px;
    margin-bottom: 16px;
  }

  .dialog-search {
    margin-bottom: 16px;
  }

  .dialog-pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }
}

.btn-green {
  background-color: #00a65a !important;
  border-color: #00a65a !important;
  color: #fff !important;
  &:hover {
    background-color: #008d4c !important;
    border-color: #008d4c !important;
  }
}
</style>
