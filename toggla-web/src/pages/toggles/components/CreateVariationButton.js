import { SwitchHorizontalIcon } from "@heroicons/react/outline";
import { useState } from "react";
import CreateVariation from "./CreateVariation";

export default function CreateVariationButton() {
  const [openCreationModal, setOpenCreationModal] = useState(false);

  return (
    <>
      <button
        type="button"
        onClick={() => setOpenCreationModal(true)}
        className="relative block w-full border-2 border-gray-300 border-dashed rounded-lg p-5 text-center hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-azure-500"
      >
        <div className="flex justify-center">
          <SwitchHorizontalIcon
            className="h-5 w-5 text-center pr-2"
            aria-hidden="true"
          />{" "}
          Create a new variation
        </div>
      </button>
      <CreateVariation
        open={openCreationModal}
        setOpen={setOpenCreationModal}
      />
    </>
  );
}
