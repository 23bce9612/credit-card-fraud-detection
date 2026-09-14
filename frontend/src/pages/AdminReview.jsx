import { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import TransactionTable from '../components/TransactionTable';
import LoadingSpinner from '../components/LoadingSpinner';
import client from '../api/client';

export default function AdminReview() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchReview = () => {
    setLoading(true);
    client.get('/admin/transactions/review')
      .then((res) => setTransactions(res.data))
      .catch(() => setError('Failed to load review queue.'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { fetchReview(); }, []);

  const handleStatusUpdate = async (txId, newStatus) => {
    try {
      await client.put(`/admin/transactions/${txId}/status`, { status: newStatus });
      // Remove from review queue after action
      setTransactions((prev) => prev.filter((tx) => tx.id !== txId));
    } catch {
      alert('Failed to update status.');
    }
  };

  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="Review Queue" />
        <div className="page-content">
          <h1 className="page-title">Review Queue</h1>
          <p className="page-subtitle">
            Transactions flagged by the ML model for manual review
          </p>

          {!loading && !error && transactions.length > 0 && (
            <div className="review-banner">
              <span style={{ fontSize: '1.3rem' }}>⚠️</span>
              <div>
                <strong>{transactions.length} transaction{transactions.length !== 1 ? 's' : ''}</strong> require
                {transactions.length === 1 ? 's' : ''} your attention. Please review and take action.
              </div>
            </div>
          )}

          {!loading && !error && transactions.length === 0 && (
            <div
              style={{
                background: 'var(--status-approved-bg)',
                border: '1px solid rgba(0,224,150,0.2)',
                borderRadius: 'var(--radius-lg)',
                padding: '20px 24px',
                marginBottom: '24px',
                color: 'var(--status-approved)',
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
              }}
            >
              ✅ <strong>All clear!</strong> No transactions currently need review.
            </div>
          )}

          <div className="card">
            <div className="card-header">
              <h2 className="card-title">⚠️ Flagged Transactions</h2>
              <button
                className="btn btn-secondary btn-sm"
                onClick={fetchReview}
              >
                🔄 Refresh
              </button>
            </div>
            <div className="card-body" style={{ padding: 0 }}>
              {loading && <LoadingSpinner text="Loading review queue..." />}
              {error && (
                <div style={{ padding: '24px', color: 'var(--status-blocked)' }}>
                  ⚠️ {error}
                </div>
              )}
              {!loading && !error && (
                <TransactionTable
                  transactions={transactions}
                  onStatusUpdate={handleStatusUpdate}
                  showActions
                />
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
