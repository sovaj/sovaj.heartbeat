
package com.ypg.car.monitoring.monitors;

import com.ypg.car.monitoring.api.AbstractMonitor;
import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;

/**
 * Cette classe permet de tester si la version de JVM est celle support�e.
 */
public class JVMMonitor extends AbstractMonitor {

    /**
     * Version de JVM attendue.
     */
    private String supportedJvmVersion;

    /**
     * Constructeur par d�faut. Il n'est pas conseill� de l'utiliser, sauf via
     * Spring.
     */
    public JVMMonitor() {
        super("JVM Version", Type.JVM);
    }

    /**
     * Cr�ation d'un JVMMonitor.
     * 
     * @param version version attendue de JVM, doit �tre sous la forme x.y avec x et y num�riques.
     */
    public JVMMonitor(String version) {
        super("JVM Version " + version, Type.JVM);
        // Contr�le validit� de la version :
        if (version == null || !version.matches("^\\d+.\\d+$")) {
            throw new IllegalArgumentException("illegal JVM version number: " + version);
        }
        supportedJvmVersion = version;
    }

    /**
     * {@inheritDoc}
     */
    public void doMonitor(TestElement monitoredElement) {
        final String runningOnJvm = System.getProperty("java.version");
        if (runningOnJvm.startsWith(supportedJvmVersion)) {
            monitoredElement.setTestIsOk();
        } else {
            final String msg =
                            "The version of the JDK is not correct. The application is running on " + runningOnJvm
                                            + " instead of " + supportedJvmVersion;
            monitoredElement.setTestIsKo(msg);
        }
    }

    /**
     * @param version the supportedJvmVersion to set
     */
    public void setSupportedJvmVersion(String version) {
        this.supportedJvmVersion = version;
    }
}
