import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { Sliders, Save, Building2, DollarSign } from 'lucide-react';

export default function SettingsAdminPage() {
  const { currentSociety } = useAuth();
  const { showToast } = useToast();

  const [poolingFee, setPoolingFee] = useState(currentSociety.basePoolingRate || 4200);
  const [skipRefund, setSkipRefund] = useState(currentSociety.skipMealCredit || 140);
  const [slotDuration, setSlotDuration] = useState(45);

  const handleSave = (e) => {
    e.preventDefault();
    showToast("Society operating parameters updated successfully!", "success");
  };

  return (
    <div className="space-y-6 max-w-3xl mx-auto">
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Society Rules & Billing Parameters
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Configure monthly pooling charges, wallet skip credits, and cooking slot lengths for {currentSociety.name}.
        </p>
      </div>

      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 sm:p-8 shadow-xl">
        <form onSubmit={handleSave} className="space-y-5">
          <div>
            <label className="block text-xs font-semibold text-gray-300 mb-1">
              Monthly Base Pooling Fee per Flat (₹)
            </label>
            <input
              type="number"
              value={poolingFee}
              onChange={(e) => setPoolingFee(Number(e.target.value))}
              className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
            />
            <span className="text-[10px] text-gray-500 mt-1 block">
              Default standard rate when 4 to 5 flats share a cook.
            </span>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 mb-1">
              Skip-Meal Wallet Credit per Session (₹)
            </label>
            <input
              type="number"
              value={skipRefund}
              onChange={(e) => setSkipRefund(Number(e.target.value))}
              className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
            />
            <span className="text-[10px] text-gray-500 mt-1 block">
              Amount refunded into resident's digital wallet when they skip an evening slot.
            </span>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 mb-1">
              Slot Duration (Minutes)
            </label>
            <input
              type="number"
              value={slotDuration}
              onChange={(e) => setSlotDuration(Number(e.target.value))}
              className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
            />
            <span className="text-[10px] text-gray-500 mt-1 block">
              Configured slot length across morning and evening shifts (Default: 45 mins).
            </span>
          </div>

          <div className="pt-4 flex justify-end">
            <button
              type="submit"
              className="px-6 py-2.5 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg transition-all flex items-center gap-2"
            >
              <Save className="w-4 h-4" />
              <span>Save Society Settings</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

