"use client";

import Link from "next/link";
import { useState } from "react";
import { useCreateStudent, useStudents } from "@/lib/queries";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { setStudentBranch } from "@/store/filtersSlice";
import { BRANCHES } from "@/lib/types";
import { Card, CardTitle, Empty, ErrorMessage, Loading } from "@/components/ui";

const emptyForm = {
  rollNo: "",
  name: "",
  email: "",
  branch: "CSE",
  cgpa: "",
  graduationYear: "2026",
  activeBacklogs: "0",
};

export default function StudentsPage() {
  // the selected branch filter lives in Redux, so it is kept when you come back to this page
  const branch = useAppSelector((state) => state.filters.studentBranch);
  const dispatch = useAppDispatch();

  const students = useStudents(branch);
  const createStudent = useCreateStudent();

  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);

  // one change handler for all inputs, using the input's "name" attribute
  function handleChange(event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
    setForm({ ...form, [event.target.name]: event.target.value });
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    createStudent.mutate(
      {
        rollNo: form.rollNo,
        name: form.name,
        email: form.email,
        branch: form.branch,
        cgpa: Number(form.cgpa),
        graduationYear: Number(form.graduationYear),
        activeBacklogs: Number(form.activeBacklogs),
      },
      {
        onSuccess: () => {
          setForm(emptyForm);
          setShowForm(false);
        },
      }
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-bold">Students</h1>
        <div className="flex gap-2">
          <select className="input w-auto" value={branch} onChange={(e) => dispatch(setStudentBranch(e.target.value))}>
            <option value="">All branches</option>
            {BRANCHES.map((b) => (
              <option key={b} value={b}>
                {b}
              </option>
            ))}
          </select>
          <button className="btn" onClick={() => setShowForm(!showForm)}>
            {showForm ? "Close" : "Add student"}
          </button>
        </div>
      </div>

      {showForm && (
        <Card>
          <CardTitle>New student</CardTitle>
          <form onSubmit={handleSubmit} className="grid gap-3 sm:grid-cols-2 md:grid-cols-4">
            <div>
              <label className="label">Roll no *</label>
              <input className="input" name="rollNo" value={form.rollNo} onChange={handleChange} required />
            </div>
            <div>
              <label className="label">Name *</label>
              <input className="input" name="name" value={form.name} onChange={handleChange} required />
            </div>
            <div>
              <label className="label">Email *</label>
              <input className="input" type="email" name="email" value={form.email} onChange={handleChange} required />
            </div>
            <div>
              <label className="label">Branch *</label>
              <select className="input" name="branch" value={form.branch} onChange={handleChange}>
                {BRANCHES.map((b) => (
                  <option key={b}>{b}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="label">CGPA *</label>
              <input
                className="input"
                type="number"
                step="0.01"
                min="0"
                max="10"
                name="cgpa"
                value={form.cgpa}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label className="label">Graduation year *</label>
              <input
                className="input"
                type="number"
                name="graduationYear"
                value={form.graduationYear}
                onChange={handleChange}
                required
              />
            </div>
            <div>
              <label className="label">Active backlogs</label>
              <input
                className="input"
                type="number"
                min="0"
                name="activeBacklogs"
                value={form.activeBacklogs}
                onChange={handleChange}
              />
            </div>
            <div className="flex items-end">
              <button className="btn w-full" disabled={createStudent.isPending}>
                {createStudent.isPending ? "Saving..." : "Save student"}
              </button>
            </div>
          </form>
        </Card>
      )}

      <Card>
        {students.isLoading && <Loading />}
        {students.error && <ErrorMessage error={students.error} />}
        {students.data && students.data.length === 0 && <Empty text="No students found." />}
        {students.data && students.data.length > 0 && (
          <div className="overflow-x-auto">
            <table className="table">
              <thead>
                <tr>
                  <th>Roll no</th>
                  <th>Name</th>
                  <th>Branch</th>
                  <th>CGPA</th>
                  <th>Backlogs</th>
                  <th>Batch</th>
                </tr>
              </thead>
              <tbody>
                {students.data.map((student) => (
                  <tr key={student.id} className="hover:bg-gray-50">
                    <td>
                      <Link href={"/students/" + student.id} className="font-medium text-indigo-600 hover:underline">
                        {student.rollNo}
                      </Link>
                    </td>
                    <td>{student.name}</td>
                    <td>{student.branch}</td>
                    <td>{student.cgpa.toFixed(2)}</td>
                    <td>{student.activeBacklogs}</td>
                    <td>{student.graduationYear}</td>
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
