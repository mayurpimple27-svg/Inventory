import React, { useState, useEffect } from 'react';

const Dashboard = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    category: '',
    price: '',
    stock: '',
    reorderLevel: ''
  });

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      setError(null);

      const response = await fetch('http://localhost:8080/api/products');

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setProducts(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    setFormData({
      ...formData,
      [name]: value
    });
  };

  // ADD PRODUCT
  const handleAddProduct = async (e) => {
    e.preventDefault();

    try {
      setError(null);

      const product = {
        name: formData.name,
        category: formData.category,
        price: Number(formData.price),
        stock: Number(formData.stock),
        reorderLevel: Number(formData.reorderLevel)
      };

      const response = await fetch(
        'http://localhost:8080/api/products',
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(product)
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const newProduct = await response.json();

      setProducts([...products, newProduct]);

      resetForm();

    } catch (err) {
      setError(err.message);
    }
  };

  // START EDIT
  const handleEdit = (product) => {
    setEditingId(product.id);

    setFormData({
      name: product.name,
      category: product.category,
      price: product.price,
      stock: product.stock,
      reorderLevel: product.reorderLevel
    });

    setShowForm(true);
  };

  // UPDATE PRODUCT
  const handleUpdateProduct = async (e) => {
    e.preventDefault();

    try {
      setError(null);

      const updatedProduct = {
        name: formData.name,
        category: formData.category,
        price: Number(formData.price),
        stock: Number(formData.stock),
        reorderLevel: Number(formData.reorderLevel)
      };

      const response = await fetch(
        `http://localhost:8080/api/products/${editingId}`,
        {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(updatedProduct)
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const updated = await response.json();

      setProducts(
        products.map(product =>
          product.id === editingId ? updated : product
        )
      );

      resetForm();

    } catch (err) {
      setError(err.message);
    }
  };

  // DELETE
  const handleDelete = async (id) => {
    try {
      setError(null);

      const response = await fetch(
        `http://localhost:8080/api/products/${id}`,
        {
          method: 'DELETE'
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      setProducts(
        products.filter(product => product.id !== id)
      );

    } catch (err) {
      setError(err.message);
    }
  };

  // RESET FORM
  const resetForm = () => {
    setFormData({
      name: '',
      category: '',
      price: '',
      stock: '',
      reorderLevel: ''
    });

    setEditingId(null);
    setShowForm(false);
  };

  if (loading) {
    return <div className="loading">Loading products...</div>;
  }

  return (
    <div className="dashboard">

      <div className="header">

        <h1>Product Dashboard</h1>

        <div>

          <button
            onClick={() => {
              if (showForm) {
                resetForm();
              } else {
                setShowForm(true);
              }
            }}
            className="add-btn"
          >
            {showForm ? 'Cancel' : '+ Add Product'}
          </button>

          <button
            onClick={fetchProducts}
            className="refresh-btn"
          >
            Refresh
          </button>

        </div>

      </div>

      {error && (
        <div className="error">
          Error: {error}
        </div>
      )}

      {showForm && (
        <form
          className="product-form"
          onSubmit={
            editingId
              ? handleUpdateProduct
              : handleAddProduct
          }
        >

          <h3>
            {editingId ? 'Edit Product' : 'Add Product'}
          </h3>

          <input
            type="text"
            name="name"
            placeholder="Product name"
            value={formData.name}
            onChange={handleInputChange}
            required
          />

          <input
            type="text"
            name="category"
            placeholder="Category"
            value={formData.category}
            onChange={handleInputChange}
            required
          />

          <input
            type="number"
            name="price"
            placeholder="Price"
            min="0"
            step="0.01"
            value={formData.price}
            onChange={handleInputChange}
            required
          />

          <input
            type="number"
            name="stock"
            placeholder="Stock"
            min="0"
            value={formData.stock}
            onChange={handleInputChange}
            required
          />

          <input
            type="number"
            name="reorderLevel"
            placeholder="Reorder level"
            min="0"
            value={formData.reorderLevel}
            onChange={handleInputChange}
            required
          />

          <button
            type="submit"
            className="save-btn"
          >
            {editingId ? 'Update Product' : 'Add Product'}
          </button>

        </form>
      )}

      {products.length === 0 ? (

        <div className="empty-state">
          <p>No products found.</p>
        </div>

      ) : (

        <table className="products-table">

          <thead>
            <tr>
              <th>Name</th>
              <th>Category</th>
              <th>Price</th>
              <th>Stock</th>
              <th>Reorder Level</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>

          <tbody>

            {products.map((product) => (

              <tr
                key={product.id}
                className={
                  product.stock < product.reorderLevel
                    ? 'low-stock'
                    : ''
                }
              >

                <td>{product.name}</td>
                <td>{product.category}</td>
                <td>${Number(product.price).toFixed(2)}</td>
                <td>{product.stock}</td>
                <td>{product.reorderLevel}</td>

                <td>
                  {product.stock < product.reorderLevel ? (
                    <span className="status-low">
                      Low Stock
                    </span>
                  ) : (
                    <span className="status-normal">
                      Normal
                    </span>
                  )}
                </td>

                <td>

                  <button
                    onClick={() => handleEdit(product)}
                    className="edit-btn"
                  >
                    Edit
                  </button>

                  <button
                    onClick={() => handleDelete(product.id)}
                    className="delete-btn"
                  >
                    Delete
                  </button>

                </td>

              </tr>

            ))}

          </tbody>

        </table>

      )}

    </div>
  );
};

export default Dashboard;

