package edu.gmu.swe622.fss;

import java.io.File;
import java.util.Arrays;

/**
 * Created by jmr on 10/19/2017.
 */
public class Request extends Message {

    private Action action;

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Request(Action action, File file) {
        super(file);
        this.action = action;
    }

    public Request(Action action, String... arguments) {
        super(Arrays.asList(arguments));
        this.action = action;
    }

    public Request(Action action, File file, String... arguments) {
        super(file, Arrays.asList(arguments));
        this.action = action;
    }

}
