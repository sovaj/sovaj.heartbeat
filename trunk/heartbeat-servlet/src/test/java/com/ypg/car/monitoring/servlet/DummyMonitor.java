
package com.ypg.car.monitoring.servlet;

import com.ypg.car.monitoring.api.AbstractMonitor;
import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;

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
