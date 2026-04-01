package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.vo.AdminMangaVO;
import com.manga.recommendation.vo.MangaAddRequest;
import com.manga.recommendation.vo.PageResponse;
import com.manga.recommendation.service.MangaManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MangaManageServiceImpl implements MangaManageService {

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Override
    public PageResponse<AdminMangaVO> getMangaPage(Integer current, Integer size, String title, String author) {
        Page<MangaInfo> page = new Page<>(current, size);

        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();

        if (title != null && !title.trim().isEmpty()) {
            queryWrapper.like("title", title);
        }
        if (author != null && !author.trim().isEmpty()) {
            queryWrapper.like("author", author);
        }

        queryWrapper.orderByDesc("create_time");

        Page<MangaInfo> resultPage = mangaInfoMapper.selectPage(page, queryWrapper);

        java.util.List<AdminMangaVO> voList = new java.util.ArrayList<>();
        for (MangaInfo manga : resultPage.getRecords()) {
            AdminMangaVO vo = new AdminMangaVO();
            BeanUtils.copyProperties(manga, vo);
            voList.add(vo);
        }

        return new PageResponse<>(resultPage.getTotal(), voList);
    }

    @Override
    public AdminMangaVO getMangaById(Long id) {
        MangaInfo manga = mangaInfoMapper.selectById(id);
        if (manga == null) {
            return null;
        }
        AdminMangaVO vo = new AdminMangaVO();
        BeanUtils.copyProperties(manga, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addManga(MangaAddRequest request) {
        MangaInfo manga = new MangaInfo();
        manga.setTitle(request.getTitle());
        manga.setAuthor(request.getAuthor());
        manga.setCoverUrl(request.getCoverUrl());
        manga.setCategory(request.getCategory());
        manga.setStatus(request.getStatus() != null ? request.getStatus() : 0);
        manga.setDescription(request.getDescription());
        manga.setClickCount(0);
        manga.setIsDeleted(0);
        manga.setVersion(1);
        manga.setCreateTime(LocalDateTime.now());
        mangaInfoMapper.insert(manga);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateManga(Long id, MangaAddRequest request) {
        MangaInfo existManga = mangaInfoMapper.selectById(id);
        if (existManga == null) {
            throw new RuntimeException("漫画不存在");
        }

        UpdateWrapper<MangaInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);

        if (request.getTitle() != null) {
            updateWrapper.set("title", request.getTitle());
        }
        if (request.getAuthor() != null) {
            updateWrapper.set("author", request.getAuthor());
        }
        if (request.getCoverUrl() != null) {
            updateWrapper.set("cover_url", request.getCoverUrl());
        }
        if (request.getCategory() != null) {
            updateWrapper.set("category", request.getCategory());
        }
        if (request.getStatus() != null) {
            updateWrapper.set("status", request.getStatus());
        }
        if (request.getDescription() != null) {
            updateWrapper.set("description", request.getDescription());
        }

        mangaInfoMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteManga(Long id) {
        UpdateWrapper<MangaInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("is_deleted", 1);
        mangaInfoMapper.update(null, updateWrapper);
    }
}
