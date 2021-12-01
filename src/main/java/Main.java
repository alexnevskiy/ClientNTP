import client.Client;

import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {
    private static final String HELP = "No arguments - Start the client, which will send a request to pool.ntp.org\n" +
            "-a address port - Server address and port where the NTP message will be sent";
    private static final String BAD_COMMAND = "Bad command. Enter -help for information on startup options";

    public static void main(String[] args) throws SocketException, UnknownHostException {
        String serverName = null;
        int port = 0;
        if (args.length == 0) {
            serverName = "pool.ntp.org";
            port = 123;
        } else if (args.length == 3 && args[0].equals("-a")) {
            serverName = args[1];
            port = Integer.parseInt(args[2]);
        } else if (args[0].equals("-help")) {
            System.out.println(HELP);
            System.exit(0);
        } else {
            System.out.println(BAD_COMMAND);
            System.exit(0);
        }

        Client client = new Client(serverName, port);
        client.start();
    }
}
