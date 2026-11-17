import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { useToast } from '../../context/ToastContext';
import {
  Receipt,
  CreditCard,
  Wallet,
  CheckCircle2,
  Clock,
  Download,
  ShieldCheck,
  X,
  ArrowRight,
  ChefHat
} from 'lucide-react';
import { Link } from 'react-router-dom';

export default function ResidentInvoicesPage() {
  const { currentUser } = useAuth();
  const { invoices, payInvoice, walletBalance } = useApp();
  const { showToast } = useToast();

  const isDemoResident = currentUser?.email === 'resident@society.com';
  const residentFlat = currentUser?.flatNumber || (isDemoResident ? '402' : '');

  // Filter invoices for current user: new users start with 0 past invoices
  const userInvoices = isDemoResident
    ? invoices
    : invoices.filter(inv =>
        (residentFlat && inv.flatNumber === residentFlat) ||
        (currentUser?.fullName && inv.residentName === currentUser?.fullName)
      );

  const effectiveWalletBalance = isDemoResident ? walletBalance : Math.max(0, walletBalance - 280);

  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('UPI');
  const [upiId, setUpiId] = useState(currentUser?.email ? `${currentUser.email.split('@')[0]}@okaxis` : 'resident@okhdfcbank');
  const [isProcessing, setIsProcessing] = useState(false);

  const handleOpenPayment = (inv) => {
    setSelectedInvoice(inv);
    setPaymentModalOpen(true);
  };

  const handleExecutePayment = async (e) => {
    e.preventDefault();
    setIsProcessing(true);

    try {
      await payInvoice(selectedInvoice.id, `Razorpay (${paymentMethod})`);
    } finally {
      setIsProcessing(false);
      setPaymentModalOpen(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 pb-2">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
            Monthly Invoices & Razorpay Payments
          </h1>
          <p className="text-xs text-gray-400 mt-1">
            {residentFlat ? `Flat ${residentFlat} • ` : ''}Verified billing records, wallet skip deductions, and tax receipts.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <div className="px-3.5 py-1.5 rounded-xl bg-zinc-900 border border-zinc-800 text-xs flex items-center gap-2">
            <Wallet className="w-4 h-4 text-emerald-400" />
            <span className="text-gray-400">Available Wallet Credit:</span>
            <span className="font-bold text-emerald-300">₹{effectiveWalletBalance}</span>
          </div>
        </div>
      </div>

      {/* Empty State for Fresh New Users */}
      {userInvoices.length === 0 ? (
        <div className="p-12 text-center bg-[#151515] border border-zinc-800 rounded-3xl space-y-4 shadow-xl">
          <div className="w-16 h-16 rounded-2xl bg-zinc-900 border border-zinc-800 flex items-center justify-center text-emerald-400 mx-auto">
            <Receipt className="w-8 h-8" />
          </div>
          <div>
            <h3 className="text-xl font-bold text-white">No Billing History (Start Fresh)</h3>
            <p className="text-xs text-gray-400 max-w-md mx-auto mt-1 leading-relaxed">
              As a new registered resident, you have zero past invoices. Your first monthly tower pool subscription invoice (₹4,200) will be generated once you choose your cook partner and select your 45-minute daily cooking slot.
            </p>
          </div>
          <div className="pt-2">
            <Link
              to="/resident"
              className="inline-flex items-center gap-2 px-6 py-3 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg shadow-emerald-950/40 transition-all"
            >
              <ChefHat className="w-4 h-4" />
              <span>Choose Your Cook & Activate Subscription</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        </div>
      ) : (
        /* Invoices List */
        <div className="space-y-4">
          {userInvoices.map((inv) => {
            const isPaid = inv.status === 'PAID';
            return (
              <div
                key={inv.id}
                className={`p-6 rounded-2xl border transition-all ${
                  isPaid
                    ? 'bg-[#151515] border-zinc-800'
                    : 'bg-emerald-950/20 border-emerald-600/50 shadow-lg'
                }`}
              >
                <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between pb-4 border-b border-zinc-800 gap-4">
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-mono font-bold text-gray-400">{inv.id}</span>
                      <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${
                        isPaid ? 'bg-zinc-800 text-emerald-400' : 'bg-amber-500/20 text-amber-300'
                      }`}>
                        {inv.status}
                      </span>
                    </div>
                    <h3 className="text-base font-bold text-white mt-1">
                      Subscription for {inv.month}
                    </h3>
                    <p className="text-xs text-gray-400">
                      {inv.societyName} • {inv.towerName} • Flat {inv.flatNumber}
                    </p>
                  </div>

                  <div className="text-right">
                    <span className="text-[10px] text-gray-400 uppercase font-bold block">Net Payable</span>
                    <span className="text-2xl font-black text-white">₹{inv.netPayable.toLocaleString()}</span>
                    <span className="text-[10px] text-gray-500 block">Due date: {inv.dueDate}</span>
                  </div>
                </div>

                {/* Itemized Calculation */}
                <div className="mt-4 pt-1 grid grid-cols-1 sm:grid-cols-3 gap-4 text-xs">
                  <div className="p-3 rounded-xl bg-zinc-900 border border-zinc-800/80">
                    <span className="text-gray-400 text-[11px] block">Base Tower Pooling Rate</span>
                    <span className="font-bold text-white text-sm">₹{(inv.baseAmount || inv.amount || 4200).toLocaleString()}</span>
                  </div>
                  <div className="p-3 rounded-xl bg-zinc-900 border border-zinc-800/80">
                    <span className="text-gray-400 text-[11px] block">Wallet Skip-Meal Rebate</span>
                    <span className="font-bold text-emerald-400 text-sm">-₹{(inv.walletRebate || inv.discount || 0).toLocaleString()}</span>
                  </div>
                  <div className="p-3 rounded-xl bg-zinc-900 border border-zinc-800/80">
                    <span className="text-gray-400 text-[11px] block">Payment Gateway</span>
                    <span className="font-bold text-brand-greenLight text-sm">Razorpay UPI / NetBanking</span>
                  </div>
                </div>

                {/* Actions */}
                <div className="mt-5 pt-4 border-t border-zinc-800/80 flex flex-wrap items-center justify-between gap-3">
                  <div className="flex items-center gap-1.5 text-xs text-gray-400">
                    <ShieldCheck className="w-4 h-4 text-brand-green" />
                    <span>Includes 100% Standby Float Cook Guarantee & Accident Cover</span>
                  </div>

                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => showToast(`Downloaded GST invoice tax receipt for ${inv.id}`, "info")}
                      className="px-3 py-1.5 rounded-lg bg-zinc-900 hover:bg-zinc-800 border border-zinc-800 text-gray-300 text-xs font-medium transition-colors flex items-center gap-1.5"
                    >
                      <Download className="w-3.5 h-3.5" />
                      <span>Download Receipt</span>
                    </button>

                    {!isPaid && (
                      <button
                        onClick={() => handleOpenPayment(inv)}
                        className="px-5 py-2 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-md shadow-emerald-950/40 transition-all flex items-center gap-1.5"
                      >
                        <CreditCard className="w-3.5 h-3.5" />
                        <span>Pay ₹{inv.netPayable.toLocaleString()} via Razorpay</span>
                      </button>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Razorpay Simulation Modal */}
      {paymentModalOpen && selectedInvoice && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
          <div className="bg-[#181818] border border-zinc-700 max-w-md w-full rounded-2xl p-6 shadow-2xl space-y-5 relative">
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <div className="flex items-center gap-2">
                <CreditCard className="w-5 h-5 text-brand-greenLight" />
                <h3 className="text-base font-bold text-white">Razorpay Secure Checkout</h3>
              </div>
              <button
                onClick={() => setPaymentModalOpen(false)}
                className="p-1 rounded-lg text-gray-400 hover:text-white hover:bg-zinc-800"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="p-3.5 rounded-xl bg-zinc-900 border border-zinc-800 flex items-center justify-between">
              <div>
                <span className="text-xs text-gray-400 block">{selectedInvoice.month} Subscription</span>
                <span className="text-lg font-black text-white">₹{selectedInvoice.netPayable.toLocaleString()}</span>
              </div>
              <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-500/20 text-emerald-400">
                256-bit Encrypted
              </span>
            </div>

            <form onSubmit={handleExecutePayment} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-2">Payment Option</label>
                <div className="grid grid-cols-3 gap-2">
                  {['UPI', 'Card', 'NetBanking'].map((method) => (
                    <button
                      key={method}
                      type="button"
                      onClick={() => setPaymentMethod(method)}
                      className={`py-2 px-3 rounded-xl border text-xs font-bold transition-all ${
                        paymentMethod === method
                          ? 'bg-brand-green text-white border-brand-green shadow-md'
                          : 'bg-zinc-900 text-gray-400 border-zinc-800 hover:border-zinc-700'
                      }`}
                    >
                      {method}
                    </button>
                  ))}
                </div>
              </div>

              {paymentMethod === 'UPI' && (
                <div>
                  <label className="block text-xs font-semibold text-gray-300 mb-1">UPI ID / VPA</label>
                  <input
                    type="text"
                    required
                    value={upiId}
                    onChange={(e) => setUpiId(e.target.value)}
                    placeholder="name@okaxis"
                    className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-green"
                  />
                </div>
              )}

              <button
                type="submit"
                disabled={isProcessing}
                className="w-full py-2.5 rounded-xl bg-brand-green hover:bg-emerald-600 disabled:opacity-50 text-white font-bold text-xs shadow-lg transition-all flex items-center justify-center gap-1.5"
              >
                <span>{isProcessing ? 'Processing via Bank Gateway...' : `Complete Payment of ₹${selectedInvoice.netPayable.toLocaleString()}`}</span>
                <ArrowRight className="w-3.5 h-3.5" />
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
