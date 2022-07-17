import { useNavigate } from "react-router-dom";
import { useContext, useState } from "react";
import { PlusIcon } from "@heroicons/react/solid";
import ProjectContext from "../../ProjectContext";
import ProjectDeletionModal from "./components/ProjectDeletionModal";

const team = [
  {
    name: "Calvin Hawkins",
    email: "calvin.hawkins@example.com",
    imageUrl:
      "https://images.unsplash.com/photo-1513910367299-bce8d8a0ebf6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
  },
  {
    name: "Bessie Richards",
    email: "bessie.richards@example.com",
    imageUrl:
      "https://images.unsplash.com/photo-1517841905240-472988babdf9?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
  },
  {
    name: "Floyd Black",
    email: "floyd.black@example.com",
    imageUrl:
      "https://images.unsplash.com/photo-1531427186611-ecfd6d936c79?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=facearea&facepad=2&w=256&h=256&q=80",
  },
];

const SettingsPage = () => {
  const [openProjectDeletionModal, setOpenProjectDeletionModal] =
    useState(false);
  const { selectedProject } = useContext(ProjectContext);

  return (
    <>
      <form>
        <div className="space-y-6">
          <div>
            <h1 className="text-lg leading-6 font-medium text-gray-900">
              Project Settings
            </h1>
            <p className="mt-1 text-sm text-gray-500">
              Here you can see your current project settings.
            </p>
          </div>

          <div>
            <label
              htmlFor="project-name"
              className="block text-sm font-medium text-gray-700"
            >
              Project Name
            </label>
            <div className="mt-1">
              <input
                type="text"
                name="project-name"
                id="project-name"
                disabled
                className="block w-full shadow-sm text-gray-500 focus:ring-sky-500 focus:border-sky-500 sm:text-sm border-gray-300 rounded-md"
                defaultValue={selectedProject.projectKey}
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="description"
              className="block text-sm font-medium text-gray-700"
            >
              Description
            </label>
            <div className="mt-1">
              <textarea
                id="description"
                name="description"
                disabled
                rows={3}
                className="block w-full shadow-sm text-gray-500 focus:ring-sky-500 focus:border-sky-500 sm:text-sm border border-gray-300 rounded-md"
                defaultValue={selectedProject.description}
              />
            </div>
          </div>

          <div className="space-y-2">
            <div className="space-y-1">
              <label
                htmlFor="add-team-members"
                className="block text-sm font-medium text-gray-700"
              >
                Add Team Members (Coming soon)
              </label>
              <p id="add-team-members-helper" className="sr-only">
                Search by email address
              </p>
              <div className="flex blur-xs">
                <div className="flex-grow">
                  <input
                    type="text"
                    name="add-team-members"
                    id="add-team-members"
                    className="block w-full shadow-sm focus:ring-sky-500 focus:border-sky-500 sm:text-sm border-gray-300 rounded-md"
                    placeholder="Email address"
                    disabled
                    aria-describedby="add-team-members-helper"
                  />
                </div>
                <span className="ml-3 blur-xs">
                  <button
                    type="button"
                    disabled
                    className="bg-white inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-sky-500"
                  >
                    <PlusIcon
                      className="-ml-2 mr-1 h-5 w-5 text-gray-400"
                      aria-hidden="true"
                    />
                    <span>Add</span>
                  </button>
                </span>
              </div>
            </div>

            <div className="blur-xs">
              <div className="border-b border-gray-200">
                <ul role="list" className="divide-y divide-gray-200">
                  {team.map((person) => (
                    <li key={person.email} className="py-4 flex">
                      <img
                        className="h-10 w-10 rounded-full"
                        src={person.imageUrl}
                        alt=""
                      />
                      <div className="ml-3 flex flex-col">
                        <span className="text-sm font-medium text-gray-900">
                          {person.name}
                        </span>
                        <span className="text-sm text-gray-500">
                          {person.email}
                        </span>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>

          {/* PROJECT TAGS */}
          <div>
            <label
              htmlFor="tags"
              className="block text-sm font-medium text-gray-700"
            >
              Tags (Coming soon)
            </label>
            <input
              disabled
              type="text"
              name="tags"
              id="tags"
              className="blur-xs mt-1 block w-full shadow-sm focus:ring-sky-500 focus:border-sky-500 sm:text-sm border-gray-300 rounded-md"
            />
          </div>

          <div className="flex justify-end">
            <button
              type="button"
              onClick={() => setOpenProjectDeletionModal(true)}
              className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-red-500 hover:text-white focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-sky-500"
            >
              Delete this project
            </button>
            <button
              type="button"
              className="ml-3 inline-flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-hanpurple-500 hover:bg-hanpurple-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-sky-500"
            >
              Save settings
            </button>
          </div>
        </div>
      </form>
      <ProjectDeletionModal
        open={openProjectDeletionModal}
        setOpen={setOpenProjectDeletionModal}
      />
    </>
  );
};

export default SettingsPage;
