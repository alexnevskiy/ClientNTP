package model;

public class NTPMessage {
    private byte li = 0;
    private byte vn = 0;
    private byte mode = 0;
    private byte stratum = 0;
    private byte poll = 0;
    private byte precision = 0;
    private double rootDelay = 0;
    private double rootDispersion = 0;
    private byte[] referenceId = {0, 0, 0, 0};
    private double referenceTimestamp = 0;
    private double originTimestamp = 0;
    private double receiveTimestamp = 0;
    private double transmitTimestamp = 0;

    public NTPMessage() {}
}
