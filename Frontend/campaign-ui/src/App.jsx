import { useState } from "react";
import CampaignForm from "./components/CampaignForm";
import CampaignPreview from "./components/CampaignPreview";

function App() {

  const [campaignPlan, setCampaignPlan] = useState(null);

  return (
    <div style={{ padding: "30px" }}>

      <h1>AI Campaign Agent</h1>

      {!campaignPlan && (
        <CampaignForm onGenerate={setCampaignPlan} />
      )}

      {campaignPlan && (
        <CampaignPreview plan={campaignPlan} />
      )}

    </div>
  );
}

export default App;