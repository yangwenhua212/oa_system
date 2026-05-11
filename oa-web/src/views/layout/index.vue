<template>
  <el-container class="layout-container">
    <!-- Left Sidebar -->
    <el-aside :width="isCollapsed ? '64px' : '200px'" class="sidebar">
      <!-- Logo -->
      <div class="logo green">
        <span v-if="!isCollapsed" class="logo-text">办公系统</span>
        <span v-else class="logo-icon">OA</span>
      </div>

      <!-- User Info -->
      <div class="user-panel" v-if="!isCollapsed">
        <div class="user-avatar">
          <el-avatar :size="45" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
        </div>
        <div class="user-info">
          <p class="user-name">{{ userStore.userInfo.realName || 'Admin' }}</p>
          <p class="user-status">
            <span class="status-dot"></span> 在线
          </p>
        </div>
      </div>

      <!-- Navigation Menu -->
      <el-menu
        :default-active="activeMenu"
        :default-openeds="openedMenus"
        :collapse="isCollapsed"
        :collapse-transition="false"
        class="sidebar-menu"
        background-color="#222d32"
        text-color="#b8c7ce"
        active-text-color="#ffffff"
        @select="handleMenuSelect"
      >
        <!-- 动态菜单：根据用户角色权限从后端加载 -->
        <template v-for="menu in menuList" :key="menu.id">
          <!-- 有子菜单的目录 -->
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
            <template #title>
              <el-icon><component :is="iconMap[menu.icon]" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </template>
            <el-menu-item v-for="child in menu.children" :key="child.id" :index="child.path">
              {{ child.menuName }}
            </el-menu-item>
          </el-sub-menu>
          <!-- 没有子菜单的单项 -->
          <el-menu-item v-else :index="menu.path">
            <el-icon><component :is="iconMap[menu.icon]" /></el-icon>
            <template #title>{{ menu.menuName }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- Top Header -->
      <el-header class="header green">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapsed = !isCollapsed">
            <Fold v-if="!isCollapsed" />
            <Expand v-else />
          </el-icon>
        </div>

        <div class="header-right">
          <!-- Notice -->
          <el-popover placement="bottom" :width="320" trigger="click" class="notice-popover">
            <template #reference>
              <el-badge :value="noticeCount" :hidden="noticeCount === 0" class="header-badge">
                <el-icon class="header-icon header-btn"><Bell /></el-icon>
              </el-badge>
            </template>
            <div class="popover-header">
              <span>通知公告</span>
              <el-button link type="primary" size="small" @click="goTo('/notice/list')">查看全部</el-button>
            </div>
            <div class="popover-body">
              <div v-for="n in noticeList" :key="n.id" class="popover-item" @click="handleNoticeClick(n.id)">
                <div class="item-title">
                  <el-tag v-if="n.noticeType === 'important'" size="small" type="danger">重要</el-tag>
                  <span class="title-text">{{ n.title }}</span>
                </div>
                <div class="item-time">{{ formatTime(n.publishTime) }}</div>
              </div>
              <el-empty v-if="noticeList.length === 0" description="暂无通知" :image-size="40" />
            </div>
          </el-popover>

          <!-- Mail -->
          <el-badge :value="mailCount" :hidden="mailCount === 0" class="header-badge">
            <el-icon class="header-icon header-btn" @click="goTo('/mail/list')"><Message /></el-icon>
          </el-badge>

          <!-- Task -->
          <el-badge :value="taskCount" :hidden="taskCount === 0" class="header-badge" type="danger">
            <el-icon class="header-icon header-btn" @click="goTo('/task/my')"><Flag /></el-icon>
          </el-badge>

          <!-- User Dropdown -->
          <el-dropdown @command="handleCommand" trigger="click">
            <span class="user-dropdown">
              <el-avatar :size="30" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="username">{{ userStore.userInfo.realName || 'Admin' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown-menu">
                <div class="user-header green">
                  <el-avatar :size="80" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                  <p class="white">
                    <span>{{ userStore.userInfo.deptName || '未分配部门' }}</span> / <span>{{ userStore.userInfo.realName || '未知用户' }}</span><br>
                    <small>{{ userStore.userInfo.isAdmin === 1 ? '系统管理员' : '普通用户' }}</small>
                  </p>
                </div>
                <el-dropdown-item command="profile" divided>个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item command="logout" divided style="color: #dd4b39;">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Main Content Area -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getUserMenus } from '@/api/auth'
import { getNoticeList, getUnreadCount, markNoticeRead } from '@/api/notice'
import { getUnreadCount as getMailUnread } from '@/api/mail'
import { getMyTasks } from '@/api/task'
import dayjs from 'dayjs'

// 图标映射（菜单图标名 → Element Plus图标组件）
import {
  Odometer, UserFilled, Setting, Clock, Collection, Document,
  Bell, Message, Calendar, Edit, TrendCharts, Folder, ChatDotRound,
  User, Lock, Search, Plus, Refresh, Share, Connection
} from '@element-plus/icons-vue'
const iconMap = {
  Odometer, UserFilled, Setting, Clock, Collection, Document,
  Bell, Message, Calendar, Edit, TrendCharts, Folder, ChatDotRound,
  User, Lock, Search, Plus, Refresh, Share, Connection
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)

// 动态菜单数据
const menuList = ref([])
const menuLoading = ref(true)

// 右上角角标数据
const noticeCount = ref(0)
const mailCount = ref(0)
const taskCount = ref(0)
const noticeList = ref([])

// 加载动态菜单
async function loadMenus() {
  menuLoading.value = true
  try {
    const res = await getUserMenus()
    if (res.code === 200) {
      menuList.value = res.data || []
    }
  } catch (e) {
    console.error('加载菜单失败', e)
    menuList.value = []
  } finally {
    menuLoading.value = false
  }
}

// 加载角标数据
async function loadBadgeData() {
  try {
    const [unreadRes, noticeRes, mailRes, taskRes] = await Promise.all([
      getUnreadCount().catch(() => ({ data: 0 })),
      getNoticeList({ pageNum: 1, pageSize: 5 }).catch(() => ({ data: { records: [] } })),
      getMailUnread().catch(() => ({ data: 0 })),
      getMyTasks().catch(() => ([]))
    ])
    noticeCount.value = unreadRes.data || 0
    noticeList.value = noticeRes.data?.records || []
    mailCount.value = mailRes.data || 0
    // 任务：统计未完成的数量
    const tasks = Array.isArray(taskRes) ? taskRes : (taskRes.data || [])
    taskCount.value = tasks.filter(t => t.status !== 'completed').length || 0
  } catch (e) { /* ignore */ }
}

// 当前激活的菜单项
const activeMenu = computed(() => route.path)

// 根据当前路由路径计算需要展开的子菜单
const openedMenus = ref(getOpenedMenus())

function getOpenedMenus() {
  const path = route.path
  const menus = []
  const match = path.match(/^\/([^/]+)/)
  if (match) {
    menus.push('/' + match[1])
  }
  return menus
}

watch(() => route.path, () => {
  openedMenus.value = getOpenedMenus()
})

function handleMenuSelect(index) {
  if (index !== route.path) {
    router.push(index)
  }
}

function goTo(path) {
  router.push(path)
}

function formatTime(t) {
  return t ? dayjs(t).format('MM-DD HH:mm') : ''
}

function handleNoticeClick(id) {
  markNoticeRead(id).catch(() => {})
  noticeCount.value = Math.max(0, noticeCount.value - 1)
  goTo(`/notice/detail?id=${id}`)
}

const handleCommand = (command) => {
  switch (command) {
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(() => {
        userStore.logout()
        router.push('/login')
      }).catch(() => {})
      break
    case 'profile':
      router.push(`/user/edit/${userStore.userInfo.id}`)
      break
    case 'settings':
      router.push('/system/menu')
      break
  }
}

onMounted(() => {
  loadMenus()
  loadBadgeData()
})
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

// Sidebar
.sidebar {
  background-color: #222d32;
  transition: width 0.3s;
  overflow-x: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-bottom: 1px solid rgba(255,255,255,0.1);

    .logo-text {
      color: white;
      font-size: 18px;
      font-weight: bold;
      letter-spacing: 2px;
    }

    .logo-icon {
      color: white;
      font-size: 20px;
      font-weight: bold;
    }
  }

  .user-panel {
    height: 80px;
    padding: 15px 10px;
    display: flex;
    align-items: center;
    border-bottom: 1px solid rgba(255,255,255,0.1);

    .user-avatar {
      margin-right: 10px;
    }

    .user-info {
      .user-name {
        color: #fff;
        font-size: 14px;
        line-height: 1.5;
      }

      .user-status {
        font-size: 12px;
        color: #b8c7ce;

        .status-dot {
          display: inline-block;
          width: 8px;
          height: 8px;
          background: #00a65a;
          border-radius: 50%;
          margin-right: 4px;
        }
      }
    }
  }

  .sidebar-menu {
    background-color: #222d32 !important;
    border-right: none;

    :deep(.el-sub-menu__title) {
      color: #b8c7ce !important;

      &:hover {
        background-color: #18252b !important;
        color: #fff !important;
      }
    }

    :deep(.el-menu-item) {
      color: #b8c7ce !important;
      border-left: 3px solid transparent;

      &:hover {
        background-color: #18252b !important;
        color: #fff !important;
      }
    }

    :deep(.el-menu-item.is-active) {
      background-color: #18252b !important;
      color: #fff !important;
      border-left-color: #00a65a !important;
    }

    :deep(.el-sub-menu .el-menu-item) {
      padding-left: 50px !important;
    }
  }
}

// Header
.header {
  background-color: #00a65a;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);

  .collapse-btn {
    font-size: 20px;
    color: white;
    cursor: pointer;

    &:hover {
      opacity: 0.8;
    }
  }

  .header-right {
    display: flex;
    align-items: center;

    .header-badge {
      margin: 0 15px;
      cursor: pointer;

      .header-icon {
        font-size: 20px;
        color: white;
      }
    }

    .header-btn {
      cursor: pointer;
      transition: opacity 0.2s;
      &:hover { opacity: 0.7; }
    }

    .user-dropdown {
      display: flex;
      align-items: center;
      cursor: pointer;
      padding: 8px 10px;
      border-radius: 4px;

      &:hover {
        background-color: #008d4c;
      }

      .username {
        color: white;
        margin: 0 8px;
        font-size: 14px;
      }
    }
  }
}

// User Dropdown Menu
.user-dropdown-menu {
  padding: 0 !important;

  .user-header {
    height: 140px;
    width: 280px;
    padding: 15px;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .white {
      color: white;
      margin-top: 10px;
      font-size: 14px;
      line-height: 1.6;

      small {
        color: rgba(255,255,255,0.7);
      }
    }
  }
}

// Popover 通知列表
.popover-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  font-weight: 600;
  font-size: 14px;
}

.popover-body {
  max-height: 280px;
  overflow-y: auto;

  .popover-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 10px 4px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background 0.2s;

    &:hover { background: #f5f7fa; }
    &:last-child { border-bottom: none; }

    .item-title {
      display: flex;
      align-items: center;
      gap: 6px;

      .title-text {
        font-size: 13px;
        color: #333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }

    .item-time {
      font-size: 11px;
      color: #999;
      padding-left: 2px;
    }
  }
}

// Main Content
.main-content {
  background-color: #ecf0f5;
  padding: 15px;
  overflow-y: auto;
}

// Theme Colors
.green {
  background: #00a65a !important;
}

.green-g {
  background: #008d4c !important;
}

.white {
  color: white;
}
</style>
