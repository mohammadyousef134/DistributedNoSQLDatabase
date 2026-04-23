import { useState } from "react";
import { request } from "../api/api";
import Sidebar from "../components/Sidebar";
import { useToast } from "../context/ToastContext";

export default function SendEmail() {
  const [to, setTo] = useState("");
  const [subject, setSubject] = useState("");
  const [body, setBody] = useState("");
  const [sending, setSending] = useState(false);
  const toast = useToast();

  async function handleSend() {
    if (!to.trim() || !subject.trim()) {
      toast("Please fill in To and Subject fields", "error");
      return;
    }
    setSending(true);
    try {
      const username = localStorage.getItem("username");
      await request("/api/emails/send", "POST", {
        from: username,
        to: to.trim(),
        subject: subject.trim(),
        body: body.trim(),
      });

      toast("Email sent!");
      setTo("");
      setSubject("");
      setBody("");
    } catch (err) {
      toast(err.message, "error");
    } finally {
      setSending(false);
    }
  }

  return (
    <div className="app-shell">
      <Sidebar />
      <main className="main-content">
        <div className="page-header">
          <h1 className="page-title">Compose</h1>
          <p className="page-subtitle">Write a new email</p>
        </div>

        <div className="compose-card">
          <div className="compose-field">
            <span className="compose-label">To</span>
            <input
              className="compose-input"
              type="text"
              placeholder="Recipient username"
              value={to}
              onChange={(e) => setTo(e.target.value)}
              autoFocus
            />
          </div>

          <div className="compose-field">
            <span className="compose-label">Subject</span>
            <input
              className="compose-input"
              type="text"
              placeholder="What's this about?"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
            />
          </div>

          <div className="compose-field" style={{ alignItems: "stretch" }}>
            <span className="compose-label" style={{ paddingTop: 16 }}>Body</span>
            <textarea
              className="compose-body"
              placeholder="Write your message…"
              value={body}
              onChange={(e) => setBody(e.target.value)}
            />
          </div>

          <div className="compose-footer">
            <button
              className="btn btn-primary"
              onClick={handleSend}
              disabled={sending}
            >
              {sending ? "Sending…" : "Send email →"}
            </button>
            <span style={{ fontSize: 12, color: "var(--text-muted)" }}>
              {body.length > 0 ? `${body.length} chars` : ""}
            </span>
          </div>
        </div>
      </main>
    </div>
  );
}