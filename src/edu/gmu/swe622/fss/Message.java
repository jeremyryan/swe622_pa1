package edu.gmu.swe622.fss;

import java.io.Serializable;

/**
 * Base class for messages sent by FSS clients and servers.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object value;
    private Long fileSize;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

}
