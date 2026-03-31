import request from '@/utils/request'

export const mangaApi = {
  getMangaPage: (params) => request.get('/manga/page', { params }),
  getMangaById: (id) => request.get(`/manga/${id}`),
  getMangaChapters: (mangaId) => request.get(`/manga/${mangaId}/chapters`)
}
