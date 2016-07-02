
package com.ypg.car.monitoring.api.impl;

import com.ypg.car.monitoring.api.AbstractMonitor;
import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;

/**
 * Ex�cute un test avec temporisation. Le r�sultat est toujours OK. Int�r�t :
 * tester le calcul de la dur�e d'ex�cution.
 */
public class TempoMonitor extends AbstractMonitor {

    /**
     * Temporisation en ms.
     */
    private static final int TEMPO = 1000;

    /**
     * Constructeur.
     * @param name nom de test 
     */
    public TempoMonitor(String name) {
        super(name, Type.DATABASE);
    }

    /**
     * {@inheritDoc}
     */
    public void doMonitor(TestElement monitoredElement) {
        // Permet de provoquer une dur�e > 0 :
        try {
            Thread.sleep(TEMPO);
            monitoredElement.setTestIsOk();
        } catch (InterruptedException e) {
            monitoredElement.setTestIsKo(e);
        }
    }

}
