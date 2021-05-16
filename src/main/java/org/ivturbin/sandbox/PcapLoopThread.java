package org.ivturbin.sandbox;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;

import java.util.ArrayList;


public class PcapLoopThread extends Thread{
    private Pcap pcap;
    private ArrayList <PacketListener> listeners = new ArrayList<>();

    PcapLoopThread(Pcap pcap) {
        this.pcap = pcap;
    }

    @Override
    public void run() {
        while(!isInterrupted())
        {
            try{
                //Приостанавливает поток 1мс
                sleep(1);
                // отлов одного пакета если был Start
                pcap.loop(1, jpacketHandler, "jnetpcap rocks!");
            }catch(Exception e){
                System.out.println("Ошибка вывода пакетов");
            }
        }
    }

    private PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

        // строка данных
        String readData;
        // буфер данных
        byte[] bufrd = new byte [2000];

        @Override
        public void nextPacket(PcapPacket packet, String user) {


            Ip4 ip = new Ip4();
            if (packet.hasHeader(ip)) {
                Packet myPacket = new Packet(packet.size(), FormatUtils.ip(packet.getHeader(ip).source()), FormatUtils.ip(packet.getHeader(ip).destination()));
                for (PacketListener listener:
                        listeners
                     ) {
                    listener.newPacket(myPacket, packet);
                }

                // данные фрейма data
                byte[] data = packet.getByteArray(0, packet.size());

                // номер фрейма
                //= (int) packet.getFrameNumber();
                readData = "";
                byte[] sIP = packet.getHeader(ip).source();
                System.out.println("Пакет " + data.length + " байт, номер фрейма " + packet.getFrameNumber() + " IP источника: " + org.jnetpcap.packet.format.FormatUtils.ip(sIP));

                //System.out.println("Номер фрейма " + packet.getFrameNumber());
                // перенос данных фрейма в форматированную строку
                for (int i = 0; i < data.length; i++) {
                    bufrd[i] = data[i];
                    readData = readData + String.format("%02X ", bufrd[i]);
                }
                System.out.println(readData);
            }
        }
    };

    public void addListener(PacketListener listener) {
        listeners.add(listener);
    }
}
