import edu.gmu.swe622.fss.Action;
import edu.gmu.swe622.fss.FSSClient;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by jmr on 10/16/2017.
 */
public class client {

    private static void printUsage() {
        Stream.of(
            "Usage:", "upload <path_on_client> </path/filename/on/server>"
        ).forEach(System.out::println);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
        }

        String actionName = args[0];
        Action action = Action.findByName(actionName);

        if (action == null || action.getNumArgs() < args.length-1) {
            printUsage();
            System.exit(0);
        }

        String[] commandArgs = new String[action.getNumArgs()];
        for (int i = 0; i < action.getNumArgs(); i++) {
            commandArgs[i] = args[i+1];
        }
        new FSSClient().doAction(action, commandArgs);
    }
}
