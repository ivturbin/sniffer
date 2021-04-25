package org.ivturbin.sandbox;

import javafx.scene.text.Text;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;

public class Sniffer {
    private static Pcap pcap;
    private final ArrayList<PcapIf> allDevs = new ArrayList<>();
    private final ArrayList<String> devices = new ArrayList<>();
    PcapLoopThread pcapLoopThread;

    private void findDevices() {
        StringBuilder errorBuffer = new StringBuilder();
        int r = Pcap.findAllDevs(allDevs, errorBuffer);
        if (r != Pcap.OK) {
            SnifferApp.logger.info("Ошибка чтения сетевых устройств:\n" + errorBuffer.toString());
        }

        for (PcapIf device:
             allDevs) {
            // записать название адаптера в строку
            devices.add((device.getDescription() != null) ? device.getDescription()
                    : "No description available");
        }
    }

    public void openDevice() {
        findDevices();
        StringBuilder errorBuffer = new StringBuilder();
        // выбор адаптера - на данный момент всего один адаптер на компе
        PcapIf device = allDevs.get(0);
        // открыть выбранный адаптер
        pcap = Pcap.openLive(device.getName(), 64 * 1024, Pcap.MODE_PROMISCUOUS, 100, errorBuffer);
        if (pcap == null) {
            SnifferApp.logger.info("Ошибка открытия сетевого устройства для захвата пакетов:\n"
                    + errorBuffer.toString());
        }
    }

    public List<String> getDevices() {
        return devices;
    }

    public void startCapturing(Text text) {
        pcapLoopThread = new PcapLoopThread(pcap, text);
        pcapLoopThread.start();
    }

    public void stopCapturing() {
        pcapLoopThread.interrupt();
    }
}
