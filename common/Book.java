package common;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a physical Book.
 * Demonstrates: Inheritance, Polymorphism (method overriding), Interface implementation
 */
public class Book extends LibraryItem implements Searchable, Borrowable {

    private String genre;
    private int    pages;
    private String borrowedBy; // stores the userId who borrowed it

    public Book(String itemId, String title, String author, int year, String genre, int pages) {
        super(itemId, title, author, year); // call parent constructor
        this.genre     = genre;
        this.pages     = pages;
        this.borrowedBy = null;
    }

    // --- Abstract method implementations ---

    @Override
    public String getType() {
        return "Book";
    }

    @Override
    public String getDetails() {
        return "Genre: " + genre + " | Pages: " + pages +
               " | Available: " + isAvailable();
    }

    // --- Polymorphism: override getSummary ---
    @Override
    public String getSummary() {
        return super.getSummary() + " | " + pages + " pages";
    }

    // --- Searchable implementation ---

    @Override
    public boolean matchesQuery(String query) {
        String q = query.toLowerCase();
        return getTitle().toLowerCase().contains(q)
            || getAuthor().toLowerCase().contains(q)
            || genre.toLowerCase().contains(q);
    }

    @Override
    public List<String> getKeywords() {
        return Arrays.asList(getTitle(), getAuthor(), genre, getType());
    }

    // --- Borrowable implementation ---

    @Override
    public boolean borrow(String userId) {
        if (isAvailable()) {
            setAvailable(false);
            this.borrowedBy = userId;
            return true;
        }
        return false;
    }

    @Override
    public boolean returnItem(String userId) {
        if (!isAvailable() && userId.equals(borrowedBy)) {
            setAvailable(true);
            this.borrowedBy = null;
            return true;
        }
        return false;
    }

    @Override
    public String getBorrowedBy() { return borrowedBy; }

    // --- Getters ---
    public String getGenre() { return genre; }
    public int    getPages() { return pages; }

    // --- CSV format: itemId,Book,title,author,year,available,genre,pages,borrowedBy ---
    @Override
    public String toCSV() {
        return super.toCSV() + "," + genre + "," + pages + "," + (borrowedBy == null ? "none" : borrowedBy);
    }
}
