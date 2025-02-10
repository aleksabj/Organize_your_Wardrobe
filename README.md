# Virtual Wardrobe Organizer

Welcome to the Virtual Wardrobe Organizer! This project is a JavaFX application that helps you organize your wardrobe, suggest outfits, and even recommend clothing based on the weather in your city.

## Features

- **View Wardrobe**: Browse through your wardrobe items.
- **Add Clothing Items**: Add new clothing items to your wardrobe.
- **Create Outfits**: Get outfit suggestions based on colour combinations.
- **Weather-Based Suggestions**: Get outfit recommendations based on the current weather in your city.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 11 or higher.
- **JavaFX**: Version 11 or higher.
- **IntelliJ IDEA**: Recommended IDE for Java development (I used this for developing this project).

## Libraries and Dependencies

The following libraries are required for this project:

- **JavaFX**: For building the user interface.
- **WeatherAPI**: For fetching weather data.

## Getting Started

1. **Clone the Repository**:
    ```sh
    git clone https://github.com/aleksabj/Organize_your_Wardrobe.git
    cd Organize_your_Wardrobe
    ```

2. **Set Up JavaFX**:
    - Download JavaFX from [Gluon](https://gluonhq.com/products/javafx/).
    - Follow the instructions to set up JavaFX in IntelliJ IDEA.

3. **Obtain WeatherAPI Key**:
    - Sign up at [WeatherAPI](https://www.weatherapi.com/).
    - After signing up, you will receive an API key.
    - Set the API key as an environment variable named `WeatherKey`.

4. **Run the Application**:
    - Open the project in IntelliJ IDEA.
    - Run the `Main` class located in `src/com/project/main/Main.java`.

## Project Structure

- `src/com/project/main/Main.java`: The main entry point of the application.
- `src/com/project/controllers/`: Contains all the controllers for handling UI interactions.
- `src/com/project/model/`: Contains the `ClothingItem` model class.
- `src/com/project/views/`: Contains the FXML file for the UI layout.
- `clothes/`: Contains sample clothing items for testing outfit suggestions with different colour combinations.

## Usage

1. **Start the Application**:
    - Run the `Main` class.
    - The main window will open with a welcome message and a "Start Organizing" button.

2. **Organize Your Wardrobe**:
    - Click "Start Organizing" to view options for managing your wardrobe.
    - Add new clothing items by clicking the "+" button.
    - View your wardrobe by clicking "View Your Wardrobe".
    - Create outfits by clicking "Create an Outfit".

3. **Weather-Based Outfit Suggestions**:
    - Click "Suggest Outfit for Weather".
    - Enter your city name to get weather-based outfit recommendations.

