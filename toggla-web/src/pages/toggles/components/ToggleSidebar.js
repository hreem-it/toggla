/* This example requires Tailwind CSS v2.0+ */
import { Fragment, useContext, useEffect } from "react";
import { Dialog, Transition } from "@headlessui/react";
import { SwitchHorizontalIcon, XIcon } from "@heroicons/react/outline";
import ToggleDetails from "./ToggleDetails";
import ProjectContext from "../../../ProjectContext";
import { getToggles } from "../../../core/api/api";

export default function ToggleSidebar({ open, setOpen }) {
  const {
    addFetchedToggles,
    apiKey,
    selectToggle,
    selectedToggle: toggle,
  } = useContext(ProjectContext);

  const handleModalClose = async () => {
    const response = await getToggles(apiKey);
    addFetchedToggles(response);
    selectToggle(response.find((t) => t.toggleKey === toggle?.toggleKey));
    setOpen(false);
  };

  useEffect(() => {
    if (!toggle && open) {
      handleModalClose();
    }
  }, [toggle]);

  return (
    <Transition.Root show={open && !!selectToggle} as={Fragment}>
      <Dialog as="div" className="relative z-10" onClose={handleModalClose}>
        <Transition.Child
          as={Fragment}
          enter="ease-in-out duration-500"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in-out duration-500"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-gray-500 bg-opacity-25 transition-opacity" />
        </Transition.Child>

        <div className="fixed inset-0 overflow-hidden">
          <div className="absolute inset-0 overflow-hidden">
            <div className="pointer-events-none fixed inset-y-0 right-0 flex max-w-full pl-10">
              <Transition.Child
                as={Fragment}
                enter="transform transition ease-in-out duration-500 sm:duration-700"
                enterFrom="translate-x-full"
                enterTo="translate-x-0"
                leave="transform transition ease-in-out duration-500 sm:duration-700"
                leaveFrom="translate-x-0"
                leaveTo="translate-x-full"
              >
                <Dialog.Panel className="pointer-events-auto w-screen max-w-4xl">
                  <div className="flex h-full flex-col overflow-y-scroll bg-white py-6 shadow-xl">
                    <div className="px-4 sm:px-6">
                      <div className="flex items-start justify-between">
                        <Dialog.Title className="text-lg font-medium text-gray-900">
                          <SwitchHorizontalIcon
                            className="h-10 w-10 p-3 rounded-full bg-gray-100"
                            aria-hidden="true"
                          />
                        </Dialog.Title>
                        <div className="ml-3 flex h-7 items-center">
                          <button
                            type="button"
                            className="rounded-md bg-white text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-hanpurple-500 focus:ring-offset-2"
                            onClick={handleModalClose}
                          >
                            <span className="sr-only">Close panel</span>
                            <XIcon className="h-6 w-6" aria-hidden="true" />
                          </button>
                        </div>
                      </div>
                    </div>
                    <div className="relative mt-6 flex-1 px-4">
                      <div className="absolute inset-0 px-4">
                        <ToggleDetails />
                      </div>
                    </div>
                  </div>
                </Dialog.Panel>
              </Transition.Child>
            </div>
          </div>
        </div>
      </Dialog>
    </Transition.Root>
  );
}
