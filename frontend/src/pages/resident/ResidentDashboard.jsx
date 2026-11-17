import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { useToast } from '../../context/ToastContext';
import {
  ChefHat,
  ShieldCheck,
  Clock,
  Sparkles,
  CheckCircle2,
  CalendarCheck,
  RefreshCw,
  KeyRound,
  AlertOctagon,
  Users,
  ChevronRight,
  UtensilsCrossed,
  ArrowRight,
  X,
  Star,
  Check
} from 'lucide-react';
import { Link } from 'react-router-dom';

export default function ResidentDashboard() {
  const { currentUser, setResidentCookAndSlot, currentSociety, currentTower } = useAuth();
  const {
    slots,
    cooks,
    activeTowerCook,
    isBackupDispatched,
    walletBalance,
    prepChecklist,
    toggleChecklistItem,
    selectedMeal,
    setSelectedMeal,
    skipMeal,
    bookSlot,
    assignUserCookAndSlot
  } = useApp();
  const { showToast } = useToast();

  const isDemoResident = currentUser?.email === 'resident@society.com';
  const hasCookSelected = isDemoResident || Boolean(currentUser?.hasSelectedCook && currentUser?.assignedCookId);
  const residentName = currentUser?.fullName || (isDemoResident ? 'Aarav Sharma' : 'Resident');
  const residentFlat = currentUser?.flatNumber || (isDemoResident ? '402' : '');

  // Cook Setup Modal State (for new users choosing their cook & slot)
  const [setupModalOpen, setSetupModalOpen] = useState(false);
  const [selectedCookForSetup, setSelectedCookForSetup] = useState(null);
  const [selectedSlotOrder, setSelectedSlotOrder] = useState(1);
  const [customFlatInput, setCustomFlatInput] = useState(residentFlat || '501');
  const [chosenMeal, setChosenMeal] = useState('Dal Tadka + Aloo Gobhi + 4 Phulkas');
  const [submittingSetup, setSubmittingSetup] = useState(false);

  // Active slot for this resident
  const assignedCook = currentUser?.assignedCookId
    ? cooks.find(c => c.id === currentUser.assignedCookId) || activeTowerCook
    : activeTowerCook;

  const mySlot = slots.find(s => s.flatNumber && s.flatNumber === residentFlat)
    || (currentUser?.bookedSlotOrder ? slots.find(s => s.order === currentUser.bookedSlotOrder) : null)
    || (isDemoResident ? slots[0] : null);

  // Countdown timer simulation
  const [secondsRemaining, setSecondsRemaining] = useState(1680);

  useEffect(() => {
    if (mySlot && (mySlot.status === 'COOKING' || mySlot.status === 'IN_PROGRESS')) {
      const interval = setInterval(() => {
        setSecondsRemaining(prev => (prev > 0 ? prev - 1 : 0));
      }, 1000);
      return () => clearInterval(interval);
    }
  }, [mySlot?.status]);

  const formatTimer = (secs) => {
    const mins = Math.floor(secs / 60);
    const s = secs % 60;
    return `${mins}m ${s < 10 ? '0' : ''}${s}s`;
  };

  // Open cook & slot onboarding modal
  const handleOpenSetup = (cook) => {
    setSelectedCookForSetup(cook);
    setSelectedSlotOrder(1);
    setCustomFlatInput(residentFlat || '501');
    setSetupModalOpen(true);
  };

  // Confirm cook selection & activate subscription
  const handleConfirmCookAndSlot = (e) => {
    e.preventDefault();
    if (!selectedCookForSetup) return;

    setSubmittingSetup(true);
    try {
      // 1. Activate in AppContext (updates slots, generates invoice, adds notification)
      assignUserCookAndSlot({
        cookId: selectedCookForSetup.id,
        cookName: selectedCookForSetup.name,
        slotOrder: selectedSlotOrder,
        flatNumber: customFlatInput,
        residentName,
        dietaryPref: currentUser?.dietaryPref || 'Pure Veg',
        householdSize: currentUser?.householdSize || '3-4 members',
        mealPlan: chosenMeal
      });

      // 2. Persist to AuthContext & localStorage
      if (setResidentCookAndSlot) {
        setResidentCookAndSlot(selectedCookForSetup.id, selectedCookForSetup.name, selectedSlotOrder);
      }

      setSetupModalOpen(false);
      showToast(`Congratulations! Cook ${selectedCookForSetup.name} is now assigned to Flat ${customFlatInput}.`, 'success');
    } catch (err) {
      showToast('Failed to activate subscription. Please try again.', 'error');
    } finally {
      setSubmittingSetup(false);
    }
  };

  const mealsList = [
    "Dal Tadka + Aloo Gobhi + 4 Phulkas",
    "Paneer Bhurji + Jeera Rice + 3 Phulkas",
    "Rajma Masala + Steamed Basmati Rice + Salad",
    "Gujarati Kadi + Khichdi + Batata Nu Shaak",
    "Chole Masala + 3 Bhature + Mint Chutney",
    "Methi Thepla + Sukhi Aloo Bhaji + Curd"
  ];

  // User's effective wallet balance: demo user has 280, new user starts at 0 (or earned refunds)
  const effectiveWalletBalance = isDemoResident ? walletBalance : Math.max(0, walletBalance - 280);

  return (
    <div className="space-y-6">
      {/* Backup Cook Alert (if active) */}
      {isBackupDispatched && hasCookSelected && (
        <div className="p-4 rounded-2xl bg-amber-950/40 border border-amber-600/60 flex items-start justify-between gap-4 animate-fade-in">
          <div className="flex items-start gap-3">
            <div className="p-2 rounded-xl bg-amber-500/20 text-amber-400 flex-shrink-0 mt-0.5">
              <AlertOctagon className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="px-2 py-0.5 rounded-full text-[10px] font-extrabold bg-amber-500/30 text-amber-200 uppercase tracking-wider">
                  Zero Disruption Guarantee Active
                </span>
                <span className="text-xs text-amber-300 font-bold">
                  Standby Float Cook Dispatched
                </span>
              </div>
              <p className="text-xs text-amber-200/90 mt-1 leading-relaxed">
                Primary Cook {assignedCook.name} is on leave. Standby Float Cook <strong className="text-white">Ramesh Kumar (4.85 ★)</strong> has been assigned to your tower's evening shift. Your slot will be served without disruption.
              </p>
            </div>
          </div>
          <span className="hidden sm:inline-flex px-3 py-1 rounded-lg text-xs font-bold bg-amber-500/20 text-amber-300 border border-amber-500/30 whitespace-nowrap">
            Zero Disruption
          </span>
        </div>
      )}

      {/* Header Greeting */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight flex items-center gap-2">
            <span>Welcome, {residentName}!</span>
            {residentFlat && <span className="text-base font-normal text-gray-400">(Flat {residentFlat})</span>}
          </h1>
          <p className="text-xs text-gray-400 mt-1">
            {currentSociety.name} • {currentTower.name} • Hyperlocal Tower Cooking Platform
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <div className="px-3.5 py-1.5 rounded-xl bg-zinc-900 border border-zinc-800 text-xs">
            <span className="text-gray-400 block text-[10px] uppercase font-bold">Wallet Credit</span>
            <span className="font-extrabold text-emerald-400 text-sm">₹{effectiveWalletBalance}</span>
          </div>

          <div className="px-3.5 py-1.5 rounded-xl bg-emerald-950/40 border border-emerald-800/40 text-xs">
            <span className="text-emerald-300 block text-[10px] uppercase font-bold">Monthly Pool Cost</span>
            <span className="font-extrabold text-white text-sm">₹4,200</span>
            <span className="text-emerald-300 text-[10px]"> (72% saved)</span>
          </div>
        </div>
      </div>

      {/* ------------------------------------------------------------------------- */}
      {/* NEW USER ONBOARDING: CHOOSE YOUR COOK & START FRESH */}
      {/* ------------------------------------------------------------------------- */}
      {!hasCookSelected && (
        <div className="bg-gradient-to-br from-[#16221d] via-[#141414] to-[#111111] border border-emerald-500/40 rounded-3xl p-6 sm:p-8 shadow-2xl space-y-6 animate-fade-in">
          <div>
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-brand-green/20 border border-brand-green/30 text-brand-greenLight text-xs font-bold mb-2">
              <Sparkles className="w-3.5 h-3.5" /> Start Fresh — Welcome to Your Tower Cooking Pool
            </div>
            <h2 className="text-xl sm:text-2xl font-extrabold text-white">
              Step 1: Choose Your Cook Partner for Flat {residentFlat || 'Your Flat'}
            </h2>
            <p className="text-xs text-gray-400 mt-1 max-w-2xl leading-relaxed">
              As a new resident, you start completely fresh with zero past history. Browse the verified domestic cook partners available for your society, review their cuisines and ratings, and select your preferred daily 45-minute cooking slot.
            </p>
          </div>

          {/* Cooks Available Selection Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {cooks.map((cook) => (
              <div
                key={cook.id}
                className="p-5 rounded-2xl bg-zinc-900/90 border border-zinc-800 hover:border-emerald-500/50 hover:bg-zinc-900 transition-all flex flex-col justify-between group shadow-lg"
              >
                <div>
                  <div className="flex items-start gap-4 mb-3">
                    <img
                      src={cook.photo}
                      alt={cook.name}
                      className="w-16 h-16 rounded-2xl object-cover border-2 border-zinc-700 group-hover:border-brand-green transition-colors"
                    />
                    <div className="flex-1">
                      <div className="flex items-center justify-between">
                        <h4 className="text-base font-bold text-white">{cook.name}</h4>
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 flex items-center gap-1">
                          ★ {cook.rating} ({cook.reviewsCount || 85})
                        </span>
                      </div>
                      <p className="text-xs text-brand-orangeLight font-medium mt-0.5">
                        {cook.experienceYears} Years Exp • {cook.type === 'PRIMARY_COOK' ? 'Tower Dedicated Cook' : 'Rapid Standby Float Cook'}
                      </p>
                      <p className="text-[11px] text-gray-400 mt-1 line-clamp-1">
                        Specialties: {cook.specialties.join(', ')}
                      </p>
                    </div>
                  </div>

                  <div className="flex flex-wrap items-center gap-1.5 pt-2 border-t border-zinc-800/80 text-[10px] text-gray-300">
                    <span className="px-2 py-0.5 rounded bg-zinc-800 text-emerald-400 flex items-center gap-1 font-semibold">
                      <ShieldCheck className="w-3 h-3" /> Aadhaar Verified
                    </span>
                    <span className="px-2 py-0.5 rounded bg-zinc-800 text-emerald-400 flex items-center gap-1 font-semibold">
                      <ShieldCheck className="w-3 h-3" /> Police Verified
                    </span>
                  </div>
                </div>

                <div className="mt-4 pt-3 border-t border-zinc-800">
                  <button
                    type="button"
                    onClick={() => handleOpenSetup(cook)}
                    className="w-full py-2.5 px-4 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-md shadow-emerald-950/40 transition-all flex items-center justify-center gap-2 group-hover:scale-[1.01]"
                  >
                    <span>Choose {cook.name} & Select Slot</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ------------------------------------------------------------------------- */}
      {/* ACTIVE SUBSCRIBED VIEW: ONCE COOK IS CHOSEN */}
      {/* ------------------------------------------------------------------------- */}
      {hasCookSelected && (
        <>
          {/* Assigned Cook Profile & OTP Card */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Cook Profile Card */}
            <div className="lg:col-span-2 bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl relative overflow-hidden">
              <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-6 border-b border-zinc-800">
                <div className="flex items-center gap-4">
                  <div className="relative">
                    <img
                      src={assignedCook.photo}
                      alt={assignedCook.name}
                      className="w-16 h-16 rounded-2xl object-cover border-2 border-emerald-500/50 shadow-md"
                    />
                    <span className="absolute -bottom-1 -right-1 w-5 h-5 rounded-full bg-brand-green border-2 border-[#151515] flex items-center justify-center">
                      <CheckCircle2 className="w-3 h-3 text-white" />
                    </span>
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="text-lg font-bold text-white">{assignedCook.name}</h3>
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300 flex items-center gap-1">
                        ★ {assignedCook.rating} ({assignedCook.reviewsCount || 98})
                      </span>
                    </div>
                    <p className="text-xs text-gray-400 mt-0.5">
                      {assignedCook.experienceYears} Years Experience • {assignedCook.specialties.join(', ')}
                    </p>
                    <div className="flex flex-wrap items-center gap-2 mt-2">
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-950/60 text-emerald-300 border border-emerald-800/50">
                        <ShieldCheck className="w-3 h-3" /> Aadhaar Verified
                      </span>
                      <span className="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-950/60 text-emerald-300 border border-emerald-800/50">
                        <ShieldCheck className="w-3 h-3" /> Police Verified
                      </span>
                    </div>
                  </div>
                </div>

                {/* 1-Click Skip Meal Button */}
                <div>
                  <button
                    onClick={() => mySlot && skipMeal(mySlot.order)}
                    disabled={!mySlot || mySlot.status === 'SKIPPED'}
                    className={`px-3.5 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 shadow-sm ${
                      !mySlot || mySlot.status === 'SKIPPED'
                        ? 'bg-zinc-800 text-gray-500 cursor-not-allowed border border-zinc-700'
                        : 'bg-zinc-900 border border-zinc-700 hover:border-amber-500/60 hover:bg-amber-950/20 text-gray-200 hover:text-amber-300'
                    }`}
                    title="Skip this slot and instantly receive ₹140 wallet refund"
                  >
                    <RefreshCw className="w-3.5 h-3.5 text-brand-orange" />
                    <span>
                      {mySlot?.status === 'SKIPPED' ? 'Meal Skipped (+₹140 Credited)' : '1-Click Skip Meal (+₹140 Credit)'}
                    </span>
                  </button>
                </div>
              </div>

              {/* Cooking Session Live Window & OTP */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-6">
                {/* 4-Digit Arrival OTP */}
                <div className="p-4 rounded-xl bg-zinc-900/90 border border-zinc-800 flex items-center justify-between">
                  <div>
                    <span className="text-[10px] uppercase font-bold text-gray-400 tracking-wider flex items-center gap-1">
                      <KeyRound className="w-3 h-3 text-brand-green" /> Cook Arrival OTP
                    </span>
                    <p className="text-[11px] text-gray-400 mt-0.5">Share with cook upon door arrival</p>
                  </div>
                  <div className="px-3.5 py-1.5 rounded-lg bg-[#0A0A0A] border border-emerald-600/50 text-emerald-400 font-mono text-xl font-black tracking-widest">
                    {mySlot?.arrivalOtp || '4829'}
                  </div>
                </div>

                {/* Session Timing & Status */}
                <div className="p-4 rounded-xl bg-zinc-900/90 border border-zinc-800 flex items-center justify-between">
                  <div>
                    <span className="text-[10px] uppercase font-bold text-gray-400 tracking-wider flex items-center gap-1">
                      <Clock className="w-3 h-3 text-brand-orange" /> Session Timing
                    </span>
                    <p className="text-xs font-semibold text-white mt-0.5">{mySlot?.timeDisplay || '6:30 PM – 7:15 PM'}</p>
                  </div>
                  <div className="text-right">
                    <span className="text-[10px] font-bold text-gray-400 uppercase block">Status</span>
                    <span className="text-xs font-bold text-brand-greenLight flex items-center gap-1 justify-end">
                      <span className="w-2 h-2 rounded-full bg-brand-green animate-pulse"></span>
                      {mySlot?.status === 'COOKING' || mySlot?.status === 'IN_PROGRESS'
                        ? `Cooking (${formatTimer(secondsRemaining)})`
                        : mySlot?.status || 'CONFIRMED'}
                    </span>
                  </div>
                </div>
              </div>
            </div>

            {/* Today's Prep Checklist Card */}
            <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between mb-2">
                  <h3 className="font-bold text-white text-sm flex items-center gap-2">
                    <UtensilsCrossed className="w-4 h-4 text-brand-orange" />
                    <span>60-Min Pre-Cook Checklist</span>
                  </h3>
                  <span className="text-[10px] font-bold text-brand-greenLight bg-brand-green/20 px-2 py-0.5 rounded">
                    Active
                  </span>
                </div>
                <p className="text-[11px] text-gray-400 mb-4 leading-relaxed">
                  Keep prep ready so your cook completes dishes within the 45-minute window.
                </p>

                {/* Checklist Items */}
                <div className="space-y-2.5">
                  {prepChecklist.map((item) => (
                    <div
                      key={item.id}
                      onClick={() => toggleChecklistItem(item.id)}
                      className={`p-2.5 rounded-xl border text-xs cursor-pointer flex items-start gap-2.5 transition-all ${
                        item.done
                          ? 'bg-zinc-900/80 border-emerald-900/50 text-gray-300'
                          : 'bg-zinc-900/40 border-zinc-800 text-gray-400 hover:border-zinc-700'
                      }`}
                    >
                      <input
                        type="checkbox"
                        checked={item.done}
                        onChange={() => {}}
                        className="w-4 h-4 mt-0.5 rounded border-zinc-700 text-brand-green focus:ring-0 cursor-pointer accent-brand-green"
                      />
                      <span className={`text-[11px] leading-tight ${item.done ? 'line-through text-gray-500' : 'text-gray-200'}`}>
                        {item.text}
                      </span>
                    </div>
                  ))}
                </div>
              </div>

              {/* Dish Selector Dropdown */}
              <div className="mt-4 pt-3 border-t border-zinc-800">
                <label className="block text-[11px] font-bold text-gray-300 mb-1">
                  Today's Selected Meal:
                </label>
                <select
                  value={selectedMeal}
                  onChange={(e) => {
                    setSelectedMeal(e.target.value);
                    showToast(`Meal updated to: ${e.target.value}`, "info");
                  }}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-brand-greenLight font-medium focus:outline-none focus:border-brand-green cursor-pointer"
                >
                  {mealsList.map((meal, idx) => (
                    <option key={idx} value={meal} className="bg-zinc-900 text-white">
                      {meal}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        </>
      )}

      {/* ------------------------------------------------------------------------- */}
      {/* 5-SLOT TIMELINE VISUALIZER */}
      {/* ------------------------------------------------------------------------- */}
      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl">
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-4 border-b border-zinc-800 gap-4">
          <div>
            <div className="flex items-center gap-2">
              <span className="w-2.5 h-2.5 rounded-full bg-brand-green animate-pulse"></span>
              <h2 className="text-base font-extrabold text-white tracking-tight">
                Live Tower Cook Pool Visualizer
              </h2>
            </div>
            <p className="text-xs text-gray-400 mt-0.5">
              {currentTower.name} • Evening Shift (6:30 PM – 10:15 PM) • 5 consecutive 45-min slots
            </p>
          </div>

          <div className="flex items-center gap-3">
            <span className="text-xs text-gray-400">
              Monthly Subscription: <strong className="text-brand-greenLight font-bold">₹4,200</strong> per flat
            </span>
          </div>
        </div>

        {/* 5-Slot Horizontal Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-5 gap-3.5 mt-5">
          {slots.map((slot) => {
            const isBooked = slot.isBooked;
            const isMyFlat = slot.flatNumber && slot.flatNumber === residentFlat;

            return (
              <div
                key={slot.id}
                className={`p-4 rounded-xl border transition-all text-left flex flex-col justify-between ${
                  isMyFlat
                    ? 'bg-emerald-950/30 border-emerald-500/70 shadow-md shadow-emerald-950/50'
                    : isBooked
                    ? 'bg-zinc-900/80 border-zinc-800'
                    : 'bg-[#181818] border-dashed border-zinc-700 hover:border-brand-green/60'
                }`}
              >
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-bold text-gray-200">Slot {slot.order}</span>
                    <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${
                      isMyFlat
                        ? 'bg-brand-green text-white font-extrabold'
                        : isBooked
                        ? 'bg-zinc-800 text-gray-400'
                        : 'bg-emerald-500/20 text-brand-greenLight'
                    }`}>
                      {isMyFlat ? 'Your Flat' : isBooked ? 'Reserved' : 'Available'}
                    </span>
                  </div>

                  <p className="text-xs font-semibold text-white">{slot.timeDisplay.split('–')[0]}</p>
                  <p className="text-[10px] text-gray-400">to {slot.timeDisplay.split('–')[1]}</p>
                </div>

                <div className="mt-4 pt-2.5 border-t border-zinc-800/80">
                  {isBooked ? (
                    <div>
                      <span className="text-[10px] text-gray-500 uppercase block">Occupant</span>
                      <p className="text-xs font-bold text-emerald-400 truncate">
                        Flat {slot.flatNumber} {isMyFlat && '(You)'}
                      </p>
                    </div>
                  ) : (
                    <button
                      type="button"
                      onClick={() => handleOpenSetup(assignedCook)}
                      className="text-xs text-brand-greenLight font-semibold hover:underline flex items-center gap-1"
                    >
                      Join Slot <ArrowRight className="w-3 h-3 inline" />
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* ------------------------------------------------------------------------- */}
      {/* MODAL: CHOOSE COOK & CONFIRM SLOT (FOR STARTING FRESH) */}
      {/* ------------------------------------------------------------------------- */}
      {setupModalOpen && selectedCookForSetup && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
          <div className="bg-[#181818] border border-zinc-700 max-w-lg w-full rounded-2xl p-6 shadow-2xl space-y-5 relative">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <div className="flex items-center gap-2">
                <ChefHat className="w-5 h-5 text-brand-greenLight" />
                <h3 className="text-lg font-bold text-white">
                  Confirm Cooking Pool Subscription
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setSetupModalOpen(false)}
                className="p-1 rounded-lg text-gray-400 hover:text-white hover:bg-zinc-800"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Selected Cook Preview */}
            <div className="flex items-center gap-3.5 p-3.5 rounded-xl bg-zinc-900 border border-zinc-800">
              <img
                src={selectedCookForSetup.photo}
                alt={selectedCookForSetup.name}
                className="w-14 h-14 rounded-xl object-cover border-2 border-brand-green"
              />
              <div className="flex-1 text-xs">
                <h4 className="font-bold text-white text-sm">{selectedCookForSetup.name}</h4>
                <p className="text-gray-400 text-[11px]">{selectedCookForSetup.experienceYears} Years Exp • {selectedCookForSetup.rating} ★</p>
                <p className="text-emerald-400 text-[11px] mt-0.5">{selectedCookForSetup.specialties.join(', ')}</p>
              </div>
            </div>

            <form onSubmit={handleConfirmCookAndSlot} className="space-y-4">
              {/* Slot Picker */}
              <div>
                <label className="block text-xs font-bold text-gray-300 mb-1.5">
                  Select Your Daily 45-Minute Slot:
                </label>
                <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                  {slots.map((s) => {
                    const isAvailable = !s.isBooked || s.flatNumber === customFlatInput;
                    const isSelected = selectedSlotOrder === s.order;

                    return (
                      <button
                        key={s.order}
                        type="button"
                        disabled={!isAvailable}
                        onClick={() => setSelectedSlotOrder(s.order)}
                        className={`p-2.5 rounded-xl border text-left text-xs transition-all ${
                          !isAvailable
                            ? 'bg-zinc-900/40 border-zinc-800/60 text-gray-600 cursor-not-allowed'
                            : isSelected
                            ? 'bg-emerald-950/50 border-brand-green text-white ring-1 ring-emerald-500/50'
                            : 'bg-zinc-900 border-zinc-800 text-gray-300 hover:border-zinc-700'
                        }`}
                      >
                        <span className="font-bold block text-[11px]">Slot {s.order}</span>
                        <span className="text-[10px] text-gray-400 block">{s.timeDisplay.split('–')[0]}</span>
                        <span className={`text-[9px] font-bold mt-1 inline-block px-1 py-0.2 rounded ${
                          isSelected ? 'text-brand-greenLight' : isAvailable ? 'text-gray-400' : 'text-rose-500'
                        }`}>
                          {isSelected ? 'Selected' : isAvailable ? 'Available' : 'Taken'}
                        </span>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Flat Number */}
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">Your Flat Number</label>
                  <input
                    type="text"
                    required
                    value={customFlatInput}
                    onChange={(e) => setCustomFlatInput(e.target.value)}
                    placeholder="e.g. 501"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-green"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">Initial Meal Plan</label>
                  <select
                    value={chosenMeal}
                    onChange={(e) => setChosenMeal(e.target.value)}
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-green"
                  >
                    {mealsList.map((m, idx) => (
                      <option key={idx} value={m}>{m}</option>
                    ))}
                  </select>
                </div>
              </div>

              {/* Monthly Rate Summary */}
              <div className="p-3 rounded-xl bg-zinc-900/80 border border-zinc-800 text-xs flex items-center justify-between">
                <div>
                  <span className="text-gray-400 block text-[11px]">Monthly Shared Rate</span>
                  <span className="font-bold text-white text-sm">₹4,200 / month</span>
                </div>
                <span className="px-2 py-1 rounded bg-brand-green/20 text-brand-greenLight font-bold text-[11px]">
                  Guaranteed Backup Cook
                </span>
              </div>

              <div className="pt-2 flex justify-end gap-2 border-t border-zinc-800">
                <button
                  type="button"
                  onClick={() => setSetupModalOpen(false)}
                  className="px-4 py-2 rounded-xl bg-zinc-800 hover:bg-zinc-700 text-gray-300 text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submittingSetup}
                  className="px-5 py-2 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg flex items-center gap-1.5"
                >
                  <span>{submittingSetup ? 'Activating...' : 'Confirm & Start Fresh'}</span>
                  <Check className="w-3.5 h-3.5" />
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
