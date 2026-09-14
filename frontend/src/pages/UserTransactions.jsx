import { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import TransactionTable from '../components/TransactionTable';
import LoadingSpinner from '../components/LoadingSpinner';
import client from '../api/client';

export default function UserTransactions() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    client.get('/transactions/my')
      .then((res) => setTransactions(res.data))
      .catch(() => setError('Failed to load your transactions.'))
      .finally(() => setLoading(false));
  }, []);

  // Summary stats
  const total = transactions.length;
  const approved = transactions.filter(t => t.status === 'APPROVED').length;
  const review = transactions.filter(t => t.status === 'REVIEW').length;
  const blocked = transactions.filter(t => t.status === 'BLOCKED').length;

  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="My Transactions" />
        <div className="page-content">
          <h1 className="page-title">My Transactions</h1>
          <p className="page-subtitle">View your complete transaction history and fraud detection results</p>

          {/* Mini stat row */}
          {!loading && !error && (
            <div className="stat-grid" style={{ marginBottom: '24px' }}>
              <div className="stat-card gradient">
                <span className="stat-card-icon">📊</span>
                <div className="stat-card-label">Total</div>
                <div className="stat-card-value">{total}</div>
              </div>
              <div className="stat-card approved">
                <span className="stat-card-icon">✅</span>
                <div className="stat-card-label">Approved</div>
                <div className="stat-card-value">{approved}</div>
              </div>
              <div className="stat-card review">
                <span className="stat-card-icon">⚠️</span>
                <div className="stat-card-label">Under Review</div>
                <div className="stat-card-value">{review}</div>
              </div>
              <div className="stat-card blocked">
                <span className="stat-card-icon">🚫</span>
                <div className="stat-card-label">Blocked</div>
                <div className="stat-card-value">{blocked}</div>
              </div>
            </div>
          )}

          <div className="card">
            <div className="card-header">
              <h2 className="card-title">Transaction History</h2>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                {total} total
              </span>
            </div>
            <div className="card-body" style={{ padding: '0' }}>
              {loading && <LoadingSpinner text="Loading your transactions..." />}
              {error && (
                <div style={{ padding: '24px', color: 'var(--status-blocked)' }}>
                  ⚠️ {error}
                </div>
              )}
              {!loading && !error && (
                <TransactionTable transactions={transactions} showActions={false} />
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
