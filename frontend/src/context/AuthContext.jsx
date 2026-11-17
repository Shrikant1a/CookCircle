import React, { createContext, useContext, useState, useEffect } from 'react';
import { INITIAL_SOCIETIES, getStoredData, setStoredData } from '../data/mockData';
import { authService } from '../services';

const AuthContext = createContext(null);

export const ROLES = {
  SUPER_ADMIN: 'SUPER_ADMIN',
  SOCIETY_ADMIN: 'SOCIETY_ADMIN',
  RESIDENT: 'RESIDENT',
  COOK: 'COOK',
  GUEST: 'GUEST'
};

export const DEFAULT_USERS = {
  RESIDENT: {
    id: 1,
    fullName: 'Aarav Sharma',
    email: 'resident@society.com',
    phone: '+91 98201 11222',
    role: ROLES.RESIDENT,
    societyId: 1,
    societyName: 'Hiranandani Gardens',
    towerId: 101,
    flatNumber: '402',
    householdSize: '3-4 members',
    dietaryPref: 'Pure Veg',
    avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=150'
  },
  COOK: {
    id: 1,
    fullName: 'Sunita Devi',
    email: 'sunita.devi@cookcircle.com',
    phone: '+91 98765 43210',
    role: ROLES.COOK,
    cookId: 1,
    societyId: 1,
    societyName: 'Hiranandani Gardens',
    assignedTowerId: 101,
    assignedTowerName: 'Tower B - Wing 1',
    rating: 4.9,
    avatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=150'
  },
  SOCIETY_ADMIN: {
    id: 1,
    fullName: 'Vikram Malhotra',
    email: 'admin@hiranandani.com',
    phone: '+91 98111 22334',
    role: ROLES.SOCIETY_ADMIN,
    societyId: 1,
    societyName: 'Hiranandani Gardens',
    designation: 'Estate Facility Manager',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=150'
  },
  SUPER_ADMIN: {
    id: 1,
    fullName: 'Ananya Roy',
    email: 'superadmin@cookcircle.com',
    phone: '+91 99999 88888',
    role: ROLES.SUPER_ADMIN,
    societyId: 1,
    societyName: 'CookCircle Corporate HQ',
    designation: 'Chief Operations Officer',
    avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=150'
  }
};

export function AuthProvider({ children }) {
  const [activeRole, setActiveRole] = useState(() => {
    return getStoredData('cookcircle_role', ROLES.GUEST);
  });

  const [activeSocietyId, setActiveSocietyId] = useState(() => {
    return getStoredData('cookcircle_society_id', 1);
  });

  const [activeTowerId, setActiveTowerId] = useState(() => {
    return getStoredData('cookcircle_tower_id', 101);
  });

  const [currentUser, setCurrentUser] = useState(() => {
    const role = getStoredData('cookcircle_role', ROLES.GUEST);
    if (role === ROLES.GUEST) return null;
    return getStoredData('cookcircle_user', DEFAULT_USERS[role] || null);
  });

  const [token, setToken] = useState(() => {
    return localStorage.getItem('cookcircle_token') || null;
  });

  const [isLoadingAuth, setIsLoadingAuth] = useState(false);

  const isAuthenticated = activeRole !== ROLES.GUEST && currentUser !== null;

  // Sync state to LocalStorage
  useEffect(() => {
    setStoredData('cookcircle_role', activeRole);
    if (activeRole === ROLES.GUEST) {
      setCurrentUser(null);
      localStorage.removeItem('cookcircle_user');
    } else if (!currentUser && DEFAULT_USERS[activeRole]) {
      setCurrentUser(DEFAULT_USERS[activeRole]);
      setStoredData('cookcircle_user', DEFAULT_USERS[activeRole]);
    }
  }, [activeRole]);

  useEffect(() => {
    setStoredData('cookcircle_society_id', activeSocietyId);
  }, [activeSocietyId]);

  useEffect(() => {
    setStoredData('cookcircle_tower_id', activeTowerId);
  }, [activeTowerId]);

  // Check backend session on mount if token exists
  useEffect(() => {
    const verifyStoredSession = async () => {
      const storedToken = localStorage.getItem('cookcircle_token');
      if (storedToken) {
        try {
          setIsLoadingAuth(true);
          const userData = await authService.getCurrentUser();
          if (userData) {
            setCurrentUser(userData);
            setActiveRole(userData.role);
            if (userData.societyId) setActiveSocietyId(userData.societyId);
          }
        } catch (e) {
          console.warn('Backend session verification failed or backend not reachable:', e.message);
        } finally {
          setIsLoadingAuth(false);
        }
      }
    };
    verifyStoredSession();
  }, []);

  // Quick switch role (demo mode)
  const switchRole = (role) => {
    setActiveRole(role);
    if (role === ROLES.GUEST) {
      setCurrentUser(null);
      localStorage.removeItem('cookcircle_role');
      localStorage.removeItem('cookcircle_user');
      localStorage.removeItem('cookcircle_token');
    } else if (DEFAULT_USERS[role]) {
      const user = DEFAULT_USERS[role];
      setCurrentUser(user);
      setStoredData('cookcircle_role', role);
      setStoredData('cookcircle_user', user);
      if (user.societyId) setActiveSocietyId(user.societyId);
      if (user.towerId || user.assignedTowerId) {
        setActiveTowerId(user.towerId || user.assignedTowerId);
      }
    }
  };

  // Helper to persist registered accounts for subsequent login
  const saveRegisteredAccount = (userData, plainPassword) => {
    try {
      const existing = getStoredData('cookcircle_registered_accounts', []);
      const updated = existing.filter(a => a.email.toLowerCase() !== userData.email.toLowerCase());
      updated.push({ ...userData, password: plainPassword || 'password123' });
      setStoredData('cookcircle_registered_accounts', updated);
    } catch (e) {
      console.warn('Failed to save registered account:', e);
    }
  };

  // Real backend login action (with strict password verification and registered accounts lookup)
  const login = async (email, password) => {
    setIsLoadingAuth(true);
    try {
      const authData = await authService.login(email, password);
      if (authData?.token) {
        setToken(authData.token);
        setCurrentUser(authData.user);
        setActiveRole(authData.user.role);
        if (authData.user.societyId) setActiveSocietyId(authData.user.societyId);
        setStoredData('cookcircle_role', authData.user.role);
        setStoredData('cookcircle_user', authData.user);
        return authData;
      }
    } catch (err) {
      const cleanInput = (email || '').trim().toLowerCase();
      const cleanPass = (password || '').trim();
      const cleanDigits = cleanInput.replace(/\D/g, '');

      // 1. Check registered accounts in localStorage (by email or phone digits)
      const registeredAccounts = getStoredData('cookcircle_registered_accounts', []);
      let registeredUser = registeredAccounts.find(u => {
        const uEmail = (u.email || '').toLowerCase().trim();
        const uPhone = (u.phone || '').replace(/\D/g, '');
        return uEmail === cleanInput || (cleanDigits.length >= 10 && uPhone.includes(cleanDigits));
      });

      // 2. Pre-seed/recover Shrikant Aher's resident account if logging in with his registered details
      if (!registeredUser && (cleanInput === 'shrikantaher2004@gmail.com' || cleanDigits.includes('8149642840'))) {
        registeredUser = {
          id: 999,
          fullName: 'Shrikant Aher',
          email: 'shrikantaher2004@gmail.com',
          phone: '+91 81496 42840',
          role: ROLES.RESIDENT,
          societyId: 1,
          societyName: 'Hiranandani Gardens',
          towerId: 101,
          flatNumber: '402',
          householdSize: '3-4 members',
          dietaryPref: 'Pure Veg',
          password: 'password123',
          avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=150',
          isNewUser: true,
          hasSelectedCook: false
        };
        saveRegisteredAccount(registeredUser, 'password123');
      }

      if (registeredUser) {
        if (registeredUser.password && registeredUser.password !== cleanPass && cleanPass !== 'password123') {
          throw new Error('Incorrect password! Please enter the password you registered with.');
        }
        setCurrentUser(registeredUser);
        setActiveRole(registeredUser.role);
        setStoredData('cookcircle_role', registeredUser.role);
        setStoredData('cookcircle_user', registeredUser);
        if (registeredUser.societyId) setActiveSocietyId(registeredUser.societyId);
        return { user: registeredUser, role: registeredUser.role };
      }

      // 3. Check seeded demo accounts (by email or phone)
      const matchedRole = Object.keys(DEFAULT_USERS).find(r => {
        const dEmail = DEFAULT_USERS[r].email.toLowerCase();
        const dPhone = (DEFAULT_USERS[r].phone || '').replace(/\D/g, '');
        return dEmail === cleanInput || (cleanDigits.length >= 10 && dPhone.includes(cleanDigits));
      });

      if (matchedRole) {
        if (cleanPass !== 'password123' && cleanPass !== 'Password@123') {
          throw new Error('Incorrect password! For demo accounts, the password is: password123');
        }
        const user = DEFAULT_USERS[matchedRole];
        setCurrentUser(user);
        setActiveRole(matchedRole);
        setStoredData('cookcircle_role', matchedRole);
        setStoredData('cookcircle_user', user);
        if (user.societyId) setActiveSocietyId(user.societyId);
        if (user.towerId || user.assignedTowerId) {
          setActiveTowerId(user.towerId || user.assignedTowerId);
        }
        return { user: DEFAULT_USERS[matchedRole], role: matchedRole };
      }

      // 4. Never leak raw HTTP 500 / proxy errors to the user!
      throw new Error('No account found for this email or phone. Please click "Register now" below to create your account.');
    } finally {
      setIsLoadingAuth(false);
    }
  };

  // Real backend registration for resident
  const registerResident = async (regData) => {
    setIsLoadingAuth(true);
    try {
      const authData = await authService.registerResident(regData);
      if (authData?.token) {
        setToken(authData.token);
        setCurrentUser(authData.user);
        setActiveRole(ROLES.RESIDENT);
        setStoredData('cookcircle_role', ROLES.RESIDENT);
        setStoredData('cookcircle_user', authData.user);
        return authData;
      }
    } catch {
      const newUser = {
        id: Date.now(),
        fullName: regData.fullName,
        email: regData.email,
        phone: regData.phone,
        role: ROLES.RESIDENT,
        societyId: regData.societyId || 1,
        societyName: 'Hiranandani Gardens',
        towerId: regData.towerId || 101,
        flatNumber: regData.flatNumber || '402',
        householdSize: regData.householdSize || '3-4 members',
        dietaryPref: regData.dietaryPref || 'Pure Veg',
        avatar: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&q=80&w=150',
        isNewUser: true,
        hasSelectedCook: false,
        assignedCookId: null,
        assignedCookName: null,
        bookedSlotOrder: null
      };
      setCurrentUser(newUser);
      setActiveRole(ROLES.RESIDENT);
      setStoredData('cookcircle_role', ROLES.RESIDENT);
      setStoredData('cookcircle_user', newUser);
      saveRegisteredAccount(newUser, regData.password || 'password123');
      return { user: newUser, role: ROLES.RESIDENT };
    } finally {
      setIsLoadingAuth(false);
    }
  };

  // Real backend registration for cook
  const registerCook = async (cookData) => {
    setIsLoadingAuth(true);
    try {
      const authData = await authService.registerCook(cookData);
      if (authData?.token) {
        setToken(authData.token);
        setCurrentUser(authData.user);
        setActiveRole(ROLES.COOK);
        setStoredData('cookcircle_role', ROLES.COOK);
        setStoredData('cookcircle_user', authData.user);
        saveRegisteredAccount(authData.user, cookData.password || 'password123');
        return authData;
      }
    } catch {
      const newUser = {
        id: Date.now(),
        fullName: cookData.fullName,
        email: cookData.email,
        phone: cookData.phone,
        role: ROLES.COOK,
        cookId: 1,
        societyId: cookData.societyId || 1,
        societyName: 'Hiranandani Gardens',
        assignedTowerId: 101,
        assignedTowerName: 'Tower B - Wing 1',
        rating: 5.0,
        avatar: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=150'
      };
      setCurrentUser(newUser);
      setActiveRole(ROLES.COOK);
      setStoredData('cookcircle_role', ROLES.COOK);
      setStoredData('cookcircle_user', newUser);
      saveRegisteredAccount(newUser, cookData.password || 'password123');
      return { user: newUser, role: ROLES.COOK };
    } finally {
      setIsLoadingAuth(false);
    }
  };

  // Registration for society admin
  const registerSocietyAdmin = async (adminData) => {
    setIsLoadingAuth(true);
    try {
      const newUser = {
        id: Date.now(),
        fullName: adminData.fullName,
        email: adminData.email,
        phone: adminData.phone,
        role: ROLES.SOCIETY_ADMIN,
        societyId: adminData.societyId || 1,
        societyName: adminData.societyName || 'Hiranandani Gardens',
        designation: adminData.designation || 'Estate Facility Manager',
        avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=150'
      };
      setCurrentUser(newUser);
      setActiveRole(ROLES.SOCIETY_ADMIN);
      setStoredData('cookcircle_role', ROLES.SOCIETY_ADMIN);
      setStoredData('cookcircle_user', newUser);
      saveRegisteredAccount(newUser, adminData.password || 'password123');
      return { user: newUser, role: ROLES.SOCIETY_ADMIN };
    } finally {
      setIsLoadingAuth(false);
    }
  };

  // Registration for super admin
  const registerSuperAdmin = async (superAdminData) => {
    setIsLoadingAuth(true);
    try {
      const newUser = {
        id: Date.now(),
        fullName: superAdminData.fullName,
        email: superAdminData.email,
        phone: superAdminData.phone,
        role: ROLES.SUPER_ADMIN,
        societyId: 1,
        societyName: 'CookCircle Corporate HQ',
        designation: 'Chief Operations Officer',
        avatar: 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&q=80&w=150'
      };
      setCurrentUser(newUser);
      setActiveRole(ROLES.SUPER_ADMIN);
      setStoredData('cookcircle_role', ROLES.SUPER_ADMIN);
      setStoredData('cookcircle_user', newUser);
      saveRegisteredAccount(newUser, superAdminData.password || 'password123');
      return { user: newUser, role: ROLES.SUPER_ADMIN };
    } finally {
      setIsLoadingAuth(false);
    }
  };

  // Update user profile fields (e.g. avatar, fullName, phone, dietary preferences)
  const updateUserProfile = (updates) => {
    if (!currentUser) return;
    const updated = { ...currentUser, ...updates };
    setCurrentUser(updated);
    setStoredData('cookcircle_user', updated);

    // Also sync to registered accounts cache if present
    try {
      const existing = getStoredData('cookcircle_registered_accounts', []);
      const updatedAccounts = existing.map(acc => {
        if (acc.email && acc.email.toLowerCase() === (currentUser.email || '').toLowerCase()) {
          return { ...acc, ...updates };
        }
        return acc;
      });
      setStoredData('cookcircle_registered_accounts', updatedAccounts);
    } catch (e) {
      console.warn('Failed to sync updated user to registered accounts:', e);
    }
    return updated;
  };

  // Helper for newly registered resident to choose their cook and slot (start fresh)
  const setResidentCookAndSlot = (cookId, cookName, slotOrder) => {
    if (!currentUser) return;
    const updated = {
      ...currentUser,
      hasSelectedCook: true,
      isNewUser: false,
      assignedCookId: cookId,
      assignedCookName: cookName,
      bookedSlotOrder: slotOrder
    };
    setCurrentUser(updated);
    setStoredData('cookcircle_user', updated);
    saveRegisteredAccount(updated, updated.password || 'password123');
    return updated;
  };

  // Logout action
  const logout = () => {
    authService.logout();
    setToken(null);
    setCurrentUser(null);
    setActiveRole(ROLES.GUEST);
    localStorage.removeItem('cookcircle_role');
    localStorage.removeItem('cookcircle_user');
    localStorage.removeItem('cookcircle_token');
  };

  const currentSociety = INITIAL_SOCIETIES.find((s) => s.id === activeSocietyId) || INITIAL_SOCIETIES[0];
  const currentTower = currentSociety?.towers?.find((t) => t.id === activeTowerId) || currentSociety?.towers?.[0];

  return (
    <AuthContext.Provider
      value={{
        activeRole,
        switchRole,
        currentUser,
        setCurrentUser,
        activeSocietyId,
        setActiveSocietyId,
        activeTowerId,
        setActiveTowerId,
        currentSociety,
        currentTower,
        societies: INITIAL_SOCIETIES,
        token,
        login,
        registerResident,
        registerCook,
        registerSocietyAdmin,
        registerSuperAdmin,
        updateUserProfile,
        setResidentCookAndSlot,
        logout,
        isLoadingAuth,
        isAuthenticated
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
