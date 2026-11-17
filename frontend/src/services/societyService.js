import api from './api';

export const societyService = {
  // Get all active societies
  async getSocieties() {
    const res = await api.get('/societies');
    return res.data;
  },

  // Get society by ID with details
  async getSocietyById(societyId) {
    const res = await api.get(`/societies/${societyId}`);
    return res.data;
  },

  // Lookup society by society code (e.g., HIRAN-001)
  async getSocietyByCode(code) {
    const res = await api.get(`/societies/code/${code}`);
    return res.data;
  },

  // Create a new society (Super Admin)
  async createSociety(societyData) {
    const res = await api.post('/societies', societyData);
    return res.data;
  },

  // Update society configuration
  async updateSociety(societyId, societyData) {
    const res = await api.put(`/societies/${societyId}`, societyData);
    return res.data;
  },

  // Get towers for a society
  async getTowersBySociety(societyId) {
    const res = await api.get(`/societies/${societyId}/towers`);
    return res.data;
  },

  // Create tower in a society
  async createTower(societyId, towerData) {
    const res = await api.post(`/societies/${societyId}/towers`, towerData);
    return res.data;
  },

  // Get apartments in a tower
  async getApartmentsByTower(towerId) {
    const res = await api.get(`/towers/${towerId}/apartments`);
    return res.data;
  },

  // Add apartment to a tower
  async createApartment(towerId, apartmentData) {
    const res = await api.post(`/towers/${towerId}/apartments`, apartmentData);
    return res.data;
  }
};

export default societyService;

