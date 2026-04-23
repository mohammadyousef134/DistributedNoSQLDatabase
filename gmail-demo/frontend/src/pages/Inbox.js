import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { request } from "../api/api";
import Sidebar from "../components/Sidebar";
import { useToast } from "../context/ToastContext";

export default function Inbox() {
  const [emails, setEmails] = useState([]);
  const [loading, setLoading] = useState(true);
  const toast = useToast();

  useEffect(() => {
    loadInbox();
  }, []);

  async function loadInbox() {
    try {
      const data = await request("/api/emails/inbox", "GET");
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
          <h1 className="page-title">Inbox</h1>
          {!loading && (
            <p className="page-subtitle">
              {emails.filter((e) => !e.read).length > 0
                ? `${emails.filter((e) => !e.read).length} unread`
                : `${emails.length} message${emails.length !== 1 ? "s" : ""}`}
            </p>
          )}
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="loading-dots">
              <span /><span /><span />
            </div>
            <div>Loading inbox…</div>
          </div>
        ) : emails.length === 0 ? (
          <div className="email-list">
            <div className="empty-state">
              <div className="empty-icon">📭</div>
              <div>Your inbox is empty</div>
            </div>
          </div>
        ) : (
          <div className="email-list">
            {emails.map((email) => (
              <Link
                key={email.id}
                to={`/email/${email.id}`}
                className={`email-item ${!email.read ? "unread" : ""}`}
              >
                {!email.read ? (
                  <div className="unread-indicator" title="Unread" />
                ) : (
                  <div className="read-indicator" />
                )}
                <div className="email-meta">
                  <div className="email-from">{email.from}</div>
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