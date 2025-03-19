
# TravelAssistantChatbot

## Overview
This Scala project, TravelAssistantChatbot, is designed to assist users in planning their travels by providing recommendations based on user preferences, booking accommodations, and exploring various destinations.

## Features
- **User Preferences Management**: Handles user preferences throughout the session, allowing for a personalized experience.
- **Travel Information**: Utilizes data encapsulated in the `TravelInfo` class, including destination, accommodation type, cost, and transport details.
- **Interactive Commands**:
  - Greeting the user.
  - Showing available trips.
  - Initiating bookings based on user input.
  - Handling various user inputs to navigate through the bot's features.

## Components
- `TravelAssistantChatbot`: Main object handling the logic of user interactions and responses.
- `UserPreferences`: Manages and stores user preferences.
- `TravelInfo`: Case class that represents the structure of travel data.

## Methods
- `greetUser()`: Greets the user upon starting the interaction.
- `showProgramExplanation()`: Provides a detailed explanation of how the bot operates.
- `showAllTrips()`: Displays all available trips, with a focus on trips to Dubai.
- `initiateBooking()`: Handles the booking process based on user-selected trips.
- `handleUserInput()`: Processes and responds to user inputs dynamically.

## Usage
Run the main method of the `TravelAssistantChatbot` object. The bot starts with a greeting and provides instructions on how to interact with it. The user can then enter commands to explore trips, get recommendations, or start booking processes.

## Dependencies
- Scala 3
- JDK 11

## Installation
To run this project, you will need Scala and Java installed on your machine. Clone the repository, navigate to the project directory, and run the following command:
```
scala TravelAssistantChatbot.scala
```
