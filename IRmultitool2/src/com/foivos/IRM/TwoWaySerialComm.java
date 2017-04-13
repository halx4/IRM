package com.foivos.IRM;

import java.util.Arrays;
import java.util.Vector;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class TwoWaySerialComm {
    private Multitool multitool;

    private SerialPort serialPort;

    TwoWaySerialComm(Multitool multitool) {
        this.multitool = multitool;

    }

    // ---------------------------------------------------
    public static Vector<String> getAvailableComPorts() {

        String[] portNames = SerialPortList.getPortNames();

        return new Vector<String>(Arrays.asList(portNames));
    }

    // --------------------------------
    ConnectStatus connect(String portName)// throws Exception
    {
        serialPort = new SerialPort(portName);

        if (serialPort.isOpened()) {
            System.out.println("Error: Port is currently in use");
            return ConnectStatus.PORT_IN_USE;
        } else// is closed
        {
            try {
                serialPort.openPort();// Open port
                serialPort.setParams(9600, 8, 1, 0);// Set params
                int mask = SerialPort.MASK_RXCHAR;// Prepare mask
                serialPort.setEventsMask(mask);// Set mask
                serialPort.addEventListener(new SerialPortReader());// Add
                                                                    // SerialPortEventListener

            } catch (SerialPortException ex) {
                return ConnectStatus.OTHER_CONNECTION_ERROR;
            }

        }
        return ConnectStatus.CONNECTED;

    }

    // -------------------------------------------------
    public void closeConnection() {

        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

    }

    // #########################################
    class SerialPortReader implements SerialPortEventListener {

        private byte[] buffer = new byte[1024];

        int len = 0;

        @Override
        public void serialEvent(SerialPortEvent event) {

            byte data;

            try {
                while (serialPort.getInputBufferBytesCount() > 0) {
                    data = serialPort.readBytes(1)[0];
                    buffer[len++] = data;
                    if (data == '\n') {

                        break;
                    }
                }
                if ((len == 1) && (buffer[len - 1] == '\n')) {

                    len = 0;
                } else if (len > 1 && buffer[len - 1] == '\n') {
                    String str = new String(buffer, 0, len - 1);
                    System.out.println("got message->" + str);
                    multitool.commandReceived(str);
                    len = 0;
                }
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

        }
    }// end inner class

    // -------------------------------------------------

}