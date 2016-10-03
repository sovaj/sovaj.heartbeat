package io.sovaj.heartbeat.monitors;

import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;
import io.sovaj.heartbeat.monitors.mock.MockSMTPServer;

import javax.mail.MessagingException;
import java.util.Properties;

/**
 * Test du SMTPMonitor. ATTENTION : ce test n'est pas thread-safe.
 */
public class SMTPMonitorTest extends AbstractMonitorTestCase {

    /**
     * Nom d'h�te du serveur SMTP
     */
    private static final String HOST = "localhost";

    /**
     * Port du serveur SMTP
     */
    private static String port;

    /**
     * Propri�t�s du serveur SMTP
     */
    private Properties properties;

    /**
     * Initialisation de chaque test.
     * 
     * @throws Exception tout type d'exception
     */
    public void setUp() throws Exception {
        super.setUp();
        MockSMTPServer.startServer();
        port = String.valueOf(MockSMTPServer.getPort());
        properties = new Properties();
        properties.put("mail.smtp.host", HOST);
        properties.put("mail.smtp.port", port);
    }

    /**
     * Tear Down
     */
    public void tearDown() throws Exception {
        super.tearDown();
        MockSMTPServer.stopServer();
    }

    /**
     * Test cas nominal.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOK() throws Exception {
        // Ex�cution du test :
        final TestElement elem = new SMTPMonitor(HOST, port, "testOK").monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.SMTP, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "SMTP lookup,host=" + HOST + ",port=" + port, elem.getName());
        assertEquals("description", "testOK", elem.getDescription());
    }

    /**
     * Test cas nominal avec constructeur par d�faut.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOKDefaultConstructor() throws Exception {
        // Ex�cution du test :
        final SMTPMonitor mon = new SMTPMonitor();
        mon.setName("SMTP lookup,host=" + HOST + ",port=" + port);
        mon.setHost(HOST);
        mon.setPort(port);
        mon.setProperties(properties);
        mon.setDescription("desc");
        final TestElement elem = mon.monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.SMTP, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "SMTP lookup,host=" + HOST + ",port=" + port, elem.getName());
        assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test KO MessagingException
     * 
     * @throws Exception tout type d'exception
     */
    public void testKOMessagingException() throws Exception {
        final String host = "jenexistepas";

        // Ex�cution du test :
        final TestElement elem = new SMTPMonitor(host, port, "desc").monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("errorMessage", "SMTP server not found: '" + host + ":" + port + "'", elem.getErrorMessage());
        assertTrue("exception", elem.getException() instanceof MessagingException);
        assertEquals("type", Type.SMTP, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "SMTP lookup,host=" + host + ",port=" + port, elem.getName());
        assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test KO port non entier.
     * 
     * @throws Exception tout type d'exception
     */
    public void testKONotIntegerPort() throws Exception {
        // Ex�cution du test :
        final TestElement elem = new SMTPMonitor(HOST, "chaine", "testKONotIntegerPort").monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("errorMessage", "not numeric port set for SMTPMonitor", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.SMTP, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "SMTP lookup,host=" + HOST + ",port=chaine", elem.getName());
        assertEquals("description", "testKONotIntegerPort", elem.getDescription());
    }

    /**
     * Test KO host null ou vide.
     * 
     * @throws Exception tout type d'exception
     */
    public void testKOHostIsBlank() throws Exception {
        assertMissingHostConfigParam(null);
        assertMissingHostConfigParam("");
    }

    /**
     * @param host le nom d'h�te du serveur SMTP
     */
    private void assertMissingHostConfigParam(String host) {
        // Ex�cution du test :
        final TestElement elem = new SMTPMonitor(host, port).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("message", "no host set for SMTPMonitor", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.SMTP, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "SMTP lookup,host=" + host + ",port=" + port, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test KO port null ou vide.
     * 
     * @throws Exception tout type d'exception
     */
    public void testKOPortIsBlank() throws Exception {
        assertMissingPortConfigParam(null);
        assertMissingPortConfigParam("");
    }

    /**
     * @param port le port du serveur SMTP
     */
    private void assertMissingPortConfigParam(String port) {
        // Ex�cution du test :
        final TestElement elem = new SMTPMonitor(HOST, port).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("message", "no port set for SMTPMonitor", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.SMTP, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "SMTP lookup,host=" + HOST + ",port=" + port, elem.getName());
        assertNull("description", elem.getDescription());
    }

}
