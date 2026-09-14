import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function SidebarLink({ to, icon, children }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
    >
      <span className="sidebar-icon">{icon}</span>
      {children}
    </NavLink>
  );
}

export default function Sidebar() {
  const { isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <div className="sidebar-logo">🛡️</div>
        <div className="sidebar-brand-text">
          <span className="sidebar-brand-name">FraudGuard</span>
          <span className="sidebar-brand-sub">Detection System</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        {!isAdmin && (
          <>
            <span className="sidebar-section-label">My Account</span>
            <SidebarLink to="/dashboard" icon="🏠">Dashboard</SidebarLink>
            <SidebarLink to="/my-transactions" icon="📋">My Transactions</SidebarLink>
          </>
        )}

        {isAdmin && (
          <>
            <span className="sidebar-section-label">Overview</span>
            <SidebarLink to="/admin" icon="📊">Analytics</SidebarLink>

            <span className="sidebar-section-label">Transactions</span>
            <SidebarLink to="/admin/transactions" icon="💳">All Transactions</SidebarLink>
            <SidebarLink to="/admin/review" icon="⚠️">Review Queue</SidebarLink>

            <span className="sidebar-section-label">Management</span>
            <SidebarLink to="/admin/users" icon="👥">Users</SidebarLink>
          </>
        )}
      </nav>

      <div className="sidebar-footer">
        <button className="btn-logout" onClick={handleLogout}>
          <span>🚪</span>
          Sign Out
        </button>
      </div>
    </aside>
  );
}
