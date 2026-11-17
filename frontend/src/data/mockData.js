// CookCircle Initial Mock Data & LocalStorage State Manager
// Realistic Indian Housing Societies, Towers, Flats, Cooks, and Schedules

const STORAGE_KEYS = {
  SOCIETIES: 'cookcircle_societies',
  ACTIVE_SOCIETY_ID: 'cookcircle_active_society_id',
  ACTIVE_ROLE: 'cookcircle_active_role',
  CURRENT_USER: 'cookcircle_current_user',
  COOKS: 'cookcircle_cooks',
  SLOTS: 'cookcircle_slots',
  BOOKINGS: 'cookcircle_bookings',
  LEAVES: 'cookcircle_leaves',
  BACKUP_DISPATCH: 'cookcircle_backup_dispatch',
  INVOICES: 'cookcircle_invoices',
  COMPLAINTS: 'cookcircle_complaints',
  NOTIFICATIONS: 'cookcircle_notifications',
  WALLET_BALANCE: 'cookcircle_wallet_balance',
  LANGUAGE: 'cookcircle_language'
};

export const INITIAL_SOCIETIES = [
  {
    id: 1,
    name: "Hiranandani Gardens",
    code: "HG-POWAI",
    city: "Mumbai",
    state: "Maharashtra",
    area: "Powai",
    towers: [
      { id: 101, name: "Tower B - Wing 1", floors: 22, activePools: 3 },
      { id: 102, name: "Tower B - Wing 2", floors: 22, activePools: 2 },
      { id: 103, name: "Tower C - Heritage", floors: 18, activePools: 2 }
    ],
    basePoolingRate: 4200,
    soloRate: 15000,
    skipMealCredit: 140
  },
  {
    id: 2,
    name: "Prestige Shantiniketan",
    code: "PSN-WFD",
    city: "Bengaluru",
    state: "Karnataka",
    area: "Whitefield",
    towers: [
      { id: 201, name: "Tower 4 - Crescent", floors: 19, activePools: 4 },
      { id: 202, name: "Tower 7 - Boulevard", floors: 24, activePools: 3 }
    ],
    basePoolingRate: 4500,
    soloRate: 16500,
    skipMealCredit: 150
  },
  {
    id: 3,
    name: "Sobha Dream Acres",
    code: "SDA-PANATUR",
    city: "Bengaluru",
    state: "Karnataka",
    area: "Panathur",
    towers: [
      { id: 301, name: "Wing C - RainTree", floors: 14, activePools: 3 },
      { id: 302, name: "Wing F - PalmBreeze", floors: 14, activePools: 2 }
    ],
    basePoolingRate: 3999,
    soloRate: 14000,
    skipMealCredit: 135
  },
  {
    id: 4,
    name: "Amanora Park Town",
    code: "APT-HADAPSAR",
    city: "Pune",
    state: "Maharashtra",
    area: "Hadapsar",
    towers: [
      { id: 401, name: "Tower Aspen - Wing A", floors: 26, activePools: 4 },
      { id: 402, name: "Tower Gateway - Wing 1", floors: 30, activePools: 3 },
      { id: 403, name: "Tower Sweet Water - Wing B", floors: 22, activePools: 2 }
    ],
    basePoolingRate: 4200,
    soloRate: 14500,
    skipMealCredit: 140
  },
  {
    id: 5,
    name: "Blue Ridge",
    code: "BR-HINJEWADI",
    city: "Pune",
    state: "Maharashtra",
    area: "Hinjewadi Phase 1",
    towers: [
      { id: 501, name: "Tower 12 - Pine Crest", floors: 24, activePools: 3 },
      { id: 502, name: "Tower 15 - Riverview", floors: 25, activePools: 3 }
    ],
    basePoolingRate: 4100,
    soloRate: 14000,
    skipMealCredit: 135
  }
];

export const INITIAL_COOKS = [
  {
    id: 1,
    name: "Sunita Devi",
    nameHi: "सुनीता देवी",
    photo: "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=250",
    rating: 4.9,
    reviewsCount: 142,
    type: "PRIMARY_COOK",
    assignedTowerId: 101,
    assignedTowerName: "Tower B - Wing 1",
    specialties: ["North Indian", "Gujarati", "Jain", "Maharashtrian"],
    specialtiesHi: ["उत्तर भारतीय", "गुजराती", "जैन", "महाराष्ट्रीयन"],
    experienceYears: 6,
    aadhaarVerified: true,
    policeVerified: true,
    vaccinationStatus: "Double Vaccinated (Covishield)",
    vaccinationStatusHi: "पूर्ण टीकाकृत (कोविशील्ड)",
    phone: "+91 98201 44521",
    monthlyEarnings: 19500,
    zeroLeaveBonus: 1000,
    status: "ACTIVE" // or 'ON_LEAVE'
  },
  {
    id: 2,
    name: "Ramesh Kumar",
    nameHi: "रमेश कुमार",
    photo: "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&q=80&w=250",
    rating: 4.85,
    reviewsCount: 98,
    type: "BACKUP_FLOAT_COOK",
    assignedTowerId: null,
    assignedTowerName: "Standby Rapid Pool (Society Wide)",
    specialties: ["North Indian", "South Indian", "Dal Baati", "Punjabi"],
    specialtiesHi: ["उत्तर भारतीय", "दक्षिण भारतीय", "दाल बाटी", "पंजाबी"],
    experienceYears: 8,
    aadhaarVerified: true,
    policeVerified: true,
    vaccinationStatus: "Double Vaccinated (Covaxin)",
    vaccinationStatusHi: "पूर्ण टीकाकृत (कोवैक्सिन)",
    phone: "+91 98192 88390",
    monthlyEarnings: 21000,
    zeroLeaveBonus: 1000,
    status: "ACTIVE"
  },
  {
    id: 3,
    name: "Meena Bai",
    nameHi: "मीना बाई",
    photo: "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=250",
    rating: 4.92,
    reviewsCount: 110,
    type: "PRIMARY_COOK",
    assignedTowerId: 102,
    assignedTowerName: "Tower B - Wing 2",
    specialties: ["Kathiyawadi", "Marwari", "Pure Veg"],
    specialtiesHi: ["काठियावाड़ी", "मारवाड़ी", "शुद्ध शाकाहारी"],
    experienceYears: 7,
    aadhaarVerified: true,
    policeVerified: true,
    vaccinationStatus: "Double Vaccinated",
    vaccinationStatusHi: "पूर्ण टीकाकृत",
    phone: "+91 98450 11928",
    monthlyEarnings: 18800,
    zeroLeaveBonus: 1000,
    status: "ACTIVE"
  },
  {
    id: 4,
    name: "Kavita Patil",
    nameHi: "कविता पाटिल",
    photo: "https://images.unsplash.com/photo-1567532939604-b6b5b0db2604?auto=format&fit=crop&q=80&w=250",
    rating: 4.95,
    reviewsCount: 88,
    type: "PRIMARY_COOK",
    assignedTowerId: 401,
    assignedTowerName: "Tower Aspen - Wing A (Amanora, Pune)",
    specialties: ["Maharashtrian", "North Indian", "Puran Poli", "Jain"],
    specialtiesHi: ["महाराष्ट्रीयन", "उत्तर भारतीय", "पूरन पोली", "जैन"],
    experienceYears: 9,
    aadhaarVerified: true,
    policeVerified: true,
    vaccinationStatus: "Double Vaccinated",
    vaccinationStatusHi: "पूर्ण टीकाकृत",
    phone: "+91 98230 55112",
    monthlyEarnings: 20500,
    zeroLeaveBonus: 1000,
    status: "ACTIVE"
  }
];

export const INITIAL_SLOTS = [
  {
    id: 1,
    towerId: 101,
    shift: "EVENING",
    order: 1,
    timeDisplay: "6:30 PM – 7:15 PM",
    startTime: "18:30",
    endTime: "19:15",
    isBooked: true,
    flatNumber: "402",
    residentName: "Aarav Sharma",
    residentId: "res_402",
    dishesPlanned: "Dal Tadka + Aloo Gobhi + 4 Phulkas",
    householdSize: "3-4 members",
    dietaryPref: "Pure Veg",
    arrivalOtp: "4829",
    status: "IN_PROGRESS", // UPCOMING, COOKING, COMPLETED, SKIPPED
    cookId: 1
  },
  {
    id: 2,
    towerId: 101,
    shift: "EVENING",
    order: 2,
    timeDisplay: "7:15 PM – 8:00 PM",
    startTime: "19:15",
    endTime: "20:00",
    isBooked: true,
    flatNumber: "701",
    residentName: "Pooja Mehta",
    residentId: "res_701",
    dishesPlanned: "Paneer Bhurji + Jeera Rice + 3 Phulkas",
    householdSize: "1-2 members",
    dietaryPref: "Jain",
    arrivalOtp: "7134",
    status: "CONFIRMED",
    cookId: 1
  },
  {
    id: 3,
    towerId: 101,
    shift: "EVENING",
    order: 3,
    timeDisplay: "8:00 PM – 8:45 PM",
    startTime: "20:00",
    endTime: "20:45",
    isBooked: false,
    flatNumber: null,
    residentName: null,
    residentId: null,
    dishesPlanned: null,
    householdSize: null,
    dietaryPref: null,
    arrivalOtp: null,
    status: "AVAILABLE",
    cookId: 1
  },
  {
    id: 4,
    towerId: 101,
    shift: "EVENING",
    order: 4,
    timeDisplay: "8:45 PM – 9:30 PM",
    startTime: "20:45",
    endTime: "21:30",
    isBooked: false,
    flatNumber: null,
    residentName: null,
    residentId: null,
    dishesPlanned: null,
    householdSize: null,
    dietaryPref: null,
    arrivalOtp: null,
    status: "AVAILABLE",
    cookId: 1
  },
  {
    id: 5,
    towerId: 101,
    shift: "EVENING",
    order: 5,
    timeDisplay: "9:30 PM – 10:15 PM",
    startTime: "21:30",
    endTime: "22:15",
    isBooked: false,
    flatNumber: null,
    residentName: null,
    residentId: null,
    dishesPlanned: null,
    householdSize: null,
    dietaryPref: null,
    arrivalOtp: null,
    status: "AVAILABLE",
    cookId: 1
  }
];

export const INITIAL_INVOICES = [
  {
    id: "INV-2026-11-402",
    month: "November 2026",
    flatNumber: "402",
    residentName: "Aarav Sharma",
    societyName: "Hiranandani Gardens",
    towerName: "Tower B - Wing 1",
    baseAmount: 4200,
    walletDeduction: 280,
    netPayable: 3920,
    status: "PAID",
    dueDate: "2026-11-05",
    paidDate: "2026-11-03",
    method: "Razorpay (UPI / ICICI)",
    txnId: "pay_rzp_994102941"
  },
  {
    id: "INV-2026-12-402",
    month: "December 2026",
    flatNumber: "402",
    residentName: "Aarav Sharma",
    societyName: "Hiranandani Gardens",
    towerName: "Tower B - Wing 1",
    baseAmount: 4200,
    walletDeduction: 140,
    netPayable: 4060,
    status: "PENDING",
    dueDate: "2026-12-05",
    paidDate: null,
    method: null,
    txnId: null
  },
  {
    id: "INV-2026-11-701",
    month: "November 2026",
    flatNumber: "701",
    residentName: "Pooja Mehta",
    societyName: "Hiranandani Gardens",
    towerName: "Tower B - Wing 1",
    baseAmount: 4200,
    walletDeduction: 0,
    netPayable: 4200,
    status: "PAID",
    dueDate: "2026-11-05",
    paidDate: "2026-11-04",
    method: "Razorpay (Card / HDFC)",
    txnId: "pay_rzp_77301981"
  }
];

export const INITIAL_COMPLAINTS = [
  {
    id: 1,
    flatNumber: "402",
    residentName: "Aarav Sharma",
    category: "Punctuality",
    subject: "Cook arrived 15 mins late on Tuesday",
    description: "Evening dinner was delayed because the cook arrived at 6:45 PM instead of 6:30 PM due to society gatekeeper queue.",
    status: "RESOLVED",
    adminNotes: "Spoke with Tower security; fast-track CookCircle digital RFID pass enabled for Sunita Devi.",
    date: "2026-11-10"
  },
  {
    id: 2,
    flatNumber: "701",
    residentName: "Pooja Mehta",
    category: "Taste / Recipe Preference",
    subject: "Less chili requested in Jain dishes",
    description: "Please remind the cook to strictly avoid red chili powder and use only mild green chilies for Flat 701.",
    status: "IN_PROGRESS",
    adminNotes: "Noted on cook partner digital route profile.",
    date: "2026-11-14"
  }
];

export const INITIAL_NOTIFICATIONS = [
  {
    id: 1,
    title: "Slot Booking Confirmed",
    message: "Your Evening Slot 1 (6:30 PM - 7:15 PM) is confirmed with Sunita Devi.",
    time: "Today, 4:00 PM",
    type: "BOOKING",
    isRead: false
  },
  {
    id: 2,
    title: "Prep Reminder",
    message: "Your cooking slot starts in 60 minutes. Please soak dal and keep chopped vegetables ready.",
    time: "Today, 5:30 PM",
    type: "PREP",
    isRead: false
  },
  {
    id: 3,
    title: "Zero Disruption Guarantee Active",
    message: "Float Cook Ramesh Kumar is on standby for any emergency leave coverage.",
    time: "Yesterday",
    type: "BACKUP",
    isRead: true
  }
];

// Helper functions for reading/writing with LocalStorage
export function getStoredData(key, fallback) {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : fallback;
  } catch (e) {
    console.warn(`Error reading localStorage key ${key}`, e);
    return fallback;
  }
}

export function setStoredData(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    console.warn(`Error writing to localStorage key ${key}`, e);
  }
}

