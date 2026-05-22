package server;

import common.*;

import java.util.*;

/**
 * Core business logic for the library.
 * Demonstrates: Data Structures (Map, List), Polymorphism via interface
 */
public class LibraryService {

    // HashMap for O(1) lookup by ID — Data Structure
    private Map<String, LibraryItem> catalog;

    public LibraryService() {
        this.catalog = FileManager.loadItems();
        System.out.println("[Server] Loaded " + catalog.size() + " items from storage.");
    }

    /**
     * Search items by query string.
     * Polymorphism: calls matchesQuery() on any Searchable item.
     */
    public LibraryResponse search(String query, String userId) {
        if (query == null || query.trim().isEmpty()) {
            return new LibraryResponse(false, "Please provide a search term.", null);
        }

        List<String> results = new ArrayList<>();
        for (LibraryItem item : catalog.values()) {
            if (item instanceof Searchable) {
                Searchable s = (Searchable) item;
                if (s.matchesQuery(query)) {
                    String status = item.isAvailable() ? "✓ Available" : "✗ Borrowed";
                    results.add(item.getItemId() + " | " + item.getSummary() + " | " + status);
                }
            }
        }

        if (results.isEmpty()) {
            return new LibraryResponse(false, "No results found for: " + query, null);
        }
        return new LibraryResponse(true, "Found " + results.size() + " result(s):", results);
    }

    /**
     * Borrow an item by ID.
     * Polymorphism: only Borrowable items can be borrowed.
     */
    public synchronized LibraryResponse borrow(String itemId, String userId) {
        LibraryItem item = catalog.get(itemId);

        if (item == null) {
            return new LibraryResponse(false, "Item not found: " + itemId);
        }

        if (!(item instanceof Borrowable)) {
            return new LibraryResponse(false, item.getTitle() + " is a reference item and cannot be borrowed.");
        }

        Borrowable b = (Borrowable) item;
        if (b.borrow(userId)) {
            FileManager.saveItems(catalog);
            FileManager.logTransaction(userId, "BORROW", itemId, item.getTitle());
            return new LibraryResponse(true, "Successfully borrowed: " + item.getTitle());
        } else {
            return new LibraryResponse(false, "Item is currently unavailable. Borrowed by: " + b.getBorrowedBy());
        }
    }

    /**
     * Return a borrowed item.
     */
    public synchronized LibraryResponse returnItem(String itemId, String userId) {
        LibraryItem item = catalog.get(itemId);

        if (item == null) {
            return new LibraryResponse(false, "Item not found: " + itemId);
        }

        if (!(item instanceof Borrowable)) {
            return new LibraryResponse(false, "This item cannot be returned (it's a reference item).");
        }

        Borrowable b = (Borrowable) item;
        if (b.returnItem(userId)) {
            FileManager.saveItems(catalog);
            FileManager.logTransaction(userId, "RETURN", itemId, item.getTitle());
            return new LibraryResponse(true, "Successfully returned: " + item.getTitle());
        } else {
            return new LibraryResponse(false, "You did not borrow this item, or it is already available.");
        }
    }

    /**
     * List all items in the catalog.
     * Polymorphism: getSummary() called on each item returns type-specific format.
     */
    public LibraryResponse listAll(String userId) {
        List<String> results = new ArrayList<>();
        for (LibraryItem item : catalog.values()) {
            String status = item.isAvailable() ? "✓ Available" : "✗ Borrowed";
            results.add(item.getItemId() + " | " + item.getSummary() + " | " + status);
        }
        return new LibraryResponse(true, "Total items: " + results.size(), results);
    }

    /**
     * List items currently borrowed by a specific user.
     */
    public LibraryResponse listMine(String userId) {
        List<String> results = new ArrayList<>();
        for (LibraryItem item : catalog.values()) {
            if (item instanceof Borrowable) {
                Borrowable b = (Borrowable) item;
                if (userId.equals(b.getBorrowedBy())) {
                    results.add(item.getItemId() + " | " + item.getSummary());
                }
            }
        }
        if (results.isEmpty()) {
            return new LibraryResponse(true, "You have no borrowed items.", null);
        }
        return new LibraryResponse(true, "Your borrowed items (" + results.size() + "):", results);
    }
}
