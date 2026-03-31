package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manga.recommendation.entity.ForumPost;
import com.manga.recommendation.entity.ForumReply;
import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.mapper.ForumPostMapper;
import com.manga.recommendation.mapper.ForumReplyMapper;
import com.manga.recommendation.mapper.UserInfoMapper;
import com.manga.recommendation.service.ForumService;
import com.manga.recommendation.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumReplyMapper forumReplyMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public PageResponse<ForumPostVO> getPostList(Integer current, Integer size) {
        Page<ForumPost> page = new Page<>(current, size);

        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByDesc("create_time");

        Page<ForumPost> resultPage = forumPostMapper.selectPage(page, queryWrapper);

        List<Long> userIds = new ArrayList<>();
        for (ForumPost post : resultPage.getRecords()) {
            userIds.add(post.getUserId());
        }

        Map<Long, String> userNameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            for (Long userId : userIds) {
                UserInfo user = userInfoMapper.selectById(userId);
                if (user != null) {
                    userNameMap.put(userId, user.getUsername());
                }
            }
        }

        List<ForumPostVO> voList = new ArrayList<>();
        for (ForumPost post : resultPage.getRecords()) {
            ForumPostVO vo = new ForumPostVO();
            BeanUtils.copyProperties(post, vo);
            vo.setUsername(userNameMap.get(post.getUserId()));
            voList.add(vo);
        }

        return new PageResponse<>(resultPage.getTotal(), voList);
    }

    @Override
    public ForumDetailVO getPostDetail(Long postId) {
        ForumPost post = forumPostMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            return null;
        }

        UpdateWrapper<ForumPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", postId);
        updateWrapper.set("view_count", post.getViewCount() + 1);
        forumPostMapper.update(null, updateWrapper);

        ForumPostVO postVO = new ForumPostVO();
        BeanUtils.copyProperties(post, postVO);

        UserInfo user = userInfoMapper.selectById(post.getUserId());
        if (user != null) {
            postVO.setUsername(user.getUsername());
        }

        QueryWrapper<ForumReply> replyQueryWrapper = new QueryWrapper<>();
        replyQueryWrapper.eq("post_id", postId);
        replyQueryWrapper.eq("is_deleted", 0);
        replyQueryWrapper.orderByAsc("create_time");

        List<ForumReply> replies = forumReplyMapper.selectList(replyQueryWrapper);

        List<Long> replyUserIds = new ArrayList<>();
        for (ForumReply reply : replies) {
            replyUserIds.add(reply.getUserId());
        }

        Map<Long, String> replyUserNameMap = new HashMap<>();
        if (!replyUserIds.isEmpty()) {
            for (Long userId : replyUserIds) {
                UserInfo replyUser = userInfoMapper.selectById(userId);
                if (replyUser != null) {
                    replyUserNameMap.put(userId, replyUser.getUsername());
                }
            }
        }

        List<ForumReplyVO> replyVOList = new ArrayList<>();
        for (ForumReply reply : replies) {
            ForumReplyVO replyVO = new ForumReplyVO();
            BeanUtils.copyProperties(reply, replyVO);
            replyVO.setUsername(replyUserNameMap.get(reply.getUserId()));
            replyVOList.add(replyVO);
        }

        ForumDetailVO detailVO = new ForumDetailVO();
        detailVO.setPost(postVO);
        detailVO.setReplies(replyVOList);

        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPost(Long userId, ForumAddRequest request) {
        ForumPost post = new ForumPost();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setViewCount(0);
        post.setIsDeleted(0);
        post.setCreateTime(LocalDateTime.now());
        forumPostMapper.insert(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addReply(Long userId, ForumReplyRequest request) {
        ForumReply reply = new ForumReply();
        reply.setPostId(request.getPostId());
        reply.setUserId(userId);
        reply.setContent(request.getContent());
        reply.setIsDeleted(0);
        reply.setCreateTime(LocalDateTime.now());
        forumReplyMapper.insert(reply);
    }
}
