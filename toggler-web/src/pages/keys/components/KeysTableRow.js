import { useContext } from "react";
import { KeyIcon } from "@heroicons/react/outline";
import Toggle from "../../../core/components/Toggle";
import ProjectContext from "../../../ProjectContext";
import { classNames } from "../../../core/Util";
import EnvironmentChip from "../../../core/components/EnvironmentChip";

const KeysTableRow = ({ apiKey }) => {
  return (
    <>
      <tr key={apiKey.description}>
        <td className="whitespace-nowrap py-4 pl-4 pr-3 text-sm sm:pl-6">
          <div className="flex items-center">
            <div className="h-10 w-10 flex-shrink-0">
              <KeyIcon
                className="h-10 w-10 p-3 rounded-full bg-gray-100"
                aria-hidden="true"
              />
            </div>
            <div className="ml-4">
              <div className="font-medium text-gray-900">{apiKey.apiKey}</div>
              <div className="text-gray-500 max-w-[66ch] overflow-hidden text-ellipsis">
                {apiKey.description}
              </div>
            </div>
          </div>
        </td>
        <td className="whitespace-nowrap px-3 py-4 text-sm text-gray-500">
          <EnvironmentChip envOverride={apiKey.env} />
        </td>
        <td className="relative whitespace-nowrap py-4 pl-3 pr-4 text-right text-sm font-medium sm:pr-6">
          {/* <button
            className="disabled inline-flex items-center px-2.5 py-1.5 border border-transparent text-xs font-medium rounded shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            disabled
          >
            Remove
          </button> */}
        </td>
      </tr>
    </>
  );
};

export default KeysTableRow;
