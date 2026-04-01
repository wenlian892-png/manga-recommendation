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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MangaServiceImpl implements MangaService {

    @Autowired
    private MangaInfoMapper mangaInfoMapper;

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Autowired
    private MangaScoreHelper mangaScoreHelper;

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

        // 批量获取平均评分，避免N+1查询
        Set<Long> mangaIds = resultPage.getRecords().stream()
                .map(MangaInfo::getId)
                .collect(Collectors.toSet());
        Map<Long, Float> avgScoreMap = getAvgScoreMap(mangaIds);

        List<MangaVO> voList = new ArrayList<>();
        for (MangaInfo mangaInfo : resultPage.getRecords()) {
            MangaVO vo = new MangaVO();
            BeanUtils.copyProperties(mangaInfo, vo);
            vo.setAvgScore(avgScoreMap.getOrDefault(mangaInfo.getId(), 0.0f));
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
        vo.setAvgScore(mangaScoreHelper.getAverageScore(id));
        return vo;
    }

    @Override
    public void incrementClickCount(Long id) {
        UpdateWrapper<MangaInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.setSql("click_count = click_count + 1");
        mangaInfoMapper.update(null, updateWrapper);
    }

    private Map<Long, Float> getAvgScoreMap(Set<Long> mangaIds) {
        List<Map<String, Object>> avgScoreList = mangaScoreMapper.selectAvgScoreGroupByManga();
        Map<Long, Float> map = new HashMap<>();
        for (Map<String, Object> row : avgScoreList) {
            Long mangaId = ((Number) row.get("manga_id")).longValue();
            if (mangaIds.contains(mangaId)) {
                Float avgScore = ((Number) row.get("avg_score")).floatValue();
                map.put(mangaId, avgScore);
            }
        }
        return map;
    }
}
