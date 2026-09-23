// React Query hooks.
// React Query caches server data by "query key". After a change (mutation) we
// "invalidate" the related keys so the lists refetch and the screen updates by itself.

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "./api";
import { useAppDispatch } from "@/store/hooks";
import { showToast } from "@/store/toastSlice";
import type { ApplicationStatus, DriveStatus, NewCompany, NewDrive, NewStudent, SlotResult } from "./types";

// ---------------- Queries (reading data) ----------------

export function useStats() {
  return useQuery({ queryKey: ["stats"], queryFn: api.getStats });
}

export function useCompanies() {
  return useQuery({ queryKey: ["companies"], queryFn: api.getCompanies });
}

export function useStudents(branch: string) {
  // branch is part of the key, so every filter value is cached separately
  return useQuery({ queryKey: ["students", branch], queryFn: () => api.getStudents(branch) });
}

export function useStudent(id: number) {
  return useQuery({ queryKey: ["student", id], queryFn: () => api.getStudent(id) });
}

export function useStudentApplications(id: number) {
  return useQuery({ queryKey: ["applications", "student", id], queryFn: () => api.getStudentApplications(id) });
}

export function useDrives(status: string) {
  return useQuery({ queryKey: ["drives", status], queryFn: () => api.getDrives(status) });
}

export function useDrive(id: number) {
  return useQuery({ queryKey: ["drive", id], queryFn: () => api.getDrive(id) });
}

export function useEligibility(driveId: number) {
  return useQuery({ queryKey: ["eligibility", driveId], queryFn: () => api.getEligibility(driveId) });
}

export function useDriveApplications(driveId: number) {
  return useQuery({ queryKey: ["applications", "drive", driveId], queryFn: () => api.getDriveApplications(driveId) });
}

export function useRounds(driveId: number) {
  return useQuery({ queryKey: ["rounds", driveId], queryFn: () => api.getRounds(driveId) });
}

export function useSlots(roundId: number) {
  return useQuery({ queryKey: ["slots", roundId], queryFn: () => api.getSlots(roundId) });
}

// ---------------- Mutations (changing data) ----------------

// Small helper so every mutation shows a success/error toast and refreshes the given keys
function useSimpleMutation<TInput>(
  mutationFn: (input: TInput) => Promise<unknown>,
  successMessage: string,
  keysToRefresh: string[]
) {
  const queryClient = useQueryClient();
  const dispatch = useAppDispatch();

  return useMutation({
    mutationFn: mutationFn,
    onSuccess: () => {
      for (const key of keysToRefresh) {
        queryClient.invalidateQueries({ queryKey: [key] });
      }
      dispatch(showToast({ type: "success", message: successMessage }));
    },
    onError: (error: Error) => {
      dispatch(showToast({ type: "error", message: error.message }));
    },
  });
}

export function useCreateCompany() {
  return useSimpleMutation((data: NewCompany) => api.createCompany(data), "Company added", ["companies", "stats"]);
}

export function useDeleteCompany() {
  return useSimpleMutation((id: number) => api.deleteCompany(id), "Company deleted", ["companies", "stats"]);
}

export function useCreateStudent() {
  return useSimpleMutation((data: NewStudent) => api.createStudent(data), "Student added", ["students", "stats"]);
}

export function useCreateDrive() {
  return useSimpleMutation((data: NewDrive) => api.createDrive(data), "Drive created", ["drives", "stats"]);
}

export function useChangeDriveStatus() {
  return useSimpleMutation(
    (input: { id: number; status: DriveStatus }) => api.changeDriveStatus(input.id, input.status),
    "Drive status updated",
    ["drives", "drive", "stats"]
  );
}

export function useApply() {
  return useSimpleMutation(
    (input: { studentId: number; driveId: number }) => api.apply(input.studentId, input.driveId),
    "Application submitted",
    ["applications", "eligibility", "stats"]
  );
}

export function useChangeApplicationStatus() {
  return useSimpleMutation(
    (input: { id: number; status: ApplicationStatus }) => api.changeApplicationStatus(input.id, input.status),
    "Application updated",
    ["applications", "eligibility", "stats"]
  );
}

export function useAddRound() {
  return useSimpleMutation(
    (input: { driveId: number; name: string }) => api.addRound(input.driveId, input.name),
    "Round added",
    ["rounds"]
  );
}

export function useBookSlot() {
  return useSimpleMutation(
    (input: { roundId: number; applicationId: number; startTime: string; endTime: string }) =>
      api.bookSlot(input.roundId, input.applicationId, input.startTime, input.endTime),
    "Interview slot booked",
    ["slots"]
  );
}

export function useSetSlotResult() {
  return useSimpleMutation(
    (input: { slotId: number; result: SlotResult }) => api.setSlotResult(input.slotId, input.result),
    "Result saved",
    ["slots", "applications", "stats"]
  );
}
