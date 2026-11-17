import api from './api';

export const bookingService = {
  // Get all defined slots for a tower
  async getTowerSlots(towerId) {
    const res = await api.get(`/towers/${towerId}/slots`);
    return res.data;
  },

  // Get slot availability for a tower on a specific date and mealType
  async getSlotAvailability(towerId, date, mealType) {
    const res = await api.get(`/towers/${towerId}/slots/availability`, {
      params: { date, mealType }
    });
    return res.data;
  },

  // Initialize standard 10 slots (5 morning + 5 evening) for a tower
  async initializeTowerSlots(towerId) {
    const res = await api.post(`/towers/${towerId}/slots/initialize`);
    return res.data;
  },

  // Book a 45-minute cooking slot (generates 4-digit arrival OTP)
  async bookSlot(slotId, bookingDate, dishesPlanned) {
    const res = await api.post('/bookings', {
      slotId,
      bookingDate,
      dishesPlanned: dishesPlanned || "Dal Tadka, Paneer Bhurji, Phulkas, Steamed Rice"
    });
    return res.data;
  },

  // Get bookings for logged-in resident
  async getMyBookings() {
    const res = await api.get('/bookings/my');
    return res.data;
  },

  // Get cook schedule for logged-in cook on a specific date
  async getCookSchedule(date) {
    const res = await api.get('/bookings/cook-schedule', {
      params: { date }
    });
    return res.data;
  },

  // Get bookings for an entire tower on a specific date (Society Admin view)
  async getTowerBookings(towerId, date) {
    const res = await api.get(`/bookings/tower/${towerId}`, {
      params: { date }
    });
    return res.data;
  },

  // Cook starts session by entering resident's 4-digit arrival OTP
  async startCookingWithOtp(bookingId, otp) {
    const res = await api.post(`/bookings/${bookingId}/start-cooking`, { otp });
    return res.data;
  },

  // Cook marks cooking session complete
  async completeBooking(bookingId) {
    const res = await api.post(`/bookings/${bookingId}/complete`);
    return res.data;
  },

  // Resident 1-click skip meal (credits +₹140 wallet balance)
  async skipMeal(bookingId, reason = 'Resident requested 1-click skip') {
    const res = await api.post(`/bookings/${bookingId}/skip-meal`, null, {
      params: { reason }
    });
    return res.data;
  },

  // Cancel booking
  async cancelBooking(bookingId) {
    const res = await api.post(`/bookings/${bookingId}/cancel`);
    return res.data;
  }
};

export default bookingService;

