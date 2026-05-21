import {
  useEffect,
  useState
} from "react";

import {
  getContracts,
  searchContracts,
  getContractsByStatus,
  getRiskContracts
} from "../services/contractService";
import { Link } from "react-router-dom";

const ContractsPage = () => {


  const [contracts, setContracts] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(0);

  const [search, setSearch] =
    useState("");

  const [statusFilter, setStatusFilter] =
    useState("");

  useEffect(() => {

    fetchContracts();

    const interval = setInterval(() => {

      fetchContracts();

    }, 5000);

    return () => clearInterval(interval);

  }, [page]);

  const fetchContracts = async () => {

    try {

      setLoading(true);

      const response =
        await getContracts(page, 5);

      const contractsData = normalizeContractsResponse(response);

      setContracts(contractsData);
      setTotalPages(response?.totalPages ?? 1);

    } catch (error) {

      console.error(error);

    } finally {

      setLoading(false);
    }
  };

  const normalizeContractsResponse = (response) => {
    if (Array.isArray(response)) {
      return response;
    }

    if (response?.content) {
      return response.content;
    }

    return [];
  };

  const getStatusColor = (status) => {

    switch (status) {

      case "COMPLETED":
        return "bg-green-100 text-green-700";

      case "PROCESSING":
        return "bg-yellow-100 text-yellow-700";

      case "FAILED":
        return "bg-red-100 text-red-700";

      default:
        return "bg-gray-100 text-gray-700";
    }
  };

  const handleSearch = async (
    value
  ) => {

    setSearch(value);

    if (!value) {

      fetchContracts();

      return;
    }

    try {

      const response =
        await searchContracts(value);

      setContracts(normalizeContractsResponse(response));

    } catch (error) {

      console.error(error);
    }
  };

  const handleStatusFilter =
    async (status) => {

      setStatusFilter(status);

      if (!status) {

        fetchContracts();

        return;
      }

      try {

        const response =
          await getContractsByStatus(
            status
          );

        setContracts(normalizeContractsResponse(response));

      } catch (error) {

        console.error(error);
      }
    };

  const handleRiskFilter =
    async () => {

      try {

        const response =
          await getRiskContracts();

        setContracts(normalizeContractsResponse(response));

      } catch (error) {

        console.error(error);
      }
    };

  return (
    <div>

      <div className="flex items-center justify-between mb-6">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">

          <h1 className="text-3xl font-bold">
            My Contracts
          </h1>

          <div className="flex flex-col md:flex-row gap-3">

            <input
              type="text"
              placeholder="Search keywords..."
              value={search}
              onChange={(e) =>
                handleSearch(
                  e.target.value
                )
              }
              className="border rounded-lg px-4 py-2"
            />

            <select
              value={statusFilter}
              onChange={(e) =>
                handleStatusFilter(
                  e.target.value
                )
              }
              className="border rounded-lg px-4 py-2"
            >

              <option value="">
                All Status
              </option>

              <option value="COMPLETED">
                Completed
              </option>

              <option value="PROCESSING">
                Processing
              </option>

              <option value="FAILED">
                Failed
              </option>

            </select>

            <button
              onClick={handleRiskFilter}
              className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg"
            >
              Risk Contracts
            </button>

          </div>

        </div>
        <h1 className="text-3xl font-bold">
          My Contracts
        </h1>

      </div>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <div className="
  flex
  justify-between
  items-center
  mb-4
">

          <p className="text-gray-500 text-sm">

            Dashboard auto-refreshes every 5 seconds

          </p>

          <div className="
    flex
    items-center
    gap-2
  ">

            <div className="
      w-2
      h-2
      rounded-full
      bg-green-500
      animate-pulse
    " />

            <span className="text-sm text-gray-600">
              Live
            </span>

          </div>

        </div>
        {
          loading ? (

            <div className="p-10 text-center">
              Loading contracts...
            </div>

          ) : (

            <table className="w-full">

              <thead className="bg-gray-50 border-b">
                <tr>

                  <th className="px-6 py-3">
                    Actions
                  </th>

                  <th className="text-left p-4">
                    File Name
                  </th>

                  <th className="text-left p-4">
                    Status
                  </th>

                  <th className="text-left p-4">
                    Keywords
                  </th>

                  <th className="text-left p-4">
                    Risk Analysis
                  </th>

                </tr>

              </thead>

              <tbody>

                {
                  contracts.map((contract) => (

                    <tr
                      key={contract.id}
                      className="border-b hover:bg-gray-50"
                    >

                      <td className="p-4">
                        <Link
                          to={`/contracts/${contract.id}`}
                          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
                        >
                          View
                        </Link>
                      </td>

                      <td className="p-4">
                        {contract.fileName}
                      </td>

                      <td className="p-4">

                        <span
                          className={`
  px-3
  py-1
  rounded-full
  text-sm
  font-semibold
  animate-pulse

  ${contract.status === "COMPLETED"
                              ? "bg-green-100 text-green-700"
                              : contract.status === "PROCESSING"
                                ? "bg-yellow-100 text-yellow-700"
                                : "bg-red-100 text-red-700"
                            }
`}
                        >
                          {contract.status}
                        </span>

                      </td>

                      <td className="p-4 text-sm text-gray-600">
                        {
                          contract.extractedKeywords
                        }
                      </td>

                      <td className="p-4 text-sm text-gray-600">
                        {
                          contract.riskAnalysis
                        }
                      </td>

                    </tr>
                  ))
                }

              </tbody>

            </table>
          )
        }

      </div>

      <div className="flex items-center justify-center gap-4 mt-6">

        <button
          onClick={() =>
            setPage((prev) =>
              Math.max(prev - 1, 0)
            )
          }
          disabled={page === 0}
          className="px-4 py-2 bg-gray-200 rounded-lg disabled:opacity-50"
        >
          Previous
        </button>

        <p>
          Page {page + 1} of {totalPages}
        </p>

        <button
          onClick={() =>
            setPage((prev) =>
              Math.min(
                prev + 1,
                totalPages - 1
              )
            )
          }
          disabled={
            page >= totalPages - 1
          }
          className="px-4 py-2 bg-gray-200 rounded-lg disabled:opacity-50"
        >
          Next
        </button>

      </div>

    </div>
  );
};

export default ContractsPage;