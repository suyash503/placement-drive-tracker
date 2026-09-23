"use client";

import { useState } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Provider as ReduxProvider } from "react-redux";
import { makeStore } from "@/store/store";
import StyledRegistry from "./StyledRegistry";

// Wraps the whole app with: styled-components registry, Redux store and React Query client
export default function Providers({ children }: { children: React.ReactNode }) {
  // useState makes sure we create these only once (not on every render)
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 30 * 1000, // data is "fresh" for 30 seconds
            retry: 1,
          },
        },
      })
  );
  const [store] = useState(() => makeStore());

  return (
    <StyledRegistry>
      <ReduxProvider store={store}>
        <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
      </ReduxProvider>
    </StyledRegistry>
  );
}
