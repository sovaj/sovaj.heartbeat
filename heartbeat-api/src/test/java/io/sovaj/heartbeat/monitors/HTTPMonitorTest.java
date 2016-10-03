
package io.sovaj.heartbeat.monitors;

import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;
import io.sovaj.heartbeat.monitors.mock.MockHTTPServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Test du HTTPMonitor. ATTENTION : ce test n'est pas thread-safe.
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class HTTPMonitorTest extends AbstractMonitorTestCase {

    /** Serveur sur lequel sont requ�t�es les adresses */
    private MockHTTPServer server;

    /**
     * Liste de code retour HTTP valides pour Le monitoring.
     */
    private Set<String> okCodes;

    /**
     * Initialisation de chaque test.
     * 
     * @throws Exception tout type d'exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        server = new MockHTTPServer();
        server.start();

        okCodes = new HashSet<String>();
        okCodes.add("200");
    }

    /**
     * Finalisation de chaque test.
     * 
     * @throws Exception tout type d'exception
     */
    @Override
    @After
    public void tearDown() throws Exception {
        server.stop();
        super.tearDown();
    }

    /**
     * Test OK codes null ou vide.
     */
    @Test
    public void okCodes_optionel() throws Exception {
        assertMissingOKCodesConfigParam(null);
        assertMissingOKCodesConfigParam(new HashSet<String>());
    }

    /**
     * Test de configuration manquant.
     * @param okCodes
     */
    private void assertMissingOKCodesConfigParam(Set<String> okCodes) {
        final String urlOk = server.getFakeServletURL();

        /*
         * Action
         */
        final TestElement elem = new HTTPMonitor(urlOk, okCodes, "desc").monitor();

        /*
         * Assert
         */
        assertNotNull(elem);
        Assert.assertTrue("Monitoring should be OK", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.URL, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "HTTP lookup,URL=" + urlOk, elem.getName());
        assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test URL null ou vide.
     */
    @Test
    public void param_url_est_requis() throws Exception {
        assertMissingURLConfigParam(null);
        assertMissingURLConfigParam("");
    }

    /**
     * @param url URL de la ressource HTTP
     */
    private void assertMissingURLConfigParam(String url) {
        /*
         * Action
         */
        final TestElement elem = new HTTPMonitor(url, okCodes).monitor();

        /*
         * Assert
         */
        assertNotNull(elem);
        assertTrue("Le monitoring doit �tre ko", elem.getTestIsKo());
        assertEquals("message", "no URL set for HTTPMonitor", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.URL, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "HTTP lookup,URL=" + url, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test d'url Valide.
     * @throws Exception
     */
    @Test
    public void url_valide_doit_etre_ok() throws Exception {
        final String urlOk = server.getFakeServletURL();

        /*
         * Action
         */
        final TestElement elem = new HTTPMonitor(urlOk, okCodes, "desc").monitor();

        /*
         * Assert
         */
        Assert.assertNotNull(elem);
        Assert.assertTrue("Le monitoring doit �tre ok", elem.getTestIsOk());
        Assert.assertNull("errorMessage", elem.getErrorMessage());
        Assert.assertNull("exception", elem.getException());
        Assert.assertEquals("type", Type.URL, elem.getType());
        Assert.assertNotNull("startDate", elem.getStart());
        Assert.assertNotNull("stopDate", elem.getStop());
        Assert.assertEquals("name", "HTTP lookup,URL=" + urlOk, elem.getName());
        Assert.assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test de Ko sur une url inconnu
     * @throws Exception
     */
    @Test
    public void url_inconnue_doit_etre_ko() throws Exception {
        final String urlInconnue = "http://impossible";

        /*
         * Action
         */
        final TestElement elem = new HTTPMonitor(urlInconnue, okCodes, "desc").monitor();

        /*
         * Assert
         */
        Assert.assertNotNull(elem);
        Assert.assertTrue("Le monitoring doit �tre ko", elem.getTestIsKo());
        Assert.assertEquals("errorMessage", "HTTP resource not found: '" + urlInconnue + "'", elem.getErrorMessage());
        Assert.assertTrue("exception", elem.getException() instanceof IOException);
        Assert.assertEquals("type", Type.URL, elem.getType());
        Assert.assertNotNull("startDate", elem.getStart());
        Assert.assertNotNull("stopDate", elem.getStop());
        Assert.assertEquals("name", "HTTP lookup,URL=" + urlInconnue, elem.getName());
        Assert.assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test qu'une URl qui retourne un 404 est bien KO
     */
    @Test
    public void url404_doit_etre_ko() throws Exception {
        final String url404 = server.getServerURL() + "/urlEn404";

        /*
         * Action
         */
        final TestElement elem = new HTTPMonitor(url404, okCodes, "desc").monitor();

        /*
         * Assert
         */
        Assert.assertNotNull(elem);
        Assert.assertTrue("Le monitoring doit �tre ko", elem.getTestIsKo());
        Assert.assertEquals("errorMessage", "HTTP resource not found: '" + url404 + "'", elem.getErrorMessage());
        Assert.assertNull("exception", elem.getException());
        Assert.assertEquals("type", Type.URL, elem.getType());
        Assert.assertNotNull("startDate", elem.getStart());
        Assert.assertNotNull("stopDate", elem.getStop());
        Assert.assertEquals("name", "HTTP lookup,URL=" + url404, elem.getName());
        Assert.assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test qu'un timeout a bien lieu si une URL met du temps � r�pondre.
     * <p>
     * Pour cela, une Servlet est cr��e, elle ne r�pond qu'apr�s un certain
     * temps. Le monitoring attend une r�ponse plus rapide et doit donc remonter
     * ce timeout
     */
    @Test(timeout = 5000)
    public void timeOut_doit_etre_ko() throws Exception {
        final String urlServletSansReponse = "/urlEnTimeOut";
        final String urlComplete = server.getServerURL() + urlServletSansReponse;

        // Timeout max pour attendre une reponse
        final int monitoringTimeout = 1000;

        /*
         * Temps pour que la servlet r�ponde, sup�rieur au temps autoris� pour
         * le monitoring
         */
        final int tempsReponseServlet = 2000;

        // Servlet qui met du temps � r�pondre
        server.addServlet(new HttpServlet() {
            @Override
            public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
                try {
                    Thread.sleep(tempsReponseServlet);
                } catch (InterruptedException e) {
                    // Ne peut rien faire
                }
            }
        }, urlServletSansReponse);

        // Monitore la servlet.
        final HTTPMonitor httpMonitor = new HTTPMonitor(urlComplete, okCodes, "desc");
        httpMonitor.setConnectionTimeout(monitoringTimeout);
        httpMonitor.setSocketTimeout(monitoringTimeout);

        /*
         * Action
         */
        final TestElement elem = httpMonitor.monitor();

        /*
         * Assert
         */
        Assert.assertNotNull(elem);
        Assert.assertTrue("Le monitoring doit �tre ko", elem.getTestIsKo());
        Assert.assertEquals("errorMessage", "HTTP resource not found: '" + urlComplete + "'", elem.getErrorMessage());
        Assert.assertTrue("exception", elem.getException() instanceof IOException);
        Assert.assertEquals("type", Type.URL, elem.getType());
        Assert.assertNotNull("startDate", elem.getStart());
        Assert.assertNotNull("stopDate", elem.getStop());
        Assert.assertEquals("name", "HTTP lookup,URL=" + urlComplete, elem.getName());
        Assert.assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test qui simule une reponse lente d'une servlet mais qui ne doit pas
     * d�clencher un timeout
     */
    @Test(timeout = 5000)
    public void reponse_lente_mais_pas_timeout() throws Exception {
        final String urlLente = "/urlLenteARepondre";
        final String urlComplete = server.getServerURL() + urlLente;

        // Timeout max pour attendre une reponse
        final int monitoringTimeout = 3000;

        /*
         * Temps pour que la servlet r�ponde, inf�rieur au temps autoris� pour
         * le monitoring
         */
        final int tempsReponseServlet = 1000;

        // Servlet qui met du temps � r�pondre
        server.addServlet(new HttpServlet() {
            @Override
            public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
                try {
                    Thread.sleep(tempsReponseServlet);
                } catch (InterruptedException e) {
                    // Ne peut rien faire
                }
            }
        }, urlLente);

        // Monitore la servlet.
        final HTTPMonitor httpMonitor = new HTTPMonitor(urlComplete, okCodes, "desc");
        httpMonitor.setConnectionTimeout(monitoringTimeout);
        httpMonitor.setSocketTimeout(monitoringTimeout);

        /*
         * Action
         */
        final TestElement elem = httpMonitor.monitor();

        /*
         * Assert
         */
        Assert.assertNotNull(elem);
        Assert.assertTrue("Le monitoring doit �tre ok", elem.getTestIsOk());
        Assert.assertNull("errorMessage", elem.getErrorMessage());
        Assert.assertNull("exception", elem.getException());
        Assert.assertEquals("type", Type.URL, elem.getType());
        Assert.assertNotNull("startDate", elem.getStart());
        Assert.assertNotNull("stopDate", elem.getStop());
        Assert.assertEquals("name", "HTTP lookup,URL=" + urlComplete, elem.getName());
        Assert.assertEquals("description", "desc", elem.getDescription());
    }
}
