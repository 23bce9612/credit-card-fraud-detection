export default function StatusBadge({ status }) {
  const normalized = (status || '').toUpperCase();

  const classMap = {
    APPROVED: 'approved',
    REVIEW: 'review',
    BLOCKED: 'blocked',
  };

  const labelMap = {
    APPROVED: 'Approved',
    REVIEW: 'Review',
    BLOCKED: 'Blocked',
  };

  const cls = classMap[normalized] || 'review';
  const label = labelMap[normalized] || status;

  return <span className={`status-badge ${cls}`}>{label}</span>;
}
