import { useAuth } from "../../context/AuthContext";

const Navbar = () => {

  const { logout } = useAuth();

  return (
    <div className="h-16 bg-white border-b flex items-center justify-between px-6">

      <h2 className="text-xl font-semibold">
        Dashboard
      </h2>

      <button
        onClick={logout}
        className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg"
      >
        Logout
      </button>

    </div>
  );
};

export default Navbar;