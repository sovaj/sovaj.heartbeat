
package com.ypg.car.monitoring.monitors.mock;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

import javax.servlet.http.HttpServlet;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Cr�ation d'un serveur HTTP light ayant une servlet FakeServlet sur /fake avec
 * un port choisi al�atoirement. Utiliser {@link #getResourceURL()} pour
 * r�cup�rer son chemin complet
 */
public class MockHTTPServer {

    /**
     * Mock servlet URI
     */
    private static final String FAKE_SERVLET_URI = "/fake";

    /**
     * Serveur HTTP
     */
    private Server server;

    /**
     * Contexte du serveur Http, permettant d'ajouter des servlet, des
     * filtres...
     */
    private Context context;

    /**
     * Port HTTP sur lequel tournera le serveur
     */
    private int port;

    /**
     * Constructeur par d�faut priv� car c'est une classe utilitaire
     */
    public MockHTTPServer() {
    }

    /**
     * D�marrage du serveur
     * 
     * @throws Exception
     */
    public void start() throws Exception {
        // D�termination d'un port libre pour le serveur
        port = getFreeServerPort();

        server = new Server(port);

        server.setStopAtShutdown(true);
        context = new Context(server, "/");

        createDefaultServlet();

        server.start();
    }

    /** Ajout d'une servlet par d�faut */
    private void createDefaultServlet() {
        addServlet(new FakeServlet(), FAKE_SERVLET_URI);
    }

    /**
     * Arr�t du serveur
     * 
     * @throws Exception
     */
    public void stop() throws Exception {
        server.stop();
        server.destroy();
    }

    /**
     * Test si le port 18180 est libre. Si le port n'est pas disponible, un
     * nouveau port est test� au hasard dans l'intervalle 18180-18280 dans la
     * limite de 10 tests.
     * 
     * @return un port libre entre 18180 et 18280
     */
    private int getFreeServerPort() {
        int port = 18180;

        final Random random = new Random();
        int i = 0;
        while (!isPortAvailable(port) && i < 10) {
            port = 18180 + random.nextInt(100);
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
    private boolean isPortAvailable(int port) {
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
     * @return le port du serveur
     */
    public int getPort() {
        return port;
    }

    /**
     * Ajout d'une servlet sur le chemin "path"
     * 
     * @param servlet une servlet
     * @param path un chemin
     */
    public void addServlet(HttpServlet servlet, String path) {
        context.addServlet(new ServletHolder(servlet), path);
    }

    /**
     * @return Retourne l'URL du serveur avec son port 
     */
    public String getServerURL() {
        return "http://localhost:" + port;
    }

    /**
     * @return Retourne l'URL de la ressource HTTP mont�e
     */
    public String getFakeServletURL() {
        return getServerURL() + FAKE_SERVLET_URI;
    }
}
