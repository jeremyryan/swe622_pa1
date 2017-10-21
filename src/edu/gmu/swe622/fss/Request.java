package edu.gmu.swe622.fss;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jmr on 10/19/2017.
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private Action action;
    private List<String> arguments;

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<String> getArguments() {
        return this.arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public Request(Action action, String... arguments) {
        this.action = action;
        this.arguments = Arrays.asList(arguments);
    }

    public Request(Action action, List<String> arguments) {
        this.action = action;
        this.arguments = arguments;
    }
}
