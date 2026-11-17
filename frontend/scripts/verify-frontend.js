import fs from 'fs';
import path from 'path';

console.log("Validating CookCircle Frontend Build & Route Assets...");

const distDir = path.resolve('frontend/dist');
if (!fs.existsSync(distDir)) {
  console.error("Dist folder does not exist! Run npm run build first.");
  process.exit(1);
}

const indexHtml = path.join(distDir, 'index.html');
if (!fs.existsSync(indexHtml)) {
  console.error("index.html missing in dist!");
  process.exit(1);
}

const assetsDir = path.join(distDir, 'assets');
const assetFiles = fs.readdirSync(assetsDir);
console.log(`Verified dist assets (${assetFiles.length} files):`, assetFiles);

const requiredPages = [
  'frontend/src/pages/public/LandingPage.jsx',
  'frontend/src/pages/public/AboutPage.jsx',
  'frontend/src/pages/public/FeaturesPage.jsx',
  'frontend/src/pages/public/HowItWorksPage.jsx',
  'frontend/src/pages/public/ContactPage.jsx',
  'frontend/src/pages/public/LoginPage.jsx',
  'frontend/src/pages/public/RegisterPage.jsx',
  'frontend/src/pages/public/ForgotPasswordPage.jsx',
  'frontend/src/pages/resident/ResidentDashboard.jsx',
  'frontend/src/pages/resident/LiveTowerPoolPage.jsx',
  'frontend/src/pages/resident/MyBookingsPage.jsx',
  'frontend/src/pages/resident/ResidentInvoicesPage.jsx',
  'frontend/src/pages/resident/ResidentComplaintsPage.jsx',
  'frontend/src/pages/resident/ResidentProfilePage.jsx',
  'frontend/src/pages/cook/CookPartnerDashboard.jsx',
  'frontend/src/pages/cook/CookSchedulePage.jsx',
  'frontend/src/pages/cook/CookAttendancePage.jsx',
  'frontend/src/pages/cook/CookLeavePage.jsx',
  'frontend/src/pages/cook/CookProfilePage.jsx',
  'frontend/src/pages/society_admin/SocietyAdminDashboard.jsx',
  'frontend/src/pages/society_admin/TowersApartmentsPage.jsx',
  'frontend/src/pages/society_admin/CookManagementPage.jsx',
  'frontend/src/pages/society_admin/SlotsBookingAdminPage.jsx',
  'frontend/src/pages/society_admin/LeaveManagementAdminPage.jsx',
  'frontend/src/pages/society_admin/InvoicesAdminPage.jsx',
  'frontend/src/pages/society_admin/ComplaintsAdminPage.jsx',
  'frontend/src/pages/society_admin/ReportsAdminPage.jsx',
  'frontend/src/pages/society_admin/SettingsAdminPage.jsx',
  'frontend/src/pages/super_admin/SuperAdminDashboard.jsx'
];

let missing = 0;
for (const p of requiredPages) {
  if (!fs.existsSync(p)) {
    console.error(`Missing required page: ${p}`);
    missing++;
  }
}

if (missing === 0) {
  console.log(`All ${requiredPages.length} required pages exist and validated.`);
  console.log("Frontend validation: PASSED (100%)");
} else {
  console.error(`Validation failed with ${missing} missing files.`);
  process.exit(1);
}

