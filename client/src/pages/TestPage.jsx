import { useEffect } from "react";
import api from "../api/axios";

const TestPage = () => {

  useEffect(() => {
    api.get("/contracts/paginated?page=0&size=2")
      .then((response) => {
        console.log(response.data);
      })
      .catch((error) => {
        console.error(error);
      });
  }, []);

  return (
    <div className="p-10 text-xl">
      Backend Connection Test
    </div>
  );
};

export default TestPage;