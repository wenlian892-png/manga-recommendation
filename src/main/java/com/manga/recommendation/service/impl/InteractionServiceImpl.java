package com.manga.recommendation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.manga.recommendation.entity.MangaScore;
import com.manga.recommendation.entity.UserCollection;
import com.manga.recommendation.mapper.MangaScoreMapper;
import com.manga.recommendation.mapper.UserCollectionMapper;
import com.manga.recommendation.service.InteractionService;
import com.manga.recommendation.vo.CollectRequest;
import com.manga.recommendation.vo.ScoreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InteractionServiceImpl implements InteractionService {

    @Autowired
    private MangaScoreMapper mangaScoreMapper;

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void score(Long userId, ScoreRequest request) {
        QueryWrapper<MangaScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("manga_id", request.getMangaId());
        MangaScore existScore = mangaScoreMapper.selectOne(queryWrapper);

        if (existScore != null) {
            UpdateWrapper<MangaScore> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", existScore.getId());
            updateWrapper.set("score", request.getScore());
            updateWrapper.set("create_time", LocalDateTime.now());
            mangaScoreMapper.update(null, updateWrapper);
        } else {
            MangaScore mangaScore = new MangaScore();
            mangaScore.setUserId(userId);
            mangaScore.setMangaId(request.getMangaId());
            mangaScore.setScore(request.getScore());
            mangaScore.setCreateTime(LocalDateTime.now());
            mangaScoreMapper.insert(mangaScore);
        }
    }

    @Override
    public Float getUserScore(Long userId, Long mangaId) {
        QueryWrapper<MangaScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("manga_id", mangaId);
        MangaScore existScore = mangaScoreMapper.selectOne(queryWrapper);
        return existScore != null ? existScore.getScore() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collect(Long userId, CollectRequest request) {
        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("manga_id", request.getMangaId());
        UserCollection exist = userCollectionMapper.selectOne(queryWrapper);

        if (exist != null) {
            return;
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(userId);
        collection.setMangaId(request.getMangaId());
        collection.setCreateTime(LocalDateTime.now());
        userCollectionMapper.insert(collection);
    }

    @Override
    public Boolean isCollected(Long userId, Long mangaId) {
        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("manga_id", mangaId);
        UserCollection exist = userCollectionMapper.selectOne(queryWrapper);
        return exist != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uncollect(Long userId, Long mangaId) {
        QueryWrapper<UserCollection> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("manga_id", mangaId);
        userCollectionMapper.delete(queryWrapper);
    }
}
