package common;

import java.util.List;

/**
 * Represents an EBook. Extends Book (multi-level inheritance).
 * Demonstrates: Inheritance (multi-level), Polymorphism
 */
public class EBook extends Book {

    private String format;   // PDF, EPUB, MOBI
    private double fileSizeMB;

    public EBook(String itemId, String title, String author, int year,
                 String genre, int pages, String format, double fileSizeMB) {
        super(itemId, title, author, year, genre, pages);
        this.format      = format;
        this.fileSizeMB  = fileSizeMB;
    }

    @Override
    public String getType() {
        return "EBook";
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Format: " + format + " | Size: " + fileSizeMB + " MB";
    }

    // Polymorphism: override getSummary again
    @Override
    public String getSummary() {
        return super.getSummary() + " [" + format + "]";
    }

    @Override
    public List<String> getKeywords() {
        List<String> base = new java.util.ArrayList<>(super.getKeywords());
        base.add(format);
        return base;
    }

    public String getFormat()      { return format; }
    public double getFileSizeMB()  { return fileSizeMB; }

    @Override
    public String toCSV() {
        return super.toCSV() + "," + format + "," + fileSizeMB;
    }
}
