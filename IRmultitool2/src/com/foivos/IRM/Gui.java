package com.foivos.IRM;

import java.awt.AWTException;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Vector;

public class Gui extends Frame implements GuiInterfaceforMultitool {

    MultitoolCoreInterface multitool;

    private Choice comPortChoice;

    private Label l1, l2;

    private Button refreshButton, connectButton;

    private Image notConnectedImage = Toolkit.getDefaultToolkit().getImage(
            getClass().getResource("resources/red.png"));

    private Image connectedImage = Toolkit.getDefaultToolkit().getImage(
            getClass().getResource("resources/green.png"));

    private ExitHandler exitHandler = new ExitHandler();

    SystemTray tray = null;

    TrayIcon trayIcon;

    // -------------------------------------------------------------
    public Gui(MultitoolCoreInterface multitool, Rectangle rec) {
        this.multitool = multitool;

        this.setTitle("IRM - NOT connected");
        this.setLayout(null);
        this.setFont(new Font("TimesRoman", Font.PLAIN, 14));
        this.setBackground(new Color(112, 135, 159));
        setBounds(rec);

        this.toFront();
        this.setResizable(false);
        this.addWindowListener(exitHandler);

        l1 = new Label("Press connect", Label.CENTER);
        add(l1);
        l1.setBounds(10, 150, 250, 20);
        l1.setVisible(true);

        l2 = new Label("", Label.CENTER);
        add(l2);
        l2.setBounds(10, 180, 250, 20);
        l2.setVisible(true);

        comPortChoice = new Choice();
        comPortChoice.setBounds(50, 90, 80, 20);
        comPortChoice.setEnabled(true);
        add(comPortChoice);
        comPortChoice.setVisible(true);

        refreshButton = new Button("Refresh");
        refreshButton.addActionListener(new RefreshButtonHandler(this));
        this.add(refreshButton);
        refreshButton.setBounds(150, 60, 90, 25);

        connectButton = new Button("Connect");
        connectButton.addActionListener(new ConnectButtonHandler(this));
        this.add(connectButton);
        connectButton.setBounds(150, 90, 90, 25);

        if (SystemTray.isSupported()) {
            System.out.println("system tray supported");
            tray = SystemTray.getSystemTray();

            PopupMenu popup = new PopupMenu();

            MenuItem maximizeTrayMenuItem = new MenuItem("Maximize");
            maximizeTrayMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    maximizeWindow();
                }
            });
            popup.add(maximizeTrayMenuItem);

            MenuItem exitTrayMenuItem = new MenuItem("Exit");
            exitTrayMenuItem.addActionListener(exitHandler);
            popup.add(exitTrayMenuItem);

            trayIcon = new TrayIcon(notConnectedImage, "IRM - NOT connected",
                    popup);
            trayIcon.setImageAutoSize(true);
            try {
                tray.add(trayIcon);
            } catch (AWTException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        System.out.println("trayClicked");
                        maximizeWindow();
                    }
                }
            });

            addWindowStateListener(new WindowStateListener() {
                @Override
                public void windowStateChanged(WindowEvent e) {
                    System.out.println("windowStateChanged");
                    if (e.getNewState() == ICONIFIED) {
                        System.out.println("ICONIFIED");
                        // TODO uncomment this
                        setVisible(false);
                    }
                }
            });

        }
        setIconImage(notConnectedImage);

        // ---------------------------
        this.setVisible(true); // ----Frame setVisible
        // ---------------------------

    }// end constructor
     // ---------------------------------------------------------------------

    @Override
    public void initGui() {

    }

    // -----------------------
    @Override
    public void connectedNotification(boolean connected, String connectedPort) {
        if (connected) {
            connectButton.setLabel("Disconnect");
            l1.setText("Connected - " + connectedPort);
            this.setTitle("IRM - CONNECTED " + connectedPort);
            this.setState(Frame.ICONIFIED);
            if (SystemTray.isSupported()) {
                trayIcon.setImage(connectedImage);
                trayIcon.displayMessage("Connected", "you're connected to "
                        + connectedPort, TrayIcon.MessageType.NONE);
                trayIcon.setToolTip("IRM - connected to " + connectedPort);
            }
            setIconImage(connectedImage);
        } else {// disconnected
            connectButton.setLabel("Connect");
            l1.setText("NOT Connected");
            this.setTitle("IRM - NOT connected");
            l2.setText("");
            if (SystemTray.isSupported()) {
                trayIcon.setImage(notConnectedImage);
                trayIcon.displayMessage("Disconnected",
                        "you're NOT connected anymore!",
                        TrayIcon.MessageType.NONE);
                trayIcon.setToolTip("IRM - NOT connected");
            }
            setIconImage(notConnectedImage);
        }

    }

    // -----------------------
    @Override
    public void updateAvailablePortsList(Vector<String> comPortList) {
        // System.out.println("-----gui received-----");
        int i = 0;
        comPortChoice.removeAll();
        while (i < comPortList.size()) {
            comPortChoice.add(comPortList.get(i));
            // System.out.println("->"+comPortList.get(i));
            i++;
        }

    };

    // -----------------------
    @Override
    public void displayWarningMessage(String s) {
        new WarningMessage(s);
    }

    // -----------------------
    @Override
    public void lastCommandReceivedDisplay(String commandString) {
        l2.setText("Last Command: " + commandString);

    }

    // --------------------------
    private void maximizeWindow() {
        setVisible(true);
        setExtendedState(NORMAL);
    }

    // ######################################
    class RefreshButtonHandler implements ActionListener {
        Gui source;

        public RefreshButtonHandler(Gui source) {
            this.source = source;

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // System.out.println("GUI:refreshPressed");
            multitool.refreshPortListRequest();

        }

    }

    // ##########################################################
    class ConnectButtonHandler implements ActionListener {
        Gui source;

        public ConnectButtonHandler(Gui source) {
            this.source = source;

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // System.out.println("GUI:connect/disconnect button Pressed");
            if (connectButton.getLabel().equals("Connect")) {
                // System.out.println("REQUEST CONNECT");
                multitool.connectRequest(comPortChoice.getSelectedItem());

            } else {
                // System.out.println("REQUEST DISCONNECT");
                multitool.disconnectRequest();

            }

        }

    }

    // ##########################################################
    class ExitHandler extends WindowAdapter implements ActionListener {

        @Override
        public void windowClosing(WindowEvent closeWindowAndExit) {
            multitool.exitRequest();

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            multitool.exitRequest();
        }
    }
    // ##########################################################

}// ______end public class

