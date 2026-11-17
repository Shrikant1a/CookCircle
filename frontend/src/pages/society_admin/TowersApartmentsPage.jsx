import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { Building2, Plus, Users, Home, CheckCircle2, X } from 'lucide-react';

export default function TowersApartmentsPage() {
  const { currentSociety } = useAuth();
  const { showToast } = useToast();

  const [towers, setTowers] = useState(currentSociety.towers || []);
  const [modalOpen, setModalOpen] = useState(false);
  const [towerName, setTowerName] = useState('');
  const [floorCount, setFloorCount] = useState(20);

  const handleAddTower = (e) => {
    e.preventDefault();
    const newT = {
      id: Date.now(),
      name: towerName,
      floors: Number(floorCount),
      activePools: 1
    };
    setTowers([...towers, newT]);
    setModalOpen(false);
    setTowerName('');
    showToast(`Tower "${towerName}" registered under ${currentSociety.name}!`, 'success');
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            Towers & Apartment Directory
          </h1>
          <p className="text-xs text-gray-400 mt-1">
            {currentSociety.name} • Manage residential wings, floors, and flat mappings.
          </p>
        </div>

        <button
          onClick={() => setModalOpen(true)}
          className="px-4 py-2 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg transition-all flex items-center gap-1.5"
        >
          <Plus className="w-4 h-4" />
          <span>Add New Tower</span>
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {towers.map((t) => (
          <div key={t.id} className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <div className="flex items-center gap-2.5">
                <div className="w-9 h-9 rounded-xl bg-indigo-500/20 text-indigo-400 flex items-center justify-center">
                  <Building2 className="w-5 h-5" />
                </div>
                <h3 className="text-base font-bold text-white">{t.name}</h3>
              </div>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-brand-green/20 text-brand-greenLight">
                {t.activePools} Pools Active
              </span>
            </div>

            <div className="space-y-2 text-xs text-gray-300">
              <div className="flex justify-between">
                <span className="text-gray-400">Total Floors:</span>
                <span className="font-bold text-white">{t.floors} Floors</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Total Apartments:</span>
                <span className="font-bold text-white">{t.floors * 4} Flats</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Pool Subscribed Flats:</span>
                <span className="font-bold text-emerald-400">5 Flats / Pool</span>
              </div>
            </div>

            <div className="pt-3 border-t border-zinc-800/80 flex items-center justify-between text-xs text-gray-400">
              <span>Gatekeeper RFID: Enabled</span>
              <span className="text-brand-greenLight font-semibold">Active</span>
            </div>
          </div>
        ))}
      </div>

      {/* Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl max-w-sm w-full p-6 shadow-2xl relative">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <h3 className="text-sm font-bold text-white">Add New Tower</h3>
              <button onClick={() => setModalOpen(false)} className="text-gray-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddTower} className="space-y-4 mt-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Tower / Wing Name</label>
                <input
                  type="text"
                  required
                  value={towerName}
                  onChange={(e) => setTowerName(e.target.value)}
                  placeholder="e.g. Tower D - Horizon"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Number of Floors</label>
                <input
                  type="number"
                  min="1"
                  max="60"
                  required
                  value={floorCount}
                  onChange={(e) => setFloorCount(e.target.value)}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div className="flex items-center gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="flex-1 py-2 rounded-xl bg-zinc-800 text-gray-300 text-xs font-semibold"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="flex-1 py-2 rounded-xl bg-brand-green text-white text-xs font-bold"
                >
                  Save Tower
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

