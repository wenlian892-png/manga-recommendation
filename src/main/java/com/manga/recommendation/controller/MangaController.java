package com.manga.recommendation.controller;

import com.manga.recommendation.common.Result;
import com.manga.recommendation.service.MangaService;
import com.manga.recommendation.vo.MangaPageRequest;
import com.manga.recommendation.vo.MangaVO;
import com.manga.recommendation.vo.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manga")
public class MangaController {

    @Autowired
    private MangaService mangaService;

    @GetMapping("/page")
    public Result<PageResponse<MangaVO>> getMangaPage(MangaPageRequest request) {
        if (request.getCurrent() == null) {
            request.setCurrent(1);
        }
        if (request.getSize() == null) {
            request.setSize(10);
        }
        PageResponse<MangaVO> pageResponse = mangaService.getMangaPage(request);
        return Result.success(pageResponse);
    }

    @GetMapping("/detail/{id}")
    public Result<MangaVO> getMangaDetail(@PathVariable Long id) {
        mangaService.incrementClickCount(id);
        MangaVO mangaVO = mangaService.getMangaDetail(id);
        if (mangaVO == null) {
            return Result.error(404, "漫画不存在");
        }
        return Result.success(mangaVO);
    }
}
