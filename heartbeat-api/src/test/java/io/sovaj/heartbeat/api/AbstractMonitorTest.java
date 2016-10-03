package io.sovaj.heartbeat.api;

import junit.framework.TestCase;

/**
 * Test de la classe {@link io.sovaj.heartbeat.api.AbstractMonitor}
 */
public class AbstractMonitorTest extends TestCase {

    /**
     * Objet � tester.
     */
    private IMonitor monitor;

    /**
     * Cas o� la classe de monitoring n'a pas appel� setTestIsXXX sur le
     * TestElement.
     */
    public void testFailedToRun() {
        monitor = new AbstractMonitor("nom", Type.OTHER, "description") {
            public void doMonitor(TestElement myTestElt) {
                // Ne fait rien.
            }
        };
        final TestElement element = monitor.monitor();
        commonAssertions(element);
        assertFalse("status", element.getTestIsOk());
        assertEquals("errorMessage", "test failed to run", element.getErrorMessage());
    }

    /**
     * Cas o� le Monitor renvoie un r�sultat OK.
     */
    public void testOK() {
        monitor = new AbstractMonitor("nom", Type.OTHER, "description") {
            public void doMonitor(TestElement m) {
                m.setTestIsOk();
            }
        };
        final TestElement e = monitor.monitor();
        commonAssertions(e);
        assertTrue(e.getTestIsOk());
        assertNull("errorMessage", e.getErrorMessage());
    }

    /**
     * Cas o� le Monitor renvoie un r�sultat KO.
     */
    public void testKO() {
        final Throwable expectedException = new RuntimeException("exception message");
        monitor = new AbstractMonitor("nom", Type.OTHER, "description") {
            public void doMonitor(TestElement m) {
                m.setTestIsKo("my error message", expectedException);
            }
        };
        final TestElement e = monitor.monitor();
        commonAssertions(e);
        assertTrue(e.getTestIsKo());
        assertEquals("errorMessage", "my error message", e.getErrorMessage());
        assertSame("stackTrace", expectedException, e.getException());
    }

    /**
     * Cas o� le Monitor plante en exception.
     */
    public void testException() {
        final RuntimeException expectedException = new RuntimeException("exception message");
        monitor = new AbstractMonitor("nom", Type.OTHER, "description") {
            public void doMonitor(TestElement m) {
                throw expectedException;
            }
        };
        final TestElement e = monitor.monitor();
        commonAssertions(e);
        assertTrue(e.getTestIsKo());
        assertEquals("errorMessage", expectedException.getMessage(), e.getErrorMessage());
        assertSame("stackTrace", expectedException, e.getException());
    }

    /**
     * Assertions communes sur {@link TestElement}
     * 
     * @param e {@link TestElement} � v�rifier
     */
    private void commonAssertions(TestElement e) {
        assertEquals("name", "nom", e.getName());
        assertEquals("type", Type.OTHER, e.getType());
        assertEquals("desc", "description", e.getDescription());
    }
}
