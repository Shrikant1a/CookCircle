import api from './api';

export const attendanceLeaveService = {
  // Log attendance for a booking session
  async logAttendance(bookingId, status = 'ON_TIME', remarks = '') {
    const res = await api.post('/attendance/log', { bookingId, status, remarks });
    return res.data;
  },

  // Get attendance history for logged-in cook
  async getMyAttendance() {
    const res = await api.get('/attendance/my');
    return res.data;
  },

  // Get society attendance history (Society Admin view)
  async getSocietyAttendance(societyId, startDate, endDate) {
    const res = await api.get(`/attendance/society/${societyId}`, {
      params: { startDate, endDate }
    });
    return res.data;
  },

  // Submit leave request (Cook view)
  async applyLeave(startDate, endDate, reason) {
    const res = await api.post('/leaves', { startDate, endDate, reason });
    return res.data;
  },

  // Get leave requests for logged-in cook
  async getMyLeaves() {
    const res = await api.get('/leaves/my');
    return res.data;
  },

  // Get all leaves in society (Society Admin view)
  async getSocietyLeaves(societyId, status) {
    const res = await api.get('/leaves', {
      params: { societyId, status }
    });
    return res.data;
  },

  // Approve leave and trigger Standby Float Cook auto-dispatch
  async approveLeave(leaveId, backupCookId, actionNotes) {
    const res = await api.post(`/leaves/${leaveId}/approve`, {
      backupCookId,
      actionNotes: actionNotes || 'Standby Float Cook assigned. Zero meal disruption guaranteed.'
    });
    return res.data;
  },

  // Reject leave request
  async rejectLeave(leaveId, reason) {
    const res = await api.post(`/leaves/${leaveId}/reject`, null, {
      params: { reason }
    });
    return res.data;
  },

  // Get active backup cook assignments
  async getBackupAssignments(societyId) {
    const res = await api.get('/leaves/backup-assignments', {
      params: { societyId }
    });
    return res.data;
  }
};

export default attendanceLeaveService;

