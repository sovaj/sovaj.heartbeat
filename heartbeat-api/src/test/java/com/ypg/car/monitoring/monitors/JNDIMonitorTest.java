
package com.ypg.car.monitoring.monitors;

import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;
import com.ypg.car.monitoring.monitors.mock.MockContext;

import javax.naming.NamingException;

/**
 * Test du JNDIMonitor. ATTENTION : ce test n'est pas thread-safe.
 */
public class JNDIMonitorTest extends AbstractMonitorTestCase {

    /**
     * Nom JNDI de datasource.
     */
    private static final String JNDI_NAME = "/test/my/dummy/datasource";

    /**
     * Test cas nominal.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOK() throws Exception {
        // Pr�paration du test :
        getCtx().bind(JNDI_NAME, "titi");

        // Ex�cution du test :
        final TestElement elem = new JNDIMonitor(JNDI_NAME, "desc").monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JNDI, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JNDI lookup,URL=" + JNDI_NAME, elem.getName());
        assertEquals("description", "desc", elem.getDescription());
    }

    /**
     * Test cas nominal avec constructeur par d�faut.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOKDefaultConstructor() throws Exception {
        // Pr�paration du test :
        getCtx().bind(JNDI_NAME, "titi");

        // Ex�cution du test :
        final JNDIMonitor mon = new JNDIMonitor();
        mon.setJndiName(JNDI_NAME);
        mon.setDescription("desc");
        final TestElement elem = mon.monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JNDI, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JNDI lookup", elem.getName());
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
        getCtx().bind(JNDI_NAME, "titi");

        // Ex�cution du test :
        final JNDIMonitor mon = new JNDIMonitor();
        mon.setJndiName(JNDI_NAME);
        mon.setDescription("desc");
        mon.setName("nom surcharg�");
        final TestElement elem = mon.monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JNDI, elem.getType());
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
        final TestElement elem = new JNDIMonitor(JNDI_NAME).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("JNDI name not found: '" + JNDI_NAME + "'", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JNDI, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JNDI lookup,URL=" + JNDI_NAME, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test o� JNDI renvoie null.
     * 
     * @throws Exception tout type d'exception
     */
    public void testJndiObjectIsNull() throws Exception {
        // Pr�paration du test :
        getCtx().bind(JNDI_NAME, null);

        // Ex�cution du test :
        final TestElement elem = new JNDIMonitor(JNDI_NAME).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("JNDI name not found: '" + JNDI_NAME + "'", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JNDI, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JNDI lookup,URL=" + JNDI_NAME, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test NamingException.
     * 
     * @throws Exception tout type d'exception
     */
    public void testNamingException() throws Exception {
        // Pr�paration du test :
        getCtx().bind(JNDI_NAME, "titi");
        final Exception toThrow = new NamingException("my exception message");
        getCtx().bind(MockContext.EXCEPTION, toThrow);

        // Ex�cution du test :
        final TestElement elem = new JNDIMonitor(JNDI_NAME).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals(toThrow.getMessage(), elem.getErrorMessage());
        assertSame("exception", toThrow, elem.getException());
        assertEquals("type", Type.JNDI, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JNDI lookup,URL=" + JNDI_NAME, elem.getName());
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
        final TestElement elem = new JNDIMonitor(JNDI_NAME).monitor();

        // V�rification du r�sultat :
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("message", toThrow.getMessage(), elem.getErrorMessage());
        assertSame("exception", toThrow, elem.getException());
        assertEquals("type", Type.JNDI, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JNDI lookup,URL=" + JNDI_NAME, elem.getName());
        assertNull("description", elem.getDescription());
    }

    /**
     * Test Exception.
     * 
     * @throws Exception tout type d'exception
     */
    public void testSpecifiedNameIsBlank() throws Exception {
        assertMissingConfigParam(null);
        assertMissingConfigParam("");
    }

    /**
     * @param jndiName nom JNDI
     */
    private void assertMissingConfigParam(String jndiName) {
        final TestElement elem = new JNDIMonitor(jndiName).monitor();

        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("message", "no JNDI name set for JNDIMonitor", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.JNDI, elem.getType());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
        assertEquals("name", "JNDI lookup,URL=" + jndiName, elem.getName());
        assertNull("description", elem.getDescription());
    }

}
