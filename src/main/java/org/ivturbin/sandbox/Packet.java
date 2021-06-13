package org.ivturbin.sandbox;

public class Packet {
    int size;
    String sourceIP;
    String destinationIP;
    int port;

    Packet(int size, String sourceIP, String destinationIP) {
        this.size = size;
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
    }

    public int getPort() {
        return port;
    }

    public int getSize() {
        return size;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public String getDestinationIP() {
        return destinationIP;
    }
}
