package com.manga.recommendation.service;

import com.manga.recommendation.vo.MangaPageRequest;
import com.manga.recommendation.vo.MangaVO;
import com.manga.recommendation.vo.PageResponse;

public interface MangaService {

    PageResponse<MangaVO> getMangaPage(MangaPageRequest request);

    MangaVO getMangaDetail(Long id);

    void incrementClickCount(Long id);
}
