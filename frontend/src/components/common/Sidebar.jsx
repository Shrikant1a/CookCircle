import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth, ROLES } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import {
  LayoutDashboard,
  CalendarDays,
  Clock,
  ChefHat,
  Receipt,
  MessageSquareWarning,
  UserCheck,
  Building2,
  Users,
  ShieldAlert,
  BarChart3,
  Sliders,
  DollarSign,
  AlertTriangle,
  History,
  ShieldCheck,
  BookOpen,
  LogOut
} from 'lucide-react';

export default function Sidebar() {
  const { activeRole, logout } = useAuth();
  const { isBackupDispatched } = useApp();
  const navigate = useNavigate();

  const handleSignOut = () => {
    logout();
    navigate('/login');
  };

  const getLinks = () => {
    switch (activeRole) {
      case ROLES.RESIDENT:
        return [
          { to: '/resident', label: 'Overview', icon: LayoutDashboard },
          { to: '/resident/pool', label: 'Live Tower Pool', icon: Clock, badge: '5 Slots' },
          { to: '/resident/bookings', label: 'My Bookings', icon: CalendarDays },
          { to: '/resident/cook', label: 'My Cook & Prep', icon: ChefHat },
          { to: '/resident/invoices', label: 'Invoices & Razorpay', icon: Receipt },
          { to: '/resident/complaints', label: 'Complaints', icon: MessageSquareWarning },
          { to: '/resident/profile', label: 'Profile', icon: UserCheck }
        ];

      case ROLES.COOK:
        return [
          { to: '/cook', label: 'Today Route', icon: Clock, badge: 'Evening' },
          { to: '/cook/schedule', label: 'Weekly Schedule', icon: CalendarDays },
          { to: '/cook/attendance', label: 'Attendance & Earnings', icon: DollarSign },
          { to: '/cook/leave', label: 'Apply Leave', icon: History },
          { to: '/cook/profile', label: 'Partner Profile', icon: ShieldCheck }
        ];

      case ROLES.SOCIETY_ADMIN:
        return [
          { to: '/admin', label: 'Overview & KPIs', icon: LayoutDashboard },
          { to: '/admin/towers', label: 'Towers & Flats', icon: Building2 },
          { to: '/admin/cooks', label: 'Cooks Directory', icon: ChefHat },
          { to: '/admin/backup-dispatch', label: 'Backup Dispatch', icon: ShieldAlert, highlight: isBackupDispatched },
          { to: '/admin/slots', label: 'Slot Schedules', icon: Clock },
          { to: '/admin/leaves', label: 'Leave Requests', icon: History },
          { to: '/admin/invoices', label: 'Invoices & MRR', icon: Receipt },
          { to: '/admin/complaints', label: 'Complaints', icon: MessageSquareWarning },
          { to: '/admin/reports', label: 'Reports & Export', icon: BarChart3 },
          { to: '/admin/settings', label: 'Settings', icon: Sliders }
        ];

      case ROLES.SUPER_ADMIN:
        return [
          { to: '/super-admin', label: 'Platform Overview', icon: LayoutDashboard },
          { to: '/super-admin/societies', label: 'Societies Directory', icon: Building2 },
          { to: '/super-admin/admins', label: 'Society Admins', icon: Users },
          { to: '/super-admin/activity', label: 'Audit Logs', icon: History },
          { to: '/super-admin/settings', label: 'Global Settings', icon: Sliders }
        ];

      default:
        return [];
    }
  };

  const links = getLinks();

  return (
    <aside className="w-64 bg-[#111111] border-r border-zinc-800/80 min-h-[calc(100vh-4rem)] flex flex-col p-4 flex-shrink-0">
      <div className="mb-4 px-2">
        <span className="text-[11px] font-bold text-gray-400 uppercase tracking-wider">
          {activeRole.replace('_', ' ')} PORTAL
        </span>
      </div>

      <nav className="space-y-1.5 flex-1">
        {links.map((link) => {
          const Icon = link.icon;
          return (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.to === '/resident' || link.to === '/cook' || link.to === '/admin' || link.to === '/super-admin'}
              className={({ isActive }) =>
                `flex items-center justify-between px-3 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-zinc-800 text-white border border-zinc-700 shadow-sm'
                    : 'text-gray-400 hover:text-white hover:bg-zinc-800/50'
                } ${link.highlight ? 'border border-amber-500/50 text-amber-300' : ''}`
              }
            >
              <div className="flex items-center gap-3">
                <Icon className={`w-4 h-4 ${link.highlight ? 'text-amber-400' : 'text-gray-400'}`} />
                <span>{link.label}</span>
              </div>
              {link.badge && (
                <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-zinc-800 text-gray-300 border border-zinc-700">
                  {link.badge}
                </span>
              )}
              {link.highlight && (
                <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-300 border border-amber-500/30 animate-pulse">
                  Active
                </span>
              )}
            </NavLink>
          );
        })}

        <NavLink
          to="/manual"
          className="flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-emerald-400 hover:text-white hover:bg-emerald-950/30 border border-emerald-800/40 transition-all mt-3"
        >
          <BookOpen className="w-4 h-4 text-emerald-400" />
          <span>User Manual & Guide</span>
        </NavLink>
      </nav>

      {/* Standby Status Pill in Sidebar */}
      <div className="mt-auto pt-4 border-t border-zinc-800/80 space-y-2">
        <div className="p-3 rounded-xl bg-zinc-900 border border-zinc-800 text-xs">
          <div className="flex items-center justify-between text-gray-300 mb-1">
            <span className="font-semibold">Standby Float Pool</span>
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
          </div>
          <p className="text-[11px] text-gray-500 leading-snug">
            {isBackupDispatched
              ? "Standby Cook Ramesh Kumar active on Tower B."
              : "2 Standby cooks ready for emergency leave dispatch."}
          </p>
        </div>

        <button
          onClick={handleSignOut}
          className="w-full flex items-center justify-center gap-2 py-2 px-3 rounded-xl bg-zinc-900 hover:bg-rose-950/40 border border-zinc-800 hover:border-rose-900/60 text-xs text-gray-400 hover:text-rose-300 font-medium transition-all"
        >
          <LogOut className="w-3.5 h-3.5" />
          <span>Sign Out ({activeRole.replace('_', ' ')})</span>
        </button>
      </div>
    </aside>
  );
}

