package model;

import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;

public class NTPMessage {
    private byte leapIndicator = 0;             // 2 bit
    private byte version = 4;                   // 3 bit
    private final byte mode;                    // 3 bit
    private byte stratum = 0;                   // 8 bit
    private byte poll = 0;                      // 8 bit
    private byte precision = 0;                 // 8 bit
    private double rootDelay = 0;               // 32 bit
    private double rootDispersion = 0;          // 32 bit
    private byte[] referenceId = {0, 0, 0, 0};  // 32 bit
    private double referenceTimestamp = 0;      // 64 bit
    private double originTimestamp = 0;         // 64 bit
    private double receiveTimestamp = 0;        // 64 bit
    private final double transmitTimestamp;     // 64 bit

    public NTPMessage() {
        mode = 3;
        transmitTimestamp = System.currentTimeMillis() / 1000.0 + Util.PRIME_EPOCH;
    }

    public NTPMessage(byte[] bytes) {
        leapIndicator = (byte) ((bytes[0] >> 6) & 0x3);
        version = (byte) ((bytes[0] >> 3) & 0x7);
        mode = (byte) (bytes[0] & 0x7);
        stratum = bytes[1];
        poll = bytes[2];
        precision = bytes[3];
        rootDelay =
                bytes[4] * Math.pow(2, 8) +
                Util.convertUnsignedByteToShort(bytes[5]) +
                Util.convertUnsignedByteToShort(bytes[6]) / Math.pow(2, 8) +
                Util.convertUnsignedByteToShort(bytes[7]) / Math.pow(2, 16);
        rootDispersion =
                Util.convertUnsignedByteToShort(bytes[8]) * Math.pow(2, 8) +
                Util.convertUnsignedByteToShort(bytes[9]) +
                Util.convertUnsignedByteToShort(bytes[10]) / Math.pow(2, 8) +
                Util.convertUnsignedByteToShort(bytes[11]) / Math.pow(2, 16);
        referenceId = Arrays.copyOfRange(bytes, 12, 16);
        referenceTimestamp = Util.convertToTimestamp(bytes, 16);
        originTimestamp = Util.convertToTimestamp(bytes, 24);
        receiveTimestamp = Util.convertToTimestamp(bytes, 32);
        transmitTimestamp = Util.convertToTimestamp(bytes, 40);
    }

    public double getOriginTimestamp() {
        return originTimestamp;
    }

    public double getReceiveTimestamp() {
        return receiveTimestamp;
    }

    public double getTransmitTimestamp() {
        return transmitTimestamp;
    }

    public byte[] getMessage() {
        byte[] messageBytes = new byte[48];
        ByteBuffer buffer = ByteBuffer.wrap(messageBytes);
        buffer.put((byte) (leapIndicator << 6 | version << 3 | mode));
        buffer.put(stratum);
        buffer.put(poll);
        buffer.put(precision);
        buffer.put(Util.convertFloatingPointToByteArray(rootDelay));        // rootDelay signed
        buffer.put(Util.convertFloatingPointToByteArray(rootDispersion));   // rootDispersion unsigned
        buffer.put(referenceId);
        buffer.put(Util.convertTimestampToByteArray(referenceTimestamp));
        buffer.put(Util.convertTimestampToByteArray(originTimestamp));
        buffer.put(Util.convertTimestampToByteArray(receiveTimestamp));
        buffer.put(Util.convertTimestampToByteArray(transmitTimestamp));
        return messageBytes;
    }

    @Override
    public String toString() {
        return "Leap indicator: " + leapIndicator + "\n" +
                "Version: " + version + "\n" +
                "Mode: " + mode + "\n" +
                "Stratum: " + stratum + "\n" +
                "Poll: " + poll + "\n" +
                "Precision: " + precision + " (" + new DecimalFormat("0.#E0")
                .format(Math.pow(2, precision)) + " seconds)\n" +
                "Root delay: " + new DecimalFormat("0.00").format(rootDelay * 1000) + " ms\n" +
                "Root dispersion: " + new DecimalFormat("0.00").format(rootDispersion * 1000) + " ms\n" +
                "Reference id: " + referenceIdToString() + "\n" +
                "Reference timestamp:    " + Util.timestampToString(referenceTimestamp) + "\n" +
                "Originate timestamp:    " + Util.timestampToString(originTimestamp) + "\n" +
                "Receive timestamp:      " + Util.timestampToString(receiveTimestamp) + "\n" +
                "Transmit timestamp:     " + Util.timestampToString(transmitTimestamp);
    }

    private String referenceIdToString() {
        if (stratum == 0 || stratum == 1) {
            return new String(referenceId);
        } else if (version == 3 || version == 4) {
            return Util.convertUnsignedByteToShort(referenceId[0]) + "." +
                    Util.convertUnsignedByteToShort(referenceId[1]) + "." +
                    Util.convertUnsignedByteToShort(referenceId[2]) + "." +
                    Util.convertUnsignedByteToShort(referenceId[3]);
        }
        return "";
    }
}
