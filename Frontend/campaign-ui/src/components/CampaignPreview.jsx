export default function CampaignPreview({ plan, onSend }) {

  const handleSend = async () => {

    const response = await fetch("http://localhost:8080/campaign/send", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(plan)
    });

    const data = await response.json();

        alert(
    "Campaign Sent Successfully!\n\nCampaign ID: " +
    data.campaign_id
    );
  };

  return (
    <div>

      <h2>Campaign Preview (Human Approval)</h2>

      <h3>Subject</h3>
      <p>{plan.subject}</p>

      <h3>Email Body</h3>
      <p>{plan.body}</p>

      <h3>Target Customers</h3>
      <p>{plan.customerIds.length} customers selected</p>

      <ul>
        {plan.customerIds.map((id) => (
            <li key={id}>{id}</li>
        ))}
        </ul>

      <button onClick={handleSend}>Approve & Send</button>

      <button onClick={() => window.location.reload()}>
      Reject & Regenerate
        </button>

    </div>
  );
}