package org.ivturbin.sandbox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import java.util.ArrayList;
import java.util.List;

public class Sniffer {
    private static Pcap pcap;
    private final ArrayList<PcapIf> allDevs = new ArrayList<>();
    private final ArrayList<String> devices = new ArrayList<>();
    final Logger logger = LogManager.getLogger(Sniffer.class.getName());


    private void findDevices() {
        StringBuilder errorBuffer = new StringBuilder();
        int r = Pcap.findAllDevs(allDevs, errorBuffer);
        if (r != Pcap.OK) {
            logger.error("Finding devices error:\n" + errorBuffer);
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
            logger.error("Opening device error:\n"
                    + errorBuffer);
        }
    }

    public List<String> getDevices() {
        return devices;
    }

    public Pcap getPcap() {
        return pcap;
    }
}
