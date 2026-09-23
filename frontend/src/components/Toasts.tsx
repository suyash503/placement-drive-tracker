"use client";

import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { removeToast, Toast } from "@/store/toastSlice";

function ToastItem({ toast }: { toast: Toast }) {
  const dispatch = useAppDispatch();

  // hide the toast automatically after 4 seconds
  useEffect(() => {
    const timer = setTimeout(() => dispatch(removeToast(toast.id)), 4000);
    return () => clearTimeout(timer);
  }, [dispatch, toast.id]);

  const colors = toast.type === "success" ? "bg-green-600" : "bg-red-600";

  return (
    <div className={colors + " flex items-start gap-3 rounded-lg px-4 py-3 text-sm text-white shadow-lg"}>
      <span className="flex-1">{toast.message}</span>
      <button onClick={() => dispatch(removeToast(toast.id))} className="font-bold opacity-80 hover:opacity-100">
        ×
      </button>
    </div>
  );
}

export default function Toasts() {
  const toasts = useAppSelector((state) => state.toast.toasts);

  return (
    <div className="fixed right-4 bottom-4 left-4 z-50 flex flex-col gap-2 sm:left-auto sm:w-96">
      {toasts.map((toast) => (
        <ToastItem key={toast.id} toast={toast} />
      ))}
    </div>
  );
}
