import StatusBadge from './StatusBadge';

function formatDate(dateStr) {
  if (!dateStr) return '—';
  return new Date(dateStr).toLocaleString('en-IN', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function formatAmount(amount) {
  return `₹${parseFloat(amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
}

export default function TransactionTable({ transactions, onStatusUpdate, showActions }) {
  if (!transactions || transactions.length === 0) {
    return (
      <div className="table-empty">
        <span className="table-empty-icon">📭</span>
        <div>No transactions found</div>
      </div>
    );
  }

  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Amount</th>
            <th>Merchant</th>
            <th>Location</th>
            <th>Card Type</th>
            <th>Device</th>
            <th>Time</th>
            <th>Status</th>
            {showActions && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {transactions.map((tx) => (
            <tr key={tx.id}>
              <td>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>
                  #{tx.id}
                </span>
              </td>
              <td>
                <strong style={{ color: 'var(--accent-primary)' }}>
                  {formatAmount(tx.amount)}
                </strong>
              </td>
              <td>{tx.merchant || '—'}</td>
              <td>
                <span style={{ fontSize: '0.82rem' }}>📍 {tx.location || '—'}</span>
              </td>
              <td>{tx.cardType || '—'}</td>
              <td>
                <span style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>
                  {tx.deviceType || '—'}
                </span>
              </td>
              <td>
                <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                  {formatDate(tx.transactionTime)}
                </span>
              </td>
              <td>
                <StatusBadge status={tx.status} />
              </td>
              {showActions && (
                <td>
                  <div style={{ display: 'flex', gap: '6px' }}>
                    {tx.status !== 'APPROVED' && (
                      <button
                        className="btn btn-success btn-sm"
                        onClick={() => onStatusUpdate(tx.id, 'APPROVED')}
                      >
                        ✓ Approve
                      </button>
                    )}
                    {tx.status !== 'BLOCKED' && (
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => onStatusUpdate(tx.id, 'BLOCKED')}
                      >
                        ✕ Block
                      </button>
                    )}
                  </div>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
