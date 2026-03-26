import { useState, useRef } from "react";

export default function CampaignForm({ onGenerate, addToast }) {

  const [brief, setBrief] = useState("");
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [stepText, setStepText] = useState("");
  const [showSteps, setShowSteps] = useState(false);
  const [shaking, setShaking] = useState(false);

  const [agentSummary, setAgentSummary] = useState(null);   // ⭐ LOOP RESULT STATE

  const textareaRef = useRef(null);

  const genSteps = [
    "Analyzing brief...",
    "Identifying target audience...",
    "Crafting subject line...",
    "Writing email body...",
    "Optimizing for conversion...",
    "Finalizing campaign...",
  ];

  const shakeBrief = () => {
    const ta = textareaRef.current;
    if (!ta) return;

    setShaking(true);
    ta.style.borderColor = "#FF3B5C";
    ta.style.boxShadow = "0 0 0 3px rgba(255, 59, 92, 0.25)";

    const shakes = [6, -6, 5, -5, 3, -3, 0];
    let i = 0;
    ta.style.transition = "transform 0.07s";

    const interval = setInterval(() => {
      ta.style.transform = `translateX(${shakes[i]}px)`;
      if (++i >= shakes.length) {
        clearInterval(interval);
        ta.style.transform = "";
        setTimeout(() => {
          setShaking(false);
          ta.style.borderColor = "";
          ta.style.boxShadow = "";
        }, 800);
      }
    }, 60);
  };

  // ⭐ ONLY PREVIEW PLAN
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!brief.trim()) { shakeBrief(); return; }

    setLoading(true);
    setShowSteps(true);
    setProgress(0);

    let step = 0;
    const stepInterval = setInterval(() => {
      if (step < genSteps.length) {
        setStepText(genSteps[step]);
        setProgress(Math.round(((step + 1) / genSteps.length) * 90));
        step++;
      }
    }, 240);

    try {

      const response = await fetch("https://ai-marketing-agent-1anc.onrender.com/campaign/plan", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ brief }),
      });

      const data = await response.json();

      clearInterval(stepInterval);
      setProgress(100);

      setTimeout(() => {
        setShowSteps(false);
        setProgress(0);
        onGenerate(data);
      }, 300);

    } catch (err) {
      clearInterval(stepInterval);
      setShowSteps(false);
      setProgress(0);
      addToast({ message: err.message || "Failed to generate campaign", variant: "error" });
    } finally {
      setLoading(false);
    }
  };

  // ⭐⭐ FULL AGENT LOOP EXECUTION
  const runAgent = async () => {

    if (!brief.trim()) { shakeBrief(); return; }

    setLoading(true);
    setShowSteps(true);
    setProgress(20);
    setStepText("Running intelligent agent loop...");

    try {

      const response = await fetch("https://ai-marketing-agent-1anc.onrender.com/campaign/run-agent", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ brief }),
      });

      const data = await response.json();

      setAgentSummary(data);

      setProgress(100);
      setTimeout(() => {
        setShowSteps(false);
        setProgress(0);
      }, 400);

      addToast({ message: "Agent campaign executed with optimization loop", variant: "success" });

    } catch (err) {

      setShowSteps(false);
      setProgress(0);
      addToast({ message: "Agent execution failed", variant: "error" });

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="card">

      <div className="card-header">
        <div className="card-icon">✦</div>
        <div className="card-header-text">
          <div className="card-title">Create New Campaign</div>
          <div className="card-subtitle">Describe your goal — AI handles the rest</div>
        </div>
      </div>

      <form onSubmit={handleSubmit}>

        <label className="form-label">Campaign Brief</label>

        <textarea
          ref={textareaRef}
          className={`form-textarea${shaking ? " textarea--error" : ""}`}
          rows="4"
          placeholder="Run campaign for premium credit card for Bangalore female customers with income above 1 lakh and credit score above 700"
          value={brief}
          onChange={(e) => setBrief(e.target.value)}
        />

        <div className={`generating-bar${showSteps ? " active" : ""}`}>
          <div className="generating-progress" style={{ width: `${progress}%` }} />
        </div>

        <div className={`generating-steps${showSteps ? " active" : ""}`}>
          <div className="step-text">{stepText}</div>
        </div>

        <div className="preview-actions" style={{ marginTop: 24 }}>

          <button className="btn btn-primary" disabled={loading}>
            {loading ? "Generating..." : "Generate Preview"}
          </button>

          <button
            type="button"
            className="btn btn-success"
            onClick={runAgent}
            disabled={loading}
            style={{ marginLeft: 12 }}
          >
            🚀 Run Intelligent Agent Loop
          </button>

        </div>

      </form>

      {/* ⭐ SHOW LOOP RESULT */}

      {agentSummary && (
        <div className="agent-result" style={{ marginTop: 25 }}>

          <h3>📊 Optimization Loop Result</h3>

          <p><b>First Campaign:</b> {agentSummary.firstCampaignId}</p>
          <p>Open Rate: {(agentSummary.firstOpenRate * 100).toFixed(2)}%</p>
          <p>Click Rate: {(agentSummary.firstClickRate * 100).toFixed(2)}%</p>

          <p><b>Optimization Triggered:</b> {agentSummary.optimized ? "YES" : "NO"}</p>

          {agentSummary.optimized && (
            <>
              <p><b>Second Campaign:</b> {agentSummary.secondCampaignId}</p>
              <p>Open Rate: {(agentSummary.secondOpenRate * 100).toFixed(2)}%</p>
              <p>Click Rate: {(agentSummary.secondClickRate * 100).toFixed(2)}%</p>
              <p><b>Improved Subject:</b> {agentSummary.improvedSubject}</p>
            </>
          )}

        </div>
      )}

    </div>
  );
}