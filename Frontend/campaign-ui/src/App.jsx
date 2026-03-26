import { useState } from "react";
import CampaignForm from "./components/CampaignForm";
import CampaignPreview from "./components/CampaignPreview";
import ToastContainer from "./components/ToastContainer";
import { useToast } from "./hooks/useToast";
import "./App.css";

function App() {
  const [campaignPlan, setCampaignPlan] = useState(null);
  const { toasts, addToast, removeToast } = useToast();

  function handleReject() {
    setCampaignPlan(null);
  }

  return (
    <div className="app-shell">

      {/* Animated background layers */}
      <div className="bg-orb bg-orb-1" />
      <div className="bg-orb bg-orb-2" />
      <div className="bg-orb bg-orb-3" />
      <div className="bg-orb bg-orb-4" />
      <div className="bg-scanline" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />
      <div className="bg-particle" />

      <header className="app-header">
        <div className="logo">
          <div className="logo-mark">AI</div>
          <div className="logo-text">Campaign<span>Agent</span></div>
        </div>
        <div className="header-badge">● Live</div>
      </header>

      <main className="app-main">
        <div className="app-content">
          <div className="hero">
            <div className="hero-eyebrow">AI-Powered Marketing</div>
            <h1 className="hero-title">
              <span className="hero-line1">Launch campaigns that</span>
              <span className="hero-line2"><em>actually convert.</em></span>
            </h1>
          </div>

          {!campaignPlan && (
            <CampaignForm onGenerate={setCampaignPlan} addToast={addToast} />
          )}
          {campaignPlan && (
            <CampaignPreview
              plan={campaignPlan}
              onReject={handleReject}
              addToast={addToast}
            />
          )}
        </div>
      </main>

      <footer className="app-footer">
        AI Campaign Agent — Powered by AI ✦
      </footer>

      <ToastContainer toasts={toasts} onDismiss={removeToast} />
    </div>
  );
}

export default App;