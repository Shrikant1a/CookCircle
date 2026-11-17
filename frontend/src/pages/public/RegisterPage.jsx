import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { useAuth, ROLES } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';
import { ChefHat, ArrowRight, Building2, Users, Shield, Lock, CheckCircle2 } from 'lucide-react';

export default function RegisterPage() {
  const {
    societies,
    registerResident,
    registerCook,
    registerSocietyAdmin,
    registerSuperAdmin,
    isLoadingAuth
  } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  // Read role from query param if available
  const initialRole = searchParams.get('role') || 'RESIDENT';
  const [roleType, setRoleType] = useState(
    ['RESIDENT', 'COOK', 'SOCIETY_ADMIN', 'SUPER_ADMIN'].includes(initialRole) ? initialRole : 'RESIDENT'
  );

  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    phone: '',
    password: '',
    societyId: 1,
    towerId: 101,
    tower: 'Tower B - Wing 1',
    flatNumber: '',
    dietaryPref: 'Pure Veg',
    householdSize: '3-4 members',
    specialties: 'North Indian, Gujarati, Dal Baati',
    experienceYears: 5,
    designation: 'Estate Facility Manager',
    superAdminKey: 'COOKCIRCLE_SUPER_2026'
  });

  useEffect(() => {
    const r = searchParams.get('role');
    if (r && ['RESIDENT', 'COOK', 'SOCIETY_ADMIN', 'SUPER_ADMIN'].includes(r)) {
      setRoleType(r);
    }
  }, [searchParams]);

  const handleRegister = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      if (roleType === 'RESIDENT') {
        await registerResident({
          fullName: formData.fullName,
          email: formData.email,
          phone: formData.phone,
          password: formData.password || 'password123',
          societyId: formData.societyId,
          towerId: formData.towerId || 101,
          flatNumber: formData.flatNumber || '402',
          householdSize: formData.householdSize,
          dietaryPref: formData.dietaryPref
        });
        showToast(`Registration successful! Welcome to your Resident portal, ${formData.fullName}.`, 'success');
        navigate('/resident');
      } else if (roleType === 'COOK') {
        await registerCook({
          fullName: formData.fullName,
          email: formData.email,
          phone: formData.phone,
          password: formData.password || 'password123',
          societyId: formData.societyId,
          specialties: formData.specialties || 'North Indian Home Meals',
          experienceYears: formData.experienceYears || 5,
          type: 'PRIMARY_COOK'
        });
        showToast(`Cook Partner account created! Welcome, ${formData.fullName}.`, 'success');
        navigate('/cook');
      } else if (roleType === 'SOCIETY_ADMIN') {
        const selectedSoc = societies.find(s => s.id === formData.societyId);
        await registerSocietyAdmin({
          fullName: formData.fullName,
          email: formData.email,
          phone: formData.phone,
          password: formData.password || 'password123',
          societyId: formData.societyId,
          societyName: selectedSoc?.name || 'Hiranandani Gardens',
          designation: formData.designation || 'Estate Facility Manager'
        });
        showToast(`Society Admin account created! Managing ${selectedSoc?.name || 'Society'}.`, 'success');
        navigate('/admin');
      } else if (roleType === 'SUPER_ADMIN') {
        if (formData.superAdminKey.trim() !== 'COOKCIRCLE_SUPER_2026') {
          showToast("Invalid Super Admin Key! Please use the authorized authorization key.", "error");
          setSubmitting(false);
          return;
        }
        await registerSuperAdmin({
          fullName: formData.fullName,
          email: formData.email,
          phone: formData.phone,
          password: formData.password || 'password123'
        });
        showToast(`Super Admin profile created! Welcome to Platform Command.`, 'success');
        navigate('/super-admin');
      }
    } catch (err) {
      showToast(err.message || 'Registration failed. Please try again.', 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-[85vh] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-xl w-full bg-[#151515] border border-zinc-800 p-8 rounded-2xl shadow-2xl space-y-6">
        <div className="text-center">
          <div className="w-12 h-12 rounded-xl bg-brand-green mx-auto flex items-center justify-center text-white mb-3">
            <ChefHat className="w-7 h-7" />
          </div>
          <h2 className="text-2xl font-extrabold text-white">Create Your CookCircle Account</h2>
          <p className="mt-1 text-xs text-gray-400">
            Select your role to register with role-specific access permissions.
          </p>
        </div>

        {/* 4 Role Tab Selection */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-1.5 p-1 bg-zinc-900 border border-zinc-800 rounded-xl">
          <button
            type="button"
            onClick={() => setRoleType('RESIDENT')}
            className={`py-2 px-2 text-[11px] font-bold rounded-lg transition-all flex flex-col items-center gap-1 ${
              roleType === 'RESIDENT'
                ? 'bg-brand-green text-white shadow-md'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            <Users className="w-3.5 h-3.5" />
            <span>Resident</span>
          </button>

          <button
            type="button"
            onClick={() => setRoleType('COOK')}
            className={`py-2 px-2 text-[11px] font-bold rounded-lg transition-all flex flex-col items-center gap-1 ${
              roleType === 'COOK'
                ? 'bg-brand-orange text-white shadow-md'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            <ChefHat className="w-3.5 h-3.5" />
            <span>Cook Partner</span>
          </button>

          <button
            type="button"
            onClick={() => setRoleType('SOCIETY_ADMIN')}
            className={`py-2 px-2 text-[11px] font-bold rounded-lg transition-all flex flex-col items-center gap-1 ${
              roleType === 'SOCIETY_ADMIN'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            <Building2 className="w-3.5 h-3.5" />
            <span>Society Admin</span>
          </button>

          <button
            type="button"
            onClick={() => setRoleType('SUPER_ADMIN')}
            className={`py-2 px-2 text-[11px] font-bold rounded-lg transition-all flex flex-col items-center gap-1 ${
              roleType === 'SUPER_ADMIN'
                ? 'bg-purple-600 text-white shadow-md'
                : 'text-gray-400 hover:text-white'
            }`}
          >
            <Shield className="w-3.5 h-3.5" />
            <span>Super Admin</span>
          </button>
        </div>

        {/* Role Description Notice */}
        <div className="p-3 rounded-xl bg-zinc-900/60 border border-zinc-800 text-xs text-gray-300 flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-400 flex-shrink-0" />
          <span>
            {roleType === 'RESIDENT' && "You will get exclusive access to Tower Pools, 4-digit Arrival OTPs, Skip-Meal credits, and Razorpay invoices."}
            {roleType === 'COOK' && "You will get exclusive access to daily sequential routes, Hindi/English vernacular toggle, OTP verification, and leave requests."}
            {roleType === 'SOCIETY_ADMIN' && "You will get exclusive access to estate towers, cook management, standby float dispatch, and monthly billing batches."}
            {roleType === 'SUPER_ADMIN' && "You will get exclusive access to multi-society platform provisioning, global analytics, and security audit logs."}
          </span>
        </div>

        <form onSubmit={handleRegister} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Full Name</label>
              <input
                type="text"
                required
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                placeholder={roleType === 'RESIDENT' ? "e.g. Aarav Sharma" : roleType === 'COOK' ? "e.g. Sunita Devi" : "e.g. Vikram Malhotra"}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Phone Number</label>
              <input
                type="tel"
                required
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                placeholder="+91 98765 43210"
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Email Address</label>
              <input
                type="email"
                required
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="name@example.com"
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Create Password</label>
              <input
                type="password"
                required
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                placeholder="Minimum 6 characters"
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
              />
            </div>
          </div>

          {/* Housing Society selector (for Resident, Cook, and Society Admin) */}
          {roleType !== 'SUPER_ADMIN' && (
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Housing Society</label>
              <select
                value={formData.societyId}
                onChange={(e) => setFormData({ ...formData, societyId: Number(e.target.value) })}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green cursor-pointer"
              >
                {societies.map((s) => (
                  <option key={s.id} value={s.id}>
                    {s.name} — {s.city} ({s.code})
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Role-Specific Fields: RESIDENT */}
          {roleType === 'RESIDENT' && (
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Flat Number</label>
                <input
                  type="text"
                  required
                  value={formData.flatNumber}
                  onChange={(e) => setFormData({ ...formData, flatNumber: e.target.value })}
                  placeholder="e.g. 402"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Dietary Preference</label>
                <select
                  value={formData.dietaryPref}
                  onChange={(e) => setFormData({ ...formData, dietaryPref: e.target.value })}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green cursor-pointer"
                >
                  <option value="Pure Veg">Pure Veg</option>
                  <option value="Jain">Jain (No onion/garlic)</option>
                  <option value="Non-Veg">Non-Veg OK</option>
                </select>
              </div>
            </div>
          )}

          {/* Role-Specific Fields: COOK */}
          {roleType === 'COOK' && (
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Specialty Cuisines</label>
                <input
                  type="text"
                  required
                  value={formData.specialties}
                  onChange={(e) => setFormData({ ...formData, specialties: e.target.value })}
                  placeholder="e.g. North Indian, Gujarati, Dal Baati"
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-orange"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold text-gray-300 mb-1">Years of Experience</label>
                <input
                  type="number"
                  min="1"
                  max="30"
                  required
                  value={formData.experienceYears}
                  onChange={(e) => setFormData({ ...formData, experienceYears: Number(e.target.value) })}
                  className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-orange"
                />
              </div>
            </div>
          )}

          {/* Role-Specific Fields: SOCIETY_ADMIN */}
          {roleType === 'SOCIETY_ADMIN' && (
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Official Society Designation</label>
              <input
                type="text"
                required
                value={formData.designation}
                onChange={(e) => setFormData({ ...formData, designation: e.target.value })}
                placeholder="e.g. Estate Facility Manager, RWA President, Secretary"
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-indigo-500"
              />
            </div>
          )}

          {/* Role-Specific Fields: SUPER_ADMIN */}
          {roleType === 'SUPER_ADMIN' && (
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Platform Security Authorization Key</label>
              <input
                type="text"
                required
                value={formData.superAdminKey}
                onChange={(e) => setFormData({ ...formData, superAdminKey: e.target.value })}
                placeholder="Enter Super Admin Security Key"
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-purple-500 font-mono"
              />
              <span className="text-[10px] text-gray-500 mt-1 block">Default Authorization Key: COOKCIRCLE_SUPER_2026</span>
            </div>
          )}

          <button
            type="submit"
            disabled={submitting || isLoadingAuth}
            className={`w-full py-3 rounded-xl font-bold text-xs text-white shadow-lg transition-all flex items-center justify-center gap-1.5 disabled:opacity-50 ${
              roleType === 'RESIDENT'
                ? 'bg-brand-green hover:bg-emerald-600'
                : roleType === 'COOK'
                ? 'bg-brand-orange hover:bg-orange-600'
                : roleType === 'SOCIETY_ADMIN'
                ? 'bg-indigo-600 hover:bg-indigo-500'
                : 'bg-purple-600 hover:bg-purple-500'
            }`}
          >
            <span>{submitting || isLoadingAuth ? 'Creating Account...' : `Register & Enter ${roleType.replace('_', ' ')} Portal`}</span>
            <ArrowRight className="w-3.5 h-3.5" />
          </button>
        </form>

        <p className="text-center text-xs text-gray-400">
          Already registered?{' '}
          <Link to="/login" className="text-brand-greenLight font-semibold hover:underline">
            Log In Here
          </Link>
        </p>
      </div>
    </div>
  );
}
