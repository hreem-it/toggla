import { Fragment, useContext, useState } from "react";
import { Dialog, Transition } from "@headlessui/react";
import {
  BellIcon,
  DocumentIcon,
  FolderIcon,
  KeyIcon,
  MenuAlt2Icon,
  SwitchHorizontalIcon,
  XIcon,
} from "@heroicons/react/outline";
import { Link, Navigate, Route, Routes } from "react-router-dom";
import ProjectPage from "./project";
import NewProject from "./project/NewProject";
import ProjectContext from "../ProjectContext";
import EnvironmentChip from "../core/components/EnvironmentChip";
import TogglesPage from "./toggles";
import ProtectedRoute from "../core/components/ProtectedRoute";
import NewToggle from "./toggles/NewToggle";
import KeysPage from "./keys";
import NewKey from "./keys/NewKey";

function classNames(...classes) {
  return classes.filter(Boolean).join(" ");
}

export default function Example() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { selectedProject } = useContext(ProjectContext);
  let location = window.location.pathname.split("/");

  const navigation = [
    {
      name: "Projects",
      href: "/projects",
      icon: FolderIcon,
      current: location[location.length - 1].includes("projects"),
      indent: false,
      external: false,
    },
    {
      name: "Toggles",
      href: `/projects/${selectedProject?.projectKey}/toggles`,
      icon: SwitchHorizontalIcon,
      current: location[location.length - 1].includes("toggles"),
      indent: true,
      external: false,
    },
    {
      name: "API Keys",
      href: `/projects/${selectedProject?.projectKey}/keys`,
      icon: KeyIcon,
      current: location[location.length - 1].includes("keys"),
      indent: true,
      external: false,
    },
    // {
    //   name: "Reports",
    //   href: `/projects/${selectedProject?.projectKey}/reports`,
    //   icon: ChartBarIcon,
    //   current: location[location.length - 1].includes("reports"),
    //   indent: true,
    // },
  ];

  const apiDocs = {
    name: "API Docs",
    href: `${process.env.REACT_APP_BE_API_URL}/openapi/ui`,
    icon: DocumentIcon,
    current: false,
    indent: false,
    external: true,
  };

  return (
    <>
      <div>
        <Transition.Root show={sidebarOpen} as={Fragment}>
          <Dialog
            as="div"
            className="relative z-40 md:hidden"
            onClose={setSidebarOpen}
          >
            <Transition.Child
              as={Fragment}
              enter="transition-opacity ease-linear duration-300"
              enterFrom="opacity-0"
              enterTo="opacity-100"
              leave="transition-opacity ease-linear duration-300"
              leaveFrom="opacity-100"
              leaveTo="opacity-0"
            >
              <div className="fixed inset-0 bg-gray-600 bg-opacity-75" />
            </Transition.Child>

            <div className="fixed inset-0 z-40 flex">
              <Transition.Child
                as={Fragment}
                enter="transition ease-in-out duration-300 transform"
                enterFrom="-translate-x-full"
                enterTo="translate-x-0"
                leave="transition ease-in-out duration-300 transform"
                leaveFrom="translate-x-0"
                leaveTo="-translate-x-full"
              >
                <Dialog.Panel className="relative max-w-xs w-full bg-white pt-5 pb-4 flex-1 flex flex-col">
                  <Transition.Child
                    as={Fragment}
                    enter="ease-in-out duration-300"
                    enterFrom="opacity-0"
                    enterTo="opacity-100"
                    leave="ease-in-out duration-300"
                    leaveFrom="opacity-100"
                    leaveTo="opacity-0"
                  >
                    <div className="absolute top-0 right-0 -mr-12 pt-2">
                      <button
                        type="button"
                        className="ml-1 flex items-center justify-center h-10 w-10 rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white"
                        onClick={() => setSidebarOpen(false)}
                      >
                        <span className="sr-only">Close sidebar</span>
                        <XIcon
                          className="h-6 w-6 text-white"
                          aria-hidden="true"
                        />
                      </button>
                    </div>
                  </Transition.Child>
                  <div className="flex-shrink-0 px-4 py-2 flex items-center">
                    <Link to="/">
                      <img
                        className="h-11 pl-3 w-auto"
                        src="/toggler-logo-transparent.png"
                        alt="Workflow"
                      />
                    </Link>
                  </div>
                  <div className="mt-5 flex-1 h-0 overflow-y-auto">
                    <nav className="px-2 space-y-1">
                      {navigation.map((item) => (
                        <a
                          key={item.name}
                          href={item.href}
                          className={classNames(
                            item.current
                              ? "bg-gray-100 text-gray-900"
                              : "text-gray-600 hover:bg-gray-50 hover:text-gray-900",
                            "group rounded-md py-2 px-2 flex items-center text-base font-medium"
                          )}
                        >
                          <item.icon
                            className={classNames(
                              item.current
                                ? "text-gray-500"
                                : "text-gray-400 group-hover:text-gray-500",
                              "mr-4 flex-shrink-0 h-6 w-6"
                            )}
                            aria-hidden="true"
                          />
                          {item.name}
                        </a>
                      ))}
                    </nav>
                  </div>
                </Dialog.Panel>
              </Transition.Child>
              <div className="flex-shrink-0 w-14">
                {/* Dummy element to force sidebar to shrink to fit close icon */}
              </div>
            </div>
          </Dialog>
        </Transition.Root>

        {/* Static sidebar for desktop */}
        <div className="hidden md:flex md:w-64 md:flex-col md:fixed md:inset-y-0">
          {/* Sidebar component, swap this element with another sidebar if you like */}
          <div className="border-r border-gray-200 pt-5 flex flex-col flex-grow bg-white overflow-y-auto">
            <div className="flex-shrink-0 px-4 py-2 flex items-center">
              <Link to="/">
                <img
                  className="h-11 pl-3 w-auto"
                  src="/toggler-logo-transparent.png"
                  alt="Workflow"
                />
              </Link>
            </div>
            <div className="flex-grow mt-5 flex flex-col">
              <nav className="flex-1 px-2 pb-4 space-y-1">
                {navigation
                  .filter((n) => !n.external)
                  .map((item) => (
                    <Link
                      key={item.name}
                      to={item.href}
                      replace={item.replace}
                      className={classNames(
                        item.current
                          ? "bg-gray-100 text-gray-900"
                          : "text-gray-600 hover:bg-gray-50 hover:text-gray-900",
                        item.indent ? "px-5" : "px-2",
                        !selectedProject && item.indent
                          ? "pointer-events-none opacity-50"
                          : "pointer-events-auto",
                        "group rounded-md py-2 flex items-center text-sm font-medium"
                      )}
                    >
                      <item.icon
                        className={classNames(
                          item.current
                            ? "text-gray-500"
                            : "text-gray-400 group-hover:text-gray-500",
                          "flex-shrink-0 h-6 w-6"
                        )}
                        aria-hidden="true"
                      />
                      {item.name}
                    </Link>
                  ))}
              </nav>
            </div>
            <div className="flex-shrink-0 flex border-t border-gray-200 p-4">
              <a href="#" className="flex-shrink-0 w-full group block">
                <div className="flex items-center">
                  <div className="ml-3">
                    <a
                      href={apiDocs.href}
                      className="group rounded-md py-2 flex items-center text-sm font-medium text-gray-600 hover:bg-gray-50 hover:text-gray-900"
                    >
                      <DocumentIcon
                        className={classNames(
                          "text-gray-400 group-hover:text-gray-500",
                          "flex-shrink-0 h-6 w-6"
                        )}
                        aria-hidden="true"
                      />
                      {apiDocs.name}
                    </a>
                  </div>
                </div>
              </a>
            </div>
          </div>
        </div>

        <div className="md:pl-64">
          <div className="max-w-4xl mx-auto flex flex-col md:px-8 xl:px-0">
            <div className="sticky top-0 z-10 flex-shrink-0 h-16 bg-white border-b border-gray-200 flex">
              <button
                type="button"
                className="border-r border-gray-200 px-4 text-gray-500 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-indigo-500 md:hidden"
                onClick={() => setSidebarOpen(true)}
              >
                <span className="sr-only">Open sidebar</span>
                <MenuAlt2Icon className="h-6 w-6" aria-hidden="true" />
              </button>
              <div className="flex-1 flex justify-between px-4 md:px-0">
                <div className="flex-1 flex">
                  <form className="w-full flex md:ml-0" action="#" method="GET">
                    <label htmlFor="search-field" className="sr-only">
                      Choose Project
                    </label>
                    <div className="relative w-full text-gray-400 focus-within:text-gray-600">
                      <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center">
                        <FolderIcon className="h-5 w-5" aria-hidden="true" />
                      </div>
                      <input
                        id="search-field"
                        className="block h-full w-full border-transparent py-2 pl-8 pr-3 text-gray-900 placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-0 focus:border-transparent sm:text-sm"
                        placeholder={
                          selectedProject
                            ? selectedProject.projectKey
                            : "No project selected"
                        }
                        type={
                          selectedProject
                            ? selectedProject.projectKey
                            : "No project selected"
                        }
                        name={
                          selectedProject
                            ? selectedProject.projectKey
                            : "No project selected"
                        }
                        disabled
                      />
                      <div className="absolute right-0 top-5">
                        <EnvironmentChip />
                      </div>
                    </div>
                  </form>
                </div>
                <div className="ml-4 flex items-center md:ml-6">
                  <button
                    type="button"
                    className="bg-white p-1 rounded-full text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                  >
                    <span className="sr-only">View notifications</span>
                    <BellIcon className="h-6 w-6" aria-hidden="true" />
                  </button>
                </div>
              </div>
            </div>

            <main className="flex-">
              <div className="py-6">
                <Routes>
                  <Route path="/" element={<Navigate to="/projects" />} />
                  <Route path="/projects" element={<ProjectPage />} />
                  <Route path="/projects/new" element={<NewProject />} />
                  <Route
                    path="/projects/:projectKey/toggles"
                    element={
                      <ProtectedRoute>
                        <TogglesPage />
                      </ProtectedRoute>
                    }
                  />
                  <Route
                    path="/projects/:projectKey/toggles/new"
                    element={
                      <ProtectedRoute>
                        <NewToggle />
                      </ProtectedRoute>
                    }
                  />
                  <Route
                    path="/projects/:projectKey/keys"
                    element={
                      <ProtectedRoute>
                        <KeysPage />
                      </ProtectedRoute>
                    }
                  />
                  <Route
                    path="/projects/:projectKey/keys/new"
                    element={
                      <ProtectedRoute>
                        <NewKey />
                      </ProtectedRoute>
                    }
                  />
                </Routes>
              </div>
            </main>
          </div>
        </div>
      </div>
    </>
  );
}
