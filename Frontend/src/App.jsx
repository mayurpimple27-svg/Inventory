import React, { useState } from 'react';
import Dashboard from './components/Dashboard';
import Suggestions from './components/Suggestions';
import './App.css';

function App() {
  const [activeTab, setActiveTab] = useState('dashboard');

  return (
    <div className="app">
      <header className="app-header">
        <h1>StockPulse</h1>
        <nav className="navigation">
          <ul>
            <li>
              <button 
                onClick={() => setActiveTab('dashboard')}
                className={activeTab === 'dashboard' ? 'active' : ''}
              >
                Dashboard
              </button>
            </li>
            <li>
              <button 
                onClick={() => setActiveTab('suggestions')}
                className={activeTab === 'suggestions' ? 'active' : ''}
              >
                Suggestions
              </button>
            </li>
          </ul>
        </nav>
      </header>
      
      <main className="app-main">
        {activeTab === 'dashboard' && <Dashboard />}
        {activeTab === 'suggestions' && <Suggestions />}
      </main>
    </div>
  );
}

export default App;
