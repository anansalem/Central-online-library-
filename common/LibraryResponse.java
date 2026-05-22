package common;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a server response sent back to the client over the socket.
 */
public class LibraryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;
    private String  message;
    private List<String> results; // list of item summaries for search/list

    public LibraryResponse(boolean success, String message, List<String> results) {
        this.success = success;
        this.message = message;
        this.results = results;
    }

    public LibraryResponse(boolean success, String message) {
        this(success, message, null);
    }

    public boolean isSuccess()         { return success; }
    public String  getMessage()        { return message; }
    public List<String> getResults()   { return results; }
}
