import React, { useEffect, useState } from "react";
import axios from "axios";
import { API } from "../api";

export default function SettleUp() {
  const [groups, setGroups] = useState([]);
  const [users, setUsers] = useState([]);
  const [selectedGroup, setSelectedGroup] = useState("");
  const [payer, setPayer] = useState("");
  const [receiver, setReceiver] = useState("");
  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");

  // fetch groups
  useEffect(() => {
    API.get("/groups")
      .then((res) => setGroups(res.data))
      .catch((err) => console.error(err));
  }, []);

  // fetch users when group changes
  useEffect(() => {
    if (selectedGroup) {
      API.get(`/groups/${selectedGroup}/members`)
        .then((res) => setUsers(res.data))
        .catch((err) => console.error(err));
    }
  }, [selectedGroup]);

  const handleSettleUp = async () => {
    if (!selectedGroup || !payer || !receiver || !amount) {
      setMessage("⚠️ Please select group, payer, receiver, and amount.");
      return;
    }
    if (payer === receiver) {
      setMessage("⚠️ Payer and Receiver cannot be the same.");
      return;
    }

    try {
      await API.post("/settle", {
        groupId: selectedGroup,
        payerId: payer,
        receiverId: receiver,
        amount: parseFloat(amount),
      });
      setMessage("✅ Settlement recorded successfully!");
      setAmount("");
      setPayer("");
      setReceiver("");
    } catch (err) {
      setMessage("❌ Error settling up. Try again.");
      console.error(err);
    }
  };
  return (
    <div className="p-4 border rounded shadow-md bg-white">
      <h2 className="text-xl font-bold mb-3">Settle Up (Person-to-Person)</h2>

      {/* Select Group */}
      <label className="block mb-1">Select Group:</label>
      <select
        value={selectedGroup}
        onChange={(e) => setSelectedGroup(e.target.value)}
        className="border p-2 mb-3 w-full"
      >
        <option value="">-- Choose Group --</option>
        {groups.map((g) => (
          <option key={g.id} value={g.id}>
            {g.name}
          </option>
        ))}
      </select>

      {/* Select Payer */}
      <label className="block mb-1">Payer:</label>
      <select
        value={payer}
        onChange={(e) => setPayer(e.target.value)}
        className="border p-2 mb-3 w-full"
        disabled={!users.length}
      >
        <option value="">-- Select Payer --</option>
        {users.map((u) => (
          <option key={u.id} value={u.id}>
            {u.name}
          </option>
        ))}
      </select>

      {/* Select Receiver */}
      <label className="block mb-1">Receiver:</label>
      <select
        value={receiver}
        onChange={(e) => setReceiver(e.target.value)}
        className="border p-2 mb-3 w-full"
        disabled={!users.length}
      >
        <option value="">-- Select Receiver --</option>
        {users.map((u) => (
          <option key={u.id} value={u.id}>
            {u.name}
          </option>
        ))}
      </select>

      {/* Enter Amount */}
      <label className="block mb-1">Amount:</label>
      <input
        type="number"
        value={amount}
        onChange={(e) => setAmount(e.target.value)}
        className="border p-2 mb-3 w-full"
        placeholder="Enter amount"
      />

      <button
        onClick={handleSettleUp}
        className="bg-green-500 text-white px-4 py-2 rounded"
      >
        Settle Up
      </button>

      {message && <p className="mt-3 text-sm">{message}</p>}
    </div>
  );
}
