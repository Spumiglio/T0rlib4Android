package net.sf.controller.network;

import android.content.Context;

import net.sf.msopentech.thali.java.toronionproxy.OnionProxyManager;
import net.sf.msopentech.thali.java.toronionproxy.android.AndroidOnionProxyManager;



import java.io.IOException;

public class AndroidTorRelay {
    private OnionProxyManager onionProxyManager;

    private static final int TOTAL_SEC_PER_STARTUP = 4 * 60;
    private static final int TRIES_PER_STARTUP = 5;

    private static final String PROXY_LOCALHOST = "127.0.0.1";




    public AndroidTorRelay(Context ctx, String torDirectory) throws IOException {
        onionProxyManager = new AndroidOnionProxyManager(ctx, torDirectory);
        this.initTor();
    }


    public ServiceDescriptor createHiddenService(final int localPort, final int servicePort) throws IOException {
        return createHiddenService(localPort, servicePort, null);
    }

    public ServiceDescriptor createHiddenService(final int localPort, final int servicePort,
                                                 final NetLayerStatus listener) throws IOException {

        final String hiddenServiceName = onionProxyManager.publishHiddenService(servicePort, localPort);
        final ServiceDescriptor serviceDescriptor = new ServiceDescriptor(hiddenServiceName,
                localPort, servicePort);
        if (listener != null)
            onionProxyManager.attachHiddenServiceReadyListener(serviceDescriptor, listener);
        return serviceDescriptor;
    }

    public ServiceDescriptor createHiddenService(int port, NetLayerStatus listener)
            throws IOException {
        return createHiddenService(port, port, listener);
    }


    public void initTor()
            throws IOException {



        try {
            if (!onionProxyManager.startWithRepeat(TOTAL_SEC_PER_STARTUP, TRIES_PER_STARTUP)) {
                throw new IOException("Could not Start Tor.");
            } else {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            onionProxyManager.stop();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public void addHiddenServiceReadyListener(ServiceDescriptor serviceDescriptor,
                                              NetLayerStatus listener) throws IOException {
        onionProxyManager.attachHiddenServiceReadyListener(serviceDescriptor, listener);
    }

    public void ShutDown() throws IOException {
        onionProxyManager.stop();
    }

    public int getSocksPort() throws IOException {
        try {
            return onionProxyManager.getIPv4LocalHostSocksPort();
        } catch (IOException e) {

            throw new IOException(e);
        }

    }


}
