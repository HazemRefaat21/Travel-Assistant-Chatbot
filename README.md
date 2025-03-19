# Travel Assistant Chatbot

## 📌 Overview
The **Travel Assistant Chatbot** is a **Scala-based** rule-based chatbot designed to help users plan their trips efficiently. It provides travel recommendations, manages bookings, and offers destination insights based on user inputs. Developed as a **university project**, this chatbot enhances user interaction by handling structured queries.

## ✨ Features
- 🏨 **Travel Booking**: Allows users to book trips based on destination and budget.
- 🌍 **Explore Destinations**: Provides information on available travel options.
- 🎯 **User Preferences**: Stores and manages user-selected travel preferences.
- 🛠 **Interactive Query Handling**: Responds to user commands with relevant travel details.
- 💾 **CSV Data Processing**: Reads and filters travel data from a structured dataset.

## 🚀 Technologies Used
- **Scala**
- **File Handling (CSV Processing)**
- **Command-Line Interaction**
- **Rule-Based Query Processing**

## 🛠 Installation & Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/travel-assistant-chatbot.git
   ```
2. Navigate to the project directory:
   ```bash
   cd travel-assistant-chatbot
   ```
3. Ensure you have **Scala** installed. If not, install it:
   ```bash
   sudo apt install scala  # Linux
   brew install scala      # macOS
   winget install scala    # Windows
   ```
4. Compile and run the chatbot:
   ```bash
   scala src/main/scala/TravelAssistantBot.scala
   ```

## 📖 Usage
- Start the chatbot, and you will be greeted with a welcome message.
- You can type queries like:
  - `show all trips` → Displays available trips.
  - `book Paris` → Initiates a booking for a trip to Paris.
  - `explore destinations` → Lists all available travel destinations.
  - `help` → Shows available commands and usage instructions.

## 📂 Project Structure
```
📁 travel-assistant-chatbot
├── 📂 src
│   ├── 📂 main
│   │   ├── 📂 scala
│   │   │   ├── TravelAssistantBot.scala
├── 📄 README.md
├── 📄 build.sbt
├── 📄 Travel details dataset.csv
```

## 💡 Future Enhancements
- Implement **Natural Language Processing (NLP)** for smarter query handling.
- Integrate with real-time **travel APIs** for dynamic booking options.
- Add a **GUI version** for an improved user experience.

## 📜 License
This project is licensed under the **MIT License**.

---
Made with ❤️ as part of a **university project**.

