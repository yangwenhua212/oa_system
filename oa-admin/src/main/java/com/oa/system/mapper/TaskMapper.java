package com.oa.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oa.system.entity.Task;
import com.oa.system.entity.TaskComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 任务Mapper接口
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 查询任务列表
     */
    List<Task> selectTaskList(Task task);

    /**
     * 查询我的任务
     */
    List<Task> selectMyTaskList(@Param("userId") Long userId);

    /**
     * 查询任务评论
     */
    List<TaskComment> selectTaskCommentList(@Param("taskId") Long taskId);

    /**
     * 生成任务编号
     */
    String selectMaxTaskNo();

    /**
     * 查询任务完成排行（按完成数量排序）
     */
    List<Map<String, Object>> selectCompletionRank();
}
