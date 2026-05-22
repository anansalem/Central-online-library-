package common;

/**
 * Interface for items that can be borrowed and returned.
 * Demonstrates: Interface usage
 */
public interface Borrowable {
    boolean borrow(String userId);
    boolean returnItem(String userId);
    String getBorrowedBy();
}
