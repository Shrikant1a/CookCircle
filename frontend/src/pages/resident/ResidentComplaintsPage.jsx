import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { MessageSquareWarning, Send, CheckCircle2, Clock, ShieldCheck } from 'lucide-react';

export default function ResidentComplaintsPage() {
  const { complaints, addComplaint } = useApp();

  const [category, setCategory] = useState('Punctuality');
  const [subject, setSubject] = useState('');
  const [description, setDescription] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    await addComplaint({
      category,
      subject,
      description,
      flatNumber: "402",
      residentName: "Aarav Sharma"
    });
    setSubject('');
    setDescription('');
  };

  return (
    <div className="space-y-6">
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Complaints & Feedback
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Submit feedback regarding taste, hygiene, punctuality, or billing to the Society Facility Management.
        </p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Ticket Form */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl lg:col-span-1">
          <h3 className="text-base font-bold text-white mb-4">File a New Ticket</h3>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Issue Category</label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-green cursor-pointer"
              >
                <option value="Punctuality">Punctuality / Late Arrival</option>
                <option value="Taste">Taste / Spice / Recipe Preference</option>
                <option value="Hygiene">Hygiene & Kitchen Cleanliness</option>
                <option value="Billing">Billing & Invoicing Issue</option>
                <option value="Other">Other Query</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Subject</label>
              <input
                type="text"
                required
                value={subject}
                onChange={(e) => setSubject(e.target.value)}
                placeholder="Brief summary of the issue..."
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-green"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Detailed Description</label>
              <textarea
                rows="4"
                required
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Provide details for the society facility manager..."
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-green"
              ></textarea>
            </div>

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg transition-all flex items-center justify-center gap-1.5"
            >
              <Send className="w-3.5 h-3.5" />
              <span>Submit Ticket</span>
            </button>
          </form>
        </div>

        {/* Existing Tickets History */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-xl lg:col-span-2 space-y-4">
          <h3 className="text-base font-bold text-white">Your Ticket History</h3>
          <div className="space-y-3">
            {complaints.map((c) => (
              <div key={c.id} className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 space-y-2">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-bold text-white">{c.subject}</span>
                    <span className="text-[10px] px-2 py-0.5 rounded bg-zinc-800 text-gray-400">
                      {c.category}
                    </span>
                  </div>
                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded ${
                    c.status === 'RESOLVED' ? 'bg-emerald-500/20 text-emerald-300' :
                    c.status === 'IN_PROGRESS' ? 'bg-blue-500/20 text-blue-300' :
                    'bg-amber-500/20 text-amber-300'
                  }`}>
                    {c.status}
                  </span>
                </div>
                <p className="text-xs text-gray-400 leading-relaxed">{c.description}</p>
                {c.adminNotes && (
                  <div className="p-2.5 rounded-lg bg-[#111] border border-zinc-800 text-[11px] text-gray-300">
                    <strong className="text-brand-greenLight">Facility Admin Note:</strong> {c.adminNotes}
                  </div>
                )}
                <span className="text-[10px] text-gray-500 block">Filed on: {c.date}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

