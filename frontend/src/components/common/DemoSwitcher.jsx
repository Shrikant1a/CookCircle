import React from 'react';
import { useAuth, ROLES } from '../../context/AuthContext';
import { useLanguage } from '../../context/LanguageContext';
import { useApp } from '../../context/AppContext';
import { Users, ChefHat, Building2, Shield, Languages, AlertOctagon, Lock, LogOut, ArrowRight, UserPlus } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

export default function DemoSwitcher() {
  const { activeRole, currentUser, isAuthenticated, logout, currentSociety, setActiveSocietyId, societies } = useAuth();
  const { language, toggleLanguage } = useLanguage();
  const { isBackupDispatched } = useApp();
  const navigate = useNavigate();

  const handleSignOut = () => {
    logout();
    navigate('/login');
  };

  const getRoleDetails = () => {
    switch (activeRole) {
      case ROLES.RESIDENT:
        return {
          icon: Users,
          label: 'Resident Portal',
          subtext: `Flat ${currentUser?.flatNumber || '402'} • ${currentUser?.fullName || 'Aarav Sharma'}`,
          badgeClass: 'bg-brand-green/20 text-brand-greenLight border-brand-green/40',
          path: '/resident'
        };
      case ROLES.COOK:
        return {
          icon: ChefHat,
          label: 'Cook Partner Portal',
          subtext: `${currentUser?.fullName || 'Sunita Devi'} • Assigned Tower B`,
          badgeClass: 'bg-brand-orange/20 text-brand-orangeLight border-brand-orange/40',
          path: '/cook'
        };
      case ROLES.SOCIETY_ADMIN:
        return {
          icon: Building2,
          label: 'Society Admin Console',
          subtext: `${currentUser?.fullName || 'Vikram Malhotra'} • ${currentUser?.designation || 'Estate Ops'}`,
          badgeClass: 'bg-indigo-500/20 text-indigo-300 border-indigo-500/40',
          path: '/admin'
        };
      case ROLES.SUPER_ADMIN:
        return {
          icon: Shield,
          label: 'Super Admin HQ',
          subtext: `${currentUser?.fullName || 'Ananya Roy'} • Platform Command`,
          badgeClass: 'bg-purple-500/20 text-purple-300 border-purple-500/40',
          path: '/super-admin'
        };
      default:
        return null;
    }
  };

  const roleDetails = getRoleDetails();

  return (
    <div className="bg-[#111111] border-b border-zinc-800/80 px-3 py-2 text-xs text-gray-300 sticky top-0 z-40 backdrop-blur-md">
      <div className="max-w-7xl mx-auto flex flex-wrap items-center justify-between gap-2">
        {/* Left: Active Role Badge (Isolated) or Guest Register/Login Prompt */}
        <div className="flex items-center gap-2 flex-wrap">
          {isAuthenticated && roleDetails ? (
            <div className="flex items-center gap-2">
              {/* Isolated Role Pill */}
              <div className={`flex items-center gap-2 px-3 py-1.5 rounded-lg border font-medium ${roleDetails.badgeClass}`}>
                <roleDetails.icon className="w-4 h-4 flex-shrink-0" />
                <span className="font-bold text-xs">{roleDetails.label}</span>
                <span className="text-zinc-400 font-normal hidden sm:inline">|</span>
                <span className="text-xs text-gray-300 hidden sm:inline font-mono">{roleDetails.subtext}</span>
              </div>

              {/* Role Isolation Indicator */}
              <div className="hidden lg:flex items-center gap-1 text-[11px] text-zinc-400 bg-zinc-900 px-2.5 py-1.5 rounded-lg border border-zinc-800">
                <Lock className="w-3 h-3 text-emerald-400" />
                <span>Isolated Role Access Active</span>
              </div>

              {/* Switch Role via Sign Out */}
              <button
                onClick={handleSignOut}
                className="flex items-center gap-1 px-2.5 py-1.5 rounded-lg bg-zinc-800 hover:bg-rose-950/40 border border-zinc-700 hover:border-rose-900/60 text-gray-300 hover:text-rose-300 transition-colors text-xs font-medium"
                title="Sign out of this role to log in with a different role"
              >
                <LogOut className="w-3 h-3 text-gray-400" />
                <span>Sign Out to Switch Role</span>
              </button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <span className="text-gray-400 text-xs font-semibold hidden md:inline">
                Housing Society Portal:
              </span>
              <span className="text-xs text-amber-300/90 bg-amber-950/40 border border-amber-800/40 px-2 py-1 rounded-md font-medium flex items-center gap-1.5">
                <Lock className="w-3 h-3 text-amber-400" />
                Full feature access requires compulsory registration
              </span>

              <Link
                to="/register"
                className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs transition-colors shadow-sm"
              >
                <UserPlus className="w-3 h-3" />
                <span>Register</span>
              </Link>

              <Link
                to="/login"
                className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-zinc-800 hover:bg-zinc-700 text-gray-200 border border-zinc-700 transition-colors text-xs font-medium"
              >
                <span>Log In</span>
                <ArrowRight className="w-3 h-3" />
              </Link>
            </div>
          )}
        </div>

        {/* Right: Society Selector & Vernacular Language Toggle */}
        <div className="flex items-center gap-2">
          {/* Backup Dispatch Active Indicator */}
          {isBackupDispatched && (
            <span className="hidden md:inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-semibold bg-amber-950/80 text-amber-300 border border-amber-600/50 animate-pulse">
              <AlertOctagon className="w-3 h-3 text-amber-400" />
              Backup Cook Active
            </span>
          )}

          {/* Society Dropdown */}
          <div className="flex items-center gap-1 bg-zinc-800/80 px-2 py-1 rounded-lg border border-zinc-700">
            <span className="text-gray-400 text-[11px] hidden sm:inline">Society:</span>
            <select
              value={currentSociety.id}
              onChange={(e) => setActiveSocietyId(Number(e.target.value))}
              className="bg-transparent text-gray-200 text-xs focus:outline-none cursor-pointer font-medium"
            >
              {societies.map((s) => (
                <option key={s.id} value={s.id} className="bg-zinc-900 text-white">
                  {s.name} ({s.city})
                </option>
              ))}
            </select>
          </div>

          {/* Language Toggle (English / हिंदी) */}
          <button
            onClick={toggleLanguage}
            className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-zinc-800 hover:bg-zinc-700 text-gray-200 border border-zinc-700 transition-colors font-medium text-xs"
            title="Toggle between English and Hindi"
          >
            <Languages className="w-3.5 h-3.5 text-brand-orange" />
            <span>{language === 'en' ? 'English' : 'हिंदी'}</span>
          </button>
        </div>
      </div>
    </div>
  );
}
