package common;

import java.util.List;

/**
 * Interface for searchable library items.
 * Demonstrates: Interface usage
 */
public interface Searchable {
    boolean matchesQuery(String query);
    List<String> getKeywords();
}
