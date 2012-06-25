package com.javafx.experiments.scenicview.utils;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

import javafx.stage.Stage;

import com.javafx.experiments.scenicview.ScenicView;
import com.javafx.experiments.scenicview.connector.AppController;
import com.javafx.experiments.scenicview.connector.remote.RemoteScenicViewImpl;
import com.sun.javafx.application.PlatformImpl;
import com.sun.tools.attach.*;

/**
 * 
 * @author Jonathan
 */
public class JavaFXAppFinder {

    private static final List<Stage> stages = new ArrayList<Stage>();

    private static final String JAVAFX_SYSTEM_PROPERTIES_KEY = "javafx.version";

    public List<VirtualMachine> getRunningJavaFXApplications() {
        final List<VirtualMachineDescriptor> machines = VirtualMachine.list();
        final List<VirtualMachine> javaFXMachines = new ArrayList<VirtualMachine>();

        for (int i = 0; i < machines.size(); i++) {
            final VirtualMachineDescriptor vmd = machines.get(i);
            try {
                final VirtualMachine virtualMachine = VirtualMachine.attach(vmd);
                final Map sysPropertiesMap = virtualMachine.getSystemProperties();
                if (sysPropertiesMap.containsKey(JAVAFX_SYSTEM_PROPERTIES_KEY)) {
                    javaFXMachines.add(virtualMachine);
                } else {
                    virtualMachine.detach();
                }
            } catch (final AttachNotSupportedException ex) {
                ex.printStackTrace();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }

        return javaFXMachines;
    }

    ScenicView view;

    public JavaFXAppFinder() {
        PlatformImpl.startup(new Runnable() {

            @Override public void run() {
                final Stage stage = new Stage();
                // workaround for RT-10714
                stage.setWidth(640);
                stage.setHeight(800);
                stage.setTitle("Scenic View v" + ScenicView.VERSION);
                view = new ScenicView(new ArrayList<AppController>(), stage);
                ScenicView.show(view, stage);
            }
        });
        while (view != null) {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            RemoteScenicViewImpl.main(new String[0]);
        } catch (final RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (final InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        final List<VirtualMachine> machines = getRunningJavaFXApplications();

        for (final Iterator<VirtualMachine> iterator = machines.iterator(); iterator.hasNext();) {
            final VirtualMachine virtualMachine = iterator.next();
            System.out.println(virtualMachine);
        }
        final File f = new File("./ScenicView.jar");
        System.out.println(f.getAbsolutePath());

        try {
            for (final VirtualMachine machine : machines) {
                System.out.println("Loading agent for:" + machine);
                machine.loadAgent(f.getAbsolutePath());
                machine.detach();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        for (final Iterator<Stage> iterator = stages.iterator(); iterator.hasNext();) {
            final Stage stage = iterator.next();
            System.out.println(stage);
        }
    }
}
