import api from './api';

export const authService = {
  // Login with email and password
  async login(email, password) {
    const res = await api.post('/auth/login', { email, password });
    if (res?.data?.token) {
      localStorage.setItem('cookcircle_token', res.data.token);
      localStorage.setItem('cookcircle_user', JSON.stringify(res.data.user));
    }
    return res.data;
  },

  // Register a new resident
  async registerResident(residentData) {
    const res = await api.post('/auth/register-resident', residentData);
    if (res?.data?.token) {
      localStorage.setItem('cookcircle_token', res.data.token);
      localStorage.setItem('cookcircle_user', JSON.stringify(res.data.user));
    }
    return res.data;
  },

  // Register a new cook partner
  async registerCook(cookData) {
    const res = await api.post('/auth/register-cook', cookData);
    if (res?.data?.token) {
      localStorage.setItem('cookcircle_token', res.data.token);
      localStorage.setItem('cookcircle_user', JSON.stringify(res.data.user));
    }
    return res.data;
  },

  // Fetch currently authenticated user principal
  async getCurrentUser() {
    const res = await api.get('/auth/me');
    return res.data;
  },

  // Logout and clear credentials
  logout() {
    localStorage.removeItem('cookcircle_token');
    localStorage.removeItem('cookcircle_user');
  },

  // Check if session token exists
  isAuthenticated() {
    return !!localStorage.getItem('cookcircle_token');
  }
};

export default authService;

