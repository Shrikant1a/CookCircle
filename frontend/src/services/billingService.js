import api from './api';

export const billingService = {
  // Generate monthly invoices for all flats in society
  async generateMonthlyInvoices(societyId, billingMonth, dueDate) {
    const res = await api.post('/invoices/generate', { billingMonth, dueDate }, {
      params: { societyId }
    });
    return res.data;
  },

  // Get invoices in society
  async getSocietyInvoices(societyId, status) {
    const res = await api.get('/invoices', {
      params: { societyId, status }
    });
    return res.data;
  },

  // Get invoices for current logged-in resident
  async getMyInvoices() {
    const res = await api.get('/invoices/my');
    return res.data;
  },

  // Get single invoice by ID
  async getInvoiceById(invoiceId) {
    const res = await api.get(`/invoices/${invoiceId}`);
    return res.data;
  },

  // Create Razorpay checkout order
  async createRazorpayOrder(invoiceId) {
    const res = await api.post('/payments/razorpay/create-order', { invoiceId });
    return res.data;
  },

  // Verify Razorpay payment signature
  async verifyRazorpayPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature) {
    const res = await api.post('/payments/razorpay/verify', {
      razorpayOrderId,
      razorpayPaymentId,
      razorpaySignature
    });
    return res.data;
  },

  // Record manual / offline payment
  async recordOfflinePayment(paymentData) {
    const res = await api.post('/payments/offline', paymentData);
    return res.data;
  },

  // Get payment history for an invoice
  async getInvoicePayments(invoiceId) {
    const res = await api.get(`/payments/invoice/${invoiceId}`);
    return res.data;
  }
};

export default billingService;

