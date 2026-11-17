import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { Calendar, Clock, MapPin, ChefHat, CheckCircle2 } from 'lucide-react';

export default function CookSchedulePage() {
  const { currentTower } = useAuth();
  const { slots, activeTowerCook } = useApp();

  const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

  return (
    <div className="space-y-6">
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Weekly Cook Partner Schedule
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Assigned to {currentTower.name} • Evening Shift (6:30 PM – 10:15 PM) • 5 consecutive flats
        </p>
      </div>

      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {days.map((day, idx) => (
            <div key={idx} className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 space-y-3">
              <div className="flex items-center justify-between pb-2 border-b border-zinc-800">
                <span className="font-bold text-white text-xs">{day}</span>
                <span className="text-[10px] font-semibold text-brand-greenLight bg-brand-green/20 px-2 py-0.5 rounded">
                  5 Flats Assigned
                </span>
              </div>

              <div className="space-y-1.5 text-[11px] text-gray-300">
                <div className="flex justify-between">
                  <span>6:30 PM – 7:15 PM</span>
                  <strong className="text-white">Flat 402</strong>
                </div>
                <div className="flex justify-between">
                  <span>7:15 PM – 8:00 PM</span>
                  <strong className="text-white">Flat 701</strong>
                </div>
                <div className="flex justify-between text-gray-500">
                  <span>8:00 PM – 8:45 PM</span>
                  <span>Flat 904</span>
                </div>
                <div className="flex justify-between text-gray-500">
                  <span>8:45 PM – 9:30 PM</span>
                  <span>Flat 1102</span>
                </div>
                <div className="flex justify-between text-gray-500">
                  <span>9:30 PM – 10:15 PM</span>
                  <span>Flat 1403</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

