import type { NextConfig } from "next";

// Where the Kotlin backend runs. In docker-compose this is http://backend:8080
const backendUrl = process.env.BACKEND_URL || "http://localhost:8080";

const nextConfig: NextConfig = {
  // makes a small self-contained server for the Docker image
  output: "standalone",

  compiler: {
    styledComponents: true,
  },

  // Forward /api/* requests to the backend, so the browser only talks to one origin
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: backendUrl + "/api/:path*",
      },
    ];
  },
};

export default nextConfig;
