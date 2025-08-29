import { useEffect, useState } from "react";
import { API } from "../api";

export default function Balance() {
  const [groups, setGroups] = useState([]);
  const [groupId, setGroupId] = useState("");
  const [balances, setBalances] = useState({}); // username -> amount
  const [debts, setDebts] = useState([]); // [{from, to, amount, groupId}]
  const [err, setErr] = useState("");

  useEffect(() => {
    API.get("/groups")
      .then((res) => setGroups(res.data))
      .catch(() => setGroups([]));
  }, []);

  const loadData = async (id) => {
    setGroupId(id);
    setErr("");
    setBalances({});
    setDebts([]);

    if (!id) return;

    try {
      const [balRes, debtRes] = await Promise.all([
        API.get(`/groups/${id}/balances`),
        API.get(`/groups/${id}/debts`),
      ]);
      setBalances(balRes.data || {});
      setDebts(debtRes.data || []);
    } catch (e) {
      setErr("Failed to load balances/settlements");
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-xl mb-2">Group Balances & Suggested Settlements</h2>

      <select onChange={(e) => loadData(e.target.value)} className="border p-2">
        <option value="">Select Group</option>
        {groups.map((g) => (
          <option key={g.id} value={g.id}>
            {g.name}
          </option>
        ))}
      </select>

      {err && <div className="text-red-600 mt-2">{err}</div>}

      {/* Net balances (username -> amount) */}
      <div className="mt-4">
        <h3 className="font-semibold mb-2">Net Balances</h3>
        <ul>
          {Object.entries(balances).map(([name, amount]) => (
            <li key={name}>
              {name}: {Number(amount).toFixed(2)}
            </li>
          ))}
        </ul>
      </div>

      {/* Suggested person-to-person debts */}
      <div className="mt-4">
        <h3 className="font-semibold mb-2">Who should pay whom</h3>
        {debts.length === 0 && groupId && <div>No dues to settle 🎉</div>}
        <ul>
          {debts.map((d, idx) => (
            <li key={idx}>
              <strong>{d.from}</strong> should pay <strong>{d.to}</strong>:{" "}
              {Number(d.amount).toFixed(2)}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
