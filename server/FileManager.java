package server;

import common.*;

import java.io.*;
import java.util.*;

/**
 * Handles reading and writing library data to/from CSV files.
 * Demonstrates: File Handling, Data Structures (ArrayList, HashMap)
 */
public class FileManager {

    private static final String DATA_FILE = "data/library_data.csv";
    private static final String LOG_FILE  = "data/borrow_log.txt";

    /**
     * Load all library items from the CSV file.
     * Returns a Map<itemId, LibraryItem> for O(1) lookup.
     */
    public static Map<String, LibraryItem> loadItems() {
        Map<String, LibraryItem> items = new LinkedHashMap<>();
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            // Seed with sample data on first run
            items = seedData();
            saveItems(items);
            return items;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                LibraryItem item = parseLine(line);
                if (item != null) {
                    items.put(item.getItemId(), item);
                }
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Error reading data file: " + e.getMessage());
        }

        return items;
    }

    /**
     * Save all library items back to the CSV file.
     */
    public static void saveItems(Map<String, LibraryItem> items) {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (LibraryItem item : items.values()) {
                pw.println(item.toCSV());
            }
        } catch (IOException e) {
            System.err.println("[FileManager] Error saving data: " + e.getMessage());
        }
    }

    /**
     * Append a transaction to the borrow log.
     */
    public static void logTransaction(String userId, String action, String itemId, String title) {
        new File("data").mkdirs();
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                   .format(new java.util.Date());
            pw.println(timestamp + " | " + userId + " | " + action + " | " + itemId + " | " + title);
        } catch (IOException e) {
            System.err.println("[FileManager] Error writing log: " + e.getMessage());
        }
    }

    /**
     * Parse a CSV line and return the appropriate LibraryItem subclass.
     * Format: itemId,type,title,author,year,available,[extra fields...]
     */
    private static LibraryItem parseLine(String line) {
        try {
            String[] p = line.split(",");
            String id    = p[0].trim();
            String type  = p[1].trim();
            String title = p[2].trim();
            String auth  = p[3].trim();
            int    year  = Integer.parseInt(p[4].trim());
            boolean avail = Boolean.parseBoolean(p[5].trim());

            LibraryItem item = null;

            switch (type) {
                case "Book": {
                    // extra: genre, pages, borrowedBy
                    String genre = p[6].trim();
                    int pages    = Integer.parseInt(p[7].trim());
                    Book book    = new Book(id, title, auth, year, genre, pages);
                    book.setAvailable(avail);
                    String borrowedBy = p[8].trim();
                    if (!borrowedBy.equals("none")) {
                        // Mark as borrowed without re-calling borrow() to preserve state
                        book.setAvailable(false);
                        try {
                            java.lang.reflect.Field f = Book.class.getDeclaredField("borrowedBy");
                            f.setAccessible(true);
                            f.set(book, borrowedBy);
                        } catch (Exception ignored) {}
                    }
                    item = book;
                    break;
                }
                case "EBook": {
                    String genre = p[6].trim();
                    int pages    = Integer.parseInt(p[7].trim());
                    EBook eb     = new EBook(id, title, auth, year, genre, pages,
                                            p[9].trim(), Double.parseDouble(p[10].trim()));
                    eb.setAvailable(avail);
                    item = eb;
                    break;
                }
                case "Magazine": {
                    String cat   = p[6].trim();
                    int issue    = Integer.parseInt(p[7].trim());
                    item         = new Magazine(id, title, auth, year, cat, issue);
                    break;
                }
            }
            return item;
        } catch (Exception e) {
            System.err.println("[FileManager] Skipping bad line: " + line);
            return null;
        }
    }

    /**
     * Seed initial sample data so the project works out of the box.
     */
    private static Map<String, LibraryItem> seedData() {
        Map<String, LibraryItem> items = new LinkedHashMap<>();

        items.put("B001", new Book("B001", "Clean Code",          "Robert Martin", 2008, "Programming", 464));
        items.put("B002", new Book("B002", "The Great Gatsby",    "F. Scott Fitzgerald", 1925, "Fiction", 180));
        items.put("B003", new Book("B003", "Dune",                "Frank Herbert",  1965, "Sci-Fi",  412));
        items.put("B004", new Book("B004", "Design Patterns",     "GoF",            1994, "Programming", 395));
        items.put("B005", new Book("B005", "1984",                "George Orwell",  1949, "Fiction", 328));

        items.put("E001", new EBook("E001", "Head First Java",    "Kathy Sierra",   2005, "Programming", 688, "PDF",  12.5));
        items.put("E002", new EBook("E002", "The Pragmatic Programmer", "Hunt & Thomas", 1999, "Programming", 352, "EPUB", 8.2));

        items.put("M001", new Magazine("M001", "National Geographic", "Various", 2024, "Science",  201));
        items.put("M002", new Magazine("M002", "Time",                "Various", 2024, "News",     455));
        items.put("M003", new Magazine("M003", "IEEE Spectrum",       "IEEE",    2024, "Technology", 88));

        return items;
    }
}
