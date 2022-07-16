import { CalendarIcon } from "@heroicons/react/solid";
import { useContext, useState } from "react";
import { getEnvironment, getProjectKeyFromApiKey } from "../../../core/api/api";
import { useNavigate } from "react-router-dom";
import PopupModal from "../../../core/components/PopupModal";
import ProjectContext from "../../../ProjectContext";

export default function ProjectList({ projects = [] }) {
  const { addApiKey, selectProject, addEnvironment } =
    useContext(ProjectContext);
  const navigate = useNavigate();
  const [selectedProject, setSelectedProject] = useState(undefined);
  const [showSelectionModal, setShowSelectionModal] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);
  const [modalError, setModalError] = useState(undefined);

  const handleApiKeySubmission = async (apiKey) => {
    try {
      setModalError(undefined);
      setModalLoading(true);
      const projectKey = await getProjectKeyFromApiKey(apiKey);

      if (projectKey !== selectedProject.projectKey) {
        throw new Error("API Key does not match the selected project!");
      }

      // Set apiKey and project key to state
      setShowSelectionModal(false);
      addApiKey(apiKey);
      selectProject(selectedProject);

      // Fetch environment information
      const environment = await getEnvironment(apiKey, projectKey);
      addEnvironment(environment);

      // Navigate to project
      navigate("/projects/" + projectKey + "/toggles");
    } catch (e) {
      setModalError(e.message);
    }
    setModalLoading(false);
  };

  return (
    <div className="bg-white shadow overflow-hidden sm:rounded-md">
      <PopupModal
        heading={`Manage '${selectedProject?.projectKey}' project ?`}
        description="Please enter the environment specific api-key to manage this project."
        buttonText={"Enter API Key"}
        setSuccess={setShowSelectionModal}
        success={showSelectionModal}
        preRouteSubmitCheck={handleApiKeySubmission}
        type="input"
        loading={modalLoading}
        error={modalError}
      />
      <ul role="list" className="divide-y divide-gray-200">
        {projects?.map((project) => (
          <li key={project.projectKey}>
            <a
              onClick={() => {
                setSelectedProject(project);
                setShowSelectionModal(true);
              }}
              className="block hover:bg-gray-50 cursor-pointer"
            >
              <div className="px-4 py-4 sm:px-6">
                <div className="flex items-center justify-between">
                  <p className="text-sm font-medium text-indigo-600 truncate capitalize">
                    {project.projectKey}
                  </p>
                  <div className="ml-2 flex-shrink-0 flex">
                    <p className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                      {project?.apiKeys?.length} API Keys
                    </p>
                  </div>
                </div>
                <div className="mt-2 sm:flex sm:justify-between">
                  <div className="sm:flex">
                    <p className="text-gray-500 max-w-[52ch] overflow-hidden text-ellipsis">
                      {project.description}
                    </p>
                  </div>
                  <div className="mt-2 flex items-center text-sm text-gray-500 sm:mt-0">
                    <CalendarIcon
                      className="flex-shrink-0 mr-1.5 h-5 w-5 text-gray-400"
                      aria-hidden="true"
                    />
                    <p>
                      Created{" "}
                      <time dateTime={project.createdAt}>
                        {project.createdAt.split("T")[0] }
                      </time>
                    </p>
                  </div>
                </div>
              </div>
            </a>
          </li>
        ))}
      </ul>
    </div>
  );
}
