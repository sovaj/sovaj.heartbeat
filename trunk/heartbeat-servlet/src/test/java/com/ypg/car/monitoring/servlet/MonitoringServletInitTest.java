package com.ypg.car.monitoring.servlet;

import com.ypg.car.monitoring.api.IMonitoringService;
import junit.framework.TestCase;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletException;

/**
 * Tests de la m�thode init de {@link MonitoringServlet}.
 */
public class MonitoringServletInitTest extends TestCase {

    /**
     * Servlet � tester.
     */
    private MonitoringServlet servlet;

    /**
     * Contexte Spring.
     */
    private ApplicationContext springContext;

    /**
     * Initialisation du test.
     *
     * @throws Exception tout type d'exception
     */
    public void setUp() throws Exception {
        // Construction de la servlet de monitoring :
        this.servlet = new MonitoringServlet();
        // Contexte Spring : le fichier XML a le nom du test.
        final String springFile = String.format("%s.xml", getName());
        this.springContext = new ClassPathXmlApplicationContext(springFile);
        servlet.setSpringContext(springContext);
    }

    /**
     * Cas o� la servlet s'initialise bien.
     *
     * @throws ServletException exception
     */
    public void testInitOk() throws ServletException {
        servlet.init();
        assertNotNull("init spring", servlet.getSpringContext());
        //assertNotNull("service", servlet.getService());
    }

    /**
     * Cas o� aucun bean correspondant n'a �t� trouv�.
     *
     * @throws ServletException exception
     */
    public void testInitAucunBean() throws ServletException {
        IMonitoringService service = null;
        try {
            servlet.init();
            service = servlet.getMonitoringServiceBean();
            fail("aucune exception lev�e");
        } catch (ServletException ex) {
            assertTrue(ex.getMessage().startsWith("failed to initialize MonitoringServlet"));
        }
        assertNotNull("init spring", servlet.getSpringContext());
        assertNull("service", service);
    }

    /**
     * Cas o� plusieurs beans correspondants ont �t� trouv�s.
     *
     * @throws ServletException exception
     */
    public void testInitPlusieursBeans() throws ServletException {
        IMonitoringService service = null;
        try {
            servlet.init();
            service = servlet.getMonitoringServiceBean();
            fail("aucune exception lev�e");
        } catch (ServletException ex) {
            assertTrue(ex.getMessage().startsWith("failed to initialize MonitoringServlet"));
        }
        assertNotNull("init spring", servlet.getSpringContext());
        assertNull("service", service);
    }

}
