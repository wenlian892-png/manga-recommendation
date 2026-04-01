import request from '@/utils/request'

export const interactionApi = {
  addScore: (data) => request.post('/interaction/score', data),
  addCollection: (data) => request.post('/interaction/collect', data),
  addComment: (data) => request.post('/interaction/comment', data),
  getComments: (mangaId) => request.get(`/interaction/comments/${mangaId}`),
  getCollections: (userId) => request.get(`/interaction/collections/${userId}`),
  getScores: (userId) => request.get(`/interaction/scores/${userId}`)
}
