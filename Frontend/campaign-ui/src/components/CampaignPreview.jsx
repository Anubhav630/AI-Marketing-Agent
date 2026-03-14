import { useState, useEffect, useRef } from "react";

export default function CampaignPreview({ plan, onReject, addToast }) {
  const [sending, setSending] = useState(false);
  const [mounted, setMounted] = useState(false);
  const [recipientCount, setRecipientCount] = useState(0);
  const canvasRef = useRef(null);

  useEffect(() => {
    setMounted(true);
    animateNumber(0, plan.customerIds.length, 600, setRecipientCount);
  }, [plan.customerIds.length]);

  function animateNumber(from, to, duration, setter) {
    const start = performance.now();
    const step = (ts) => {
      const p = Math.min((ts - start) / duration, 1);
      const ease = 1 - Math.pow(1 - p, 3);
      setter(Math.round(from + (to - from) * ease));
      if (p < 1) requestAnimationFrame(step);
    };
    requestAnimationFrame(step);
  }

  function launchConfetti() {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    const colors = ["#FF5C1A", "#FFD200", "#00D97A", "#FF3B5C", "#ffffff", "#a78bfa"];
    const pieces = Array.from({ length: 110 }, () => ({
      x: Math.random() * canvas.width,
      y: -10 - Math.random() * 80,
      r: 4 + Math.random() * 6,
      color: colors[Math.floor(Math.random() * colors.length)],
      vx: (Math.random() - 0.5) * 5,
      vy: 3 + Math.random() * 5,
      rot: Math.random() * 360,
      rotV: (Math.random() - 0.5) * 8,
      shape: Math.random() > 0.5 ? "rect" : "circle",
      w: 6 + Math.random() * 8,
      h: 4 + Math.random() * 5,
      alpha: 1,
    }));

    const draw = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      let alive = false;
      pieces.forEach((p) => {
        p.y += p.vy; p.x += p.vx; p.vy += 0.12; p.rot += p.rotV;
        if (p.y > canvas.height * 0.7) p.alpha -= 0.025;
        if (p.alpha <= 0) return;
        alive = true;
        ctx.save();
        ctx.globalAlpha = Math.max(0, p.alpha);
        ctx.translate(p.x, p.y);
        ctx.rotate((p.rot * Math.PI) / 180);
        ctx.fillStyle = p.color;
        if (p.shape === "circle") {
          ctx.beginPath();
          ctx.arc(0, 0, p.r, 0, Math.PI * 2);
          ctx.fill();
        } else {
          ctx.fillRect(-p.w / 2, -p.h / 2, p.w, p.h);
        }
        ctx.restore();
      });
      if (alive) requestAnimationFrame(draw);
      else ctx.clearRect(0, 0, canvas.width, canvas.height);
    };
    requestAnimationFrame(draw);
  }

  const handleSend = async () => {
    setSending(true);
    try {
      const response = await fetch("http://localhost:8080/campaign/send", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(plan),
      });
      if (!response.ok) throw new Error(`Request failed with status ${response.status}`);
      await response.json();

      const flash = document.getElementById("launch-flash");
      if (flash) {
        flash.style.opacity = "1";
        setTimeout(() => (flash.style.opacity = "0"), 300);
      }

      launchConfetti();
      addToast({
        message: `🚀 Campaign launched to ${plan.customerIds.length} customers!`,
        variant: "success",
      });
    } catch (err) {
      addToast({ message: err.message || "Failed to send campaign", variant: "error" });
    } finally {
      setSending(false);
    }
  };

  return (
    <>
      <canvas
        ref={canvasRef}
        id="confetti-canvas"
      />
      <div id="launch-flash" />

      <div className={`card card--preview${mounted ? " card--animate-in" : ""}`}>
        <div className="card-header">
          <div className="card-icon">◈</div>
          <div className="card-header-text">
            <div className="card-title">Campaign Preview</div>
            <div className="card-subtitle">Review before launching</div>
          </div>
        </div>

        <div className="stats-bar">
          <div className="stat-item">
            <div className="stat-value">{recipientCount}</div>
            <div className="stat-label">Recipients</div>
          </div>
          <div className="stat-item">
            <div className="stat-value" style={{ color: "var(--color-accent)" }}>✦ AI</div>
            <div className="stat-label">Generated</div>
          </div>
          <div className="stat-item">
            <div className="stat-value" style={{ color: "var(--color-success)" }}>Ready</div>
            <div className="stat-label">Status</div>
          </div>
        </div>

        <div className="preview-section">
          <p className="preview-label">Subject</p>
          <p className="preview-subject">{plan.subject}</p>
        </div>

        <div className="preview-section">
          <p className="preview-label">Email Body</p>
          <div className="preview-body">{plan.body}</div>
        </div>

        <div className="preview-section">
          <p className="preview-label">Target Customers</p>
          <div className="preview-customer-count">
            <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
              <circle cx="6" cy="4" r="2" fill="currentColor" />
              <path d="M2 10c0-2.2 1.8-4 4-4s4 1.8 4 4" stroke="currentColor" strokeWidth="1.5" fill="none" />
            </svg>
            {plan.customerIds.length} customers selected
          </div>
          <div className="preview-badges">
            {plan.customerIds.map((id, i) => (
              <span
                key={id}
                className="badge"
                style={{ animationDelay: `${i * 55}ms` }}
              >
                {id}
              </span>
            ))}
          </div>
        </div>

        <div className="preview-actions">
          <button className="btn btn-success" onClick={handleSend} disabled={sending}>
            {sending ? (
              <>
                <span className="spinner" style={{ borderTopColor: "#001a0d", borderColor: "rgba(0,26,13,0.2)" }} />
                Sending...
              </>
            ) : (
              <>
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
                  <path d="M2 7L6 11L12 3" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
                </svg>
                Approve &amp; Send
              </>
            )}
          </button>
          <button className="btn btn-danger" onClick={onReject} disabled={sending}>
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M3 3L11 11M11 3L3 11" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
            </svg>
            Reject &amp; Regenerate
          </button>
        </div>
      </div>
    </>
  );
}