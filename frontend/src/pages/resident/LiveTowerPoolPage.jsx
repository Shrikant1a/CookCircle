import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { useToast } from '../../context/ToastContext';
import {
  Clock,
  ShieldCheck,
  CheckCircle2,
  Users,
  Building2,
  ArrowRight,
  TrendingDown,
  Percent,
  X
} from 'lucide-react';

export default function LiveTowerPoolPage() {
  const { currentSociety, currentTower } = useAuth();
  const { slots, activeTowerCook, bookSlot } = useApp();
  const { showToast } = useToast();

  const [bookingModalOpen, setBookingModalOpen] = useState(false);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [flatNumber, setFlatNumber] = useState('');
  const [householdSize, setHouseholdSize] = useState('3-4 members');
  const [dietaryPref, setDietaryPref] = useState('Pure Veg');

  // Manual Custom Slot Booking State
  const [manualModalOpen, setManualModalOpen] = useState(false);
  const [manualDate, setManualDate] = useState('2026-11-18');
  const [manualTime, setManualTime] = useState('1:00 PM – 1:45 PM (Special Lunch)');
  const [manualFlat, setManualFlat] = useState('402');
  const [manualDishes, setManualDishes] = useState('Paneer Tikka Masala, Dal Makhani, Garlic Naan');
  const [manualGuests, setManualGuests] = useState('6 guests');

  const handleOpenBooking = (slot) => {
    setSelectedSlot(slot);
    setBookingModalOpen(true);
  };

  const handleConfirm = async (e) => {
    e.preventDefault();
    if (!flatNumber.trim()) {
      showToast("Please enter flat number", "error");
      return;
    }
    await bookSlot(selectedSlot.order, {
      flatNumber,
      householdSize,
      dietaryPref
    });
    setBookingModalOpen(false);
    setFlatNumber('');
  };

  const handleManualSubmit = async (e) => {
    e.preventDefault();
    const customOtp = Math.floor(1000 + Math.random() * 9000).toString();
    showToast(`Manual custom slot confirmed for Flat ${manualFlat} at ${manualTime}! Arrival OTP: ${customOtp}`, 'success');
    setManualModalOpen(false);
  };

  const bookedCount = slots.filter(s => s.isBooked).length;
  const soloMarketRate = 15000;
  const pooledRate = currentSociety.basePoolingRate || 4200;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            Live Tower Cook Pool Visualizer
          </h1>
          <p className="text-xs text-gray-400 mt-1">
            {currentSociety.name} • {currentTower.name} • 5 Synchronized 45-Min Slots
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="px-3 py-1.5 rounded-xl bg-zinc-900 border border-zinc-800 text-xs text-gray-300">
            Pool Occupancy: <strong className="text-brand-greenLight font-bold">{bookedCount} of 5 Flats</strong>
          </span>
          <button
            onClick={() => setManualModalOpen(true)}
            className="px-3.5 py-1.5 rounded-xl bg-brand-orange hover:bg-orange-600 text-white font-bold text-xs shadow-lg transition-all flex items-center gap-1.5"
          >
            <span>+ Manual Custom Slot Option</span>
          </button>
        </div>
      </div>

      {/* Economics & Value Card */}
      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl grid grid-cols-1 md:grid-cols-3 gap-6 items-center">
        <div className="md:col-span-2 space-y-2">
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-brand-greenLight bg-brand-green/20 px-2.5 py-0.5 rounded-full uppercase tracking-wider">
              Hyperlocal Cost Optimization
            </span>
            <span className="text-xs text-gray-400">Save 72% Monthly</span>
          </div>
          <h3 className="text-lg font-bold text-white">
            Shared Tower Economics: ₹4,200/mo vs ₹15,000 Solo Cook
          </h3>
          <p className="text-xs text-gray-400 leading-relaxed">
            By grouping flats consecutively within {currentTower.name}, domestic cooks eliminate transit overhead. All flats get identical hygiene, fresh hot rotis, and automatic standby float cook coverage.
          </p>
        </div>

        <div className="bg-zinc-900 border border-zinc-800 p-4 rounded-xl text-center space-y-1">
          <span className="text-[10px] text-gray-400 uppercase font-bold block">Annual Household Savings</span>
          <span className="text-2xl font-black text-brand-greenLight">₹1,29,600 / yr</span>
          <span className="text-[10px] text-gray-500 block">per flat in Tower B</span>
        </div>
      </div>

      {/* Visual Timeline (5 Slots) */}
      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-4">
        <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
          <div>
            <h3 className="font-bold text-white text-base">Evening Shift Timeline (6:30 PM – 10:15 PM)</h3>
            <p className="text-xs text-gray-400">
              Assigned Culinary Partner: <strong className="text-white">{activeTowerCook.name}</strong> (4.9 ★)
            </p>
          </div>
          <span className="text-xs text-brand-greenLight font-semibold flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-brand-green animate-pulse"></span> Live Status
          </span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-5 gap-4 pt-2">
          {slots.map((slot) => {
            const isBooked = slot.isBooked;
            const isMyFlat = slot.flatNumber === "402";

            return (
              <div
                key={slot.id}
                className={`p-4 rounded-xl border flex flex-col justify-between transition-all ${
                  isMyFlat
                    ? 'bg-emerald-950/40 border-emerald-500/80 shadow-lg'
                    : isBooked
                    ? 'bg-zinc-900/90 border-zinc-700/60'
                    : 'bg-[#181818] border-dashed border-zinc-700 hover:border-brand-green'
                }`}
              >
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-bold text-gray-300">Slot {slot.order}</span>
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                      isMyFlat
                        ? 'bg-emerald-500/20 text-emerald-300'
                        : isBooked
                        ? 'bg-zinc-800 text-gray-400'
                        : 'bg-brand-green/20 text-brand-greenLight'
                    }`}>
                      {isMyFlat ? 'Your Flat' : isBooked ? 'Occupied' : 'Open'}
                    </span>
                  </div>

                  <p className="text-xs font-semibold text-white">{slot.timeDisplay}</p>

                  <div className="mt-4 pt-3 border-t border-zinc-800/80">
                    {isBooked ? (
                      <div className="space-y-1">
                        <p className="text-xs font-bold text-emerald-400">Flat {slot.flatNumber}</p>
                        <p className="text-[10px] text-gray-400 truncate">{slot.dishesPlanned || 'Standard Menu'}</p>
                        <p className="text-[10px] text-gray-500">{slot.householdSize || '3-4 members'}</p>
                      </div>
                    ) : (
                      <p className="text-[11px] text-gray-500">No flat assigned yet</p>
                    )}
                  </div>
                </div>

                <div className="mt-4">
                  {isBooked ? (
                    <span className="text-[11px] text-gray-400 flex items-center gap-1 font-medium">
                      <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500" /> Active in Pool
                    </span>
                  ) : (
                    <button
                      onClick={() => handleOpenBooking(slot)}
                      className="w-full py-1.5 rounded-lg bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs transition-all flex items-center justify-center gap-1"
                    >
                      <span>Reserve Slot</span>
                      <ArrowRight className="w-3 h-3" />
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Modal */}
      {bookingModalOpen && selectedSlot && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl max-w-md w-full p-6 shadow-2xl relative">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <h3 className="text-base font-bold text-white">Join Slot {selectedSlot.order}</h3>
              <button onClick={() => setBookingModalOpen(false)} className="text-gray-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleConfirm} className="space-y-4 mt-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Flat Number</label>
                <input
                  type="text"
                  required
                  value={flatNumber}
                  onChange={(e) => setFlatNumber(e.target.value)}
                  placeholder="e.g. 803"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Household Size</label>
                <select
                  value={householdSize}
                  onChange={(e) => setHouseholdSize(e.target.value)}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white cursor-pointer"
                >
                  <option value="1-2 members">1–2 members</option>
                  <option value="3-4 members">3–4 members</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Dietary Preference</label>
                <select
                  value={dietaryPref}
                  onChange={(e) => setDietaryPref(e.target.value)}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white cursor-pointer"
                >
                  <option value="Pure Veg">Pure Veg</option>
                  <option value="Jain">Jain</option>
                  <option value="Non-Veg">Non-Veg</option>
                </select>
              </div>

              <div className="flex items-center gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setBookingModalOpen(false)}
                  className="flex-1 py-2.5 rounded-xl bg-zinc-800 text-gray-300 text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl bg-brand-green text-white text-xs font-bold"
                >
                  Confirm & Join
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Working Manual Custom Slot Booking Option Modal */}
      {manualModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-[#151515] border border-amber-600/50 rounded-2xl max-w-lg w-full p-6 shadow-2xl relative space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <div className="flex items-center gap-2">
                <span className="w-2 h-2 rounded-full bg-brand-orange animate-pulse"></span>
                <h3 className="text-base font-bold text-white">Manual Custom Slot & Guest Meal Option</h3>
              </div>
              <button onClick={() => setManualModalOpen(false)} className="text-gray-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <p className="text-xs text-gray-400 leading-relaxed">
              Book a personalized manual slot outside regular pooling hours for guest dinners, Sunday feasts, or ad-hoc domestic cooking.
            </p>

            <form onSubmit={handleManualSubmit} className="space-y-3.5">
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">Target Flat</label>
                  <input
                    type="text"
                    required
                    value={manualFlat}
                    onChange={(e) => setManualFlat(e.target.value)}
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">Target Date</label>
                  <input
                    type="date"
                    required
                    value={manualDate}
                    onChange={(e) => setManualDate(e.target.value)}
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Custom Time Slot</label>
                <select
                  value={manualTime}
                  onChange={(e) => setManualTime(e.target.value)}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white cursor-pointer"
                >
                  <option value="1:00 PM – 1:45 PM (Special Lunch)">1:00 PM – 1:45 PM (Special Lunch)</option>
                  <option value="2:00 PM – 2:45 PM (Post-Lunch Clean Prep)">2:00 PM – 2:45 PM (Post-Lunch Prep)</option>
                  <option value="5:30 PM – 6:15 PM (Pre-Pool Early Dinner)">5:30 PM – 6:15 PM (Early Dinner)</option>
                  <option value="10:15 PM – 11:00 PM (Late Night Supper)">10:15 PM – 11:00 PM (Late Night Supper)</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Planned Special Dishes</label>
                <input
                  type="text"
                  required
                  value={manualDishes}
                  onChange={(e) => setManualDishes(e.target.value)}
                  placeholder="e.g. Biryani, Dal Makhani, Paneer Tikka"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Guest Scale</label>
                <select
                  value={manualGuests}
                  onChange={(e) => setManualGuests(e.target.value)}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white cursor-pointer"
                >
                  <option value="1-2 members">Family Regular (1–2 members)</option>
                  <option value="3-4 members">Family Regular (3–4 members)</option>
                  <option value="6-8 guests">Guest Dinner (6–8 guests)</option>
                  <option value="10+ guests">Party Feast (10+ guests)</option>
                </select>
              </div>

              <div className="flex items-center gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setManualModalOpen(false)}
                  className="flex-1 py-2.5 rounded-xl bg-zinc-800 text-gray-300 text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2.5 rounded-xl bg-brand-orange hover:bg-orange-600 text-white text-xs font-bold shadow-lg transition-all"
                >
                  Confirm Manual Booking
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

