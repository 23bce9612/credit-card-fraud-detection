import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import UserDashboard from './pages/UserDashboard';
import UserTransactions from './pages/UserTransactions';
import AdminDashboard from './pages/AdminDashboard';
import AdminTransactions from './pages/AdminTransactions';
import AdminReview from './pages/AdminReview';
import AdminUsers from './pages/AdminUsers';

// Redirect to login if not authenticated
function RequireAuth({ children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return children;
}

// Redirect to dashboard if not admin
function RequireAdmin({ children }) {
  const { user, isAdmin } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (!isAdmin) return <Navigate to="/dashboard" replace />;
  return children;
}

// Redirect authenticated users away from auth pages
function RedirectIfAuth({ children }) {
  const { user, isAdmin } = useAuth();
  if (user) return <Navigate to={isAdmin ? '/admin' : '/dashboard'} replace />;
  return children;
}

function AppRoutes() {
  return (
    <Routes>
      {/* Public */}
      <Route
        path="/login"
        element={<RedirectIfAuth><LoginPage /></RedirectIfAuth>}
      />
      <Route
        path="/register"
        element={<RedirectIfAuth><RegisterPage /></RedirectIfAuth>}
      />

      {/* User routes */}
      <Route
        path="/dashboard"
        element={<RequireAuth><UserDashboard /></RequireAuth>}
      />
      <Route
        path="/my-transactions"
        element={<RequireAuth><UserTransactions /></RequireAuth>}
      />

      {/* Admin routes */}
      <Route
        path="/admin"
        element={<RequireAdmin><AdminDashboard /></RequireAdmin>}
      />
      <Route
        path="/admin/transactions"
        element={<RequireAdmin><AdminTransactions /></RequireAdmin>}
      />
      <Route
        path="/admin/review"
        element={<RequireAdmin><AdminReview /></RequireAdmin>}
      />
      <Route
        path="/admin/users"
        element={<RequireAdmin><AdminUsers /></RequireAdmin>}
      />

      {/* Default redirect */}
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}
