package com.manga.recommendation.service;

import com.manga.recommendation.vo.ForumAddRequest;
import com.manga.recommendation.vo.ForumDetailVO;
import com.manga.recommendation.vo.ForumPostVO;
import com.manga.recommendation.vo.ForumReplyRequest;
import com.manga.recommendation.vo.PageResponse;

public interface ForumService {

    PageResponse<ForumPostVO> getPostList(Integer current, Integer size, String title, String sortBy, String order);

    ForumDetailVO getPostDetail(Long postId);

    void addPost(Long userId, ForumAddRequest request);

    void addReply(Long userId, ForumReplyRequest request);
}
