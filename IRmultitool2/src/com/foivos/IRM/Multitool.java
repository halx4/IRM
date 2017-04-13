package com.foivos.IRM;

import java.io.IOException;
import java.util.Vector;

public class Multitool implements MultitoolCoreInterface {

    static final int VOLUME_STEP = 2000;

    static final String MONITOROFF = "__CMD_MONOFF";

    static final String HIBERNATE = "__CMD_HBRN";

    static final String VOLUMEINC = "__CMD_VOLINC";

    static final String VOLUMEDEC = "__CMD_VOLDEC";

    static final String MUTE = "__CMD_MUTE";

    private GuiInterfaceforMultitool gg;

    private boolean connectedStatus = false;

    private TwoWaySerialComm serialCom;

    private CommandsHandler commandsHandler;

    private Vector<String> comPortList = new Vector<String>(10, 10);

    private PropertiesHandler propertiesHandler = new PropertiesHandler();

    public static void main(String[] args) {
        Multitool multitool = new Multitool();

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {

                if (args[i].startsWith("COM")) {

                    System.out.println("found COM ARGUMENT");
                    String comPortsubString = args[i].substring(3);
                    try {
                        Integer.parseInt(comPortsubString);
                        multitool.connectRequest(args[i]);
                    } catch (NumberFormatException e) {
                        multitool.gg
                                .displayWarningMessage("invalid parameter - NO "
                                        + i);
                    }
                }

            }
        }

    } // end main

    // ------------------------------

    Multitool() {

        serialCom = new TwoWaySerialComm(this);
        gg = new Gui(this, propertiesHandler.getGuiBounds());
        gg.initGui();
        refreshPortList();
        commandsHandler = new CommandsHandler();

    }

    // --------------------------------
    @Override
    public void refreshPortListRequest() {

        refreshPortList();

    }

    // -------------------------------
    @Override
    public void connectRequest(String port) {
        if (!getConnectedStatus()) {
            // System.out.println("Multitool:got connection request for:"+port);
            ConnectStatus st = serialCom.connect(port);
            // System.out.println("returned connect status="+st);
            if (st == ConnectStatus.CONNECTED) {
                setConnectedStatus(true, port);

                // gg.displayWarningMessage("CONNECTED!");

            } else if (st == ConnectStatus.PORT_IN_USE)
                gg.displayWarningMessage("Could not connect. Port in use.");
            else
                gg.displayWarningMessage("connectRequest:unknown error");
        } else {
            gg.displayWarningMessage("Already Connected!");
        }
    }

    // -------------------------------
    @Override
    public void disconnectRequest() {
        if (getConnectedStatus()) {
            serialCom.closeConnection();
            setConnectedStatus(false);
        }

    }

    // -------------------------------
    @Override
    public void exitRequest() {

        disconnectRequest();

        propertiesHandler.setGUIBounds(gg.getBounds());
        propertiesHandler.saveProperties();
        System.exit(0);
    }

    // --------------------------------------
    private void refreshPortList() {

        comPortList.clear();

        comPortList = TwoWaySerialComm.getAvailableComPorts();

        gg.updateAvailablePortsList(comPortList);
    }

    // -----------------------

    private void setConnectedStatus(boolean status, String connectedPort) {
        connectedStatus = status;
        gg.connectedNotification(status, connectedPort);
    }

    // -----
    private void setConnectedStatus(boolean status) {
        connectedStatus = status;
        gg.connectedNotification(status, "PORT_NOT_AVAILABLE");
    }

    // ----------------------------
    private boolean getConnectedStatus() {

        return connectedStatus;
    }

    // ----------------------
    public void commandReceived(String commandString) {

        String str = commandsHandler.executeCommand(commandString);
        gg.lastCommandReceivedDisplay(str);

    }

    // ############################################
    public class CommandsHandler {
        private boolean monitorIsOn = true;

        public String executeCommand(String commandString) {
            try {
                if (commandString.equals(MONITOROFF) && monitorIsOn) { // monitor
                                                                       // on->off
                    monitorIsOn = false;
                    callNirCmd("monitor", "off");
                    return "monitor off";
                } else if (commandString.equals(MONITOROFF) && !monitorIsOn) { // monitor
                                                                               // off->on
                    monitorIsOn = true;
                    callNirCmd("monitor", "on");
                    return "monitor on";
                } else if (commandString.equals(HIBERNATE)) {// hibernate
                    // disconnectRequest(); //uncomment if you want to
                    // disconnect before hibernating
                    callNirCmd("hibernate");
                    return "hibernate";
                } else if (commandString.equals(VOLUMEINC)) {// volume
                                                             // increase
                    callNirCmd("changesysvolume", Integer.toString(VOLUME_STEP));
                    return "volume up";
                } else if (commandString.equals(VOLUMEDEC)) {// volume
                                                             // decrease
                    callNirCmd("changesysvolume",
                            Integer.toString(-VOLUME_STEP));
                    return "volume down";
                } else if (commandString.equals(MUTE)) {// mute
                    callNirCmd("mutesysvolume", "2");
                    return "mute/unmute";
                } else {// rest commands
                    return commandString;
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
            return commandString;

        }// end method

        // ---------------------
        private void callNirCmd(String argument1) throws IOException,
                InterruptedException {

            Process p = new ProcessBuilder("nircmd.exe", argument1).start();
            p.waitFor();

        }

        // -------------------
        private void callNirCmd(String argument1, String argument2)
                throws IOException, InterruptedException {

            Process p = new ProcessBuilder("nircmd.exe", argument1, argument2)
                    .start();
            p.waitFor();

        }
    }// end inner class

}// end outer class

