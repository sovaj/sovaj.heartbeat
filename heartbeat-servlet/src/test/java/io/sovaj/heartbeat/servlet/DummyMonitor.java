package io.sovaj.heartbeat.servlet;

import io.sovaj.heartbeat.api.AbstractMonitor;
import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;

/**
 * {@link AbstractMonitor} bidon pour tester la servlet.
 */
public class DummyMonitor extends AbstractMonitor {

    /**
     * Constructeur.
     * 
     * @param name nom du test
     * @param type type du test
     */
    public DummyMonitor() {
        super("dummy", Type.OTHER);
    }

    /**
     * {@inheritDoc}
     */
    public void doMonitor(TestElement myTest) {
        myTest.setTestIsOk();
    }

}
