import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import AnalyzePage from './pages/AnalyzePage';

export default function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Analyze</Link>
      </nav>
      <Routes>
        <Route path="/" element={<AnalyzePage/>} />
      </Routes>
    </BrowserRouter>
  );
}
