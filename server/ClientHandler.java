package server;

import common.*;

import java.io.*;
import java.net.*;

/**
 * Handles communication with a single connected client.
 * Each ClientHandler runs in its own Thread — enables multi-client support.
 * Demonstrates: Multi-threading (BONUS), Sockets, Exception Handling
 */
public class ClientHandler implements Runnable {

    private Socket         clientSocket;
    private LibraryService service;
    private String         clientAddress;

    public ClientHandler(Socket clientSocket, LibraryService service) {
        this.clientSocket  = clientSocket;
        this.service       = service;
        this.clientAddress = clientSocket.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        System.out.println("[Server] Client connected: " + clientAddress +
                           " | Thread: " + Thread.currentThread().getName());

        // Try-with-resources ensures streams are always closed
        try (
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream  in  = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            out.flush();

            boolean running = true;
            while (running) {
                // Read a request from the client
                LibraryRequest  request  = (LibraryRequest)  in.readObject();
                LibraryResponse response;

                System.out.println("[Server] Received: " + request + " from " + clientAddress);

                // Dispatch to service based on action
                switch (request.getAction()) {
                    case LibraryRequest.SEARCH:
                        response = service.search(request.getQuery(), request.getUserId());
                        break;
                    case LibraryRequest.BORROW:
                        response = service.borrow(request.getQuery(), request.getUserId());
                        break;
                    case LibraryRequest.RETURN:
                        response = service.returnItem(request.getQuery(), request.getUserId());
                        break;
                    case LibraryRequest.LIST_ALL:
                        response = service.listAll(request.getUserId());
                        break;
                    case LibraryRequest.LIST_MINE:
                        response = service.listMine(request.getUserId());
                        break;
                    case LibraryRequest.EXIT:
                        response = new LibraryResponse(true, "Goodbye!");
                        out.writeObject(response);
                        out.flush();
                        running = false;
                        continue;
                    default:
                        response = new LibraryResponse(false, "Unknown command: " + request.getAction());
                }

                // Send response back
                out.writeObject(response);
                out.flush();
            }

        } catch (EOFException e) {
            System.out.println("[Server] Client disconnected: " + clientAddress);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Server] Error with client " + clientAddress + ": " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
            System.out.println("[Server] Connection closed: " + clientAddress);
        }
    }
}
