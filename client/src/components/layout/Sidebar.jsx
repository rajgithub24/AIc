import { Link, useLocation } from "react-router-dom";

const Sidebar = () => {

  const location = useLocation();

  const menuItems = [
    {
      name: "Dashboard",
      path: "/dashboard"
    },
    {
      name: "Upload Contract",
      path: "/upload"
    },
    {
      name: "My Contracts",
      path: "/contracts"
    }
  ];

  return (
    <div className="w-64 min-h-screen bg-gray-900 text-white p-5">

      <h1 className="text-2xl font-bold mb-10">
        AI Contract
      </h1>

      <div className="space-y-3">

        {
          menuItems.map((item) => (

            <Link
              key={item.path}
              to={item.path}
              className={`block p-3 rounded-lg transition ${
                location.pathname === item.path
                  ? "bg-blue-600"
                  : "hover:bg-gray-800"
              }`}
            >
              {item.name}
            </Link>
          ))
        }

      </div>

    </div>
  );
};

export default Sidebar;