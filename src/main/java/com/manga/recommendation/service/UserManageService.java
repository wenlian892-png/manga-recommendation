package com.manga.recommendation.service;

import com.manga.recommendation.vo.AdminUserVO;
import com.manga.recommendation.vo.PageResponse;

public interface UserManageService {

    PageResponse<AdminUserVO> getUserPage(Integer current, Integer size, String username);

    void banUser(Long userId);

    void unbanUser(Long userId);
}
