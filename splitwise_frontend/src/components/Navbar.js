import { Link } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="p-4 bg-gray-800 text-white flex gap-6">
      <Link to="/">Users </Link>
      <Link to="/groups">Groups </Link>
      <Link to="/expenses">Add Expense </Link>
      <Link to="/balances">Balances </Link>
      <Link to="/settlements">Settle Up </Link>
    </nav>
  );
}
