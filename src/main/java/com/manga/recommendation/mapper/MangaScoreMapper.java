package com.manga.recommendation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.manga.recommendation.entity.MangaScore;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MangaScoreMapper extends BaseMapper<MangaScore> {

    @Select("SELECT AVG(score) FROM manga_score WHERE manga_id = #{mangaId}")
    Float selectAvgScoreByMangaId(@Param("mangaId") Long mangaId);

    @Select("SELECT manga_id, AVG(score) as avg_score FROM manga_score GROUP BY manga_id")
    List<Map<String, Object>> selectAvgScoreGroupByManga();
}
