import React, { useState } from 'react';
import { Mail, Phone, MapPin, Send, CheckCircle2 } from 'lucide-react';
import { useToast } from '../../context/ToastContext';

export default function ContactPage() {
  const { showToast } = useToast();
  const [submitted, setSubmitted] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    society: '',
    message: ''
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    setSubmitted(true);
    showToast("Message submitted! A CookCircle community manager will reach out within 2 hours.", "success");
  };

  return (
    <div className="bg-[#0A0A0A] text-gray-100 py-16 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto">
      <div className="text-center max-w-2xl mx-auto mb-16">
        <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
          Bring CookCircle to Your Housing Society
        </h1>
        <p className="mt-3 text-sm text-gray-400">
          Are you a Managing Committee Member or a Resident? Contact us to launch verified shared cooking pools in your complex.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
        <div className="space-y-6">
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6">
            <h3 className="font-bold text-white text-base mb-4">Direct Contact Channels</h3>
            <div className="space-y-4 text-xs text-gray-300">
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-lg bg-emerald-950/60 text-brand-green flex items-center justify-center">
                  <Mail className="w-4 h-4" />
                </div>
                <div>
                  <p className="text-gray-400">General Enquiries</p>
                  <p className="font-medium text-white">hello@cookcircle.com</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-lg bg-emerald-950/60 text-brand-green flex items-center justify-center">
                  <Phone className="w-4 h-4" />
                </div>
                <div>
                  <p className="text-gray-400">Society Onboarding Helpline</p>
                  <p className="font-medium text-white">+91 1800 200 4829</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-8 h-8 rounded-lg bg-emerald-950/60 text-brand-green flex items-center justify-center">
                  <MapPin className="w-4 h-4" />
                </div>
                <div>
                  <p className="text-gray-400">Headquarters</p>
                  <p className="font-medium text-white">Bandra Kurla Complex (BKC), Mumbai, Maharashtra 400051</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Form */}
        <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 sm:p-8">
          {submitted ? (
            <div className="text-center py-10 space-y-3">
              <CheckCircle2 className="w-12 h-12 text-brand-green mx-auto" />
              <h3 className="text-lg font-bold text-white">Inquiry Received</h3>
              <p className="text-xs text-gray-400">
                Thank you! Our housing society onboarding team will coordinate with your estate manager.
              </p>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Your Full Name</label>
                <input
                  type="text"
                  required
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="e.g. Vikram Malhotra"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Email Address</label>
                <input
                  type="email"
                  required
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  placeholder="e.g. vikram@society.com"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Housing Society Name & City</label>
                <input
                  type="text"
                  required
                  value={formData.society}
                  onChange={(e) => setFormData({ ...formData, society: e.target.value })}
                  placeholder="e.g. Prestige Shantiniketan, Bengaluru"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Message or Questions</label>
                <textarea
                  rows="3"
                  required
                  value={formData.message}
                  onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                  placeholder="We have 3 towers interested in evening shift cook pooling..."
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
                ></textarea>
              </div>

              <button
                type="submit"
                className="w-full py-3 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg transition-all flex items-center justify-center gap-2"
              >
                <Send className="w-3.5 h-3.5" />
                <span>Submit Society Request</span>
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}

