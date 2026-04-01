import request from '@/utils/request'

export const recommendApi = {
  getRecommendHub: (params) => request.get('/recommend/hub', { params })
}
