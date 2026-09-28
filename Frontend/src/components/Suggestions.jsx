import React, { useState, useEffect } from 'react';

const Suggestions = () => {
  const [pricingSuggestions, setPricingSuggestions] = useState([]);
  const [reorderSuggestions, setReorderSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchSuggestions();
  }, []);

  const fetchSuggestions = async () => {
    try {
      setLoading(true);
      
      // Fetch pricing suggestions
      const pricingResponse = await fetch('http://localhost:8080/api/pricing-suggestions');
      if (!pricingResponse.ok) {
        throw new Error(`HTTP error fetching pricing suggestions! status: ${pricingResponse.status}`);
      }
      const pricingData = await pricingResponse.json();
      setPricingSuggestions(pricingData);

      // Fetch reorder suggestions
      const reorderResponse = await fetch('http://localhost:8080/api/reorder-suggestions');
      if (!reorderResponse.ok) {
        throw new Error(`HTTP error fetching reorder suggestions! status: ${reorderResponse.status}`);
      }
      const reorderData = await reorderResponse.json();
      setReorderSuggestions(reorderData);
      
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const updateSuggestionStatus = async (type, id, status) => {
    try {
      const response = await fetch(`http://localhost:8080/api/${type}-suggestions/${id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Update local state
      if (type === 'pricing') {
        setPricingSuggestions(pricingSuggestions.map(suggestion => 
          suggestion.id === id ? { ...suggestion, status } : suggestion
        ));
      } else {
        setReorderSuggestions(reorderSuggestions.map(suggestion => 
          suggestion.id === id ? { ...suggestion, status } : suggestion
        ));
      }
    } catch (err) {
      setError(err.message);
    }
  };

  const handleAccept = (type, id) => {
    updateSuggestionStatus(type, id, 'ACCEPTED');
  };

  const handleReject = (type, id) => {
    updateSuggestionStatus(type, id, 'REJECTED');
  };

  if (loading) {
    return <div className="loading">Loading suggestions...</div>;
  }

  if (error) {
    return <div className="error">Error: {error}</div>;
  }

  return (
    <div className="suggestions">
      <div className="header">
        <h1>Suggestions</h1>
        <button onClick={fetchSuggestions} className="refresh-btn">
          Refresh Suggestions
        </button>
      </div>

      <div className="suggestions-section">
        <h2>Pricing Suggestions</h2>
        {pricingSuggestions.length === 0 ? (
          <div className="empty-state">
            <p>No pricing suggestions found.</p>
          </div>
        ) : (
          <table className="suggestions-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>Recommended Price</th>
                <th>Direction</th>
                <th>Confidence</th>
                <th>Reasoning</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pricingSuggestions.map((suggestion) => (
                <tr key={suggestion.id}>
                  <td>{suggestion.productName}</td>
                  <td>${suggestion.recommendedPrice.toFixed(2)}</td>
                  <td>{suggestion.direction}</td>
                  <td>{(suggestion.confidence * 100).toFixed(0)}%</td>
                  <td>{suggestion.reasoning}</td>
                  <td>
                    <span className={`status-${suggestion.status.toLowerCase()}`}>
                      {suggestion.status}
                    </span>
                  </td>
                  <td>
                    {suggestion.status === 'PENDING' && (
                      <>
                        <button 
                          onClick={() => handleAccept('pricing', suggestion.id)}
                          className="accept-btn"
                        >
                          Accept
                        </button>
                        <button 
                          onClick={() => handleReject('pricing', suggestion.id)}
                          className="reject-btn"
                        >
                          Reject
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
      <div className="suggestions-section">
        <h2>Reorder Suggestions</h2>
        {reorderSuggestions.length === 0 ? (
          <div className="empty-state">
            <p>No reorder suggestions found.</p>
          </div>
        ) : (
          <table className="suggestions-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>Recommended Quantity</th>
                <th>Confidence</th>
                <th>Reasoning</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {reorderSuggestions.map((suggestion) => (
                <tr key={suggestion.id}>
                  <td>{suggestion.productName}</td>
                  <td>{suggestion.recommendedQuantity}</td>
                  <td>{(suggestion.confidence * 100).toFixed(0)}%</td>
                  <td>{suggestion.reasoning}</td>
                  <td>
                    <span className={`status-${suggestion.status.toLowerCase()}`}>
                      {suggestion.status}
                    </span>
                  </td>
                  <td>
                    {suggestion.status === 'PENDING' && (
                      <>
                        <button 
                          onClick={() => handleAccept('reorder', suggestion.id)}
                          className="accept-btn"
                        >
                          Accept
                        </button>
                        <button 
                          onClick={() => handleReject('reorder', suggestion.id)}
                          className="reject-btn"
                        >
                          Reject
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default Suggestions;