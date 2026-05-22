package common;

import java.io.Serializable;

/**
 * Represents a client request sent over the socket.
 * Demonstrates: Serializable objects over sockets, encapsulation
 */
public class LibraryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // Request types
    public static final String SEARCH    = "SEARCH";
    public static final String BORROW    = "BORROW";
    public static final String RETURN    = "RETURN";
    public static final String LIST_ALL  = "LIST_ALL";
    public static final String LIST_MINE = "LIST_MINE";
    public static final String EXIT      = "EXIT";

    private String action;  // one of the constants above
    private String query;   // search term or item ID
    private String userId;  // the user making the request

    public LibraryRequest(String action, String query, String userId) {
        this.action = action;
        this.query  = query;
        this.userId = userId;
    }

    public String getAction() { return action; }
    public String getQuery()  { return query; }
    public String getUserId() { return userId; }

    @Override
    public String toString() {
        return "Request[" + action + ", query=" + query + ", user=" + userId + "]";
    }
}
