package com.foivos.IRM;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CloseWindow extends WindowAdapter implements ActionListener {

    private Frame f;

    public CloseWindow() {
        System.out.println(" close window constructor");
    }

    public CloseWindow(Frame f) {
        System.out.println(" close window constructor with argument");
        this.f = f;

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("window closing");
        Frame source = (Frame) e.getSource();
        source.dispose();

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        System.out.println("action performed");
        f.dispose();
    }

}