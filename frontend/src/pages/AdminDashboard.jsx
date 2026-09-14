import { useState, useEffect } from 'react';
import { Doughnut, Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
} from 'chart.js';
import Navbar from '../components/Navbar';
import Sidebar from '../components/Sidebar';
import StatCard from '../components/StatCard';
import LoadingSpinner from '../components/LoadingSpinner';
import client from '../api/client';

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement);

function formatCurrency(val) {
  const num = parseFloat(val || 0);
  if (num >= 1_00_000) return `₹${(num / 1_00_000).toFixed(1)}L`;
  if (num >= 1000) return `₹${(num / 1000).toFixed(1)}K`;
  return `₹${num.toFixed(0)}`;
}

const CHART_OPTIONS = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      labels: {
        color: '#8892b0',
        font: { family: 'Inter', size: 12 },
        padding: 16,
      },
    },
    tooltip: {
      backgroundColor: '#0f1628',
      borderColor: 'rgba(255,255,255,0.08)',
      borderWidth: 1,
      titleColor: '#e8eaf6',
      bodyColor: '#8892b0',
    },
  },
};

export default function AdminDashboard() {
  const [analytics, setAnalytics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    client.get('/admin/analytics')
      .then((res) => setAnalytics(res.data))
      .catch(() => setError('Failed to load analytics.'))
      .finally(() => setLoading(false));
  }, []);

  const donutData = analytics
    ? {
        labels: ['Approved', 'Under Review', 'Blocked'],
        datasets: [
          {
            data: [
              analytics.approvedTransactions,
              analytics.reviewTransactions,
              analytics.blockedTransactions,
            ],
            backgroundColor: [
              'rgba(0, 224, 150, 0.8)',
              'rgba(255, 184, 0, 0.8)',
              'rgba(255, 75, 110, 0.8)',
            ],
            borderColor: [
              'rgba(0, 224, 150, 1)',
              'rgba(255, 184, 0, 1)',
              'rgba(255, 75, 110, 1)',
            ],
            borderWidth: 2,
            hoverOffset: 6,
          },
        ],
      }
    : null;

  const amountBarData = analytics
    ? {
        labels: ['Approved', 'Review', 'Blocked'],
        datasets: [
          {
            label: 'Transaction Amount (₹)',
            data: [
              parseFloat(analytics.approvedTransactionAmount || 0),
              parseFloat(analytics.reviewTransactionAmount || 0),
              parseFloat(analytics.blockedTransactionAmount || 0),
            ],
            backgroundColor: [
              'rgba(0, 224, 150, 0.6)',
              'rgba(255, 184, 0, 0.6)',
              'rgba(255, 75, 110, 0.6)',
            ],
            borderColor: [
              'rgba(0, 224, 150, 1)',
              'rgba(255, 184, 0, 1)',
              'rgba(255, 75, 110, 1)',
            ],
            borderWidth: 2,
            borderRadius: 6,
          },
        ],
      }
    : null;

  const barOptions = {
    ...CHART_OPTIONS,
    scales: {
      x: {
        ticks: { color: '#8892b0', font: { family: 'Inter', size: 12 } },
        grid: { color: 'rgba(255,255,255,0.04)' },
      },
      y: {
        ticks: {
          color: '#8892b0',
          font: { family: 'Inter', size: 12 },
          callback: (v) => formatCurrency(v),
        },
        grid: { color: 'rgba(255,255,255,0.04)' },
      },
    },
  };

  const fraudRate = analytics
    ? (analytics.fraudRate * 100).toFixed(1)
    : 0;

  return (
    <div className="app-layout">
      <Sidebar />
      <div className="main-content">
        <Navbar title="Admin Analytics" />
        <div className="page-content">
          <h1 className="page-title">Fraud Analytics</h1>
          <p className="page-subtitle">Real-time overview of fraud detection performance</p>

          {loading && <LoadingSpinner text="Loading analytics..." />}
          {error && (
            <div className="auth-error">⚠️ {error}</div>
          )}

          {analytics && (
            <>
              {/* KPI Cards */}
              <div className="stat-grid">
                <StatCard
                  icon="📊"
                  label="Total Transactions"
                  value={analytics.totalTransactions.toLocaleString()}
                  sub={formatCurrency(analytics.totalTransactionAmount) + ' total volume'}
                  variant="gradient"
                />
                <StatCard
                  icon="✅"
                  label="Approved"
                  value={analytics.approvedTransactions.toLocaleString()}
                  sub={formatCurrency(analytics.approvedTransactionAmount)}
                  variant="approved"
                />
                <StatCard
                  icon="⚠️"
                  label="Under Review"
                  value={analytics.reviewTransactions.toLocaleString()}
                  sub={formatCurrency(analytics.reviewTransactionAmount)}
                  variant="review"
                />
                <StatCard
                  icon="🚫"
                  label="Blocked"
                  value={analytics.blockedTransactions.toLocaleString()}
                  sub={formatCurrency(analytics.blockedTransactionAmount)}
                  variant="blocked"
                />
              </div>

              {/* Fraud Rate Banner */}
              <div
                style={{
                  background: fraudRate > 20
                    ? 'rgba(255,75,110,0.08)'
                    : 'rgba(0,210,255,0.06)',
                  border: `1px solid ${fraudRate > 20 ? 'rgba(255,75,110,0.2)' : 'rgba(0,210,255,0.15)'}`,
                  borderRadius: 'var(--radius-lg)',
                  padding: '20px 28px',
                  marginBottom: '28px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                }}
              >
                <div>
                  <div style={{ fontSize: '0.75rem', fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--text-secondary)', marginBottom: '4px' }}>
                    Overall Fraud Rate
                  </div>
                  <div style={{
                    fontSize: '2.4rem',
                    fontWeight: 800,
                    color: fraudRate > 20 ? 'var(--status-blocked)' : 'var(--accent-primary)',
                    lineHeight: 1,
                  }}>
                    {fraudRate}%
                  </div>
                  <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '4px' }}>
                    {fraudRate > 20 ? '⚠️ High fraud activity detected' : '✅ Within normal range'}
                  </div>
                </div>
                <div style={{ fontSize: '4rem', opacity: 0.3 }}>
                  {fraudRate > 20 ? '🚨' : '🛡️'}
                </div>
              </div>

              {/* Charts */}
              <div className="chart-grid">
                <div className="chart-card">
                  <div className="chart-card-title">Transaction Status Distribution</div>
                  <div className="chart-container">
                    {donutData && (
                      <Doughnut
                        data={donutData}
                        options={{
                          ...CHART_OPTIONS,
                          cutout: '65%',
                        }}
                      />
                    )}
                  </div>
                </div>

                <div className="chart-card">
                  <div className="chart-card-title">Transaction Volume by Status</div>
                  <div className="chart-container">
                    {amountBarData && (
                      <Bar data={amountBarData} options={barOptions} />
                    )}
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
