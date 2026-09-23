// Global pop-up messages ("Student added", "Not eligible: ...").
// Any component can dispatch showToast(), and the <Toasts /> component displays them.

import { createSlice, PayloadAction } from "@reduxjs/toolkit";

export interface Toast {
  id: number;
  type: "success" | "error";
  message: string;
}

interface ToastState {
  toasts: Toast[];
  nextId: number;
}

const initialState: ToastState = {
  toasts: [],
  nextId: 1,
};

const toastSlice = createSlice({
  name: "toast",
  initialState,
  reducers: {
    showToast(state, action: PayloadAction<{ type: "success" | "error"; message: string }>) {
      // Redux Toolkit lets us "mutate" state here - it uses Immer to make a copy behind the scenes
      state.toasts.push({ id: state.nextId, type: action.payload.type, message: action.payload.message });
      state.nextId = state.nextId + 1;
    },
    removeToast(state, action: PayloadAction<number>) {
      state.toasts = state.toasts.filter((toast) => toast.id !== action.payload);
    },
  },
});

export const { showToast, removeToast } = toastSlice.actions;
export default toastSlice.reducer;
