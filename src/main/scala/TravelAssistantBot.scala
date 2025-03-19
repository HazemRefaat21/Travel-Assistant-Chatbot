// Import necessary libraries for reading files and handling errors
import scala.io.Source
import scala.util.{Try, Success, Failure}
import java.io.{FileWriter, BufferedWriter, File}

// Define a case class to represent travel information
case class TravelInfo(destination: String, accommodationType: String, cost: Double, transport: String)

// Define a class to manage user preferences
class UserPreferences {
  // Private variable to store the user's preference
  private var preference: Option[String] = None

  // Method to set the user's preference
  def setPreference(pref: String): Unit = {
    preference = Some(pref)
  }

  // Method to clear the user's preference
  def clearPreference(): Unit = {
    preference = None
  }

  // Method to get the current user preference
  def getPreference: Option[String] = preference
}

// Object to encapsulate the travel assistant chatbot functionality
object TravelAssistantChatbot {
  // Create an instance of UserPreferences to manage user preferences
  val userPreferences = new UserPreferences()
  // Variable to hold the list of travel data
  var travelData: List[TravelInfo] = List()

  // Method to greet the user
  def greetUser(): String = "\t\t\tWelcome to the travel assistant bot! how can i assist you? If you want any explanation just type \"help\" "

  // Method to show the program explanation to the user
  def showProgramExplanation(): String = {
    "This bot helps you plan your trips. You can ask for travel recommendations based on your budget,\n" +
      "book accommodations, or just explore destinations. Start by typing your request or 'help' to see what I can do!\n" +
      "The Program is a chatbot system that does the searching for the trip you require in our database and helps you book a trip to any desired place,\n" +
      "it may happen that the chatbot sometimes get things wrong from the user but it will sure help you book your trip and recommend a trip that you will like"
  }

  // Method to explore available destinations
  def exploreDestinations(): String = {
    val destinationsInfo = travelData.map { info =>
      s"Destination: ${info.destination}, Accommodation Type: ${info.accommodationType}, Cost: ${info.cost}, Transport: ${info.transport}"
    }.mkString("\n")

    if (destinationsInfo.isEmpty) {
      "No destinations available."
    } else {
      s"All destinations:\n$destinationsInfo"
    }
  }

  // Method to show all trips based on user preferences
  def showAllTrips(): String = {
    if (travelData.isEmpty) {
      "No trips available."
    } else {
      userPreferences.getPreference match {
        case Some(userLocation) if userLocation.nonEmpty =>
          val locationTrips = travelData.filter(_.destination.equalsIgnoreCase(userLocation))
          if (locationTrips.isEmpty) {
            s"No trips available in $userLocation."
          } else {
            val tripsInfo = locationTrips.map { info =>
              s"${info.destination}: ${info.accommodationType}, costs ${info.cost}, transport by ${info.transport}."
            }.mkString("\n")
            val response = s"$tripsInfo\nWould you like to book any of these trips? (yes/no): "
            scala.io.StdIn.readLine(response).toLowerCase match {
              case "yes" | "y" => initiateBooking()
              case _ => "Feel free to ask more about other services!"
            }
          }
        case _ => "Please set your preferred location first."
      }
    }
  }

  // Method to initiate the booking process
  def initiateBooking(destination: Option[String] = None, cost: Option[Double] = None): String = {
    // Get the destination from the user if not provided
    val dest = destination.getOrElse {
      println("Enter the destination you would like to book:")
      scala.io.StdIn.readLine().trim
    }
    // Filter available trips based on the destination
    val availableTrips = travelData.filter(_.destination.equalsIgnoreCase(dest))
    if (availableTrips.isEmpty) {
      return s"No Trips Available To $dest Please try again."
    } else {
      println(s"Available trips to $dest:")
      availableTrips.foreach { trip =>
        println(s"Cost: ${trip.cost}, Accommodation: ${trip.accommodationType}, Transport: ${trip.transport}")
      }
    }

    // Get the cost from the user if not provided
    val price = cost.getOrElse {
      println(s"Enter the cost of the trip to $dest or type 'cancel' to stop:")
      val input = scala.io.StdIn.readLine().trim
      if (input.equalsIgnoreCase("cancel")) {
        return "Booking process canceled."
      }
      Try(input.toDouble) match {
        case Success(value) => value
        case Failure(_) =>
          println("Invalid input. Please enter a valid number.")
          return initiateBooking(Some(dest))
      }
    }

    // Find the trip that matches the destination and cost
    travelData.find(t => t.destination.equalsIgnoreCase(dest) && t.cost == price) match {
      case Some(trip) =>
        val name = scala.io.StdIn.readLine("Please enter your name: ")
        val number = scala.io.StdIn.readLine("Please enter your phone number: ")
        saveDetails(name, number, trip, "booking")
        userPreferences.clearPreference() // Clear preference after booking
        s"Booking confirmed for ${trip.destination} at ${trip.cost}. Thank you for booking with us!"
      case None =>
        println("Invalid destination or price. Please enter the exact destination and price for booking.")
        initiateBooking(Some(dest))
    }
  }

  // Method to handle user input and generate appropriate responses
  def handleUserInput(input: String): String = {
    val keywords = extractKeywords(input)

    // Handle greeting input
    if (isGreeting(input)) {
      return generateRandomGreeting(getUserName(input))
    }

    val maxPrice = extractMaxPriceFromInput(keywords)

    // Handle booking request
    if (isBookRequest(input)) {
      val destination = destinationFromKeywords(keywords)
      if (destination.nonEmpty && maxPrice.isDefined) {
        return initiateBooking(Some(destination), maxPrice)
      } else if (destination.nonEmpty) {
        return initiateBooking(Some(destination))
      } else {
        println("Please provide the destination and/or price for booking.")
        return initiateBooking()
      }
    }

    // Handle recommendation request
    if (keywords.contains("recommend")) {
      val dest = destinationFromKeywords(keywords)
      if (maxPrice.isDefined && dest.nonEmpty) {
        return recommendDestination(maxPrice.get, dest)
      } else {
        return "Please provide both a destination and a valid price range to recommend a destination."
      }
    }

    // Handle explore request
    if (keywords.exists(Set("explore"))) {
      return exploreDestinations()
    }

    // Handle show all trips request
    if (keywords.exists(Set("show", "all"))) {
      return showAllTrips()
    }

    // Handle multiple destinations request
    val destinations = extractMultipleDestinations(keywords)
    if (destinations.nonEmpty) {
      return showMultipleDestinations(destinations)
    }

    // Handle search destination request
    findDestinationInData(keywords) match {
      case Some(dest) =>
        userPreferences.setPreference(dest) // Update preference with destination
        searchDestination(userPreferences)
      case None =>
        recommendDestinationBasedOnLocation(input)
    }
  }

  // Method to extract multiple destinations from the user's input
  def extractMultipleDestinations(keywords: List[String]): List[String] = {
    keywords.filter(kw => travelData.exists(_.destination.toLowerCase.contains(kw.toLowerCase)))
  }

  // Method to show multiple destinations
  def showMultipleDestinations(destinations: List[String]): String = {
    val results = destinations.flatMap { dest =>
      travelData.filter(_.destination.equalsIgnoreCase(dest))
    }

    if (results.isEmpty) "Invalid."
    else results.map(info => s"${info.destination}: ${info.accommodationType}, costs ${info.cost}, transport by ${info.transport}.").mkString("\n")
  }

  // Method to find a destination in the travel data based on keywords
  def findDestinationInData(keywords: List[String]): Option[String] = {
    keywords.find(kw => travelData.exists(_.destination.toLowerCase.contains(kw.toLowerCase)))
  }

  // Method to check if the input is a greeting
  def isGreeting(input: String): Boolean = {
    val greetings = Set("hi", "hello", "hey", "howdy", "hi there", "hello there", "good morning", "good afternoon", "good evening", "what's up")
    extractKeywords(input).exists(greetings.contains)
  }

  // Method to generate a random greeting
  def generateRandomGreeting(name: Option[String]): String = {
    val greetings = Array("Hi there!", "Hello!", "Hey!", "Howdy!", "Nice to see you!")
    val greeting = greetings(scala.util.Random.nextInt(greetings.length))
    name match {
      case Some(n) => s"$greeting, $n!"
      case None => greeting
    }
  }

  // Method to extract keywords from the user's input
  def extractKeywords(input: String): List[String] = {
    val unwantedWords = Set("I","i","can", "you", "assist", "today", "with", "how", "help", "me", "please", "and", "or", "a", "an", "the", "to", "for", "of", "in", "on", "at", "by", "with", "is", "are","travel","want","to")
    input.trim.toLowerCase.split("\\s+").filterNot(unwantedWords.contains).toList
  }

  // Method to extract the maximum price from the user's input
  def extractMaxPriceFromInput(keywords: List[String]): Option[Double] = {
    val numericTokens = keywords.flatMap { token =>
      Try(token.toDouble).toOption
    }
    numericTokens.headOption
  }

  // Method to extract the price direction (cheaper or more expensive) from the user's input
  def extractPriceDirection(keywords: List[String]): Option[String] = {
    val directionWords = Map("cheaper" -> "less", "cheap" -> "less", "less" -> "less", "lower" -> "less", "more expensive" -> "more", "expensive" -> "more", "higher" -> "more" , "more" -> "more" )
    keywords.flatMap(word => directionWords.get(word)).headOption
  }

  // Method to check if the input is a booking request
  def isBookRequest(input: String): Boolean = {
    val bookWords = Set("book", "reserve", "confirm")
    extractKeywords(input).exists(bookWords.contains)
  }

  // Method to handle the booking request
  def handleBookingRequest(bookingType: String): String = {
    userPreferences.getPreference match {
      case Some(destination) =>
        val matchingDestinations = travelData.filter(_.destination.equalsIgnoreCase(destination))
        if (matchingDestinations.isEmpty) {
          "Destination not found."
        } else {
          matchingDestinations.foreach { info =>
            println(s"${info.destination}: ${info.accommodationType}, costs ${info.cost}, transport by ${info.transport}.")
          }
          val price = scala.io.StdIn.readLine(s"Enter the price for $bookingType $destination: ").toDouble
          matchingDestinations.find(_.cost == price) match {
            case Some(info) =>
              val name = scala.io.StdIn.readLine("Please enter your name: ")
              val number = scala.io.StdIn.readLine("Please enter your phone number: ")
              val confirmation = scala.io.StdIn.readLine(s"Are you sure you want to $bookingType $destination for ${info.cost} (${info.accommodationType}, transport by ${info.transport})? (yes/no): ")
              confirmation.toLowerCase match {
                case "yes" | "y" =>
                  saveDetails(name, number, info, bookingType)
                  userPreferences.clearPreference()
                  s"Your $bookingType at $destination has been confirmed."
                case "no" | "n" => "Booking canceled."
                case _ => "Invalid response. Please respond with 'yes' or 'no'."
              }
            case None => "Invalid response. Try again."
          }
        }
      case None => "You didn't choose a place to book."
    }
  }

  // Method to save booking details to a file
  def saveDetails(name: String, number: String, info: TravelInfo, bookingType: String): Unit = {
    val writer = new BufferedWriter(new FileWriter("bookings.txt", true))
    try {
      writer.newLine()
      writer.write(s"Name: $name")
      writer.newLine()
      writer.write(s"Phone number: $number")
      writer.newLine()
      writer.write(s"Destination: ${info.destination}")
      writer.newLine()
      writer.write(s"Accommodation Type: ${info.accommodationType}")
      writer.newLine()
      writer.write(s"Cost: ${info.cost}")
      writer.newLine()
      writer.write(s"Transport: ${info.transport}")
      writer.newLine()
      writer.write(s"Booking Type: $bookingType")
    } finally {
      writer.close()
    }
  }

  // Method to extract the user's name from the input
  def getUserName(input: String): Option[String] = {
    val nameKeywordIndex = input.indexOf("I am")
    if (nameKeywordIndex != -1) {
      val nameStartIndex = nameKeywordIndex + "I am".length
      Some(input.substring(nameStartIndex).trim)
    } else {
      None
    }
  }

  // Method to read travel data from a CSV file
  def readCSVFile(filePath: String): Unit = {
    try {
      val source = Source.fromFile(filePath)
      travelData = source.getLines().drop(1).flatMap { line =>
        val parts = line.split(",").map(_.trim)
        if (parts.length >= 4) Try(TravelInfo(parts(0), parts(1), parts(2).toDouble, parts(3))).toOption
        else {
          println(s"Skipping invalid line: $line")
          None
        }
      }.toList
      source.close()
    } catch {
      case e: Exception => println("Error reading file: " + e.getMessage)
    }
  }

  // Method to search for a destination based on user preferences
  def searchDestination(userPreferences: UserPreferences): String = {
    val results = userPreferences.getPreference match {
      case Some(pref) =>
        val destination = pref.toLowerCase
        travelData.filter(_.destination.toLowerCase.contains(destination))
      case None => Seq.empty // Handle the case where userPreferences.getPreference returns None
    }
    if (results.isEmpty) "No results found."
    else results.map(info => s"${info.destination}: ${info.accommodationType}, costs ${info.cost}, transport by ${info.transport}.").mkString("\n")
  }

  // Method to recommend a destination based on the maximum price and destination
  def recommendDestination(maxPrice: Double, destination: String): String = {
    val filteredResults = travelData.filter { info =>
      info.destination.equalsIgnoreCase(destination) && info.cost < maxPrice
    }

    if (filteredResults.isEmpty) {
      s"No destinations found within the specified price range in $destination."
    } else {
      val recommendedDestinations = filteredResults.map { info =>
        s"${info.destination}: ${info.accommodationType}, costs ${info.cost}, transport by ${info.transport}."
      }.mkString("\n")

      println(s"Based on your preference, here are the options:\n$recommendedDestinations")
      val wantToBook = scala.io.StdIn.readLine("Would you like to book any of these recommendations? (yes/no): ")
      wantToBook.toLowerCase match {
        case "yes" | "y" =>
          initiateBooking()
        case "no" | "n" => "Okay, feel free to ask for more recommendations!"
        case _ => "Invalid response. Please respond with 'yes' or 'no'."
      }
    }
  }

  // Method to extract the destination from keywords
  def destinationFromKeywords(keywords: List[String]): String = {
    keywords.dropWhile(_ != "recommend").drop(1).headOption.getOrElse("").toLowerCase
  }

  // Method to recommend a destination based on the user's location and input
  def recommendDestinationBasedOnLocation(input: String): String = {
    val keywords = extractKeywords(input)

    if (keywords.exists(Set("recommend", "suggest", "suggestion"))) {
      val locationIndex = keywords.indexWhere(Set("visit", "go", "destination").contains) + 1
      if (locationIndex < keywords.length) {
        val location = keywords(locationIndex)
        val maxPriceIndex = keywords.indexWhere(_.matches("\\d+"))
        if (maxPriceIndex != -1) {
          val maxPrice = keywords(maxPriceIndex).toDouble
          val similarDestinations = travelData.filter { info =>
            info.destination.equalsIgnoreCase(location) && info.cost <= maxPrice
          }
          if (similarDestinations.nonEmpty) {
            val recommendedDestinations = similarDestinations.map { info =>
              s"${info.destination}: ${info.accommodationType}, costs ${info.cost}, transport by ${info.transport}."
            }.mkString("\n")

            userPreferences.setPreference(location)
            return s"Based on your preference, here are the options:\n$recommendedDestinations\nWould you like to book any of these recommendations? (yes/no): "
          } else {
            return s"No similar destinations found within the specified price range in $location."
          }
        } else {
          return "Please provide a valid price range to recommend a destination."
        }
      } else {
        return "Invalid location. Please provide a valid location."
      }
    } else {
      return "Invalid Response, Please Try Again."
    }
  }

  // Main method to run the Travel Assistant Chatbot
  def main(args: Array[String]): Unit = {
    println(greetUser())
    readCSVFile("C:/Users/ASUS/Desktop/Project Final/Travel details dataset.csv")
    var continueLoop = true
    while (continueLoop) {
      val userInput = scala.io.StdIn.readLine("User: ")
      if (userInput.toLowerCase == "exit" || userInput.toLowerCase == "bye" || userInput.toLowerCase == "thanks" || userInput.toLowerCase == "close" || userInput.toLowerCase == "good bye") {
        continueLoop = false
        println("Exiting...")
      }
      if (userInput.toLowerCase.contains( "help" ) || userInput.toLowerCase.contains( "explain")) {
        println(showProgramExplanation())
      } else {
        val response = handleUserInput(userInput)
        println(response)
      }
    }
  }
}
