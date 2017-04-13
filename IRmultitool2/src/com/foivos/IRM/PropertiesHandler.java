package com.foivos.IRM;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {

    private Properties properties = new Properties();

    private String propertiesFilename = new String("config.properties");

    PropertiesHandler() {

        loadProperties();

    }

    // ----------------------------------
    private void loadProperties() {

        File propFile = new File(propertiesFilename);
        FileOutputStream oFile = null;
        FileInputStream iFile = null;

        try {

            if (propFile.exists()) {// file exists
                System.out.println("properties file exists. try to load...");
                iFile = new FileInputStream(propFile);
                properties.load(iFile);
            } else {
                System.out.println("properties file not exist. creating...");
                propFile.createNewFile();
                oFile = new FileOutputStream(propFile, true);

                // default properties
                properties.setProperty("GUIBoundsX", Integer.toString(50));
                properties.setProperty("GUIBoundsY", Integer.toString(100));
                properties.setProperty("GUIBoundsWidth", Integer.toString(300));
                properties
                        .setProperty("GUIBoundsHeight", Integer.toString(250));

                properties.store(oFile, null);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (oFile != null)
                    oFile.close();
                if (iFile != null)
                    iFile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // properties.list(System.out);

    }

    // ------------------------------
    public Rectangle getGuiBounds() {
        return new Rectangle(Integer.parseInt(properties
                .getProperty("GUIBoundsX")), Integer.parseInt(properties
                .getProperty("GUIBoundsY")), Integer.parseInt(properties
                .getProperty("GUIBoundsWidth")), Integer.parseInt(properties
                .getProperty("GUIBoundsHeight")));

    }

    // ------------------------------
    public void setGUIBounds(Rectangle rec) {
        properties.setProperty("GUIBoundsX", Integer.toString(rec.x));
        properties.setProperty("GUIBoundsY", Integer.toString(rec.y));
        properties.setProperty("GUIBoundsWidth", Integer.toString(rec.width));
        properties.setProperty("GUIBoundsHeight", Integer.toString(rec.height));

    }

    // ------------------------------
    public void saveProperties() {
        FileOutputStream oFile = null;
        File propFile = new File(propertiesFilename);
        try {
            propFile.createNewFile();
            oFile = new FileOutputStream(propFile);
            properties.store(oFile, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oFile != null)
                    oFile.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}// end class
