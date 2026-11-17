import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ROLES } from './context/AuthContext';
import ProtectedRoute from './components/common/ProtectedRoute';

// Layouts
import PublicLayout from './layouts/PublicLayout';
import DashboardLayout from './layouts/DashboardLayout';

// Public Pages
import LandingPage from './pages/public/LandingPage';
import AboutPage from './pages/public/AboutPage';
import FeaturesPage from './pages/public/FeaturesPage';
import HowItWorksPage from './pages/public/HowItWorksPage';
import ContactPage from './pages/public/ContactPage';
import LoginPage from './pages/public/LoginPage';
import RegisterPage from './pages/public/RegisterPage';
import ForgotPasswordPage from './pages/public/ForgotPasswordPage';
import UserManualPage from './pages/public/UserManualPage';

// Resident Pages
import ResidentDashboard from './pages/resident/ResidentDashboard';
import LiveTowerPoolPage from './pages/resident/LiveTowerPoolPage';
import MyBookingsPage from './pages/resident/MyBookingsPage';
import ResidentInvoicesPage from './pages/resident/ResidentInvoicesPage';
import ResidentComplaintsPage from './pages/resident/ResidentComplaintsPage';
import ResidentProfilePage from './pages/resident/ResidentProfilePage';

// Cook Pages
import CookPartnerDashboard from './pages/cook/CookPartnerDashboard';
import CookSchedulePage from './pages/cook/CookSchedulePage';
import CookAttendancePage from './pages/cook/CookAttendancePage';
import CookLeavePage from './pages/cook/CookLeavePage';
import CookProfilePage from './pages/cook/CookProfilePage';

// Society Admin Pages
import SocietyAdminDashboard from './pages/society_admin/SocietyAdminDashboard';
import TowersApartmentsPage from './pages/society_admin/TowersApartmentsPage';
import CookManagementPage from './pages/society_admin/CookManagementPage';
import SlotsBookingAdminPage from './pages/society_admin/SlotsBookingAdminPage';
import LeaveManagementAdminPage from './pages/society_admin/LeaveManagementAdminPage';
import InvoicesAdminPage from './pages/society_admin/InvoicesAdminPage';
import ComplaintsAdminPage from './pages/society_admin/ComplaintsAdminPage';
import ReportsAdminPage from './pages/society_admin/ReportsAdminPage';
import SettingsAdminPage from './pages/society_admin/SettingsAdminPage';

// Super Admin Pages
import SuperAdminDashboard from './pages/super_admin/SuperAdminDashboard';

export default function App() {
  return (
    <Routes>
      {/* Public Pages */}
      <Route element={<PublicLayout />}>
        <Route path="/" element={<LandingPage />} />
        <Route path="/about" element={<AboutPage />} />
        <Route path="/features" element={<FeaturesPage />} />
        <Route path="/how-it-works" element={<HowItWorksPage />} />
        <Route path="/manual" element={<UserManualPage />} />
        <Route path="/user-manual" element={<UserManualPage />} />
        <Route path="/contact" element={<ContactPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      </Route>

      {/* Resident Experience Routes */}
      <Route
        path="/resident"
        element={
          <ProtectedRoute allowedRoles={[ROLES.RESIDENT]}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<ResidentDashboard />} />
        <Route path="pool" element={<LiveTowerPoolPage />} />
        <Route path="bookings" element={<MyBookingsPage />} />
        <Route path="cook" element={<ResidentDashboard />} />
        <Route path="invoices" element={<ResidentInvoicesPage />} />
        <Route path="complaints" element={<ResidentComplaintsPage />} />
        <Route path="profile" element={<ResidentProfilePage />} />
      </Route>

      {/* Cook Partner Routes */}
      <Route
        path="/cook"
        element={
          <ProtectedRoute allowedRoles={[ROLES.COOK]}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<CookPartnerDashboard />} />
        <Route path="route" element={<CookPartnerDashboard />} />
        <Route path="schedule" element={<CookSchedulePage />} />
        <Route path="attendance" element={<CookAttendancePage />} />
        <Route path="leave" element={<CookLeavePage />} />
        <Route path="profile" element={<CookProfilePage />} />
      </Route>

      {/* Society Admin Routes */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={[ROLES.SOCIETY_ADMIN]}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<SocietyAdminDashboard />} />
        <Route path="towers" element={<TowersApartmentsPage />} />
        <Route path="cooks" element={<CookManagementPage />} />
        <Route path="backup-dispatch" element={<SocietyAdminDashboard />} />
        <Route path="slots" element={<SlotsBookingAdminPage />} />
        <Route path="leaves" element={<LeaveManagementAdminPage />} />
        <Route path="invoices" element={<InvoicesAdminPage />} />
        <Route path="complaints" element={<ComplaintsAdminPage />} />
        <Route path="reports" element={<ReportsAdminPage />} />
        <Route path="settings" element={<SettingsAdminPage />} />
      </Route>

      {/* Super Admin Routes */}
      <Route
        path="/super-admin"
        element={
          <ProtectedRoute allowedRoles={[ROLES.SUPER_ADMIN]}>
            <DashboardLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<SuperAdminDashboard />} />
        <Route path="societies" element={<SuperAdminDashboard />} />
        <Route path="admins" element={<SuperAdminDashboard />} />
        <Route path="activity" element={<SuperAdminDashboard />} />
        <Route path="settings" element={<SuperAdminDashboard />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

