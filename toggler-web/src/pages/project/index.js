import CreateProjectCallToAction from "./components/CreateProjectCallToAction";

const ProjectPage = () => {
  return (
    <>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-2xl font-semibold text-gray-900">Projects</h1>
      </div>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
        {/* Replace with your content */}
        <div className="py-4">
          <div className="border-4 border-dashed border-gray-200 rounded-lg h-96 pt-[8%]">
            <CreateProjectCallToAction />
          </div>
        </div>
        {/* /End replace */}
      </div>
    </>
  );
};

export default ProjectPage;
