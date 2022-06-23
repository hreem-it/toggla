import { useState, useContext } from "react";
import { createToggleVariation, getToggles } from "../../../core/api/api";
import Alert from "../../../core/components/Alert";
import ButtonLoadingSpinner from "../../../core/components/ButtonLoadingSpinner";
import EnvironmentChip from "../../../core/components/EnvironmentChip";
import ProjectContext from "../../../ProjectContext";

export default function CreateVariationForm({ setOpen }) {
  const { selectedToggle, apiKey, addFetchedToggles, selectToggle } =
    useContext(ProjectContext);
  const [variationKey, setVariationKey] = useState("");
  const [variationDescription, setVariationDescription] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(undefined);

  const handleSubmit = async (_) => {
    setLoading(true);
    setError(undefined);
    try {
      await createToggleVariation(apiKey, selectedToggle.key, {
        variationKey: variationKey,
        description: variationDescription,
        enabled: false,
      });
      const response = await getToggles(apiKey);
      addFetchedToggles(response);
      selectToggle(response.find((t) => t.key === selectedToggle.key));
      setOpen(false);
    } catch (e) {
      setError(e.message);
    }
    setLoading(false);
  };

  return (
    <>
      <form className="space-y-8 divide-y divide-gray-200">
        <div className="space-y-8 divide-y divide-gray-200">
          <div className="pt-6">
            <div>
              <h3 className="text-lg leading-6 font-medium text-gray-900">
                Create a new Variation for {selectedToggle.key}
                {"  "}
                <EnvironmentChip envOverride={"DEV"} />
              </h3>
            </div>

            <div className="mt-6 grid grid-cols-1 gap-y-6 gap-x-4 sm:grid-cols-6">
              <div className="sm:col-span-6">
                <label
                  htmlFor="key"
                  className="block text-sm font-medium text-gray-700"
                >
                  Toggle variation key
                </label>
                <div className="mt-1 mb-2">
                  <input
                    id="key"
                    name="key"
                    type="text"
                    placeholder="my-toggle-variation-1"
                    onChange={(e) => setVariationKey(e.target.value)}
                    className="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border border-gray-300 rounded-md"
                    defaultValue={""}
                  />
                </div>
                <label
                  htmlFor="about"
                  className="block text-sm font-medium text-gray-700"
                >
                  Toggle variation description
                </label>
                <div className="mt-1">
                  <textarea
                    id="about"
                    name="about"
                    rows={3}
                    placeholder="Enable parallel processing for new feature..."
                    onChange={(e) => setVariationDescription(e.target.value)}
                    className="shadow-sm focus:ring-indigo-500 focus:border-indigo-500 block w-full sm:text-sm border border-gray-300 rounded-md"
                    defaultValue={""}
                  />
                </div>
                <p className="mt-2 text-sm text-gray-500">
                  Optional: Describe what this toggle variation will be used
                  for.
                </p>
              </div>
            </div>
          </div>
          {error && <Alert errorMessage={error} />}
        </div>

        <div className="pt-5">
          <div className="flex justify-end">
            <button
              type="button"
              onClick={() => setOpen(false)}
              className="bg-white py-2 px-4 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Cancel
            </button>
            <button
              type="button"
              className="ml-3 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              onClick={handleSubmit}
            >
              {loading && <ButtonLoadingSpinner />}
              {loading ? "Creating Variation..." : "Create Variation"}
            </button>
          </div>
        </div>
      </form>
    </>
  );
}
