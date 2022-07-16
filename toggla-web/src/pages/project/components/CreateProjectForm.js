import { useState } from "react";
import { Link } from "react-router-dom";
import { createProject, createProjectKey } from "../../../core/api/api";
import Alert from "../../../core/components/Alert";
import ButtonLoadingSpinner from "../../../core/components/ButtonLoadingSpinner";
import EnvironmentChip from "../../../core/components/EnvironmentChip";
import PopupModal from "../../../core/components/PopupModal";

export default function CreateProjectForm() {
  const [projectName, setProjectName] = useState("");
  const [description, setDescription] = useState("");
  const [keyDescription, setKeyDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [createdApiKey, setCreatedApiKey] = useState(undefined);
  const [error, setError] = useState(undefined);

  const handleSubmit = async (_) => {
    setLoading(true);
    setError(undefined);
    try {
      await createProject({
        projectKey: projectName,
        description,
      });
      const apiKey = await createProjectKey(projectName, {
        description: keyDescription,
        env: "DEV",
      });
      setCreatedApiKey(apiKey);
      setSuccess(true);
    } catch (e) {
      setError(e.message);
    }
    setLoading(false);
  };

  return (
    <>
      <PopupModal
        heading={`Project ${projectName} created!`}
        description="Please copy the API Key, and click to return to the projects section."
        apiKey={createdApiKey}
        navigateTo="/projects"
        buttonText={"I have securely stored my API Key!"}
        setSuccess={setSuccess}
        success={success}
      />
      <form className="space-y-8 divide-y divide-gray-200">
        <div className="space-y-8 divide-y divide-gray-200">
          <div>
            <div>
              <h3 className="text-lg leading-6 font-medium text-gray-900">
                Project Details
              </h3>
              <p className="mt-1 text-sm text-gray-500">
                This information will be displayed publicly so be careful what
                you share.
              </p>
            </div>

            <div className="mt-6 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
              <div className="sm:col-span-4">
                <label
                  htmlFor="projectname"
                  className="block text-sm font-medium text-gray-700"
                >
                  Project name
                </label>
                <div className="mt-1 flex rounded-md shadow-sm">
                  <span className="inline-flex items-center px-3 rounded-l-md border border-r-0 border-gray-300 bg-gray-50 text-gray-500 sm:text-sm">
                    {window.location.href.split(window.location.pathname)[0]}
                    /projects/
                  </span>
                  <input
                    type="text"
                    name="projectname"
                    id="projectname"
                    autoComplete="projectname"
                    placeholder="my-fruity-fruit-shop"
                    onChange={(e) => setProjectName(e.target.value)}
                    className="flex-1 focus:ring-indigo-500 focus:border-indigo-500 block w-full min-w-0 rounded-none rounded-r-md sm:text-sm border-gray-300"
                  />
                </div>
              </div>

              <div className="sm:col-span-6">
                <label
                  htmlFor="about"
                  className="block text-sm font-medium text-gray-700"
                >
                  Project Description
                </label>
                <div className="mt-1">
                  <textarea
                    id="about"
                    name="about"
                    rows={3}
                    placeholder="Selling fruit and vegtables..."
                    onChange={(e) => setDescription(e.target.value)}
                    className="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border border-gray-300 rounded-md"
                    defaultValue={""}
                  />
                </div>
                <p className="mt-2 text-sm text-gray-500">
                  Write a few sentences about your project.
                </p>
              </div>
            </div>
          </div>

          <div className="pt-6">
            <div>
              <h3 className="text-lg leading-6 font-medium text-gray-900">
                API Key {"  "}
                <EnvironmentChip envOverride={"DEV"} />
              </h3>
              <p className="mt-1 text-sm text-gray-500">
                An API Key will be created for your DEV environment, this API
                key will be used to call the toggla API's and for administrating
                your project. You can create API Keys for your other
                environments via the dashboard.
                <b> Keep it secret!</b>
              </p>
            </div>

            <div className="mt-6 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
              <div className="sm:col-span-6">
                <label
                  htmlFor="about"
                  className="block text-sm font-medium text-gray-700"
                >
                  API Key Description
                </label>
                <div className="mt-1">
                  <textarea
                    id="about"
                    name="about"
                    rows={3}
                    placeholder="Handling feature-toggles for our services in dev..."
                    onChange={(e) => setKeyDescription(e.target.value)}
                    className="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border border-gray-300 rounded-md"
                    defaultValue={""}
                  />
                </div>
                <p className="mt-2 text-sm text-gray-500">
                  Optional: Describe what this API Key will be used for.
                </p>
              </div>
            </div>
          </div>
          {error && <Alert errorMessage={error} />}
        </div>

        <div className="pt-5">
          <div className="flex justify-end">
            <Link to="/projects">
              <button
                type="button"
                className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                Cancel
              </button>
            </Link>
            <button
              type="button"
              className="ml-3 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              onClick={handleSubmit}
            >
              {loading && <ButtonLoadingSpinner />}
              {loading ? "Creating Project..." : "Create Project"}
            </button>
          </div>
        </div>
      </form>
    </>
  );
}
