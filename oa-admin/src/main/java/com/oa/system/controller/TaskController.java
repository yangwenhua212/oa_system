package com.oa.system.controller;

import com.oa.system.common.PageDTO;
import com.oa.system.common.PageR;
import com.oa.system.common.R;
import com.oa.system.entity.Task;
import com.oa.system.entity.TaskComment;
import com.oa.system.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务管理控制器
 */
@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 创建任务
     */
    @PostMapping
    public R create(@RequestBody Task task) {
        Long id = taskService.createTask(task);
        return R.ok(id);
    }

    /**
     * 更新任务
     */
    @PutMapping
    public R update(@RequestBody Task task) {
        taskService.updateTask(task);
        return R.ok();
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return R.ok();
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return R.ok(task);
    }

    /**
     * 分页查询任务列表
     */
    @GetMapping("/list")
    public R list(PageDTO<Task> pageDTO) {
        PageR<Task> result = taskService.getTaskPage(pageDTO);
        return R.ok(result);
    }

    /**
     * 获取我的任务列表
     */
    @GetMapping("/my")
    public R myList() {
        List<Task> list = taskService.getMyTaskList();
        return R.ok(list);
    }

    /**
     * 更新任务状态
     */
    @PutMapping("/status/{id}")
    public R updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        String status = (String) params.get("status");
        Integer progress = (Integer) params.get("progress");
        taskService.updateTaskStatus(id, status, progress);
        return R.ok();
    }

    /**
     * 添加任务评论
     */
    @PostMapping("/comment")
    public R addComment(@RequestBody TaskComment comment) {
        taskService.addComment(comment);
        return R.ok();
    }

    /**
     * 获取任务评论列表
     */
    @GetMapping("/comment/{taskId}")
    public R commentList(@PathVariable Long taskId) {
        List<TaskComment> list = taskService.getCommentList(taskId);
        return R.ok(list);
    }

    /**
     * 获取任务完成排行
     */
    @GetMapping("/completion-rank")
    public R completionRank() {
        List<Map<String, Object>> list = taskService.getCompletionRank();
        return R.ok(list);
    }
}
