package edu.gmu.swe622.fss;

import java.io.Serializable;

/**
 * Created by jmr on 10/19/2017.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Response READY_RESPONSE = new Response("ready");
    public static final Response FILE_NOT_FOUND_RESPONSE = new Response("error", false, "File could not be found");

    private boolean valid = true;
    private String message;
    private String errorMessage;

    public Response(String message) {
        this.message = message;
    }

    public Response(String message, boolean valid) {
        this.message = message;
        this.valid = valid;
    }

    public Response(String message, boolean valid, String errorMessage) {
        this.message = message;
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
