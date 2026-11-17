import React from 'react';
import { useApp } from '../../context/AppContext';
import { ChefHat, ShieldCheck, Award, Phone, Calendar } from 'lucide-react';

export default function CookProfilePage() {
  const { activeTowerCook } = useApp();

  return (
    <div className="space-y-6 max-w-3xl mx-auto">
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Cook Partner Profile
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Verified Cook Partner Credentials & Society Gatekeeper RFID Badges.
        </p>
      </div>

      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 sm:p-8 shadow-xl space-y-6">
        <div className="flex items-center gap-5 pb-6 border-b border-zinc-800">
          <img
            src={activeTowerCook.photo}
            alt={activeTowerCook.name}
            className="w-20 h-20 rounded-2xl object-cover border-2 border-brand-orange"
          />
          <div>
            <div className="flex items-center gap-2">
              <h3 className="text-xl font-bold text-white">{activeTowerCook.name}</h3>
              <span className="px-2.5 py-0.5 rounded-full text-xs font-bold bg-amber-500/20 text-amber-300">
                ★ {activeTowerCook.rating} Rating
              </span>
            </div>
            <p className="text-xs text-gray-400 mt-1">{activeTowerCook.phone} • {activeTowerCook.assignedTowerName}</p>
            <div className="flex items-center gap-2 mt-3">
              <span className="px-2.5 py-0.5 rounded-lg text-[10px] font-bold bg-brand-green/20 text-brand-greenLight">
                Active Partner
              </span>
              <span className="px-2.5 py-0.5 rounded-lg text-[10px] font-bold bg-zinc-800 text-gray-300">
                {activeTowerCook.experienceYears} Years Exp
              </span>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
          <div className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 space-y-2">
            <span className="font-bold text-white flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-brand-green" /> Verification Badges
            </span>
            <ul className="space-y-1 text-gray-300">
              <li className="flex items-center gap-1.5">✓ Aadhaar Verified (UIDAI)</li>
              <li className="flex items-center gap-1.5">✓ Local Police Clearance Certificate</li>
              <li className="flex items-center gap-1.5">✓ Double Vaccinated (Covishield)</li>
            </ul>
          </div>

          <div className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 space-y-2">
            <span className="font-bold text-white flex items-center gap-2">
              <ChefHat className="w-4 h-4 text-brand-orange" /> Cuisines & Specialities
            </span>
            <p className="text-gray-300 leading-relaxed">
              {activeTowerCook.specialties.join(', ')}
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

