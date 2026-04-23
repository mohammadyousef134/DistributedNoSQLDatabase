import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { request } from "../api/api";
import Sidebar from "../components/Sidebar";
import { useToast } from "../context/ToastContext";

export default function Sent() {
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(true);
  const toast = useToast();

  useEffect(() => {
    loadSent();
  }, []);

  async function loadSent() {
    try {
      const data = await request("/api/emails/sent", "GET");
      setEmails(data);
    } catch (err) {
      toast(err.message, "error");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="app-shell">
      <Sidebar />
      <main className="main-content">
        <div className="page-header">
          <h1 className="page-title">Sent</h1>
          {!loading && (
            <p className="page-subtitle">
              {emails.length} sent message{emails.length !== 1 ? "s" : ""}
            </p>
          )}
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="loading-dots">
              <span /><span /><span />
            </div>
            <div>Loading sent mail…</div>
          </div>
        ) : emails.length === 0 ? (
          <div className="email-list">
            <div className="empty-state">
              <div className="empty-icon">📤</div>
              <div>No sent emails yet</div>
            </div>
          </div>
        ) : (
          <div className="email-list">
            {emails.map((email) => (
              <Link
                key={email.id}
                to={`/email/${email.id}`}
                className="email-item"
              >
                <div className="read-indicator" />
                <div className="email-meta">
                  <div className="email-from">To: {email.to}</div>
                  <div className="email-subject">{email.subject}</div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </main>
    </div>
  );
}