import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElNotification } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '工作台', icon: 'Odometer' }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('@/views/layout/index.vue'),
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'type',
        name: 'TypeManage',
        component: () => import('@/views/system/systemType.vue'),
        meta: { title: '类型管理' }
      },
      {
        path: 'menu',
        name: 'MenuManage',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理' }
      },
      {
        path: 'status',
        name: 'StatusManage',
        component: () => import('@/views/system/systemStatus.vue'),
        meta: { title: '状态管理' }
      }
    ]
  },
  {
    path: '/user',
    component: () => import('@/views/layout/index.vue'),
    meta: { title: '用户管理', icon: 'UserFilled' },
    children: [
      {
        path: 'list',
        name: 'UserManage',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'edit/:id',
        name: 'UserEdit',
        component: () => import('@/views/system/user/edit.vue'),
        meta: { title: '编辑用户' }
      },
      {
        path: 'role',
        name: 'RoleManage',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理' }
      },
      {
        path: 'dept',
        name: 'DeptManage',
        component: () => import('@/views/system/dept/index.vue'),
        meta: { title: '部门管理' }
      },
      {
        path: 'position',
        name: 'PositionManage',
        component: () => import('@/views/system/position.vue'),
        meta: { title: '职位管理' }
      },
      {
        path: 'online',
        name: 'OnlineUser',
        component: () => import('@/views/system/online.vue'),
        meta: { title: '在线用户' }
      }
    ]
  },
  {
    path: '/attendance',
    component: () => import('@/views/layout/index.vue'),
    meta: { title: '考勤管理', icon: 'Clock' },
    children: [
      {
        path: 'checkin',
        name: 'CheckIn',
        component: () => import('@/views/attendance/checkin.vue'),
        meta: { title: '考勤管理' }
      },
      {
        path: 'weekly',
        name: 'AttendanceWeekly',
        component: () => import('@/views/attendance/weekly.vue'),
        meta: { title: '考勤周报表' }
      },
      {
        path: 'monthly',
        name: 'AttendanceMonthly',
        component: () => import('@/views/attendance/monthly.vue'),
        meta: { title: '考勤月报表' }
      },
      {
        path: 'list',
        name: 'AttendanceList',
        component: () => import('@/views/attendance/list.vue'),
        meta: { title: '考勤列表' }
      },
      {
        path: 'leave',
        name: 'Leave',
        component: () => import('@/views/attendance/leave.vue'),
        meta: { title: '请假申请', hidden: true }
      },
      {
        path: 'overtime',
        name: 'Overtime',
        component: () => import('@/views/attendance/overtime.vue'),
        meta: { title: '加班申请', hidden: true }
      },
      {
        path: 'leave-form',
        name: 'LeaveForm',
        component: () => import('@/views/attendance/leave-form.vue'),
        meta: { title: '新建请假', hidden: true }
      },
      {
        path: 'overtime-form',
        name: 'OvertimeForm',
        component: () => import('@/views/attendance/overtime-form.vue'),
        meta: { title: '新建加班', hidden: true }
      }
    ]
  },
  {
    path: '/notice',
    component: () => import('@/views/layout/index.vue'),
    meta: { title: '通知公告', icon: 'Bell' },
    children: [
      {
        path: 'list',
        name: 'NoticeList',
        component: () => import('@/views/notice/list.vue'),
        meta: { title: '公告列表' }
      },
      {
        path: 'publish',
        name: 'NoticePublish',
        component: () => import('@/views/notice/publish.vue'),
        meta: { title: '发布公告' }
      },
      {
        path: 'detail',
        name: 'NoticeDetail',
        component: () => import('@/views/notice/detail.vue'),
        meta: { title: '公告详情' }
      }
    ]
  },
  {
    path: '/mail',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/mail/list',
    meta: { title: '邮箱管理', icon: 'Message' },
    children: [
      {
        path: 'list',
        name: 'MailManage',
        component: () => import('@/views/mail/index.vue'),
        meta: { title: '邮箱管理' }
      },
      {
        path: 'account',
        name: 'MailAccount',
        component: () => import('@/views/mail/account.vue'),
        meta: { title: '账号管理' }
      }
    ]
  },
  {
    path: '/schedule',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/schedule/list',
    meta: { title: '日程安排', icon: 'Calendar' },
    children: [
      {
        path: 'list',
        name: 'Schedule',
        component: () => import('@/views/schedule/index.vue'),
        meta: { title: '日程安排' }
      }
    ]
  },
  {
    path: '/task',
    component: () => import('@/views/layout/index.vue'),
    meta: { title: '任务管理', icon: 'Collection' },
    children: [
      {
        path: 'my',
        name: 'MyTask',
        component: () => import('@/views/task/my.vue'),
        meta: { title: '我的任务' }
      },
      {
        path: 'all',
        name: 'AllTask',
        component: () => import('@/views/task/all.vue'),
        meta: { title: '全部任务' }
      },
      {
        path: 'edit',
        name: 'TaskEdit',
        component: () => import('@/views/task/edit.vue'),
        meta: { title: '新建任务' }
      }
    ]
  },
  {
    path: '/process',
    component: () => import('@/views/layout/index.vue'),
    meta: { title: '审批流程', icon: 'Document' },
    children: [
      {
        path: 'new',
        name: 'NewProcess',
        component: () => import('@/views/process/new.vue'),
        meta: { title: '新建流程' }
      },
      {
        path: 'expense',
        name: 'ExpenseReimbursement',
        component: () => import('@/views/process/expense.vue'),
        meta: { title: '费用报销单' }
      },
      {
        path: 'trip',
        name: 'TripApplication',
        component: () => import('@/views/process/trip.vue'),
        meta: { title: '出差申请单' }
      },
      {
        path: 'regular',
        name: 'RegularApplication',
        component: () => import('@/views/process/regular.vue'),
        meta: { title: '转正申请单' }
      },
      {
        path: 'resignation',
        name: 'ResignationApplication',
        component: () => import('@/views/process/resignation.vue'),
        meta: { title: '离职申请单' }
      },
      {
        path: 'my',
        name: 'MyProcess',
        component: () => import('@/views/process/my.vue'),
        meta: { title: '我的申请' }
      },
      {
        path: 'pending',
        name: 'PendingProcess',
        component: () => import('@/views/process/pending.vue'),
        meta: { title: '待我审批' }
      },
      {
        path: 'detail',
        name: 'ProcessDetail',
        component: () => import('@/views/process/detail.vue'),
        meta: { title: '流程详情' }
      }
    ]
  },
  {
    path: '/address',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/address/list',
    meta: { title: '通讯录', icon: 'User' },
    children: [
      {
        path: 'list',
        name: 'Address',
        component: () => import('@/views/address/index.vue'),
        meta: { title: '通讯录管理' }
      }
    ]
  },
  {
    path: '/note',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/note/list',
    meta: { title: '笔记管理', icon: 'Edit' },
    children: [
      {
        path: 'list',
        name: 'Note',
        component: () => import('@/views/note/index.vue'),
        meta: { title: '笔记管理' }
      }
    ]
  },
  {
    path: '/plan',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/plan/list',
    meta: { title: '工作计划', icon: 'TrendCharts' },
    children: [
      {
        path: 'list',
        name: 'Plan',
        component: () => import('@/views/plan/index.vue'),
        meta: { title: '计划管理' }
      }
    ]
  },
  {
    path: '/file',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/file/list',
    meta: { title: '文件管理', icon: 'Folder' },
    children: [
      {
        path: 'list',
        name: 'File',
        component: () => import('@/views/file/index.vue'),
        meta: { title: '文件管理' }
      }
    ]
  },
  {
    path: '/chat',
    component: () => import('@/views/layout/index.vue'),
    redirect: '/chat/list',
    meta: { title: '讨论区', icon: 'ChatDotRound' },
    children: [
      {
        path: 'list',
        name: 'ChatList',
        component: () => import('@/views/chat/index.vue'),
        meta: { title: '讨论区列表' }
      },
      {
        path: 'admin',
        name: 'ChatAdmin',
        component: () => import('@/views/chat/admin.vue'),
        meta: { title: '超级管理员' }
      },
      {
        path: 'manage',
        name: 'ChatManage',
        component: () => import('@/views/chat/manage.vue'),
        meta: { title: '我的管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation Guard
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.path === '/login') {
    if (userStore.token) return next('/')
    return next()
  }

  if (!userStore.token) return next('/login')

  // 检查会话是否超过12小时
  if (userStore.isSessionExpired) {
    userStore.logout()
    ElNotification({
      title: '会话已过期',
      message: '距离上次登录已超过12小时，请重新登录',
      type: 'warning',
      duration: 4000
    })
    return next('/login')
  }

  next()
})

export default router
