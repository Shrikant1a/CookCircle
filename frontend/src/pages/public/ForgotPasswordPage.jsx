import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { ChefHat, ArrowLeft, Mail, CheckCircle2 } from 'lucide-react';
import { useToast } from '../../context/ToastContext';

export default function ForgotPasswordPage() {
  const { showToast } = useToast();
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    setSent(true);
    showToast("Password reset instructions sent to your email.", "info");
  };

  return (
    <div className="min-h-[75vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full bg-[#151515] border border-zinc-800 p-8 rounded-2xl shadow-2xl space-y-6">
        <div className="text-center">
          <div className="w-12 h-12 rounded-xl bg-brand-green mx-auto flex items-center justify-center text-white mb-3">
            <ChefHat className="w-7 h-7" />
          </div>
          <h2 className="text-2xl font-extrabold text-white">Reset Password</h2>
          <p className="mt-1 text-xs text-gray-400">
            Enter your registered email address to receive a secure recovery link.
          </p>
        </div>

        {sent ? (
          <div className="text-center py-6 space-y-4">
            <CheckCircle2 className="w-12 h-12 text-brand-green mx-auto" />
            <h3 className="text-base font-bold text-white">Check Your Inbox</h3>
            <p className="text-xs text-gray-400">
              We've dispatched a reset token to <strong className="text-white">{email}</strong>.
            </p>
            <Link
              to="/login"
              className="inline-flex items-center gap-1.5 text-xs text-brand-greenLight font-bold hover:underline"
            >
              <ArrowLeft className="w-3.5 h-3.5" /> Back to Log In
            </Link>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Email Address</label>
              <div className="relative">
                <Mail className="w-4 h-4 text-gray-500 absolute left-3 top-3" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="resident@society.com"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl pl-9 pr-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-2.5 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg transition-all"
            >
              Send Password Reset Link
            </button>

            <div className="text-center pt-2">
              <Link to="/login" className="text-xs text-gray-400 hover:text-white inline-flex items-center gap-1">
                <ArrowLeft className="w-3 h-3" /> Back to Log In
              </Link>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

