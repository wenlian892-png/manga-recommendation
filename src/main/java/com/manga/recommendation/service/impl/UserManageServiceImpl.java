package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manga.recommendation.entity.UserInfo;
import com.manga.recommendation.mapper.UserInfoMapper;
import com.manga.recommendation.service.UserManageService;
import com.manga.recommendation.vo.AdminUserVO;
import com.manga.recommendation.vo.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserManageServiceImpl implements UserManageService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public PageResponse<AdminUserVO> getUserPage(Integer current, Integer size, String username) {
        Page<UserInfo> page = new Page<>(current, size);

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.like("username", username);
        }

        queryWrapper.orderByDesc("create_time");

        Page<UserInfo> resultPage = userInfoMapper.selectPage(page, queryWrapper);

        List<AdminUserVO> voList = new ArrayList<>();
        for (UserInfo user : resultPage.getRecords()) {
            AdminUserVO vo = new AdminUserVO();
            BeanUtils.copyProperties(user, vo);
            voList.add(vo);
        }

        return new PageResponse<>(resultPage.getTotal(), voList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void banUser(Long userId) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        updateWrapper.set("status", 0); // 0=封禁
        userInfoMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbanUser(Long userId) {
        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UpdateWrapper<UserInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userId);
        updateWrapper.set("status", 1); // 1=正常
        userInfoMapper.update(null, updateWrapper);
    }
}
