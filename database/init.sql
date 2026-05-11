-- =========================================
-- OA系统数据库初始化脚本
-- 数据库版本: MySQL 8.0
-- 创建时间: 2026-04-29
-- =========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `oa_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `oa_system`;

-- 禁用外键检查（导入时）
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================
-- 1. 用户与权限模块
-- =========================================

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT '/assets/avatar/default.jpg' COMMENT '头像',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `sex` TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    `dept_id` BIGINT COMMENT '部门ID',
    `position_id` BIGINT COMMENT '职位ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `is_admin` TINYINT DEFAULT 0 COMMENT '是否管理员: 0-否, 1-是',
    `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `login_count` INT DEFAULT 0 COMMENT '登录次数',
    `remark` VARCHAR(500) COMMENT '备注',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `school` VARCHAR(100) DEFAULT NULL COMMENT '毕业院校',
    `bank_account` VARCHAR(50) DEFAULT NULL COMMENT '银行账号',
    `education` VARCHAR(50) DEFAULT NULL COMMENT '学历',
    `id_card` VARCHAR(20) DEFAULT NULL COMMENT '身份证号',
    `salary` DECIMAL(10,2) DEFAULT NULL COMMENT '工资',
    `skin` VARCHAR(20) DEFAULT 'blue' COMMENT '皮肤',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志: 0-正常, 1-已删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 部门表
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父部门ID',
    `dept_name` VARCHAR(50) NOT NULL COMMENT '部门名称',
    `dept_code` VARCHAR(50) COMMENT '部门编码',
    `dept_type` VARCHAR(20) COMMENT '部门类型',
    `leader` BIGINT COMMENT '负责人',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `email` VARCHAR(100) COMMENT '邮箱',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `remark` VARCHAR(500) COMMENT '备注',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 职位表
DROP TABLE IF EXISTS `sys_position`;
CREATE TABLE `sys_position` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '职位ID',
    `position_name` VARCHAR(50) NOT NULL COMMENT '职位名称',
    `position_code` VARCHAR(50) COMMENT '职位编码',
    `dept_id` BIGINT COMMENT '部门ID',
    `salary_level` VARCHAR(50) COMMENT '薪资级别',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `remark` VARCHAR(500) COMMENT '备注',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职位表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `role_type` VARCHAR(20) COMMENT '角色类型',
    `data_scope` TINYINT DEFAULT 1 COMMENT '数据范围: 1-全部, 2-本部门, 3-本部门及以下, 4-仅本人',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `remark` VARCHAR(500) COMMENT '备注',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 菜单表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父菜单ID',
    `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
    `menu_type` VARCHAR(20) NOT NULL COMMENT '菜单类型: M-目录, C-菜单, F-按钮',
    `icon` VARCHAR(100) COMMENT '菜单图标',
    `path` VARCHAR(200) COMMENT '路由路径',
    `component` VARCHAR(255) COMMENT '组件路径',
    `query` VARCHAR(500) COMMENT '路由参数',
    `is_frame` TINYINT DEFAULT 1 COMMENT '是否外链: 0-是, 1-否',
    `is_cache` TINYINT DEFAULT 0 COMMENT '是否缓存: 0-缓存, 1-不缓存',
    `visible` TINYINT DEFAULT 1 COMMENT '显示状态: 0-隐藏, 1-显示',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `perms` VARCHAR(100) COMMENT '权限标识',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 角色菜单关联表
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- 操作日志表
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT COMMENT '操作用户ID',
    `username` VARCHAR(50) COMMENT '用户名',
    `operation` VARCHAR(100) COMMENT '操作类型',
    `method` VARCHAR(200) COMMENT '请求方法',
    `url` VARCHAR(500) COMMENT '请求URL',
    `ip` VARCHAR(50) COMMENT 'IP地址',
    `location` VARCHAR(100) COMMENT '操作地点',
    `params` TEXT COMMENT '请求参数',
    `result` TEXT COMMENT '返回结果',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-异常, 1-正常',
    `error_msg` TEXT COMMENT '错误信息',
    `duration` INT COMMENT '执行时长(毫秒)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 字典类型表
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
    `dict_code` VARCHAR(100) NOT NULL COMMENT '字典编码',
    `dict_type` VARCHAR(100) DEFAULT 'string' COMMENT '字典类型',
    `status` TINYINT DEFAULT 1,
    `remark` VARCHAR(500),
    `del_flag` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_code` (`dict_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- 字典数据表
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `dict_type_id` BIGINT NOT NULL COMMENT '字典类型ID',
    `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
    `dict_type` VARCHAR(100) COMMENT '数据类型',
    `sort` INT DEFAULT 0,
    `status` TINYINT DEFAULT 1,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_dict_type_id` (`dict_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- =========================================
-- 2. 考勤模块
-- =========================================

-- 考勤记录表
DROP TABLE IF EXISTS `oa_attendance`;
CREATE TABLE `oa_attendance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `username` VARCHAR(50) COMMENT '用户名',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `dept_id` BIGINT COMMENT '部门ID',
    `dept_name` VARCHAR(50) COMMENT '部门名称',
    `attendance_date` DATE NOT NULL COMMENT '考勤日期',
    `clock_in_time` DATETIME COMMENT '上班打卡时间',
    `clock_out_time` DATETIME COMMENT '下班打卡时间',
    `clock_in_status` VARCHAR(20) DEFAULT 'normal' COMMENT '上班状态: normal-正常, late-迟到, absent-缺勤',
    `clock_out_status` VARCHAR(20) DEFAULT 'normal' COMMENT '下班状态: normal-正常, leave-早退, absent-缺勤',
    `work_duration` DECIMAL(5,2) COMMENT '工作时长(小时)',
    `overtime_duration` DECIMAL(5,2) DEFAULT 0 COMMENT '加班时长(小时)',
    `remark` VARCHAR(500) COMMENT '备注',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_date` (`user_id`, `attendance_date`),
    KEY `idx_attendance_date` (`attendance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤记录表';

-- 请假表
DROP TABLE IF EXISTS `oa_leave`;
CREATE TABLE `oa_leave` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '申请人ID',
    `username` VARCHAR(50) COMMENT '申请人用户名',
    `real_name` VARCHAR(50) COMMENT '申请人姓名',
    `dept_id` BIGINT COMMENT '部门ID',
    `leave_type` VARCHAR(20) NOT NULL COMMENT '请假类型: annual-年假, sick-病假, personal-事假, marriage-婚假, maternity-产假, funeral-丧假',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `duration` DECIMAL(5,2) NOT NULL COMMENT '时长(天)',
    `reason` VARCHAR(500) NOT NULL COMMENT '请假原因',
    `process_instance_id` BIGINT COMMENT '流程实例ID',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft-草稿, pending-审批中, approved-已通过, rejected-已拒绝, cancelled-已取消',
    `current_approver` BIGINT COMMENT '当前审批人',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请假表';

-- 加班表
DROP TABLE IF EXISTS `oa_overtime`;
CREATE TABLE `oa_overtime` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '申请人ID',
    `username` VARCHAR(50) COMMENT '申请人用户名',
    `real_name` VARCHAR(50) COMMENT '申请人姓名',
    `dept_id` BIGINT COMMENT '部门ID',
    `start_time` DATETIME NOT NULL COMMENT '加班开始时间',
    `end_time` DATETIME NOT NULL COMMENT '加班结束时间',
    `duration` DECIMAL(5,2) NOT NULL COMMENT '加班时长(小时)',
    `reason` VARCHAR(500) COMMENT '加班原因',
    `overtime_type` VARCHAR(20) DEFAULT 'weekday' COMMENT '加班类型: weekday-工作日, weekend-周末, holiday-节假日',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态',
    `process_instance_id` BIGINT COMMENT '流程实例ID',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='加班表';

-- =========================================
-- 3. 审批流程模块
-- =========================================

-- 审批流程定义表
DROP TABLE IF EXISTS `oa_process_definition`;
CREATE TABLE `oa_process_definition` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `process_key` VARCHAR(100) NOT NULL COMMENT '流程标识',
    `process_name` VARCHAR(100) NOT NULL COMMENT '流程名称',
    `process_type` VARCHAR(50) COMMENT '流程类型: leave-请假, overtime-加班, resignation-离职, etc',
    `form_id` BIGINT COMMENT '表单ID',
    `icon` VARCHAR(100) COMMENT '图标',
    `description` VARCHAR(500) COMMENT '描述',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `flow_data` JSON COMMENT '流程配置JSON',
    `del_flag` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_process_key_version` (`process_key`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程定义表';

-- 审批流程实例表
DROP TABLE IF EXISTS `oa_process_instance`;
CREATE TABLE `oa_process_instance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `process_definition_id` BIGINT NOT NULL COMMENT '流程定义ID',
    `process_key` VARCHAR(100) NOT NULL COMMENT '流程标识',
    `process_name` VARCHAR(100) COMMENT '流程名称',
    `business_key` VARCHAR(100) COMMENT '业务主键',
    `business_type` VARCHAR(50) COMMENT '业务类型',
    `title` VARCHAR(200) NOT NULL COMMENT '流程标题',
    `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
    `applicant_name` VARCHAR(50) COMMENT '申请人姓名',
    `current_node_key` VARCHAR(50) COMMENT '当前节点Key',
    `current_node_name` VARCHAR(100) COMMENT '当前节点名称',
    `current_approver_id` BIGINT COMMENT '当前审批人ID',
    `current_approver_name` VARCHAR(50) COMMENT '当前审批人姓名',
    `form_data` JSON COMMENT '表单数据JSON',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending-审批中, approved-已通过, rejected-已拒绝, cancelled-已撤回',
    `start_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `duration` INT COMMENT '处理时长(秒)',
    `del_flag` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_applicant_id` (`applicant_id`),
    KEY `idx_status` (`status`),
    KEY `idx_business_key` (`business_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例表';

-- 审批记录表
DROP TABLE IF EXISTS `oa_approval_record`;
CREATE TABLE `oa_approval_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `process_instance_id` BIGINT NOT NULL COMMENT '流程实例ID',
    `task_id` BIGINT COMMENT '任务ID',
    `node_key` VARCHAR(50) COMMENT '节点Key',
    `node_name` VARCHAR(100) COMMENT '节点名称',
    `approver_id` BIGINT NOT NULL COMMENT '审批人ID',
    `approver_name` VARCHAR(50) COMMENT '审批人姓名',
    `action` VARCHAR(20) NOT NULL COMMENT '审批动作: approve-同意, reject-拒绝, transfer-转交, back-退回',
    `comment` VARCHAR(500) COMMENT '审批意见',
    `form_data` JSON COMMENT '表单数据',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
    `start_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `end_time` DATETIME COMMENT '审批时间',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_instance_id` (`process_instance_id`),
    KEY `idx_approver_id` (`approver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审批记录表';

-- =========================================
-- 4. 通知公告模块
-- =========================================

DROP TABLE IF EXISTS `oa_notice`;
CREATE TABLE `oa_notice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `notice_type` VARCHAR(20) DEFAULT 'normal' COMMENT '类型: normal-普通, important-重要, urgent-紧急',
    `publish_scope` VARCHAR(20) DEFAULT 'all' COMMENT '发布范围: all-全体, dept-部门, user-个人',
    `target_dept_ids` VARCHAR(500) COMMENT '目标部门ID列表',
    `target_user_ids` VARCHAR(500) COMMENT '目标用户ID列表',
    `attachment_ids` VARCHAR(500) COMMENT '附件ID列表',
    `publisher_id` BIGINT COMMENT '发布人ID',
    `publisher_name` VARCHAR(50) COMMENT '发布人姓名',
    `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶',
    `is_public` TINYINT DEFAULT 1 COMMENT '是否公开',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `publish_time` DATETIME COMMENT '发布时间',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_publisher_id` (`publisher_id`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告表';

DROP TABLE IF EXISTS `oa_notice_read`;
CREATE TABLE `oa_notice_read` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `notice_id` BIGINT NOT NULL COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `read_time` DATETIME COMMENT '阅读时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_notice_user` (`notice_id`, `user_id`),
    KEY `idx_notice_id` (`notice_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知阅读记录表';

-- =========================================
-- 5. 邮件模块
-- =========================================

DROP TABLE IF EXISTS `oa_mail`;
CREATE TABLE `oa_mail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `mail_id` VARCHAR(100) NOT NULL COMMENT '邮件唯一标识',
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `folder` VARCHAR(20) DEFAULT 'inbox' COMMENT '文件夹: inbox-收件箱, sent-已发送, draft-草稿箱, trash-回收站',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读',
    `is_star` TINYINT DEFAULT 0 COMMENT '是否星标',
    `is_important` TINYINT DEFAULT 0 COMMENT '是否重要',
    `sender_id` BIGINT COMMENT '发件人ID',
    `sender_name` VARCHAR(50) COMMENT '发件人',
    `sender_email` VARCHAR(100) COMMENT '发件人邮箱',
    `to_ids` VARCHAR(500) COMMENT '收件人ID列表',
    `to_names` VARCHAR(500) COMMENT '收件人姓名列表',
    `to_emails` VARCHAR(500) COMMENT '收件人邮箱列表',
    `cc_ids` VARCHAR(500) COMMENT '抄送人ID列表',
    `cc_names` VARCHAR(500) COMMENT '抄送人姓名列表',
    `subject` VARCHAR(200) COMMENT '主题',
    `content` TEXT COMMENT '内容',
    `content_text` TEXT COMMENT '纯文本内容',
    `attachment_ids` VARCHAR(500) COMMENT '附件ID列表',
    `send_time` DATETIME COMMENT '发送时间',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_folder` (`user_id`, `folder`),
    KEY `idx_mail_id` (`mail_id`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_send_time` (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件表';

-- =========================================
-- 6. 日程模块
-- =========================================

DROP TABLE IF EXISTS `oa_schedule`;
CREATE TABLE `oa_schedule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '创建人ID',
    `title` VARCHAR(200) NOT NULL COMMENT '日程标题',
    `content` TEXT COMMENT '日程内容',
    `location` VARCHAR(200) COMMENT '地点',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `all_day` TINYINT DEFAULT 0 COMMENT '是否全天',
    `remind_type` VARCHAR(20) COMMENT '提醒类型: none-不提醒, 5-提前5分钟, 15-提前15分钟, 30-提前30分钟, 60-提前1小时, 1day-提前1天',
    `color` VARCHAR(20) DEFAULT '#00a65a' COMMENT '日程颜色',
    `repeat_type` VARCHAR(20) COMMENT '重复类型: none-不重复, daily-每天, weekly-每周, monthly-每月, yearly-每年',
    `privacy_type` VARCHAR(20) DEFAULT 'public' COMMENT '隐私类型: public-公开, private-私密',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程表';

DROP TABLE IF EXISTS `oa_schedule_share`;
CREATE TABLE `oa_schedule_share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `schedule_id` BIGINT NOT NULL COMMENT '日程ID',
    `share_user_id` BIGINT NOT NULL COMMENT '共享用户ID',
    `can_edit` TINYINT DEFAULT 0 COMMENT '是否可编辑',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_schedule_user` (`schedule_id`, `share_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程共享表';

-- =========================================
-- 7. 任务模块
-- =========================================

DROP TABLE IF EXISTS `oa_task`;
CREATE TABLE `oa_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_no` VARCHAR(50) COMMENT '任务编号',
    `task_name` VARCHAR(200) NOT NULL COMMENT '任务名称',
    `content` TEXT COMMENT '任务内容',
    `priority` VARCHAR(20) DEFAULT 'medium' COMMENT '优先级: low-低, medium-中, high-高, urgent-紧急',
    `task_type` VARCHAR(50) COMMENT '任务类型',
    `project_id` BIGINT COMMENT '所属项目ID',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '截止时间',
    `status` VARCHAR(20) DEFAULT 'todo' COMMENT '状态: todo-待处理, in_progress-进行中, testing-测试中, completed-已完成, closed-已关闭',
    `progress` INT DEFAULT 0 COMMENT '进度百分比',
    `estimated_hours` DECIMAL(10,2) COMMENT '预计工时',
    `spent_hours` DECIMAL(10,2) DEFAULT 0 COMMENT '已用工时',
    `creator_id` BIGINT NOT NULL COMMENT '创建人ID',
    `creator_name` VARCHAR(50) COMMENT '创建人姓名',
    `handler_id` BIGINT COMMENT '负责人ID',
    `handler_name` VARCHAR(50) COMMENT '负责人姓名',
    `dept_id` BIGINT COMMENT '所属部门',
    `attachment_ids` VARCHAR(500) COMMENT '附件ID列表',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_handler_id` (`handler_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

DROP TABLE IF EXISTS `oa_task_user`;
CREATE TABLE `oa_task_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `user_type` VARCHAR(20) DEFAULT 'participant' COMMENT '用户类型: participant-参与人, follower-关注者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_user` (`task_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务参与者表';

DROP TABLE IF EXISTS `oa_task_comment`;
CREATE TABLE `oa_task_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `reply_id` BIGINT COMMENT '回复ID',
    `user_id` BIGINT NOT NULL COMMENT '评论人ID',
    `user_name` VARCHAR(50) COMMENT '评论人姓名',
    `user_avatar` VARCHAR(255) COMMENT '评论人头像',
    `status` TINYINT DEFAULT 1,
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务评论表';

DROP TABLE IF EXISTS `oa_task_log`;
CREATE TABLE `oa_task_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `user_id` BIGINT NOT NULL COMMENT '操作人ID',
    `user_name` VARCHAR(50) COMMENT '操作人姓名',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型',
    `detail` TEXT COMMENT '操作详情',
    `old_value` TEXT COMMENT '旧值',
    `new_value` TEXT COMMENT '新值',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务日志表';

-- =========================================
-- 8. 笔记模块
-- =========================================

DROP TABLE IF EXISTS `oa_note`;
CREATE TABLE `oa_note` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '创建人ID',
    `title` VARCHAR(200) COMMENT '笔记标题',
    `content` TEXT COMMENT '笔记内容',
    `content_text` TEXT COMMENT '纯文本内容',
    `catalog_id` BIGINT COMMENT '分类ID',
    `tags` VARCHAR(500) COMMENT '标签列表',
    `color` VARCHAR(20) DEFAULT '#ffffff' COMMENT '背景颜色',
    `is_public` TINYINT DEFAULT 0 COMMENT '是否公开',
    `share_user_ids` VARCHAR(500) COMMENT '分享用户ID列表',
    `status` TINYINT DEFAULT 1,
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_catalog_id` (`catalog_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔记表';

DROP TABLE IF EXISTS `oa_note_catalog`;
CREATE TABLE `oa_note_catalog` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `catalog_name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `sort` INT DEFAULT 0,
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='笔记分类表';

-- =========================================
-- 9. 通讯录模块
-- =========================================

DROP TABLE IF EXISTS `oa_address_book`;
CREATE TABLE `oa_address_book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `contact_name` VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    `contact_type` VARCHAR(20) DEFAULT 'personal' COMMENT '联系人类型: personal-个人, company-公司',
    `company` VARCHAR(100) COMMENT '公司名称',
    `department` VARCHAR(100) COMMENT '部门',
    `position` VARCHAR(50) COMMENT '职位',
    `phone` VARCHAR(50) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `telephone` VARCHAR(50) COMMENT '固定电话',
    `fax` VARCHAR(50) COMMENT '传真',
    `address` VARCHAR(200) COMMENT '地址',
    `birthday` DATE COMMENT '生日',
    `wechat` VARCHAR(50) COMMENT '微信',
    `qq` VARCHAR(20) COMMENT 'QQ',
    `remark` VARCHAR(500) COMMENT '备注',
    `star` TINYINT DEFAULT 0 COMMENT '是否星标',
    `group_id` BIGINT COMMENT '分组ID',
    `status` TINYINT DEFAULT 1,
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录表';

DROP TABLE IF EXISTS `oa_address_group`;
CREATE TABLE `oa_address_group` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `group_name` VARCHAR(50) NOT NULL COMMENT '分组名称',
    `group_type` VARCHAR(20) DEFAULT 'personal' COMMENT '分组类型',
    `sort` INT DEFAULT 0,
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录分组表';

-- =========================================
-- 10. 聊天模块
-- =========================================

DROP TABLE IF EXISTS `oa_chat_group`;
CREATE TABLE `oa_chat_group` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `group_name` VARCHAR(50) NOT NULL COMMENT '群名称',
    `avatar` VARCHAR(255) COMMENT '群头像',
    `owner_id` BIGINT NOT NULL COMMENT '群主ID',
    `notice` VARCHAR(500) COMMENT '群公告',
    `max_member` INT DEFAULT 200 COMMENT '最大成员数',
    `member_count` INT DEFAULT 0 COMMENT '当前成员数',
    `status` TINYINT DEFAULT 1,
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群聊表';

DROP TABLE IF EXISTS `oa_chat_member`;
CREATE TABLE `oa_chat_member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `group_id` BIGINT NOT NULL COMMENT '群ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `nickname` VARCHAR(50) COMMENT '群内昵称',
    `role` VARCHAR(20) DEFAULT 'member' COMMENT '角色: owner-群主, admin-管理员, member-普通成员',
    `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `last_read_time` DATETIME COMMENT '最后阅读时间',
    `is_muted` TINYINT DEFAULT 0 COMMENT '是否禁言',
    `is_pinned` TINYINT DEFAULT 0 COMMENT '是否置顶',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_user` (`group_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群成员表';

DROP TABLE IF EXISTS `oa_chat_message`;
CREATE TABLE `oa_chat_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `group_id` BIGINT NOT NULL COMMENT '群ID',
    `user_id` BIGINT NOT NULL COMMENT '发送人ID',
    `user_name` VARCHAR(50) COMMENT '发送人姓名',
    `user_avatar` VARCHAR(255) COMMENT '发送人头像',
    `message_type` VARCHAR(20) DEFAULT 'text' COMMENT '消息类型: text-文本, image-图片, file-文件, emoji-表情',
    `content` TEXT COMMENT '消息内容',
    `attachment_url` VARCHAR(500) COMMENT '附件URL',
    `attachment_name` VARCHAR(200) COMMENT '附件名称',
    `attachment_size` BIGINT COMMENT '附件大小',
    `reply_id` BIGINT COMMENT '回复ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_group_id` (`group_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息表';

-- =========================================
-- 11. 文件模块
-- =========================================

DROP TABLE IF EXISTS `oa_file`;
CREATE TABLE `oa_file` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `file_name` VARCHAR(200) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `file_type` VARCHAR(50) COMMENT '文件类型',
    `file_ext` VARCHAR(20) COMMENT '文件扩展名',
    `storage_type` VARCHAR(20) DEFAULT 'local' COMMENT '存储类型: local-本地, oss-阿里云, minio-MinIO',
    `bucket` VARCHAR(100) COMMENT '存储桶',
    `upload_type` VARCHAR(20) DEFAULT 'common' COMMENT '上传类型: common-普通, avatar-头像, attachment-附件',
    `user_id` BIGINT COMMENT '上传用户ID',
    `biz_type` VARCHAR(50) COMMENT '业务类型',
    `biz_id` BIGINT COMMENT '业务ID',
    `status` TINYINT DEFAULT 1,
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

DROP TABLE IF EXISTS `oa_file_folder`;
CREATE TABLE `oa_file_folder` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `folder_name` VARCHAR(100) NOT NULL COMMENT '文件夹名称',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父文件夹ID',
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `folder_path` VARCHAR(500) COMMENT '文件夹路径',
    `is_public` TINYINT DEFAULT 0 COMMENT '是否公共',
    `sort` INT DEFAULT 0,
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件文件夹表';

-- =========================================
-- 12. 初始化数据
-- =========================================

-- 插入超级管理员角色
INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `role_type`, `data_scope`, `status`, `remark`) VALUES
(1, '超级管理员', 'super_admin', 'system', 1, 1, '系统超级管理员，拥有所有权限');

-- 插入默认部门
INSERT INTO `sys_dept` (`id`, `dept_name`, `dept_code`, `leader`, `sort`, `status`) VALUES
(1, '总公司', 'HQ', 1, 0, 1),
(2, '技术部', 'TECH', 1, 1, 1),
(3, '运营部', 'OPS', NULL, 2, 1),
(4, '人事部', 'HR', NULL, 3, 1),
(5, '财务部', 'FIN', NULL, 4, 1);

-- 插入默认职位
INSERT INTO `sys_position` (`id`, `position_name`, `position_code`, `dept_id`, `sort`) VALUES
(1, '总经理', 'CEO', 1, 1),
(2, '技术总监', 'CTO', 2, 1),
(3, 'Java开发工程师', 'JAVA_DEV', 2, 2),
(4, '前端开发工程师', 'FE_DEV', 2, 3),
(5, '运营总监', 'COO', 3, 1),
(6, '人事经理', 'HR_MGR', 4, 1);

-- 插入默认用户 (密码: admin123)
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `nickname`, `email`, `phone`, `sex`, `dept_id`, `position_id`, `status`, `is_admin`, `remark`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', '管理员', 'admin@example.com', '13800138000', 1, 1, 1, 1, 1, '系统内置超级管理员账号');

-- 绑定管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 插入菜单数据
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `icon`, `path`, `component`, `perms`, `sort`, `status`) VALUES
-- 一级菜单
(1, 0, '工作台', 'C', 'Odometer', '/dashboard', 'dashboard/index', '', 0, 1),
(1401, 0, '用户管理', 'C', 'UserFilled', '/user', '', '', 1, 1),
(10, 0, '系统管理', 'C', 'Setting', '/system', '', '', 2, 1),
(2, 0, '考勤管理', 'C', 'Clock', '/attendance', '', '', 3, 1),
(3, 0, '审批流程', 'C', 'Document', '/process', '', '', 4, 1),
(4, 0, '通知公告', 'C', 'Bell', '/notice', '', '', 5, 1),
(5, 0, '邮件', 'C', 'Message', '/mail', '', '', 6, 1),
(6, 0, '日程', 'C', 'Calendar', '/schedule', '', '', 7, 1),
(7, 0, '任务', 'C', 'Collection', '/task', '', '', 8, 1),
(8, 0, '通讯录', 'C', 'User', '/address', '', '', 9, 1),
(9, 0, '笔记', 'C', 'Edit', '/note', '', '', 10, 1),
(1101, 0, '工作计划', 'C', 'TrendCharts', '/plan', '', '', 11, 1),
(1201, 0, '文件管理', 'C', 'Folder', '/file', '', '', 12, 1),
(1301, 0, '讨论区', 'C', 'ChatDotRound', '/chat', '', '', 13, 1),
-- 考勤子菜单
(201, 2, '考勤打卡', 'C', '', '/attendance/checkin', 'attendance/checkin', 'attendance:checkin:list', 0, 1),
(202, 2, '请假申请', 'C', '', '/attendance/leave', 'attendance/leave', 'attendance:leave:list', 1, 1),
(203, 2, '加班申请', 'C', '', '/attendance/overtime', 'attendance/overtime', 'attendance:overtime:list', 2, 1),
(204, 2, '考勤周报表', 'C', '', '/attendance/weekly', 'attendance/weekly', 'attendance:weekly:list', 3, 1),
(205, 2, '考勤月报表', 'C', '', '/attendance/monthly', 'attendance/monthly', 'attendance:monthly:list', 4, 1),
(206, 2, '考勤列表', 'C', '', '/attendance/list', 'attendance/list', 'attendance:list:list', 5, 1),
-- 审批子菜单
(301, 3, '新建流程', 'C', '', '/process/new', 'process/new', 'process:new:add', 0, 1),
(302, 3, '我的申请', 'C', '', '/process/my', 'process/my', 'process:my:list', 1, 1),
(303, 3, '待我审批', 'C', '', '/process/pending', 'process/pending', 'process:pending:list', 2, 1),
-- 通知子菜单
(401, 4, '公告列表', 'C', '', '/notice/list', 'notice/list', 'notice:list:list', 0, 1),
(402, 4, '发布公告', 'C', '', '/notice/publish', 'notice/publish', 'notice:publish:add', 1, 1),
-- 邮件子菜单
(504, 5, '邮箱管理', 'C', '', '/mail/list', 'mail/inbox', 'mail:inbox:list', 0, 1),
(503, 5, '账号管理', 'C', '', '/mail/account', 'mail/account', 'mail:account:list', 1, 1),
-- 日程子菜单
(601, 6, '日程安排', 'C', '', '/schedule/list', 'schedule/index', 'schedule:list:list', 0, 1),
-- 任务子菜单
(701, 7, '我的任务', 'C', '', '/task/my', 'task/my', 'task:my:list', 0, 1),
(702, 7, '全部任务', 'C', '', '/task/all', 'task/all', 'task:all:list', 1, 1),
-- 通讯录子菜单
(801, 8, '通讯录管理', 'C', '', '/address/list', 'address/index', 'address:list:list', 0, 1),
-- 笔记子菜单
(901, 9, '笔记管理', 'C', '', '/note/list', 'note/list', 'note:list:list', 0, 1),
-- 工作计划子菜单
(1102, 1101, '计划管理', 'C', '', '/plan/list', 'plan/list', 'plan:list:list', 0, 1),
-- 文件管理子菜单
(1202, 1201, '文件管理', 'C', '', '/file/list', 'file/index', 'file:list:list', 0, 1),
-- 讨论区子菜单
(1302, 1301, '讨论区列表', 'C', '', '/chat/list', 'chat/index', 'chat:list:list', 0, 1),
(1303, 1301, '超级管理员', 'C', '', '/chat/admin', 'chat/admin', 'chat:admin:list', 1, 1),
(1304, 1301, '我的管理', 'C', '', '/chat/manage', 'chat/manage', 'chat:manage:list', 2, 1),
-- 用户管理子菜单
(1402, 1401, '用户管理', 'C', '', '/user/list', 'system/user/index', 'system:user:list', 0, 1),
(1403, 1401, '角色管理', 'C', '', '/user/role', 'system/role/index', 'system:role:list', 1, 1),
(1404, 1401, '部门管理', 'C', '', '/user/dept', 'system/dept/index', 'system:dept:list', 2, 1),
(1405, 1401, '职位管理', 'C', '', '/user/position', 'system/position', 'system:position:list', 3, 1),
(1406, 1401, '在线用户', 'C', '', '/user/online', 'system/online', 'system:online:list', 4, 1),
-- 系统管理子菜单
(1001, 10, '菜单管理', 'C', '', '/system/menu', 'system/menu/index', 'system:menu:list', 0, 1),
(1002, 10, '类型管理', 'C', '', '/system/type', 'system/systemType', 'system:type:list', 1, 1),
(1003, 10, '状态管理', 'C', '', '/system/status', 'system/systemStatus', 'system:status:list', 2, 1);

-- 给超级管理员分配所有菜单
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_menu` WHERE `del_flag` = 0;

-- 插入字典类型
INSERT INTO `sys_dict_type` (`id`, `dict_name`, `dict_code`, `dict_type`, `remark`) VALUES
(1, '用户性别', 'sys_user_sex', 'string', '用户性别字典'),
(2, '菜单状态', 'sys_menu_status', 'string', '菜单状态字典'),
(3, '角色状态', 'sys_role_status', 'string', '角色状态字典'),
(4, '用户状态', 'sys_user_status', 'string', '用户状态字典'),
(5, '请假类型', 'oa_leave_type', 'string', '请假类型字典'),
(6, '任务优先级', 'oa_task_priority', 'string', '任务优先级字典'),
(7, '任务状态', 'oa_task_status', 'string', '任务状态字典'),
(8, '考勤状态', 'oa_attendance_status', 'string', '考勤状态字典'),
(9, '通知类型', 'oa_notice_type', 'string', '通知公告类型'),
(10, '流程状态', 'oa_process_status', 'string', '流程状态字典');

-- 插入字典数据
INSERT INTO `sys_dict_data` (`dict_type_id`, `dict_label`, `dict_value`, `sort`) VALUES
-- 性别
(1, '男', '1', 1), (1, '女', '2', 2), (1, '未知', '0', 0),
-- 菜单状态
(2, '正常', '1', 1), (2, '停用', '0', 0),
-- 角色状态
(3, '正常', '1', 1), (3, '停用', '0', 0),
-- 用户状态
(4, '正常', '1', 1), (4, '禁用', '0', 0),
-- 请假类型
(5, '年假', 'annual', 1), (5, '病假', 'sick', 2), (5, '事假', 'personal', 3), (5, '婚假', 'marriage', 4), (5, '产假', 'maternity', 5), (5, '丧假', 'funeral', 6),
-- 任务优先级
(6, '紧急', 'urgent', 1), (6, '高', 'high', 2), (6, '中', 'medium', 3), (6, '低', 'low', 4),
-- 任务状态
(7, '待处理', 'todo', 1), (7, '进行中', 'in_progress', 2), (7, '测试中', 'testing', 3), (7, '已完成', 'completed', 4), (7, '已关闭', 'closed', 5),
-- 考勤状态
(8, '正常', 'normal', 1), (8, '迟到', 'late', 2), (8, '早退', 'early', 3), (8, '缺勤', 'absent', 4),
-- 通知类型
(9, '普通通知', 'normal', 1), (9, '重要通知', 'important', 2), (9, '紧急通知', 'urgent', 3),
-- 流程状态
(10, '草稿', 'draft', 0), (10, '审批中', 'pending', 1), (10, '已通过', 'approved', 2), (10, '已拒绝', 'rejected', 3), (10, '已撤回', 'cancelled', 4);

-- =========================================
-- 13. 邮件账号表
-- =========================================

DROP TABLE IF EXISTS `oa_mail_account`;
CREATE TABLE `oa_mail_account` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `account_type` VARCHAR(50) DEFAULT 'system' COMMENT '账号类型: system-系统邮件',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-有效',
    `account_name` VARCHAR(100) COMMENT '名称',
    `sender_nickname` VARCHAR(100) COMMENT '发件昵称',
    `account_addr` VARCHAR(200) COMMENT '邮件账号',
    `auth_code` VARCHAR(200) COMMENT '授权码',
    `remark` VARCHAR(500) COMMENT '备注',
    `user_id` BIGINT COMMENT '创建用户ID',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮件账号表';

-- =========================================
-- 14. 通知公告评论表
-- =========================================

DROP TABLE IF EXISTS `oa_notice_comment`;
CREATE TABLE `oa_notice_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `notice_id` BIGINT NOT NULL COMMENT '公告ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `user_id` BIGINT COMMENT '评论人ID',
    `user_name` VARCHAR(100) COMMENT '评论人姓名',
    `reply_id` BIGINT COMMENT '回复目标评论ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-删除',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME COMMENT '创建时间',
    `create_by` BIGINT DEFAULT NULL COMMENT '创建者',
    `update_by` BIGINT DEFAULT NULL COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_notice_id` (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告评论表';

-- =========================================
-- 15. 讨论区表
-- =========================================

DROP TABLE IF EXISTS `aoa_discuss_list`;
CREATE TABLE `aoa_discuss_list` (
    `discuss_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '讨论ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `attachment_id` BIGINT COMMENT '附件ID',
    `status_id` BIGINT COMMENT '状态ID',
    `type_id` BIGINT COMMENT '类型ID',
    `visit_num` INT DEFAULT 0 COMMENT '浏览次数',
    `discuss_user_id` BIGINT COMMENT '发布用户ID',
    `vote_id` BIGINT COMMENT '投票ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time` DATETIME COMMENT '修改时间',
    PRIMARY KEY (`discuss_id`),
    KEY `idx_user_id` (`discuss_user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论区表';

-- =========================================
-- 16. 工作计划表
-- =========================================

DROP TABLE IF EXISTS `aoa_plan_list`;
CREATE TABLE `aoa_plan_list` (
    `plan_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '计划ID',
    `title` VARCHAR(200) COMMENT '标题',
    `plan_content` TEXT COMMENT '计划内容',
    `plan_summary` VARCHAR(500) COMMENT '计划摘要',
    `start_time` DATETIME COMMENT '开始时间',
    `end_time` DATETIME COMMENT '结束时间',
    `plan_user_id` BIGINT COMMENT '创建用户ID',
    `plan_comment` TEXT COMMENT '计划评论',
    `type_id` BIGINT COMMENT '类型ID',
    `status_id` BIGINT COMMENT '状态ID',
    `label` VARCHAR(100) COMMENT '标签',
    `attach_id` BIGINT COMMENT '附件ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`plan_id`),
    KEY `idx_user_id` (`plan_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作计划表';

-- =========================================
-- 17. 文件路径表（文件管理模块）
-- =========================================

DROP TABLE IF EXISTS `aoa_file_path`;
CREATE TABLE `aoa_file_path` (
    `path_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '路径ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父路径ID',
    `path_name` VARCHAR(100) NOT NULL COMMENT '路径名称',
    `path_user_id` BIGINT NOT NULL COMMENT '创建用户ID',
    `path_istrash` TINYINT DEFAULT 0 COMMENT '是否删除',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`path_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_user_id` (`path_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件路径表';

DROP TABLE IF EXISTS `aoa_file_list`;
CREATE TABLE `aoa_file_list` (
    `file_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `file_name` VARCHAR(200) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    `file_shuffix` VARCHAR(20) COMMENT '文件后缀',
    `content_type` VARCHAR(100) COMMENT '内容类型',
    `model` VARCHAR(50) COMMENT '模块',
    `path_id` BIGINT COMMENT '路径ID',
    `size` BIGINT COMMENT '文件大小(字节)',
    `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `file_user_id` BIGINT COMMENT '上传用户ID',
    `file_istrash` TINYINT DEFAULT 0 COMMENT '是否删除: 0-正常, 1-回收站',
    `file_isshare` TINYINT DEFAULT 0 COMMENT '是否共享',
    PRIMARY KEY (`file_id`),
    KEY `idx_path_id` (`path_id`),
    KEY `idx_user_id` (`file_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- =========================================
-- 18. 讨论区扩展表（回复、点赞、投票）
-- =========================================

-- 讨论回复表
DROP TABLE IF EXISTS `aoa_discuss_reply`;
CREATE TABLE `aoa_discuss_reply` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '回复ID',
    `discuss_id` BIGINT NOT NULL COMMENT '讨论ID',
    `content` TEXT COMMENT '回复内容',
    `user_id` BIGINT COMMENT '回复用户ID',
    `user_name` VARCHAR(50) COMMENT '回复用户名',
    `reply_id` BIGINT DEFAULT 0 COMMENT '父回复ID',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_discuss_id` (`discuss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论回复表';

-- 讨论点赞表
DROP TABLE IF EXISTS `aoa_discuss_like`;
CREATE TABLE `aoa_discuss_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `discuss_id` BIGINT DEFAULT NULL COMMENT '帖子ID',
    `reply_id` BIGINT DEFAULT NULL COMMENT '回复ID',
    `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
    `del_flag` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_discuss_user` (`discuss_id`, `user_id`),
    UNIQUE KEY `uk_reply_user` (`reply_id`, `user_id`),
    KEY `idx_discuss_id` (`discuss_id`),
    KEY `idx_reply_id` (`reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='讨论点赞表';

-- 投票选项表
DROP TABLE IF EXISTS `aoa_discuss_vote_item`;
CREATE TABLE `aoa_discuss_vote_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `discuss_id` BIGINT NOT NULL COMMENT '讨论ID',
    `content` VARCHAR(200) NOT NULL COMMENT '选项内容',
    `vote_count` INT DEFAULT 0 COMMENT '得票数',
    `del_flag` TINYINT DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_discuss_id` (`discuss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票选项表';

-- 投票记录表
DROP TABLE IF EXISTS `aoa_discuss_vote_record`;
CREATE TABLE `aoa_discuss_vote_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `discuss_id` BIGINT NOT NULL COMMENT '讨论ID',
    `vote_item_id` BIGINT NOT NULL COMMENT '选项ID',
    `user_id` BIGINT NOT NULL COMMENT '投票用户ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_discuss_user` (`discuss_id`, `user_id`),
    KEY `idx_discuss_id` (`discuss_id`),
    KEY `idx_vote_item_id` (`vote_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投票记录表';

-- =========================================
-- 19. 通讯录分享表
-- =========================================

DROP TABLE IF EXISTS `oa_address_share`;
CREATE TABLE `oa_address_share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `contact_id` BIGINT NOT NULL COMMENT '联系人ID',
    `shared_by` BIGINT NOT NULL COMMENT '分享者用户ID',
    `shared_with` BIGINT NOT NULL COMMENT '被分享用户ID',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建者',
    `update_by` BIGINT COMMENT '更新者',
    PRIMARY KEY (`id`),
    KEY `idx_contact_id` (`contact_id`),
    KEY `idx_shared_by` (`shared_by`),
    KEY `idx_shared_with` (`shared_with`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通讯录分享表';

-- =========================================
-- 20. 初始化数据（扩展）
-- =========================================

-- 插入邮件账号示例
INSERT INTO `oa_mail_account` (`id`, `account_type`, `account_name`, `sender_nickname`, `account_addr`, `auth_code`, `status`, `remark`) VALUES
(1, 'system', '系统邮件', '系统邮件', 'system@example.com', 'abc123', 1, '系统邮件账号');

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;
