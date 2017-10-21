package edu.gmu.swe622.fss;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Created by jmr on 10/21/17.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> values;
    private File file;

    public Message() {
    }

    public Message(File file) {
        this.file = file;
    }

    public Message(List<String> values) {
        this.values = values;
    }

    public Message(File file, List<String> values) {
        this.values = values;
        this.file = file;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
