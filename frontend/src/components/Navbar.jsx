import { useAuth } from '../context/AuthContext';

export default function Navbar({ title }) {
  const { user } = useAuth();

  const initials = user?.email
    ? user.email.slice(0, 2).toUpperCase()
    : 'U';

  const roleLabel = user?.role === 'ROLE_ADMIN' || user?.role === 'ADMIN'
    ? 'Admin'
    : 'User';

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <span className="navbar-title">{title}</span>
      </div>
      <div className="navbar-right">
        <div className="navbar-user">
          <div className="navbar-avatar">{initials}</div>
          <div className="navbar-user-info">
            <span style={{ fontSize: '0.82rem', fontWeight: 500, color: 'var(--text-primary)' }}>
              {user?.email}
            </span>
            <span className="navbar-user-email">
              <span className="navbar-role-badge">{roleLabel}</span>
            </span>
          </div>
        </div>
      </div>
    </nav>
  );
}
