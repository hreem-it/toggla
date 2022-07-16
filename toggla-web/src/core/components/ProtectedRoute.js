import { useContext } from "react";
import { Navigate } from "react-router-dom";
import ProjectContext from "../../ProjectContext";

const ProtectedRoute = ({ children }) => {
  const { selectedProject } = useContext(ProjectContext);

  if (!selectedProject) {
    return <Navigate to={"/projects"} replace />;
  }

  return children;
};

export default ProtectedRoute;
