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
import java.util.stream.Collectors;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumPostMapper forumPostMapper;

    @Autowired
    private ForumReplyMapper forumReplyMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public PageResponse<ForumPostVO> getPostList(Integer current, Integer size, String title, String sortBy, String order) {
        Page<ForumPost> page = new Page<>(current, size);

        QueryWrapper<ForumPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);

        // 支持按标题搜索
        if (title != null && !title.trim().isEmpty()) {
            queryWrapper.like("title", title.trim());
        }

        // 支持排序：默认按创建时间倒序，sortBy=viewCount 时按浏览量排序
        if ("viewCount".equals(sortBy)) {
            queryWrapper.orderBy(true, !"asc".equals(order), "view_count");
        } else {
            queryWrapper.orderByDesc("create_time");
        }

        Page<ForumPost> resultPage = forumPostMapper.selectPage(page, queryWrapper);

        List<Long> userIds = resultPage.getRecords().stream()
                .map(ForumPost::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> userNameMap = batchGetUserNames(userIds);

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

        // 原子更新浏览量
        UpdateWrapper<ForumPost> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", postId);
        updateWrapper.setSql("view_count = view_count + 1");
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

        List<Long> replyUserIds = replies.stream()
                .map(ForumReply::getUserId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> replyUserNameMap = batchGetUserNames(replyUserIds);

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

    private Map<Long, String> batchGetUserNames(List<Long> userIds) {
        Map<Long, String> userNameMap = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return userNameMap;
        }
        List<UserInfo> users = userInfoMapper.selectBatchIds(userIds);
        for (UserInfo user : users) {
            if (user != null) {
                userNameMap.put(user.getId(), user.getUsername());
            }
        }
        return userNameMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPost(Long userId, ForumAddRequest request) {
        ForumPost post = new ForumPost();
        post.setUserId(userId);
        post.setTitle(escapeHtml(request.getTitle()));
        post.setContent(escapeHtml(request.getContent()));
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
        reply.setContent(escapeHtml(request.getContent()));
        reply.setIsDeleted(0);
        reply.setCreateTime(LocalDateTime.now());
        forumReplyMapper.insert(reply);
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
