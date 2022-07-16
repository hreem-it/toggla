import { useEffect, useState } from "react";
import { getProjects } from "../../core/api/api";
import CreateProjectCallToAction from "./components/CreateProjectCallToAction";
import ProjectList from "./components/ProjectList";

const ProjectPage = () => {
  const [projects, setProjects] = useState([]);

  useEffect(() => {
    const fetchProjects = async () => {
      const response = await getProjects();
      setProjects(response);
    };

    fetchProjects().catch(console.error);
  }, []);

  return (
    <>
      <div className="px-4 sm:px-6 md:px-0">
        <h1 className="text-2xl font-semibold text-gray-900">Projects</h1>
      </div>
      <div className="px-4 sm:px-6 md:px-0">
        {/* Replace with your content */}
        <div className="py-4">
          {projects && <ProjectList projects={projects} />}
          <div className="border-4 border-dashed border-gray-200 rounded-lg h-60 mt-3 pt-[3%]">
            <CreateProjectCallToAction projects={projects} />
          </div>
        </div>
        {/* /End replace */}
      </div>
    </>
  );
};

export default ProjectPage;
