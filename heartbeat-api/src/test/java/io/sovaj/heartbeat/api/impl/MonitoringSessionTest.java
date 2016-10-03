package io.sovaj.heartbeat.api.impl;

import io.sovaj.heartbeat.api.AbstractMonitor;
import io.sovaj.heartbeat.api.IMonitor;
import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;
import io.sovaj.heartbeat.api.jaxb2.Group;
import io.sovaj.heartbeat.api.jaxb2.Status;
import org.w3c.dom.Document;

/**
 * Tests des fonctionnalit�s de base de {@link MonitoringSession}.
 */
public class MonitoringSessionTest extends AbstractMonitoringSessionTestCase {

    /**
     * Pas de webapp sp�cifi�e.
     *
     * @throws Exception tout type d'exception
     */
    public void testNoWebAppName() throws Exception {
        monitoringSession = new MonitoringSession(null);

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("NEEDS A WEBAPP NAME !!!", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("This application has no technical tests.",
                        "/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * Pas de webapp sp�cifi�e.
     *
     * @throws Exception tout type d'exception
     */
    public void testBlankWebAppName() throws Exception {
        monitoringSession = new MonitoringSession("  ");

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("NEEDS A WEBAPP NAME !!!", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("This application has no technical tests.",
                        "/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * Aucun test technique.
     *
     * @throws Exception tout type d'exception
     */
    public void testNoTestWithWebAppName() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("This application has no technical tests.",
                        "/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * Aucun test technique.
     *
     * @throws Exception tout type d'exception
     */
    public void testNoTestWithNodeName() throws Exception {
        monitoringSession = new MonitoringSession("webAppName", "nodeName");

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertXpathEvaluatesTo("nodeName", "/application-test/@nodeName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("This application has no technical tests.",
                        "/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * Aucun test technique.
     *
     * @throws Exception tout type d'exception
     */
    public void testNoTestWithAppServerName() throws Exception {
        monitoringSession = new MonitoringSession("webAppName", "nodeName", "appServerName");

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertXpathEvaluatesTo("nodeName", "/application-test/@nodeName", doc);
        assertXpathEvaluatesTo("appServerName", "/application-test/@appServerName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("This application has no technical tests.",
                        "/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * Just dependency tests should not be allowed. ERROR
     *
     * @throws Exception tout type d'exception
     */
    public void testDependencyTestOnly() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");
        monitoringSession.doDependencyTest(new MockMonitor());

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("This application has no technical tests.",
                        "/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * OK avec 2 tests techniques.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalTestOK() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");
        monitoringSession.doTechnicalTest(new TempoMonitor("test 1"));
        monitoringSession.doTechnicalTest(new TempoMonitor("test 2"));

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertXpathEvaluatesTo("1.0", "/application-test/@schemaVersion", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/@status", doc);

        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("test 1", "/application-test/technical-tests/test[1]/@name", doc);
        assertXpathEvaluatesTo("true", "/application-test/technical-tests/test[1]/@duration >= 1000", doc);
        assertXpathNotExists("/application-test/technical-tests/test[1]/errorMessage", doc);

        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[2]/@status", doc);
        assertXpathEvaluatesTo("test 2", "/application-test/technical-tests/test[2]/@name", doc);
        assertXpathEvaluatesTo("true", "/application-test/technical-tests/test[2]/@duration >= 1000", doc);
        assertXpathNotExists("/application-test/technical-tests/test[2]/errorMessage", doc);
    }

    /**
     * OK avec 1 test technique et une description.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalTestOKWithDescription() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");
        final MockMonitor mon = new MockMonitor("name", Type.DATABASE, "this is the description of the test");
        monitoringSession.doTechnicalTest(mon);

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("this is the description of the test",
                        "/application-test/technical-tests/test[1]/description", doc);
        assertXpathNotExists("/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * Ajout d'un test technique KO.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalTestKO() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");
        final IMonitor mon = new AbstractMonitor("name", Type.DATABASE) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsKo("error message");
            }
        };
        monitoringSession.doTechnicalTest(mon);

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("error message", "/application-test/technical-tests/test[1]/errorMessage", doc);
    }

    /**
     * Ajout de 4 tests techniques. Un seul est KO, donc le groupe entier doit
     * �tre KO �galement.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalTestGroupKO() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");

        monitoringSession.doTechnicalTest(new AbstractMonitor("db", Type.DATABASE) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsKo("error message");
            }
        });

        monitoringSession.doTechnicalTest(new AbstractMonitor("ldap", Type.LDAP) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsOk();
            }
        });

        monitoringSession.doTechnicalTest(new AbstractMonitor("ctg", Type.CTG) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsOk();
            }
        });

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
    }

    /**
     * Ajout de 2 tests techniques. Tous OK, donc le groupe entier
     * doit �tre OK.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalTestGroupOK() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");

        monitoringSession.doTechnicalTest(new AbstractMonitor("ldap", Type.LDAP) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsOk();
            }
        });

        monitoringSession.doTechnicalTest(new AbstractMonitor("ctg", Type.CTG) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsOk();
            }
        });

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/@status", doc);
    }

    /**
     * Test technique avec exception : on doit obtenir la stacktrace.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalTestKOWithException() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");

        monitoringSession.doTechnicalTest(new AbstractMonitor("name", Type.DATABASE) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsKo(new NullPointerException("exception message"));
            }
        });

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("exception message", "/application-test/technical-tests/test[1]/errorMessage", doc);
        assertNotBlank(doc, "/application-test/technical-tests/test[1]/stackTrace");
    }

    /**
     * Test technique avec exception encapsul�e dans une autre : on doit obtenir
     * le message d'erreur correct.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalTestKOWithEmbeddedException() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");
        monitoringSession.doTechnicalTest(new AbstractMonitor("name", Type.DATABASE) {
            public void doMonitor(TestElement monitoredElement) {
                final RuntimeException ex = new RuntimeException(new NullPointerException("exception message"));
                monitoredElement.setTestIsKo(ex);
            }
        });

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("java.lang.NullPointerException: exception message",
                        "/application-test/technical-tests/test[1]/errorMessage", doc);
        assertNotBlank(doc, "/application-test/technical-tests/test[1]/stackTrace");
        assertXpathContains(doc, "/application-test/technical-tests/test[1]/stackTrace", "(...)");
    }

    /**
     * Test technique avec exception et message d'erreur.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalTestKOWithExceptionAndMessage() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");
        monitoringSession.doTechnicalTest(new AbstractMonitor("name", Type.DATABASE) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsKo("error message", new NullPointerException("exception message"));
            }
        });

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo("error message", "/application-test/technical-tests/test[1]/errorMessage", doc);
        assertNotBlank(doc, "/application-test/technical-tests/test[1]/stackTrace");
    }

    /**
     * Cas nominal : tests techniques et de d�pendances OK.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalAndDepTestOK() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");

        monitoringSession.doTechnicalTest(new MockMonitor("DB Test", Type.DATABASE, "this is a technical test"));

        monitoringSession.doTechnicalTest(new MockMonitor("LDAP de prod", Type.LDAP, "this is a technical test"));

        monitoringSession.doTechnicalTest(new MockMonitor("JNDI", Type.OTHER));

        monitoringSession.doDependencyTest(new MockMonitor("DB Client", Type.DATABASE, "this is a dependency test"));

        monitoringSession.doDependencyTest(new MockMonitor("DB Contrat", Type.DATABASE));

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[2]/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[3]/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/test[2]/@status", doc);
    }

    /**
     * Cas nominal : tests de d�pendances et techniques KO.
     *
     * @throws Exception tout type d'exception
     */
    public void testTechnicalAndDependencyTestKO() throws Exception {
        monitoringSession = new MonitoringSession("webAppName");

        monitoringSession.doTechnicalTest(new MockMonitor("DB Test", Type.DATABASE, "this is a technical test"));

        monitoringSession.doTechnicalTest(new AbstractMonitor("LDAP de prod", Type.LDAP, "this is a technical test") {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsKo("error message", new NullPointerException("exception message"));
            }
        });

        monitoringSession.doTechnicalTest(new MockMonitor("JNDI", Type.OTHER));

        monitoringSession.doDependencyTest(new MockMonitor("DB Client", Type.DATABASE, "this is a dependency test"));

        monitoringSession.doDependencyTest(new AbstractMonitor("LDAP de prod", Type.LDAP) {
            public void doMonitor(TestElement monitoredElement) {
                monitoredElement.setTestIsKo("error message", new NullPointerException("exception message"));
            }
        });

        final Document doc = getValidatedDOM();
        assertNotNull(doc);
        assertXpathEvaluatesTo("webAppName", "/application-test/@webAppName", doc);
        assertNotBlank(doc, "/application-test/@startMonitoring");
        assertNotBlank(doc, "/application-test/@schemaVersion");
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[2]/@status", doc);
        assertXpathEvaluatesTo("error message", "/application-test/technical-tests/test[2]/errorMessage", doc);
        assertNotBlank(doc, "/application-test/technical-tests/test[2]/stackTrace");
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[3]/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/dependency-tests/@status", doc);
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/test[1]/@status", doc);
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/dependency-tests/test[2]/@status", doc);
    }

    /**
     * Test de la m�thode interne "isBlank".
     */
    public void testIsBlank() {
        monitoringSession = new MonitoringSession("webappname");
        assertTrue("null", monitoringSession.isBlank(null));
        assertTrue("empty", monitoringSession.isBlank(""));
        assertTrue("space", monitoringSession.isBlank(" "));
        assertTrue("spaces", monitoringSession.isBlank("   "));
        assertTrue("spacesAndTabs", monitoringSession.isBlank(" \t "));
        assertFalse("nospace", monitoringSession.isBlank("abcd"));
        assertFalse("spacesAndNoSpaces", monitoringSession.isBlank("  abcd   "));
    }

    /**
     * Test de la m�thode doTechnicalTest : cas nominal.
     *
     * @throws Exception tout type d'exception.
     */
    public void testDoTechnicalTestOK() throws Exception {
        monitoringSession = new MonitoringSession("webappname");
        final MockMonitor mon = new MockMonitor();
        monitoringSession.doTechnicalTest(mon);
        assertTrue("monitor appel�", mon.isCalled());
        final Document doc = getValidatedDOM();
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/technical-tests/test[1]/@status", doc);
    }

    /**
     * Test de la m�thode doTechnicalTest : cas du param�tre null.
     */
    public void testDoTechnicalTestNull() {
        monitoringSession = new MonitoringSession("webappname");
        try {
            monitoringSession.doTechnicalTest(null);
            fail("aurait du echouer");
        } catch (IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test de la m�thode doTechnicalTest : cas nominal.
     *
     * @throws Exception tout type d'exception.
     */
    public void testDoDependencyTestOK() throws Exception {
        monitoringSession = new MonitoringSession("webappname");
        final MockMonitor mon = new MockMonitor();
        monitoringSession.doDependencyTest(mon);
        assertTrue("monitor appel�", mon.isCalled());
        final Document doc = getValidatedDOM();

        // On retrouve notre r�sultat de test de d�pendance :
        assertXpathEvaluatesTo(Status.OK.toString(), "/application-test/dependency-tests/test[1]/@status", doc);

        // Il existe un test technique KO : il est ajout� par le service parce
        // qu'on n'a pas de test technique.
        assertXpathEvaluatesTo(Status.KO.toString(), "/application-test/technical-tests/test[1]/@status", doc);
    }

    /**
     * Test de la m�thode doTechnicalTest : cas du param�tre null.
     */
    public void testDoDependencyTestNull() {
        monitoringSession = new MonitoringSession("webappname");
        try {
            monitoringSession.doDependencyTest(null);
            fail("aurait du echouer");
        } catch (IllegalArgumentException ex) {
            // OK
        }
    }

    /**
     * Test du constructeur.
     */
    public void testInit() {
        final MonitoringSession serviceImpl = new MonitoringSession("a", "b", "c");
        assertEquals("a", serviceImpl.getApplication().getWebAppName());
        assertEquals("b", serviceImpl.getApplication().getNodeName());
        assertEquals("c", serviceImpl.getApplication().getAppServerName());

    }

    /**
     * Test de addTestToGroup, cas impossibles � tester via les m�thodes
     * publiques.
     */
    public void testAddTestToGroup() {
        final MonitoringSession serviceImpl = new MonitoringSession("a", "b", "c");
        final TestElement monitoredElement = new TestElement("un nom", Type.CTG);
        monitoredElement.setTestIsOk();
        final Group xmlGroup = new Group();
        serviceImpl.addTestToGroup(monitoredElement, xmlGroup);
        assertEquals("nb tests", 1, xmlGroup.getTests().size());
        serviceImpl.addTestToGroup(monitoredElement, xmlGroup);
        assertEquals("nb tests", 2, xmlGroup.getTests().size());
    }

    /**
     * Test de addTestToGroup, cas impossibles � tester via les m�thodes
     * publiques. Type de test est null.
     */
    public void testAddTestToGroupTypeNull() {
        final MonitoringSession serviceImpl = new MonitoringSession("a", "b", "c");
        final TestElement monitoredElement = new TestElement("un nom", null);
        final Group xmlGroup = new Group();
        serviceImpl.addTestToGroup(monitoredElement, xmlGroup);
        assertEquals("nb tests", 1, xmlGroup.getTests().size());
        assertNull(xmlGroup.getTests().get(0).getType());
    }

    /**
     * Test de addTestToGroup, cas impossibles � tester via les m�thodes
     * publiques. Status de test est null.
     */
    public void testAddTestToGroupStatusNull() {
        final MonitoringSession serviceImpl = new MonitoringSession("a", "b", "c");
        final TestElement monitoredElement = new TestElement("un nom", Type.CTG);
        // Rien ne sette le status ici.
        final Group xmlGroup = new Group();
        serviceImpl.addTestToGroup(monitoredElement, xmlGroup);
        assertEquals("nb tests", 1, xmlGroup.getTests().size());
        assertEquals(Status.KO, xmlGroup.getTests().get(0).getStatus());
        assertEquals("errorMsg", "Non-executed test", xmlGroup.getTests().get(0).getErrorMessage());
    }

}
