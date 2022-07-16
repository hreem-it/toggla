import { KeyIcon } from "@heroicons/react/outline";
import { useContext } from "react";
import { Link } from "react-router-dom";
import ProjectContext from "../../../ProjectContext";
import KeysTableRow from "./KeysTableRow";

export default function KeysTable() {
  const { selectedProject, apiKey } = useContext(ProjectContext);

  return (
    <>
      <div className="sm:flex sm:items-center pt-7">
        <div className="sm:flex-auto">
          <h1 className="text-xl font-semibold text-gray-900">API Keys</h1>
          <p className="mt-2 text-sm text-gray-700">
            A list of all API Keys are displayed here.
          </p>
        </div>
        <div className="mt-4 sm:mt-0 sm:ml-16 sm:flex-none">
          <Link to={`./new`}>
            <button
              type="button"
              className="inline-flex items-center justify-center rounded-md border border-transparent bg-indigo-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 sm:w-auto"
            >
              <KeyIcon className="-ml-1 mr-2 h-5 w-5" aria-hidden="true" />
              Add new API Key
            </button>
          </Link>
        </div>
      </div>
      <div className="mt-8 flex flex-col">
        <div className="-my-2 -mx-4 overflow-x-auto sm:-mx-6 lg:-mx-8">
          <div className="inline-block min-w-full py-2 align-middle md:px-6 lg:px-8">
            <div className="overflow-hidden shadow ring-1 ring-black ring-opacity-5 md:rounded-lg">
              <table className="min-w-full divide-y divide-gray-300">
                <thead className="bg-gray-50">
                  <tr>
                    <th
                      scope="col"
                      className="py-3.5 pl-4 pr-3 text-left text-sm font-semibold text-gray-900 sm:pl-6"
                    >
                      API Key
                    </th>
                    <th
                      scope="col"
                      className="px-3 py-3.5 text-left text-sm font-semibold text-gray-900"
                    >
                      Environment
                    </th>
                    <th
                      scope="col"
                      className="relative py-3.5 pl-3 pr-4 sm:pr-6"
                    >
                      <span className="sr-only">Edit</span>
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200 bg-white">
                  {selectedProject &&
                    selectedProject?.apiKeys?.map((apiKey) => (
                      <KeysTableRow apiKey={apiKey} />
                    ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
