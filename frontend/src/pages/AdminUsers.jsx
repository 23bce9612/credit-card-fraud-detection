import { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import LoadingSpinner from '../components/LoadingSpinner';
import client from '../api/client';

function RoleBadge({ role }) {
  const isAdmin = role === 'ADMIN' || role === 'ROLE_ADMIN';
  return (
    <span className={`badge ${isAdmin ? 'badge-admin' : 'badge-user'}`}>
      {isAdmin ? '🔑 Admin' : '👤 User'}
    </span>
  );
}

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');

  useEffect(() => {
    client.get('/admin/users')
      .then((res) => setUsers(res.data))
      .catch(() => setError('Failed to load users.'))
      .finally(() => setLoading(false));
  }, []);

  const filtered = users.filter(
    (u) =>
      u.email?.toLowerCase().includes(search.toLowerCase()) ||
      u.fullName?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="User Management" />
        <div className="page-content">
          <h1 className="page-title">Users</h1>
          <p className="page-subtitle">
            Manage registered users — {users.length} total accounts
          </p>

          {/* Search */}
          <div className="filters-bar" style={{ marginBottom: '24px' }}>
            <input
              id="user-search"
              className="filter-input"
              placeholder="🔍 Search by name or email..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={{ minWidth: '300px' }}
            />
            {search && (
              <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                {filtered.length} result{filtered.length !== 1 ? 's' : ''}
              </span>
            )}
          </div>

          <div className="card">
            <div className="card-header">
              <h2 className="card-title">👥 Registered Users</h2>
            </div>
            <div className="card-body" style={{ padding: 0 }}>
              {loading && <LoadingSpinner text="Loading users..." />}
              {error && (
                <div style={{ padding: '24px', color: 'var(--status-blocked)' }}>
                  ⚠️ {error}
                </div>
              )}
              {!loading && !error && (
                filtered.length > 0 ? (
                  <div className="table-wrapper">
                    <table>
                      <thead>
                        <tr>
                          <th>ID</th>
                          <th>Full Name</th>
                          <th>Email</th>
                          <th>Role</th>
                        </tr>
                      </thead>
                      <tbody>
                        {filtered.map((u) => (
                          <tr key={u.id}>
                            <td>
                              <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>
                                #{u.id}
                              </span>
                            </td>
                            <td>
                              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                <div
                                  style={{
                                    width: 32,
                                    height: 32,
                                    borderRadius: '50%',
                                    background: 'var(--accent-gradient)',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    fontSize: '0.75rem',
                                    fontWeight: 700,
                                    color: 'white',
                                    flexShrink: 0,
                                  }}
                                >
                                  {(u.fullName || u.email || 'U').slice(0, 2).toUpperCase()}
                                </div>
                                <span style={{ fontWeight: 600 }}>{u.fullName || '—'}</span>
                              </div>
                            </td>
                            <td>
                              <span style={{ color: 'var(--text-secondary)' }}>{u.email}</span>
                            </td>
                            <td>
                              <RoleBadge role={u.role} />
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="table-empty">
                    <span className="table-empty-icon">👥</span>
                    <div>No users found</div>
                  </div>
                )
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
