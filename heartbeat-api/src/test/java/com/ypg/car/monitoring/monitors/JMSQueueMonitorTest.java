package com.ypg.car.monitoring.monitors;

import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;
import com.ypg.car.monitoring.monitors.mock.MockContext;
import com.ypg.car.monitoring.monitors.mock.MockInitialContextFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Test du JMSQueueMonitor. ATTENTION : ce test n'est pas thread-safe.
 */
public class JMSQueueMonitorTest extends AbstractMonitorTestCase {

    /**
     * Nom JNDI de la queue JMS.
     */
    private static final String QUEUE_NAME = "/test/my/dummy/queue";

    /**
     * Propri�t�s d'environnement de la queue JMS.
     */
    private Properties environment;

    /**
     * Initialisation de chaque test.
     * 
     * @throws Exception tout type d'exception
     */
    public void setUp() throws Exception {
        super.setUp();
        environment = new Properties();
        environment.setProperty("java.naming.factory.initial", MockInitialContextFactory.class.getName());
    }

    /**
     * Test cas nominal.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOK() throws Exception {
        // Pr�paration du test :
        getCtx().bind(QUEUE_NAME, "titi");
        getCtx().addToEnvironment(Context.INITIAL_CONTEXT_FACTORY, environment);

        // Ex�cution du test :
        final TestElement elem = new JMSQueueMonitor(QUEUE_NAME, environment, "desc").monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JMS Queue lookup,URL=" + QUEUE_NAME, elem.getName());
        assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test cas nominal avec constructeur par d�faut.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOKDefaultConstructor() throws Exception {
        // Pr�paration du test :
        getCtx().bind(QUEUE_NAME, "titi");

        // Ex�cution du test :
        final JMSQueueMonitor mon = new JMSQueueMonitor();
        mon.setQueueName(QUEUE_NAME);
        mon.setEnvironment(environment);
        mon.setDescription("desc");
        final TestElement elem = mon.monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JMS Queue lookup", elem.getName());
        assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test cas nominal avec constructeur par d�faut et nom sp�cifi�
     * explicitement.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOKNomSurcharge() throws Exception {
        // Pr�paration du test :
        getCtx().bind(QUEUE_NAME, "titi");

        // Ex�cution du test :
        final JMSQueueMonitor mon = new JMSQueueMonitor();
        mon.setQueueName(QUEUE_NAME);
        mon.setEnvironment(environment);
        mon.setDescription("desc");
        mon.setName("nom surcharg�");
        final TestElement elem = mon.monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "nom surcharg�", elem.getName());
        assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test nom JNDI non trouv�.
     * 
     * @throws Exception tout type d'exception
     */
    public void testNotFound() throws Exception {
        // Pr�paration du test :
        getCtx().bind("/z", "titi");

        // Ex�cution du test :
        final TestElement elem = new JMSQueueMonitor(QUEUE_NAME, environment).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("JMS Queue not found: '" + QUEUE_NAME + "'", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JMS Queue lookup,URL=" + QUEUE_NAME, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test o� JNDI renvoie null.
     * 
     * @throws Exception tout type d'exception
     */
    public void testJndiObjectIsNull() throws Exception {
        // Pr�paration du test :
        getCtx().bind(QUEUE_NAME, null);

        // Ex�cution du test :
        final TestElement elem = new JMSQueueMonitor(QUEUE_NAME, environment).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("JMS Queue not found: '" + QUEUE_NAME + "'", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JMS Queue lookup,URL=" + QUEUE_NAME, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test NamingException.
     * 
     * @throws Exception tout type d'exception
     */
    public void testNamingException() throws Exception {
        // Pr�paration du test :
        getCtx().bind(QUEUE_NAME, "titi");
        final Exception toThrow = new NamingException("my exception message");
        getCtx().bind(MockContext.EXCEPTION, toThrow);

        // Ex�cution du test :
        final TestElement elem = new JMSQueueMonitor(QUEUE_NAME, environment).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals(toThrow.getMessage(), elem.getErrorMessage());
        assertSame("exception", toThrow, elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JMS Queue lookup,URL=" + QUEUE_NAME, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test Exception.
     * 
     * @throws Exception tout type d'exception
     */
    public void testException() throws Exception {
        // Pr�paration du test :
        getCtx().bind("/test/my/dummy/datasource", "titi");
        final Exception toThrow = new IllegalArgumentException("my exception message");
        getCtx().bind(MockContext.EXCEPTION, toThrow);

        // Ex�cution du test :
        final TestElement elem = new JMSQueueMonitor(QUEUE_NAME, environment).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("message", toThrow.getMessage(), elem.getErrorMessage());
        assertSame("exception", toThrow, elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JMS Queue lookup,URL=" + QUEUE_NAME, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test nom de la queue null ou vide.
     * 
     * @throws Exception tout type d'exception
     */
    public void testQueueNameIsBlank() throws Exception {
        assertMissingQueueNameConfigParam(null, environment);
        assertMissingQueueNameConfigParam("", environment);
    }

    /**
     * Test propri�t�s d'environnement de la queue null ou vide.
     * 
     * @throws Exception tout type d'exception
     */
    public void testEnvironmentPropertiesIsBlank() throws Exception {
        assertMissingEnvironmentPropertiesConfigParam(QUEUE_NAME, null);
        assertMissingEnvironmentPropertiesConfigParam(QUEUE_NAME, new Properties());
    }

    /**
     * @param queueName nom JNDI
     * @param environment propri�t�s d'environnement
     */
    private void assertMissingQueueNameConfigParam(String queueName, Properties environment) {
        final TestElement elem = new JMSQueueMonitor(queueName, environment).monitor();

        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("message", "no JMS Queue name set for JMSQueueMonitor", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JMS Queue lookup,URL=" + queueName, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * @param queueName nom JNDI
     * @param environment propri�t�s d'environnement
     */
    private void assertMissingEnvironmentPropertiesConfigParam(String queueName, Properties environment) {
        final TestElement elem = new JMSQueueMonitor(queueName, environment).monitor();

        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("message", "no environment properties set for JMSQueueMonitor", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JMS, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JMS Queue lookup,URL=" + queueName, elem.getName());
        assertNull("description", elem.getDescription());
    }

}
