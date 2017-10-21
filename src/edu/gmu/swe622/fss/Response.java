package edu.gmu.swe622.fss;

import java.io.File;
import java.io.Serializable;

/**
 * Created by jmr on 10/19/2017.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Response FILE_NOT_FOUND = new Response("File was not found");
    public static final Response SUCCESSFUL = new Response(true);

    private boolean valid = true;
    private String errorMessage;
    private File file;

    public Response(boolean valid) {
        this.valid = valid;
    }

    public Response(File file) {
        this.file = file;
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

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
