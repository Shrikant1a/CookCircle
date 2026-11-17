import React from 'react';
import { Building, Clock, ChefHat, CheckCircle, ShieldAlert, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function HowItWorksPage() {
  const steps = [
    {
      num: "01",
      title: "Select Your Society & Tower",
      desc: "Pick your residential society (e.g. Hiranandani Gardens) and your tower wing to view active cooking pools already formed by your neighbors."
    },
    {
      num: "02",
      title: "Choose Your 45-Minute Slot",
      desc: "Select an available evening slot (e.g. 6:30 PM, 7:15 PM, 8:00 PM), input your flat number, dietary preferences (Veg/Jain/Non-Veg), and household size."
    },
    {
      num: "03",
      title: "Cook Prepares Meals in Your Kitchen",
      desc: "Your verified cook arrives at your apartment on time. Share your 4-digit arrival OTP to start the 45-minute cooking window for fresh hot food."
    },
    {
      num: "04",
      title: "Zero Disruption Standby Guarantee",
      desc: "If your primary cook falls ill or takes leave, the platform automatically dispatches an on-call Standby Float Cook to your tower."
    }
  ];

  return (
    <div className="bg-[#0A0A0A] text-gray-100 py-16 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto">
      <div className="text-center max-w-3xl mx-auto mb-16">
        <h1 className="text-3xl sm:text-5xl font-extrabold text-white tracking-tight">
          How Tower Cook Pooling Works
        </h1>
        <p className="mt-4 text-sm sm:text-base text-gray-400">
          Four simple steps to save 70% on monthly home cooks without compromising on hygiene, quality, or reliability.
        </p>
      </div>

      <div className="space-y-6 max-w-3xl mx-auto">
        {steps.map((s, idx) => (
          <div key={idx} className="flex items-start gap-5 p-6 rounded-2xl bg-[#151515] border border-zinc-800">
            <span className="text-2xl font-black text-brand-greenLight font-mono bg-zinc-900 border border-zinc-700 w-12 h-12 rounded-xl flex items-center justify-center flex-shrink-0">
              {s.num}
            </span>
            <div>
              <h3 className="text-base font-bold text-white mb-1.5">{s.title}</h3>
              <p className="text-xs text-gray-400 leading-relaxed">{s.desc}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-16 text-center">
        <Link
          to="/resident"
          className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-sm shadow-xl transition-all"
        >
          <span>Try It in the Live Resident App</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
      </div>
    </div>
  );
}

