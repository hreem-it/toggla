import { createContext, useState } from "react";

const ProjectContext = createContext();

export function ProjectProvider({ children }) {
  const [selectedProject, setSelectedProject] = useState(undefined);
  const [selectedToggle, setSelectedToggle] = useState(undefined);
  const [apiKey, setApiKey] = useState(undefined);
  const [environment, setEnvironment] = useState(undefined);
  const [fetchedToggles, setFetchedToggles] = useState([]);

  const addFetchedToggles = (toggles) => {
    setFetchedToggles(toggles);
  };

  const selectProject = (project) => {
    setSelectedProject(project);
  };

  const selectToggle = (toggle) => {
    setSelectedToggle(toggle);
  };

  const addApiKey = (key) => {
    setApiKey(key);
  };

  const addEnvironment = (env) => {
    setEnvironment(env);
  };

  return (
    <ProjectContext.Provider
      value={{
        // Project
        selectedProject,
        selectProject,
        // API Key
        apiKey,
        addApiKey,
        // Environment
        environment,
        addEnvironment,
        // Toggle
        selectedToggle,
        selectToggle,
        // Toggles
        fetchedToggles,
        addFetchedToggles,
      }}
    >
      {children}
    </ProjectContext.Provider>
  );
}

export default ProjectContext;
