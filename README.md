# AI Multi-Agent System for Marketing Automation

An autonomous AI system that plans, executes, monitors and optimizes email marketing campaigns with human-in-the-loop approval.

## Tech Stack

Backend
- Java
- Spring Boot
- OpenRouter (LLaMA)

Frontend
- React
- Vite

## Features

- AI campaign planning
- Customer segmentation
- Email generation
- Human approval before sending
- Campaign execution
- Campaign reporting

## Architecture

React UI → Spring Boot Agent → CampaignX APIs → Email Campaign Execution

## How It Works

1. User enters campaign brief
2. AI agent interprets the marketing intent
3. System segments customers based on attributes
4. LLM generates marketing email
5. Human reviews the campaign
6. Campaign is sent through CampaignX API
7. Campaign performance can be analyzed

---

## Human-in-the-Loop

Before sending any campaign, the generated email and target audience are presented to a human for approval via the UI.

---

## Author

AI Multi-Agent Marketing Automation Project