import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import client from '../api/client';

const INITIAL_FORM = {
  amount: '',
  merchant: '',
  location: '',
  cardType: 'VISA',
  deviceType: 'MOBILE',
  deviceId: '',
  ipAddress: '',
};

function ProbabilityBar({ probability }) {
  const pct = Math.round((probability || 0) * 100);
  const color =
    pct < 30 ? 'var(--status-approved)'
    : pct < 70 ? 'var(--status-review)'
    : 'var(--status-blocked)';

  return (
    <div className="prob-bar-wrap">
      <div className="prob-bar-label">
        <span>Fraud Probability</span>
        <strong style={{ color }}>{pct}%</strong>
      </div>
      <div className="prob-bar-track">
        <div
          className="prob-bar-fill"
          style={{ width: `${pct}%`, background: color }}
        />
      </div>
    </div>
  );
}

function ResultCard({ result }) {
  if (!result) return null;
  const statusClass = result.status?.toLowerCase();
  const icons = { approved: '✅', review: '⚠️', blocked: '🚫' };

  return (
    <div className={`result-card ${statusClass}`}>
      <div className="result-card-title">
        {icons[statusClass]} Transaction {result.status}
      </div>
      <div className="result-card-body">
        Transaction #{result.id} has been processed and flagged as{' '}
        <strong>{result.status}</strong>.
      </div>
      <ProbabilityBar probability={result.fraudProbability} />
    </div>
  );
}

export default function UserDashboard() {
  const { user } = useAuth();
  const [form, setForm] = useState(INITIAL_FORM);
  const [result, setResult] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) =>
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setResult(null);
    setLoading(true);
    try {
      const res = await client.post('/transactions', {
        ...form,
        amount: parseFloat(form.amount),
      });
      setResult(res.data);
      setForm(INITIAL_FORM);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        JSON.stringify(err.response?.data) ||
        'Failed to submit transaction.'
      );
    } finally {
      setLoading(false);
    }
  };

  const firstName = user?.email?.split('@')[0] || 'User';

  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="Dashboard" />
        <div className="page-content">
          {/* Welcome banner */}
          <div
            style={{
              background: 'linear-gradient(135deg, rgba(0,210,255,0.12) 0%, rgba(123,97,255,0.12) 100%)',
              border: '1px solid rgba(0,210,255,0.15)',
              borderRadius: 'var(--radius-lg)',
              padding: '28px 32px',
              marginBottom: '32px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
            }}
          >
            <div>
              <h1 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '6px' }}>
                Hello, {firstName}! 👋
              </h1>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                Submit a transaction below for real-time fraud analysis.
              </p>
            </div>
            <div style={{ fontSize: '3rem', opacity: 0.6 }}>💳</div>
          </div>

          {/* Transaction form */}
          <div className="card">
            <div className="card-header">
              <h2 className="card-title">🔍 Submit New Transaction</h2>
            </div>
            <div className="card-body">
              {error && (
                <div className="auth-error" style={{ marginBottom: '16px' }}>
                  <span>⚠️</span> {error}
                </div>
              )}
              <form onSubmit={handleSubmit}>
                <div className="form-grid">
                  <div className="form-group">
                    <label className="form-label" htmlFor="tx-amount">Amount (₹)</label>
                    <input
                      id="tx-amount"
                      name="amount"
                      type="number"
                      step="0.01"
                      min="0.01"
                      className="form-input"
                      placeholder="e.g. 2500.00"
                      value={form.amount}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label" htmlFor="tx-merchant">Merchant</label>
                    <input
                      id="tx-merchant"
                      name="merchant"
                      type="text"
                      className="form-input"
                      placeholder="e.g. Amazon"
                      value={form.merchant}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label" htmlFor="tx-location">Location</label>
                    <input
                      id="tx-location"
                      name="location"
                      type="text"
                      className="form-input"
                      placeholder="e.g. Hyderabad"
                      value={form.location}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label" htmlFor="tx-cardtype">Card Type</label>
                    <select
                      id="tx-cardtype"
                      name="cardType"
                      className="form-select"
                      value={form.cardType}
                      onChange={handleChange}
                    >
                      <option value="VISA">VISA</option>
                      <option value="MASTERCARD">MasterCard</option>
                      <option value="RUPAY">RuPay</option>
                      <option value="AMEX">Amex</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="form-label" htmlFor="tx-devicetype">Device Type</label>
                    <select
                      id="tx-devicetype"
                      name="deviceType"
                      className="form-select"
                      value={form.deviceType}
                      onChange={handleChange}
                    >
                      <option value="MOBILE">Mobile</option>
                      <option value="DESKTOP">Desktop</option>
                      <option value="TABLET">Tablet</option>
                      <option value="POS">POS Terminal</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="form-label" htmlFor="tx-deviceid">Device ID</label>
                    <input
                      id="tx-deviceid"
                      name="deviceId"
                      type="text"
                      className="form-input"
                      placeholder="e.g. device-abc-123"
                      value={form.deviceId}
                      onChange={handleChange}
                      required
                    />
                  </div>

                  <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                    <label className="form-label" htmlFor="tx-ip">IP Address</label>
                    <input
                      id="tx-ip"
                      name="ipAddress"
                      type="text"
                      className="form-input"
                      placeholder="e.g. 192.168.1.1"
                      value={form.ipAddress}
                      onChange={handleChange}
                      required
                    />
                  </div>
                </div>

                <button
                  id="tx-submit"
                  type="submit"
                  className="btn btn-primary btn-lg"
                  disabled={loading}
                >
                  {loading ? '⏳ Analyzing...' : '🔍 Analyze Transaction'}
                </button>
              </form>

              <ResultCard result={result} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
