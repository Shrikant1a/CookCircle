import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth, ROLES } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import {
  ChefHat,
  Bell,
  Wallet,
  Menu,
  X,
  CheckCircle2,
  AlertCircle,
  Clock,
  ArrowRight,
  LogOut
} from 'lucide-react';

export default function Navbar() {
  const { activeRole, currentUser, switchRole, logout, isAuthenticated } = useAuth();
  const { walletBalance, notifications, markAllNotificationsRead } = useApp();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [showNotifications, setShowNotifications] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const unreadCount = notifications.filter(n => !n.isRead).length;

  return (
    <header className="bg-[#0A0A0A]/95 border-b border-zinc-800 sticky top-0 z-30 backdrop-blur-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2.5 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-brand-green to-emerald-700 flex items-center justify-center shadow-lg shadow-emerald-950/40 group-hover:scale-105 transition-transform">
              <ChefHat className="w-6 h-6 text-white" />
            </div>
            <div>
              <span className="text-xl font-extrabold tracking-tight text-white flex items-center gap-1">
                COOK<span className="text-brand-greenLight">CIRCLE</span>
              </span>
              <span className="text-[10px] text-gray-400 block tracking-wider uppercase -mt-1 font-medium">
                Society Cooking, Simplified
              </span>
            </div>
          </Link>

          {/* Desktop Navigation Links */}
          <nav className="hidden md:flex items-center gap-6 text-sm font-medium text-gray-300">
            {activeRole === ROLES.GUEST ? (
              <>
                <Link to="/" className="hover:text-brand-greenLight transition-colors">Home</Link>
                <Link to="/features" className="hover:text-brand-greenLight transition-colors">Features</Link>
                <Link to="/how-it-works" className="hover:text-brand-greenLight transition-colors">How It Works</Link>
                <Link to="/manual" className="hover:text-brand-greenLight transition-colors font-semibold text-brand-greenLight">User Manual</Link>
                <Link to="/about" className="hover:text-brand-greenLight transition-colors">About Us</Link>
                <Link to="/contact" className="hover:text-brand-greenLight transition-colors">Contact</Link>
              </>
            ) : (
              <>
                <Link
                  to={
                    activeRole === ROLES.RESIDENT ? '/resident' :
                    activeRole === ROLES.COOK ? '/cook' :
                    activeRole === ROLES.SOCIETY_ADMIN ? '/admin' : '/super-admin'
                  }
                  className="text-white hover:text-brand-greenLight transition-colors flex items-center gap-1.5"
                >
                  <span className="w-2 h-2 rounded-full bg-brand-green animate-pulse"></span>
                  Active Dashboard
                </Link>

                <Link to="/manual" className="text-gray-300 hover:text-brand-greenLight transition-colors font-medium">
                  User Manual
                </Link>

                {activeRole === ROLES.RESIDENT && (
                  <>
                    <Link to="/resident/pool" className="hover:text-brand-greenLight transition-colors">
                      Live Tower Pool
                    </Link>
                    <Link to="/resident/bookings" className="hover:text-brand-greenLight transition-colors">
                      My Bookings
                    </Link>
                    <Link to="/resident/invoices" className="hover:text-brand-greenLight transition-colors">
                      Invoices & Razorpay
                    </Link>
                  </>
                )}

                {activeRole === ROLES.COOK && (
                  <>
                    <Link to="/cook/route" className="hover:text-brand-orangeLight transition-colors">
                      Tower Route
                    </Link>
                    <Link to="/cook/attendance" className="hover:text-brand-orangeLight transition-colors">
                      Attendance & Bonus
                    </Link>
                  </>
                )}

                {activeRole === ROLES.SOCIETY_ADMIN && (
                  <>
                    <Link to="/admin/towers" className="hover:text-indigo-400 transition-colors">
                      Towers & Flats
                    </Link>
                    <Link to="/admin/cooks" className="hover:text-indigo-400 transition-colors">
                      Cooks & Standby Pool
                    </Link>
                    <Link to="/admin/invoices" className="hover:text-indigo-400 transition-colors">
                      Billing & MRR
                    </Link>
                  </>
                )}
              </>
            )}
          </nav>

          {/* Right Action Icons & User Info */}
          <div className="flex items-center gap-3">
            {/* Wallet Credit Badge (Visible for Residents) */}
            {activeRole === ROLES.RESIDENT && (
              <div
                className="hidden sm:flex items-center gap-1.5 bg-zinc-900 border border-zinc-800 px-3 py-1.5 rounded-lg text-xs"
                title="Your CookCircle Wallet Balance (Refunds from skipped meal slots)"
              >
                <Wallet className="w-4 h-4 text-emerald-400" />
                <span className="text-gray-400">Wallet:</span>
                <span className="font-bold text-emerald-300">₹{walletBalance}</span>
              </div>
            )}

            {/* Notification Bell */}
            <div className="relative">
              <button
                onClick={() => setShowNotifications(!showNotifications)}
                className="p-2 rounded-lg bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 text-gray-300 hover:text-white transition-colors relative"
                aria-label="View notifications"
              >
                <Bell className="w-4 h-4" />
                {unreadCount > 0 && (
                  <span className="absolute -top-1 -right-1 w-4 h-4 rounded-full bg-brand-orange text-white text-[10px] font-bold flex items-center justify-center animate-bounce">
                    {unreadCount}
                  </span>
                )}
              </button>

              {/* Notification Dropdown Panel */}
              {showNotifications && (
                <div className="absolute right-0 mt-2 w-80 sm:w-96 bg-[#151515] border border-zinc-800 rounded-xl shadow-2xl z-50 p-3 animate-fade-in">
                  <div className="flex items-center justify-between pb-2 border-b border-zinc-800">
                    <div className="flex items-center gap-2">
                      <span className="font-semibold text-white text-sm">Notifications</span>
                      {unreadCount > 0 && (
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-brand-green/20 text-brand-greenLight">
                          {unreadCount} new
                        </span>
                      )}
                    </div>
                    {unreadCount > 0 && (
                      <button
                        onClick={markAllNotificationsRead}
                        className="text-xs text-brand-greenLight hover:underline font-medium"
                      >
                        Mark all read
                      </button>
                    )}
                  </div>

                  <div className="divide-y divide-zinc-800/60 max-h-72 overflow-y-auto mt-2">
                    {notifications.length === 0 ? (
                      <div className="py-6 text-center text-xs text-gray-500">
                        No notifications yet
                      </div>
                    ) : (
                      notifications.map((n) => (
                        <div
                          key={n.id}
                          className={`py-2.5 px-2 hover:bg-zinc-800/50 rounded-lg transition-colors text-left flex gap-2.5 ${
                            !n.isRead ? 'bg-zinc-800/20' : ''
                          }`}
                        >
                          <div className="mt-0.5 flex-shrink-0">
                            {n.type === 'BACKUP' ? (
                              <AlertCircle className="w-4 h-4 text-amber-400" />
                            ) : n.type === 'CREDIT' ? (
                              <Wallet className="w-4 h-4 text-emerald-400" />
                            ) : (
                              <CheckCircle2 className="w-4 h-4 text-brand-green" />
                            )}
                          </div>
                          <div className="flex-1">
                            <p className="text-xs font-semibold text-white leading-tight">{n.title}</p>
                            <p className="text-[11px] text-gray-400 mt-0.5 leading-snug">{n.message}</p>
                            <span className="text-[10px] text-gray-500 mt-1 block flex items-center gap-1">
                              <Clock className="w-3 h-3 inline" /> {n.time}
                            </span>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              )}
            </div>

            {/* User Profile Chip or Login/Register */}
            {activeRole !== ROLES.GUEST && currentUser ? (
              <div className="flex items-center gap-2 pl-2 border-l border-zinc-800">
                <img
                  src={currentUser.avatar}
                  alt={currentUser.fullName}
                  className="w-8 h-8 rounded-full object-cover border border-zinc-700"
                />
                <div className="hidden lg:block text-left text-xs leading-tight">
                  <p className="font-semibold text-white">{currentUser.fullName}</p>
                  <p className="text-[10px] text-gray-400 font-medium">
                    {currentUser.flatNumber ? `Flat ${currentUser.flatNumber}` : currentUser.role.replace('ROLE_', '')}
                  </p>
                </div>
                <button
                  onClick={handleLogout}
                  className="p-1.5 ml-1 rounded-lg bg-zinc-900 border border-zinc-800 hover:bg-rose-950/40 hover:border-rose-800 text-gray-400 hover:text-rose-400 transition-colors"
                  title="Sign Out"
                >
                  <LogOut className="w-3.5 h-3.5" />
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  to="/login"
                  className="px-3.5 py-1.5 rounded-lg text-xs font-medium text-gray-300 hover:text-white bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 transition-colors"
                >
                  Log In
                </Link>
                <Link
                  to="/register"
                  className="px-3.5 py-1.5 rounded-lg text-xs font-semibold text-white bg-brand-green hover:bg-emerald-600 transition-colors flex items-center gap-1 shadow-md shadow-emerald-950/30"
                >
                  Join Now <ArrowRight className="w-3 h-3" />
                </Link>
              </div>
            )}

            {/* Mobile menu button */}
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="md:hidden p-2 rounded-lg bg-zinc-900 border border-zinc-800 text-gray-400 hover:text-white"
            >
              {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-[#111111] border-b border-zinc-800 px-4 pt-3 pb-5 space-y-3 animate-fade-in text-sm">
          {activeRole === ROLES.GUEST ? (
            <>
              <Link
                to="/"
                onClick={() => setMobileMenuOpen(false)}
                className="block text-gray-200 hover:text-brand-green py-1.5 font-medium"
              >
                Home
              </Link>
              <Link
                to="/features"
                onClick={() => setMobileMenuOpen(false)}
                className="block text-gray-200 hover:text-brand-green py-1.5 font-medium"
              >
                Features & Pricing
              </Link>
              <Link
                to="/how-it-works"
                onClick={() => setMobileMenuOpen(false)}
                className="block text-gray-200 hover:text-brand-green py-1.5 font-medium"
              >
                How It Works
              </Link>
              <Link
                to="/manual"
                onClick={() => setMobileMenuOpen(false)}
                className="block text-brand-greenLight font-semibold py-1.5"
              >
                User Manual
              </Link>
              <div className="pt-2 border-t border-zinc-800 flex gap-2">
                <Link
                  to="/login"
                  onClick={() => setMobileMenuOpen(false)}
                  className="w-1/2 text-center py-2 rounded-lg bg-zinc-800 text-white font-medium text-xs"
                >
                  Log In
                </Link>
                <Link
                  to="/register"
                  onClick={() => setMobileMenuOpen(false)}
                  className="w-1/2 text-center py-2 rounded-lg bg-brand-green text-white font-bold text-xs"
                >
                  Register
                </Link>
              </div>
            </>
          ) : (
            <>
              <div className="pb-2 border-b border-zinc-800 text-xs font-semibold text-gray-400">
                Logged in as {activeRole.replace('_', ' ')}: {currentUser?.fullName}
              </div>
              <Link
                to={
                  activeRole === ROLES.RESIDENT ? '/resident' :
                  activeRole === ROLES.COOK ? '/cook' :
                  activeRole === ROLES.SOCIETY_ADMIN ? '/admin' : '/super-admin'
                }
                onClick={() => setMobileMenuOpen(false)}
                className="block text-white hover:text-brand-green py-1.5 font-medium"
              >
                Dashboard
              </Link>
              {activeRole === ROLES.RESIDENT && (
                <>
                  <Link
                    to="/resident/pool"
                    onClick={() => setMobileMenuOpen(false)}
                    className="block text-gray-300 hover:text-brand-green py-1.5"
                  >
                    Live Tower Pool
                  </Link>
                  <Link
                    to="/resident/bookings"
                    onClick={() => setMobileMenuOpen(false)}
                    className="block text-gray-300 hover:text-brand-green py-1.5"
                  >
                    My Bookings
                  </Link>
                </>
              )}
              {activeRole === ROLES.COOK && (
                <>
                  <Link
                    to="/cook/route"
                    onClick={() => setMobileMenuOpen(false)}
                    className="block text-gray-300 hover:text-brand-orange py-1.5"
                  >
                    Tower Route
                  </Link>
                  <Link
                    to="/cook/attendance"
                    onClick={() => setMobileMenuOpen(false)}
                    className="block text-gray-300 hover:text-brand-orange py-1.5"
                  >
                    Attendance & Bonus
                  </Link>
                </>
              )}
              {activeRole === ROLES.SOCIETY_ADMIN && (
                <>
                  <Link
                    to="/admin/towers"
                    onClick={() => setMobileMenuOpen(false)}
                    className="block text-gray-300 hover:text-indigo-400 py-1.5"
                  >
                    Towers & Flats
                  </Link>
                  <Link
                    to="/admin/cooks"
                    onClick={() => setMobileMenuOpen(false)}
                    className="block text-gray-300 hover:text-indigo-400 py-1.5"
                  >
                    Cooks & Standby
                  </Link>
                </>
              )}
              <Link
                to="/manual"
                onClick={() => setMobileMenuOpen(false)}
                className="block text-emerald-400 hover:underline py-1.5"
              >
                User Manual
              </Link>
              <button
                onClick={() => { handleLogout(); setMobileMenuOpen(false); }}
                className="w-full text-left text-rose-400 py-2 flex items-center gap-2 font-medium"
              >
                <LogOut className="w-4 h-4" />
                <span>Sign Out</span>
              </button>
            </>
          )}
        </div>
      )}
    </header>
  );
}

