"use client";

// Reusable UI pieces built with styled-components.
// (Page layouts and forms use Tailwind classes; these small components use styled-components,
// mostly to show both approaches - the StatusBadge picks its colours from a prop.)

import styled from "styled-components";

export const Card = styled.div`
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
`;

export const CardTitle = styled.h2`
  font-size: 16px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 12px;
`;

const badgeColors: Record<string, { bg: string; text: string }> = {
  OPEN: { bg: "#dcfce7", text: "#166534" },
  CLOSED: { bg: "#fef3c7", text: "#92400e" },
  COMPLETED: { bg: "#e5e7eb", text: "#374151" },
  APPLIED: { bg: "#dbeafe", text: "#1e40af" },
  SHORTLISTED: { bg: "#ede9fe", text: "#5b21b6" },
  SELECTED: { bg: "#dcfce7", text: "#166534" },
  REJECTED: { bg: "#fee2e2", text: "#991b1b" },
  PENDING: { bg: "#f3f4f6", text: "#374151" },
  PASSED: { bg: "#dcfce7", text: "#166534" },
  FAILED: { bg: "#fee2e2", text: "#991b1b" },
};

// "$status" starts with $ so styled-components doesn't pass it down to the HTML <span>
const Badge = styled.span<{ $status: string }>`
  display: inline-block;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  background: ${(props) => badgeColors[props.$status]?.bg ?? "#f3f4f6"};
  color: ${(props) => badgeColors[props.$status]?.text ?? "#374151"};
`;

export function StatusBadge({ status }: { status: string }) {
  return <Badge $status={status}>{status}</Badge>;
}

const StatBox = styled(Card)`
  display: flex;
  flex-direction: column;
  gap: 4px;
`;

const StatLabel = styled.span`
  font-size: 13px;
  color: #6b7280;
`;

const StatValue = styled.span`
  font-size: 28px;
  font-weight: 700;
  color: #111827;
`;

export function StatCard({ label, value }: { label: string; value: string | number }) {
  return (
    <StatBox>
      <StatLabel>{label}</StatLabel>
      <StatValue>{value}</StatValue>
    </StatBox>
  );
}

// Simple loading / error / empty messages used on every page
export function Loading() {
  return <p className="text-sm text-gray-500">Loading...</p>;
}

export function ErrorMessage({ error }: { error: Error }) {
  return (
    <p className="rounded-md bg-red-50 p-3 text-sm text-red-700">
      Could not load data: {error.message}. Is the backend running on port 8080?
    </p>
  );
}

export function Empty({ text }: { text: string }) {
  return <p className="text-sm text-gray-500">{text}</p>;
}
