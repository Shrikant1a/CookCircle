import React from 'react';
import { Outlet } from 'react-router-dom';
import DemoSwitcher from '../components/common/DemoSwitcher';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';

export default function PublicLayout() {
  return (
    <div className="min-h-screen flex flex-col bg-[#0A0A0A] text-gray-100">
      <DemoSwitcher />
      <Navbar />
      <main className="flex-1">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}

