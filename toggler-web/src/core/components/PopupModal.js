import { Fragment, useState } from "react";
import { Dialog, Transition } from "@headlessui/react";
import {
  CheckIcon,
  KeyIcon,
  QuestionMarkCircleIcon,
} from "@heroicons/react/outline";
import { Link } from "react-router-dom";
import ButtonLoadingSpinner from "./ButtonLoadingSpinner";
import Alert from "./Alert";
import { classNames } from "../Util";

export default function PopupModal({
  heading,
  description,
  buttonText,
  navigateTo,
  success: open,
  setSuccess: setOpen,
  type = "success",
  preRouteSubmitCheck,
  apiKey,
  loading,
  error,
}) {
  const [input, setInput] = useState("");

  return (
    <Transition.Root show={open} as={Fragment}>
      <Dialog as="div" className="relative z-10" onClose={setOpen}>
        <Transition.Child
          as={Fragment}
          enter="ease-out duration-300"
          enterFrom="opacity-0"
          enterTo="opacity-100"
          leave="ease-in duration-200"
          leaveFrom="opacity-100"
          leaveTo="opacity-0"
        >
          <div className="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" />
        </Transition.Child>

        <div className="fixed z-10 inset-0 overflow-y-auto">
          <div className="flex items-end sm:items-center justify-center min-h-full p-4 text-center sm:p-0">
            <Transition.Child
              as={Fragment}
              enter="ease-out duration-300"
              enterFrom="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
              enterTo="opacity-100 translate-y-0 sm:scale-100"
              leave="ease-in duration-200"
              leaveFrom="opacity-100 translate-y-0 sm:scale-100"
              leaveTo="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            >
              <Dialog.Panel className="relative bg-white rounded-lg px-4 pt-5 pb-4 text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:max-w-sm sm:w-full sm:p-6">
                <div>
                  <div
                    className={classNames(
                      type === "success" ? "bg-green-100" : "bg-yellow-100",
                      "mx-auto flex items-center justify-center h-12 w-12 rounded-full"
                    )}
                  >
                    {type === "success" ? (
                      <CheckIcon
                        className="h-6 w-6 text-green-60"
                        aria-hidden="true"
                      />
                    ) : (
                      <KeyIcon
                        className="h-6 w-6 text-yellow-600"
                        aria-hidden="true"
                      />
                    )}
                  </div>
                  <div className="mt-3 text-center sm:mt-5">
                    <Dialog.Title
                      as="h3"
                      className="text-lg leading-6 font-medium text-gray-900"
                    >
                      {heading}
                    </Dialog.Title>
                    <div className="mt-2">
                      <p className="text-sm text-gray-500">{description}</p>
                    </div>
                  </div>
                </div>
                {type !== "success" && (
                  <div className="mt-5 sm:mt-6">
                    <input
                      type="password"
                      name="api-secret"
                      id="api-secret"
                      autoComplete="api-secret"
                      onChange={(e) => setInput(e.target.value)}
                      placeholder="00000000-0000-0000-0000-000000000000"
                      className="flex-1 text-center focus:ring-indigo-500 focus:border-indigo-500 block w-full min-w-0 rounded-none rounded-r-md sm:text-sm border-gray-300"
                    />
                  </div>
                )}
                {type === "success" && apiKey && (
                  <div className="mt-5 sm:mt-6">
                    <input
                      type="text"
                      name="api-secret"
                      id="api-secret"
                      autoComplete="api-secret"
                      value={apiKey}
                      placeholder="00000000-0000-0000-0000-000000000000"
                      className="flex-1 text-center focus:ring-indigo-500 focus:border-indigo-500 block w-full min-w-0 rounded-none rounded-r-md sm:text-sm border-gray-300"
                      disabled
                    />
                  </div>
                )}
                <div className="mt-5 sm:mt-6">
                  {preRouteSubmitCheck ? (
                    <button
                      type="button"
                      className="inline-flex justify-center w-full rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:text-sm"
                      onClick={() => {
                        preRouteSubmitCheck(input);
                      }}
                    >
                      {loading && <ButtonLoadingSpinner />}
                      {loading ? "Loading..." : buttonText}
                    </button>
                  ) : (
                    <Link to={navigateTo}>
                      <button
                        type="button"
                        className="inline-flex justify-center w-full rounded-md border border-transparent shadow-sm px-4 py-2 bg-indigo-600 text-base font-medium text-white hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 sm:text-sm"
                        onClick={() => {
                          setOpen(false);
                        }}
                      >
                        {buttonText}
                      </button>
                    </Link>
                  )}
                  {error && <Alert errorMessage={error} />}
                </div>
              </Dialog.Panel>
            </Transition.Child>
          </div>
        </div>
      </Dialog>
    </Transition.Root>
  );
}
