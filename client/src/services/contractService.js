import api from "../api/axios";

export const uploadContract = async (
  file,
  onUploadProgress
) => {

  const formData = new FormData();

  formData.append("file", file);

  const response = await api.post(
    "/files/upload",
    formData,
    {
      onUploadProgress,
    }
  );

  return response.data;
};

export const getContracts = async (
  page = 0,
  size = 5
) => {

  const response = await api.get(
    `/contracts?page=${page}&size=${size}`
  );

  return response.data;
};

export const searchContracts =
  async (keyword) => {

    const response = await api.get(
      `/contracts/search?keyword=${keyword}`
    );

    return response.data;
};

export const getContractsByStatus =
  async (status) => {

    const response = await api.get(
      `/contracts/status/${status}`
    );

    return response.data;
};

export const getRiskContracts =
  async () => {

    const response = await api.get(
      "/contracts/risk"
    );

    return response.data;
};

export const getContractById =
  async (id) => {

    const response = await api.get(
      `/contracts/${id}`
    );

    return response.data;
};

export const downloadContractFile =
  async (id) => {

    const response = await api.get(
      `/files/download/${id}`,
      {
        responseType: "blob",
      }
    );

    return response.data;
};

export const getContractDownloadUrl =
  (id) => {

    return `${
      import.meta.env.VITE_API_BASE_URL
    }/files/download/${id}`;
};