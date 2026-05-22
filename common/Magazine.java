package common;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a Magazine (reference only, cannot be borrowed).
 * Demonstrates: Inheritance, Polymorphism
 */
public class Magazine extends LibraryItem implements Searchable {

    private String category;
    private int    issueNumber;

    public Magazine(String itemId, String title, String author, int year,
                    String category, int issueNumber) {
        super(itemId, title, author, year);
        this.category    = category;
        this.issueNumber = issueNumber;
    }

    @Override
    public String getType() {
        return "Magazine";
    }

    @Override
    public String getDetails() {
        return "Category: " + category + " | Issue #" + issueNumber +
               " | Reference only (not borrowable)";
    }

    // Polymorphism: different summary format from Book
    @Override
    public String getSummary() {
        return super.getSummary() + " | Issue #" + issueNumber;
    }

    @Override
    public boolean matchesQuery(String query) {
        String q = query.toLowerCase();
        return getTitle().toLowerCase().contains(q)
            || category.toLowerCase().contains(q);
    }

    @Override
    public List<String> getKeywords() {
        return Arrays.asList(getTitle(), category, getType());
    }

    public String getCategory()  { return category; }
    public int    getIssueNumber() { return issueNumber; }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + category + "," + issueNumber;
    }
}
