"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useStudent, useStudentApplications } from "@/lib/queries";
import { Card, CardTitle, Empty, ErrorMessage, Loading, StatusBadge } from "@/components/ui";

export default function StudentDetailPage() {
  const params = useParams();
  const studentId = Number(params.id);

  const student = useStudent(studentId);
  const applications = useStudentApplications(studentId);

  if (student.isLoading) return <Loading />;
  if (student.error) return <ErrorMessage error={student.error} />;
  if (!student.data) return null;

  const s = student.data;

  return (
    <div className="space-y-6">
      <Link href="/students" className="text-sm text-indigo-600 hover:underline">
        ← All students
      </Link>

      <Card>
        <h1 className="text-2xl font-bold">{s.name}</h1>
        <p className="text-gray-500">
          {s.rollNo} · {s.email}
        </p>
        <div className="mt-4 grid grid-cols-2 gap-4 text-sm sm:grid-cols-4">
          <Info label="Branch" value={s.branch} />
          <Info label="CGPA" value={s.cgpa.toFixed(2)} />
          <Info label="Active backlogs" value={String(s.activeBacklogs)} />
          <Info label="Batch" value={String(s.graduationYear)} />
        </div>
      </Card>

      <Card>
        <CardTitle>Applications</CardTitle>
        {applications.isLoading && <Loading />}
        {applications.data && applications.data.length === 0 && (
          <Empty text="Not applied anywhere yet. Open a drive to apply on behalf of this student." />
        )}
        {applications.data && applications.data.length > 0 && (
          <div className="overflow-x-auto">
            <table className="table">
              <thead>
                <tr>
                  <th>Company</th>
                  <th>Role</th>
                  <th>Applied on</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {applications.data.map((application) => (
                  <tr key={application.id}>
                    <td>
                      <Link href={"/drives/" + application.driveId} className="text-indigo-600 hover:underline">
                        {application.companyName}
                      </Link>
                    </td>
                    <td>{application.jobRole}</td>
                    <td>{application.appliedAt.slice(0, 10)}</td>
                    <td>
                      <StatusBadge status={application.status} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
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
