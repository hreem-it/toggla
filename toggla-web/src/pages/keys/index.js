import KeysTable from "./components/KeysTable";

const KeysPage = () => {
  return (
    <>
      <div className="px-4 sm:px-6 md:px-0">
        <h1 className="text-2xl font-semibold text-gray-900">
          Manage API Keys
        </h1>
      </div>
      {/* Replace with your content */}
      <div>
        <KeysTable />
      </div>
      {/* /End replace */}
    </>
  );
};

export default KeysPage;
