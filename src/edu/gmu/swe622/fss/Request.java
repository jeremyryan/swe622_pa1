package edu.gmu.swe622.fss;

/**
 * Request to send from the client to the server.
 */
public class Request extends Message {

    private Action action;

    public Request(Action action) {
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
