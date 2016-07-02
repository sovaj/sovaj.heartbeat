package com.ypg.car.monitoring.monitors.mock;

import com.dumbster.smtp.SimpleSmtpServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

/**
 * Cr�ation d'un serveur SMTP light.
 */
public final class MockSMTPServer {

    /**
     * Serveur SMTP
     */
    private static SimpleSmtpServer server;

    /**
     * Port HTTP
     */
    private static int port;

    /**
     * Constructeur par d�faut priv� car c'est une classe utilitaire
     */
    private MockSMTPServer() {
    }

    /**
     * D�marrage du serveur
     * 
     * @throws Exception
     */
    public static void startServer() throws Exception {
        // D�termination d'un port libre pour le serveur
        port = getFreeServerPort();
        server = SimpleSmtpServer.start(port);
    }

    /**
     * Arr�t du serveur
     * 
     * @throws Exception
     */
    public static void stopServer() throws Exception {
        server.stop();
    }

    /**
     * Test si le port 18025 est libre. Si le port n'est pas disponible, un
     * nouveau port est test� au hasard dans l'intervalle 18025-18125 dans la
     * limite de 10 tests.
     * 
     * @return un port libre entre 18025 et 18125
     */
    private static int getFreeServerPort() {
        int port = 18025;

        final Random random = new Random();
        int i = 0;
        while (!isPortAvailable(port) && i < 10) {
            port = 18025 + random.nextInt(100);
            i++;
        }
        return port;
    }

    /**
     * Test si le port est disponible
     * 
     * @param port le num�ro de port
     * @return true ou false
     */
    private static boolean isPortAvailable(int port) {
        try {
            ServerSocket socket = new ServerSocket(port);
            socket.close();
            socket = null;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @return le port
     */
    public static int getPort() {
        return port;
    }
}
