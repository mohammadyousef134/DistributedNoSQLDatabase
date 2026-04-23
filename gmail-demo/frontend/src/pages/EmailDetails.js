import { useState, useEffect } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { request } from "../api/api";
import Sidebar from "../components/Sidebar";
import { useToast } from "../context/ToastContext";

export default function EmailDetails() {
  const { id } = useParams();
  const [email, setEmail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);
  const navigate = useNavigate();
  const toast = useToast();

  useEffect(() => {
    loadEmail();
  }, [id]);

  async function loadEmail() {
    try {
      // Backend marks email as read=true on GET /api/emails/:id
      const data = await request(`/api/emails/${id}`, "GET");
      setEmail(data);
    } catch (err) {
      toast(err.message, "error");
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete() {
    if (!window.confirm("Delete this email?")) return;
    setDeleting(true);
    try {
      await request(`/api/emails/${id}`, "DELETE");
      toast("Email deleted");
      navigate(-1);
    } catch (err) {
      toast(err.message, "error");
      setDeleting(false);
    }
  }

  const username = localStorage.getItem("username");
  const isInbox = email && email.to === username;

  return (
    <div className="app-shell">
      <Sidebar />
      <main className="main-content">
        <Link to={isInbox ? "/inbox" : "/sent"} className="back-link">
          ← Back to {isInbox ? "Inbox" : "Sent"}
        </Link>

        {loading ? (
          <div className="loading-state">
            <div className="loading-dots">
              <span /><span /><span />
            </div>
            <div>Loading email…</div>
          </div>
        ) : !email ? (
          <div className="email-list">
            <div className="empty-state">Email not found</div>
          </div>
        ) : (
          <div className="email-detail-card">
            <div className="email-detail-header">
              <div className="email-detail-subject">{email.subject}</div>
              <div className="email-detail-meta">
                <div className="meta-row">
                  <span className="meta-label">From</span>
                  <span className="meta-value">{email.from}</span>
                </div>
                <div className="meta-row">
                  <span className="meta-label">To</span>
                  <span className="meta-value">{email.to}</span>
                </div>
              </div>
            </div>

            <div className="email-detail-body">{email.body}</div>

            <div className="email-detail-actions">
              <button
                className="btn btn-danger"
                onClick={handleDelete}
                disabled={deleting}
              >
                {deleting ? "Deleting…" : "Delete"}
              </button>
              <Link to="/send" className="btn btn-ghost" style={{ textDecoration: "none" }}>
                Reply
              </Link>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}