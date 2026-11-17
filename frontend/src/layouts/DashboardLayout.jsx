import React from 'react';
import { Outlet } from 'react-router-dom';
import DemoSwitcher from '../components/common/DemoSwitcher';
import Navbar from '../components/common/Navbar';
import Sidebar from '../components/common/Sidebar';

export default function DashboardLayout() {
  return (
    <div className="min-h-screen flex flex-col bg-[#0A0A0A] text-gray-100">
      <DemoSwitcher />
      <Navbar />
      <div className="flex-1 flex overflow-hidden">
        <Sidebar />
        <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 bg-[#0D0D0D]">
          <div className="max-w-7xl mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}

