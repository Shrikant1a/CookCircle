import React from 'react';
import { useApp } from '../../context/AppContext';
import { DollarSign, Award, Calendar, CheckCircle, Clock } from 'lucide-react';

export default function CookAttendancePage() {
  const { activeTowerCook } = useApp();

  const attendanceLog = [
    { date: "16 Nov 2026", flatsServed: "5 of 5", status: "ON_TIME", payout: "₹750" },
    { date: "15 Nov 2026", flatsServed: "5 of 5", status: "ON_TIME", payout: "₹750" },
    { date: "14 Nov 2026", flatsServed: "5 of 5", status: "ON_TIME", payout: "₹750" },
    { date: "13 Nov 2026", flatsServed: "5 of 5", status: "ON_TIME", payout: "₹750" },
    { date: "12 Nov 2026", flatsServed: "4 of 5 (1 flat skipped)", status: "ON_TIME", payout: "₹750 (Protected Base)" }
  ];

  return (
    <div className="space-y-6">
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Attendance & Payout Ledger
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Guaranteed monthly wages with zero informal deduction on resident skips.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-5 rounded-2xl bg-[#151515] border border-zinc-800">
          <span className="text-[10px] text-gray-400 uppercase font-bold block">November Accrued Wages</span>
          <span className="text-2xl font-black text-brand-greenLight mt-1 block">₹19,500</span>
          <span className="text-xs text-gray-500">Base salary guaranteed</span>
        </div>

        <div className="p-5 rounded-2xl bg-[#151515] border border-zinc-800">
          <span className="text-[10px] text-gray-400 uppercase font-bold block">Punctuality Score</span>
          <span className="text-2xl font-black text-white mt-1 block">99.2%</span>
          <span className="text-xs text-emerald-400 font-medium">Eligible for +₹1,000 bonus</span>
        </div>

        <div className="p-5 rounded-2xl bg-[#151515] border border-zinc-800">
          <span className="text-[10px] text-gray-400 uppercase font-bold block">Next Bank Transfer</span>
          <span className="text-2xl font-black text-white mt-1 block">01 Dec 2026</span>
          <span className="text-xs text-gray-500">Direct to SBI A/C ending **4821</span>
        </div>
      </div>

      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl">
        <h3 className="font-bold text-white text-base mb-4">Recent Daily Logs</h3>
        <div className="divide-y divide-zinc-800">
          {attendanceLog.map((log, idx) => (
            <div key={idx} className="py-3.5 flex items-center justify-between text-xs">
              <div className="flex items-center gap-3">
                <Calendar className="w-4 h-4 text-brand-orange" />
                <div>
                  <span className="font-bold text-white block">{log.date}</span>
                  <span className="text-gray-400 text-[11px]">{log.flatsServed}</span>
                </div>
              </div>
              <div className="text-right">
                <span className="font-bold text-brand-greenLight block">{log.payout}</span>
                <span className="text-[10px] text-gray-400">{log.status}</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

