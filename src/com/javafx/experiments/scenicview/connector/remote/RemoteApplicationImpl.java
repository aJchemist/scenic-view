package com.javafx.experiments.scenicview.connector.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import com.javafx.experiments.scenicview.connector.StageID;
import com.javafx.experiments.scenicview.connector.event.MousePosEvent;

public class RemoteApplicationImpl extends UnicastRemoteObject implements RemoteApplication {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    RemoteApplication application;

    public RemoteApplicationImpl(final RemoteApplication browser, final int port) throws RemoteException {
        try {
            this.application = browser;
            RMIUtils.bindApplication(this, port);
        } catch (final Exception e) {
            throw new RemoteException("Error starting agent", e);
        }

    }

    @Override public void sendInfo(final String info) throws RemoteException {
        application.sendInfo(info);
    }

    public static RemoteScenicView scenicView;

    public static void main(final String[] args) throws RemoteException, InterruptedException {
        final int port = Integer.parseInt(args[0]);
        new RemoteApplicationImpl(new RemoteApplication() {

            @Override public void sendInfo(final String info) {
                System.out.println("INFO:" + info);

            }
        }, port);
        System.out.println("Remote application launched");

        RMIUtils.findScenicView(new Observer() {

            @Override public void update(final Observable o, final Object obj) {
                scenicView = (RemoteScenicView) obj;
                try {
                    scenicView.onAgentStarted(port);
                } catch (final RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    scenicView.dispatchEvent(new MousePosEvent(new StageID(0, 0), "1024x345"));
                } catch (final RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });

        while (scenicView == null) {
            Thread.sleep(1000);
        }
        System.out.println("ScenicView found:" + scenicView);

    }

}
