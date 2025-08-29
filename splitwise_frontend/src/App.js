import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import UserList from "./components/UserList";
import CreateGroup from "./components/CreateGroup";
import AddExpense from "./components/AddExpense";
import Balance from "./components/Balance";
import Settlement from "./components/Settlement";

export default function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<UserList />} />

        <Route path="/groups" element={<CreateGroup />} />
        <Route path="/expenses" element={<AddExpense />} />
        <Route path="/balances" element={<Balance />} />
        <Route path="/settlements" element={<Settlement />} />
      </Routes>
    </BrowserRouter>
  );
}
