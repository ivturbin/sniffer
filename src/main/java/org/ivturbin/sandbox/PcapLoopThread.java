package org.ivturbin.sandbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;

import java.util.ArrayList;


public class PcapLoopThread extends Thread{
    private final Pcap pcap;
    private ArrayList <PacketListener> listeners = new ArrayList<>();
    final Logger logger = LogManager.getLogger(PcapLoopThread.class.getName());

    PcapLoopThread(Pcap pcap) {
        this.pcap = pcap;
    }

    @Override
    public void run() {
        logger.info("Capture started");
        while(!isInterrupted())
        {
            try{
                //Приостанавливает поток 1мс
                sleep(1);
                // отлов одного пакета если был Start
                pcap.loop(1, pcapPacketHandler, "jnetpcap rocks!");
            }catch(Exception e){
                logger.error("Capturing error");
            }
        }
    }

    private final PcapPacketHandler<String> pcapPacketHandler = new PcapPacketHandler<>() {

        // строка данных
        String readData;

        @Override
        public void nextPacket(PcapPacket packet, String user) {

            Ip4 ip = new Ip4();
            if (packet.hasHeader(ip)) {
                for (PacketListener listener :
                        listeners
                ) {
                    listener.newPacket(packet);
                }

                // данные фрейма data
//                byte[] data = packet.getByteArray(0, packet.size());
//
//                readData = "";
//                byte[] sIP = packet.getHeader(ip).source();
//                System.out.println("Пакет " + data.length + " байт, номер фрейма " + packet.getFrameNumber() + " IP источника: " + org.jnetpcap.packet.format.FormatUtils.ip(sIP));

                /*
                 * Перенос данных фрейма в форматированную строку
                String readData = "";
                for (int i = 0; i < data.length; i++) {
                    readData = readData + String.format("%02X ", data[i]);
                }
                System.out.println(readData);

                 */
            }
        }
    };

    public void addListener(PacketListener listener) {
        listeners.add(listener);
    }
}
