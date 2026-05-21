import {
  useEffect,
  useState
} from "react";

import {
  useParams
} from "react-router-dom";

import {
  getContractById,
  downloadContractFile
} from "../services/contractService";

const ContractDetailsPage = () => {

  const { id } = useParams();

  const [contract, setContract] =
    useState(null);

  const [loading, setLoading] =
    useState(true);

  const [pdfUrl, setPdfUrl] =
    useState(null);

  const [pdfLoading, setPdfLoading] =
    useState(false);

  const [downloadError, setDownloadError] =
    useState("");

  const complianceKeywords = (() => {
    if (!contract?.complianceKeywords) {
      return [];
    }

    try {
      return JSON.parse(contract.complianceKeywords);
    } catch (error) {
      return contract.complianceKeywords
        .split(/[,\s]+/)
        .map((keyword) => keyword.trim())
        .filter(Boolean);
    }
  })();

  const fetchPdfPreview = async (contractId) => {
    if (!contractId) {
      return;
    }

    setPdfLoading(true);
    setDownloadError("");

    try {
      const blob = await downloadContractFile(contractId);
      const url = URL.createObjectURL(blob);
      setPdfUrl(url);
    } catch (error) {
      console.error(error);
      setDownloadError("Unable to load PDF preview. Please use the download button.");
    } finally {
      setPdfLoading(false);
    }
  };

  const handleDownload = () => {
    if (!pdfUrl || !contract) {
      return;
    }

    const link = document.createElement("a");
    link.href = pdfUrl;
    link.download = contract.fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  useEffect(() => {

    fetchContract();

    const interval = setInterval(() => {

      fetchContract();

    }, 4000);

    return () => clearInterval(interval);

  }, []);

  useEffect(() => {
    if (!contract?.id) {
      return;
    }

    fetchPdfPreview(contract.id);

    return () => {
      if (pdfUrl) {
        URL.revokeObjectURL(pdfUrl);
      }
    };
  }, [contract?.id]);

  const fetchContract = async () => {

    try {

      const data =
        await getContractById(id);

      setContract(data);

    } catch (error) {

      console.error(error);

    } finally {

      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="p-6">
        Loading contract...
      </div>
    );
  }

  if (!contract) {
    return (
      <div className="p-6 text-red-500">
        Contract not found
      </div>
    );
  }

  return (
    <div className="p-6 grid grid-cols-1 lg:grid-cols-2 gap-6">

      {/* Header */}

      <div className="bg-white rounded-xl shadow p-6">

        <div className="flex justify-between items-center">

          <div>

            <h1 className="text-2xl font-bold">
              {contract.fileName}
            </h1>

            <p className="text-gray-500 mt-1">
              AI Contract Intelligence
            </p>

          </div>

          <div>

            <span
              className={`px-4 py-2 rounded-full text-sm font-semibold
              ${contract.status === "COMPLETED"
                  ? "bg-green-100 text-green-700"
                  : contract.status === "PROCESSING"
                    ? "bg-yellow-100 text-yellow-700"
                    : "bg-red-100 text-red-700"
                }`}
            >
              {contract.status}
            </span>

          </div>

        </div>

      </div>

      {/* PDF Viewer */}

      <div className="bg-white rounded-xl shadow p-4 h-[900px]">

        <div className="
    flex
    justify-between
    items-center
    mb-4
  ">

          <h2 className="text-xl font-semibold">
            Contract Document
          </h2>

          <button
            onClick={handleDownload}
            disabled={pdfLoading || !pdfUrl}
            className="
        bg-blue-600
        text-white
        px-4
        py-2
        rounded-lg
        hover:bg-blue-700
        disabled:opacity-50
      "
          >
            {pdfLoading ? "Preparing PDF..." : "Download PDF"}
          </button>

        </div>

        {pdfLoading ? (
          <div className="flex h-[800px] items-center justify-center rounded-lg border border-dashed border-slate-300 bg-slate-50 text-slate-500">
            Loading preview...
          </div>
        ) : pdfUrl ? (
          <iframe
            src={pdfUrl}
            title="PDF Viewer"
            className="
      w-full
      h-[800px]
      border
      rounded-lg
    "
          />
        ) : (
          <div className="flex h-[800px] items-center justify-center rounded-lg border border-dashed border-slate-300 bg-slate-50 text-slate-500">
            {downloadError || "Preview not available."}
          </div>
        )}
        <div className="space-y-6"></div>

      </div>

      {/* AI Summary */}

      <div className="bg-white rounded-xl shadow p-6">

        <h2 className="text-xl font-semibold mb-4">
          AI Summary
        </h2>

        {
          contract.status === "PROCESSING" && (

            <div className="
      mb-4
      bg-yellow-50
      border
      border-yellow-200
      p-4
      rounded-lg
    ">

              <div className="
        flex
        items-center
        gap-3
      ">

                <div className="
          w-4
          h-4
          rounded-full
          bg-yellow-500
          animate-pulse
        " />

                <p className="text-yellow-700">

                  AI analysis is currently processing...

                </p>

              </div>

            </div>
          )
        }

        <p className="text-gray-700 leading-7">
          {
  contract.summary
    ? contract.summary
    : contract.status === "PROCESSING"
    ? "AI is generating summary..."
    : "Summary not available."
}
        </p>

      </div>

      {/* Risk Analysis */}

      <div className="bg-white rounded-xl shadow p-6">

        <div className="flex items-center justify-between mb-4 gap-4">

          <div>
            <h2 className="text-xl font-semibold">
              Risk Analysis
            </h2>
          </div>

          <div className="flex items-center gap-2">
            <span
              className={`px-3 py-1 rounded-full text-sm font-semibold
              ${contract.riskLevel === "HIGH"
                  ? "bg-red-100 text-red-700"
                  : contract.riskLevel === "MEDIUM"
                    ? "bg-yellow-100 text-yellow-700"
                    : "bg-green-100 text-green-700"
                }`}
            >
              {contract.riskLevel || "UNKNOWN"}
            </span>

            <span className="text-sm text-slate-500">
              Score: {contract.riskScore ?? "—"}
            </span>
          </div>

        </div>

        <div className="bg-red-50 border border-red-200 rounded-lg p-4 whitespace-pre-line text-red-700 leading-7">
          {contract.riskAnalysis || "No risk analysis available."}
        </div>

      </div>

      {/* Keywords */}

      <div className="bg-white rounded-xl shadow p-6">

        <h2 className="text-xl font-semibold mb-4">
          Extracted Keywords
        </h2>

        <div className="flex flex-wrap gap-3">
          {complianceKeywords.length > 0 ? (
            complianceKeywords.map((keyword, index) => (
              <span
                key={index}
                className="
                  px-4 py-2
                  bg-blue-100
                  text-blue-700
                  rounded-full
                  text-sm
                  font-medium
                "
              >
                {keyword}
              </span>
            ))
          ) : (
            <span className="text-sm text-slate-500">
              No extracted keywords available.
            </span>
          )}
        </div>

      </div>

      {/* Metadata */}

      <div className="bg-white rounded-xl shadow p-6">

        <h2 className="text-xl font-semibold mb-4">
          Document Metadata
        </h2>

        <div className="grid grid-cols-2 gap-4">

          <div>

            <p className="text-gray-500 text-sm">
              Contract ID
            </p>

            <p className="font-semibold">
              #{contract.id}
            </p>

          </div>

          <div>

            <p className="text-gray-500 text-sm">
              File Name
            </p>

            <p className="font-semibold">
              {contract.fileName}
            </p>

          </div>

          <div>

            <p className="text-gray-500 text-sm">
              Processing Status
            </p>

            <p className="font-semibold">
              {contract.status}
            </p>

          </div>

        </div>

      </div>

    </div>
  );
};

export default ContractDetailsPage;