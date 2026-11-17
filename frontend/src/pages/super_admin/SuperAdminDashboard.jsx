import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import {
  Shield,
  Building2,
  Users,
  ChefHat,
  Plus,
  Activity,
  CheckCircle2,
  DollarSign,
  TrendingUp,
  X
} from 'lucide-react';

export default function SuperAdminDashboard() {
  const { societies } = useAuth();
  const { showToast } = useToast();

  const [societyList, setSocietyList] = useState(societies);
  const [modalOpen, setModalOpen] = useState(false);
  const [newSocName, setNewSocName] = useState('');
  const [newSocCode, setNewSocCode] = useState('');
  const [newSocCity, setNewSocCity] = useState('Pune');
  const [newAdminEmail, setNewAdminEmail] = useState('');

  const handleCreateSociety = (e) => {
    e.preventDefault();
    const newSoc = {
      id: Date.now(),
      name: newSocName,
      code: newSocCode.toUpperCase(),
      city: newSocCity,
      state: "Maharashtra",
      area: "Hadapsar",
      towers: [
        { id: Date.now() + 1, name: "Tower 1", floors: 20, activePools: 1 }
      ],
      basePoolingRate: 4200,
      soloRate: 15000,
      skipMealCredit: 140
    };
    setSocietyList([...societyList, newSoc]);
    setModalOpen(false);
    showToast(`Society "${newSocName}" provisioned! Admin credentials emailed to ${newAdminEmail}.`, 'success');
  };

  const auditLogs = [
    { id: 1, action: "STANDBY_DISPATCH_TRIGGERED", details: "Standby Cook Ramesh Kumar assigned to Tower B (Sunita Devi on leave)", time: "10 mins ago" },
    { id: 2, action: "RAZORPAY_SIGNATURE_VERIFIED", details: "Payment order_RzpTest987123 verified via HMAC-SHA256 for Flat 402", time: "25 mins ago" },
    { id: 3, action: "SOCIETY_REGISTERED", details: "Sobha Dream Acres provisioned with 3 towers by SuperAdmin", time: "2 hours ago" },
    { id: 4, action: "COOK_VERIFICATION_APPROVED", details: "Aadhaar & Police clearance validated for Meena Bai", time: "5 hours ago" }
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight flex items-center gap-2">
            <Shield className="w-7 h-7 text-purple-400" />
            <span>Super Admin Governance Console</span>
          </h1>
          <p className="text-xs text-gray-400 mt-1">
            Global Platform Command • Housing Societies Directory, Admins & Audit Logs
          </p>
        </div>

        <button
          onClick={() => setModalOpen(true)}
          className="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs shadow-lg transition-all flex items-center gap-1.5"
        >
          <Plus className="w-4 h-4" />
          <span>Provision New Housing Society</span>
        </button>
      </div>

      {/* Global Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-[#151515] border border-zinc-800">
          <span className="text-[10px] text-gray-400 uppercase font-bold tracking-wider block">Registered Societies</span>
          <div className="text-2xl font-black text-white mt-1">{societyList.length} Complexes</div>
          <p className="text-[11px] text-gray-500 mt-1">Mumbai, Bengaluru, Pune</p>
        </div>

        <div className="p-5 rounded-2xl bg-[#151515] border border-zinc-800">
          <span className="text-[10px] text-gray-400 uppercase font-bold tracking-wider block">Total Active Towers</span>
          <div className="text-2xl font-black text-white mt-1">18 Towers</div>
          <p className="text-[11px] text-gray-500 mt-1">360+ Residential Flats</p>
        </div>

        <div className="p-5 rounded-2xl bg-[#151515] border border-zinc-800">
          <span className="text-[10px] text-gray-400 uppercase font-bold tracking-wider block">Active Cook Partners</span>
          <div className="text-2xl font-black text-brand-orangeLight mt-1">24 Cooks</div>
          <p className="text-[11px] text-gray-500 mt-1">100% Aadhaar & Police Verified</p>
        </div>

        <div className="p-5 rounded-2xl bg-[#151515] border border-zinc-800">
          <span className="text-[10px] text-gray-400 uppercase font-bold tracking-wider block">Platform Gross Volume</span>
          <div className="text-2xl font-black text-brand-greenLight mt-1">₹7,56,000</div>
          <p className="text-[11px] text-emerald-400 mt-1">100% digital collection</p>
        </div>
      </div>

      {/* Societies Directory */}
      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-4">
        <h3 className="font-bold text-white text-base">Housing Society Master Directory</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-zinc-900 text-gray-400 uppercase font-bold text-[10px]">
              <tr>
                <th className="p-3.5 rounded-l-lg">Society Name</th>
                <th className="p-3.5">Code</th>
                <th className="p-3.5">City & State</th>
                <th className="p-3.5">Towers</th>
                <th className="p-3.5">Pooling Base Rate</th>
                <th className="p-3.5">Skip Credit</th>
                <th className="p-3.5 rounded-r-lg text-right">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-800/60">
              {societyList.map((soc) => (
                <tr key={soc.id} className="hover:bg-zinc-900/40">
                  <td className="p-3.5 font-bold text-white">{soc.name}</td>
                  <td className="p-3.5 font-mono text-gray-300">{soc.code}</td>
                  <td className="p-3.5 text-gray-400">{soc.city}, {soc.state}</td>
                  <td className="p-3.5 font-semibold text-white">{soc.towers?.length || 2} Towers</td>
                  <td className="p-3.5 font-bold text-brand-greenLight">₹{soc.basePoolingRate}/mo</td>
                  <td className="p-3.5 text-emerald-400">₹{soc.skipMealCredit}</td>
                  <td className="p-3.5 text-right">
                    <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-brand-green/20 text-brand-greenLight">
                      ACTIVE
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Platform Activity & Audit Logs */}
      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl space-y-4">
        <div className="flex items-center gap-2">
          <Activity className="w-5 h-5 text-purple-400" />
          <h3 className="font-bold text-white text-base">System Audit & Dispatch Stream</h3>
        </div>

        <div className="divide-y divide-zinc-800/80">
          {auditLogs.map((log) => (
            <div key={log.id} className="py-3 flex items-start justify-between text-xs gap-4">
              <div>
                <span className="font-mono text-purple-300 font-bold block">{log.action}</span>
                <p className="text-gray-400 mt-0.5">{log.details}</p>
              </div>
              <span className="text-[11px] text-gray-500 whitespace-nowrap">{log.time}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Provision Society Modal */}
      {modalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 animate-fade-in">
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl max-w-md w-full p-6 shadow-2xl relative">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <h3 className="text-sm font-bold text-white">Provision Housing Society</h3>
              <button onClick={() => setModalOpen(false)} className="text-gray-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateSociety} className="space-y-4 mt-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Society Name</label>
                <input
                  type="text"
                  required
                  value={newSocName}
                  onChange={(e) => setNewSocName(e.target.value)}
                  placeholder="e.g. Amanora Park Town"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">Society Code</label>
                  <input
                    type="text"
                    required
                    value={newSocCode}
                    onChange={(e) => setNewSocCode(e.target.value)}
                    placeholder="APT-PUNE"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">City</label>
                  <input
                    type="text"
                    required
                    value={newSocCity}
                    onChange={(e) => setNewSocCity(e.target.value)}
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Designated Society Admin Email</label>
                <input
                  type="email"
                  required
                  value={newAdminEmail}
                  onChange={(e) => setNewAdminEmail(e.target.value)}
                  placeholder="facility@amanora.com"
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
                  className="flex-1 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-bold"
                >
                  Authorize & Provision
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

