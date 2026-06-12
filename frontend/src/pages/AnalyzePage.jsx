import React, { useState } from 'react';
import api from '../api/axiosConfig';
import GapReport from '../components/GapReport';

export default function AnalyzePage() {
  const [studentId, setStudentId] = useState('');
  const [companyId, setCompanyId] = useState('');
  const [report, setReport]       = useState(null);
  const [error, setError] = useState(null);

  const handleAnalyze = async () => {
    try {
      setError(null);
      const res = await api.get('/gap/analyze', {
        params: { studentId, companyId }
      });
      setReport(res.data);
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <div className="analyze-page">
      <h2>Skill Gap Analyzer</h2>
      <input
        placeholder="Student ID"
        value={studentId}
        onChange={e => setStudentId(e.target.value)}
      />
      <input
        placeholder="Company ID"
        value={companyId}
        onChange={e => setCompanyId(e.target.value)}
      />
      <button onClick={handleAnalyze}>Analyze</button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      {report && <GapReport data={report} />}
    </div>
  );
}
