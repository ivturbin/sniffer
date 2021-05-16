package org.ivturbin.sandbox;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.network.Ip4;

import java.net.InetAddress;
import java.util.ArrayList;


public class MainWindowController implements PacketListener{
    Sniffer sniffer = new Sniffer();
    PcapLoopThread pcapLoopThread;

    ArrayList<InetAddress> addresses = new ArrayList<>();

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
    private Label lbDevice;

    @FXML
    void btnStartClicked() {
        SnifferApp.logger.info("Start clicked");
        sniffer.openDevice();
        lbDevice.setText("Connected: " + sniffer.getDevices().get(0));

        pcapLoopThread = new PcapLoopThread(sniffer.getPcap());
        pcapLoopThread.addListener(this);
        pcapLoopThread.start();
        btnStart.setDisable(true);
        btnStop.setDisable(false);
    }

    @FXML
    void btnStopClicked() {
        SnifferApp.logger.info("Stop clicked");
        lbDevice.setText("");
        pcapLoopThread.interrupt();
        btnStart.setDisable(false);
        btnStop.setDisable(true);
    }

    @FXML
    void initialize() {
        SnifferApp.logger.info("Main window initialize");
        tcSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        tcSourceIP.setCellValueFactory(new PropertyValueFactory<>("sourceIP"));
        tcDestinationIP.setCellValueFactory(new PropertyValueFactory<>("destinationIP"));
    }

    @Override
    public void newPacket(Packet packet, PcapPacket pcapPacket) {
        Ip4 ip = new Ip4();
        try {
        addAddressIfNew(InetAddress.getByAddress(pcapPacket.getHeader(ip).source()));
        } catch (Exception e) {
            SnifferApp.logger.info("No address");
        }
        if (tvPackets.getItems() == null) {
            ArrayList <Packet> packets = new ArrayList<>();
            packets.add(packet);
            tvPackets.setItems(javafx.collections.FXCollections.observableList(packets));

        } else {
            tvPackets.getItems().add(packet);
        }
    }

    private void addAddressIfNew(InetAddress inetAddress)
    {
        if (!addresses.contains(inetAddress)) {
            addresses.add(inetAddress);
        }

    }
}
