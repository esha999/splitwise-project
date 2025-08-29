import React, { useState, useEffect } from "react";
import axios from "axios";

export default function UserList() {
  const [users, setUsers] = useState([]);
  const [name, setName] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await axios.get("http://localhost:8081/api/users"); // backend should return list
      setUsers(res.data);
    } catch (err) {
      setError("Failed to fetch users");
    }
  };

  const handleAddUser = async (e) => {
    e.preventDefault();
    if (!name.trim()) {
      setError("Name is required");
      return;
    }

    try {
      await axios.post("http://localhost:8081/api/users", { name }); // only send name
      setName("");
      fetchUsers(); // refresh list
    } catch (err) {
      setError("Failed to add user");
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-3">Users</h2>

      {/* Add user form */}
      <form onSubmit={handleAddUser} className="mb-4 flex gap-2">
        <input
          type="text"
          placeholder="Enter user name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="border p-2 rounded w-64"
        />
        <button
          type="submit"
          className="bg-blue-500 text-white px-4 py-2 rounded"
        >
          Add User
        </button>
      </form>

      {error && <p className="text-red-500">{error}</p>}

      {/* User list */}
      <ul className="list-disc pl-6">
        {users.length === 0 ? (
          <li>No users available</li>
        ) : (
          users.map((u) => <li key={u.id}>{u.name}</li>)
        )}
      </ul>
    </div>
  );
}
