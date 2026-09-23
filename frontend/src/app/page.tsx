"use client";

import Link from "next/link";
import { useDrives, useStats } from "@/lib/queries";
import { Card, CardTitle, Empty, ErrorMessage, Loading, StatCard, StatusBadge } from "@/components/ui";

export default function DashboardPage() {
  const stats = useStats();
  const openDrives = useDrives("OPEN");

  if (stats.isLoading) return <Loading />;
  if (stats.error) return <ErrorMessage error={stats.error} />;
  if (!stats.data) return null;

  const data = stats.data;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Placement Dashboard</h1>

      <div className="grid grid-cols-2 gap-4 md:grid-cols-4">
        <StatCard label="Students" value={data.totalStudents} />
        <StatCard label="Placed" value={data.placedStudents} />
        <StatCard label="Placement %" value={data.placementPercentage + "%"} />
        <StatCard label="Open drives" value={data.openDrives} />
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardTitle>Branch-wise placement</CardTitle>
          {data.branchWise.length === 0 && <Empty text="No students yet." />}
          <div className="space-y-3">
            {data.branchWise.map((row) => {
              const percent = row.totalStudents === 0 ? 0 : Math.round((row.placedStudents * 100) / row.totalStudents);
              return (
                <div key={row.branch}>
                  <div className="mb-1 flex justify-between text-sm">
                    <span className="font-medium">{row.branch}</span>
                    <span className="text-gray-500">
                      {row.placedStudents} / {row.totalStudents} placed
                    </span>
                  </div>
                  <div className="h-2 rounded-full bg-gray-100">
                    <div className="h-2 rounded-full bg-indigo-500" style={{ width: percent + "%" }} />
                  </div>
                </div>
              );
            })}
          </div>
        </Card>

        <Card>
          <CardTitle>Applications by status</CardTitle>
          <p className="mb-3 text-sm text-gray-500">{data.totalApplications} applications in total</p>
          <div className="space-y-2">
            {Object.entries(data.applicationsByStatus).map(([status, count]) => (
              <div key={status} className="flex items-center justify-between">
                <StatusBadge status={status} />
                <span className="font-semibold">{count}</span>
              </div>
            ))}
          </div>
        </Card>
      </div>

      <Card>
        <CardTitle>Open drives</CardTitle>
        {openDrives.isLoading && <Loading />}
        {openDrives.data && openDrives.data.length === 0 && <Empty text="No open drives right now." />}
        <div className="divide-y divide-gray-100">
          {openDrives.data?.map((drive) => (
            <Link
              key={drive.id}
              href={"/drives/" + drive.id}
              className="flex flex-wrap items-center justify-between gap-2 py-3 hover:bg-gray-50"
            >
              <div>
                <p className="font-medium">
                  {drive.companyName} — {drive.jobRole}
                </p>
                <p className="text-xs text-gray-500">
                  {drive.driveDate} · {drive.ctcLpa} LPA · min CGPA {drive.minCgpa}
                </p>
              </div>
              <StatusBadge status={drive.status} />
            </Link>
          ))}
        </div>
      </Card>
    </div>
  );
}
