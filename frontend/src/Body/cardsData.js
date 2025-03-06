import { info1, info2, info3, how1, how2, how3 } from "../imports/importPictures.js";

const aboutUsCards = [
    {
        title: "Enter Property Details",
        text: "We make property evaluation simple—just input key details or provide a real estate listing link, and we’ll handle the rest.",
        backText: "Too often, buyers struggle to assess a property’s true value. Our system ensures transparency, preventing misleading listings and giving you a clear, data-driven evaluation.",
        image: info1,
    },
    {
        title: "Set Your Preferences",
        text: "Everyone has different needs when buying a home. We let you prioritize what matters most—whether it's location, price, or future investment potential.",
        backText: "Brokers often push properties that benefit them, not you. Our tool puts the power back in your hands, making sure you get the best deal based on your preferences.",
        image: info2,
    },
    {
        title: "AI-Powered Analysis",
        text: "We use AI to eliminate uncertainty in real estate pricing, providing a fair value estimate so you know exactly what a property is worth.",
        backText: "No more overpaying or falling for sales tactics. Our AI-driven analysis ensures you get an unbiased valuation, helping you make confident decisions and find your dream home at the right price.",
        image: info3,
    }
];

const howToUseCards = [
    {
        title: "Enter Property Details",
        text: "Manually input property details or provide a link to fetch data from a real estate listing.",
        backText: "Our system will extract and process the relevant data to evaluate the property for you.",
        image: how1,
    },
    {
        title: "Set Your Preferences",
        text: "Choose the criteria that matter most—whether it’s the location, size, price, or investment potential.",
        backText: "Based on your preferences, we personalize the analysis to provide the most relevant results.",
        image: how2,
    },
    {
        title: "AI-Powered Analysis",
        text: "Our AI analyzes the property using up-to-date market trends, comparable listings, and the property’s features to generate a fair price estimate.",
        backText: "We use powerful machine learning tools to ensure our valuations are accurate and reflective of the real estate market.",
        image: how3,
    }
];

export { howToUseCards, aboutUsCards };
