package server;

import java.io.*;
import java.net.*;

/**
 * Main Library Server — listens for client connections and spawns threads.
 * Demonstrates: Sockets (ServerSocket), Multi-threading (BONUS), Exception Handling
 *
 * HOW TO RUN:
 *   cd LibrarySystem
 *   javac -d out common/*.java server/*.java
 *   java -cp out server.LibraryServer
 */
public class LibraryServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        // Create the shared service (one instance shared across all threads)
        LibraryService service = new LibraryService();

        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║   Online Library System - Server   ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  Listening on port " + PORT + "             ║");
        System.out.println("║  Press Ctrl+C to stop              ║");
        System.out.println("╚════════════════════════════════════╝");

        // Exception handling: catch connection errors gracefully
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Main accept loop — keeps the server alive
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();  // blocks until client connects

                    // BONUS: Each client gets its own thread (multi-client support)
                    Thread thread = new Thread(new ClientHandler(clientSocket, service));
                    thread.setName("Client-" + clientSocket.getPort());
                    thread.setDaemon(true);
                    thread.start();

                    System.out.println("[Server] Active threads: " + Thread.activeCount());

                } catch (IOException e) {
                    System.err.println("[Server] Error accepting client: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("[Server] FATAL — Could not start server on port " + PORT + ": " + e.getMessage());
            System.exit(1);
        }
    }
}
