
package com.ypg.car.monitoring.servlet;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.custommonkey.xmlunit.XMLTestCase;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * Tests de {@link MonitoringServlet}. Le principe : on fait tourner la servlet
 * dans Jetty. Il ne s'agit pas de tester l'API de monitoring mais uniquement la
 * servlet elle-m�me. C'est pourquoi l'API de monitoring est ici remplac�e par
 * un bouchon. On a fourni la config Spring minimale pour que la servlet
 * d�marre.
 */
public class MonitoringServletTestIT extends XMLTestCase {

    /**
     * Code retour HTTP signifiant "OK".
     */
    private static final int HTTP_OK = 200;

    /**
     * Port sur lequel Jetty va tourner.
     */
    private static final int SERVER_PORT = 18081;
    public static final int HTTP_KO = 500;

    /**
     * Serveur Jetty embarqu�.
     */
    private Server jettyServer;

    /**
     * Initialisation du test.
     * 
     * @throws Exception tout type d'exception
     */
    @Override
    public void setUp() throws Exception {

        // Construction de la servlet de monitoring :
        final MonitoringServlet servlet = new MonitoringServlet();

        // Param�trage de Jetty, on y installe notre servlet :
        jettyServer = new Server(SERVER_PORT);
        final Context ctx = new Context(jettyServer, "/", Context.SESSIONS);
        final ServletHolder holder = new ServletHolder(servlet);
        ctx.addServlet(holder, "/*");

        // Permet de charger Spring :
        // Les noms des fichiers Spring XML charg�s d�pendent du nom du test.
        // Ceci permet d'avoir facilement un fichier XML diff�rent par test.
        final String springConfigFile = String.format("classpath:%s.xml", getName());
        ctx.getInitParams().put("contextConfigLocation", springConfigFile);
        ctx.addEventListener(new ContextLoaderListener());

        // D�marrage de Jetty :
        jettyServer.start();
    }

    /**
     * Cleanup apr�s ex�cution du test.
     * 
     * @throws Exception tout type d'exception
     */
    @Override
    public void tearDown() throws Exception {
        // Arr�t de Jetty :
        jettyServer.stop();
    }

    /**
     * Cas minimal : aucun test d�clar�.
     * 
     * @throws Exception tout type d'exception
     */
    public void testCasMinimal() throws Exception {

        // Appel de la servlet :
        final HttpClient httpClient = new HttpClient();
        final HttpMethod method = new GetMethod("http://localhost:" + SERVER_PORT + "/Monitoring");
        httpClient.executeMethod(method);

        // V�rification du code retour HTTP :
        if (method.getStatusCode() != HTTP_KO) {
            // Le code retour est KO.
            fail(String.format("code retour HTTP : re�u %d au lieu de %d, r�ponse : %s", method.getStatusCode(),
                            HTTP_OK, method.getResponseBodyAsString()));
        }

        // V�rification du type MIME et de l'encoding renvoy� par la servlet :
        final Header[] headers = method.getResponseHeaders("Content-Type");
        assertEquals("type MIME et encoding", "text/xml; charset=utf-8", headers[0].getValue());

        // V�rification que la r�ponse est celle renvoy�e par l'API :
        // Note : les tests du XML restent tr�s superficiels. En effet, le but
        // n'est pas de tester le XML lui-m�me car il est d�j� test� via les
        // tests unitaires du module "monitoring-api". Le but est juste de
        // v�rifier le bon fonctionnement de la servlet.
        final Document inDocument = getDocument(method.getResponseBodyAsString());

        // Je dois avoir un seul �l�ment test dans technical-tests :
        assertXpathEvaluatesTo("1", "count(/application-test/technical-tests/test)", inDocument);
        // Je ne dois pas avoir de dependency test :
        assertXpathEvaluatesTo("0", "count(/application-test/dependency-tests)", inDocument);
        // Status KO :
        assertXpathEvaluatesTo("KO", "/application-test/technical-tests/test/@status", inDocument);
        // Message d'erreur correspondant � la situation :
        assertXpathEvaluatesTo("This application has no technical tests.",
                        "/application-test/technical-tests/test/errorMessage", inDocument);

        // Nom de la webapp doit �tre renseign� :
        assertXpathEvaluatesTo("testWebApp", "/application-test/@webAppName", inDocument);
    }

    /**
     * Cas complet : divers tests d�clar�s.
     * 
     * @throws Exception tout type d'exception
     */
    public void testCasComplet() throws Exception {

        // Appel de la servlet :
        final HttpClient httpClient = new HttpClient();
        final HttpMethod method = new GetMethod("http://localhost:" + SERVER_PORT + "/Monitoring");
        httpClient.executeMethod(method);

        // V�rification du code retour HTTP :
        if (method.getStatusCode() != HTTP_OK) {
            // Le code retour est KO.
            fail(String.format("code retour HTTP : re�u %d au lieu de %d, r�ponse : %s", method.getStatusCode(),
                            HTTP_OK, method.getResponseBodyAsString()));
        }

        // V�rification du type MIME et de l'encoding renvoy� par la servlet :
        final Header[] headers = method.getResponseHeaders("Content-Type");
        assertEquals("type MIME et encoding", "text/xml; charset=utf-8", headers[0].getValue());

        // V�rification que la r�ponse est celle renvoy�e par l'API :
        // Note : les tests du XML restent tr�s superficiels. En effet, le but
        // n'est pas de tester le XML lui-m�me car il est d�j� test� via les
        // tests unitaires du module "monitoring-api". Le but est juste de
        // v�rifier le bon fonctionnement de la servlet.
        final Document inDocument = getDocument(method.getResponseBodyAsString());

        // Je dois avoir 2 �l�ments tests dans technical-tests :
        assertXpathEvaluatesTo("2", "count(/application-test/technical-tests/test)", inDocument);
        // Je dois avoir 2 dependency tests :
        assertXpathEvaluatesTo("2", "count(/application-test/dependency-tests/test)", inDocument);
        // Status OK :
        assertXpathEvaluatesTo("OK", "/application-test/technical-tests/test[1]/@status", inDocument);
        assertXpathEvaluatesTo("OK", "/application-test/technical-tests/test[2]/@status", inDocument);
        assertXpathEvaluatesTo("OK", "/application-test/dependency-tests/test[1]/@status", inDocument);
        assertXpathEvaluatesTo("OK", "/application-test/dependency-tests/test[2]/@status", inDocument);

        // Nom de la webapp doit �tre renseign� :
        assertXpathEvaluatesTo("testWebApp", "/application-test/@webAppName", inDocument);
        assertXpathEvaluatesTo("testNode", "/application-test/@nodeName", inDocument);
        assertXpathEvaluatesTo("testAppServer", "/application-test/@appServerName", inDocument);
    }

    /**
     * Parsing du XML renvoy� par la servlet.
     * 
     * @param xml r�ponse
     * @return DOM
     * @throws Exception tout type d'exception
     */
    private Document getDocument(String xml) throws Exception {

        // System.out.println(xml);
        final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return db.parse(new InputSource(new StringReader(xml)));
    }

}
