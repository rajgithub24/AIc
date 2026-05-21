import Sidebar from "./Sidebar";
import Navbar from "./Navbar";
import { Outlet } from "react-router-dom";

const DashboardLayout = () => {

  return (
    <div className="flex bg-gray-100">

      <Sidebar />

      <div className="flex-1 min-h-screen">

        <Navbar />

        <div className="p-6">
          <Outlet />
        </div>

      </div>

    </div>
  );
};

export default DashboardLayout;