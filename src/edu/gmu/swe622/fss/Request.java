package edu.gmu.swe622.fss;

import java.io.File;
import java.util.Arrays;

/**
 * Request to send from the client to the server.
 */
public class Request extends Message {

    private Action action;

    /**
     * Constructor which initializes an instance with an action and a file to send.
     * @param action  the action represented by this request
     * @param file  the file to send with this request
     */
    public Request(Action action, File file) {
        super(file);
        this.action = action;
    }

    /**
     * constructor which initializes an instance with an action and a set of values to send.
     * @param action  the action represented by this request
     * @param values  the values to send with this request
     */
    public Request(Action action, String... values) {
        super(Arrays.asList(values));
        this.action = action;
    }

    /**
     * Constructor which initializes an instance with an action, a file to send, and a set of values to send.
     * @param action  the action represented by this request
     * @param file  the file to send with this request
     * @param values  the values to send with this request
     */
    public Request(Action action, File file, String... values) {
        super(file, Arrays.asList(values));
        this.action = action;
    }

    /**
     * Getter for request action.
     * @return  the action set for this request
     */
    public Action getAction() {
        return this.action;
    }

    /**
     * Setter for request action.
     * @param action  the action to set for this request
     */
    public void setAction(Action action) {
        this.action = action;
    }

}
