import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ToastProvider } from "./context/ToastContext";
import Register from "./pages/Register";
import SendEmail from "./pages/SendEmail";
import Inbox from "./pages/Inbox";
import EmailDetails from "./pages/EmailDetails";
import Sent from "./pages/Sent";
import "./styles/Global.css";

function App() {
  return (
    <ToastProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navigate to="/register" replace />} />
          <Route path="/register" element={<Register />} />
          <Route path="/inbox" element={<Inbox />} />
          <Route path="/sent" element={<Sent />} />
          <Route path="/send" element={<SendEmail />} />
          <Route path="/email/:id" element={<EmailDetails />} />
        </Routes>
      </BrowserRouter>
    </ToastProvider>
  );
}

export default App;