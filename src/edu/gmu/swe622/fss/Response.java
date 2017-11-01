package edu.gmu.swe622.fss;

/**
 * Class representing a response from the FSS server.
 */
public class Response extends Message {

    public static final Response FILE_NOT_FOUND = new Response("File was not found");
    public static final Response DIRECTORY_NOT_FOUND = new Response("Directory was not found");
    public static final Response SUCCESSFUL = new Response();

    private boolean valid = true;
    private String errorMessage;

    /**
     *
     */
    public Response() {
    }

    /**
     *
     * @param errorMessage
     */
    public Response(String errorMessage) {
        this.valid = false;
        this.errorMessage = errorMessage;
    }

    /**
     *
     * @return
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     *
     * @param valid
     */
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     *
     * @return
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     *
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
