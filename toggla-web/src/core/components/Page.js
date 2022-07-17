const Page = (props) => {
  return (
    <>
      <div className="min-h-full">
        <>{props?.children}</>
      </div>
    </>
  );
};

export default Page;
