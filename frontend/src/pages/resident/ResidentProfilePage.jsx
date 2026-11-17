import React, { useState, useRef } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useApp } from '../../context/AppContext';
import { useToast } from '../../context/ToastContext';
import {
  User,
  Building2,
  Phone,
  Mail,
  Utensils,
  Users,
  Save,
  Camera,
  Upload,
  Image as ImageIcon,
  Check,
  X,
  Trash2,
  RefreshCw,
  Sparkles
} from 'lucide-react';

// Preset avatar collection for quick 1-click selection
const AVATAR_PRESETS = [
  { id: 1, label: 'Professional Male', url: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=250' },
  { id: 2, label: 'Modern Professional', url: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=250' },
  { id: 3, label: 'Friendly Resident', url: 'https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?auto=format&fit=crop&q=80&w=250' },
  { id: 4, label: 'Smiling Professional', url: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=250' },
  { id: 5, label: 'Young Professional', url: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=250' },
  { id: 6, label: 'Culinary Enthusiast', url: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=250' },
  { id: 7, label: 'Minimalist 3D Male', url: 'https://images.unsplash.com/photo-1628157582853-a796fa650a6a?auto=format&fit=crop&q=80&w=250' },
  { id: 8, label: 'Minimalist 3D Female', url: 'https://images.unsplash.com/photo-1560250097-0b93528c311a?auto=format&fit=crop&q=80&w=250' }
];

export default function ResidentProfilePage() {
  const { currentUser, updateUserProfile, currentSociety, currentTower } = useAuth();
  const { showToast } = useToast();
  const fileInputRef = useRef(null);

  // Profile Form States
  const [fullName, setFullName] = useState(currentUser?.fullName || 'Shrikant Aher');
  const [phone, setPhone] = useState(currentUser?.phone || '+91 81496 42840');
  const [flatNumber, setFlatNumber] = useState(currentUser?.flatNumber || '402');
  const [dietaryPref, setDietaryPref] = useState(currentUser?.dietaryPref || 'Pure Veg');
  const [householdSize, setHouseholdSize] = useState(currentUser?.householdSize || '3-4 members');

  // Avatar Modal States
  const [isPhotoModalOpen, setIsPhotoModalOpen] = useState(false);
  const [tempAvatarUrl, setTempAvatarUrl] = useState(currentUser?.avatar || AVATAR_PRESETS[0].url);
  const [customUrlInput, setCustomUrlInput] = useState('');
  const [uploading, setUploading] = useState(false);

  // Handle local file upload (converts file to Base64 data URL)
  const handleFileUpload = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      showToast('Please select a valid image file (PNG, JPG, or WebP).', 'error');
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      showToast('Image size exceeds 5MB. Please choose a smaller photo.', 'error');
      return;
    }

    setUploading(true);
    const reader = new FileReader();
    reader.onload = () => {
      const result = reader.result;
      setTempAvatarUrl(result);
      if (updateUserProfile) {
        updateUserProfile({ avatar: result });
      }
      setUploading(false);
      setIsPhotoModalOpen(false);
      showToast('Profile photo uploaded and saved successfully!', 'success');
    };
    reader.onerror = () => {
      setUploading(false);
      showToast('Failed to read image file. Please try again.', 'error');
    };
    reader.readAsDataURL(file);
  };

  // Apply custom URL
  const handleApplyCustomUrl = (e) => {
    e.preventDefault();
    if (!customUrlInput.trim()) return;
    setTempAvatarUrl(customUrlInput.trim());
    if (updateUserProfile) {
      updateUserProfile({ avatar: customUrlInput.trim() });
    }
    setCustomUrlInput('');
    setIsPhotoModalOpen(false);
    showToast('Profile photo updated from URL!', 'success');
  };

  // Select preset avatar
  const handleSelectPreset = (url) => {
    setTempAvatarUrl(url);
    if (updateUserProfile) {
      updateUserProfile({ avatar: url });
    }
    setIsPhotoModalOpen(false);
    showToast('Profile avatar updated!', 'success');
  };

  // Remove photo (revert to default)
  const handleRemovePhoto = () => {
    const defaultUrl = AVATAR_PRESETS[0].url;
    setTempAvatarUrl(defaultUrl);
    if (updateUserProfile) {
      updateUserProfile({ avatar: defaultUrl });
    }
    setIsPhotoModalOpen(false);
    showToast('Reset to default profile photo.', 'info');
  };

  // Save general profile details
  const handleSaveProfile = (e) => {
    e.preventDefault();
    if (updateUserProfile) {
      updateUserProfile({
        fullName,
        phone,
        flatNumber,
        dietaryPref,
        householdSize
      });
    }
    showToast('Profile preferences saved successfully!', 'success');
  };

  const currentAvatar = currentUser?.avatar || tempAvatarUrl;

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Page Title */}
      <div className="pb-2">
        <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
          Resident Profile & Preferences
        </h1>
        <p className="text-xs text-gray-400 mt-1">
          Manage your flat configuration, dietary preferences, and billing contact details.
        </p>
      </div>

      {/* Main Profile Card */}
      <div className="bg-[#151515] border border-zinc-800 rounded-2xl p-6 sm:p-8 shadow-xl space-y-6">
        {/* Profile Avatar Header with Interactive Edit Button */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-5 pb-6 border-b border-zinc-800">
          <div className="flex items-center gap-4">
            {/* Clickable Profile Photo Container */}
            <div className="relative group">
              <img
                src={currentAvatar}
                alt={currentUser?.fullName || fullName}
                className="w-20 h-20 rounded-2xl object-cover border-2 border-brand-green/60 shadow-lg group-hover:brightness-75 transition-all"
              />
              {/* Camera Hover Overlay */}
              <button
                type="button"
                onClick={() => setIsPhotoModalOpen(true)}
                className="absolute inset-0 rounded-2xl bg-black/50 opacity-0 group-hover:opacity-100 flex flex-col items-center justify-center text-white transition-opacity cursor-pointer"
                title="Change Profile Picture"
              >
                <Camera className="w-5 h-5 mb-0.5 text-brand-greenLight" />
                <span className="text-[10px] font-semibold">Change</span>
              </button>

              {/* Small Camera Badge Icon */}
              <button
                type="button"
                onClick={() => setIsPhotoModalOpen(true)}
                className="absolute -bottom-1 -right-1 p-1.5 rounded-full bg-brand-green text-white shadow-md hover:bg-emerald-600 transition-colors cursor-pointer"
                title="Edit Photo"
              >
                <Camera className="w-3.5 h-3.5" />
              </button>
            </div>

            {/* Name & Credentials */}
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-xl font-bold text-white">{currentUser?.fullName || fullName}</h3>
                <button
                  type="button"
                  onClick={() => setIsPhotoModalOpen(true)}
                  className="hidden sm:inline-flex items-center gap-1 text-[11px] text-brand-greenLight hover:underline font-semibold"
                >
                  Edit Photo
                </button>
              </div>
              <p className="text-xs text-gray-400 mt-0.5">
                {currentUser?.email || 'shrikantaher2004@gmail.com'} • {currentUser?.phone || phone}
              </p>
              <div className="flex items-center gap-2 mt-2">
                <span className="px-2.5 py-0.5 rounded text-[11px] font-bold bg-zinc-800 text-gray-300 border border-zinc-700">
                  Flat {currentUser?.flatNumber || flatNumber}
                </span>
                <span className="px-2.5 py-0.5 rounded text-[11px] font-bold bg-brand-green/20 text-brand-greenLight border border-brand-green/30">
                  Active Pool Member
                </span>
              </div>
            </div>
          </div>

          {/* Direct Upload Shortcut Button */}
          <div>
            <button
              type="button"
              onClick={() => setIsPhotoModalOpen(true)}
              className="px-3.5 py-2 rounded-xl bg-zinc-900 hover:bg-zinc-800 border border-zinc-700 text-xs font-semibold text-gray-200 hover:text-white transition-all flex items-center gap-2 shadow-sm"
            >
              <Camera className="w-4 h-4 text-brand-greenLight" />
              <span>Change Profile Picture</span>
            </button>
          </div>
        </div>

        {/* Profile Information & Preferences Form */}
        <form onSubmit={handleSaveProfile} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Full Name</label>
              <input
                type="text"
                required
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Phone Number</label>
              <input
                type="text"
                required
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Society</label>
              <input
                type="text"
                disabled
                value={currentSociety?.name || 'Hiranandani Gardens'}
                className="w-full bg-zinc-900 border border-zinc-800/80 rounded-xl px-3.5 py-2.5 text-xs text-gray-500 cursor-not-allowed"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Tower / Wing</label>
              <input
                type="text"
                disabled
                value={currentTower?.name || 'Tower B - Wing 1'}
                className="w-full bg-zinc-900 border border-zinc-800/80 rounded-xl px-3.5 py-2.5 text-xs text-gray-500 cursor-not-allowed"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Flat Number</label>
              <input
                type="text"
                value={flatNumber}
                onChange={(e) => setFlatNumber(e.target.value)}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white focus:outline-none focus:border-brand-green"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Dietary Preference</label>
              <select
                value={dietaryPref}
                onChange={(e) => setDietaryPref(e.target.value)}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white cursor-pointer focus:border-brand-green"
              >
                <option value="Pure Veg">Pure Veg</option>
                <option value="Jain">Jain (No onion/garlic/potatoes)</option>
                <option value="Non-Veg">Non-Veg OK</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-gray-300 mb-1">Household Size</label>
              <select
                value={householdSize}
                onChange={(e) => setHouseholdSize(e.target.value)}
                className="w-full bg-zinc-900 border border-zinc-800 rounded-xl px-3.5 py-2.5 text-xs text-white cursor-pointer focus:border-brand-green"
              >
                <option value="1-2 members">1–2 members</option>
                <option value="3-4 members">3–4 members</option>
                <option value="5+ members">5+ members</option>
              </select>
            </div>
          </div>

          <div className="pt-4 flex justify-end">
            <button
              type="submit"
              className="px-6 py-2.5 rounded-xl bg-brand-green hover:bg-emerald-600 text-white font-bold text-xs shadow-lg shadow-emerald-950/40 transition-all flex items-center gap-2"
            >
              <Save className="w-3.5 h-3.5" />
              <span>Save Profile Changes</span>
            </button>
          </div>
        </form>
      </div>

      {/* Hidden File Input for Device Photo Upload */}
      <input
        type="file"
        ref={fileInputRef}
        onChange={handleFileUpload}
        accept="image/png,image/jpeg,image/webp,image/jpg"
        className="hidden"
      />

      {/* EDIT PROFILE PICTURE MODAL */}
      {isPhotoModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
          <div className="bg-[#181818] border border-zinc-700 max-w-lg w-full rounded-2xl p-6 shadow-2xl space-y-5 relative">
            {/* Modal Header */}
            <div className="flex items-center justify-between pb-3 border-b border-zinc-800">
              <div className="flex items-center gap-2">
                <Camera className="w-5 h-5 text-brand-greenLight" />
                <h3 className="text-lg font-bold text-white">Edit Profile Picture</h3>
              </div>
              <button
                type="button"
                onClick={() => setIsPhotoModalOpen(false)}
                className="p-1 rounded-lg text-gray-400 hover:text-white hover:bg-zinc-800 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Current Preview */}
            <div className="flex items-center gap-4 p-4 rounded-xl bg-zinc-900 border border-zinc-800">
              <img
                src={currentAvatar}
                alt="Current Preview"
                className="w-16 h-16 rounded-2xl object-cover border-2 border-brand-green"
              />
              <div className="flex-1 text-xs">
                <p className="font-semibold text-white">Current Profile Photo</p>
                <p className="text-[11px] text-gray-400 mt-0.5">
                  Visible to your cook partner, tower pool members, and estate admin.
                </p>
              </div>
              <button
                type="button"
                onClick={handleRemovePhoto}
                className="p-2 rounded-lg bg-zinc-800 hover:bg-rose-950/40 text-gray-400 hover:text-rose-400 border border-zinc-700 transition-colors text-xs flex items-center gap-1"
                title="Reset to default avatar"
              >
                <Trash2 className="w-3.5 h-3.5" />
                <span className="hidden sm:inline">Reset</span>
              </button>
            </div>

            {/* Option 1: Upload from Computer/Device */}
            <div className="space-y-2">
              <label className="text-xs font-bold text-gray-300 block">
                Option 1: Upload from Device (PC or Phone)
              </label>
              <button
                type="button"
                disabled={uploading}
                onClick={() => fileInputRef.current?.click()}
                className="w-full py-3 px-4 rounded-xl border-2 border-dashed border-zinc-700 hover:border-brand-green bg-zinc-900/60 hover:bg-zinc-900 text-gray-300 hover:text-white transition-all flex items-center justify-center gap-2 text-xs font-semibold cursor-pointer group"
              >
                <Upload className="w-4 h-4 text-brand-greenLight group-hover:scale-110 transition-transform" />
                <span>{uploading ? 'Reading Photo...' : 'Click to Browse & Upload Image (PNG, JPG, WebP)'}</span>
              </button>
            </div>

            {/* Option 2: Select from Ready-Made Avatars */}
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <label className="text-xs font-bold text-gray-300">
                  Option 2: Choose from Avatar Presets
                </label>
                <span className="text-[10px] text-gray-500">Instant 1-click select</span>
              </div>
              <div className="grid grid-cols-4 sm:grid-cols-8 gap-2">
                {AVATAR_PRESETS.map((preset) => {
                  const isSelected = currentAvatar === preset.url;
                  return (
                    <button
                      key={preset.id}
                      type="button"
                      onClick={() => handleSelectPreset(preset.url)}
                      className={`relative rounded-xl overflow-hidden aspect-square border-2 transition-all hover:scale-105 ${
                        isSelected
                          ? 'border-brand-green shadow-md ring-2 ring-emerald-500/50'
                          : 'border-zinc-800 hover:border-zinc-600'
                      }`}
                      title={preset.label}
                    >
                      <img
                        src={preset.url}
                        alt={preset.label}
                        className="w-full h-full object-cover"
                      />
                      {isSelected && (
                        <div className="absolute inset-0 bg-brand-green/30 flex items-center justify-center">
                          <Check className="w-4 h-4 text-white drop-shadow" />
                        </div>
                      )}
                    </button>
                  );
                })}
              </div>
            </div>

            {/* Option 3: Paste Custom Web Image URL */}
            <form onSubmit={handleApplyCustomUrl} className="space-y-2 pt-2 border-t border-zinc-800">
              <label className="text-xs font-bold text-gray-300 block">
                Option 3: Paste Image Web URL
              </label>
              <div className="flex gap-2">
                <input
                  type="url"
                  value={customUrlInput}
                  onChange={(e) => setCustomUrlInput(e.target.value)}
                  placeholder="https://example.com/my-photo.jpg"
                  className="flex-1 bg-zinc-900 border border-zinc-800 rounded-xl px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-green"
                />
                <button
                  type="submit"
                  disabled={!customUrlInput.trim()}
                  className="px-4 py-2 rounded-xl bg-brand-green hover:bg-emerald-600 disabled:opacity-40 text-white font-bold text-xs transition-colors"
                >
                  Apply URL
                </button>
              </div>
            </form>

            {/* Modal Footer */}
            <div className="pt-2 border-t border-zinc-800 flex justify-end">
              <button
                type="button"
                onClick={() => setIsPhotoModalOpen(false)}
                className="px-4 py-2 rounded-xl bg-zinc-800 hover:bg-zinc-700 text-gray-200 text-xs font-semibold transition-colors"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
