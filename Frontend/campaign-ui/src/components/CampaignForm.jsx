import { useState } from "react";

export default function CampaignForm({ onGenerate }) {

  const [brief, setBrief] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const response = await fetch("http://localhost:8080/campaign/plan", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ brief })
    });

    const data = await response.json();

    onGenerate(data);
  };

  return (
    <div>
      <h2>Create Campaign</h2>

      <form onSubmit={handleSubmit}>
        <textarea
          rows="4"
          placeholder="Enter campaign brief..."
          value={brief}
          onChange={(e) => setBrief(e.target.value)}
        />

        <br />

        <button type="submit">Generate Campaign</button>
      </form>
    </div>
  );
}