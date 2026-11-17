import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { useToast } from '../../context/ToastContext';
import { reportService } from '../../services';
import {
  Building2,
  DollarSign,
  TrendingUp,
  Clock,
  ShieldAlert,
  ChefHat,
  Users,
  CheckCircle2,
  AlertTriangle,
  Play,
  RotateCcw,
  Receipt,
  ArrowRight
} from 'lucide-react';
import { Link } from 'react-router-dom';

export default function SocietyAdminDashboard() {
  const { currentSociety, currentTower } = useAuth();
  const {
    slots,
    cooks,
    isBackupDispatched,
    simulatePrimaryCookLeave,
    resetBackupSimulation,
    invoices,
    complaints
  } = useApp();
  const { showToast } = useToast();

  const [liveKpis, setLiveKpis] = useState(null);

  useEffect(() => {
    async function loadKpis() {
      try {
        const kpis = await reportService.getDashboardKpis(currentSociety?.id || 1);
        if (kpis) setLiveKpis(kpis);
      } catch (e) {
        // Fallback to local computed metrics
      }
    }
    loadKpis();
  }, [currentSociety?.id]);

  const primaryCook = cooks.find(c => c.id === 1);
  const standbyCook = cooks.find(c => c.id === 2);

  const totalFlats = liveKpis?.totalFlats || 40;
  const subscribedFlats = liveKpis?.activeResidents || (slots.filter(s => s.isBooked).length + 35);
  const mrr = liveKpis?.monthlyRecurringRevenue || 168000;
  const utilization = liveKpis?.poolUtilizationRate || 94.5;
  const onTimeSla = liveKpis?.onTimeArrivalSla || 98.2;

  return (
    <div className="space-y-6">
      {/* 1. TOP HEADER & SOCIETY IDENTIFIER */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            Society Operations & Dispatch Console
          </h1>
          <p className="text-xs text-gray-400 mt-1">
            {currentSociety.name} ({currentSociety.code}) • Estate Management Office
          </p>
        </div>

        <div className="flex items-center gap-2">
          <span className="px-3 py-1.5 rounded-xl bg-zinc-900 border border-zinc-800 text-xs text-gray-300">
            Active Community: <strong className="text-white font-bold">{currentSociety.name}</strong>
          </span>
        </div>
      </div>

      {/* 2. AUTOMATED BACKUP COOK DISPATCH SIMULATOR (CORE SPEC REQUIREMENT) */}
      <div className={`p-6 rounded-2xl border transition-all ${
        isBackupDispatched
          ? 'bg-amber-950/30 border-amber-500/70 shadow-2xl'
          : 'bg-[#151515] border-zinc-800 shadow-xl'
      }`}>
        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
          <div className="space-y-2 max-w-2xl">
            <div className="flex items-center gap-2">
              <ShieldAlert className={`w-5 h-5 ${isBackupDispatched ? 'text-amber-400' : 'text-brand-orange'}`} />
              <h2 className="text-base font-extrabold text-white tracking-tight">
                Automated Standby Cook Dispatch Simulator
              </h2>
              {isBackupDispatched && (
                <span className="px-2.5 py-0.5 rounded-full text-[10px] font-extrabold bg-amber-500/20 text-amber-300 border border-amber-500/40 animate-pulse">
                  Simulation Active
                </span>
              )}
            </div>
            <p className="text-xs text-gray-300 leading-relaxed">
              Test what happens when Primary Cook <strong className="text-white">{primaryCook?.name || 'Sunita Devi'}</strong> takes emergency leave.
              The dispatch engine matches Standby Float Cook <strong className="text-white">{standbyCook?.name || 'Ramesh Kumar'}</strong> (Standby Rapid Pool) and reassigns all 5 slots of Tower B with zero meal disruption.
            </p>
          </div>

          <div className="flex items-center gap-3 w-full md:w-auto">
            {!isBackupDispatched ? (
              <button
                onClick={simulatePrimaryCookLeave}
                className="w-full md:w-auto px-5 py-3 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold text-xs shadow-lg transition-all flex items-center justify-center gap-2 hover:scale-[1.02]"
              >
                <Play className="w-4 h-4 fill-current" />
                <span>Simulate Primary Cook Leave</span>
              </button>
            ) : (
              <button
                onClick={resetBackupSimulation}
                className="w-full md:w-auto px-5 py-3 rounded-xl bg-zinc-800 hover:bg-zinc-700 text-gray-200 font-bold text-xs border border-zinc-700 transition-all flex items-center justify-center gap-2"
              >
                <RotateCcw className="w-4 h-4 text-emerald-400" />
                <span>Reset Simulation</span>
              </button>
            )}
          </div>
        </div>

        {/* Verification badge banner if active */}
        {isBackupDispatched && (
          <div className="mt-5 pt-4 border-t border-amber-800/40 flex flex-col sm:flex-row items-start sm:items-center justify-between text-xs text-amber-200 gap-2">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-400 flex-shrink-0" />
              <span>
                <strong>Dispatched:</strong> {standbyCook?.name} (Standby Float Pool) covering Tower B Evening Shift (6:30 PM - 10:15 PM).
              </span>
            </div>
            <span className="px-3 py-1 rounded-lg bg-emerald-950/60 border border-emerald-600/50 text-emerald-300 font-bold text-[11px]">
              Backup Cook Assigned — Zero Meal Disruption
            </span>
          </div>
        )}
      </div>

      {/* 3. 4 KPI SUMMARY CARDS */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Card 1: Active Pools */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-5 shadow-lg">
          <div className="flex items-center justify-between text-gray-400 mb-2">
            <span className="text-xs font-bold uppercase tracking-wider">Active Tower Pools</span>
            <Building2 className="w-4 h-4 text-brand-green" />
          </div>
          <div className="text-2xl font-black text-white">8 Pools</div>
          <p className="text-[11px] text-gray-500 mt-1">Across 3 towers (Tower B, 4, C)</p>
        </div>

        {/* Card 2: MRR */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-5 shadow-lg">
          <div className="flex items-center justify-between text-gray-400 mb-2">
            <span className="text-xs font-bold uppercase tracking-wider">Monthly Recurring Rev</span>
            <DollarSign className="w-4 h-4 text-emerald-400" />
          </div>
          <div className="text-2xl font-black text-brand-greenLight">₹{mrr.toLocaleString()}</div>
          <p className="text-[11px] text-gray-500 mt-1">37 flats subscribed at ₹4,200/mo</p>
        </div>

        {/* Card 3: Cook Utilization */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-5 shadow-lg">
          <div className="flex items-center justify-between text-gray-400 mb-2">
            <span className="text-xs font-bold uppercase tracking-wider">Cook Utilization</span>
            <TrendingUp className="w-4 h-4 text-brand-orange" />
          </div>
          <div className="text-2xl font-black text-white">{utilization}%</div>
          <p className="text-[11px] text-emerald-400 mt-1">High efficiency (4.8 flats/shift)</p>
        </div>

        {/* Card 4: SLA On-Time % */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-5 shadow-lg">
          <div className="flex items-center justify-between text-gray-400 mb-2">
            <span className="text-xs font-bold uppercase tracking-wider">SLA On-Time %</span>
            <Clock className="w-4 h-4 text-blue-400" />
          </div>
          <div className="text-2xl font-black text-white">{onTimeSla}%</div>
          <p className="text-[11px] text-gray-500 mt-1">Average delay &lt; 2 minutes</p>
        </div>
      </div>

      {/* 4. TOWER B LIVE POOL STATUS TABLE */}
      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-4">
        <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
          <div>
            <h3 className="text-base font-bold text-white">Tower B — Evening Shift Slots Monitor</h3>
            <p className="text-xs text-gray-400">
              Active Cook: <strong className="text-white">{isBackupDispatched ? standbyCook?.name : primaryCook?.name}</strong> • 6:30 PM to 10:15 PM
            </p>
          </div>
          <Link
            to="/admin/slots"
            className="text-xs text-brand-greenLight hover:underline font-semibold flex items-center gap-1"
          >
            <span>Manage All Slots</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-zinc-900 text-gray-400 uppercase font-bold text-[10px]">
              <tr>
                <th className="p-3 rounded-l-lg">Slot</th>
                <th className="p-3">Timing</th>
                <th className="p-3">Flat</th>
                <th className="p-3">Resident</th>
                <th className="p-3">Dietary Pref</th>
                <th className="p-3">Status</th>
                <th className="p-3 rounded-r-lg text-right">Arrival OTP</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-800/60">
              {slots.map((s) => (
                <tr key={s.id} className="hover:bg-zinc-900/40">
                  <td className="p-3 font-bold text-white">Slot {s.order}</td>
                  <td className="p-3 text-gray-300 font-mono">{s.timeDisplay}</td>
                  <td className="p-3">
                    {s.isBooked ? (
                      <span className="font-bold text-emerald-400">Flat {s.flatNumber}</span>
                    ) : (
                      <span className="text-gray-500 italic">Open</span>
                    )}
                  </td>
                  <td className="p-3 text-gray-300">{s.residentName || '—'}</td>
                  <td className="p-3 text-gray-400">{s.dietaryPref || '—'}</td>
                  <td className="p-3">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      s.status === 'COMPLETED' ? 'bg-zinc-800 text-emerald-400' :
                      s.status === 'COOKING' ? 'bg-emerald-500/20 text-emerald-300' :
                      s.status === 'SKIPPED' ? 'bg-amber-500/20 text-amber-300' :
                      s.isBooked ? 'bg-zinc-800 text-gray-300' : 'bg-brand-green/20 text-brand-greenLight'
                    }`}>
                      {s.status}
                    </span>
                  </td>
                  <td className="p-3 text-right font-mono font-bold text-gray-300">
                    {s.arrivalOtp || '—'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* 5. QUICK OPERATIONS TILES */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Link
          to="/admin/cooks"
          className="p-5 rounded-xl bg-[#151515] border border-zinc-800 hover:border-zinc-700 transition-all group"
        >
          <div className="flex items-center justify-between mb-2">
            <span className="font-bold text-white text-sm group-hover:text-brand-greenLight">Cook Directory</span>
            <ChefHat className="w-5 h-5 text-brand-orange" />
          </div>
          <p className="text-xs text-gray-400">
            3 verified cooks onboarded, 1 standby float cook in reserve.
          </p>
        </Link>

        <Link
          to="/admin/invoices"
          className="p-5 rounded-xl bg-[#151515] border border-zinc-800 hover:border-zinc-700 transition-all group"
        >
          <div className="flex items-center justify-between mb-2">
            <span className="font-bold text-white text-sm group-hover:text-brand-greenLight">Invoices & Collections</span>
            <Receipt className="w-5 h-5 text-emerald-400" />
          </div>
          <p className="text-xs text-gray-400">
            ₹1,68,000 monthly target. 92% paid online via Razorpay.
          </p>
        </Link>

        <Link
          to="/admin/complaints"
          className="p-5 rounded-xl bg-[#151515] border border-zinc-800 hover:border-zinc-700 transition-all group"
        >
          <div className="flex items-center justify-between mb-2">
            <span className="font-bold text-white text-sm group-hover:text-brand-greenLight">Resident Tickets</span>
            <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-amber-500/20 text-amber-300">
              {complaints.filter(c => c.status !== 'RESOLVED').length} Open
            </span>
          </div>
          <p className="text-xs text-gray-400">
            Review punctuality and taste feedback from flat residents.
          </p>
        </Link>
      </div>
    </div>
  );
}

