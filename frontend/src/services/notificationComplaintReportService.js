import api from './api';

export const notificationService = {
  // Get notifications for current user
  async getNotifications() {
    const res = await api.get('/notifications');
    return res.data;
  },

  // Get unread notification count
  async getUnreadCount() {
    const res = await api.get('/notifications/unread-count');
    return res.data;
  },

  // Mark single notification as read
  async markAsRead(notificationId) {
    const res = await api.post(`/notifications/${notificationId}/read`);
    return res.data;
  },

  // Mark all notifications as read
  async markAllAsRead() {
    const res = await api.post('/notifications/read-all');
    return res.data;
  }
};

export const complaintService = {
  // Submit new service complaint
  async submitComplaint(complaintData) {
    const res = await api.post('/complaints', complaintData);
    return res.data;
  },

  // Get complaints filed by current user
  async getMyComplaints() {
    const res = await api.get('/complaints/my');
    return res.data;
  },

  // Get all society complaints (Society Admin view)
  async getSocietyComplaints(societyId, status) {
    const res = await api.get('/complaints', {
      params: { societyId, status }
    });
    return res.data;
  },

  // Resolve complaint with action notes
  async resolveComplaint(complaintId, resolutionNotes) {
    const res = await api.post(`/complaints/${complaintId}/resolve`, {
      resolutionNotes
    });
    return res.data;
  }
};

export const reportService = {
  // Get executive KPI metrics for society operations dashboard
  async getDashboardKpis(societyId) {
    const res = await api.get('/reports/dashboard-kpis', {
      params: { societyId }
    });
    return res.data;
  },

  // Download monthly billing CSV ledger
  async downloadInvoicesCsv(societyId) {
    const csvContent = await api.get('/reports/invoices/csv', {
      params: { societyId },
      headers: { Accept: 'text/csv' }
    });
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `cookcircle_invoices_society_${societyId || 1}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    return true;
  },

  // Download cook attendance CSV roster
  async downloadAttendanceCsv(societyId) {
    const csvContent = await api.get('/reports/attendance/csv', {
      params: { societyId },
      headers: { Accept: 'text/csv' }
    });
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.setAttribute('href', url);
    link.setAttribute('download', `cookcircle_attendance_society_${societyId || 1}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    return true;
  }
};

export default {
  notificationService,
  complaintService,
  reportService
};

