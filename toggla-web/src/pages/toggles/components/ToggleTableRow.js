import { useState, useContext } from "react";
import { SwitchHorizontalIcon } from "@heroicons/react/outline";
import Toggle from "../../../core/components/Toggle";
import ToggleSidebar from "./ToggleSidebar";
import ProjectContext from "../../../ProjectContext";
import { toggleToggle, getToggles } from "../../../core/api/api";
import { classNames } from "../../../core/Util";

const ToggleTableRow = ({ toggle }) => {
  const { selectToggle, apiKey, addFetchedToggles } =
    useContext(ProjectContext);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const isDefaultActive =
    toggle?.variations?.filter(
      (v) => v.variationKey === "default" && v.enabled === true
    )?.length !== 0;
  const activeVariations = toggle?.variations?.filter(
    (v) => v.variationKey !== "default" && v.enabled === true
  )?.length;
  const areAnyVariationsActive = activeVariations !== 0;

  const handleToggleSelection = (toggle) => {
    selectToggle(toggle);
    setSidebarOpen(true);
  };

  const handleToggle = async () => {
    await toggleToggle(apiKey, toggle.toggleKey);
    const response = await getToggles(apiKey);
    addFetchedToggles(response);
  };

  return (
    <>
      <ToggleSidebar
        key={toggle.toggleKey}
        open={sidebarOpen}
        setOpen={setSidebarOpen}
      />
      <tr key={toggle.description} className="cursor-pointer">
        <td
          onClick={() => handleToggleSelection(toggle)}
          className="whitespace-nowrap py-4 pl-4 pr-3 text-sm sm:pl-6"
        >
          <div className="flex items-center">
            <div className="h-10 w-10 flex-shrink-0">
              <SwitchHorizontalIcon
                className="h-10 w-10 p-3 rounded-full bg-gray-100"
                aria-hidden="true"
              />
            </div>
            <div className="ml-4">
              <div className="font-medium text-gray-900">
                {toggle.toggleKey}
              </div>
              <div className="text-gray-500 max-w-[35ch] overflow-hidden text-ellipsis">
                {toggle.description}
              </div>
            </div>
          </div>
        </td>
        <td
          onClick={() => handleToggleSelection(toggle)}
          className="whitespace-nowrap px-3 py-4 text-sm text-gray-500"
        >
          <span
            className={classNames(
              isDefaultActive
                ? "text-green-800 bg-green-100"
                : areAnyVariationsActive
                ? "text-yellow-800 bg-yellow-100"
                : "text-gray-800 bg-gray-100",
              "inline-flex rounded-full px-2 text-xs font-semibold leading-5 "
            )}
          >
            {isDefaultActive
              ? "Active"
              : areAnyVariationsActive
              ? `${activeVariations} variation${
                  activeVariations > 1 ? "s" : ""
                } active`
              : "Inactive"}
          </span>
        </td>
        <td
          onClick={() => handleToggleSelection(toggle)}
          className="whitespace-nowrap px-3 py-4 text-sm text-gray-500"
        >
          {
            toggle.variations?.filter(
              (variation) => variation.variationKey !== "default"
            ).length
          }{" "}
          Variations
        </td>
        <td className="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
          <Toggle
            enabled={
              toggle?.variations?.find(
                (variation) => variation.variationKey === "default"
              )?.enabled
            }
            setEnabled={handleToggle}
          />
        </td>
      </tr>
    </>
  );
};

export default ToggleTableRow;
