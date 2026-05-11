<template>
  <div class="notice-publish">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑公告' : '发布公告' }}</span>
          <el-button @click="goBack">
            <el-icon><ArrowLeft /></el-icon>返回列表
          </el-button>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="notice-form">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>

        <el-form-item label="公告类型" prop="noticeType">
          <el-radio-group v-model="form.noticeType">
            <el-radio value="system">系统通知</el-radio>
            <el-radio value="activity">活动公告</el-radio>
            <el-radio value="meeting">会议通知</el-radio>
            <el-radio value="important">重要提醒</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请输入公告内容" maxlength="5000" show-word-limit />
        </el-form-item>

        <el-form-item label="附件上传">
          <el-upload
            ref="uploadRef"
            :file-list="uploadedFiles"
            :http-request="customUpload"
            :on-remove="handleRemove"
            :on-exceed="handleExceed"
            :before-upload="checkFile"
            list-type="text"
            :limit="5"
            accept=".doc,.docx,.pdf,.jpg,.jpeg,.png,.xlsx,.xls,.zip"
            multiple
          >
            <el-button type="primary" text>
              <el-icon><Upload /></el-icon>选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">支持 doc/pdf/jpg/png/xlsx/zip，最多 5 个文件，单个不超过 10MB</div>
            </template>
          </el-upload>
          <div v-if="uploadingFiles.length > 0" class="uploading-list">
            <div v-for="f in uploadingFiles" :key="f.name" class="uploading-item">
              <el-icon class="uploading-icon"><Loading /></el-icon>
              <span>{{ f.name }}</span>
              <el-progress :percentage="f.progress" :width="60" type="circle" :stroke-width="4" size="small" />
            </div>
          </div>
        </el-form-item>

        <el-form-item label="发布范围" prop="publishScope">
          <el-select v-model="form.publishScope" placeholder="请选择发布范围" style="width: 100%">
            <el-option label="全公司" value="all" />
          </el-select>
        </el-form-item>

        <el-form-item label="置顶公告">
          <el-switch v-model="form.isTop" />
          <span class="form-tip">置顶公告将在列表顶部显示</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            {{ isEdit ? '保存修改' : '立即发布' }}
          </el-button>
          <el-button @click="saveDraft" :loading="draftLoading">保存草稿</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { publishNotice, updateNotice, getNoticeById } from '@/api/notice'
import { uploadFile } from '@/api/file'

const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const uploadRef = ref(null)
const loading = ref(false)
const draftLoading = ref(false)

const isEdit = computed(() => !!route.query.id)

const form = reactive({
  title: '',
  noticeType: 'system',
  content: '',
  publishScope: 'all',
  isTop: false,
  isScheduled: false,
  scheduledTime: ''
})

const rules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  noticeType: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  publishScope: [{ required: true, message: '请选择发布范围', trigger: 'change' }]
}

// 已上传的文件列表（用于 el-upload 显示）
const uploadedFiles = ref([])
// 上传中的文件
const uploadingFiles = ref([])
// 已上传成功的文件ID列表
const uploadedFileIds = ref([])

// 检查文件类型大小
const checkFile = (rawFile) => {
  const maxSize = 10 * 1024 * 1024
  if (rawFile.size > maxSize) {
    ElMessage.error('单个文件不能超过 10MB')
    return false
  }
  return true
}

// 自定义上传（http-request）
const customUpload = async (options) => {
  const { file, onSuccess, onError } = options
  const formData = new FormData()
  formData.append('file', file)
  formData.append('model', 'notice')

  const upItem = { name: file.name, progress: 0 }
  uploadingFiles.value.push(upItem)

  try {
    const res = await uploadFile(formData)
    if (res.code === 200 && res.data) {
      const { fileId, fileName } = res.data
      uploadedFileIds.value.push(fileId)
      uploadedFiles.value.push({ name: fileName, url: '', fileId })
      upItem.progress = 100
      onSuccess(res)
    } else {
      onError(new Error('上传失败'))
      ElMessage.error(`文件 ${file.name} 上传失败`)
    }
  } catch (error) {
    onError(error)
    ElMessage.error(`文件 ${file.name} 上传失败`)
  } finally {
    setTimeout(() => {
      uploadingFiles.value = uploadingFiles.value.filter(f => f.name !== file.name)
    }, 1000)
  }
}

const handleRemove = (file) => {
  uploadedFiles.value = uploadedFiles.value.filter(f => f.name !== file.name)
  if (file.fileId) {
    uploadedFileIds.value = uploadedFileIds.value.filter(id => id !== file.fileId)
  }
}

const handleExceed = () => {
  ElMessage.warning('最多只能上传 5 个文件')
}

const disabledDate = (time) => time.getTime() < Date.now() - 8.64e7

// 组装提交数据
function buildNoticeData(status) {
  return {
    title: form.title,
    content: form.content,
    noticeType: form.noticeType,
    publishScope: form.publishScope,
    isTop: form.isTop ? 1 : 0,
    status,
    attachmentIds: uploadedFileIds.value.length > 0 ? uploadedFileIds.value.join(',') : null
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const data = buildNoticeData(1)
      if (isEdit.value) {
        data.id = Number(route.query.id)
        await updateNotice(data)
        ElMessage.success('公告修改成功')
      } else {
        await publishNotice(data)
        ElMessage.success('公告发布成功')
      }
      router.push('/notice/list')
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    } finally {
      loading.value = false
    }
  })
}

const saveDraft = async () => {
  draftLoading.value = true
  try {
    const data = buildNoticeData(0)
    if (isEdit.value) {
      data.id = Number(route.query.id)
      await updateNotice(data)
    } else {
      await publishNotice(data)
    }
    ElMessage.success('草稿保存成功')
  } catch (error) {
    ElMessage.error('保存失败，请重试')
  } finally {
    draftLoading.value = false
  }
}

const goBack = () => router.push('/notice/list')

const loadEditData = async () => {
  if (!isEdit.value) return
  try {
    const res = await getNoticeById(route.query.id)
    if (res.code === 200 && res.data) {
      const d = res.data
      form.title = d.title || ''
      form.noticeType = d.noticeType || 'system'
      form.content = d.content || ''
      form.publishScope = d.publishScope || 'all'
      form.isTop = d.isTop === 1
      // 回显附件
      if (d.attachments && d.attachments.length > 0) {
        uploadedFiles.value = d.attachments.map(a => ({
          name: a.name,
          url: a.filePath,
          fileId: a.fileId
        }))
        uploadedFileIds.value = d.attachments.map(a => a.fileId)
      }
    }
  } catch (error) {
    console.error('加载公告数据失败', error)
  }
}

onMounted(() => {
  loadEditData()
})
</script>

<style scoped lang="scss">
.notice-publish {
  .box-card {
    border-top: 3px solid #00a65a;
  }
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .notice-form {
    max-width: 900px;
    padding: 20px 0;
    .form-tip { margin-left: 10px; color: #999; font-size: 12px; }
  }
  .uploading-list {
    margin-top: 10px;
    .uploading-item {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 6px 0;
      font-size: 13px;
      color: #666;
      .uploading-icon { font-size: 16px; }
    }
  }
}
</style>
