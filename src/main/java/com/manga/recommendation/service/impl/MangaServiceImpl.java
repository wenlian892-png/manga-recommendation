package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.mapper.MangaInfoMapper;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.service.MangaService;
import com.manga.recommendation.vo.MangaPageRequest;
import com.manga.recommendation.vo.MangaVO;
import com.manga.recommendation.vo.PageResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MangaServiceImpl implements MangaService {

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Override
    public PageResponse<MangaVO> getMangaPage(MangaPageRequest request) {
        Page<MangaInfo> page = new Page<>(request.getCurrent(), request.getSize());

        QueryWrapper<MangaInfo> queryWrapper = new QueryWrapper<>();

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            queryWrapper.like("title", request.getTitle());
        }
        if (request.getAuthor() != null && !request.getAuthor().trim().isEmpty()) {
            queryWrapper.like("author", request.getAuthor());
        }
        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            queryWrapper.eq("category", request.getCategory());
        }
        if (request.getStatus() != null) {
            queryWrapper.eq("status", request.getStatus());
        }

        queryWrapper.orderByDesc("create_time");

        Page<MangaInfo> resultPage = mangaInfoMapper.selectPage(page, queryWrapper);

        List<MangaVO> voList = new ArrayList<>();
        for (MangaInfo mangaInfo : resultPage.getRecords()) {
            MangaVO vo = new MangaVO();
            BeanUtils.copyProperties(mangaInfo, vo);
            vo.setAvgScore(getAverageScore(mangaInfo.getId()));
            voList.add(vo);
        }

        return new PageResponse<>(resultPage.getTotal(), voList);
    }

    @Override
    public MangaVO getMangaDetail(Long id) {
        MangaInfo mangaInfo = mangaInfoMapper.selectById(id);
        if (mangaInfo == null || mangaInfo.getIsDeleted() == 1) {
            return null;
        }
        MangaVO vo = new MangaVO();
        BeanUtils.copyProperties(mangaInfo, vo);
        vo.setAvgScore(getAverageScore(id));
        return vo;
    }

    @Override
    @Async
    public void incrementClickCount(Long id) {
        UpdateWrapper<MangaInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("click_count", mangaInfoMapper.selectById(id).getClickCount() + 1);
        mangaInfoMapper.update(null, updateWrapper);
    }

    private Float getAverageScore(Long mangaId) {
        QueryWrapper<MangaScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("manga_id", mangaId);
        List<MangaScore> scoreList = mangaScoreMapper.selectList(queryWrapper);
        if (scoreList == null || scoreList.isEmpty()) {
            return null;
        }
        float sum = 0.0f;
        for (MangaScore score : scoreList) {
            sum += score.getScore();
        }
        return sum / scoreList.size();
    }
}
