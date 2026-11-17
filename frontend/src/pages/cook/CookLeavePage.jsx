import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { useToast } from '../../context/ToastContext';
import { Calendar, History, ShieldAlert, CheckCircle2 } from 'lucide-react';

export default function CookLeavePage() {
  const { leaveRequests, submitLeaveRequest, activeTowerCook } = useApp();
  const { showToast } = useToast();

  const [startDate, setStartDate] = useState('2026-11-20');
  const [endDate, setEndDate] = useState('2026-11-20');
  const [reason, setReason] = useState('Doctor appointment');

  const handleSubmit = async (e) => {
    e.preventDefault();
    await submitLeaveRequest({
      cookId: activeTowerCook.id,
      startDate,
      endDate,
      reason
    });
    showToast("Leave request registered and standby replacement assigned!", "warning");
  };

  return (
    <div className="space-y-6">
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Leave Management & Standby Replacement
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Apply for scheduled leaves without stress — our automated Standby Float Cook pool covers your flats.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Form */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl lg:col-span-1">
          <h3 className="font-bold text-white text-base mb-4">Apply for Leave</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Start Date</label>
              <input
                type="date"
                required
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">End Date</label>
              <input
                type="date"
                required
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Reason</label>
              <textarea
                rows="3"
                required
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                placeholder="Reason for absence..."
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
              ></textarea>
            </div>

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-brand-orange hover:bg-orange-600 text-white font-bold text-xs shadow-lg transition-all"
            >
              Submit Leave Request
            </button>
          </form>
        </div>

        {/* History */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl lg:col-span-2 space-y-4">
          <h3 className="font-bold text-white text-base">Leave Records & Dispatch Status</h3>
          <div className="space-y-3">
            {leaveRequests.map((l) => (
              <div key={l.id} className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 space-y-2">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-white">
                    {l.startDate} to {l.endDate}
                  </span>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-300">
                    {l.status}
                  </span>
                </div>
                <p className="text-xs text-gray-400">{l.reason}</p>
                {l.backupAssigned && (
                  <div className="p-2.5 rounded-lg bg-amber-950/30 border border-amber-800/40 text-[11px] text-amber-200 flex items-center gap-2">
                    <ShieldAlert className="w-4 h-4 text-amber-400 flex-shrink-0" />
                    <span>Dispatched Standby Cook: <strong>{l.backupAssigned}</strong></span>
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

