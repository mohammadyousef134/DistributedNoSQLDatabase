import { Link, useLocation } from "react-router-dom";

function InboxIcon() {
  return (
    <svg width="15" height="15" fill="none" stroke="currentColor" strokeWidth="1.8" viewBox="0 0 24 24">
      <polyline points="22 12 16 12 14 15 10 15 8 12 2 12" />
      <path d="M5.45 5.11L2 12v6a2 2 0 002 2h16a2 2 0 002-2v-6l-3.45-6.89A2 2 0 0016.76 4H7.24a2 2 0 00-1.79 1.11z" />
    </svg>
  );
}

function SendIcon() {
  return (
    <svg width="15" height="15" fill="none" stroke="currentColor" strokeWidth="1.8" viewBox="0 0 24 24">
      <line x1="22" y1="2" x2="11" y2="13" />
      <polygon points="22 2 15 22 11 13 2 9 22 2" />
    </svg>
  );
}

function ComposeIcon() {
  return (
    <svg width="15" height="15" fill="none" stroke="currentColor" strokeWidth="1.8" viewBox="0 0 24 24">
      <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" />
      <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" />
    </svg>
  );
}

export default function Sidebar() {
  const location = useLocation();
  const username = localStorage.getItem("username");
  const initial = username ? username[0].toUpperCase() : "?";

  const links = [
    { to: "/inbox", label: "Inbox", Icon: InboxIcon },
    { to: "/sent", label: "Sent", Icon: SendIcon },
    { to: "/send", label: "Compose", Icon: ComposeIcon },
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <span>✦</span> Mail
      </div>

      <nav className="sidebar-nav">
        {links.map(({ to, label, Icon }) => (
          <Link
            key={to}
            to={to}
            className={`nav-link ${location.pathname === to ? "active" : ""}`}
          >
            <Icon />
            {label}
          </Link>
        ))}
      </nav>

      <div className="sidebar-user">
        <div className="user-badge">
          <div className="user-avatar">{initial}</div>
          <div className="user-name">{username || "Guest"}</div>
        </div>
      </div>
    </aside>
  );
}