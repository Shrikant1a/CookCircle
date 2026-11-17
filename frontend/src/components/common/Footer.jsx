import React from 'react';
import { Link } from 'react-router-dom';
import { ChefHat, ShieldCheck, Heart, Sparkles, Phone, Mail, MapPin } from 'lucide-react';

export default function Footer() {
  return (
    <footer className="bg-[#0D0D0D] border-t border-zinc-800/80 text-gray-400 text-sm mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
          {/* Brand Info */}
          <div className="space-y-4 md:col-span-1">
            <Link to="/" className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-lg bg-brand-green flex items-center justify-center">
                <ChefHat className="w-5 h-5 text-white" />
              </div>
              <span className="text-lg font-bold text-white tracking-tight">
                COOK<span className="text-brand-greenLight">CIRCLE</span>
              </span>
            </Link>
            <p className="text-xs text-gray-400 leading-relaxed">
              Hyperlocal shared cooking infrastructure for urban housing societies.
              Save 70% on daily meals with background-verified cooks and guaranteed standby replacements.
            </p>
            <div className="flex items-center gap-2 text-xs text-brand-greenLight bg-emerald-950/40 border border-emerald-800/40 px-3 py-1.5 rounded-lg w-fit">
              <ShieldCheck className="w-4 h-4 text-brand-greenLight" />
              <span>100% Aadhaar & Police Verified</span>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-white font-semibold text-xs uppercase tracking-wider mb-4">Platform</h4>
            <ul className="space-y-2 text-xs">
              <li><Link to="/features" className="hover:text-white transition-colors">Features & Tech Stack</Link></li>
              <li><Link to="/how-it-works" className="hover:text-white transition-colors">How Tower Pooling Works</Link></li>
              <li><Link to="/resident" className="hover:text-white transition-colors">Resident Experience</Link></li>
              <li><Link to="/cook" className="hover:text-white transition-colors">Cook Partner Portal</Link></li>
              <li><Link to="/admin" className="hover:text-white transition-colors">Society Operations Console</Link></li>
            </ul>
          </div>

          {/* Societies & Cities */}
          <div>
            <h4 className="text-white font-semibold text-xs uppercase tracking-wider mb-4">Active Communities</h4>
            <ul className="space-y-2 text-xs text-gray-400">
              <li>Hiranandani Gardens, Powai (Mumbai)</li>
              <li>Prestige Shantiniketan, Whitefield (Bengaluru)</li>
              <li>Sobha Dream Acres, Panathur (Bengaluru)</li>
              <li>Amanora Park Town, Hadapsar (Pune)</li>
              <li>DLF Phase 5, Golf Course Rd (Gurugram)</li>
            </ul>
          </div>

          {/* Contact & Support */}
          <div>
            <h4 className="text-white font-semibold text-xs uppercase tracking-wider mb-4">Support & Office</h4>
            <ul className="space-y-2.5 text-xs text-gray-400">
              <li className="flex items-center gap-2">
                <Mail className="w-3.5 h-3.5 text-brand-greenLight" />
                <span>support@cookcircle.com</span>
              </li>
              <li className="flex items-center gap-2">
                <Phone className="w-3.5 h-3.5 text-brand-greenLight" />
                <span>+91 1800 200 4829 (Toll Free)</span>
              </li>
              <li className="flex items-start gap-2">
                <MapPin className="w-3.5 h-3.5 text-brand-greenLight flex-shrink-0 mt-0.5" />
                <span>CookCircle Labs, BKC & Indiranagar Tech Parks, India</span>
              </li>
            </ul>
          </div>
        </div>

        <div className="mt-12 pt-6 border-t border-zinc-800/60 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-gray-500">
          <p>© 2026 CookCircle Technologies Pvt. Ltd. All rights reserved.</p>
          <div className="flex items-center gap-6">
            <Link to="/privacy" className="hover:text-gray-300 transition-colors">Privacy Policy</Link>
            <Link to="/terms" className="hover:text-gray-300 transition-colors">Terms of Service</Link>
            <Link to="/security" className="hover:text-gray-300 transition-colors">Security & Verification</Link>
          </div>
        </div>
      </div>
    </footer>
  );
}

