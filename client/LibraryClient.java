package client;

import common.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Client application for the Online Library System.
 * Connects to the server, sends requests, and displays responses.
 * Demonstrates: Sockets (client side), Exception Handling, Input Validation
 *
 * HOW TO RUN (in a separate terminal AFTER starting the server):
 *   cd LibrarySystem
 *   java -cp out client.LibraryClient
 */
public class LibraryClient {

    private static final String HOST = "localhost";
    private static final int    PORT = 5000;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Get username
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║  Online Library System - Client    ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Enter your username: ");
        String userId = scanner.nextLine().trim();

        // Validate username
        if (userId.isEmpty()) {
            System.out.println("Username cannot be empty. Exiting.");
            return;
        }

        System.out.println("\nConnecting to " + HOST + ":" + PORT + "...");

        // Exception handling: wrap entire connection in try-catch
        try (
            Socket socket                 = new Socket(HOST, PORT);
            ObjectOutputStream out        = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream  in         = new ObjectInputStream(socket.getInputStream())
        ) {
            out.flush();
            System.out.println("Connected! Welcome, " + userId + "\n");
            printMenu();

            boolean running = true;
            while (running) {
                System.out.print("\n> Choose option: ");

                // Validate numeric input
                String input = scanner.nextLine().trim();
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number 1-6.");
                    continue;
                }

                LibraryRequest request = null;

                switch (choice) {
                    case 1:
                        System.out.print("  Search query: ");
                        String query = scanner.nextLine().trim();
                        if (query.isEmpty()) { System.out.println("Query cannot be empty."); continue; }
                        request = new LibraryRequest(LibraryRequest.SEARCH, query, userId);
                        break;

                    case 2:
                        System.out.print("  Enter Item ID to borrow: ");
                        String borrowId = scanner.nextLine().trim().toUpperCase();
                        request = new LibraryRequest(LibraryRequest.BORROW, borrowId, userId);
                        break;

                    case 3:
                        System.out.print("  Enter Item ID to return: ");
                        String returnId = scanner.nextLine().trim().toUpperCase();
                        request = new LibraryRequest(LibraryRequest.RETURN, returnId, userId);
                        break;

                    case 4:
                        request = new LibraryRequest(LibraryRequest.LIST_ALL, "", userId);
                        break;

                    case 5:
                        request = new LibraryRequest(LibraryRequest.LIST_MINE, "", userId);
                        break;

                    case 6:
                        request = new LibraryRequest(LibraryRequest.EXIT, "", userId);
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid option. Choose 1-6.");
                        continue;
                }

                // Send request to server
                out.writeObject(request);
                out.flush();

                // Receive and display response
                LibraryResponse response = (LibraryResponse) in.readObject();
                displayResponse(response);
            }

        } catch (ConnectException e) {
            System.err.println("\n[Error] Cannot connect to server. Is it running on port " + PORT + "?");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("\n[Error] Connection issue: " + e.getMessage());
        }

        System.out.println("\nThank you for using the Online Library System. Goodbye!");
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("┌─────────────────────────────┐");
        System.out.println("│         MAIN MENU           │");
        System.out.println("├─────────────────────────────┤");
        System.out.println("│  1. Search catalog          │");
        System.out.println("│  2. Borrow item             │");
        System.out.println("│  3. Return item             │");
        System.out.println("│  4. List all items          │");
        System.out.println("│  5. My borrowed items       │");
        System.out.println("│  6. Exit                    │");
        System.out.println("└─────────────────────────────┘");
    }

    private static void displayResponse(LibraryResponse response) {
        System.out.println();
        if (response.isSuccess()) {
            System.out.println("✓ " + response.getMessage());
        } else {
            System.out.println("✗ " + response.getMessage());
        }

        // Print list results if any
        if (response.getResults() != null && !response.getResults().isEmpty()) {
            System.out.println("─".repeat(60));
            for (String result : response.getResults()) {
                System.out.println("  " + result);
            }
            System.out.println("─".repeat(60));
        }
    }
}
