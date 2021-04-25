package org.ivturbin.sandbox;


import javafx.scene.text.Text;
import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;


public class PcapLoopThread extends Thread {
    private Pcap pcap;
    private Text text;

    PcapLoopThread(Pcap pcap, Text text) {
        this.pcap = pcap;
        this.text = text;
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

                // данные фрейма data
                byte[] data = packet.getByteArray(0, packet.size());

                // номер фрейма
                //nbpct = (int) packet.getFrameNumber();
                readData = "";
                // количество байт фрейма
                //label2.setText(String.format("Length of packet %d bytes", data.length));
                //label3.setText(String.format("Frame number %d ", packet.getFrameNumber()));
                byte[] sIP = packet.getHeader(ip).source();
                System.out.println("Пакет " + data.length + " байт, номер фрейма " + packet.getFrameNumber() + " IP источника: " + org.jnetpcap.packet.format.FormatUtils.ip(sIP));
                //System.out.println("Номер фрейма " + packet.getFrameNumber());
                // перенос данных фрейма в форматированную строку
                for (int i = 0; i < data.length; i++) {
                    bufrd[i] = data[i];
                    readData = readData + String.format("%02X ", bufrd[i]);
                }
                // данные фрейма в окно input
                System.out.println(readData);
                //input.setText(readData);
            }
        }
    };
}
