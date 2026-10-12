import request from './request'

export const authApi = {
  login: (username: string, password: string) =>
    request.post('/auth/login', { username, password }),
  me: () => request.get('/auth/me')
}

export const caseApi = {
  list: () => request.get('/cases'),
  detail: (id: number) => request.get(`/cases/${id}`),
  create: (data: any) => request.post('/cases', data),
  update: (id: number, data: any) => request.put(`/cases/${id}`, data)
}

export const taskApi = {
  list: () => request.get('/tasks'),
  detail: (id: number) => request.get(`/tasks/${id}`),
  create: (caseId: number) => request.post('/tasks', { caseId }),
  reject: (id: number, reason: string) => request.post(`/tasks/${id}/reject`, { reason }),
  submit: (id: number) => request.post(`/tasks/${id}/submit`)
}

export const userApi = {
  list: () => request.get('/users'),
  disable: (id: number) => request.delete(`/users/${id}`)
}