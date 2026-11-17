import api from './api';

export const cookService = {
  // Get all cooks, optionally filtered by society
  async getCooks(societyId) {
    const res = await api.get('/cooks', { params: { societyId } });
    return res.data;
  },

  // Get currently logged-in cook profile
  async getMyProfile() {
    const res = await api.get('/cooks/me');
    return res.data;
  },

  // Get cook profile by ID
  async getCookById(cookId) {
    const res = await api.get(`/cooks/${cookId}`);
    return res.data;
  },

  // Register / Add a cook to society
  async addCook(societyId, cookData) {
    const res = await api.post(`/societies/${societyId}/cooks`, cookData);
    return res.data;
  },

  // Update cook profile
  async updateCook(cookId, updateData) {
    const res = await api.put(`/cooks/${cookId}`, updateData);
    return res.data;
  },

  // Assign cook to a tower (PRIMARY or BACKUP_FLOAT)
  async assignCookToTower(cookId, assignmentData) {
    const res = await api.post(`/cooks/${cookId}/assignments`, assignmentData);
    return res.data;
  },

  // Get assignments for a cook
  async getCookAssignments(cookId) {
    const res = await api.get(`/cooks/${cookId}/assignments`);
    return res.data;
  },

  // Get cook assignments for a tower
  async getTowerAssignments(towerId) {
    const res = await api.get(`/towers/${towerId}/cook-assignments`);
    return res.data;
  },

  // Remove / delete a cook assignment
  async removeAssignment(assignmentId) {
    const res = await api.delete(`/cook-assignments/${assignmentId}`);
    return res.data;
  }
};

export default cookService;

