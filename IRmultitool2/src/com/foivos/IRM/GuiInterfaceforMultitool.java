package com.foivos.IRM;

import java.awt.Rectangle;
import java.util.Vector;

public interface GuiInterfaceforMultitool {

    public void initGui();

    public void connectedNotification(boolean a, String connectedPort);

    public void displayWarningMessage(String s);

    public void lastCommandReceivedDisplay(String s);

    public void updateAvailablePortsList(Vector<String> list);

    public Rectangle getBounds();

}
