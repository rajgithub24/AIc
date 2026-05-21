import { useState } from "react";
import { uploadContract } from "../services/contractService";

const UploadPage = () => {

  const [selectedFile, setSelectedFile] =
    useState(null);

  const [uploading, setUploading] =
    useState(false);

  const [progress, setProgress] =
    useState(0);

  const [message, setMessage] =
    useState("");

  const handleFileChange = (e) => {

    setSelectedFile(
      e.target.files[0]
    );
  };

  const handleUpload = async () => {

    if (!selectedFile) {
      return;
    }

    try {

      setUploading(true);

      setMessage("");

      const response =
        await uploadContract(
          selectedFile,
          (progressEvent) => {

            const percent =
              Math.round(
                (
                  progressEvent.loaded * 100
                ) / progressEvent.total
              );

            setProgress(percent);
          }
        );

      setMessage(response.message);

    } catch (error) {

      setMessage(
        "Upload failed"
      );

      console.error(error);

    } finally {

      setUploading(false);
    }
  };

  return (
    <div>

      <h1 className="text-3xl font-bold mb-6">
        Upload Contract
      </h1>

      <div className="bg-white p-8 rounded-2xl shadow-sm max-w-2xl">

        <input
          type="file"
          accept=".pdf"
          onChange={handleFileChange}
          className="mb-4"
        />

        {
          selectedFile && (
            <p className="mb-4 text-sm text-gray-600">
              Selected:
              {" "}
              {selectedFile.name}
            </p>
          )
        }

        {
          uploading && (
            <div className="mb-4">

              <div className="w-full bg-gray-200 rounded-full h-4">

                <div
                  className="bg-blue-600 h-4 rounded-full transition-all"
                  style={{
                    width: `${progress}%`,
                  }}
                />

              </div>

              <p className="mt-2 text-sm">
                Uploading:
                {" "}
                {progress}%
              </p>

            </div>
          )
        }

        <button
          onClick={handleUpload}
          disabled={uploading}
          className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-xl disabled:opacity-50"
        >
          {
            uploading
              ? "Uploading..."
              : "Upload PDF"
          }
        </button>
        <p className="text-xs text-gray-500 mt-3">
          Only PDF files are allowed
        </p>

        {
          message && (
            <p className="mt-4 text-sm font-medium">
              {message}
            </p>
          )
        }

      </div>

    </div>
  );
};

export default UploadPage;