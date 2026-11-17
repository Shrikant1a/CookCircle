import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { useLanguage } from '../../context/LanguageContext';
import { useToast } from '../../context/ToastContext';
import {
  ChefHat,
  Clock,
  CheckCircle2,
  AlertCircle,
  Play,
  CheckSquare,
  KeyRound,
  Calendar,
  Languages,
  DollarSign,
  TrendingUp,
  ShieldCheck,
  X,
  PhoneCall
} from 'lucide-react';

export default function CookPartnerDashboard() {
  const { currentSociety, currentTower } = useAuth();
  const {
    slots,
    activeTowerCook,
    isBackupDispatched,
    startCookingWithOtp,
    completeCooking,
    submitLeaveRequest
  } = useApp();
  const { language, toggleLanguage, t } = useLanguage();
  const { showToast } = useToast();

  // OTP Modal State
  const [otpModalOpen, setOtpModalOpen] = useState(false);
  const [activeSlotForOtp, setActiveSlotForOtp] = useState(null);
  const [enteredOtp, setEnteredOtp] = useState('');

  // Leave Modal State
  const [leaveModalOpen, setLeaveModalOpen] = useState(false);
  const [leaveStartDate, setLeaveStartDate] = useState('2026-11-18');
  const [leaveEndDate, setLeaveEndDate] = useState('2026-11-18');
  const [leaveReason, setLeaveReason] = useState('Family function in village');

  const handleOpenOtpModal = (slot) => {
    setActiveSlotForOtp(slot);
    setEnteredOtp('');
    setOtpModalOpen(true);
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    if (!enteredOtp) {
      showToast(language === 'hi' ? "कृपया OTP दर्ज करें" : "Please enter OTP", "error");
      return;
    }
    const success = await startCookingWithOtp(activeSlotForOtp.order, enteredOtp);
    if (success) {
      setOtpModalOpen(false);
      setEnteredOtp('');
    }
  };

  const handleSubmitLeave = async (e) => {
    e.preventDefault();
    await submitLeaveRequest({
      cookId: activeTowerCook.id,
      startDate: leaveStartDate,
      endDate: leaveEndDate,
      reason: leaveReason
    });
    setLeaveModalOpen(false);
    showToast(
      language === 'hi'
        ? "छुट्टी स्वीकृत! स्टैंडबाय कुक रमेश कुमार को टावर आवंटित किया गया।"
        : "Leave submitted & approved! Standby Cook Ramesh Kumar dispatched to cover your tower.",
      "warning"
    );
  };

  return (
    <div className="space-y-6">
      {/* 1. TOP HEADER & VERNACULAR CONTROLS */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2">
        <div className="flex items-center gap-3">
          <img
            src={activeTowerCook.photo}
            alt={activeTowerCook.name}
            className="w-14 h-14 rounded-2xl object-cover border-2 border-brand-orange"
          />
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-xl sm:text-2xl font-extrabold text-white">
                {language === 'hi' ? activeTowerCook.nameHi || activeTowerCook.name : activeTowerCook.name}
              </h1>
              <span className="px-2 py-0.5 rounded-full text-[10px] font-extrabold bg-brand-green/20 text-brand-greenLight">
                {t('onDuty')}
              </span>
            </div>
            <p className="text-xs text-gray-400 mt-0.5">
              {currentSociety.name} • {currentTower.name} • {t('eveningShift')}
            </p>
          </div>
        </div>

        {/* Action Controls */}
        <div className="flex items-center gap-2.5">
          <button
            onClick={toggleLanguage}
            className="px-3 py-1.5 rounded-xl bg-zinc-900 border border-zinc-700 hover:bg-zinc-800 text-xs text-gray-200 font-bold flex items-center gap-1.5 transition-all shadow-sm"
          >
            <Languages className="w-4 h-4 text-brand-orange" />
            <span>{language === 'en' ? 'हिंदी में बदलें' : 'Switch to English'}</span>
          </button>

          <button
            onClick={() => setLeaveModalOpen(true)}
            className="px-3.5 py-1.5 rounded-xl bg-zinc-900 hover:bg-zinc-800 border border-amber-600/60 text-amber-300 text-xs font-bold transition-all flex items-center gap-1.5"
          >
            <Calendar className="w-3.5 h-3.5 text-amber-400" />
            <span>{t('requestLeave')}</span>
          </button>
        </div>
      </div>

      {/* 2. EARNINGS & ATTENDANCE BONUS BANNER */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-5">
          <span className="text-[10px] text-gray-400 uppercase font-bold tracking-wider block">
            {t('earningsTitle')}
          </span>
          <div className="flex items-baseline gap-2 mt-1">
            <span className="text-2xl font-black text-brand-greenLight">₹19,500</span>
            <span className="text-xs text-gray-400">/ ₹21,000</span>
          </div>
          <p className="text-[11px] text-gray-500 mt-1">Direct bank transfer on 1st of next month</p>
        </div>

        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-5 sm:col-span-2 flex flex-col justify-between">
          <div className="flex items-center justify-between mb-2">
            <div>
              <span className="text-xs font-bold text-white flex items-center gap-1.5">
                <TrendingUp className="w-4 h-4 text-brand-orange" />
                {t('zeroLeaveBonus')}
              </span>
              <p className="text-[11px] text-emerald-400 mt-0.5">{t('bonusEligible')}</p>
            </div>
            <span className="text-xs font-mono font-bold text-brand-orangeLight">+₹1,000 Bonus</span>
          </div>

          <div>
            <div className="w-full bg-zinc-800 h-2.5 rounded-full overflow-hidden">
              <div className="bg-gradient-to-r from-brand-orange to-brand-green h-full rounded-full w-[85%]"></div>
            </div>
            <div className="flex justify-between text-[10px] text-gray-400 mt-1.5">
              <span>Attendance: 24 / 26 Shifts Completed</span>
              <span className="font-bold text-gray-200">92% Perfect</span>
            </div>
          </div>
        </div>
      </div>

      {/* 3. DAILY TOWER ROUTE CARD (VERTICAL TIMELINE OF FLATS) */}
      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-5">
        <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
          <div>
            <h2 className="text-base font-extrabold text-white flex items-center gap-2">
              <Clock className="w-4 h-4 text-brand-orange" />
              <span>{t('todaySchedule')} — {currentTower.name}</span>
            </h2>
            <p className="text-xs text-gray-400 mt-0.5">
              Sequential 45-minute flats inside the same tower (Zero transit delay)
            </p>
          </div>

          <span className="text-xs text-gray-400 hidden sm:inline">
            Status: <strong className="text-white">Active Shift</strong>
          </span>
        </div>

        {/* Vertical Timeline List */}
        <div className="space-y-4">
          {slots.map((slot, index) => {
            const isBooked = slot.isBooked;
            const isCooking = slot.status === 'COOKING' || slot.status === 'IN_PROGRESS';
            const isCompleted = slot.status === 'COMPLETED';
            const isSkipped = slot.status === 'SKIPPED';

            return (
              <div
                key={slot.id}
                className={`p-5 rounded-2xl border transition-all ${
                  isCooking
                    ? 'bg-emerald-950/30 border-emerald-500/70 shadow-lg'
                    : isCompleted
                    ? 'bg-zinc-900/60 border-zinc-800 opacity-80'
                    : isSkipped
                    ? 'bg-zinc-900/40 border-dashed border-zinc-800'
                    : isBooked
                    ? 'bg-zinc-900 border-zinc-700/80'
                    : 'bg-[#121212] border-zinc-800/60'
                }`}
              >
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                  <div className="flex items-start gap-4">
                    {/* Time & Sequence Bubble */}
                    <div className="w-12 h-12 rounded-xl bg-zinc-800 border border-zinc-700 flex flex-col items-center justify-center text-center flex-shrink-0">
                      <span className="text-[10px] font-bold text-gray-400">SLOT</span>
                      <span className="text-sm font-extrabold text-white">{slot.order}</span>
                    </div>

                    <div>
                      <div className="flex items-center gap-2.5">
                        <span className="text-sm font-bold text-white">
                          {isBooked ? `Flat ${slot.flatNumber}` : 'Open Slot (Unbooked)'}
                        </span>
                        <span className="text-xs text-gray-400 font-mono font-medium">
                          ({slot.timeDisplay})
                        </span>
                        {isBooked && (
                          <span className="text-[10px] px-2 py-0.5 rounded font-bold bg-zinc-800 text-gray-300">
                            {slot.dietaryPref || 'Veg'}
                          </span>
                        )}
                      </div>

                      {isBooked ? (
                        <div className="mt-1 text-xs">
                          <span className="text-gray-400">{t('dishesToCook')}: </span>
                          <strong className="text-brand-greenLight font-semibold">
                            {slot.dishesPlanned || "Dal Tadka + Aloo Gobhi + 4 Phulkas"}
                          </strong>
                          <span className="text-gray-500 text-[11px] ml-2">
                            ({slot.householdSize || '3-4 members'})
                          </span>
                        </div>
                      ) : (
                        <p className="text-xs text-gray-500 mt-1">Available for resident pooling</p>
                      )}
                    </div>
                  </div>

                  {/* Actions according to slot status */}
                  <div className="w-full sm:w-auto flex items-center justify-end gap-2 pt-2 sm:pt-0">
                    {isCompleted ? (
                      <span className="px-3 py-1.5 rounded-lg bg-zinc-800 text-emerald-400 text-xs font-bold flex items-center gap-1.5">
                        <CheckCircle2 className="w-4 h-4" /> {t('statusCompleted')}
                      </span>
                    ) : isSkipped ? (
                      <span className="px-3 py-1.5 rounded-lg bg-zinc-800/80 text-amber-300 text-xs font-semibold">
                        {t('statusSkipped')}
                      </span>
                    ) : isCooking ? (
                      <div className="flex items-center gap-2 w-full sm:w-auto">
                        <span className="text-xs font-bold text-emerald-300 flex items-center gap-1 animate-pulse">
                          <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
                          {t('cookingInProgress')}
                        </span>
                        <button
                          onClick={() => completeCooking(slot.order)}
                          className="px-4 py-2 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-md transition-all flex items-center gap-1.5"
                        >
                          <CheckSquare className="w-4 h-4" />
                          <span>{t('completeCooking')}</span>
                        </button>
                      </div>
                    ) : isBooked ? (
                      <button
                        onClick={() => handleOpenOtpModal(slot)}
                        className="w-full sm:w-auto px-4 py-2 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-md transition-all flex items-center justify-center gap-1.5"
                      >
                        <Play className="w-3.5 h-3.5 fill-current" />
                        <span>{t('startCooking')}</span>
                      </button>
                    ) : (
                      <span className="text-xs text-gray-500 italic">No action needed</span>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* 4. OTP ENTRY MODAL */}
      {otpModalOpen && activeSlotForOtp && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl max-w-sm w-full p-6 shadow-2xl relative">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <div className="flex items-center gap-2">
                <KeyRound className="w-5 h-5 text-brand-green" />
                <h3 className="text-sm font-bold text-white">
                  Flat {activeSlotForOtp.flatNumber} Check-In
                </h3>
              </div>
              <button onClick={() => setOtpModalOpen(false)} className="text-gray-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <p className="text-xs text-gray-400 my-3 leading-relaxed">
              {t('otpDescription')} (Demo Resident OTP: <strong className="text-white">{activeSlotForOtp.arrivalOtp || '4829'}</strong>)
            </p>

            <form onSubmit={handleVerifyOtp} className="space-y-4">
              <div>
                <input
                  type="text"
                  maxLength="4"
                  autoFocus
                  required
                  value={enteredOtp}
                  onChange={(e) => setEnteredOtp(e.target.value)}
                  placeholder="e.g. 4829"
                  className="w-full text-center font-mono text-2xl tracking-widest bg-zinc-900 border border-zinc-700 rounded-xl py-3 text-white focus:outline-none focus:border-brand-green"
                />
              </div>

              <div className="flex items-center gap-2">
                <button
                  type="button"
                  onClick={() => setOtpModalOpen(false)}
                  className="flex-1 py-2.5 rounded-xl bg-zinc-800 text-gray-300 text-xs font-semibold"
                >
                  {t('cancel')}
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl bg-brand-green hover:bg-emerald-600 text-white text-xs font-bold shadow-lg"
                >
                  {t('verifyAndStart')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 5. REQUEST LEAVE MODAL */}
      {leaveModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl max-w-md w-full p-6 shadow-2xl relative">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <h3 className="text-base font-bold text-white">{t('leaveModalTitle')}</h3>
              <button onClick={() => setLeaveModalOpen(false)} className="text-gray-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <p className="text-xs text-amber-300/90 my-3 p-3 rounded-xl bg-amber-950/30 border border-amber-800/40 leading-relaxed">
              {t('backupCookNotice')}
            </p>

            <form onSubmit={handleSubmitLeave} className="space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">{t('startDate')}</label>
                  <input
                    type="date"
                    required
                    value={leaveStartDate}
                    onChange={(e) => setLeaveStartDate(e.target.value)}
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">{t('endDate')}</label>
                  <input
                    type="date"
                    required
                    value={leaveEndDate}
                    onChange={(e) => setLeaveEndDate(e.target.value)}
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">{t('reason')}</label>
                <textarea
                  rows="3"
                  required
                  value={leaveReason}
                  onChange={(e) => setLeaveReason(e.target.value)}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                ></textarea>
              </div>

              <div className="flex items-center gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setLeaveModalOpen(false)}
                  className="flex-1 py-2.5 rounded-xl bg-zinc-800 text-gray-300 text-xs font-semibold"
                >
                  {t('cancel')}
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl bg-brand-orange hover:bg-orange-600 text-white text-xs font-bold shadow-lg"
                >
                  {t('submitLeaveRequest')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

