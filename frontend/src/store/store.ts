import { configureStore } from "@reduxjs/toolkit";
import filtersReducer from "./filtersSlice";
import toastReducer from "./toastSlice";

export function makeStore() {
  return configureStore({
    reducer: {
      filters: filtersReducer,
      toast: toastReducer,
    },
  });
}

export type AppStore = ReturnType<typeof makeStore>;
export type RootState = ReturnType<AppStore["getState"]>;
export type AppDispatch = AppStore["dispatch"];
