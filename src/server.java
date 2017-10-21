import edu.gmu.swe622.fss.FSSServer;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by jmr on 10/16/2017.
 */
public class server {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
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

    private static void printUsage() {
        Stream.of("Usage:", "start <portnumber>").forEach(System.out::println);
    }

}
