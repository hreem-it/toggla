import { KeyIcon } from "@heroicons/react/outline";
import { useState, useContext, useEffect } from "react";
import { Link } from "react-router-dom";
import { createProjectKey } from "../../../core/api/api";
import Alert from "../../../core/components/Alert";
import ButtonLoadingSpinner from "../../../core/components/ButtonLoadingSpinner";
import PopupModal from "../../../core/components/PopupModal";
import ProjectContext from "../../../ProjectContext";
import EnvironmentSelector from "./EnvironmentSelector";

export default function CreateKeyForm() {
  const { selectedProject } = useContext(ProjectContext);
  const [keyDescription, setKeyDescription] = useState("");
  const [environment, setEnvironment] = useState(undefined);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(undefined);
  const [createdApiKey, setCreatedApiKey] = useState(undefined);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (_) => {
    setLoading(true);
    setError(undefined);
    try {
      const newKey = await createProjectKey(selectedProject.projectKey, {
        description: keyDescription,
        env: environment.key,
      });
      setCreatedApiKey(newKey);
      setSuccess(true);
    } catch (e) {
      setError(e.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    const listener = (event) => {
      if (event.code === "Enter" || event.code === "NumpadEnter") {
        event.preventDefault();
        handleSubmit();
      }
    };
    document.addEventListener("keydown", listener);
    return () => {
      document.removeEventListener("keydown", listener);
    };
  }, [keyDescription, environment]);

  return (
    <>
      <PopupModal
        heading={`New API Key created for ${selectedProject.projectKey}!`}
        description="Please copy the API Key, and click to return to the projects section."
        apiKey={createdApiKey}
        navigateTo="/projects"
        buttonText={"I have securely stored my API Key!"}
        setSuccess={setSuccess}
        success={success}
      />
      <form className="space-y-8 divide-y divide-gray-200">
        <div className="space-y-8 divide-y divide-gray-200">
          <div className="pt-6">
            <div>
              <h3 className="text-lg leading-6 font-medium text-gray-900">
                Create a new API Key
              </h3>
            </div>

            <div className="mt-6 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
              <div className="sm:col-span-6">
                <label
                  htmlFor="key"
                  className="block text-sm font-medium text-gray-700"
                >
                  Environment
                </label>
                <div className="mt-1 mb-5">
                  <EnvironmentSelector
                    selected={environment}
                    setSelected={setEnvironment}
                  />
                </div>
                <label
                  htmlFor="about"
                  className="block text-sm font-medium text-gray-700"
                >
                  API Key description
                </label>
                <div className="mt-1">
                  <textarea
                    id="about"
                    name="about"
                    rows={3}
                    placeholder="For fetching keys via DEV services..."
                    onChange={(e) => setKeyDescription(e.target.value)}
                    value={keyDescription}
                    className="shadow-sm focus:ring-hanpurple-500 focus:border-hanpurple-500 block w-full sm:text-sm border border-gray-300 rounded-md"
                    defaultValue={""}
                  />
                </div>
                <p className="mt-2 text-sm text-gray-500">
                  Optional: Describe what the key is used for.
                </p>
              </div>
            </div>
          </div>
          {error && <Alert errorMessage={error} />}
        </div>

        <div className="pt-5">
          <div className="flex justify-end">
            <Link to={`/projects/${selectedProject.projectKey}/keys`}>
              <button
                type="button"
                className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-hanpurple-500"
              >
                Cancel
              </button>
            </Link>
            <button
              type="button"
              className="ml-3 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-hanpurple-700 hover:bg-hanpurple-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-hanpurple-500"
              onClick={handleSubmit}
            >
              <KeyIcon className="-ml-1 mr-2 h-5 w-5" aria-hidden="true" />
              {loading && <ButtonLoadingSpinner />}
              {loading ? "Creating API Key..." : "Create API Key"}
            </button>
          </div>
        </div>
      </form>
    </>
  );
}
