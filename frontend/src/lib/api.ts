// All calls to the backend go through this file.
// We call "/api/..." on our own Next.js server, and next.config.ts forwards it to the
// Kotlin backend. In Kubernetes the Ingress does the same job. So there are no CORS issues.

import type {
  Application,
  ApplicationStatus,
  Company,
  DashboardStats,
  Drive,
  DriveStatus,
  Eligibility,
  NewCompany,
  NewDrive,
  NewStudent,
  Round,
  Slot,
  SlotResult,
  Student,
} from "./types";

async function request<T>(url: string, method = "GET", body?: unknown): Promise<T> {
  const response = await fetch(url, {
    method: method,
    headers: { "Content-Type": "application/json" },
    body: body ? JSON.stringify(body) : undefined,
  });

  if (!response.ok) {
    // our backend sends errors like {"status": 400, "message": "..."}
    let message = "Something went wrong (" + response.status + ")";
    try {
      const error = await response.json();
      if (error.message) {
        message = error.message;
      } else if (error._embedded?.errors?.[0]?.message) {
        // format of Micronaut's built-in validation errors
        message = error._embedded.errors[0].message;
      }
    } catch {
      // response had no JSON body, keep the default message
    }
    throw new Error(message);
  }

  // 204 No Content (e.g. after DELETE) has no body
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json();
}

export const api = {
  // dashboard
  getStats: () => request<DashboardStats>("/api/dashboard/stats"),

  // companies
  getCompanies: () => request<Company[]>("/api/companies"),
  createCompany: (data: NewCompany) => request<Company>("/api/companies", "POST", data),
  deleteCompany: (id: number) => request<void>("/api/companies/" + id, "DELETE"),

  // students
  getStudents: (branch: string) => {
    const query = branch ? "?branch=" + branch : "";
    return request<Student[]>("/api/students" + query);
  },
  getStudent: (id: number) => request<Student>("/api/students/" + id),
  createStudent: (data: NewStudent) => request<Student>("/api/students", "POST", data),
  getStudentApplications: (id: number) => request<Application[]>("/api/students/" + id + "/applications"),

  // drives
  getDrives: (status: string) => {
    const query = status ? "?status=" + status : "";
    return request<Drive[]>("/api/drives" + query);
  },
  getDrive: (id: number) => request<Drive>("/api/drives/" + id),
  createDrive: (data: NewDrive) => request<Drive>("/api/drives", "POST", data),
  changeDriveStatus: (id: number, status: DriveStatus) =>
    request<Drive>("/api/drives/" + id + "/status", "PUT", { status }),
  getEligibility: (driveId: number) => request<Eligibility[]>("/api/drives/" + driveId + "/eligibility"),
  getDriveApplications: (driveId: number) => request<Application[]>("/api/drives/" + driveId + "/applications"),

  // applications
  apply: (studentId: number, driveId: number) =>
    request<Application>("/api/applications", "POST", { studentId, driveId }),
  changeApplicationStatus: (id: number, status: ApplicationStatus) =>
    request<Application>("/api/applications/" + id + "/status", "PUT", { status }),

  // rounds and interview slots
  getRounds: (driveId: number) => request<Round[]>("/api/drives/" + driveId + "/rounds"),
  addRound: (driveId: number, name: string) => request<Round>("/api/drives/" + driveId + "/rounds", "POST", { name }),
  getSlots: (roundId: number) => request<Slot[]>("/api/rounds/" + roundId + "/slots"),
  bookSlot: (roundId: number, applicationId: number, startTime: string, endTime: string) =>
    request<Slot>("/api/rounds/" + roundId + "/slots", "POST", { applicationId, startTime, endTime }),
  setSlotResult: (slotId: number, result: SlotResult) =>
    request<Slot>("/api/slots/" + slotId + "/result", "PUT", { result }),
};
