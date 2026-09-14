import { useState, useEffect, useCallback } from 'react';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import TransactionTable from '../components/TransactionTable';
import LoadingSpinner from '../components/LoadingSpinner';
import client from '../api/client';

export default function AdminTransactions() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({
    status: '',
    merchant: '',
    location: '',
  });
  const [searching, setSearching] = useState(false);

  const fetchAll = useCallback(() => {
    setLoading(true);
    client.get('/admin/transactions')
      .then((res) => setTransactions(res.data))
      .catch(() => setError('Failed to load transactions.'))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { fetchAll(); }, [fetchAll]);

  const applyFilters = async () => {
    setSearching(true);
    setError('');
    try {
      let res;
      if (filters.status) {
        res = await client.get(`/admin/transactions/status?status=${filters.status}`);
      } else if (filters.merchant) {
        res = await client.get(`/admin/transactions/search?merchant=${encodeURIComponent(filters.merchant)}`);
      } else if (filters.location) {
        res = await client.get(`/admin/transactions/location?location=${encodeURIComponent(filters.location)}`);
      } else {
        fetchAll();
        return;
      }
      setTransactions(res.data);
    } catch {
      setError('Failed to apply filters.');
    } finally {
      setSearching(false);
    }
  };

  const clearFilters = () => {
    setFilters({ status: '', merchant: '', location: '' });
    fetchAll();
  };

  const handleStatusUpdate = async (txId, newStatus) => {
    try {
      const res = await client.put(`/admin/transactions/${txId}/status`, { status: newStatus });
      setTransactions((prev) =>
        prev.map((tx) => (tx.id === txId ? res.data : tx))
      );
    } catch {
      alert('Failed to update status.');
    }
  };

  const hasFilters = filters.status || filters.merchant || filters.location;

  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="All Transactions" />
        <div className="page-content">
          <h1 className="page-title">All Transactions</h1>
          <p className="page-subtitle">
            Search, filter, and manage all {transactions.length} transactions in the system
          </p>

          {/* Filter bar */}
          <div className="filters-bar">
            <select
              id="filter-status"
              className="filter-select"
              value={filters.status}
              onChange={(e) => setFilters((f) => ({ ...f, status: e.target.value, merchant: '', location: '' }))}
            >
              <option value="">All Statuses</option>
              <option value="APPROVED">Approved</option>
              <option value="REVIEW">Review</option>
              <option value="BLOCKED">Blocked</option>
            </select>

            <input
              id="filter-merchant"
              className="filter-input"
              placeholder="🔍 Search by merchant..."
              value={filters.merchant}
              onChange={(e) => setFilters((f) => ({ ...f, merchant: e.target.value, status: '', location: '' }))}
            />

            <input
              id="filter-location"
              className="filter-input"
              placeholder="📍 Filter by location..."
              value={filters.location}
              onChange={(e) => setFilters((f) => ({ ...f, location: e.target.value, status: '', merchant: '' }))}
            />

            <button
              id="filter-apply"
              className="btn btn-primary btn-sm"
              onClick={applyFilters}
              disabled={searching}
            >
              {searching ? '⏳' : '🔍'} Apply
            </button>

            {hasFilters && (
              <button
                id="filter-clear"
                className="btn btn-secondary btn-sm"
                onClick={clearFilters}
              >
                ✕ Clear
              </button>
            )}
          </div>

          <div className="card">
            <div className="card-body" style={{ padding: 0 }}>
              {loading && <LoadingSpinner text="Loading transactions..." />}
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
