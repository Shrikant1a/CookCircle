import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth, ROLES } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import {
  ChefHat,
  Lock,
  Mail,
  ArrowRight,
  Eye,
  EyeOff,
  ShieldCheck
} from 'lucide-react';

export default function LoginPage() {
  const { login, isLoadingAuth } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    if (!email.trim()) {
      showToast('Please enter your email or phone number.', 'error');
      return;
    }
    if (!password.trim()) {
      showToast('Please enter your password.', 'error');
      return;
    }

    setSubmitting(true);
    try {
      const res = await login(email, password);
      showToast(`Welcome back, ${res.user?.fullName || 'User'}!`, 'success');
      const role = res.user?.role;
      if (role === ROLES.COOK) navigate('/cook');
      else if (role === ROLES.SOCIETY_ADMIN) navigate('/admin');
      else if (role === ROLES.SUPER_ADMIN) navigate('/super-admin');
      else navigate('/resident');
    } catch (err) {
      showToast(err.message || 'Invalid credentials! Please try again.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-6 bg-[#151515] border border-zinc-800 p-8 rounded-2xl shadow-2xl">
        {/* Header */}
        <div className="text-center">
          <div className="w-12 h-12 rounded-xl bg-brand-green mx-auto flex items-center justify-center text-white mb-3 shadow-lg shadow-emerald-950/40">
            <ChefHat className="w-7 h-7" />
          </div>
          <h2 className="text-2xl font-extrabold text-white">Log in to CookCircle</h2>
          <p className="mt-1 text-xs text-gray-400">
            Enter your credentials to access your authorized portal.
          </p>
        </div>

        {/* Clean Production Login Form */}
        <form onSubmit={handleFormSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-gray-300 mb-1">Email or Phone</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-gray-500 absolute left-3 top-3" />
              <input
                type="text"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@society.com or +91 98765 43210"
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl pl-9 pr-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green transition-colors"
              />
            </div>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-xs font-semibold text-gray-300">Password</label>
              <Link to="/forgot-password" className="text-[11px] text-brand-greenLight hover:underline">
                Forgot password?
              </Link>
            </div>
            <div className="relative">
              <Lock className="w-4 h-4 text-gray-500 absolute left-3 top-3" />
              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl pl-9 pr-10 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green transition-colors"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-2.5 text-gray-400 hover:text-white"
                tabIndex={-1}
              >
                {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
              </button>
            </div>
          </div>

          <div className="p-2.5 rounded-xl bg-zinc-900/60 border border-zinc-800 text-[11px] text-gray-400 flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-emerald-400 flex-shrink-0" />
            <span>Strict Role Access: Your account grants access strictly to your registered portal.</span>
          </div>

          <button
            type="submit"
            disabled={submitting || isLoadingAuth}
            className="w-full py-2.5 rounded-xl bg-brand-green hover:bg-emerald-600 disabled:opacity-50 text-white font-bold text-xs shadow-lg shadow-emerald-950/40 transition-all flex items-center justify-center gap-1.5"
          >
            <span>{submitting || isLoadingAuth ? 'Authenticating...' : 'Sign In'}</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </form>

        {/* Register Link */}
        <div className="pt-3 border-t border-zinc-800/80 text-center">
          <p className="text-xs text-gray-400">
            Don't have an account?{' '}
            <Link to="/register" className="text-brand-greenLight font-bold hover:underline">
              Register now
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
