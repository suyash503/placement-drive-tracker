import type { Metadata } from "next";
import "./globals.css";
import Providers from "@/components/Providers";
import Navbar from "@/components/Navbar";
import Toasts from "@/components/Toasts";

export const metadata: Metadata = {
  title: "Placement Drive Tracker",
  description: "Track companies, placement drives, applications and interviews",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>
        <Providers>
          <Navbar />
          <main className="mx-auto max-w-6xl px-4 py-6">{children}</main>
          <Toasts />
        </Providers>
      </body>
    </html>
  );
}
