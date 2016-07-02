
package com.ypg.car.monitoring.monitors;

import com.ypg.car.monitoring.api.TestElement;

/**
 * Test du JVMMonitor.
 */
public class JVMMonitorTest extends AbstractMonitorTestCase {

    /**
     * Test bonne version de JVM.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOK() throws Exception {
        final String javaVersionStr = System.getProperty("java.version");
        final String javaVersionToTest = javaVersionStr.substring(0, 3);
        final TestElement elem = new JVMMonitor(javaVersionToTest).monitor();
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
    }

    /**
     * Test version de JVM non-correspondante.
     * 
     * @throws Exception tout type d'exception
     */
    public void testKO() throws Exception {
        // Version de VM qui n'existe pas :
        final TestElement elem = new JVMMonitor("0.9").monitor();
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsKo());
        assertEquals("errorMessage", "The version of the JDK is not correct. The application is running on "
                        + System.getProperty("java.version") + " instead of 0.9", elem.getErrorMessage());
        assertNull("exception", elem.getException());
    }

    /**
     * Test cha�ne de version de JVM vide.
     * 
     * @throws Exception tout type d'exception
     */
    public void testInvalidVersion() throws Exception {
        assertInvalidVersion(null);
        assertInvalidVersion("");
        assertInvalidVersion("  ");
        assertInvalidVersion("z");
        assertInvalidVersion(".");
        assertInvalidVersion("aa.bb");
        assertInvalidVersion("1.");
        assertInvalidVersion(".1");
        assertInvalidVersion("1..5");
        assertInvalidVersion(" 1.5 ");
    }

    /**
     * @param input
     */
    private void assertInvalidVersion(String input) {
        try {
            new JVMMonitor(input).monitor();
            fail("should have failed with input '" + input + "'");
        } catch (IllegalArgumentException ex) {
            // OK.
        }
    }

    /**
     * Test bonne version de JVM et nom surcharg�.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOKNomSurcharge() throws Exception {
        final String javaVersionStr = System.getProperty("java.version");
        final String javaVersionToTest = javaVersionStr.substring(0, 3);
        final JVMMonitor mon = new JVMMonitor(javaVersionToTest);
        mon.setName("nom surcharg�");
        final TestElement elem = mon.monitor();
        assertNotNull(elem);
        assertTrue("status", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("nom surcharg�", elem.getName());
    }

}
