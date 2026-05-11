<template>
  <div class="position-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>职位管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增职位
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="positionName" label="职位名称" />
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑职位' : '新增职位'" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="职位名称" prop="positionName">
          <el-input v-model="form.positionName" placeholder="请输入职位名称" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPositions, createPosition, updatePosition, deletePosition } from '@/api/system'

const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  positionName: '',
  remark: ''
})

const rules = {
  positionName: [{ required: true, message: '请输入职位名称', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getPositions({ page: currentPage.value, pageSize: pageSize.value })
    tableData.value = res.data?.list || res.data || []
    total.value = res.data?.total || tableData.value.length
  } catch (error) {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, positionName: '', remark: '' })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  try {
    await formRef.value.validate()
    if (form.id) {
      await updatePosition(form)
    } else {
      await createPosition(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('保存失败')
    }
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该职位吗？', '提示', { type: 'warning' })
    await deletePosition(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped lang="scss">
.position-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 15px;
  }
}

.el-button--primary {
  color: #fff;
}
</style>
