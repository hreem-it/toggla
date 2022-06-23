import TogglesTable from "./components/TogglesTable";

const TogglesPage = () => {
  return (
    <>
      <div className="px-4 sm:px-6 md:px-0">
        <h1 className="text-2xl font-semibold text-gray-900">
          Manage feature toggles
        </h1>
      </div>
      {/* Replace with your content */}
      <div>
        <TogglesTable />
      </div>
      {/* /End replace */}
    </>
  );
};

export default TogglesPage;
