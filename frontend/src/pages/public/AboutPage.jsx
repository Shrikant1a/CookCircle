import React from 'react';
import { ChefHat, ShieldCheck, HeartHandshake, Users, Sparkles, Building2 } from 'lucide-react';

export default function AboutPage() {
  return (
    <div className="bg-[#0A0A0A] text-gray-100 py-16 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto">
      <div className="text-center max-w-3xl mx-auto mb-16">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-zinc-900 border border-zinc-800 text-xs text-brand-greenLight font-semibold mb-4">
          <HeartHandshake className="w-3.5 h-3.5" />
          <span>Our Mission & Vision</span>
        </div>
        <h1 className="text-3xl sm:text-5xl font-extrabold text-white tracking-tight">
          Transforming Domestic Cooking into Dignified, Shared Infrastructure
        </h1>
        <p className="mt-4 text-base text-gray-400 leading-relaxed">
          CookCircle was born from a common struggle across residential communities in Mumbai, Bengaluru, and Pune:
          families paying exorbitant rates for unverified domestic help, while cooks faced informal wage bargaining and fragmented travel.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-16">
        <div className="p-6 rounded-2xl bg-[#151515] border border-zinc-800">
          <div className="w-10 h-10 rounded-xl bg-brand-green/20 text-brand-green flex items-center justify-center mb-4">
            <Building2 className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-white text-base">Hyperlocal Tower Pooling</h3>
          <p className="text-xs text-gray-400 mt-2 leading-relaxed">
            By grouping flats inside the same residential tower, cooks eliminate 2 hours of daily traffic delays and serve 5 flats in synchronized 45-minute windows.
          </p>
        </div>

        <div className="p-6 rounded-2xl bg-[#151515] border border-zinc-800">
          <div className="w-10 h-10 rounded-xl bg-brand-orange/20 text-brand-orange flex items-center justify-center mb-4">
            <ShieldCheck className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-white text-base">Standby Float Pool</h3>
          <p className="text-xs text-gray-400 mt-2 leading-relaxed">
            We retain dedicated standby cooks on full retainer so that when a primary cook falls sick or takes leave, zero households go without dinner.
          </p>
        </div>

        <div className="p-6 rounded-2xl bg-[#151515] border border-zinc-800">
          <div className="w-10 h-10 rounded-xl bg-indigo-500/20 text-indigo-400 flex items-center justify-center mb-4">
            <Users className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-white text-base">Dignified Cook Partner Safety</h3>
          <p className="text-xs text-gray-400 mt-2 leading-relaxed">
            Domestic cooks receive predictable monthly direct bank transfers (₹20,000+/mo), performance bonuses, accident coverage, and zero informal haggling.
          </p>
        </div>
      </div>
    </div>
  );
}

