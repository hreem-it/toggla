/* This example requires Tailwind CSS v2.0+ */
import { PlusIcon } from "@heroicons/react/solid";
import { useContext } from "react";
import { Link } from "react-router-dom";
import Toggle from "../../../core/components/Toggle";
import ProjectContext from "../../../ProjectContext";

export default function CreateToggleCallToAction({ toggles }) {
  const { selectedProject } = useContext(ProjectContext);
  return (
    <div className="text-center pt-8">
      <Toggle disabled={true} />
      <h3 className="mt-2 text-sm font-medium text-gray-900">
        {toggles ? "Can't find your project?" : "No toggles"}
      </h3>
      <p className="mt-1 text-sm text-gray-500">
        Get started by creating a new feature-toggle.
      </p>
      <div className="mt-6">
        <Link to={`/projects/${selectedProject.projectKey}/toggles/new`}>
          <button
            type="button"
            className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-hanpurple-700 hover:bg-hanpurple-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-hanpurple-500"
          >
            <PlusIcon className="-ml-1 mr-2 h-5 w-5" aria-hidden="true" />
            Create a new Feature toggle
          </button>
        </Link>
      </div>
    </div>
  );
}
