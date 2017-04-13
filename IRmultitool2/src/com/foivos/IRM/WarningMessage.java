package com.foivos.IRM;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;

public class WarningMessage extends Frame {

    public WarningMessage(String msg) {
        super.setTitle("Warning");
        this.setLayout(null);
        this.setFont(new Font("TimesRoman", Font.PLAIN, 14));
        this.setBackground(Color.lightGray);
        this.setLocation(855, 200);
        this.setSize(400, 160);
        this.toFront();
        this.setResizable(false);
        this.addWindowListener(new CloseWindow());

        Label lb = new Label(msg, Label.CENTER);
        add(lb);
        lb.setFont(new Font("TimesRoman", Font.PLAIN, 14));
        lb.setBounds(0, 15, 400, 80);
        lb.setVisible(true);

        Button close = new Button("Close");
        close.addActionListener(new CloseWindow(this));
        this.add(close);
        close.setBounds(165, 100, 70, 30);
        close.setVisible(true);

        this.setVisible(true);

    }

}