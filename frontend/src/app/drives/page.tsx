"use client";

import Link from "next/link";
import { useState } from "react";
import { useCompanies, useCreateDrive, useDrives } from "@/lib/queries";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { setDriveStatus } from "@/store/filtersSlice";
import { BRANCHES } from "@/lib/types";
import { Card, CardTitle, Empty, ErrorMessage, Loading, StatusBadge } from "@/components/ui";

export default function DrivesPage() {
  const status = useAppSelector((state) => state.filters.driveStatus);
  const dispatch = useAppDispatch();
  const drives = useDrives(status);
  const [showForm, setShowForm] = useState(false);

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold">Placement Drives</h1>
        <div className="flex gap-2">
          <select className="input w-auto" value={status} onChange={(e) => dispatch(setDriveStatus(e.target.value))}>
            <option value="">All statuses</option>
            <option value="OPEN">Open</option>
            <option value="CLOSED">Closed</option>
            <option value="COMPLETED">Completed</option>
          </select>
          <button className="btn" onClick={() => setShowForm(!showForm)}>
            {showForm ? "Close" : "New drive"}
          </button>
        </div>
      </div>

      {showForm && <NewDriveForm onDone={() => setShowForm(false)} />}

      {drives.isLoading && <Loading />}
      {drives.error && <ErrorMessage error={drives.error} />}
      {drives.data && drives.data.length === 0 && <Empty text="No drives found." />}

      <div className="grid gap-4 md:grid-cols-2">
        {drives.data?.map((drive) => (
          <Link key={drive.id} href={"/drives/" + drive.id}>
            <Card className="h-full transition hover:border-indigo-300 hover:shadow-sm">
              <div className="flex items-start justify-between gap-2">
                <div>
                  <p className="text-lg font-semibold">{drive.companyName}</p>
                  <p className="text-gray-600">{drive.jobRole}</p>
                </div>
                <StatusBadge status={drive.status} />
              </div>
              <div className="mt-3 flex flex-wrap gap-x-4 gap-y-1 text-sm text-gray-500">
                <span>📅 {drive.driveDate}</span>
                <span>💰 {drive.ctcLpa} LPA</span>
                <span>🎓 CGPA ≥ {drive.minCgpa}</span>
                <span>Backlogs ≤ {drive.maxBacklogs}</span>
              </div>
              <p className="mt-2 text-xs text-gray-500">Branches: {drive.allowedBranches.join(", ") || "All"}</p>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}

function NewDriveForm({ onDone }: { onDone: () => void }) {
  const companies = useCompanies();
  const createDrive = useCreateDrive();

  const [companyId, setCompanyId] = useState("");
  const [jobRole, setJobRole] = useState("");
  const [ctcLpa, setCtcLpa] = useState("");
  const [driveDate, setDriveDate] = useState("");
  const [minCgpa, setMinCgpa] = useState("7");
  const [maxBacklogs, setMaxBacklogs] = useState("0");
  const [branches, setBranches] = useState<string[]>(["CSE", "IT"]);

  function toggleBranch(branch: string) {
    if (branches.includes(branch)) {
      setBranches(branches.filter((b) => b !== branch));
    } else {
      setBranches([...branches, branch]);
    }
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    createDrive.mutate(
      {
        companyId: Number(companyId),
        jobRole: jobRole,
        ctcLpa: Number(ctcLpa),
        driveDate: driveDate,
        minCgpa: Number(minCgpa),
        maxBacklogs: Number(maxBacklogs),
        allowedBranches: branches,
      },
      { onSuccess: onDone }
    );
  }

  return (
    <Card>
      <CardTitle>New placement drive</CardTitle>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid gap-3 sm:grid-cols-2 md:grid-cols-3">
          <div>
            <label className="label">Company *</label>
            <select className="input" value={companyId} onChange={(e) => setCompanyId(e.target.value)} required>
              <option value="">Select company</option>
              {companies.data?.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.name}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="label">Job role *</label>
            <input className="input" value={jobRole} onChange={(e) => setJobRole(e.target.value)} required />
          </div>
          <div>
            <label className="label">CTC (LPA) *</label>
            <input
              className="input"
              type="number"
              step="0.1"
              min="0.1"
              value={ctcLpa}
              onChange={(e) => setCtcLpa(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="label">Drive date *</label>
            <input
              className="input"
              type="date"
              value={driveDate}
              onChange={(e) => setDriveDate(e.target.value)}
              required
            />
          </div>
          <div>
            <label className="label">Minimum CGPA</label>
            <input
              className="input"
              type="number"
              step="0.1"
              min="0"
              max="10"
              value={minCgpa}
              onChange={(e) => setMinCgpa(e.target.value)}
            />
          </div>
          <div>
            <label className="label">Max active backlogs</label>
            <input
              className="input"
              type="number"
              min="0"
              value={maxBacklogs}
              onChange={(e) => setMaxBacklogs(e.target.value)}
            />
          </div>
        </div>

        <div>
          <label className="label">Allowed branches (none selected = all branches)</label>
          <div className="flex flex-wrap gap-2">
            {BRANCHES.map((branch) => (
              <button
                type="button"
                key={branch}
                onClick={() => toggleBranch(branch)}
                className={
                  "rounded-full border px-3 py-1 text-sm " +
                  (branches.includes(branch)
                    ? "border-indigo-600 bg-indigo-600 text-white"
                    : "border-gray-300 bg-white text-gray-700")
                }
              >
                {branch}
              </button>
            ))}
          </div>
        </div>

        <button className="btn" disabled={createDrive.isPending}>
          {createDrive.isPending ? "Creating..." : "Create drive"}
        </button>
      </form>
    </Card>
  );
}
