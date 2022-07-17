/* This example requires Tailwind CSS v2.0+ */
import { SwitchHorizontalIcon } from "@heroicons/react/outline";
import { useContext, useState } from "react";
import { toggleToggleVariant, getToggles } from "../../../core/api/api";
import Toggle from "../../../core/components/Toggle";
import ProjectContext from "../../../ProjectContext";
import CreateVariationButton from "./CreateVariationButton";
import ToggleDeletionModal from "./ToggleDeletionModal";

export default function ToggleDetails() {
  const {
    selectedProject,
    environment,
    apiKey,
    selectToggle,
    addFetchedToggles,
    selectedToggle: toggle,
  } = useContext(ProjectContext);
  const [toggleDeletionModalOpen, setToggleDeletionModalOpen] = useState(false);

  const handleToggleVariant = async (variantKey) => {
    await toggleToggleVariant(apiKey, toggle?.key, variantKey);
    const response = await getToggles(apiKey);
    addFetchedToggles(response);
    selectToggle(response.find((t) => t.key === toggle?.key));
  };

  return (
    <>
      <div className="bg-white shadow overflow-hidden sm:rounded-lg">
        <div className="px- py-5 sm:px-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Toggle Status url:
          </h3>
          <p className="mt-1 max-w-2xl text-sm text-gray-500">
            GET {process.env.REACT_APP_BE_API_URL}/toggles/{toggle?.key}/status
          </p>
        </div>
        <div className="border-t border-gray-200 px-4 py-5 sm:px-6">
          <dl className="grid grid-cols-1 gap-x-4 gap-y-8 sm:grid-cols-2">
            <div className="sm:col-span-1">
              <dt className="text-sm font-medium text-gray-500">Toggle key</dt>
              <dd className="mt-1 text-sm text-gray-900">{toggle?.key}</dd>
            </div>
            <div className="sm:col-span-1">
              <dt className="text-sm font-medium text-gray-500">Project</dt>
              <dd className="mt-1 text-sm text-gray-900">
                {selectedProject.projectKey} - {environment}
              </dd>
            </div>
            <div className="sm:col-span-1">
              <dt className="text-sm font-medium text-gray-500">Created</dt>
              <dd className="mt-1 text-sm text-gray-900">
                {toggle?.createdAt.split("T")[0] +
                  " " +
                  toggle?.createdAt.split("T")[1]}
              </dd>
            </div>
            <div className="sm:col-span-1">
              <dt className="text-sm font-medium text-gray-500">Modified</dt>
              <dd className="mt-1 text-sm text-gray-900">
                {toggle?.updatedAt.split("T")[0] +
                  " " +
                  toggle?.updatedAt.split("T")[1]}
              </dd>
            </div>
            <div className="sm:col-span-2">
              <dt className="text-sm font-medium text-gray-500">
                Toggle description
              </dt>
              <dd className="mt-1 text-sm text-gray-900">
                {toggle?.description}
              </dd>
            </div>
            <div className="sm:col-span-2">
              <dt className="text-sm font-medium text-gray-500">Variations</dt>
              <dd className="mt-1 text-sm text-gray-900">
                <ul
                  role="list"
                  className="border border-gray-200 rounded-md divide-y divide-gray-200"
                >
                  {toggle?.variations
                    ?.filter(
                      (variation) => variation.variationKey !== "default"
                    )
                    .map((variation) => (
                      <li className="pl-3 pr-4 py-3 text-sm">
                        <div className="flex-row-reverse items-center justify-between">
                          <div className="flex items-center justify-between">
                            <div className="w-0 flex-1 flex items-center">
                              <SwitchHorizontalIcon
                                className="flex-shrink-0 h-5 w-5 text-gray-400"
                                aria-hidden="true"
                              />
                              <span className="ml-2 flex-1 w-0 truncate">
                                {variation.variationKey}
                              </span>
                            </div>
                            <div className="ml-4 flex-shrink-0">
                              <Toggle
                                enabled={variation.enabled}
                                setEnabled={() =>
                                  handleToggleVariant(variation.variationKey)
                                }
                              />
                            </div>
                          </div>
                          <div className="flex items-center justify-between pt-1 pb-5">
                            <div className="w-0 flex-1 flex items-center">
                              <span className="text-gray-500 ">
                                <p className="mt-1 max-w-2xl text-sm text-gray-500">
                                  GET {process.env.REACT_APP_BE_API_URL}
                                  /toggles/
                                  {toggle?.key}/{variation.variationKey}/status
                                </p>
                              </span>
                            </div>
                          </div>
                          <div className="flex items-center justify-between pt-1">
                            <div className="w-0 flex-1 flex items-center">
                              <span className="text-gray-500 ">
                                <b>Created:</b>{" "}
                                {variation.createdAt.split("T")[0] +
                                  " " +
                                  variation.createdAt.split("T")[1]}
                              </span>
                            </div>
                          </div>
                          <div className="flex items-center justify-between pt-1">
                            <div className="w-0 flex-1 flex items-center">
                              <span className="text-gray-500 ">
                                <b>Updated:</b>{" "}
                                {variation.updatedAt.split("T")[0] +
                                  " " +
                                  variation.updatedAt.split("T")[1]}
                              </span>
                            </div>
                          </div>
                          <div className="flex items-center justify-between pt-4">
                            <div className="w-0 flex-1 flex items-center">
                              <span className="text-gray-500 ">
                                <b>Description:</b> {variation.description}
                              </span>
                            </div>
                          </div>
                        </div>
                      </li>
                    ))}
                </ul>
                <div className="flex items-center justify-between pt-4">
                  <div className="w-0 flex-1 flex items-center">
                    <CreateVariationButton />
                  </div>
                </div>
              </dd>
            </div>
          </dl>
        </div>
      </div>
      <div className="mt-5 sm:mt-4 sm:flex sm:flex-row-reverse">
        <button
          type="button"
          className="absolute bottom-1 left-[10rem] bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-red-500 hover:text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-sky-500"
          onClick={() => setToggleDeletionModalOpen(true)}
        >
          Remove Toggle
        </button>
      </div>
      <div className="mt-5 sm:mt-4 sm:flex sm:flex-row-reverse">
        <button
          type="button"
          className="absolute bottom-1 left-8 w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-hanpurple-600 text-base font-medium text-white hover:bg-hanpurple-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 sm:ml-3 sm:w-auto sm:text-sm"
        >
          Edit Toggle
        </button>
      </div>
      <ToggleDeletionModal
        open={toggleDeletionModalOpen}
        setOpen={setToggleDeletionModalOpen}
      />
    </>
  );
}
