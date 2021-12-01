package client;

import model.NTPMessage;
import model.Util;

import java.io.IOException;
import java.net.*;
import java.text.DecimalFormat;

public class Client extends Thread {
    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;

    public Client(String serverName, int port) throws SocketException, UnknownHostException {
        this.socket = new DatagramSocket();
        this.address = InetAddress.getByName(serverName);
        this.port = port;
    }

    @Override
    public void run() {
        byte[] buffer = new NTPMessage().getMessage();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        try {
            socket.send(packet);
            System.out.println("NTP request sent, waiting for response...\n");

            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            double destinationTimestamp = System.currentTimeMillis() / 1000.0 + Util.PRIME_EPOCH;

            NTPMessage message = new NTPMessage(packet.getData());

            double roundTripDelay = (destinationTimestamp - message.getOriginTimestamp()) -
                    (message.getTransmitTimestamp() - message.getReceiveTimestamp());

            double localClockOffset = ((message.getReceiveTimestamp() - message.getOriginTimestamp()) +
                            (message.getTransmitTimestamp() - destinationTimestamp)) / 2;

            System.out.println("NTP server: " + address.getHostName());
            System.out.println("=================================================");
            System.out.println(message.toString());
            System.out.println("=================================================");

            System.out.println("Destination timestamp:\t" + Util.timestampToString(destinationTimestamp));

            System.out.println("Round-trip delay: " +
                    new DecimalFormat("0.00").format(roundTripDelay * 1000) + " ms");

            System.out.println("Local clock offset: " +
                    new DecimalFormat("0.00").format(localClockOffset * 1000) + " ms");
        } catch (IOException exception) {
            System.out.println("The client was unable to send or process the message");
        }
        socket.close();
    }
}
