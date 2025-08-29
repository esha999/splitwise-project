// src/components/AddExpense.js
import { useEffect, useState } from "react";
import { API } from "../api";

export default function AddExpense() {
  const [groups, setGroups] = useState([]);
  const [groupId, setGroupId] = useState("");
  const [members, setMembers] = useState([]);

  const [description, setDescription] = useState("");
  const [amount, setAmount] = useState("");
  const [payerId, setPayerId] = useState("");
  const [splitType, setSplitType] = useState("EQUAL"); // EQUAL | EXACT | PERCENT
  const [splits, setSplits] = useState({}); // { [userId]: number }

  const [err, setErr] = useState("");

  // Load groups initially
  useEffect(() => {
    API.get("/groups")
      .then((res) => setGroups(res.data))
      .catch(() => setGroups([]));
  }, []);

  // When a group is selected, load its members and reset payer / splits
  const handleSelectGroup = async (id) => {
    setGroupId(id);
    setErr("");
    setPayerId("");
    setSplits({});
    setMembers([]);

    if (!id) return;

    try {
      // IMPORTANT: this endpoint must exist in your GroupController
      // GET /api/groups/{groupId}/members -> List<User>
      const res = await API.get(`/groups/${id}/members`);
      setMembers(res.data || []);
    } catch (e) {
      setErr("Failed to load group members");
    }
  };

  const onChangeSplitFor = (userId, value) => {
    const v = value === "" ? "" : Number(value);
    setSplits((prev) => ({ ...prev, [userId]: v }));
  };

  // Basic client-side validation for EXACT and PERCENT
  const validateSplits = () => {
    if (splitType === "EQUAL") return true;
    if (members.length === 0) return false;

    const values = members.map((m) => Number(splits[m.id] ?? 0));

    if (splitType === "EXACT") {
      const total = values.reduce((a, b) => a + b, 0);
      const amt = Number(amount || 0);
      // allow small rounding diff
      if (Math.abs(total - amt) > 0.01) {
        setErr(
          `Exact shares (${total.toFixed(2)}) must sum to amount (${amt.toFixed(
            2
          )}).`
        );
        return false;
      }
    }

    if (splitType === "PERCENT") {
      const totalPct = values.reduce((a, b) => a + b, 0);
      if (Math.abs(totalPct - 100) > 0.01) {
        setErr(
          `Percent shares must total 100 (currently ${totalPct.toFixed(2)}).`
        );
        return false;
      }
    }
    return true;
  };

  const handleSubmit = async () => {
    setErr("");

    if (!groupId) return setErr("Please select a group.");
    if (!payerId) return setErr("Please select the payer.");
    if (!amount || Number(amount) <= 0)
      return setErr("Please enter a valid amount.");

    if (!validateSplits()) return;

    // Build request for backend DTO: CreateExpenseRequest
    // {
    //   groupId, payerId, amount, description, splitType, splits(Map<Long,Double>)
    // }
    const payload = {
      groupId: Number(groupId),
      payerId: Number(payerId),
      amount: Number(amount),
      description,
      splitType, // "EQUAL" | "EXACT" | "PERCENT"
      // For EQUAL the service ignores splits; for EXACT/PERCENT we send the map
      splits:
        splitType === "EQUAL"
          ? {}
          : Object.fromEntries(
              members.map((m) => [m.id, Number(splits[m.id] ?? 0)])
            ),
    };

    try {
      await API.post("/expenses", payload);
      alert("Expense added!");
      // reset
      setDescription("");
      setAmount("");
      setSplitType("EQUAL");
      setSplits({});
      setPayerId("");
    } catch (e) {
      setErr("Failed to add expense. Check console/network logs.");
      // console.error(e);
    }
  };

  return (
    <div className="p-4 space-y-3">
      <h2 className="text-xl font-semibold">Add Expense</h2>

      {err && <div className="text-red-600">{err}</div>}

      {/* 1) Select Group */}
      <div className="space-x-2">
        <label className="font-medium">Group:</label>
        <select
          value={groupId}
          onChange={(e) => handleSelectGroup(e.target.value)}
          className="border p-2"
        >
          <option value="">Select Group</option>
          {groups.map((g) => (
            <option key={g.id} value={g.id}>
              {g.name}
            </option>
          ))}
        </select>
      </div>

      {/* 2) Select Payer (members of the selected group) */}
      <div className="space-x-2">
        <label className="font-medium">Payer:</label>
        <select
          value={payerId}
          onChange={(e) => setPayerId(e.target.value)}
          className="border p-2"
          disabled={!groupId || members.length === 0}
        >
          <option value="">Select Payer</option>
          {members.map((m) => (
            <option key={m.id} value={m.id}>
              {m.name}
            </option>
          ))}
        </select>
      </div>

      {/* 3) Description + Amount */}
      <div className="space-x-2">
        <input
          type="text"
          placeholder="Description"
          className="border p-2"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
        <input
          type="number"
          placeholder="Amount"
          className="border p-2"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          min="0"
          step="0.01"
        />
      </div>

      {/* 4) Split Type */}
      <div className="space-x-2">
        <label className="font-medium">Split:</label>
        <select
          value={splitType}
          onChange={(e) => setSplitType(e.target.value)}
          className="border p-2"
          disabled={!groupId || members.length === 0}
        >
          <option value="EQUAL">Equal</option>
          <option value="EXACT">Exact</option>
          <option value="PERCENT">Percentage</option>
        </select>
      </div>

      {/* 5) Per-member inputs for EXACT / PERCENT */}
      {splitType !== "EQUAL" && members.length > 0 && (
        <div className="mt-2 space-y-2">
          <div className="text-sm text-gray-700">
            {splitType === "EXACT"
              ? "Enter exact amount per member (must total the expense amount)."
              : "Enter percentage per member (must total 100%)."}
          </div>
          {members.map((m) => (
            <div key={m.id} className="flex items-center space-x-2">
              <div className="w-40">{m.name}</div>
              <input
                type="number"
                className="border p-2 w-40"
                placeholder={splitType === "EXACT" ? "Amount" : "%"}
                value={splits[m.id] ?? ""}
                onChange={(e) => onChangeSplitFor(m.id, e.target.value)}
                min="0"
                step="0.01"
              />
              <span>{splitType === "EXACT" ? "" : "%"}</span>
            </div>
          ))}
        </div>
      )}

      {/* 6) Submit */}
      <button
        className="bg-green-600 text-white px-4 py-2 rounded"
        onClick={handleSubmit}
        disabled={!groupId || !payerId || !amount}
      >
        Add Expense
      </button>
    </div>
  );
}
