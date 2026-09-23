"use client";

import Link from "next/link";
import { useState } from "react";
import { useParams } from "next/navigation";
import {
  useAddRound,
  useApply,
  useBookSlot,
  useChangeApplicationStatus,
  useChangeDriveStatus,
  useDrive,
  useDriveApplications,
  useEligibility,
  useRounds,
  useSetSlotResult,
  useSlots,
} from "@/lib/queries";
import type { Application, Drive, Round } from "@/lib/types";
import { Card, CardTitle, Empty, ErrorMessage, Loading, StatusBadge } from "@/components/ui";

type Tab = "applications" | "eligibility" | "interviews";

export default function DriveDetailPage() {
  const params = useParams();
  const driveId = Number(params.id);

  const drive = useDrive(driveId);
  const [tab, setTab] = useState<Tab>("applications");

  if (drive.isLoading) return <Loading />;
  if (drive.error) return <ErrorMessage error={drive.error} />;
  if (!drive.data) return null;

  return (
    <div className="space-y-6">
      <Link href="/drives" className="text-sm text-indigo-600 hover:underline">
        ← All drives
      </Link>

      <DriveHeader drive={drive.data} />

      <div className="flex gap-1 border-b border-gray-200">
        <TabButton label="Applications" active={tab === "applications"} onClick={() => setTab("applications")} />
        <TabButton label="Eligibility" active={tab === "eligibility"} onClick={() => setTab("eligibility")} />
        <TabButton label="Interviews" active={tab === "interviews"} onClick={() => setTab("interviews")} />
      </div>

      {tab === "applications" && <ApplicationsTab driveId={driveId} />}
      {tab === "eligibility" && <EligibilityTab drive={drive.data} />}
      {tab === "interviews" && <InterviewsTab driveId={driveId} />}
    </div>
  );
}

function TabButton({ label, active, onClick }: { label: string; active: boolean; onClick: () => void }) {
  return (
    <button
      onClick={onClick}
      className={
        "-mb-px border-b-2 px-4 py-2 text-sm font-medium " +
        (active ? "border-indigo-600 text-indigo-700" : "border-transparent text-gray-500 hover:text-gray-700")
      }
    >
      {label}
    </button>
  );
}

// ------------------------------------------------------------------
// Header: drive details + buttons to close / complete the drive
// ------------------------------------------------------------------
function DriveHeader({ drive }: { drive: Drive }) {
  const changeStatus = useChangeDriveStatus();

  return (
    <Card>
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold">{drive.companyName}</h1>
          <p className="text-gray-600">{drive.jobRole}</p>
        </div>
        <StatusBadge status={drive.status} />
      </div>

      <div className="mt-4 grid grid-cols-2 gap-4 text-sm md:grid-cols-5">
        <Info label="Drive date" value={drive.driveDate} />
        <Info label="CTC" value={drive.ctcLpa + " LPA"} />
        <Info label="Min CGPA" value={String(drive.minCgpa)} />
        <Info label="Max backlogs" value={String(drive.maxBacklogs)} />
        <Info label="Branches" value={drive.allowedBranches.join(", ") || "All"} />
      </div>

      <div className="mt-4 flex flex-wrap gap-2">
        {drive.status === "OPEN" && (
          <button
            className="btn-secondary"
            disabled={changeStatus.isPending}
            onClick={() => changeStatus.mutate({ id: drive.id, status: "CLOSED" })}
          >
            Close applications
          </button>
        )}
        {drive.status === "CLOSED" && (
          <>
            <button
              className="btn-secondary"
              disabled={changeStatus.isPending}
              onClick={() => changeStatus.mutate({ id: drive.id, status: "OPEN" })}
            >
              Re-open applications
            </button>
            <button
              className="btn-secondary"
              disabled={changeStatus.isPending}
              onClick={() => changeStatus.mutate({ id: drive.id, status: "COMPLETED" })}
            >
              Mark as completed
            </button>
          </>
        )}
      </div>
    </Card>
  );
}

function Info({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <p className="text-xs text-gray-500">{label}</p>
      <p className="font-semibold">{value}</p>
    </div>
  );
}

// ------------------------------------------------------------------
// Tab 1: everyone who applied, with buttons to move them forward
// ------------------------------------------------------------------
function ApplicationsTab({ driveId }: { driveId: number }) {
  const applications = useDriveApplications(driveId);
  const changeStatus = useChangeApplicationStatus();

  if (applications.isLoading) return <Loading />;
  if (applications.error) return <ErrorMessage error={applications.error} />;
  if (!applications.data || applications.data.length === 0) {
    return <Empty text="No applications yet. Go to the Eligibility tab to apply for students." />;
  }

  return (
    <Card>
      <div className="overflow-x-auto">
        <table className="table">
          <thead>
            <tr>
              <th>Roll no</th>
              <th>Name</th>
              <th>Branch</th>
              <th>CGPA</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {applications.data.map((application) => (
              <tr key={application.id}>
                <td>{application.rollNo}</td>
                <td>
                  <Link href={"/students/" + application.studentId} className="text-indigo-600 hover:underline">
                    {application.studentName}
                  </Link>
                </td>
                <td>{application.branch}</td>
                <td>{application.cgpa.toFixed(2)}</td>
                <td>
                  <StatusBadge status={application.status} />
                </td>
                <td className="space-x-2 whitespace-nowrap">
                  {application.status === "APPLIED" && (
                    <button
                      className="btn-secondary"
                      disabled={changeStatus.isPending}
                      onClick={() => changeStatus.mutate({ id: application.id, status: "SHORTLISTED" })}
                    >
                      Shortlist
                    </button>
                  )}
                  {application.status === "SHORTLISTED" && (
                    <button
                      className="btn-secondary"
                      disabled={changeStatus.isPending}
                      onClick={() => changeStatus.mutate({ id: application.id, status: "SELECTED" })}
                    >
                      Select
                    </button>
                  )}
                  {(application.status === "APPLIED" || application.status === "SHORTLISTED") && (
                    <button
                      className="btn-secondary"
                      disabled={changeStatus.isPending}
                      onClick={() => changeStatus.mutate({ id: application.id, status: "REJECTED" })}
                    >
                      Reject
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  );
}

// ------------------------------------------------------------------
// Tab 2: every student, whether they are eligible, and an Apply button
// ------------------------------------------------------------------
function EligibilityTab({ drive }: { drive: Drive }) {
  const eligibility = useEligibility(drive.id);
  const apply = useApply();
  const [showOnlyEligible, setShowOnlyEligible] = useState(false);

  if (eligibility.isLoading) return <Loading />;
  if (eligibility.error) return <ErrorMessage error={eligibility.error} />;
  if (!eligibility.data) return null;

  let rows = eligibility.data;
  if (showOnlyEligible) {
    rows = rows.filter((row) => row.eligible);
  }
  const eligibleCount = eligibility.data.filter((row) => row.eligible).length;

  return (
    <Card>
      <div className="mb-3 flex flex-wrap items-center justify-between gap-2">
        <p className="text-sm text-gray-600">
          <b>{eligibleCount}</b> of {eligibility.data.length} students are eligible
        </p>
        <label className="flex items-center gap-2 text-sm">
          <input type="checkbox" checked={showOnlyEligible} onChange={(e) => setShowOnlyEligible(e.target.checked)} />
          Show only eligible
        </label>
      </div>

      <div className="overflow-x-auto">
        <table className="table">
          <thead>
            <tr>
              <th>Student</th>
              <th>Branch</th>
              <th>CGPA</th>
              <th>Result</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.student.id}>
                <td>
                  <p className="font-medium">{row.student.name}</p>
                  <p className="text-xs text-gray-500">{row.student.rollNo}</p>
                </td>
                <td>{row.student.branch}</td>
                <td>{row.student.cgpa.toFixed(2)}</td>
                <td>
                  {row.eligible ? (
                    <span className="text-green-700">✓ Eligible</span>
                  ) : (
                    <ul className="text-xs text-red-700">
                      {row.reasons.map((reason) => (
                        <li key={reason}>✗ {reason}</li>
                      ))}
                    </ul>
                  )}
                </td>
                <td className="text-right">
                  {row.alreadyApplied && <span className="text-xs text-gray-500">Applied</span>}
                  {row.eligible && !row.alreadyApplied && drive.status === "OPEN" && (
                    <button
                      className="btn-secondary"
                      disabled={apply.isPending}
                      onClick={() => apply.mutate({ studentId: row.student.id, driveId: drive.id })}
                    >
                      Apply
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  );
}

// ------------------------------------------------------------------
// Tab 3: interview rounds, and slots inside each round
// ------------------------------------------------------------------
function InterviewsTab({ driveId }: { driveId: number }) {
  const rounds = useRounds(driveId);
  const applications = useDriveApplications(driveId);
  const addRound = useAddRound();
  const [roundName, setRoundName] = useState("");

  function handleAddRound(event: React.FormEvent) {
    event.preventDefault();
    addRound.mutate({ driveId, name: roundName }, { onSuccess: () => setRoundName("") });
  }

  // only shortlisted students can be given interview slots
  const shortlisted = (applications.data ?? []).filter((a) => a.status === "SHORTLISTED");

  return (
    <div className="space-y-4">
      <Card>
        <form onSubmit={handleAddRound} className="flex flex-wrap items-end gap-3">
          <div className="min-w-48 flex-1">
            <label className="label">New round name</label>
            <input
              className="input"
              placeholder="e.g. Online Assessment, Technical Interview, HR"
              value={roundName}
              onChange={(e) => setRoundName(e.target.value)}
              required
            />
          </div>
          <button className="btn" disabled={addRound.isPending}>
            Add round
          </button>
        </form>
      </Card>

      {rounds.isLoading && <Loading />}
      {rounds.data && rounds.data.length === 0 && <Empty text="No rounds yet. Add the first round above." />}
      {rounds.data?.map((round) => (
        <RoundCard key={round.id} round={round} shortlisted={shortlisted} />
      ))}
    </div>
  );
}

function RoundCard({ round, shortlisted }: { round: Round; shortlisted: Application[] }) {
  const slots = useSlots(round.id);
  const bookSlot = useBookSlot();
  const setResult = useSetSlotResult();

  const [applicationId, setApplicationId] = useState("");
  const [startTime, setStartTime] = useState("");
  const [endTime, setEndTime] = useState("");

  // students who don't have a slot in this round yet
  const bookedIds = (slots.data ?? []).map((slot) => slot.applicationId);
  const available = shortlisted.filter((a) => !bookedIds.includes(a.id));

  function handleBook(event: React.FormEvent) {
    event.preventDefault();
    bookSlot.mutate(
      { roundId: round.id, applicationId: Number(applicationId), startTime, endTime },
      {
        onSuccess: () => {
          setApplicationId("");
        },
      }
    );
  }

  return (
    <Card>
      <CardTitle>
        Round {round.roundNumber}: {round.name}
      </CardTitle>

      {slots.data && slots.data.length === 0 && <Empty text="No slots booked in this round." />}
      {slots.data && slots.data.length > 0 && (
        <div className="mb-4 overflow-x-auto">
          <table className="table">
            <thead>
              <tr>
                <th>Student</th>
                <th>Time</th>
                <th>Result</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {slots.data.map((slot) => (
                <tr key={slot.id}>
                  <td>
                    {slot.studentName} <span className="text-xs text-gray-500">({slot.rollNo})</span>
                  </td>
                  <td className="whitespace-nowrap">
                    {slot.startTime.replace("T", " ").slice(0, 16)} – {slot.endTime.slice(11, 16)}
                  </td>
                  <td>
                    <StatusBadge status={slot.result} />
                  </td>
                  <td className="space-x-2 text-right whitespace-nowrap">
                    {slot.result === "PENDING" && (
                      <>
                        <button
                          className="btn-secondary"
                          disabled={setResult.isPending}
                          onClick={() => setResult.mutate({ slotId: slot.id, result: "PASSED" })}
                        >
                          Pass
                        </button>
                        <button
                          className="btn-secondary"
                          disabled={setResult.isPending}
                          onClick={() => setResult.mutate({ slotId: slot.id, result: "FAILED" })}
                        >
                          Fail
                        </button>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {available.length > 0 ? (
        <form onSubmit={handleBook} className="grid gap-3 border-t border-gray-100 pt-4 sm:grid-cols-4 sm:items-end">
          <div>
            <label className="label">Shortlisted student</label>
            <select className="input" value={applicationId} onChange={(e) => setApplicationId(e.target.value)} required>
              <option value="">Select</option>
              {available.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.studentName} ({a.rollNo})
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Start</label>
            <input
              className="input"
              type="datetime-local"
              value={startTime}
              onChange={(e) => setStartTime(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="label">End</label>
            <input
              className="input"
              type="datetime-local"
              value={endTime}
              onChange={(e) => setEndTime(e.target.value)}
              required
            />
          </div>
          <button className="btn" disabled={bookSlot.isPending}>
            Book slot
          </button>
        </form>
      ) : (
        <p className="border-t border-gray-100 pt-3 text-xs text-gray-500">
          Shortlist students in the Applications tab to book their interview slots.
        </p>
      )}
    </Card>
  );
}
