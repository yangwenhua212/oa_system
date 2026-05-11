<template>
  <div class="note-page">
    <el-row :gutter="20">
      <!-- 左侧边栏 -->
      <el-col :span="5">
        <el-card class="sidebar-card">
          <template #header>
            <div class="card-header">
              <span>我的笔记</span>
              <el-button type="primary" size="small" @click="handleNewNote">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
          </template>

          <!-- 搜索 -->
          <div class="search-box">
            <el-input v-model="searchKeyword" placeholder="搜索笔记..." clearable>
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <!-- 分类列表 -->
          <div class="category-list">
            <div 
              class="category-item"
              :class="{ active: !currentCategory }"
              @click="handleCategoryChange(null)"
            >
              <el-icon><Document /></el-icon>
              <span>全部笔记</span>
              <el-badge :value="total" class="badge" />
            </div>
            <div 
              v-for="cat in categories" 
              :key="cat.id"
              class="category-item"
              :class="{ active: currentCategory?.id === cat.id }"
              @click="handleCategoryChange(cat)"
            >
              <el-icon><Folder /></el-icon>
              <span>{{ cat.name }}</span>
              <el-badge :value="cat.count" class="badge" />
              <div class="category-actions">
                <el-icon @click.stop="handleEditCategory(cat)"><Edit /></el-icon>
                <el-icon @click.stop="handleDeleteCategory(cat)"><Delete /></el-icon>
              </div>
            </div>
          </div>

          <!-- 新增分类 -->
          <div class="add-category">
            <el-input 
              v-model="newCategoryName" 
              placeholder="新建分类" 
              size="small"
              @keyup.enter="handleAddCategory"
            >
              <template #append>
                <el-button @click="handleAddCategory">
                  <el-icon><Plus /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
        </el-card>
      </el-col>

      <!-- 中间笔记列表 -->
      <el-col :span="7">
        <el-card class="note-list-card">
          <div class="note-list">
            <div 
              v-for="note in notes" 
              :key="note.id"
              class="note-item"
              :class="{ active: currentNote?.id === note.id }"
              @click="handleSelectNote(note)"
            >
              <div class="note-title">{{ note.title }}</div>
              <div class="note-preview">{{ note.content?.substring(0, 50) }}...</div>
              <div class="note-meta">
                <span class="note-date">{{ note.createTime }}</span>
                <span class="note-category" v-if="note.categoryName">{{ note.categoryName }}</span>
              </div>
            </div>
            <el-empty v-if="notes.length === 0" description="暂无笔记" />
          </div>
        </el-card>
      </el-col>

      <!-- 右侧笔记编辑器 -->
      <el-col :span="12">
        <el-card class="editor-card">
          <template #header>
            <div class="editor-header">
              <el-input 
                v-model="noteForm.title" 
                placeholder="笔记标题" 
                class="title-input"
              />
              <div class="editor-actions">
                <el-select v-model="noteForm.categoryId" placeholder="选择分类" size="small">
                  <el-option label="无分类" :value="null" />
                  <el-option 
                    v-for="cat in categories" 
                    :key="cat.id" 
                    :label="cat.name" 
                    :value="cat.id" 
                  />
                </el-select>
                <el-button type="primary" size="small" @click="handleSaveNote">
                  <el-icon><Check /></el-icon>
                  保存
                </el-button>
                <el-button type="danger" size="small" @click="handleDeleteNote" v-if="currentNote">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </template>

          <!-- 富文本编辑器 -->
          <div class="editor-container">
            <el-input
              v-model="noteForm.content"
              type="textarea"
              :rows="20"
              placeholder="在这里写下你的笔记..."
              class="content-editor"
            />
          </div>

          <div class="editor-footer">
            <span class="word-count">字数：{{ noteForm.content?.length || 0 }}</span>
            <span class="last-save" v-if="lastSaveTime">最后保存：{{ lastSaveTime }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getNotes,
  createNote,
  updateNote,
  deleteNote,
  getNoteCategories,
  createNoteCategory,
  deleteNoteCategory
} from '@/api/note'

// 状态变量
const searchKeyword = ref('')
const currentCategory = ref(null)
const currentNote = ref(null)
const notes = ref([])
const categories = ref([])
const newCategoryName = ref('')
const lastSaveTime = ref('')
const loading = ref(false)

// 笔记表单
const noteForm = reactive({
  id: null,
  title: '',
  content: '',
  categoryId: null
})

// 过滤笔记
const total = computed(() => notes.value.length)

// 加载笔记列表
async function loadNotes() {
  loading.value = true
  try {
    const params = {
      keyword: searchKeyword.value,
      categoryId: currentCategory.value?.id
    }
    const res = await getNotes(params)
    notes.value = res.data?.list || res.data || []
  } catch (error) {
    console.error('加载笔记失败:', error)
    notes.value = []
  } finally {
    loading.value = false
  }
}

// 加载分类
async function loadCategories() {
  try {
    const res = await getNoteCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
    categories.value = []
  }
}

// 选择笔记
function handleSelectNote(note) {
  currentNote.value = note
  noteForm.id = note.id
  noteForm.title = note.title
  noteForm.content = note.content
  noteForm.categoryId = categories.value.find(c => c.name === note.categoryName)?.id
}

// 选择分类
function handleCategoryChange(cat) {
  currentCategory.value = cat
  currentNote.value = null
  loadNotes()
}

// 新建笔记
function handleNewNote() {
  currentNote.value = null
  noteForm.id = null
  noteForm.title = ''
  noteForm.content = ''
  noteForm.categoryId = currentCategory.value?.id
}

// 保存笔记
async function handleSaveNote() {
  if (!noteForm.title.trim()) {
    ElMessage.warning('请输入笔记标题')
    return
  }
  
  try {
    if (noteForm.id) {
      await updateNote(noteForm.id, noteForm)
    } else {
      const res = await createNote(noteForm)
      noteForm.id = res.data?.id
    }
    ElMessage.success('保存成功')
    lastSaveTime.value = new Date().toLocaleTimeString()
    loadNotes()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

// 删除笔记
async function handleDeleteNote() {
  if (!currentNote.value) return
  
  try {
    await ElMessageBox.confirm('确定要删除这篇笔记吗？', '提示', {
      type: 'warning'
    })
    await deleteNote(currentNote.value.id)
    ElMessage.success('删除成功')
    handleNewNote()
    loadNotes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 添加分类
async function handleAddCategory() {
  if (!newCategoryName.value.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    await createNoteCategory({ name: newCategoryName.value.trim() })
    ElMessage.success('添加成功')
    newCategoryName.value = ''
    loadCategories()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

// 编辑分类
function handleEditCategory(cat) {
  ElMessageBox.prompt('请输入新的分类名称', '编辑分类', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: cat.name
  }).then(async ({ value }) => {
    ElMessage.success('更新成功')
    loadCategories()
  }).catch(() => {})
}

// 删除分类
function handleDeleteCategory(cat) {
  ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNoteCategory(cat.id)
      ElMessage.success('删除成功')
      if (currentCategory.value?.id === cat.id) {
        currentCategory.value = null
      }
      loadCategories()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 监听搜索
watch(searchKeyword, () => {
  loadNotes()
})

// 初始化
onMounted(() => {
  loadNotes()
  loadCategories()
})
</script>

<style scoped lang="scss">
.note-page {
  height: calc(100vh - 100px);

  .sidebar-card {
    height: 100%;
    
    .search-box {
      margin-bottom: 15px;
    }

    .category-list {
      .category-item {
        display: flex;
        align-items: center;
        padding: 10px 12px;
        cursor: pointer;
        border-radius: 4px;
        margin-bottom: 4px;
        position: relative;

        &:hover {
          background-color: #f5f7fa;
        }

        &.active {
          background-color: #e8f5e9;
          color: #00a65a;
        }

        .el-icon {
          margin-right: 8px;
        }

        span {
          flex: 1;
        }

        .badge {
          margin-left: 8px;
        }

        .category-actions {
          display: none;
          gap: 8px;

          .el-icon {
            cursor: pointer;
            color: #909399;
            margin: 0;

            &:hover {
              color: #409eff;
            }
          }
        }

        &:hover .category-actions {
          display: flex;
        }
      }
    }

    .add-category {
      margin-top: 15px;
    }
  }

  .note-list-card {
    height: 100%;
    overflow-y: auto;

    .note-list {
      .note-item {
        padding: 15px;
        border-bottom: 1px solid #ebeef5;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background-color: #f5f7fa;
        }

        &.active {
          background-color: #e8f5e9;
          border-left: 3px solid #00a65a;
        }

        .note-title {
          font-weight: 600;
          margin-bottom: 8px;
          color: #303133;
        }

        .note-preview {
          font-size: 12px;
          color: #909399;
          margin-bottom: 8px;
          line-height: 1.4;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .note-meta {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: #c0c4cc;

          .note-category {
            background-color: #f0f2f5;
            padding: 2px 8px;
            border-radius: 3px;
          }
        }
      }
    }
  }

  .editor-card {
    height: 100%;
    display: flex;
    flex-direction: column;

    .editor-header {
      display: flex;
      align-items: center;
      gap: 15px;

      .title-input {
        flex: 1;

        :deep(.el-input__inner) {
          font-size: 18px;
          font-weight: 600;
          border: none;
          background: transparent;

          &:focus {
            background: transparent;
          }
        }
      }

      .editor-actions {
        display: flex;
        gap: 10px;
      }
    }

    .editor-container {
      flex: 1;

      .content-editor {
        height: 100%;

        :deep(.el-textarea__inner) {
          height: 100%;
          resize: none;
          border: none;
          font-size: 14px;
          line-height: 1.8;
          padding: 15px;

          &:focus {
            background-color: #fafafa;
          }
        }
      }
    }

    .editor-footer {
      display: flex;
      justify-content: space-between;
      padding: 10px 0;
      border-top: 1px solid #ebeef5;
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
