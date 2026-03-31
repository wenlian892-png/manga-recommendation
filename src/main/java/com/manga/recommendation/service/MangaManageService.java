package com.manga.recommendation.service;

import com.manga.recommendation.entity.MangaInfo;
import com.manga.recommendation.vo.AdminMangaVO;
import com.manga.recommendation.vo.MangaAddRequest;
import com.manga.recommendation.vo.PageResponse;

public interface MangaManageService {

    PageResponse<AdminMangaVO> getMangaPage(Integer current, Integer size, String title, String author);

    AdminMangaVO getMangaById(Long id);

    void addManga(MangaAddRequest request);

    void updateManga(Long id, MangaAddRequest request);

    void deleteManga(Long id);
}
