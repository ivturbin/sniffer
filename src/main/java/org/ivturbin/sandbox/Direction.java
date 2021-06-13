package org.ivturbin.sandbox;

public class Direction {
    String IP;
    int port;

    Direction(String IP, int port){
        this.IP = IP;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Direction) {
            Direction direction = (Direction) obj;
            return IP.equals(direction.IP) && port == direction.port;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (IP + port).hashCode();
    }
}
