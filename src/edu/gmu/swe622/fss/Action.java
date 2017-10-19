package edu.gmu.swe622.fss;

/**
 * Created by jmr on 10/16/2017.
 */
public enum Action {
    RM("rm", 1),
    RMDIR("rmdir", 1),
    UPLOAD("upload", 2),
    DOWNLOAD("download", 2),
    DIR("dir", 1),
    MKDIR("mkdir", 1);

    private int numargs;
    private String name;

    Action(String name, int numargs) {
        this.name = name;
        this.numargs = numargs;
    }

    public String getName() {
        return this.name;
    }

    public int getNumArgs() {
        return this.numargs;
    }

    public static Action findByName(String name) {
        for (Action action : Action.values()) {
            if (name.equals(action.name)) {
                return action;
            }
        }
        return null;
    }
}
