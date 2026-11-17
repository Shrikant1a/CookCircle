import React from 'react';
import { useApp } from '../../context/AppContext';
import { Calendar, ShieldAlert, CheckCircle2, RotateCcw } from 'lucide-react';

export default function LeaveManagementAdminPage() {
  const { leaveRequests, isBackupDispatched, simulatePrimaryCookLeave, resetBackupSimulation } = useApp();

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            Leave Requests & Standby Coverage
          </h1>
          <p className="text-xs text-gray-400 mt-1">
            Review submitted cook leave applications and manage automated backup float cook assignments.
          </p>
        </div>

        <div className="flex items-center gap-2">
          {!isBackupDispatched ? (
            <button
              onClick={simulatePrimaryCookLeave}
              className="px-4 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-black font-extrabold text-xs shadow-lg transition-all"
            >
              Simulate Leave & Dispatch Backup
            </button>
          ) : (
            <button
              onClick={resetBackupSimulation}
              className="px-4 py-2 rounded-xl bg-zinc-800 hover:bg-zinc-700 text-gray-200 font-bold text-xs border border-zinc-700 transition-all flex items-center gap-1.5"
            >
              <RotateCcw className="w-3.5 h-3.5 text-emerald-400" />
              <span>Reset Standby Dispatch</span>
            </button>
          )}
        </div>
      </div>

      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-4">
        <div className="space-y-3">
          {leaveRequests.map((l) => (
            <div key={l.id} className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
              <div className="space-y-1">
                <div className="flex items-center gap-2">
                  <span className="font-bold text-white text-sm">{l.cookName}</span>
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-300">
                    {l.status}
                  </span>
                </div>
                <p className="text-xs text-gray-400">
                  Dates: <strong className="text-gray-200">{l.startDate} to {l.endDate}</strong> • Reason: {l.reason}
                </p>
                <div className="flex items-center gap-1.5 text-xs text-amber-300 font-semibold pt-1">
                  <ShieldAlert className="w-4 h-4 text-amber-400 flex-shrink-0" />
                  <span>Standby Replacement Assigned: {l.backupAssigned}</span>
                </div>
              </div>

              <div className="text-right">
                <span className="text-[10px] text-gray-500 block">Logged on: {l.createdDate}</span>
                <span className="text-[11px] font-bold text-emerald-400">Zero Disruption Confirmed</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

