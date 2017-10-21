package edu.gmu.swe622.fss;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing a response from the FSS server.
 */
public class Response extends Message {

    public static final Response FILE_NOT_FOUND = new Response("File was not found");
    public static final Response DIRECTORY_NOT_FOUND = new Response("Directory was not found");
    public static final Response SUCCESSFUL = new Response();

    private boolean valid = true;
    private String errorMessage;

    public Response() {
    }

    public Response(File file) {
        super(file);
    }

    public Response(List<String> values) {
        super(values);
    }

    public Response(String... values) {
        super(Arrays.asList(values));
    }

    public Response(String errorMessage) {
        this.valid = false;
        this.errorMessage = errorMessage;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
