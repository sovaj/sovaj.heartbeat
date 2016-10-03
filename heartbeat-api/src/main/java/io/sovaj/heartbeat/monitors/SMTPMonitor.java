
package io.sovaj.heartbeat.monitors;

import io.sovaj.heartbeat.api.AbstractMonitor;
import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import java.util.Properties;

/**
 * Cette classe teste la disponibilit� d'un serveur SMTP.
 */
public class SMTPMonitor extends AbstractMonitor {

    /**
     * Message d'erreur.
     */
    private static final String MSG_SMTP_SERVER_NOT_FOUND = "SMTP server not found: ";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NO_HOST = "no host set for SMTPMonitor";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NO_PORT = "no port set for SMTPMonitor";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NOT_NUMERIC_PORT = "not numeric port set for SMTPMonitor";

    /**
     * Nom d'h�te du serveur SMTP.
     */
    private String host;

    /**
     * Port du serveur SMTP.
     */
    private String port;

    /**
     * Propri�t�s du serveur SMTP.
     */
    private Properties properties;

    /**
     * Constructeur par d�faut. Il n'est pas conseill� de l'utiliser, sauf via
     * Spring.
     */
    public SMTPMonitor() {
        super("SMTP lookup", Type.SMTP);
    }

    /**
     * Cr�ation d'un {@link SMTPMonitor}
     * 
     * @param aHost nom d'h�te du serveur SMTP � tester
     * @param aPort port du serveur SMTP
     */
    public SMTPMonitor(String aHost, String aPort) {
        this(aHost, aPort, null);
    }

    /**
     * Cr�ation d'un {@link SMTPMonitor}
     * 
     * @param aHost nom d'h�te du serveur SMTP � tester
     * @param aPort port du serveur SMTP
     * @param desc description du test
     */
    public SMTPMonitor(String aHost, String aPort, String desc) {
        super("SMTP lookup,host=" + aHost + ",port=" + aPort, Type.SMTP, desc);
        this.host = aHost;
        this.port = aPort;
    }

    /**
     * {@inheritDoc}
     */
    public void doMonitor(TestElement monitoredElement) {

        // V�rification du param�trage du monitor :
        if (this.host == null || this.host.length() == 0) {
            monitoredElement.setTestIsKo(MSG_NO_HOST);
        } else if (this.port == null || this.port.length() == 0) {
            monitoredElement.setTestIsKo(MSG_NO_PORT);
        } else if (isInteger(this.port)) {

            initServerProperties();

            try {
                final Session session = Session.getDefaultInstance(properties);
                final Transport transport = session.getTransport("smtp");
                transport.connect(host, Integer.parseInt(port), null, null);
                transport.close();
                monitoredElement.setTestIsOk();
            } catch (MessagingException ex) {
                monitoredElement.setTestIsKo(getResourceNotFoundErrorMsg(), ex);
            }
        } else {
            monitoredElement.setTestIsKo(MSG_NOT_NUMERIC_PORT);
        }
    }

    /**
     * Initialise les propri�t�s du serveur SMTP avec le nom d'h�te et le port
     * si ce n'est pas fait dans la configuration
     */
    private void initServerProperties() {
        if (properties == null) {
            properties = new Properties();
        }
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
    }

    /**
     * Test si la valeur est un entier
     * 
     * @param value la valeur � tester
     * @return true or false
     */
    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Construction d'un message d'erreur de type "resource not found".
     * 
     * @return message d'erreur
     */
    private String getResourceNotFoundErrorMsg() {
        return String.format(MSG_SMTP_SERVER_NOT_FOUND + "'%s:%s'", host, port);
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
