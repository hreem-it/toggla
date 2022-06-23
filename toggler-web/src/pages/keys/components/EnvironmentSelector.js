import { useContext, useState } from "react";
import { RadioGroup } from "@headlessui/react";
import ProjectContext from "../../../ProjectContext";
import EnvironmentChip from "../../../core/components/EnvironmentChip";

const settings = [
  {
    key: "DEV",
    name: "Develepment",
    description:
      "This logical project grouping would allow you to create and manage feature-toggles for your development environment and see Reports.",
  },
  {
    key: "STAGE",
    name: "Staging",
    description:
      "This logical project grouping would allow you to create and manage feature-toggles for your staging environment and see Reports.",
  },
  {
    key: "CANARY",
    name: "Canary",
    description:
      "This logical project grouping would allow you to create and manage feature-toggles for your canary environment and see Reports.",
  },
  {
    key: "PROD",
    name: "Production",
    description:
      "This logical project grouping would allow you to create and manage feature-toggles for your production environment and see Reports.",
  },
];

function classNames(...classes) {
  return classes.filter(Boolean).join(" ");
}

export default function EnvironmentSelector() {
  const [selected, setSelected] = useState(settings[1]);
  const { selectedProject } = useContext(ProjectContext);
  const environments = selectedProject?.apiKeys.map((apiKey) => apiKey.env);

  return (
    <RadioGroup value={selected} onChange={setSelected}>
      <RadioGroup.Label className="sr-only">Privacy setting</RadioGroup.Label>
      <div className="bg-white rounded-md -space-y-px">
        {settings.map((setting, settingIdx) => (
          <RadioGroup.Option
            disabled={environments.includes(setting.key)}
            key={setting.name}
            value={setting}
            className={({ checked }) =>
              classNames(
                environments.includes(setting.key) && "bg-gray-100",
                settingIdx === 0 ? "rounded-tl-md rounded-tr-md" : "",
                settingIdx === settings.length - 1
                  ? "rounded-bl-md rounded-br-md"
                  : "",
                checked
                  ? "bg-indigo-50 border-indigo-200 z-10"
                  : "border-gray-200",
                "relative border p-4 flex cursor-pointer focus:outline-none"
              )
            }
          >
            {({ active, checked }) => (
              <>
                <span
                  className={classNames(
                    checked
                      ? "bg-indigo-600 border-transparent"
                      : "bg-white border-gray-300",
                    active ? "ring-2 ring-offset-2 ring-indigo-500" : "",
                    "h-4 w-4 mt-0.5 cursor-pointer shrink-0 rounded-full border flex items-center justify-center"
                  )}
                  aria-hidden="true"
                >
                  <span className="rounded-full bg-white w-1.5 h-1.5" />
                </span>
                <span className="ml-3 flex flex-col">
                  <RadioGroup.Label
                    as="span"
                    className={classNames(
                      checked ? "text-indigo-900" : "text-gray-900",
                      "block text-sm font-medium"
                    )}
                  >
                    {setting.name + "  "}
                    <EnvironmentChip envOverride={setting.key} />
                  </RadioGroup.Label>
                  <RadioGroup.Description
                    as="span"
                    className={classNames(
                      checked ? "text-indigo-700" : "text-gray-500",
                      "block text-sm"
                    )}
                  >
                    {setting.description}
                  </RadioGroup.Description>
                </span>
              </>
            )}
          </RadioGroup.Option>
        ))}
      </div>
    </RadioGroup>
  );
}
