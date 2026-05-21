import { useEffect, useState }
from "react";

import { motion } from "framer-motion";

import {
  getContracts
} from "../services/contractService";

import AnalyticsCards
from "../components/AnalyticsCards";

import ContractsChart
from "../components/ContractsChart";

const DashboardPage = () => {

  const [contracts, setContracts] =
    useState([]);

  useEffect(() => {

    fetchContracts();

  }, []);

  const fetchContracts = async () => {

    try {

      const response =
        await getContracts(0, 100);

      setContracts(response.content);

    } catch (error) {

      console.error(error);
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.7, ease: "easeOut" }}
      className="space-y-8"
    >

      <section className="relative overflow-hidden rounded-[2rem] border border-white/10 bg-slate-950/95 px-6 py-8 shadow-[0_35px_120px_-50px_rgba(15,23,42,0.65)] sm:px-10 sm:py-10">
        <div className="absolute inset-x-0 top-0 h-52 bg-gradient-to-r from-cyan-500/20 via-slate-50/0 to-fuchsia-500/10 blur-3xl opacity-80" />
        <div className="relative grid gap-6 lg:grid-cols-[1.7fr_1fr] items-center">
          <div className="space-y-4 text-white">
            <p className="text-sm uppercase tracking-[0.3em] text-cyan-300">
              Smart contract intelligence
            </p>
            <h1 className="text-4xl font-semibold tracking-tight text-white sm:text-5xl">
              Premium Dashboard
            </h1>
            <p className="max-w-2xl text-slate-300 text-lg leading-8">
              Track your contracts with elegant metrics, contract health insights, and a rich status overview that responds beautifully across devices.
            </p>
          </div>

          <div className="rounded-[1.75rem] border border-white/10 bg-slate-900/90 p-6 shadow-[0_20px_80px_-40px_rgba(15,23,42,0.8)]">
            <div className="mb-6 flex items-center justify-between gap-4">
              <div>
                <p className="text-sm text-cyan-300 uppercase tracking-[0.24em]">
                  Total active contracts
                </p>
                <p className="text-4xl font-semibold text-white">
                  {contracts.length}
                </p>
              </div>
              <div className="rounded-3xl bg-cyan-500/10 px-4 py-3 text-cyan-200 ring-1 ring-cyan-500/20">
                <span className="text-sm font-medium">Live data</span>
              </div>
            </div>
            <div className="space-y-4 text-slate-300">
              <div className="rounded-3xl bg-white/5 p-4">
                <p className="text-xs uppercase tracking-[0.24em] text-slate-400">Performance</p>
                <p className="mt-2 text-2xl font-semibold text-white">Fast AI processing</p>
              </div>
              <div className="rounded-3xl bg-white/5 p-4">
                <p className="text-xs uppercase tracking-[0.24em] text-slate-400">Experience</p>
                <p className="mt-2 text-2xl font-semibold text-white">Smooth, responsive, polished</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <AnalyticsCards
        contracts={contracts}
      />

      <ContractsChart
        contracts={contracts}
      />

    </motion.div>
  );
};

export default DashboardPage;