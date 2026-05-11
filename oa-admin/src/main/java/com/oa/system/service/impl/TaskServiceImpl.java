package com.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Dept;
import com.oa.system.entity.Task;
import com.oa.system.entity.TaskComment;
import com.oa.system.entity.User;
import com.oa.system.mapper.DeptMapper;
import com.oa.system.mapper.TaskMapper;
import com.oa.system.mapper.UserMapper;
import com.oa.system.service.TaskService;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 任务服务实现类
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    @Transactional
    public Long createTask(Task task) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        task.setCreatorId(userId);
        task.setCreatorName(user.getRealName());
        task.setStatus("todo");
        task.setProgress(0);

        // 生成任务编号
        String maxNo = taskMapper.selectMaxTaskNo();
        if (maxNo == null) {
            task.setTaskNo("TASK" + "001");
        } else {
            int num = Integer.parseInt(maxNo.replace("TASK", "")) + 1;
            task.setTaskNo("TASK" + String.format("%03d", num));
        }

        if (user.getDeptId() != null) {
            task.setDeptId(user.getDeptId());
            Dept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                task.setDeptName(dept.getDeptName());
            }
        }

        taskMapper.insert(task);
        return task.getId();
    }

    @Override
    public boolean updateTask(Task task) {
        taskMapper.updateById(task);
        return true;
    }

    @Override
    public boolean deleteTask(Long id) {
        taskMapper.deleteById(id);
        return true;
    }

    @Override
    public Task getTaskById(Long id) {
        Task task = taskMapper.selectById(id);
        if (task != null && task.getDeptId() != null) {
            Dept dept = deptMapper.selectById(task.getDeptId());
            if (dept != null) {
                task.setDeptName(dept.getDeptName());
            }
        }
        return task;
    }

    @Override
    public PageR<Task> getTaskPage(PageDTO<Task> pageDTO) {
        Page<Task> page = new Page<>(pageDTO.getPageNum(), pageDTO.getPageSize());
        Task params = pageDTO.getParams() != null ? pageDTO.getParams() : new Task();

        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(params.getTaskName() != null, Task::getTaskName, params.getTaskName())
                .eq(params.getStatus() != null, Task::getStatus, params.getStatus())
                .eq(params.getHandlerId() != null, Task::getHandlerId, params.getHandlerId())
                .orderByDesc(Task::getCreateTime);

        Page<Task> result = taskMapper.selectPage(page, wrapper);
        return PageR.ok(result.getTotal(), result.getRecords());
    }

    @Override
    public List<Task> getMyTaskList() {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        return taskMapper.selectMyTaskList(userId);
    }

    @Override
    @Transactional
    public boolean updateTaskStatus(Long id, String status, Integer progress) {
        Task task = taskMapper.selectById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        if (status != null) {
            task.setStatus(status);
        }
        if (progress != null) {
            task.setProgress(progress);
            if (progress == 100) {
                task.setStatus("completed");
            }
        }

        taskMapper.updateById(task);
        return true;
    }

    @Override
    @Transactional
    public boolean addComment(TaskComment comment) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        comment.setUserId(userId);
        comment.setUserName(user.getRealName());
        comment.setUserAvatar(user.getAvatar());
        comment.setStatus(1);

        return true;
    }

    @Override
    public List<TaskComment> getCommentList(Long taskId) {
        return taskMapper.selectTaskCommentList(taskId);
    }

    @Override
    public List<Map<String, Object>> getCompletionRank() {
        return taskMapper.selectCompletionRank();
    }
}
