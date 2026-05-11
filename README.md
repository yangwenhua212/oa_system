# OA System - 办公系统

Modern Enterprise OA System based on Spring Boot 3.4.5 + Vue 3 + Element Plus

---

## Tech Stack

### Backend

| 技术 | 版本 |
|------|------|
| **Spring Boot** | 3.4.5 |
| **Java** | 21 |
| **ORM** | MyBatis-Plus 3.5.9 |
| **Security** | Spring Security + JWT |
| **Database** | MySQL 8.0 |
| **Build Tool** | Maven |

### Frontend

| 技术 | 版本 |
|------|------|
| **Vue** | 3.4 |
| **Build Tool** | Vite 5.1 |
| **UI Framework** | Element Plus 2.6 |
| **State Management** | Pinia 2.1 |
| **Charts** | ECharts |
| **Date** | Day.js |

---

## Project Structure

```
oa_new/
├── oa-admin/                      # Backend (Spring Boot)
│   ├── src/main/java/com/oa/
│   │   ├── system/
│   │   │   ├── common/            # Common response, utils
│   │   │   ├── config/            # Security, DataSource config
│   │   │   ├── controller/        # REST Controllers
│   │   │   ├── entity/            # Database entities
│   │   │   ├── mapper/            # MyBatis-Plus mappers
│   │   │   ├── security/          # JWT filter, utils
│   │   │   └── service/           # Business logic
│   │   └── resources/
│   │       └── mapper/            # XML mapper files
│   └── pom.xml
├── oa-web/                        # Frontend (Vue 3)
│   ├── src/
│   │   ├── api/                   # API request functions
│   │   ├── assets/                # Static assets, global styles
│   │   ├── router/                # Vue Router config
│   │   ├── store/                 # Pinia stores
│   │   ├── utils/                 # Axios request utility
│   │   └── views/                 # Page components
│   ├── index.html
│   └── package.json
├── database/
│   ├── init.sql                   # 全量初始化（新人部署只用跑这个）
│   └── create_discuss_tables.sql  # 增量补丁：讨论区回复/点赞/投票表
├── uploads/                       # File uploads directory
└── README.md
```

---

## Modules

### 系统管理
- **类型管理** — 系统字典类型维护
- **菜单管理** — 动态菜单配置
- **状态管理** — 系统状态字典

### 用户管理
- **用户管理** — 用户 CRUD、密码重置
- **角色管理** — 角色 CRUD、权限分配
- **部门管理** — 部门树结构、人员调动
- **职位管理** — 职位维护
- **在线用户** — 在线用户监控

### 考勤管理
- **考勤管理** — 签到/签退打卡（含迟到/早退判定）
- **考勤周报表** — 人员×天交叉表统计
- **考勤月报表** — 月度统计（应出勤天数可编辑）
- **考勤列表** — 详细记录查询
- **请假申请 / 加班申请** — 审批流程

### 任务管理
- **我的任务** — 个人任务列表
- **全部任务** — 全局任务管理

### 审批流程
- **新建流程** — 费用报销、出差申请、转正申请、离职申请
- **我的申请** — 申请记录
- **待我审批** — 审批处理

### 通知公告
- **公告列表** — 公告查看
- **发布公告** — 公告发布

### 邮箱管理
- **邮箱管理** — 邮件收发
- **账号管理** — 邮箱账号配置

### 日程安排
- 日历视图、事件标记

### 通讯录
- 联系人管理、分组管理

### 笔记管理
- 个人笔记 CRUD

### 工作计划
- 计划创建与管理

### 文件管理
- 文件上传/下载、文件夹管理、回收站

### 讨论区
- **讨论区列表** — 帖子浏览/发布/回复/投票/点赞/附件
- **超级管理员** — 全站帖子管理
- **我的管理** — 个人帖子管理

### 工作台（Dashboard）
- 签到打卡、快捷入口
- 系统使用量统计图表
- 任务完成排行
- 公告通知、行事历
- 流程管理、工作计划、我的帖子

---

## Getting Started

### 1. Database Setup

```bash
# 全量初始化（新人/部署只用跑这一个）
mysql -u root -p < database/init.sql

# 如果已存在数据库，只需补跑增量 SQL：
mysql -u root -p < database/create_discuss_tables.sql  # 讨论区回复/点赞/投票/通讯录分享
```

### 2. Backend Setup

```bash
cd oa-admin
mvn clean install
mvn spring-boot:run
```

Backend will start at http://localhost:8080

### 3. Frontend Setup

```bash
cd oa-web
npm install
npm run dev
```

Frontend will start at http://localhost:5173

### 4. Default Account

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `admin` | `admin123` | 管理员 |

## Data Scope（数据权限说明）

当前版本所有用户数据互通，暂未按部门/角色隔离。数据库已预留以下字段供后续扩展：

| 预留字段 | 所在表 | 用途 |
|---------|--------|------|
| `data_scope` | `sys_role` | 数据范围：1-全部 / 2-本部门 / 3-本部门及以下 / 4-仅本人 |
| `publish_scope` | `oa_notice` | 发布范围：all-全体 / dept-部门 / user-个人 |
| `target_dept_ids` | `oa_notice` | 目标部门ID列表 |

> 如需启用数据隔离，可参考以上字段实现 AOP 数据权限拦截器。

---

## Features

- ✅ Modern responsive UI with sidebar navigation
- ✅ JWT authentication with 12-hour session expiry
- ✅ RBAC (Role-Based Access Control) — 菜单权限，数据暂未隔离
- ✅ Department tree structure
- ✅ Attendance check-in/out with late/early detection
- ✅ Weekly/Monthly attendance reports
- ✅ Notice publishing with read tracking
- ✅ Email integration (SMTP/IMAP)
- ✅ Schedule/Calendar with event markers
- ✅ Workflow/Approval process (expense, trip, resignation, etc.)
- ✅ Task management with completion ranking
- ✅ File management with folders, upload/download, trash
- ✅ Discussion forum with replies, likes, voting, attachments
- ✅ Personal notes
- ✅ Address book with groups
- ✅ Work plans
- ✅ Dashboard with statistics and charts (ECharts)
- ✅ Real-time clock display on dashboard

---

## License

MIT
