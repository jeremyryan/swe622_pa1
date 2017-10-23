package edu.gmu.swe622.fss;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Base class for messages sent by FSS clients and servers.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> values;
    private File file;

    private boolean restart;

    /**
     * No argument constructor.
     */
    public Message() { }

    /**
     * Constructor which initializes an instance with a file.
     * @param file  the file that will be sent with this message
     */
    public Message(File file) {
        this.file = file;
    }

    /**
     * Constructor which initializes an instance with a list of values.
     * @param values  the values that will be sent with this message
     */
    public Message(List<String> values) {
        this.values = values;
    }

    /**
     * Constructor which initializes an instance with a file and a list of values to send.
     * @param file  the file to send with the request
     * @param values  the values to send with the request
     */
    public Message(File file, List<String> values) {
        this.values = values;
        this.file = file;
    }

    /**
     * Getter for request values.
     * @return the values to send with the request
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * Getter for request values.
     * @param values the values to send with the request.
     */
    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * Getter for message file.
     * @return  the file that will be sent with this message
     */
    public File getFile() {
        return file;
    }

    /**
     * Setter for request file.
     * @param file  the file that will be sent with this message
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Getter for restart.
     * @return  true if this is a restart of a previous request, otherwise false
     */
    public boolean isRestart() {
        return restart;
    }

    /**
     * Setter for restart.
     * @param restart  is this request a restart of a previous request
     */
    public void setRestart(boolean restart) {
        this.restart = restart;
    }

}
