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
            try {
                String serverVar = System.getenv("PA1_SERVER");
                if (serverVar == null) {
                    throw new IllegalStateException("environment variable PA1_SERVER must be set");
                }
                String[] serverVarItems = serverVar.split(":");
                if (serverVarItems.length != 2) {
                    throw new IllegalStateException("no hostname could be found; make sure PA1_SERVER is set: hostname:port");
                }
                String hostName = serverVarItems[0];
                String portParam = serverVarItems[1];
                if (hostName == null) {

                    throw new IllegalStateException("no hostname could be found; make sure PA1_SERVER is set: hostname:port");
                }
                if (portParam == null) {
                    throw new IllegalStateException("no port could be found; make sure PA1_SERVER is set: hostname:port");
                }
                Integer port = Integer.valueOf(portParam);
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
                new FSSClient(hostName, port).doAction(action, commandArgs);
            } catch (Exception exp) {
                System.out.println("Action could not be completed: " + exp.getMessage());
                System.exit(1);
            }

        } else {
            printUsage();
            System.exit(0);
        }
    }
}
