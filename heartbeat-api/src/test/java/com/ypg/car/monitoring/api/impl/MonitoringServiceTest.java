/**
 * Generali Solutions d'assurances - Tous droits r�serv�s &copy; 2007 - 2010
 */
package com.ypg.car.monitoring.api.impl;

import com.ypg.car.monitoring.api.jaxb2.Status;
import org.w3c.dom.Document;

import java.util.LinkedList;
import java.util.List;

/**
 * Test de MonitoringServiceImpl.
 */
public class MonitoringServiceTest extends AbstractXPathTestCase {

    /**
     * Nom de webapp.
     */
    private static final String WEBAPPNAME = "testWebApp";

    /**
     * Service test�.
     */
    private DefaultMonitoringService service;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        service = new DefaultMonitoringService(WEBAPPNAME,null,null);

    }

    /**
     * Test du constructeur.
     */
    public void testInit() {
        assertNotNull(service.getTechnicalTests());
        assertNotNull(service.getDependencyTests());
        assertEquals(0, service.getTechnicalTests().size());
        assertEquals(0, service.getDependencyTests().size());
    }

    /**
     * Test de addTechnicalTest.
     */
    public void testAddTechnicalTest() {
        final int nbTests = service.getTechnicalTests().size();
        final MockMonitor mon = new MockMonitor();
        service.addTechnicalTest(mon);
        assertEquals(nbTests + 1, service.getTechnicalTests().size());
        assertSame(mon, service.getTechnicalTests().get(0));
    }

    /**
     * Test de addDependencyTest.
     */
    public void testAddDependencyTest() {
        final int nbTests = service.getDependencyTests().size();
        final MockMonitor mon = new MockMonitor();
        service.addDependencyTest(mon);
        assertEquals(nbTests + 1, service.getDependencyTests().size());
        assertSame(mon, service.getDependencyTests().get(0));
    }

    /**
     * Test de runTests sans aucun test d�clar�.
     *
     * @throws Exception tout type
     */
    public void testRunTestsNoDeclaredTest() throws Exception {
        final String xml = service.runTests(true,true);
        final Document doc = getValidatedDOM(xml);
        assertXpathEvaluatesTo(WEBAPPNAME, "/application-test/@webAppName", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("This application has no technical tests.",
                        "/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * Test de runTests avec tests techniques et de d�pendances. Il s'agit
     * uniquement de tester MonitoringService. Les tests plus complets sont ceux
     * de MonitoringSession. Voir {@link MonitoringSessionTest}.
     *
     * @throws Exception tout type
     */
    public void testRunTestsOK() throws Exception {
        final List technicalTests = new LinkedList();
        technicalTests.add(new MockMonitor());
        technicalTests.add(new MockMonitor());
        final List dependencyTests = new LinkedList();
        dependencyTests.add(new MockMonitor());
        dependencyTests.add(new MockMonitor());
        service.setTechnicalTests(technicalTests);
        service.setDependencyTests(dependencyTests);

        final String xml = service.runTests(true,true);
        final Document doc = getValidatedDOM(xml);
        assertXpathEvaluatesTo(WEBAPPNAME, "/application-test/@webAppName", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[2]/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/test[2]/@status", doc);
    }
}
