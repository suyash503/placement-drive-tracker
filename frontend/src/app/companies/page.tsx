"use client";

import { useState } from "react";
import { useCompanies, useCreateCompany, useDeleteCompany } from "@/lib/queries";
import { Card, CardTitle, Empty, ErrorMessage, Loading } from "@/components/ui";

export default function CompaniesPage() {
  const companies = useCompanies();
  const createCompany = useCreateCompany();
  const deleteCompany = useDeleteCompany();

  const [name, setName] = useState("");
  const [industry, setIndustry] = useState("");
  const [website, setWebsite] = useState("");

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    createCompany.mutate(
      { name, industry, website },
      {
        onSuccess: () => {
          // clear the form after saving
          setName("");
          setIndustry("");
          setWebsite("");
        },
      }
    );
  }

  function handleDelete(id: number, companyName: string) {
    if (confirm("Delete " + companyName + "?")) {
      deleteCompany.mutate(id);
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Companies</h1>

      <Card>
        <CardTitle>Add company</CardTitle>
        <form onSubmit={handleSubmit} className="grid gap-3 md:grid-cols-4 md:items-end">
          <div>
            <label className="label">Name *</label>
            <input className="input" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div>
            <label className="label">Industry</label>
            <input className="input" value={industry} onChange={(e) => setIndustry(e.target.value)} />
          </div>
          <div>
            <label className="label">Website</label>
            <input className="input" value={website} onChange={(e) => setWebsite(e.target.value)} />
          </div>
          <button className="btn" disabled={createCompany.isPending}>
            {createCompany.isPending ? "Saving..." : "Add company"}
          </button>
        </form>
      </Card>

      <Card>
        {companies.isLoading && <Loading />}
        {companies.error && <ErrorMessage error={companies.error} />}
        {companies.data && companies.data.length === 0 && <Empty text="No companies yet." />}
        {companies.data && companies.data.length > 0 && (
          <div className="overflow-x-auto">
            <table className="table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Industry</th>
                  <th>Website</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {companies.data.map((company) => (
                  <tr key={company.id}>
                    <td className="font-medium">{company.name}</td>
                    <td>{company.industry || "-"}</td>
                    <td>
                      {company.website ? (
                        <a href={company.website} target="_blank" className="text-indigo-600 hover:underline">
                          {company.website.replace("https://", "")}
                        </a>
                      ) : (
                        "-"
                      )}
                    </td>
                    <td className="text-right">
                      <button
                        className="btn-secondary"
                        onClick={() => handleDelete(company.id, company.name)}
                        disabled={deleteCompany.isPending}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>
    </div>
  );
}
