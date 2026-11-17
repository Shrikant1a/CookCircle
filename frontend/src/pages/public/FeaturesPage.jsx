import React from 'react';
import { Clock, ShieldCheck, DollarSign, Smartphone, HeartHandshake, FileText, CheckCircle2, RefreshCw } from 'lucide-react';

export default function FeaturesPage() {
  const features = [
    {
      icon: Clock,
      title: "45-Minute Synchronized Shift Slots",
      desc: "5 consecutive evening slots (6:30 PM – 10:15 PM) and morning slots (7:00 AM – 10:00 AM) inside the same residential wing.",
      color: "text-emerald-400",
      bg: "bg-emerald-950/40"
    },
    {
      icon: ShieldCheck,
      title: "Automated Standby Cook Dispatch",
      desc: "Instant algorithm-driven assignment of standby float cooks during approved leaves with zero meal disruption.",
      color: "text-amber-400",
      bg: "bg-amber-950/40"
    },
    {
      icon: RefreshCw,
      title: "1-Click Skip Meal & Wallet Credits",
      desc: "Skip dinner slots 3 hours prior and receive ₹140 credit back to your CookCircle wallet, automatically deducted from monthly bills.",
      color: "text-brand-orange",
      bg: "bg-orange-950/40"
    },
    {
      icon: Smartphone,
      title: "Vernacular Cook Partner App (हिंदी / EN)",
      desc: "Simplified Hindi/English bilingual mobile interface for domestic cooks with daily flat itineraries and 4-digit arrival OTPs.",
      color: "text-blue-400",
      bg: "bg-blue-950/40"
    },
    {
      icon: DollarSign,
      title: "Razorpay Integrated Automated Invoicing",
      desc: "Monthly itemized invoices, UPI/Card payment integration in test mode, and backend HMAC-SHA256 signature verification.",
      color: "text-purple-400",
      bg: "bg-purple-950/40"
    },
    {
      icon: FileText,
      title: "60-Min Pre-Cook Prep Checklists",
      desc: "Smart prep reminders for residents (soak dal, knead atta, chop vegetables) to optimize the 45-minute cooking window.",
      color: "text-teal-400",
      bg: "bg-teal-950/40"
    }
  ];

  return (
    <div className="bg-[#0A0A0A] text-gray-100 py-16 px-4 sm:px-6 lg:px-8 max-w-6xl mx-auto">
      <div className="text-center max-w-3xl mx-auto mb-16">
        <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
          Comprehensive Feature Matrix
        </h1>
        <p className="mt-4 text-sm text-gray-400">
          Built with React.js, Tailwind CSS, Spring Boot 3, and MySQL to deliver an enterprise-grade SaaS experience for housing societies.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {features.map((feat, i) => {
          const Icon = feat.icon;
          return (
            <div key={i} className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 hover:border-zinc-700 transition-all">
              <div className={`w-12 h-12 rounded-xl ${feat.bg} flex items-center justify-center ${feat.color} mb-4`}>
                <Icon className="w-6 h-6" />
              </div>
              <h3 className="text-base font-bold text-white mb-2">{feat.title}</h3>
              <p className="text-xs text-gray-400 leading-relaxed">{feat.desc}</p>
            </div>
          );
        })}
      </div>
    </div>
  );
}

