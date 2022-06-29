import { useContext } from "react";
import ProjectContext from "../../ProjectContext";

const EnvironmentChip = ({ envOverride }) => {
  const { environment } = useContext(ProjectContext);

  function classNames(...classes) {
    return classes.filter(Boolean).join(" ");
  }

  const env = envOverride ? envOverride : environment;
  return (
    <p
      className={classNames(
        env === "DEV" && "text-green-800 bg-green-100",
        env === "TEST" && "text-yellow-800 bg-yellow-100",
        env === "CANARY" && "text-orange-800 bg-orange-100",
        env === "PROD" && "text-red-800 bg-red-100",
        "px-2 inline-flex text-xs leading-5 font-semibold rounded-full "
      )}
    >
      {env}
    </p>
  );
};

export default EnvironmentChip;
