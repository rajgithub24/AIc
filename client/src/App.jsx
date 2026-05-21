import {
  BrowserRouter,
  Routes,
  Route,
  Navigate
} from "react-router-dom";
import ContractDetailsPage
from "./pages/ContractDetailsPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import UploadPage from "./pages/UploadPage";
import ContractsPage from "./pages/ContractsPage";

import ProtectedRoute from "./routes/ProtectedRoute";
import DashboardLayout from "./components/layout/DashboardLayout";

function App() {

  return (
    <BrowserRouter>

      <Routes>

        <Route path="/login" element={<LoginPage />} />

        <Route path="/register" element={<RegisterPage />} />

        <Route
          element={
            <ProtectedRoute>
              <DashboardLayout />
            </ProtectedRoute>
          }
        >

          <Route path="/dashboard" element={<DashboardPage />} />

          <Route path="/upload" element={<UploadPage />} />

          <Route path="/contracts" element={<ContractsPage />} />

          <Route path="/contracts/:id" element={<ContractDetailsPage />} />

        </Route>

        <Route
          path="*"
          element={<Navigate to="/dashboard" />}
        />

      </Routes>

    </BrowserRouter>
  );
}

export default App;