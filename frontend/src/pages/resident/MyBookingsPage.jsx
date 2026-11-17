import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { Calendar, Clock, ChefHat, CheckCircle2, RefreshCw, AlertCircle, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function MyBookingsPage() {
  const { currentUser } = useAuth();
  const { slots, cooks, activeTowerCook, skipMeal } = useApp();

  const isDemoResident = currentUser?.email === 'resident@society.com';
  const hasSubscribed = isDemoResident || Boolean(currentUser?.hasSelectedCook && currentUser?.assignedCookId);
  const residentFlat = currentUser?.flatNumber || (isDemoResident ? '402' : '');

  const assignedCook = currentUser?.assignedCookId
    ? cooks.find(c => c.id === currentUser.assignedCookId) || activeTowerCook
    : activeTowerCook;

  const mySlot = slots.find(s => s.flatNumber && s.flatNumber === residentFlat)
    || (currentUser?.bookedSlotOrder ? slots.find(s => s.order === currentUser.bookedSlotOrder) : null)
    || (isDemoResident ? slots[0] : null);

  // Dynamic upcoming schedule starting from today
  const today = new Date();
  const getUpcomingDays = () => {
    const days = [];
    const mealDish = mySlot?.dishesPlanned || "Dal Tadka, Aloo Gobhi, Phulkas";
    for (let i = 0; i < 4; i++) {
      const d = new Date(today);
      d.setDate(today.getDate() + i);
      const label = i === 0 ? "Today" : i === 1 ? "Tomorrow" : d.toLocaleDateString('en-US', { weekday: 'long' });
      days.push({
        date: `${label}, ${d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}`,
        time: mySlot?.timeDisplay || "6:30 PM – 7:15 PM",
        status: i === 0 ? (mySlot?.status || "CONFIRMED") : "CONFIRMED",
        dish: i === 0 ? mealDish : i === 1 ? "Paneer Bhurji + Jeera Rice" : i === 2 ? "Chole Masala + Phulkas" : "Gujarati Kadi Khichdi"
      });
    }
    return days;
  };

  const scheduleDays = getUpcomingDays();

  return (
    <div className="space-y-6">
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          My Cooking Bookings & Schedule
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Review your daily cooking subscription, upcoming sessions, and skip refund records.
        </p>
      </div>

      {/* Fresh New User Empty State (Zero past history) */}
      {!hasSubscribed ? (
        <div className="p-12 text-center bg-[#151515] border border-zinc-800 rounded-3xl space-y-4 shadow-xl">
          <div className="w-16 h-16 rounded-2xl bg-zinc-900 border border-zinc-800 flex items-center justify-center text-emerald-400 mx-auto">
            <Calendar className="w-8 h-8" />
          </div>
          <div>
            <h3 className="text-xl font-bold text-white">No Booking History (Start Fresh)</h3>
            <p className="text-xs text-gray-400 max-w-md mx-auto mt-1 leading-relaxed">
              As a new resident of Flat {residentFlat || 'your flat'}, you haven't joined a cooking pool yet. Choose your preferred cook partner and select your daily 45-minute slot to begin your cooking schedule.
            </p>
          </div>
          <div className="pt-2">
            <Link
              to="/resident"
              className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg shadow-emerald-950/40 transition-all"
            >
              <ChefHat className="w-4 h-4" />
              <span>Choose Your Cook & Pick a Slot</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>
      ) : (
        /* Active Subscription Summary */
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-5">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-4 border-b border-zinc-800">
            <div>
              <div className="flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-brand-green animate-pulse"></span>
                <span className="text-xs font-bold text-brand-greenLight uppercase tracking-wider">
                  Active Monthly Subscription
                </span>
              </div>
              <h3 className="text-lg font-bold text-white mt-1">
                Evening Shift • {mySlot?.timeDisplay || '6:30 PM – 7:15 PM'} (Slot {mySlot?.order || 1})
              </h3>
              <p className="text-xs text-gray-400">
                Assigned Cook: <strong className="text-white">{assignedCook.name}</strong> • Flat {residentFlat || '402'}
              </p>
            </div>

            {mySlot && (
              <button
                onClick={() => skipMeal(mySlot.order)}
                disabled={mySlot.status === 'SKIPPED'}
                className={`px-4 py-2 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
                  mySlot.status === 'SKIPPED'
                    ? 'bg-zinc-800 text-gray-500 cursor-not-allowed'
                    : 'bg-zinc-900 border border-zinc-700 hover:border-amber-500 text-amber-300'
                }`}
              >
                <RefreshCw className="w-3.5 h-3.5" />
                <span>{mySlot.status === 'SKIPPED' ? 'Meal Skipped (₹140 Credited)' : 'Skip Today (+₹140 Credit)'}</span>
              </button>
            )}
          </div>

          {/* Schedule List */}
          <div className="mt-4 space-y-3">
            <h4 className="text-xs font-bold text-gray-300 uppercase tracking-wider">Upcoming Sessions:</h4>
            {scheduleDays.map((item, idx) => (
              <div key={idx} className="p-3.5 rounded-xl bg-zinc-900 border border-zinc-800 flex items-center justify-between text-xs">
                <div className="flex items-center gap-3">
                  <Calendar className="w-4 h-4 text-brand-green" />
                  <div>
                    <span className="font-bold text-white block">{item.date}</span>
                    <span className="text-gray-400 text-[11px]">{item.time} • {item.dish}</span>
                  </div>
                </div>

                <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                  item.status === 'SKIPPED' ? 'bg-amber-500/20 text-amber-300' :
                  item.status === 'COOKING' ? 'bg-emerald-500/20 text-emerald-300' :
                  'bg-zinc-800 text-gray-300'
                }`}>
                  {item.status}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
