package com.oa.system.service;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.entity.Task;
import com.oa.system.entity.TaskComment;

import java.util.List;
import java.util.Map;

/**
 * 任务服务接口
 */
public interface TaskService {

    /**
     * 创建任务
     */
    Long createTask(Task task);

    /**
     * 更新任务
     */
    boolean updateTask(Task task);

    /**
     * 删除任务
     */
    boolean deleteTask(Long id);

    /**
     * 获取任务详情
     */
    Task getTaskById(Long id);

    /**
     * 分页查询任务列表
     */
    PageR<Task> getTaskPage(PageDTO<Task> pageDTO);

    /**
     * 获取我的任务列表
     */
    List<Task> getMyTaskList();

    /**
     * 更新任务状态
     */
    boolean updateTaskStatus(Long id, String status, Integer progress);

    /**
     * 添加任务评论
     */
    boolean addComment(TaskComment comment);

    /**
     * 获取任务评论列表
     */
    List<TaskComment> getCommentList(Long taskId);

    /**
     * 获取任务完成排行
     */
    List<Map<String, Object>> getCompletionRank();
}
