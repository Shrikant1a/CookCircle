import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth, ROLES } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { useToast } from '../../context/ToastContext';
import {
  ChefHat,
  ShieldCheck,
  Clock,
  Sparkles,
  ArrowRight,
  CheckCircle2,
  Users,
  Building2,
  CalendarCheck,
  TrendingUp,
  Percent,
  Star,
  ChevronDown,
  ChevronUp,
  Sliders,
  Lock,
  UserPlus,
  LogIn
} from 'lucide-react';

export default function LandingPage() {
  const { activeRole, isAuthenticated, currentUser } = useAuth();
  const { slots } = useApp();
  const { showToast } = useToast();
  const navigate = useNavigate();

  // Pricing calculator state: number of flats pooling
  const [flatCount, setFlatCount] = useState(4);
  const [faqOpen, setFaqOpen] = useState({ 0: true });

  const toggleFaq = (index) => {
    setFaqOpen(prev => ({ ...prev, [index]: !prev[index] }));
  };

  const soloCookPrice = 15000;
  const pooledPricePerFlat = flatCount >= 4 ? 4200 : flatCount === 3 ? 4800 : 5500;
  const monthlySavings = soloCookPrice - pooledPricePerFlat;
  const savingsPercent = Math.round((monthlySavings / soloCookPrice) * 100);

  const handleResidentAccess = () => {
    if (isAuthenticated && activeRole === ROLES.RESIDENT) {
      navigate('/resident');
    } else if (isAuthenticated) {
      showToast(`You are currently logged in as ${activeRole.replace('_', ' ')}. Please sign out to register or access Resident portal.`, 'info');
    } else {
      navigate('/register?role=RESIDENT');
    }
  };

  const handleCookAccess = () => {
    if (isAuthenticated && activeRole === ROLES.COOK) {
      navigate('/cook');
    } else if (isAuthenticated) {
      showToast(`You are currently logged in as ${activeRole.replace('_', ' ')}. Please sign out to access Cook Partner portal.`, 'info');
    } else {
      navigate('/register?role=COOK');
    }
  };

  const handleAdminAccess = () => {
    if (isAuthenticated && activeRole === ROLES.SOCIETY_ADMIN) {
      navigate('/admin');
    } else if (isAuthenticated) {
      showToast(`You are currently logged in as ${activeRole.replace('_', ' ')}. Please sign out to access Society Admin console.`, 'info');
    } else {
      navigate('/login?role=SOCIETY_ADMIN');
    }
  };

  const handleSuperAdminAccess = () => {
    if (isAuthenticated && activeRole === ROLES.SUPER_ADMIN) {
      navigate('/super-admin');
    } else if (isAuthenticated) {
      showToast(`You are currently logged in as ${activeRole.replace('_', ' ')}. Please sign out to access Super Admin console.`, 'info');
    } else {
      navigate('/login?role=SUPER_ADMIN');
    }
  };

  return (
    <div className="bg-[#0A0A0A] text-gray-100 overflow-hidden">
      {/* 1. HERO SECTION */}
      <section className="relative pt-12 pb-20 md:pt-20 md:pb-32 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
        {/* Background Ambient Glow */}
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 w-96 h-96 bg-brand-green/10 rounded-full blur-3xl pointer-events-none"></div>

        <div className="text-center max-w-3xl mx-auto relative z-10">
          <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-zinc-900 border border-zinc-800 text-xs text-brand-greenLight font-medium mb-6">
            <Sparkles className="w-3.5 h-3.5 text-brand-greenLight" />
            <span>Hyperlocal Housing-Society Shared Cooking Platform</span>
          </div>

          <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold tracking-tight text-white leading-[1.15]">
            Your Society. Your Cook.{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-brand-green via-emerald-400 to-teal-300">
              Your Schedule.
            </span>
          </h1>

          <p className="mt-6 text-lg sm:text-xl text-gray-400 max-w-2xl mx-auto leading-relaxed">
            Ghar ka khana, shared with your society. Pool 4 to 5 flats in your tower to share a verified domestic cook across consecutive 45-minute slots — saving over 70% with guaranteed backup replacements.
          </p>

          {/* Action CTAs */}
          <div className="mt-10 flex flex-wrap items-center justify-center gap-4">
            <button
              onClick={handleResidentAccess}
              className="px-7 py-3.5 rounded-xl font-bold text-white bg-brand-green hover:bg-emerald-600 transition-all duration-200 shadow-xl shadow-emerald-950/50 flex items-center gap-2 hover:scale-[1.02] active:scale-[0.98]"
            >
              <span>{isAuthenticated && activeRole === ROLES.RESIDENT ? 'Open Resident App' : 'Join Your Tower Pool (Register)'}</span>
              <ArrowRight className="w-4 h-4" />
            </button>

            <button
              onClick={handleAdminAccess}
              className="px-7 py-3.5 rounded-xl font-semibold text-gray-300 hover:text-white bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 transition-all duration-200 flex items-center gap-2 hover:scale-[1.02]"
            >
              <Building2 className="w-4 h-4 text-indigo-400" />
              <span>Society Admin Portal</span>
            </button>
          </div>

          <div className="mt-4 flex items-center justify-center gap-2 text-[11px] text-gray-400">
            <Lock className="w-3.5 h-3.5 text-brand-greenLight" />
            <span>Registration compulsory for role-scoped features: Resident • Cook Partner • Society Admin • Super Admin</span>
          </div>

          {/* Trust Badges */}
          <div className="mt-12 flex flex-wrap items-center justify-center gap-6 text-xs text-gray-400">
            <div className="flex items-center gap-1.5">
              <ShieldCheck className="w-4 h-4 text-brand-green" />
              <span>Aadhaar & Police Verified</span>
            </div>
            <div className="flex items-center gap-1.5">
              <Clock className="w-4 h-4 text-brand-orange" />
              <span>Zero Disruption Standby Guarantee</span>
            </div>
            <div className="flex items-center gap-1.5">
              <Percent className="w-4 h-4 text-emerald-400" />
              <span>₹4,200/mo vs ₹15,000 Solo</span>
            </div>
          </div>
        </div>

        {/* 2. LIVE INTERACTIVE TOWER POOL PREVIEW CARD */}
        <div className="mt-16 max-w-4xl mx-auto bg-[#151515] border border-zinc-800 rounded-2xl p-6 shadow-2xl relative">
          <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-5 border-b border-zinc-800 gap-4">
            <div>
              <div className="flex items-center gap-2">
                <span className="w-2.5 h-2.5 rounded-full bg-brand-green animate-pulse"></span>
                <span className="text-xs font-semibold text-brand-greenLight uppercase tracking-wider">
                  Live Tower Timeline
                </span>
              </div>
              <h3 className="text-lg font-bold text-white mt-1">
                Hiranandani Gardens — Tower B (Evening Shift: 6:30 PM – 10:15 PM)
              </h3>
              <p className="text-xs text-gray-400">
                Primary Cook: <strong className="text-gray-200">Sunita Devi (4.9 ★)</strong> • Standby Backup: <strong className="text-gray-200">Ramesh Kumar (Ready)</strong>
              </p>
            </div>

            <div className="bg-zinc-900 border border-zinc-800 px-4 py-2 rounded-xl text-right">
              <span className="text-[10px] text-gray-400 uppercase font-bold block">Current Pool Rate</span>
              <span className="text-xl font-extrabold text-white">₹4,200</span>
              <span className="text-xs text-gray-400"> / flat / mo</span>
            </div>
          </div>

          {/* 5-Slot Horizontal Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-5 gap-3 mt-6">
            {slots.map((slot) => {
              const isOccupied = slot.isBooked;
              return (
                <div
                  key={slot.id}
                  className={`p-3.5 rounded-xl border text-left transition-all ${
                    isOccupied
                      ? 'bg-zinc-900/90 border-zinc-700/60'
                      : 'bg-emerald-950/20 border-emerald-800/40 hover:border-emerald-500/60'
                  }`}
                >
                  <div className="flex items-center justify-between text-xs mb-1.5">
                    <span className="font-bold text-gray-300">Slot {slot.order}</span>
                    <span className={`text-[10px] font-semibold px-1.5 py-0.5 rounded ${
                      isOccupied ? 'bg-zinc-800 text-gray-400' : 'bg-brand-green/20 text-brand-greenLight'
                    }`}>
                      {isOccupied ? 'Reserved' : 'Available'}
                    </span>
                  </div>

                  <p className="text-xs text-gray-300 font-medium">{slot.timeDisplay.split('–')[0]}</p>

                  <div className="mt-3 pt-2 border-t border-zinc-800/60">
                    {isOccupied ? (
                      <p className="text-[11px] font-semibold text-emerald-400">
                        Flat {slot.flatNumber}
                      </p>
                    ) : (
                      <button
                        onClick={handleResidentAccess}
                        className="text-[11px] font-semibold text-brand-greenLight hover:underline flex items-center gap-1"
                      >
                        Join Slot <ArrowRight className="w-3 h-3 inline" />
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>

          <div className="mt-4 pt-3 flex items-center justify-between text-xs text-gray-400">
            <span>Consecutive 45-minute cooking windows inside your tower (Zero transit delay).</span>
            <button
              onClick={handleResidentAccess}
              className="text-brand-greenLight font-semibold hover:underline"
            >
              Open Resident Portal →
            </button>
          </div>
        </div>
      </section>

      {/* 3. SAVINGS CALCULATOR SECTION */}
      <section className="py-16 bg-[#0E0E0E] border-y border-zinc-800/80 px-4 sm:px-6 lg:px-8">
        <div className="max-w-5xl mx-auto">
          <div className="text-center max-w-2xl mx-auto mb-12">
            <h2 className="text-3xl font-extrabold text-white tracking-tight">
              Calculate Your Tower Pool Savings
            </h2>
            <p className="mt-3 text-sm text-gray-400">
              Private home cooks cost ₹12,000–₹18,000/month for simple meals. By pooling 4–5 flats in the same tower, you get the exact same fresh home cooking at a fraction of the cost.
            </p>
          </div>

          <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 sm:p-10 shadow-xl grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
            {/* Slider Controls */}
            <div>
              <label className="block text-sm font-semibold text-white mb-2">
                Flats Sharing Cook in Your Tower: <span className="text-brand-greenLight text-lg font-bold">{flatCount} Flats</span>
              </label>
              <input
                type="range"
                min="2"
                max="5"
                step="1"
                value={flatCount}
                onChange={(e) => setFlatCount(Number(e.target.value))}
                className="w-full h-2 bg-zinc-800 rounded-lg appearance-none cursor-pointer accent-brand-green"
              />
              <div className="flex justify-between text-xs text-gray-500 mt-2 font-medium">
                <span>2 Flats (Solo Duo)</span>
                <span>3 Flats</span>
                <span>4 Flats</span>
                <span>5 Flats (Full Pool)</span>
              </div>

              <div className="mt-8 space-y-3">
                <div className="flex items-center gap-2 text-xs text-gray-300">
                  <CheckCircle2 className="w-4 h-4 text-brand-green flex-shrink-0" />
                  <span>Dedicated 45-minute cooking session per flat</span>
                </div>
                <div className="flex items-center gap-2 text-xs text-gray-300">
                  <CheckCircle2 className="w-4 h-4 text-brand-green flex-shrink-0" />
                  <span>Fresh rotis, dal, sabzi & rice cooked in your own kitchen</span>
                </div>
                <div className="flex items-center gap-2 text-xs text-gray-300">
                  <CheckCircle2 className="w-4 h-4 text-brand-green flex-shrink-0" />
                  <span>₹140 wallet credit automatically refunded when you skip a meal</span>
                </div>
              </div>
            </div>

            {/* Comparison Display */}
            <div className="bg-zinc-900 border border-zinc-800 p-6 rounded-xl space-y-4">
              <div className="flex justify-between items-center pb-3 border-b border-zinc-800">
                <span className="text-xs text-gray-400">Private Domestic Cook (Solo)</span>
                <span className="text-sm font-bold text-gray-400 line-through">₹{soloCookPrice.toLocaleString()}/mo</span>
              </div>

              <div className="flex justify-between items-center pb-3 border-b border-zinc-800">
                <div>
                  <span className="text-sm font-bold text-white block">CookCircle Tower Pool</span>
                  <span className="text-[11px] text-brand-greenLight font-medium">All-inclusive service + Standby replacement</span>
                </div>
                <span className="text-2xl font-extrabold text-brand-greenLight">₹{pooledPricePerFlat.toLocaleString()}/mo</span>
              </div>

              <div className="p-4 rounded-lg bg-emerald-950/40 border border-emerald-800/40 flex items-center justify-between">
                <div>
                  <span className="text-xs text-emerald-300 font-bold block">Annual Household Savings</span>
                  <span className="text-xs text-emerald-200/80">Save {savingsPercent}% every month</span>
                </div>
                <span className="text-xl font-black text-emerald-300">
                  ₹{((soloCookPrice - pooledPricePerFlat) * 12).toLocaleString()} / yr
                </span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* 4. CORE FEATURES SECTION */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
        <div className="text-center max-w-2xl mx-auto mb-16">
          <h2 className="text-3xl font-extrabold text-white tracking-tight">
            Engineered for Modern Apartment Societies
          </h2>
          <p className="mt-3 text-sm text-gray-400">
            Everything your housing society needs to replace informal cook negotiations with a verified digital subscription service.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {/* Card 1 */}
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 hover:border-zinc-700 transition-all hover:-translate-y-1">
            <div className="w-12 h-12 rounded-xl bg-emerald-950/60 border border-emerald-800/40 flex items-center justify-center text-brand-green mb-5">
              <Clock className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-white">Synchronized 45-Min Slots</h3>
            <p className="text-xs text-gray-400 mt-2 leading-relaxed">
              Consecutive fixed slots (6:30 PM – 10:15 PM) in the same tower wing eliminate cook transit time, enabling rapid flat-to-flat service.
            </p>
          </div>

          {/* Card 2 */}
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 hover:border-zinc-700 transition-all hover:-translate-y-1">
            <div className="w-12 h-12 rounded-xl bg-amber-950/60 border border-amber-800/40 flex items-center justify-center text-amber-400 mb-5">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-white">Automated Standby Dispatch</h3>
            <p className="text-xs text-gray-400 mt-2 leading-relaxed">
              When primary cooks take leave, our system instantly assigns standby float cooks (Ramesh Kumar) to ensure zero meal disruption.
            </p>
          </div>

          {/* Card 3 */}
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 hover:border-zinc-700 transition-all hover:-translate-y-1">
            <div className="w-12 h-12 rounded-xl bg-orange-950/60 border border-orange-800/40 flex items-center justify-center text-brand-orange mb-5">
              <CalendarCheck className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-white">4-Digit Arrival OTP & Timer</h3>
            <p className="text-xs text-gray-400 mt-2 leading-relaxed">
              Cooks check in with a resident OTP upon door arrival, starting a live 45-minute cooking window with automated attendance recording.
            </p>
          </div>

          {/* Card 4 */}
          <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 hover:border-zinc-700 transition-all hover:-translate-y-1">
            <div className="w-12 h-12 rounded-xl bg-indigo-950/60 border border-indigo-800/40 flex items-center justify-center text-indigo-400 mb-5">
              <TrendingUp className="w-6 h-6" />
            </div>
            <h3 className="text-base font-bold text-white">Skip Meal Wallet Credits</h3>
            <p className="text-xs text-gray-400 mt-2 leading-relaxed">
              Going out or dining with friends? 1-Click skip meal toggle automatically credits ₹140 into your wallet, deducted from your monthly invoice.
            </p>
          </div>
        </div>
      </section>

      {/* 5. FAQ SECTION */}
      <section className="py-16 bg-[#0D0D0D] border-t border-zinc-800/80 px-4 sm:px-6 lg:px-8">
        <div className="max-w-3xl mx-auto">
          <div className="text-center mb-12">
            <h2 className="text-2xl sm:text-3xl font-extrabold text-white">
              Frequently Asked Questions
            </h2>
            <p className="text-xs text-gray-400 mt-2">
              Everything you need to know about CookCircle housing society operations.
            </p>
          </div>

          <div className="space-y-3">
            {[
              {
                q: "How does CookCircle keep costs 70% lower than private home cooks?",
                a: "By grouping 4 to 5 apartments in the same residential tower, domestic cooks do not waste 1–2 hours commuting between distant societies. They serve flats back-to-back in 45-minute windows, earning ₹20,000+/month from a single building while reducing each flat's cost to ~₹4,200/month."
              },
              {
                q: "What happens when our primary cook goes on emergency leave?",
                a: "Our society management platform retains Standby Float Cooks (such as Ramesh Kumar). The moment a leave request is submitted, the system reassigns the tower's slots to the standby cook with zero meal disruption."
              },
              {
                q: "What if I am traveling or dining out on a specific day?",
                a: "Simply tap the 1-Click 'Skip Meal' button in the Resident App at least 3 hours prior. ₹140 will be credited directly to your CookCircle wallet and automatically subtracted from your monthly invoice."
              },
              {
                q: "Are the cooks background-verified and vaccinated?",
                a: "Yes. 100% of CookCircle culinary partners undergo biometric Aadhaar verification, local police verification checks, and health/vaccination screening before assignment to any society."
              }
            ].map((faq, index) => (
              <div key={index} className="bg-[#151515] border border-zinc-800 rounded-xl overflow-hidden">
                <button
                  onClick={() => toggleFaq(index)}
                  className="w-full p-4 text-left flex items-center justify-between text-sm font-semibold text-white hover:text-brand-greenLight transition-colors"
                >
                  <span>{faq.q}</span>
                  {faqOpen[index] ? <ChevronUp className="w-4 h-4 text-gray-400" /> : <ChevronDown className="w-4 h-4 text-gray-400" />}
                </button>
                {faqOpen[index] && (
                  <div className="px-4 pb-4 text-xs text-gray-400 leading-relaxed border-t border-zinc-800/50 pt-3">
                    {faq.a}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* 6. BOTTOM CTA */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-5xl mx-auto text-center">
        <div className="bg-gradient-to-br from-zinc-900 to-[#121212] border border-zinc-800 rounded-3xl p-10 sm:p-14 shadow-2xl relative overflow-hidden">
          <div className="absolute top-0 right-0 w-64 h-64 bg-brand-green/10 rounded-full blur-3xl pointer-events-none"></div>

          <h2 className="text-3xl sm:text-4xl font-extrabold text-white">
            Ready to simplify society cooking?
          </h2>
          <p className="mt-4 text-sm sm:text-base text-gray-400 max-w-xl mx-auto">
            Experience the interactive Resident App, Cook Partner portal, and Society Admin operations console right now.
          </p>

          <div className="mt-8 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 text-left">
            <button
              onClick={handleResidentAccess}
              className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 hover:border-emerald-500/50 hover:bg-emerald-950/20 transition-all group"
            >
              <div className="flex items-center gap-2 mb-1">
                <Users className="w-4 h-4 text-brand-greenLight" />
                <span className="text-xs font-bold text-white group-hover:text-brand-greenLight">Resident</span>
              </div>
              <p className="text-[11px] text-gray-400">Join your tower pool with flat-level slot booking.</p>
              <span className="text-[10px] text-brand-greenLight mt-2 inline-flex items-center gap-1 font-semibold">
                {isAuthenticated && activeRole === ROLES.RESIDENT ? 'Open App' : 'Register Flat'} <ArrowRight className="w-3 h-3" />
              </span>
            </button>

            <button
              onClick={handleCookAccess}
              className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 hover:border-orange-500/50 hover:bg-orange-950/20 transition-all group"
            >
              <div className="flex items-center gap-2 mb-1">
                <ChefHat className="w-4 h-4 text-brand-orangeLight" />
                <span className="text-xs font-bold text-white group-hover:text-brand-orangeLight">Cook Partner</span>
              </div>
              <p className="text-[11px] text-gray-400">Daily route, QR check-in & bonus earnings tracker.</p>
              <span className="text-[10px] text-brand-orangeLight mt-2 inline-flex items-center gap-1 font-semibold">
                {isAuthenticated && activeRole === ROLES.COOK ? 'Open App' : 'Register Cook'} <ArrowRight className="w-3 h-3" />
              </span>
            </button>

            <button
              onClick={handleAdminAccess}
              className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 hover:border-indigo-500/50 hover:bg-indigo-950/20 transition-all group"
            >
              <div className="flex items-center gap-2 mb-1">
                <Building2 className="w-4 h-4 text-indigo-400" />
                <span className="text-xs font-bold text-white group-hover:text-indigo-400">Society Admin</span>
              </div>
              <p className="text-[11px] text-gray-400">Tower management, standby float dispatch & MRR.</p>
              <span className="text-[10px] text-indigo-400 mt-2 inline-flex items-center gap-1 font-semibold">
                {isAuthenticated && activeRole === ROLES.SOCIETY_ADMIN ? 'Open Console' : 'Admin Login'} <ArrowRight className="w-3 h-3" />
              </span>
            </button>

            <button
              onClick={handleSuperAdminAccess}
              className="p-4 rounded-xl bg-zinc-900 border border-zinc-800 hover:border-purple-500/50 hover:bg-purple-950/20 transition-all group"
            >
              <div className="flex items-center gap-2 mb-1">
                <ShieldCheck className="w-4 h-4 text-purple-400" />
                <span className="text-xs font-bold text-white group-hover:text-purple-400">Super Admin</span>
              </div>
              <p className="text-[11px] text-gray-400">Platform-wide multi-society command & audits.</p>
              <span className="text-[10px] text-purple-400 mt-2 inline-flex items-center gap-1 font-semibold">
                {isAuthenticated && activeRole === ROLES.SUPER_ADMIN ? 'Open HQ' : 'HQ Login'} <ArrowRight className="w-3 h-3" />
              </span>
            </button>
          </div>
        </div>
      </section>
    </div>
  );
}

