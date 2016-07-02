
package com.ypg.car.monitoring.monitors;

import com.ypg.car.monitoring.api.AbstractMonitor;
import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Cette classe teste une ressource HTTP.
 */
public class HTTPMonitor extends AbstractMonitor {

    /**
     * Time out par d�faut.
     */
    private static final int DEFAULT_TIMEOUT = 10000;

    /**
     * Message d'erreur.
     */
    private static final String MSG_HTTP_RESOURCE_NOT_FOUND = "HTTP resource not found: ";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NO_URL = "no URL set for HTTPMonitor";

    /**
     * URL de la ressource HTTP � tester.
     */
    private String url;

    /**
     * Liste de code retour HTTP valides pour le test.
     */
    private Set<String> okCodes;

    /**
     * Temps d'attente maximum pour �tablir une connexion. Une valeur de 0
     * indique un temps infini. Valeur en milliseconds.
     */
    private int connectionTimeout = DEFAULT_TIMEOUT;

    /**
     * Temps d'attente maximum pour recevoir des donn�es. Une valeur de 0
     * indique un temps infini. Valeur en milliseconds.
     */
    private int socketTimeout = DEFAULT_TIMEOUT;

    /**
     * Constructeur par d�faut. Il n'est pas conseill� de l'utiliser, sauf via
     * Spring.
     */
    public HTTPMonitor() {
        super("HTTP lookup", Type.URL);
    }

    /**
     * Cr�ation d'un {@link HTTPMonitor}
     * 
     * @param aURL URL de la ressource HTTP � tester
     * @param aOKCodes proprit�t�s d'environnement de la queue JMS
     */
    public HTTPMonitor(String aURL, Set<String> aOKCodes) {
        this(aURL, aOKCodes, null);
    }

    /**
     * Cr�ation d'un {@link HTTPMonitor}
     * 
     * @param aURL URL de la ressource HTTP � tester
     * @param aOKCodes proprit�t�s d'environnement de la queue JMS
     * @param desc description du test
     */
    public HTTPMonitor(String aURL, Set<String> aOKCodes, String desc) {
        super("HTTP lookup,URL=" + aURL, Type.URL, desc);
        this.url = aURL;
        this.okCodes = aOKCodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doMonitor(TestElement monitoredElement) {

        // V�rification du param�trage du monitor :
        if (this.url == null || this.url.length() == 0) {
            monitoredElement.setTestIsKo(MSG_NO_URL);
        } else {

            // initialisation par d�faut des codes retours valides
            // si non d�finis
            if (okCodes == null || okCodes.isEmpty()) {
                okCodes = new HashSet<String>();
                okCodes.add("200");
            }

            HttpURLConnection urlConn = null;
            try {
                final URL myUrl = new URL(url);
                urlConn = (HttpURLConnection ) myUrl.openConnection();
                urlConn.setConnectTimeout(connectionTimeout);
                urlConn.setReadTimeout(socketTimeout);
                urlConn.connect();

                if (okCodes.contains(String.valueOf(urlConn.getResponseCode()))) {
                    monitoredElement.setTestIsOk();
                } else {
                    monitoredElement.setTestIsKo(getResourceNotFoundErrorMsg());
                }

            } catch (SocketTimeoutException ex) {
                monitoredElement.setTestIsKo(getResourceNotFoundErrorMsg(), ex);
            } catch (IOException ex) {
                monitoredElement.setTestIsKo(getResourceNotFoundErrorMsg(), ex);
            } finally {
                if (urlConn != null) {
                    urlConn.disconnect();
                }
            }
        }
    }

    /**
     * Construction d'un message d'erreur de type "resource not found".
     * 
     * @return message d'erreur
     */
    private String getResourceNotFoundErrorMsg() {
        return String.format(MSG_HTTP_RESOURCE_NOT_FOUND + "'%s'", url);
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @param okCodes the okCodes to set
     */
    public void setOkCodes(Set<String> okCodes) {
        this.okCodes = okCodes;
    }

    /**
     * @param connectionTimeout the connectionTimeout to set
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * @param socketTimeout the socketTimeout to set
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

}
