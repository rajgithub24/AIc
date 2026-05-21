import { motion } from "framer-motion";

const AnalyticsCards = ({
  contracts
}) => {

  const total =
    contracts.length;

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

  const cards = [
    {
      title: "Total Contracts",
      value: total,
      description: "All tracked contracts in the current workspace.",
    },
    {
      title: "Completed",
      value: completed,
      description: "Contracts with finished AI review and status updates.",
    },
    {
      title: "Processing",
      value: processing,
      description: "Active files currently being analyzed by the AI engine.",
    },
    {
      title: "Failed",
      value: failed,
      description: "Contracts that need re-upload or manual review.",
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">

      {
        cards.map((card, index) => (

          <motion.div
            key={card.title}
            initial={{ opacity: 0, y: 22 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.55, delay: index * 0.08, ease: "easeOut" }}
            className="group overflow-hidden rounded-[1.9rem] border border-slate-800/90 bg-slate-950/95 p-6 shadow-[0_28px_90px_-60px_rgba(15,23,42,0.8)] transition duration-300 hover:-translate-y-1 hover:shadow-[0_40px_100px_-60px_rgba(34,211,238,0.18)]"
          >

            <div className="mb-5 flex items-center justify-between gap-4">
              <span className="inline-flex h-12 w-12 items-center justify-center rounded-3xl bg-slate-800/80 text-cyan-300 shadow-[0_15px_45px_-30px_rgba(34,211,238,0.8)]">
                {card.title.charAt(0)}
              </span>
              <span className="rounded-full bg-slate-800/70 px-3 py-1 text-xs uppercase tracking-[0.3em] text-slate-400">
                {card.title}
              </span>
            </div>

            <p className="text-5xl font-semibold tracking-tight text-white">
              {card.value}
            </p>

            <p className="mt-4 text-sm leading-6 text-slate-400">
              {card.description}
            </p>

          </motion.div>
        ))
      }

    </div>
  );
};

export default AnalyticsCards;