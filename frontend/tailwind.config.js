/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        dark: {
          bg: '#0A0A0A',
          card: '#151515',
          elevated: '#1D1D1D',
          hover: '#242424',
          border: '#27272A',
          borderLight: '#3F3F46'
        },
        brand: {
          green: '#10B981',
          greenLight: '#34D399',
          greenDark: '#059669',
          orange: '#EA580C',
          orangeLight: '#F97316',
          orangeDark: '#C2410C',
        }
      },
      fontFamily: {
        sans: ['Inter', 'Manrope', 'system-ui', 'sans-serif'],
      }
    },
  },
  plugins: [],
}

