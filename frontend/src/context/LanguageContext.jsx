import React, { createContext, useContext, useState, useEffect } from 'react';

const LanguageContext = createContext(null);

const TRANSLATIONS = {
  en: {
    // Cook Partner Translations
    cookPartnerApp: "Cook Partner App",
    onDuty: "On Duty",
    offDuty: "Off Duty",
    todaySchedule: "Today's Tower Route",
    eveningShift: "Evening Shift (6:30 PM – 10:15 PM)",
    morningShift: "Morning Shift (7:00 AM – 10:00 AM)",
    startCooking: "Start Cooking (Enter OTP)",
    completeCooking: "Mark Slot Completed",
    enterOtpPrompt: "Enter 4-Digit Resident OTP",
    otpDescription: "Ask flat resident for their arrival code to begin the 45-minute cooking timer.",
    verifyAndStart: "Verify & Start Timer",
    cancel: "Cancel",
    cookingInProgress: "Cooking in Progress",
    timeRemaining: "Remaining",
    mins: "mins",
    earningsTitle: "This Month's Earnings",
    zeroLeaveBonus: "Zero-Leave Attendance Bonus",
    bonusEligible: "You are on track for +₹1,000 monthly bonus!",
    requestLeave: "Request Leave",
    emergencySupport: "Emergency Help",
    dishesToCook: "Dishes to Cook",
    householdMembers: "Members",
    statusCompleted: "Completed",
    statusInProgress: "In Progress",
    statusConfirmed: "Upcoming",
    statusSkipped: "Resident Skipped",
    leaveModalTitle: "Apply for Leave",
    startDate: "Start Date",
    endDate: "End Date",
    reason: "Reason for Leave",
    submitLeaveRequest: "Submit Leave Request",
    backupCookNotice: "Standby float cook will be automatically dispatched to cover your tower slots upon approval.",
    // Resident Translations
    residentApp: "Resident App",
    society: "Society",
    tower: "Tower",
    flat: "Flat",
    liveCookPool: "Live Tower Cook Pool",
    poolTagline: "Ghar ka khana, shared with your society. Save 70% on daily home cooks.",
    savePercentage: "Save 72% vs Private Cook",
    perMonth: "/month",
    assignedCook: "Assigned Cook Partner",
    verifiedBadges: "Aadhaar & Police Verified",
    arrivalOtpLabel: "Cook Arrival OTP",
    shareOnArrival: "Share with cook when they arrive at your door",
    skipMealButton: "1-Click Skip Meal / +₹140 Wallet Credit",
    skipMealSuccess: "Meal skipped. ₹140 credited to your wallet.",
    prepChecklistTitle: "60-Min Pre-Cook Prep Checklist",
    prepChecklistSubtitle: "Ensure smooth 45-min cooking by prepping essentials before cook arrives",
    dishSelectorLabel: "Today's Selected Meal",
    joinPoolButton: "Join Tower Pool",
    // Society Admin Translations
    adminDashboard: "Society Admin Dashboard",
    activePools: "Active Tower Pools",
    mrr: "Monthly Recurring Revenue",
    cookUtilization: "Cook Utilization Rate",
    slaOnTime: "SLA On-Time %",
    simulateLeave: "Simulate Primary Cook Leave",
    simulateLeaveDesc: "Demonstrate automated standby dispatch with zero meal disruption",
    backupDispatchedBadge: "Backup Cook Assigned — Zero Meal Disruption",
    resetSimulation: "Reset Simulation"
  },
  hi: {
    // Cook Partner Translations in Hindi
    cookPartnerApp: "रसोई पार्टनर ऐप",
    onDuty: "कार्यरत (ऑन ड्यूटी)",
    offDuty: "ऑफ ड्यूटी",
    todaySchedule: "आज का टावर मार्ग",
    eveningShift: "शाम की शिफ्ट (6:30 PM – 10:15 PM)",
    morningShift: "सुबह की शिफ्ट (7:00 AM – 10:00 AM)",
    startCooking: "खाना बनाना शुरू करें (OTP दर्ज करें)",
    completeCooking: "स्लॉट पूरा मार्क करें",
    enterOtpPrompt: "निवासी का 4-अंकीय OTP दर्ज करें",
    otpDescription: "45-मिनट का कुकिंग टाइमर शुरू करने के लिए फ्लैट निवासी से OTP प्राप्त करें।",
    verifyAndStart: "सत्यापित करें और शुरू करें",
    cancel: "रद्द करें",
    cookingInProgress: "खाना तैयार हो रहा है",
    timeRemaining: "शेष समय",
    mins: "मिनट",
    earningsTitle: "इस महीने की कुल कमाई",
    zeroLeaveBonus: "शून्य-अवकाश उपस्थिति बोनस",
    bonusEligible: "आप +₹1,000 मासिक बोनस प्राप्त करने के पात्र हैं!",
    requestLeave: "छुट्टी का आवेदन करें",
    emergencySupport: "आपातकालीन सहायता",
    dishesToCook: "बनाए जाने वाले व्यंजन",
    householdMembers: "सदस्य",
    statusCompleted: "संपन्न",
    statusInProgress: "जारी है",
    statusConfirmed: "आगामी",
    statusSkipped: "निवासी द्वारा रद्द (स्किप)",
    leaveModalTitle: "छुट्टी के लिए आवेदन करें",
    startDate: "प्रारंभ तिथि",
    endDate: "अंतिम तिथि",
    reason: "छुट्टी का कारण",
    submitLeaveRequest: "छुट्टी का अनुरोध भेजें",
    backupCookNotice: "अनुमोदन के बाद आपके टावर स्लॉट को संभालने के लिए स्टैंडबाय फ्लोट कुक को भेजा जाएगा।",
    // Resident Translations
    residentApp: "निवासी ऐप (कस्टमर अनुभव)",
    society: "सोसायटी",
    tower: "टावर",
    flat: "फ्लैट",
    liveCookPool: "लाइव टावर कुक पूल",
    poolTagline: "घर का ताजा खाना, आपकी अपनी सोसायटी में। घरेलू कुक पर 70% तक बचत।",
    savePercentage: "प्राइवेट कुक की तुलना में 72% बचत",
    perMonth: "/माह",
    assignedCook: "आवंटित कुक पार्टनर",
    verifiedBadges: "आधार और पुलिस सत्यापित",
    arrivalOtpLabel: "कुक आगमन OTP",
    shareOnArrival: "कुक के दरवाजे पर आने पर यह कोड साझा करें",
    skipMealButton: "1-क्लिक मील स्किप / +₹140 वॉलेट क्रेडिट",
    skipMealSuccess: "भोजन स्किप किया गया। ₹140 आपके वॉलेट में जोड़ दिए गए हैं।",
    prepChecklistTitle: "60-मिनट पूर्व-कुकिंग तैयारी सूची",
    prepChecklistSubtitle: "कुक के आने से पहले आवश्यक सामग्री तैयार रखें",
    dishSelectorLabel: "आज का चुना गया भोजन",
    joinPoolButton: "टावर पूल में शामिल हों",
    // Society Admin Translations
    adminDashboard: "सोसायटी व्यवस्थापक डैशबोर्ड",
    activePools: "सक्रिय टावर पूल",
    mrr: "मासिक आवर्ती राजस्व (MRR)",
    cookUtilization: "कुक उपयोग दर",
    slaOnTime: "समय पर आगमन SLA",
    simulateLeave: "प्राइमरी कुक छुट्टी का अनुकरण करें",
    simulateLeaveDesc: "शून्य भोजन व्यवधान के साथ स्वचालित बैकअप डिस्पैच प्रदर्शित करें",
    backupDispatchedBadge: "बैकअप कुक तैनात — शून्य भोजन व्यवधान",
    resetSimulation: "सिमुलेशन रीसेट करें"
  }
};

export function LanguageProvider({ children }) {
  const [language, setLanguage] = useState(() => {
    return localStorage.getItem('cookcircle_lang') || 'en';
  });

  useEffect(() => {
    localStorage.setItem('cookcircle_lang', language);
  }, [language]);

  const toggleLanguage = () => {
    setLanguage((prev) => (prev === 'en' ? 'hi' : 'en'));
  };

  const t = (key) => {
    return TRANSLATIONS[language]?.[key] || TRANSLATIONS['en']?.[key] || key;
  };

  return (
    <LanguageContext.Provider value={{ language, setLanguage, toggleLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
}

