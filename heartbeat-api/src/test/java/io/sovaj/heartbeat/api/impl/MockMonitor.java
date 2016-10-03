package io.sovaj.heartbeat.api.impl;

import io.sovaj.heartbeat.api.AbstractMonitor;
import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;

/**
 * Mock de {@link AbstractMonitor}
 */
public class MockMonitor extends AbstractMonitor {

    /**
     * Le monitor a-t-il �t� appel� ?
     */
    private boolean called;

    /**
     * Constructeur par d�faut.
     */
    public MockMonitor() {
        this("mockMonitor", Type.OTHER, null);
    }

    /**
     * Constructeur.
     * 
     * @param name nom du test
     * @param type type du test
     */
    public MockMonitor(String name, Type type) {
        this(name, type, null);
        this.called = false;
    }

    /**
     * Constructeur complet.
     * 
     * @param name nom du test
     * @param type type du test
     * @param desc description du test
     */
    public MockMonitor(String name, Type type, String desc) {
        super(name, type, desc);
        this.called = false;
    }

    /**
     * {@inheritDoc}
     */
    public void doMonitor(TestElement myTest) {
        called = true;
        myTest.setTestIsOk();
    }

    /**
     * @return the called
     */
    public boolean isCalled() {
        return called;
    }

}
