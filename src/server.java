import edu.gmu.swe622.fss.FSSServer;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Class with a main method to invoke from the command line in order to start the FSS server.
 */
public class server {

    /**
     * Starts the server listening on the port specified by the command line arguments.
     * @param args  the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage();
            System.exit(1);
        }

        if (! "start".equals(args[0])) {
            printUsage();
            System.exit(1);
        }

        String portParam = args[1];
        Integer port = null;
        try {
            port = Integer.valueOf(portParam);

        } catch (NumberFormatException exp) {
            printUsage();
            System.exit(1);
        }

        new FSSServer().serve(port);
    }

    /**
     * Prints usage information to stdout.
     */
    private static void printUsage() {
        Stream.of("Usage:", "start <portnumber>").forEach(System.out::println);
    }

}
