import Footer from "./Footer";
import Header from "./Header";

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
