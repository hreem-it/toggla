import { Route, Routes } from "react-router-dom";
import "./App.css";
import Page from "./core/components/Page";
import Dashboard from "./pages/Dashboard";
import Home from "./pages/Dashboard";

function App() {
  return (
    <Page>
      <Dashboard />
    </Page>
  );
}

export default App;
