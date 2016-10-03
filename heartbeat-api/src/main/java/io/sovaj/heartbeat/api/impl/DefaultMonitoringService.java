package io.sovaj.heartbeat.api.impl;

import io.sovaj.heartbeat.api.IMonitor;
import io.sovaj.heartbeat.api.IMonitoringService;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * {@inheritDoc}
 */
public class DefaultMonitoringService implements IMonitoringService {

    /**
     * Tests techniques � ex�cuter.
     */
    private List technicalTests;

    /**
     * Tests de d�pendances.
     */
    private List dependencyTests;

    /**
     * Nom de la webapp.
     */
    private String webAppName;

    /**
     * Nom du noeud.
     */
    private String nodeName;

    /**
     * Nom du serveur d'application.
     */
    private String appServerName;

    private final MonitoringSession monitoringSession;

    public MonitoringSession getMonitoringSession() {
        return monitoringSession;
    }



    public DefaultMonitoringService(String webAppName, String nodeName, String appServerName) {
        this.technicalTests = new LinkedList();
        this.dependencyTests = new LinkedList();
        this.webAppName = webAppName;
        this.nodeName = nodeName;
        this.appServerName = appServerName;
        this.monitoringSession = new MonitoringSession(webAppName, nodeName, appServerName);
    }

    /**
     * {@inheritDoc}
     */
    public void addTechnicalTest(IMonitor mon) {
        this.technicalTests.add(mon);
    }

    /**
     * {@inheritDoc}
     */
    public void addDependencyTest(IMonitor mon) {
        this.dependencyTests.add(mon);
    }

    /**
     * {@inheritDoc}
     */
    public String runTests(boolean technical, boolean functional) {

        if (technical) {
            for (final Iterator it = technicalTests.iterator(); it.hasNext(); ) {
                monitoringSession.doTechnicalTest((IMonitor) it.next());
            }
        }
        if (functional) {
            for (final Iterator it = dependencyTests.iterator(); it.hasNext(); ) {
                monitoringSession.doDependencyTest((IMonitor) it.next());
            }
        }

        return monitoringSession.toXML(technical);
    }

    /**
     * @return the technicalTests
     */
    public List getTechnicalTests() {
        return technicalTests;
    }

    /**
     * @param myTechnicalTests the technicalTests to set
     */
    public void setTechnicalTests(List myTechnicalTests) {
        this.technicalTests = myTechnicalTests;
    }

    /**
     * @return the dependencyTests
     */
    public List getDependencyTests() {
        return dependencyTests;
    }

    /**
     * @param myDepTests the dependencyTests to set
     */
    public void setDependencyTests(List myDepTests) {
        this.dependencyTests = myDepTests;
    }

    /**
     * @return the webAppName
     */
    public String getWebAppName() {
        return webAppName;
    }

    /**
     * @param myWebAppName the webAppName to set
     */
    public void setWebAppName(String myWebAppName) {
        this.webAppName = myWebAppName;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param myNodeName the nodeName to set
     */
    public void setNodeName(String myNodeName) {
        this.nodeName = myNodeName;
    }

    /**
     * @return the appServerName
     */
    public String getAppServerName() {
        return appServerName;
    }

    /**
     * @param myAppServerName the appServerName to set
     */
    public void setAppServerName(String myAppServerName) {
        this.appServerName = myAppServerName;
    }

}
