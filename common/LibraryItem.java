package common;

/**
 * Abstract base class for all library items.
 * Demonstrates: Abstract class, Encapsulation, Inheritance base
 */
public abstract class LibraryItem {

    // Encapsulation: private fields with getters/setters
    private String itemId;
    private String title;
    private String author;
    private int year;
    private boolean available;

    // Constructor
    public LibraryItem(String itemId, String title, String author, int year) {
        this.itemId    = itemId;
        this.title     = title;
        this.author    = author;
        this.year      = year;
        this.available = true;
    }

    // --- Abstract methods (must be implemented by subclasses) ---
    public abstract String getType();
    public abstract String getDetails();

    // --- Polymorphism: overrideable display method ---
    public String getSummary() {
        return "[" + getType() + "] " + title + " by " + author + " (" + year + ")";
    }

    // --- Getters ---
    public String getItemId()  { return itemId; }
    public String getTitle()   { return title; }
    public String getAuthor()  { return author; }
    public int    getYear()    { return year; }
    public boolean isAvailable() { return available; }

    // --- Setters ---
    public void setAvailable(boolean available) { this.available = available; }

    // --- CSV serialization helper ---
    public String toCSV() {
        return itemId + "," + getType() + "," + title + "," + author + "," + year + "," + available;
    }
}
