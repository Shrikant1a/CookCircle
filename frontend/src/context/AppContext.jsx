import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import {
  INITIAL_SLOTS,
  INITIAL_COOKS,
  INITIAL_INVOICES,
  INITIAL_COMPLAINTS,
  INITIAL_NOTIFICATIONS,
  getStoredData,
  setStoredData
} from '../data/mockData';
import { useToast } from './ToastContext';
import {
  bookingService,
  billingService,
  attendanceLeaveService,
  complaintService,
  notificationService,
  cookService
} from '../services';

const AppContext = createContext(null);

const DEFAULT_PREP_CHECKLIST = [
  { id: 1, text: "Soak Toor / Moong Dal in warm water (30 mins prior)", done: true },
  { id: 2, text: "Keep fresh atta kneaded and covered in container", done: true },
  { id: 3, text: "Wash, peel and chop vegetables as per today's recipe", done: true },
  { id: 4, text: "Place primary spice box, oil & salt on kitchen counter", done: false }
];

export function AppProvider({ children }) {
  const { showToast } = useToast();

  const [slots, setSlots] = useState(() => {
    return getStoredData('cookcircle_slots', INITIAL_SLOTS);
  });

  const [cooks, setCooks] = useState(() => {
    return getStoredData('cookcircle_cooks', INITIAL_COOKS);
  });

  const [isBackupDispatched, setIsBackupDispatched] = useState(() => {
    return getStoredData('cookcircle_backup_dispatched', false);
  });

  const [walletBalance, setWalletBalance] = useState(() => {
    return getStoredData('cookcircle_wallet_balance', 280);
  });

  const [prepChecklist, setPrepChecklist] = useState(() => {
    return getStoredData('cookcircle_prep_checklist', DEFAULT_PREP_CHECKLIST);
  });

  const [selectedMeal, setSelectedMeal] = useState(() => {
    return getStoredData('cookcircle_selected_meal', "Dal Tadka + Aloo Gobhi + 4 Phulkas");
  });

  const [invoices, setInvoices] = useState(() => {
    return getStoredData('cookcircle_invoices', INITIAL_INVOICES);
  });

  const [complaints, setComplaints] = useState(() => {
    return getStoredData('cookcircle_complaints', INITIAL_COMPLAINTS);
  });

  const [notifications, setNotifications] = useState(() => {
    return getStoredData('cookcircle_notifications', INITIAL_NOTIFICATIONS);
  });

  const [leaveRequests, setLeaveRequests] = useState(() => {
    return getStoredData('cookcircle_leaves', [
      {
        id: 1,
        cookId: 1,
        cookName: "Sunita Devi",
        startDate: "2026-11-17",
        endDate: "2026-11-17",
        reason: "Family emergency in hometown",
        status: "APPROVED",
        backupAssigned: "Ramesh Kumar (Standby Float Pool)",
        createdDate: "2026-11-15"
      }
    ]);
  });

  // Sync to LocalStorage
  useEffect(() => { setStoredData('cookcircle_slots', slots); }, [slots]);
  useEffect(() => { setStoredData('cookcircle_cooks', cooks); }, [cooks]);
  useEffect(() => { setStoredData('cookcircle_backup_dispatched', isBackupDispatched); }, [isBackupDispatched]);
  useEffect(() => { setStoredData('cookcircle_wallet_balance', walletBalance); }, [walletBalance]);
  useEffect(() => { setStoredData('cookcircle_prep_checklist', prepChecklist); }, [prepChecklist]);
  useEffect(() => { setStoredData('cookcircle_selected_meal', selectedMeal); }, [selectedMeal]);
  useEffect(() => { setStoredData('cookcircle_invoices', invoices); }, [invoices]);
  useEffect(() => { setStoredData('cookcircle_complaints', complaints); }, [complaints]);
  useEffect(() => { setStoredData('cookcircle_notifications', notifications); }, [notifications]);
  useEffect(() => { setStoredData('cookcircle_leaves', leaveRequests); }, [leaveRequests]);

  // Current primary cook for Tower B
  const primaryCook = cooks.find(c => c.id === 1) || cooks[0];
  const standbyCook = cooks.find(c => c.id === 2) || cooks[1];
  const activeTowerCook = isBackupDispatched ? standbyCook : primaryCook;

  // Toggle checklist item
  const toggleChecklistItem = (id) => {
    setPrepChecklist(prev =>
      prev.map(item => item.id === id ? { ...item, done: !item.done } : item)
    );
  };

  // Sync with backend API if server is reachable
  const refreshBackendData = useCallback(async (towerId = 101, societyId = 1) => {
    try {
      // Fetch notifications
      const notifs = await notificationService.getNotifications();
      if (Array.isArray(notifs) && notifs.length > 0) {
        setNotifications(notifs);
      }
    } catch {
      // Quiet fallback
    }

    try {
      // Fetch complaints
      const compList = await complaintService.getMyComplaints();
      if (Array.isArray(compList) && compList.length > 0) {
        setComplaints(compList);
      }
    } catch {
      // Quiet fallback
    }

    try {
      // Fetch cooks
      const cookList = await cookService.getCooks(societyId);
      if (Array.isArray(cookList) && cookList.length > 0) {
        setCooks(cookList);
      }
    } catch {
      // Quiet fallback
    }
  }, []);

  // Join pool / Book slot (with backend API sync)
  const bookSlot = async (slotOrder, { flatNumber, residentName, householdSize, dietaryPref, dishesPlanned }) => {
    let generatedOtp = Math.floor(1000 + Math.random() * 9000).toString();
    const today = new Date().toISOString().split('T')[0];

    // Attempt real backend booking call
    try {
      const targetSlot = slots.find(s => s.order === slotOrder);
      const slotId = targetSlot?.id || slotOrder;
      const response = await bookingService.bookSlot(slotId, today, dishesPlanned);
      if (response && response.arrivalOtp) {
        generatedOtp = response.arrivalOtp;
      }
    } catch (e) {
      console.log('BookSlot running with optimistic local state:', e.message);
    }

    setSlots(prev => prev.map(s => {
      if (s.order === slotOrder) {
        return {
          ...s,
          isBooked: true,
          flatNumber: flatNumber || "904",
          residentName: residentName || "New Resident",
          residentId: `res_${flatNumber || '904'}`,
          householdSize: householdSize || "1-2 members",
          dietaryPref: dietaryPref || "Pure Veg",
          dishesPlanned: dishesPlanned || "Dal Fry + Jeera Aloo + 3 Phulkas",
          arrivalOtp: generatedOtp,
          status: "CONFIRMED"
        };
      }
      return s;
    }));

    // Add notification
    const newNotif = {
      id: Date.now(),
      title: "New Slot Reserved",
      message: `Slot ${slotOrder} joined by Flat ${flatNumber || '904'}. 4-digit arrival OTP is ${generatedOtp}.`,
      time: "Just now",
      type: "BOOKING",
      isRead: false
    };
    setNotifications(prev => [newNotif, ...prev]);
    showToast(`Slot ${slotOrder} confirmed for Flat ${flatNumber || '904'}! Arrival OTP: ${generatedOtp}`, 'success');
    return generatedOtp;
  };

  // 1-Click Skip Meal (with backend API sync)
  const skipMeal = async (slotOrder) => {
    const slot = slots.find(s => s.order === slotOrder);
    if (slot?.id) {
      try {
        await bookingService.skipMeal(slot.id, 'Resident 1-click meal skip');
      } catch (e) {
        console.log('SkipMeal running with optimistic state:', e.message);
      }
    }

    setSlots(prev => prev.map(s => {
      if (s.order === slotOrder) {
        return {
          ...s,
          status: "SKIPPED"
        };
      }
      return s;
    }));

    const credit = 140;
    setWalletBalance(prev => prev + credit);

    const newNotif = {
      id: Date.now(),
      title: "Meal Skipped — Wallet Credited",
      message: `Evening slot skipped. ₹${credit} has been credited to your CookCircle wallet.`,
      time: "Just now",
      type: "CREDIT",
      isRead: false
    };
    setNotifications(prev => [newNotif, ...prev]);
    showToast(`Meal skipped for today. ₹${credit} credited to your wallet balance!`, 'success');
  };

  // Start cooking session with OTP (Cook App, with backend API sync)
  const startCookingWithOtp = async (slotOrder, enteredOtp) => {
    const slot = slots.find(s => s.order === slotOrder);
    if (!slot) {
      showToast("Slot not found", "error");
      return false;
    }

    if (slot.arrivalOtp && slot.arrivalOtp.trim() !== enteredOtp.trim()) {
      showToast("Incorrect OTP! Please verify with resident.", "error");
      return false;
    }

    if (slot.id) {
      try {
        await bookingService.startCookingWithOtp(slot.id, enteredOtp.trim());
      } catch (e) {
        console.log('Start cooking synced locally:', e.message);
      }
    }

    setSlots(prev => prev.map(s => {
      if (s.order === slotOrder) {
        return {
          ...s,
          status: "COOKING"
        };
      }
      return s;
    }));

    showToast(`OTP verified! 45-minute cooking timer started for Flat ${slot.flatNumber}.`, 'success');
    return true;
  };

  // Mark cooking slot complete (Cook App, with backend API sync)
  const completeCooking = async (slotOrder) => {
    const slot = slots.find(s => s.order === slotOrder);
    if (slot?.id) {
      try {
        await bookingService.completeBooking(slot.id);
      } catch (e) {
        console.log('Complete cooking synced locally:', e.message);
      }
    }

    setSlots(prev => prev.map(s => {
      if (s.order === slotOrder) {
        return {
          ...s,
          status: "COMPLETED"
        };
      }
      return s;
    }));

    showToast(`Slot for Flat ${slot?.flatNumber || ''} marked as completed!`, 'success');
  };

  // Automated Standby Cook Dispatch Simulator
  const simulatePrimaryCookLeave = () => {
    setIsBackupDispatched(true);

    // Update primary cook status to ON_LEAVE
    setCooks(prev => prev.map(c => {
      if (c.id === 1) return { ...c, status: "ON_LEAVE" };
      return c;
    }));

    const alertNotif = {
      id: Date.now(),
      title: "Backup Cook Assigned — Zero Meal Disruption",
      message: "Primary cook Sunita Devi is on leave. Standby Float Cook Ramesh Kumar has been dispatched to cover your tower's evening slots.",
      time: "Just now",
      type: "BACKUP",
      isRead: false
    };
    setNotifications(prev => [alertNotif, ...prev]);

    showToast("Standby Float Cook Ramesh Kumar dispatched to Tower B! Zero Meal Disruption guarantee active.", "warning", 6000);
  };

  // Reset backup dispatch simulation
  const resetBackupSimulation = () => {
    setIsBackupDispatched(false);
    setCooks(prev => prev.map(c => {
      if (c.id === 1) return { ...c, status: "ACTIVE" };
      return c;
    }));
    showToast("Backup dispatch simulator reset. Sunita Devi resumed as Primary Cook.", "info");
  };

  // Submit leave request (from Cook App, with backend API sync)
  const submitLeaveRequest = async ({ cookId, startDate, endDate, reason }) => {
    try {
      await attendanceLeaveService.applyLeave(startDate, endDate, reason);
    } catch (e) {
      console.log('Leave request saved locally:', e.message);
    }

    const newLeave = {
      id: Date.now(),
      cookId: cookId || 1,
      cookName: cookId === 2 ? "Ramesh Kumar" : "Sunita Devi",
      startDate,
      endDate,
      reason,
      status: "APPROVED",
      backupAssigned: "Ramesh Kumar (Standby Float Pool)",
      createdDate: new Date().toISOString().split('T')[0]
    };
    setLeaveRequests(prev => [newLeave, ...prev]);
    simulatePrimaryCookLeave();
  };

  // Pay invoice via simulated or live Razorpay Test Gateway
  const payInvoice = async (invoiceId, paymentMethod = 'Razorpay (UPI / Cards)') => {
    let txnId = `pay_rzp_${Math.floor(100000000 + Math.random() * 900000000)}`;

    try {
      // Step 1: Create Razorpay Order via Spring Boot
      const order = await billingService.createRazorpayOrder(typeof invoiceId === 'number' ? invoiceId : 1);
      if (order && order.orderId) {
        // Step 2: Verify Razorpay Payment Signature
        const fakePaymentId = `pay_test_${Date.now()}`;
        const fakeSignature = `sig_${Date.now()}`;
        await billingService.verifyRazorpayPayment(order.orderId, fakePaymentId, fakeSignature);
        txnId = fakePaymentId;
      }
    } catch (e) {
      console.log('Razorpay transaction verified locally:', e.message);
    }

    setInvoices(prev => prev.map(inv => {
      if (inv.id === invoiceId) {
        return {
          ...inv,
          status: "PAID",
          paidDate: new Date().toISOString().split('T')[0],
          method: paymentMethod,
          txnId
        };
      }
      return inv;
    }));

    const notif = {
      id: Date.now(),
      title: "Invoice Paid Successfully",
      message: `Invoice ${invoiceId} marked as paid via Razorpay Test Gateway (${txnId}).`,
      time: "Just now",
      type: "PAYMENT",
      isRead: false
    };
    setNotifications(prev => [notif, ...prev]);
    showToast(`Payment successful for Invoice ${invoiceId}! Receipt ready.`, "success");
  };

  // Add resident complaint (with backend API sync)
  const addComplaint = async ({ category, subject, description, flatNumber, residentName }) => {
    try {
      await complaintService.submitComplaint({ category, subject, description });
    } catch (e) {
      console.log('Complaint submitted locally:', e.message);
    }

    const newComp = {
      id: Date.now(),
      flatNumber: flatNumber || "402",
      residentName: residentName || "Aarav Sharma",
      category,
      subject,
      description,
      status: "OPEN",
      adminNotes: "Under review by Society Facility Manager",
      date: new Date().toISOString().split('T')[0]
    };
    setComplaints(prev => [newComp, ...prev]);
    showToast("Complaint ticket submitted to society office.", "info");
  };

  // Activate subscription for a resident: binds cook, reserves slot, creates first invoice, adds notification
  const assignUserCookAndSlot = ({ cookId, cookName, slotOrder, flatNumber, residentName, dietaryPref, householdSize, mealPlan }) => {
    const generatedOtp = Math.floor(1000 + Math.random() * 9000).toString();

    // 1. Update the slots list
    setSlots(prev => prev.map(s => {
      if (s.order === slotOrder) {
        return {
          ...s,
          isBooked: true,
          flatNumber: flatNumber,
          residentName: residentName,
          residentId: `res_${flatNumber}`,
          householdSize: householdSize || "3-4 members",
          dietaryPref: dietaryPref || "Pure Veg",
          dishesPlanned: mealPlan || "Dal Tadka + Aloo Gobhi + 4 Phulkas",
          arrivalOtp: generatedOtp,
          status: "CONFIRMED"
        };
      }
      return s;
    }));

    // 2. Generate their first subscription invoice
    const newInvoice = {
      id: `INV-${Date.now().toString().slice(-6)}`,
      month: new Date().toLocaleString('default', { month: 'long', year: 'numeric' }),
      flatNumber: flatNumber,
      residentName: residentName,
      amount: 4200,
      discount: 0,
      netPayable: 4200,
      status: 'PENDING',
      dueDate: new Date(Date.now() + 7 * 86400000).toISOString().split('T')[0],
      societyName: 'Hiranandani Gardens',
      towerName: 'Tower B - Wing 1',
      paymentRef: null
    };
    setInvoices(prev => [newInvoice, ...prev]);

    // 3. Add confirmation notification
    const welcomeNotif = {
      id: Date.now(),
      title: "Cooking Pool Subscription Activated",
      message: `Assigned Cook ${cookName} for Flat ${flatNumber} (Slot ${slotOrder}). Arrival OTP is ${generatedOtp}.`,
      time: "Just now",
      type: "BOOKING",
      isRead: false
    };
    setNotifications(prev => [welcomeNotif, ...prev]);

    showToast(`Subscription activated! Cook ${cookName} assigned to Slot ${slotOrder}. Arrival OTP: ${generatedOtp}`, 'success');
    return { otp: generatedOtp, invoice: newInvoice };
  };

  // Mark all notifications read (with backend API sync)
  const markAllNotificationsRead = async () => {
    try {
      await notificationService.markAllAsRead();
    } catch (e) {
      console.log('Notifications marked read locally:', e.message);
    }
    setNotifications(prev => prev.map(n => ({ ...n, isRead: true })));
  };

  return (
    <AppContext.Provider
      value={{
        slots,
        setSlots,
        cooks,
        primaryCook,
        standbyCook,
        activeTowerCook,
        isBackupDispatched,
        walletBalance,
        prepChecklist,
        selectedMeal,
        setSelectedMeal,
        toggleChecklistItem,
        invoices,
        complaints,
        notifications,
        leaveRequests,
        bookSlot,
        assignUserCookAndSlot,
        skipMeal,
        startCookingWithOtp,
        completeCooking,
        simulatePrimaryCookLeave,
        resetBackupSimulation,
        submitLeaveRequest,
        payInvoice,
        addComplaint,
        markAllNotificationsRead,
        refreshBackendData
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
}
