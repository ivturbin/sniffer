package org.ivturbin.sandbox;


import org.jnetpcap.packet.PcapPacket;

public interface PacketListener {
    void newPacket(Packet packet, PcapPacket pcapPacket);
}
