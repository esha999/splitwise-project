import { useState, useEffect } from "react";
import axios from "axios";

export default function CreateGroup() {
  const [groups, setGroups] = useState([]);
  const [users, setUsers] = useState([]);
  const [groupName, setGroupName] = useState("");
  const [selectedGroup, setSelectedGroup] = useState("");
  const [selectedUser, setSelectedUser] = useState("");
  const [members, setMembers] = useState([]);

  // Fetch groups and users
  useEffect(() => {
    fetchGroups();
    fetchUsers();
  }, []);

  const fetchGroups = async () => {
    try {
      const res = await axios.get("http://localhost:8081/api/groups");
      setGroups(res.data);
    } catch (err) {
      console.error("Error fetching groups", err);
    }
  };

  const fetchUsers = async () => {
    try {
      const res = await axios.get("http://localhost:8081/api/users");
      setUsers(res.data);
    } catch (err) {
      console.error("Error fetching users", err);
    }
  };

  // Fetch members when group changes
  useEffect(() => {
    if (selectedGroup) {
      fetchGroupMembers(selectedGroup);
    }
  }, [selectedGroup]);

  const fetchGroupMembers = async (groupId) => {
    try {
      const res = await axios.get(
        `http://localhost:8081/api/groups/${groupId}/members`
      );
      setMembers(res.data);
    } catch (err) {
      console.error("Error fetching members", err);
    }
  };

  const createGroup = async () => {
    try {
      const res = await axios.post("http://localhost:8081/api/groups", {
        name: groupName,
      });
      setGroups([...groups, res.data]);
      setGroupName("");
    } catch (err) {
      console.error("Error creating group", err);
    }
  };

  const addMemberToGroup = async () => {
    if (!selectedGroup || !selectedUser) return;
    try {
      await axios.post(
        `http://localhost:8081/api/groups/${selectedGroup}/members`,
        {
          userId: selectedUser,
        }
      );
      fetchGroupMembers(selectedGroup); // refresh members list
      setSelectedUser(""); // reset dropdown
    } catch (err) {
      console.error("Error adding member", err);
    }
  };

  return (
    <div className="p-4 space-y-6">
      {/* Create New Group */}
      <div className="p-4 border rounded-lg shadow-md">
        <h2 className="text-lg font-semibold mb-2">Create New Group</h2>
        <input
          type="text"
          value={groupName}
          onChange={(e) => setGroupName(e.target.value)}
          placeholder="Enter group name"
          className="border p-2 mr-2 rounded"
        />
        <button
          onClick={createGroup}
          className="bg-blue-500 text-white px-3 py-1 rounded"
        >
          Create
        </button>
      </div>

      {/* Select Group */}
      <div className="p-4 border rounded-lg shadow-md">
        <h2 className="text-lg font-semibold mb-2">Manage Groups</h2>

        <select
          value={selectedGroup}
          onChange={(e) => setSelectedGroup(e.target.value)}
          className="border p-2 rounded mb-3"
        >
          <option value="">Select a group</option>
          {groups.map((g) => (
            <option key={g.id} value={g.id}>
              {g.name}
            </option>
          ))}
        </select>

        {selectedGroup && (
          <div className="mt-4">
            {/* Add Member */}
            <div className="flex items-center space-x-2 mb-4">
              <select
                value={selectedUser}
                onChange={(e) => setSelectedUser(e.target.value)}
                className="border p-2 rounded"
              >
                <option value="">Select a user</option>
                {users.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.name}
                  </option>
                ))}
              </select>
              <button
                onClick={addMemberToGroup}
                className="bg-green-500 text-white px-3 py-1 rounded"
              >
                Add Member
              </button>
            </div>

            {/* Members List */}
            <h3 className="font-semibold mb-2">Group Members:</h3>
            <ul className="list-disc pl-6">
              {members.length > 0 ? (
                members.map((m) => <li key={m.id}>{m.name}</li>)
              ) : (
                <p>No members yet</p>
              )}
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}
