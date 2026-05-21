import { useState } from "react";
import { registerUser } from "../services/authService";
import { useNavigate, Link } from "react-router-dom";

const RegisterPage = () => {

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const handleChange = (e) => {

    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {

    e.preventDefault();

    try {

      await registerUser(formData);

      alert("Registration Successful");

      // Redirect to login after successful registration
      navigate("/login");

    } catch (error) {

      console.error(error);

      alert("Registration Failed");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">

      <form
        onSubmit={handleSubmit}
        className="bg-white p-8 rounded-xl shadow-md w-[400px]"
      >

        <h1 className="text-3xl font-bold mb-6 text-center">
          Register
        </h1>

        <input
          type="email"
          name="email"
          placeholder="Enter email"
          className="w-full border p-3 rounded mb-4"
          onChange={handleChange}
        />

        <input
          type="password"
          name="password"
          placeholder="Enter password"
          className="w-full border p-3 rounded mb-4"
          onChange={handleChange}
        />

        <button
          type="submit"
          className="w-full bg-black text-white p-3 rounded"
        >
          Register
        </button>

        <p className="text-sm text-center mt-4">
          Already have an account? <Link to="/login" className="text-blue-600 hover:underline">Login</Link>
        </p>

      </form>
    </div>
  );
};

export default RegisterPage;