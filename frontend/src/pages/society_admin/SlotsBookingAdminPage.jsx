import React from 'react';
import { useApp } from '../../context/AppContext';
import { Clock, CheckCircle2, User, KeyRound } from 'lucide-react';

export default function SlotsBookingAdminPage() {
  const { slots } = useApp();

  return (
    <div className="space-y-6">
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Tower Slot & Booking Schedules
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Monitor real-time slot occupancy, assigned flats, and arrival OTP tokens.
        </p>
      </div>

      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-4">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-zinc-900 text-gray-400 uppercase font-bold text-[10px]">
              <tr>
                <th className="p-3.5 rounded-l-lg">Slot</th>
                <th className="p-3.5">Time Interval</th>
                <th className="p-3.5">Assigned Flat</th>
                <th className="p-3.5">Resident Name</th>
                <th className="p-3.5">Dishes Planned</th>
                <th className="p-3.5">Status</th>
                <th className="p-3.5 rounded-r-lg text-right">Arrival OTP</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-800/60">
              {slots.map((s) => (
                <tr key={s.id} className="hover:bg-zinc-900/40">
                  <td className="p-3.5 font-bold text-white">Slot {s.order}</td>
                  <td className="p-3.5 font-mono text-gray-300">{s.timeDisplay}</td>
                  <td className="p-3.5">
                    {s.isBooked ? (
                      <span className="font-bold text-emerald-400">Flat {s.flatNumber}</span>
                    ) : (
                      <span className="text-gray-500 italic">Unbooked</span>
                    )}
                  </td>
                  <td className="p-3.5 text-gray-300">{s.residentName || '—'}</td>
                  <td className="p-3.5 text-gray-400 truncate max-w-xs">{s.dishesPlanned || '—'}</td>
                  <td className="p-3.5">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                      s.status === 'COMPLETED' ? 'bg-zinc-800 text-emerald-400' :
                      s.status === 'COOKING' ? 'bg-emerald-500/20 text-emerald-300' :
                      s.status === 'SKIPPED' ? 'bg-amber-500/20 text-amber-300' :
                      s.isBooked ? 'bg-zinc-800 text-gray-300' : 'bg-brand-green/20 text-brand-greenLight'
                    }`}>
                      {s.status}
                    </span>
                  </td>
                  <td className="p-3.5 text-right font-mono font-bold text-gray-300">
                    {s.arrivalOtp || '—'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

