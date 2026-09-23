"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const links = [
  { href: "/", label: "Dashboard" },
  { href: "/drives", label: "Drives" },
  { href: "/students", label: "Students" },
  { href: "/companies", label: "Companies" },
];

export default function Navbar() {
  const pathname = usePathname();

  function isActive(href: string) {
    if (href === "/") {
      return pathname === "/";
    }
    return pathname.startsWith(href);
  }

  return (
    <header className="border-b border-gray-200 bg-white">
      <nav className="mx-auto flex max-w-6xl flex-wrap items-center gap-x-6 gap-y-2 px-4 py-3">
        <Link href="/" className="text-lg font-bold text-indigo-700">
          T&amp;P Tracker
        </Link>
        <div className="flex flex-wrap gap-1">
          {links.map((link) => (
            <Link
              key={link.href}
              href={link.href}
              className={
                "rounded-md px-3 py-1.5 text-sm font-medium " +
                (isActive(link.href) ? "bg-indigo-50 text-indigo-700" : "text-gray-600 hover:bg-gray-100")
              }
            >
              {link.label}
            </Link>
          ))}
        </div>
      </nav>
    </header>
  );
}
