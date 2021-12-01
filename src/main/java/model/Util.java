package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static final double PRIME_EPOCH = 2208988800.0;    // 1 January 1900 UTC

    public static short convertUnsignedByteToShort(byte unsignedByte) {
        return (short) (unsignedByte & 0xFF);
    }

    public static double convertToTimestamp(byte[] bytes, int index) {
        double timestamp = 0.0;
        for (int i = 0; i < 8; i++) {
            timestamp += convertUnsignedByteToShort(bytes[index + i]) * Math.pow(2, (3 - i) * 8);
        }
        return timestamp;
    }

    public static byte[] convertFloatingPointToByteArray(double value) {
        int valueSigned = (int) (value * Math.pow(2, 16));
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((valueSigned >> 24) & 0xFF);
        bytes[1] = (byte) ((valueSigned >> 16) & 0xFF);
        bytes[2] = (byte) ((valueSigned >> 8) & 0xFF);
        bytes[3] = (byte) (valueSigned & 0xFF);
        return bytes;
    }

    public static byte[] convertTimestampToByteArray(double timestamp) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            double base = Math.pow(2, (3 - i) * 8);
            bytes[i] = (byte) (timestamp / base);
            timestamp = timestamp - convertUnsignedByteToShort(bytes[i]) * base;
        }
        return bytes;
    }

    public static String timestampToString(double timestamp) {
        if (timestamp == 0) {
            return "0";
        }
        double utc = timestamp - Util.PRIME_EPOCH;
        long milliseconds = (long) (utc * 1000.0);
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSSSS").format(new Date(milliseconds));
    }
}
