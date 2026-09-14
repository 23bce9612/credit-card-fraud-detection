export default function StatCard({ icon, label, value, sub, variant }) {
  return (
    <div className={`stat-card${variant ? ` ${variant}` : ''}`}>
      <span className="stat-card-icon">{icon}</span>
      <div className="stat-card-label">{label}</div>
      <div className="stat-card-value">{value}</div>
      {sub && <div className="stat-card-sub">{sub}</div>}
    </div>
  );
}
