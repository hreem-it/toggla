module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      blur: {
        xs: '2px',
      },
      colors: {
        hanpurple: {
          200: "#7D71FE",
          300: "#6A5DFE",
          400: "#5748FE",
          500: "#4534FE",
          600: "#3220FE",
          700: "#200BFE",
          800: "#1501F4",
          900: "#1401DF",
        },
        azure: {
          200: "#4AA0FC",
          300: "#3696FC",
          400: "#228BFC",
          500: "#0479F6",
          600: "#0479F1",
          700: "#036DDD",
          800: "#0363C9",
          900: "#0359B5"
        },
        capri: {
          200: "#47CBFF",
          300: "#33C5FF",
          400: "#1FBFFF",
          500: "#00B2F9",
          600: "#00AFF5",
          700: "#00A1E0",
          800: "#0092CC",
          900: "#0084B8"
        },
        lightcyan: {
          300: "#FFFFFF",
          400: "#EEFAFC",
          500: "#00B2F9",
          600: "#CCEFF5",
          700: "#BBE9F2",
          800: "#AAE4EE",
        }
      },
    },
  },
  plugins: [require("@tailwindcss/forms")],
};
