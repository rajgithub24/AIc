import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
} from "chart.js";

import { motion } from "framer-motion";
import { Doughnut }
  from "react-chartjs-2";

ChartJS.register(
  ArcElement,
  Tooltip,
  Legend
);

const ContractsChart = ({
  contracts
}) => {

  const completed =
    contracts.filter(
      (c) =>
        c.status === "COMPLETED"
    ).length;

  const processing =
    contracts.filter(
      (c) =>
        c.status === "PROCESSING"
    ).length;

  const failed =
    contracts.filter(
      (c) =>
        c.status === "FAILED"
    ).length;

  const data = {
    labels: [
      "Completed",
      "Processing",
      "Failed",
    ],

    datasets: [
      {
        data: [
          completed,
          processing,
          failed,
        ],

        backgroundColor: [
          "#22c55e",
          "#eab308",
          "#ef4444",
        ],

        borderWidth: 0,
      },
    ],
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.7, ease: "easeOut" }}
      className="rounded-[2rem] border border-white/10 bg-gradient-to-br from-slate-950/95 to-slate-900/90 p-6 shadow-[0_35px_120px_-50px_rgba(15,23,42,0.65)]"
    >

      <div className="flex flex-col gap-6 lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p className="text-sm uppercase tracking-[0.3em] text-cyan-300">
            Status Overview
          </p>
          <h2 className="mt-3 text-3xl font-semibold tracking-tight text-white">
            Contract Health
          </h2>
          <p className="mt-2 max-w-xl text-sm leading-6 text-slate-400">
            A visual breakdown of current contract processing outcomes, with clear color coding and actionable counts.
          </p>
        </div>
        <div className="rounded-3xl border border-white/10 bg-slate-900/80 px-4 py-3 text-white shadow-[0_20px_60px_-40px_rgba(15,23,42,0.7)]">
          <p className="text-xs uppercase tracking-[0.24em] text-slate-400">Updated</p>
          <p className="mt-1 text-lg font-semibold">Real-time snapshot</p>
        </div>
      </div>

      <div className="mt-8 grid gap-6 lg:grid-cols-[0.9fr_0.7fr]">
        <div className="rounded-[1.75rem] border border-white/10 bg-slate-950/95 p-6 shadow-[0_26px_80px_-60px_rgba(15,23,42,0.8)]">
          <div className="max-w-sm mx-auto">
            <Doughnut data={data} />
          </div>
        </div>

        <div className="space-y-4 rounded-[1.75rem] border border-white/10 bg-slate-900/90 p-6 shadow-[0_26px_80px_-60px_rgba(15,23,42,0.8)]">
          {[
            { label: "Completed", value: completed, color: "bg-emerald-400/15 text-emerald-300 border-emerald-400/30" },
            { label: "Processing", value: processing, color: "bg-amber-400/15 text-amber-300 border-amber-400/30" },
            { label: "Failed", value: failed, color: "bg-rose-400/15 text-rose-300 border-rose-400/30" },
          ].map((item) => (
            <div key={item.label} className="rounded-3xl border px-4 py-4 text-white transition hover:-translate-y-1 hover:border-white/20 hover:bg-white/5">
              <div className="flex items-center justify-between gap-4">
                <span className={`inline-flex rounded-full border px-3 py-1 text-xs font-semibold uppercase tracking-[0.3em] ${item.color}`}>
                  {item.label}
                </span>
                <span className="text-3xl font-semibold text-white">{item.value}</span>
              </div>
              <p className="mt-3 text-sm text-slate-400">
                {item.label === "Failed" ? "Needs attention and manual review." : "Continuing through AI contract intelligence flow."}
              </p>
            </div>
          ))}
        </div>
      </div>

    </motion.div>
  );
};

export default ContractsChart;