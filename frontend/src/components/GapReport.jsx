import React from 'react';

export default function GapReport({ data }) {
  const { studentName, companyName, matchPercentage, missingSkills, status } = data;

  const color = matchPercentage >= 80 ? 'green'
              : matchPercentage >= 50 ? 'orange' : 'red';

  return (
    <div className="gap-report">
      <h3>{studentName} → {companyName}</h3>
      <p style={{ color, fontWeight: 'bold', fontSize: '1.4rem' }}>
        {matchPercentage.toFixed(1)}% Match
      </p>
      <span className={`badge badge-${status.toLowerCase().replace(' ', '-')}`}>
        {status}
      </span>

      <h4>Missing Skills</h4>
      {missingSkills && missingSkills.length === 0
        ? <p>✅ No missing skills!</p>
        : <ul>{missingSkills && missingSkills.map(s => <li key={s}>{s}</li>)}</ul>
      }
    </div>
  );
}
