// Redux Toolkit slice for UI filters.
// React Query holds data that comes FROM the server.
// Redux holds state that only lives in the browser - like which filter is selected.
// Keeping filters in Redux means they are remembered when you move between pages.

import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface FiltersState {
  studentBranch: string; // "" means all branches
  driveStatus: string; // "" means all statuses
}

const initialState: FiltersState = {
  studentBranch: "",
  driveStatus: "",
};

const filtersSlice = createSlice({
  name: "filters",
  initialState,
  reducers: {
    setStudentBranch(state, action: PayloadAction<string>) {
      state.studentBranch = action.payload;
    },
    setDriveStatus(state, action: PayloadAction<string>) {
      state.driveStatus = action.payload;
    },
  },
});

export const { setStudentBranch, setDriveStatus } = filtersSlice.actions;
export default filtersSlice.reducer;
