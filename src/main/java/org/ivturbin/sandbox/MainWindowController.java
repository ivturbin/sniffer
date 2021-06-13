package org.ivturbin.sandbox;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;


public class MainWindowController implements PacketListener {
    Sniffer sniffer = new Sniffer();
    PcapLoopThread pcapLoopThread;
    final Logger logger = LogManager.getLogger(MainWindowController.class.getName());

    ArrayList<PcapPacket> packets = new ArrayList<>();
    ArrayList<Packet> outgoingPackets = new ArrayList<>();
    ArrayList<Packet> incomingPackets = new ArrayList<>();

    HashMap<Direction, Integer> outgoingIdentifiedTraffic = new HashMap<>();
    HashMap<Direction, Integer> incomingIdentifiedTraffic = new HashMap<>();
    HashMap<Direction, Integer> outgoingUnidentifiedTraffic = new HashMap<>();
    HashMap<Direction, Integer> incomingUnidentifiedTraffic = new HashMap<>();
    final int[] totalTraffic = {0, 0, 0, 0};

    String localhost;

    @FXML
    private TableView<Packet> tvPackets;

    @FXML
    private TableColumn<Packet, Integer> tcSize;

    @FXML
    private TableColumn<Packet, String> tcSourceIP;

    @FXML
    private TableColumn<Packet, String> tcDestinationIP;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnStop;

    @FXML
    private TextArea input;

    @FXML
    private TextArea output;

    @FXML
    private TextArea unidIn;

    @FXML
    private TextArea unidOut;

    @FXML
    private Label lbDevice;

    @FXML
    void btnStartClicked() {
        logger.info("Start clicked");
        
        sniffer.openDevice();
        lbDevice.setText("Connected: " + sniffer.getDevices().get(0));

        pcapLoopThread = new PcapLoopThread(sniffer.getPcap());
        pcapLoopThread.addListener(this);
        pcapLoopThread.start();
        btnStart.setDisable(true);
        btnStop.setDisable(false);

        String tryLocalhost = "";
        try {
            tryLocalhost = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        localhost = tryLocalhost;
    }

    @FXML
    void btnStopClicked() {
        logger.info("Stop clicked");
        lbDevice.setText("");
        pcapLoopThread.interrupt();
        btnStart.setDisable(false);
        btnStop.setDisable(true);

        final String[] text = {"", "", "",""};

        sortPackets();
        analyzeTraffic();

        incomingIdentifiedTraffic.forEach((direction, traffic) ->
                text[0] += direction.IP + ", port: " + direction.port + ", " + traffic + " bytes\n");
        outgoingIdentifiedTraffic.forEach((direction, traffic) ->
                text[1] += direction.IP + ", port: " + direction.port + ", " + traffic + " bytes\n");

        incomingUnidentifiedTraffic.forEach(((direction, traffic) ->
                text[2] += direction.IP + ", port: " + direction.port + ", " + traffic + " bytes\n"));
        outgoingUnidentifiedTraffic.forEach(((direction, traffic) ->
                text[3] += direction.IP + ", port: " + direction.port + ", " + traffic + " bytes\n"));

        input.setText(text[0] + "\nTotal: " + totalTraffic[0] + " bytes");
        output.setText(text[1] + "\nTotal: " + totalTraffic[1] + " bytes");
        unidIn.setText(text[2] + "\nTotal: " + totalTraffic[2] + " bytes");
        unidOut.setText(text[3] + "\nTotal: " + totalTraffic[3] + " bytes");

/*
        WhoisClient whoisClient = new WhoisClient();
        whoisClient.setConnectTimeout(10000);
        whoisClient.setDefaultTimeout(10000);

        try {
            whoisClient.connect("whois.verisign-grs.com", 43);
            traffic.forEach((inetAddress, size) -> {
                try {
                    SnifferApp.logger.info(inetAddress.getHostName() + " " +
                            inetAddress.getHostAddress() + " " +
                            inetAddress.getCanonicalHostName() + " " +
                            whoisClient.query("=" + inetAddress.getHostName()));
                } catch (Exception e) {
                    SnifferApp.logger.error("Whois query error");
                    e.printStackTrace();
                }
            });
            whoisClient.disconnect();
        } catch (SocketException e) {
            SnifferApp.logger.error("No whois connection: socket exception");
        } catch (IOException e) {
            SnifferApp.logger.error("No whois connection: IO exception");
        }
*/

//        try {
//            whoisClient.connect("whois.verisign-grs.com", 43);
//            SnifferApp.logger.info(whoisClient.query("=google.com"));
//        }   catch (Exception e) {
//
//        }


    }

    @FXML
    void initialize() {
        logger.info("Main window initialize");

        tvPackets.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tvPackets.setItems(FXCollections.observableArrayList());
        tvPackets.setPlaceholder(new Label("No packets"));
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tcSourceIP.setCellValueFactory(new PropertyValueFactory<>("sourceIP"));
        tcDestinationIP.setCellValueFactory(new PropertyValueFactory<>("destinationIP"));
        output.setEditable(false);
    }

    @Override
    public void newPacket(PcapPacket pcapPacket) {
        Ip4 ip = new Ip4();

        Packet packet = new Packet(pcapPacket.size(), FormatUtils.ip(pcapPacket.getHeader(ip).source()), FormatUtils.ip(pcapPacket.getHeader(ip).destination()));
        tvPackets.getItems().add(packet);
        packets.add(pcapPacket);
        logger.info(packet.size + " bytes, source: " + packet.sourceIP);
    }

    /**
     * Сортирует трафик на входящий и исходящий
     * */

    private void sortPackets() {
        Ip4 ip = new Ip4();
        Tcp tcp = new Tcp();
        Udp udp = new Udp();
        final Packet[] packet = new Packet[1];

        packets.forEach(pcapPacket -> {
            packet[0] = new Packet (pcapPacket.size(), FormatUtils.ip(pcapPacket.getHeader(ip).source()), FormatUtils.ip(pcapPacket.getHeader(ip).destination()));
            if (packet[0].sourceIP.equals(localhost)) {
                if (pcapPacket.hasHeader(tcp)){
                    packet[0].port = pcapPacket.getHeader(tcp).destination();
                } else {
                    packet[0].port = pcapPacket.getHeader(udp).destination();
                }
                outgoingPackets.add(packet[0]);
            } else {
                if (pcapPacket.hasHeader(tcp)) {
                    packet[0].port = pcapPacket.getHeader(tcp).source();
                } else {
                    packet[0].port = pcapPacket.getHeader(udp).source();
                }
                incomingPackets.add(packet[0]);
            }
        });
    }

    /**
     * Разбирает трафик по использованию портов
     * */

    private void analyzeTraffic() {
        Services services = new Services();

        for (Packet packet:
             incomingPackets) {
            if (services.ports.contains(packet.port)){
                if (incomingIdentifiedTraffic.containsKey(new Direction(packet.sourceIP, packet.port))) {
                    incomingIdentifiedTraffic.compute(new Direction(packet.sourceIP, packet.port), (k, v) -> v += packet.size);
                } else {
                    incomingIdentifiedTraffic.put(new Direction(packet.sourceIP, packet.port), packet.size);
                }
                totalTraffic[0] += packet.size;
            } else {
                if (incomingUnidentifiedTraffic.containsKey(new Direction(packet.sourceIP, packet.port))) {
                    incomingUnidentifiedTraffic.compute(new Direction(packet.sourceIP, packet.port), (k, v) -> v += packet.size);
                } else {
                    incomingUnidentifiedTraffic.put(new Direction(packet.sourceIP, packet.port), packet.size);
                }
                totalTraffic[2] += packet.size;
            }
        }

        for (Packet packet:
             outgoingPackets) {
            if (services.ports.contains(packet.port)) {
                if (outgoingIdentifiedTraffic.containsKey(new Direction(packet.destinationIP, packet.port))) {
                    outgoingIdentifiedTraffic.compute(new Direction(packet.destinationIP, packet.port), (k, v) -> v += packet.size);
                } else {
                    outgoingIdentifiedTraffic.put(new Direction(packet.destinationIP, packet.port), packet.size);
                }
                totalTraffic[1] += packet.size;
            } else {
                if (outgoingUnidentifiedTraffic.containsKey(new Direction(packet.destinationIP, packet.port))) {
                    outgoingUnidentifiedTraffic.compute(new Direction(packet.destinationIP, packet.port), (k, v) -> v += packet.size);
                } else {
                    outgoingUnidentifiedTraffic.put(new Direction(packet.destinationIP, packet.port), packet.size);
                }
                totalTraffic[3] += packet.size;
            }
        }
    }
}