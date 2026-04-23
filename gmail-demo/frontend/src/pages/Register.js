import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useToast } from "../context/ToastContext";

export default function Register() {
  const [username, setUsername] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const toast = useToast();

  async function handleRegister() {
    if (!username.trim()) return;
    setLoading(true);
    try {
      const res = await fetch("http://localhost:8085/api/auth/register/" + username.trim(), {
        method: "POST",
      });

      if (!res.ok) throw new Error("Registration failed");

      const data = await res.json();
      localStorage.setItem("username", data.username);
      localStorage.setItem("token", data.token);

      toast("Welcome, " + data.username + "!");
      navigate("/inbox");
    } catch (err) {
      toast(err.message, "error");
    } finally {
      setLoading(false);
    }
  }

  function handleKeyDown(e) {
    if (e.key === "Enter") handleRegister();
  }

  return (
    <div className="auth-wrapper">
      <div className="auth-card">
        <div className="auth-logo">
          <span>✦</span> Mail
        </div>

        <h1 className="auth-title">Get started</h1>
        <p className="auth-subtitle">Create an account to access your inbox</p>

        <div className="auth-field">
          <label>Username</label>
          <input
            className="auth-input"
            type="text"
            placeholder="Choose a username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            onKeyDown={handleKeyDown}
            autoFocus
          />
        </div>

        <button
          className="auth-btn"
          onClick={handleRegister}
          disabled={loading || !username.trim()}
        >
          {loading ? "Creating account…" : "Create account →"}
        </button>
      </div>
    </div>
  );
}