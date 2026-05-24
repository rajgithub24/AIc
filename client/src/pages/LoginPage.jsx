import { useState } from "react";
import { loginUser } from "../services/authService";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const LoginPage = () => {

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const navigate = useNavigate();

  const { login } = useAuth();

  const handleChange = (e) => {

    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    if (isLoading) {
      return;
    }

    setIsLoading(true);
    setMessage(null);

    try {

      const response =
        await loginUser(formData);

      login(response.token);

      setMessage({
        type: "success",
        text: "Login successful. Redirecting...",
      });

      navigate("/");

    } catch (error) {

      console.error(error);

      setMessage({
        type: "error",
        text: "Login failed. Please check your email and password.",
      });
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">

      <form
        onSubmit={handleSubmit}
        className="bg-white p-8 rounded-xl shadow-md w-[400px]"
      >

        <h1 className="text-3xl font-bold mb-6 text-center">
          Login
        </h1>

        <input
          type="email"
          name="email"
          placeholder="Enter email"
          className="w-full border p-3 rounded mb-4"
          value={formData.email}
          onChange={handleChange}
          disabled={isLoading}
          required
        />

        <input
          type="password"
          name="password"
          placeholder="Enter password"
          className="w-full border p-3 rounded mb-4"
          value={formData.password}
          onChange={handleChange}
          disabled={isLoading}
          required
        />

        {message && (
          <div
            className={`mb-4 rounded border px-3 py-2 text-sm ${
              message.type === "success"
                ? "border-green-200 bg-green-50 text-green-700"
                : "border-red-200 bg-red-50 text-red-700"
            }`}
          >
            {message.text}
          </div>
        )}

        <button
          type="submit"
          className="w-full bg-black text-white p-3 rounded disabled:cursor-not-allowed disabled:opacity-70"
          disabled={isLoading}
        >
          {isLoading ? (
            <span className="flex items-center justify-center gap-2">
              <span className="h-4 w-4 animate-spin rounded-full border-2 border-white/40 border-t-white" />
              Signing in...
            </span>
          ) : (
            "Login"
          )}
        </button>

        <p className="text-sm text-center mt-4">
          New user? <Link to="/register" className="text-blue-600 hover:underline">Register</Link>
        </p>

      </form>
    </div>
  );
};

export default LoginPage;
