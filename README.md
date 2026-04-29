# Multi-Agent Auction System

A Java-based Multi-Agent System (MAS) that simulates an auction environment. The project uses the JADE (Java Agent Development Framework) to manage agents and features a Swing-based Graphical User Interface (GUI) to visualize and configure the auction in real-time.

## Features

- **Agent Communication:** Uses JADE to facilitate communication between the Seller agent and multiple Buyer agents.
- **Dynamic Configuration:** A dynamic Swing GUI allows users to configure seller parameters and add buyer agents at runtime.
- **Real-time Monitoring:** The interface displays the current auction price, active buyers, and tracks the leading bidder.
- **Winner Notification:** Visual notification within the GUI to announce the auction winner.

## Technologies Used

- **Java:** Core programming language.
- **JADE:** Provides the framework for agent lifecycle, behaviors, and standard communicative acts.
- **Java Swing:** Used to build the graphical user interface.

## Project Structure

- `src/MainLauncher.java`: Main entry point that initializes the JADE platform main container and launches the GUI.
- `src/AuctionGUI.java`: The primary desktop interface for user interaction and auction monitoring.
- `src/GuiAgent.java`: An interface agent bridging the JADE environment and the UI components.
- `src/SellerAgent.java`: The agent responsible for managing the auction rules and the bidding process.
- `src/BuyerAgent.java`: The participant agent that bids dynamically based on individual parameters.

## Getting Started

1. Ensure you have Java installed.
2. Verify that the JADE dependency located in the `lib` directory is added to your project's build path.
3. Compile the Java files in the `src` directory.
4. Execute `MainLauncher.java` to start the JADE runtime and open the interactive auction dashboard.
