package com.oa.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oa.system.common.R;
import com.oa.system.entity.*;
import com.oa.system.mapper.*;
import com.oa.system.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 讨论区控制器
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final DiscussMapper discussMapper;
    private final UserMapper userMapper;
    private final DeptMapper deptMapper;
    private final DiscussReplyMapper discussReplyMapper;
    private final DiscussLikeMapper discussLikeMapper;
    private final DiscussVoteItemMapper discussVoteItemMapper;
    private final DiscussVoteRecordMapper discussVoteRecordMapper;
    private final JwtTokenUtil jwtTokenUtil;

    // 填充用户信息和部门名称
    private void enrichDiscuss(Discuss d) {
        if (d.getDiscussUserId() != null) {
            User user = userMapper.selectById(d.getDiscussUserId());
            if (user != null) {
                d.setDiscussUserName(user.getRealName());
                // 查询部门名称
                if (user.getDeptId() != null) {
                    Dept dept = deptMapper.selectById(user.getDeptId());
                    if (dept != null) {
                        d.setDeptName(dept.getDeptName());
                    }
                }
            }
        }
        // 统计回复数
        Long replyCount = discussReplyMapper.selectCount(
                new LambdaQueryWrapper<DiscussReply>()
                        .eq(DiscussReply::getDiscussId, d.getDiscussId())
                        .eq(DiscussReply::getDelFlag, 0));
        d.setReplyCount(replyCount != null ? replyCount.intValue() : 0);
    }

    // ===================== 帖子 CRUD =====================

    /**
     * 获取帖子列表
     */
    @GetMapping("/posts")
    public R posts() {
        LambdaQueryWrapper<Discuss> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Discuss::getCreateTime);
        List<Discuss> list = discussMapper.selectList(wrapper);
        list.forEach(this::enrichDiscuss);
        return R.ok(list);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/posts/{id}")
    public R getPost(@PathVariable Long id) {
        Discuss discuss = discussMapper.selectById(id);
        if (discuss != null) {
            // 增加浏览次数
            discuss.setVisitNum(discuss.getVisitNum() == null ? 1 : discuss.getVisitNum() + 1);
            discussMapper.updateById(discuss);
            enrichDiscuss(discuss);
        }
        return R.ok(discuss);
    }

    /**
     * 发布帖子
     */
    @PostMapping("/posts")
    public R createPost(@RequestBody Discuss discuss) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        discuss.setDiscussUserId(userId);
        discuss.setVisitNum(0);
        discussMapper.insert(discuss);
        return R.ok(discuss.getDiscussId());
    }

    /**
     * 更新帖子
     */
    @PutMapping("/posts/{id}")
    public R updatePost(@PathVariable Long id, @RequestBody Discuss discuss) {
        Discuss existing = discussMapper.selectById(id);
        if (existing == null) {
            return R.error("帖子不存在");
        }
        if (discuss.getTitle() != null) {
            existing.setTitle(discuss.getTitle());
        }
        if (discuss.getContent() != null) {
            existing.setContent(discuss.getContent());
        }
        if (discuss.getAttachmentId() != null) {
            existing.setAttachmentId(discuss.getAttachmentId());
        }
        discussMapper.updateById(existing);
        return R.ok();
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/posts/{id}")
    public R deletePost(@PathVariable Long id) {
        discussMapper.deleteById(id);
        return R.ok();
    }

    // ===================== 回复相关 =====================

    /**
     * 获取回复列表
     */
    @GetMapping("/posts/{id}/replies")
    public R getReplies(@PathVariable Long id) {
        LambdaQueryWrapper<DiscussReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DiscussReply::getDiscussId, id)
                .eq(DiscussReply::getDelFlag, 0)
                .orderByAsc(DiscussReply::getCreateTime);
        List<DiscussReply> list = discussReplyMapper.selectList(wrapper);
        return R.ok(list);
    }

    /**
     * 发表回复
     */
    @PostMapping("/posts/{id}/replies")
    public R createReply(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        User user = userMapper.selectById(userId);

        DiscussReply reply = new DiscussReply();
        reply.setDiscussId(id);
        reply.setContent(body.get("content"));
        reply.setUserId(userId);
        reply.setUserName(user != null ? user.getRealName() : "未知");
        reply.setLikeCount(0);
        discussReplyMapper.insert(reply);
        return R.ok(reply.getId());
    }

    /**
     * 删除回复
     */
    @DeleteMapping("/replies/{id}")
    public R deleteReply(@PathVariable Long id) {
        discussReplyMapper.deleteById(id);
        return R.ok();
    }

    // ===================== 点赞相关 =====================

    /**
     * 点赞帖子
     */
    @PostMapping("/posts/{id}/like")
    public R likePost(@PathVariable Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());

        Long count = discussLikeMapper.selectCount(
                new LambdaQueryWrapper<DiscussLike>()
                        .eq(DiscussLike::getDiscussId, id)
                        .eq(DiscussLike::getUserId, userId)
                        .eq(DiscussLike::getDelFlag, 0));
        if (count > 0) {
            return R.error("已点赞");
        }

        DiscussLike like = new DiscussLike();
        like.setDiscussId(id);
        like.setUserId(userId);
        discussLikeMapper.insert(like);
        return R.ok();
    }

    /**
     * 取消点赞帖子
     */
    @PostMapping("/posts/{id}/unlike")
    public R unlikePost(@PathVariable Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        discussLikeMapper.delete(
                new LambdaQueryWrapper<DiscussLike>()
                        .eq(DiscussLike::getDiscussId, id)
                        .eq(DiscussLike::getUserId, userId));
        return R.ok();
    }

    /**
     * 点赞回复
     */
    @PostMapping("/replies/{id}/like")
    public R likeReply(@PathVariable Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());

        Long count = discussLikeMapper.selectCount(
                new LambdaQueryWrapper<DiscussLike>()
                        .eq(DiscussLike::getReplyId, id)
                        .eq(DiscussLike::getUserId, userId)
                        .eq(DiscussLike::getDelFlag, 0));
        if (count > 0) {
            return R.error("已点赞");
        }

        DiscussLike like = new DiscussLike();
        like.setReplyId(id);
        like.setUserId(userId);
        discussLikeMapper.insert(like);

        DiscussReply reply = discussReplyMapper.selectById(id);
        if (reply != null) {
            reply.setLikeCount(reply.getLikeCount() == null ? 1 : reply.getLikeCount() + 1);
            discussReplyMapper.updateById(reply);
        }
        return R.ok();
    }

    // ===================== 投票相关 =====================

    /**
     * 获取投票选项
     */
    @GetMapping("/posts/{id}/vote")
    public R getVote(@PathVariable Long id) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());

        List<DiscussVoteItem> items = discussVoteItemMapper.selectList(
                new LambdaQueryWrapper<DiscussVoteItem>()
                        .eq(DiscussVoteItem::getDiscussId, id)
                        .eq(DiscussVoteItem::getDelFlag, 0));

        int totalVotes = items.stream().mapToInt(i -> i.getVoteCount() != null ? i.getVoteCount() : 0).sum();

        DiscussVoteRecord record = discussVoteRecordMapper.selectOne(
                new LambdaQueryWrapper<DiscussVoteRecord>()
                        .eq(DiscussVoteRecord::getDiscussId, id)
                        .eq(DiscussVoteRecord::getUserId, userId));

        Map<String, Object> result = new HashMap<>();
        result.put("options", items);
        result.put("totalVotes", totalVotes);
        result.put("hasVoted", record != null);
        result.put("selectedOptionId", record != null ? record.getVoteItemId() : null);
        return R.ok(result);
    }

    /**
     * 投票
     */
    @PostMapping("/posts/{id}/vote")
    public R vote(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = jwtTokenUtil.getUserIdFromToken(jwtTokenUtil.getTokenFromRequest());
        Long optionId = body.get("optionId");
        if (optionId == null) {
            return R.error("请选择投票选项");
        }

        Long count = discussVoteRecordMapper.selectCount(
                new LambdaQueryWrapper<DiscussVoteRecord>()
                        .eq(DiscussVoteRecord::getDiscussId, id)
                        .eq(DiscussVoteRecord::getUserId, userId));
        if (count > 0) {
            return R.error("已投票");
        }

        DiscussVoteRecord record = new DiscussVoteRecord();
        record.setDiscussId(id);
        record.setVoteItemId(optionId);
        record.setUserId(userId);
        discussVoteRecordMapper.insert(record);

        DiscussVoteItem item = discussVoteItemMapper.selectById(optionId);
        if (item != null) {
            item.setVoteCount(item.getVoteCount() == null ? 1 : item.getVoteCount() + 1);
            discussVoteItemMapper.updateById(item);
        }
        return R.ok();
    }
}
