import CreateKeyForm from "./components/CreateKeyForm";

const NewKey = () => {
  return (
    <>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 className="text-2xl font-semibold text-gray-900">
          Create a new feature toggle
        </h1>
      </div>
      <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
        {/* Replace with your content */}
        <div className="py-4">
          <CreateKeyForm />
        </div>
        {/* /End replace */}
      </div>
    </>
  );
};

export default NewKey;
