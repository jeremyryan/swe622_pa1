package edu.gmu.swe622.fss;

import java.util.stream.Stream;

/**
 * Created by jmr on 10/31/17.
 */
public class Main {

    private static void printUsage() {
        Stream.of(
                "Usage:",
                "server start <portnumber>",
                "client upload <path_on_client> </path/filename/on/server>",
                "client download </path/existing_filename/on/server> <path_on_client>",
                "client dir <path/existing_directory/on/server>",
                "client mkdir </path/new_directory/on/server>",
                "client rmdir <path/existing_directory/on/server>",
                "client rm </path/existing_filename/on/server>",
                "client shutdown"
        ).forEach(System.out::println);
    }

    public static void main(String[] args) {
        if (args.length <= 1) {
            printUsage();
            System.exit(0);
        }

        if ("server".equalsIgnoreCase(args[0])) {
            if (! "start".equalsIgnoreCase(args[1]) || args.length < 3) {
                printUsage();
                System.exit(1);
            }

            String portParam = args[2];
            Integer port = null;
            try {
                port = Integer.valueOf(portParam);
            } catch (NumberFormatException exp) {
                printUsage();
                System.exit(1);
            }

            FSSServer.newInstance().serve(port);

        } else if ("client".equalsIgnoreCase(args[0])) {
            String actionName = args[1];
            Action action = Action.findByName(actionName);

            if (action == null || args.length-2 < action.getNumArgs()) {
                printUsage();
                System.exit(0);
            }

            String[] commandArgs = new String[action.getNumArgs()];
            for (int i = 0; i < action.getNumArgs(); i++) {
                commandArgs[i] = args[i+2];
            }
            try {
                new FSSClient().doAction(action, commandArgs);
            } catch (Exception exp) {
                System.out.println("Could not start client");
                exp.printStackTrace();
                System.exit(1);
            }

        } else {
            printUsage();
            System.exit(0);
        }

    }

}
