<template>
  <div class="address-page">
    <!-- 左侧边栏 -->
    <div class="sidebar">
      <!-- 顶部标题栏（截图样式） -->
      <div class="sidebar-header" @click="handleAddContact">
        <el-icon><Plus /></el-icon>
        <span>新建联系人</span>
      </div>

      <!-- 通讯录模块 -->
      <div class="sidebar-section">
        <div class="section-header" @click="toggleSection('address')">
          <span>通讯录</span>
          <el-icon :class="{ rotate: addressCollapsed }"><ArrowUp /></el-icon>
        </div>
        <div class="section-content" v-show="!addressCollapsed">
          <el-menu :default-active="addressType" @select="handleTypeChange" class="address-menu">
            <el-menu-item index="internal">
              <el-icon><User /></el-icon>
              <span>内部通讯录</span>
            </el-menu-item>
            <el-menu-item index="external" class="external-menu-item">
              <el-icon><UserFilled /></el-icon>
              <span>外部通讯录</span>
              <el-icon class="arrow-right"><ArrowRight /></el-icon>
            </el-menu-item>
          </el-menu>
        </div>
      </div>

      <!-- 分类管理模块 -->
      <div class="sidebar-section">
        <div class="section-header" @click="toggleSection('category')">
          <span>分类</span>
          <el-icon :class="{ rotate: categoryCollapsed }"><ArrowUp /></el-icon>
        </div>
        <div class="section-content" v-show="!categoryCollapsed">
          <div class="add-category">
            <el-input v-model="newCategoryName" placeholder="新增外部分类" size="small" />
            <el-button type="primary" size="small" @click="handleAddCategory">新增</el-button>
          </div>
        </div>
      </div>

      <!-- 共享消息模块 -->
      <div class="sidebar-section">
        <div class="section-header" @click="toggleSection('share')">
          <span>共享消息</span>
          <el-icon :class="{ rotate: shareCollapsed }"><ArrowUp /></el-icon>
        </div>
        <div class="section-content" v-show="!shareCollapsed">
          <el-menu :default-active="shareType" @select="handleShareTypeChange" class="share-menu">
            <el-menu-item index="my-share">
              <el-icon><Share /></el-icon>
              <span>我共享的</span>
            </el-menu-item>
            <el-menu-item index="share-with-me">
              <el-icon><Connection /></el-icon>
              <span>共享与我</span>
              <el-badge v-if="shareCount > 0" :value="shareCount" size="small" />
            </el-menu-item>
          </el-menu>
        </div>
      </div>
    </div>

    <!-- 右侧主内容区 -->
    <div class="main-content">
      <!-- 字母快速筛选（截图样式） -->
      <div class="letter-filter">
        <span 
          v-for="letter in letters" 
          :key="letter"
          :class="{ active: currentLetter === letter }"
          @click="handleLetterClick(letter)"
        >
          {{ letter }}
        </span>
      </div>

      <!-- 列表视图 -->
      <div v-if="!isAddView" class="list-view">
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索姓名、电话、公司..."
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>

        <!-- 内部通讯录列表 -->
        <el-table v-if="addressType === 'internal'" :data="filteredContacts" stripe v-loading="loading">
          <el-table-column label="姓名" prop="realName" width="120" />
          <el-table-column label="用户名" prop="username" width="100" />
          <el-table-column label="电话" prop="phone" width="140" />
          <el-table-column label="邮箱" prop="email" min-width="180" />
          <el-table-column label="部门" prop="deptName" width="120" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleView(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 外部通讯录列表 -->
        <el-table v-else :data="filteredContacts" stripe v-loading="loading">
          <el-table-column type="selection" width="55" />
          <el-table-column label="姓名" prop="contactName" width="120" />
          <el-table-column label="性别" prop="sex" width="60">
            <template #default="{ row }">
              <el-tag size="small" :type="row.sex === '男' ? 'primary' : 'danger'">
                {{ row.sex }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="电话" prop="phone" width="140" />
          <el-table-column label="邮箱" prop="email" min-width="180" />
          <el-table-column label="公司" prop="company" min-width="150" />
          <el-table-column label="分类" prop="groupName" width="100" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleView(row)">查看</el-button>
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="warning" link @click="handleShare(row)">分享</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>

      <!-- 新建/编辑表单视图（截图样式） -->
      <div v-else class="form-view">
        <!-- 返回按钮 -->
        <div class="form-header">
          <el-button text @click="handleBackToList">
            <el-icon><ArrowLeft /></el-icon> 返回
          </el-button>
        </div>

        <!-- 表单主体（两列布局，和截图一致） -->
        <div class="form-body">
          <el-row :gutter="40">
            <!-- 左列 -->
            <el-col :span="12">
              <div class="form-item">
                <label class="form-label">分类</label>
                <el-select v-model="contactForm.groupId" placeholder="外部通讯录">
                  <el-option value="" label="外部通讯录" />
                  <el-option v-for="cat in categories" :key="cat.id" :value="cat.id" :label="cat.name" />
                </el-select>
              </div>
              <div class="form-item">
                <label class="form-label">姓名</label>
                <el-input v-model="contactForm.name" placeholder="" />
              </div>
              <div class="form-item">
                <label class="form-label">邮箱</label>
                <el-input v-model="contactForm.email" placeholder="" />
              </div>
              <div class="form-item">
                <label class="form-label">所属公司</label>
                <el-input v-model="contactForm.company" placeholder="" />
              </div>
              <div class="form-item">
                <label class="form-label">备注</label>
                <el-input v-model="contactForm.remark" type="textarea" :rows="6" placeholder="" />
              </div>
            </el-col>

            <!-- 右列 -->
            <el-col :span="12">
              <div class="form-item">
                <label class="form-label">性别</label>
                <el-select v-model="contactForm.sex" placeholder="男">
                  <el-option value="男" label="男" />
                  <el-option value="女" label="女" />
                </el-select>
              </div>
              <div class="form-item">
                <label class="form-label">电话号码</label>
                <el-input v-model="contactForm.phone" placeholder="" />
              </div>
              <div class="form-item">
                <label class="form-label">住址</label>
                <el-input v-model="contactForm.address" placeholder="" />
              </div>
              <div class="form-item">
                <label class="form-label">公司号码</label>
                <el-input v-model="contactForm.telephone" placeholder="" />
              </div>
              <div class="form-item">
                <label class="form-label">图片</label>
                <div class="upload-wrapper">
                  <el-button type="primary" link @click="triggerUpload">
                    <el-icon><Paperclip /></el-icon> 增加附件
                  </el-button>
                  <span class="upload-tip">5MB以内</span>
                  <el-upload
                    ref="uploadRef"
                    class="avatar-uploader"
                    action="#"
                    :show-file-list="false"
                    :before-upload="beforeAvatarUpload"
                    :on-success="handleUploadSuccess"
                    style="display: none;"
                  />
                  <img v-if="contactForm.image" :src="contactForm.image" class="preview-img" />
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 表单底部按钮 -->
        <div class="form-footer">
          <el-button type="primary" @click="handleSaveContact">保存</el-button>
          <el-button @click="handleBackToList">取消</el-button>
        </div>
      </div>
    </div>
  </div>

  <!-- 查看弹窗（内部通讯录 - 显示用户管理信息） -->
  <el-dialog
    v-model="viewDialogVisible"
    :title="viewDialogTitle"
    width="500px"
  >
    <el-descriptions :column="1" border v-if="currentContact">
      <template v-if="addressType === 'internal'">
        <el-descriptions-item label="姓名">{{ currentContact.realName }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentContact.username }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ currentContact.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentContact.email }}</el-descriptions-item>
        <el-descriptions-item label="部门">{{ currentContact.deptName }}</el-descriptions-item>
        <el-descriptions-item label="职位">{{ currentContact.positionName }}</el-descriptions-item>
      </template>
      <template v-else>
        <el-descriptions-item label="姓名">{{ currentContact.contactName || currentContact.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ currentContact.sex }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ currentContact.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentContact.email }}</el-descriptions-item>
        <el-descriptions-item label="公司">{{ currentContact.company }}</el-descriptions-item>
        <el-descriptions-item label="公司号码">{{ currentContact.telephone }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ currentContact.address }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentContact.remark }}</el-descriptions-item>
      </template>
    </el-descriptions>
    <template #footer>
      <el-button @click="viewDialogVisible = false">关闭</el-button>
      <el-button v-if="addressType !== 'internal'" type="primary" @click="handleEditFromView">编辑</el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="shareDialogVisible"
    title="分享联系人"
    width="500px"
  >
    <p>选择要分享的用户：</p>
    <el-select v-model="selectedUsers" multiple filterable placeholder="选择用户" style="width: 100%">
      <el-option
        v-for="user in userList"
        :key="user.id"
        :label="user.name"
        :value="user.id"
      />
    </el-select>
    <template #footer>
      <el-button @click="shareDialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleConfirmShare">确认分享</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowLeft, Plus, Delete, Edit, Share, Refresh, Connection, User, UserFilled, 
  ArrowUp, ArrowRight, Paperclip 
} from '@element-plus/icons-vue'
import {
  getInternalContacts,
  getExternalContacts,
  getContactCategories,
  createCategory,
  updateCategory,
  deleteCategory,
  saveContact,
  deleteContact as deleteContactApi,
  getMySharedContacts,
  getSharedWithMeContacts,
  shareContacts
} from '@/api/address'

// 状态变量
const loading = ref(false)
const addressType = ref('internal')
const shareType = ref('')
const currentLetter = ref('ALL')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const categories = ref([])
const contacts = ref([])
const newCategoryName = ref('')
const shareCount = ref(0)

// 视图控制
const isAddView = ref(false)
const addressCollapsed = ref(false)
const categoryCollapsed = ref(false)
const shareCollapsed = ref(false)
const uploadRef = ref(null)

// 对话框状态
const viewDialogVisible = ref(false)
const shareDialogVisible = ref(false)
const isEdit = ref(false)
const currentContact = ref(null)
const selectedUsers = ref([])
const userList = ref([])

// 查看弹窗标题
const viewDialogTitle = computed(() => {
  if (addressType.value === 'internal') return '员工详情'
  return '联系人详情'
})

// 联系人表单
const contactForm = reactive({
  id: null,
  groupId: '',
  name: '',
  sex: '男',
  phone: '',
  email: '',
  address: '',
  company: '',
  telephone: '',
  remark: '',
  image: ''
})

// 头像上传
const beforeAvatarUpload = (file) => {
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    ElMessage.warning('图片大小不能超过 5MB!')
    return false
  }
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    contactForm.image = reader.result
  }
  return false
}
const handleUploadSuccess = () => {}
const triggerUpload = () => uploadRef.value?.click()

// 字母数组
const letters = ['ALL', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z']

// 过滤后的联系人
const filteredContacts = computed(() => {
  let result = contacts.value
  if (currentLetter.value !== 'ALL') {
    const letter = currentLetter.value.toUpperCase()
    if (addressType.value === 'internal') {
      result = result.filter(c => c.realName?.charAt(0)?.toUpperCase() === letter || c.username?.charAt(0)?.toUpperCase() === letter)
    } else {
      result = result.filter(c => c.pinyin?.toUpperCase().startsWith(letter) || c.contactName?.charAt(0)?.toUpperCase() === letter)
    }
  }
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    if (addressType.value === 'internal') {
      result = result.filter(c =>
        c.realName?.toLowerCase().includes(keyword) ||
        c.username?.toLowerCase().includes(keyword) ||
        c.phone?.includes(keyword)
      )
    } else {
      result = result.filter(c =>
        c.contactName?.toLowerCase().includes(keyword) ||
        c.phone?.includes(keyword) ||
        c.company?.toLowerCase().includes(keyword)
      )
    }
  }
  return result
})

// 加载联系人列表
async function loadContacts() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      letter: currentLetter.value === 'ALL' ? '' : currentLetter.value
    }
    
    let res
    if (addressType.value === 'internal') {
      res = await getInternalContacts(params)
    } else {
      res = await getExternalContacts(params)
    }
    contacts.value = res.data?.list || res.data || []
    total.value = res.data?.total || contacts.value.length
  } catch (error) {
    console.error('加载联系人失败:', error)
    contacts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 加载分类
async function loadCategories() {
  try {
    const res = await getContactCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
    categories.value = [
      { id: 1, name: '客户' },
      { id: 2, name: '供应商' },
      { id: 3, name: '合作伙伴' }
    ]
  }
}

// 模块折叠切换
function toggleSection(section) {
  if (section === 'address') addressCollapsed.value = !addressCollapsed.value
  if (section === 'category') categoryCollapsed.value = !categoryCollapsed.value
  if (section === 'share') shareCollapsed.value = !shareCollapsed.value
}

// 事件处理函数
function handleTypeChange(index) {
  addressType.value = index
  shareType.value = ''
  currentLetter.value = 'ALL'
  currentPage.value = 1
  loadContacts()
}

function handleCategoryChange(cat) {
  currentPage.value = 1
  loadContacts()
}

function handleShareTypeChange(index) {
  shareType.value = index
  loadSharedContacts()
}

async function loadSharedContacts() {
  loading.value = true
  try {
    let res
    if (shareType.value === 'my-share') {
      res = await getMySharedContacts()
    } else {
      res = await getSharedWithMeContacts()
    }
    contacts.value = res.data || []
    total.value = contacts.value.length
  } catch (error) {
    contacts.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleLetterClick(letter) {
  currentLetter.value = letter
  currentPage.value = 1
  loadContacts()
}

function handleSearch() {
  currentPage.value = 1
  loadContacts()
}

function handleRefresh() {
  loadContacts()
  ElMessage.success('刷新成功')
}

function handleAddContact() {
  isEdit.value = false
  Object.assign(contactForm, {
    id: null,
    groupId: '',
    name: '',
    sex: '男',
    phone: '',
    email: '',
    address: '',
    company: '',
    telephone: '',
    remark: '',
    image: ''
  })
  isAddView.value = true
}

function handleBackToList() {
  isAddView.value = false
}

function handleView(row) {
  currentContact.value = row
  viewDialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(contactForm, {
    id: row.id,
    groupId: row.groupId || row.group_id || '',
    name: row.contactName || row.name || '',
    sex: row.sex || '男',
    phone: row.phone || '',
    email: row.email || '',
    address: row.address || '',
    company: row.company || '',
    telephone: row.telephone || '',
    remark: row.remark || '',
    image: row.image || ''
  })
  isAddView.value = true
}

function handleEditFromView() {
  viewDialogVisible.value = false
  handleEdit(currentContact.value)
}

async function handleSaveContact() {
  try {
    const payload = {
      id: contactForm.id,
      contactName: contactForm.name,
      sex: contactForm.sex,
      phone: contactForm.phone,
      email: contactForm.email,
      address: contactForm.address,
      company: contactForm.company,
      telephone: contactForm.telephone,
      remark: contactForm.remark,
      image: contactForm.image,
      groupId: contactForm.groupId || null,
      contactType: 'company'
    }
    await saveContact(payload)
    ElMessage.success('保存成功')
    isAddView.value = false
    loadContacts()
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

function handleShare(row) {
  currentContact.value = row
  selectedUsers.value = []
  shareDialogVisible.value = true
}

async function handleConfirmShare() {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择要分享的用户')
    return
  }
  try {
    await shareContacts({
      contactId: currentContact.value.id,
      userIds: selectedUsers.value
    })
    ElMessage.success('分享成功')
    shareDialogVisible.value = false
  } catch (error) {
    ElMessage.error('分享失败')
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该联系人吗？', '提示', {
      type: 'warning'
    })
    await deleteContactApi(row.id)
    ElMessage.success('删除成功')
    loadContacts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

async function handleAddCategory() {
  if (!newCategoryName.value.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    await createCategory({ name: newCategoryName.value.trim() })
    ElMessage.success('添加成功')
    newCategoryName.value = ''
    loadCategories()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

function handleEditCategory(cat) {
  ElMessageBox.prompt('请输入新的分类名称', '编辑分类', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: cat.name
  }).then(async ({ value }) => {
    try {
      await updateCategory(cat.id, { name: value })
      ElMessage.success('更新成功')
      loadCategories()
    } catch (error) {
      ElMessage.error('更新失败')
    }
  }).catch(() => {})
}

function handleDeleteCategory(cat) {
  ElMessageBox.confirm('确定要删除该分类吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCategory(cat.id)
      ElMessage.success('删除成功')
      loadCategories()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

function handlePageChange(page) {
  currentPage.value = page
  loadContacts()
}

function handleSizeChange(size) {
  pageSize.value = size
  loadContacts()
}

// 初始化
onMounted(() => {
  loadContacts()
  loadCategories()
})
</script>

<style scoped lang="scss">
.address-page {
  display: flex;
  height: 100vh;
  background-color: #f5f7fa;

  // 左侧边栏
  .sidebar {
    width: 280px;
    background-color: #fff;
    border-right: 1px solid #e4e7ed;
    display: flex;
    flex-direction: column;

    .sidebar-header {
      background-color: #00a65a;
      color: #fff;
      padding: 14px 16px;
      font-size: 16px;
      font-weight: 500;
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      transition: background 0.2s;
      &:hover { background-color: #008d4c; }
    }

    .sidebar-section {
      border-bottom: 1px solid #f0f0f0;

      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 16px;
        font-size: 14px;
        color: #303133;
        cursor: pointer;

        .el-icon {
          transition: transform 0.3s;
          &.rotate { transform: rotate(180deg); }
        }
      }

      .section-content {
        padding: 8px 0;

        .address-menu,
        .share-menu {
          border-right: none;

          .el-menu-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            &.is-active { background-color: #f5f7fa; color: #409eff; }
            .arrow-right { margin-left: auto; }
          }
        }

        .add-category {
          display: flex;
          gap: 8px;
          padding: 0 16px;
          .el-input { flex: 1; }
        }
      }
    }
  }

  // 右侧主内容
  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    background-color: #fff;

    // 字母筛选
    .letter-filter {
      display: flex;
      border-bottom: 1px solid #e4e7ed;
      padding: 8px 16px;
      gap: 0;

      span {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 40px;
        height: 32px;
        border: 1px solid #dcdfe6;
        border-right: none;
        background-color: #fff;
        cursor: pointer;
        font-size: 14px;
        transition: all 0.3s;

        &:first-child {
          border-top-left-radius: 4px;
          border-bottom-left-radius: 4px;
          background-color: #409eff;
          color: #fff;
          border-color: #409eff;
        }

        &:last-child {
          border-right: 1px solid #dcdfe6;
          border-top-right-radius: 4px;
          border-bottom-right-radius: 4px;
        }

        &:hover:not(.active) {
          background-color: #ecf5ff;
          color: #409eff;
        }

        &.active {
          background-color: #409eff;
          color: #fff;
          border-color: #409eff;
        }
      }
    }

    // 列表视图
    .list-view {
      padding: 16px;
      flex: 1;
      overflow-y: auto;

      .search-bar { margin-bottom: 16px; }
      .pagination { display: flex; justify-content: flex-end; margin-top: 16px; }
    }

    // 表单视图（截图样式）
    .form-view {
      flex: 1;
      display: flex;
      flex-direction: column;

      .form-header {
        padding: 16px;
        border-bottom: 1px solid #f0f0f0;
        .el-button { color: #606266; font-size: 14px; }
      }

      .form-body {
        flex: 1;
        padding: 24px;
        overflow-y: auto;

        .form-item {
          margin-bottom: 24px;
          display: flex;
          flex-direction: column;
          gap: 8px;

          .form-label {
            font-size: 14px;
            color: #606266;
            font-weight: 500;
          }

          .el-input,
          .el-select,
          .el-textarea { width: 100%; }

          .upload-wrapper {
            display: flex;
            align-items: center;
            gap: 8px;
            .upload-tip { font-size: 12px; color: #909399; }
            .preview-img { width: 80px; height: 80px; object-fit: cover; border-radius: 4px; }
          }
        }
      }

      .form-footer {
        padding: 16px 24px;
        border-top: 1px solid #f0f0f0;
        display: flex;
        gap: 12px;
        .el-button { padding: 8px 20px; }
      }
    }
  }
}
</style>
