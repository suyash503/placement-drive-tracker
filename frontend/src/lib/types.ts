// These types match the JSON returned by the Kotlin backend (see backend/.../dto/Responses.kt)

export type DriveStatus = "OPEN" | "CLOSED" | "COMPLETED";
export type ApplicationStatus = "APPLIED" | "SHORTLISTED" | "SELECTED" | "REJECTED";
export type SlotResult = "PENDING" | "PASSED" | "FAILED";

export const BRANCHES = ["CSE", "IT", "ECE", "EE", "ME", "CE"];

export interface Company {
  id: number;
  name: string;
  industry: string | null;
  website: string | null;
}

export interface Student {
  id: number;
  rollNo: string;
  name: string;
  email: string;
  branch: string;
  cgpa: number;
  graduationYear: number;
  activeBacklogs: number;
}

export interface Drive {
  id: number;
  companyId: number;
  companyName: string;
  jobRole: string;
  ctcLpa: number;
  driveDate: string; // "2026-10-05"
  minCgpa: number;
  maxBacklogs: number;
  allowedBranches: string[];
  status: DriveStatus;
}

export interface Application {
  id: number;
  studentId: number;
  studentName: string;
  rollNo: string;
  branch: string;
  cgpa: number;
  driveId: number;
  companyName: string;
  jobRole: string;
  status: ApplicationStatus;
  appliedAt: string;
}

export interface Round {
  id: number;
  driveId: number;
  roundNumber: number;
  name: string;
}

export interface Slot {
  id: number;
  roundId: number;
  roundName: string;
  applicationId: number;
  studentName: string;
  rollNo: string;
  startTime: string; // "2026-10-05T10:00:00"
  endTime: string;
  result: SlotResult;
}

export interface Eligibility {
  student: Student;
  eligible: boolean;
  alreadyApplied: boolean;
  reasons: string[];
}

export interface DashboardStats {
  totalCompanies: number;
  totalStudents: number;
  openDrives: number;
  totalApplications: number;
  placedStudents: number;
  placementPercentage: number;
  applicationsByStatus: Record<string, number>;
  branchWise: { branch: string; totalStudents: number; placedStudents: number }[];
}

// Request bodies
export interface NewCompany {
  name: string;
  industry: string;
  website: string;
}

export interface NewStudent {
  rollNo: string;
  name: string;
  email: string;
  branch: string;
  cgpa: number;
  graduationYear: number;
  activeBacklogs: number;
}

export interface NewDrive {
  companyId: number;
  jobRole: string;
  ctcLpa: number;
  driveDate: string;
  minCgpa: number;
  maxBacklogs: number;
  allowedBranches: string[];
}
