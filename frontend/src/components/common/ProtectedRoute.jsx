import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth, ROLES } from '../../context/AuthContext';
import { useToast } from '../../context/ToastContext';

export default function ProtectedRoute({ allowedRoles, children }) {
  const { activeRole, currentUser, isAuthenticated } = useAuth();
  const location = useLocation();
  const { showToast } = useToast();

  // 1. If not logged in, must login first
  if (!isAuthenticated || !currentUser || activeRole === ROLES.GUEST) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 2. Role-based check: user can ONLY access the portal of the type they logged in as
  if (allowedRoles && !allowedRoles.includes(activeRole)) {
    let target = '/resident';
    if (activeRole === ROLES.COOK) target = '/cook';
    else if (activeRole === ROLES.SOCIETY_ADMIN) target = '/admin';
    else if (activeRole === ROLES.SUPER_ADMIN) target = '/super-admin';

    return <Navigate to={target} replace />;
  }

  return children;
}

