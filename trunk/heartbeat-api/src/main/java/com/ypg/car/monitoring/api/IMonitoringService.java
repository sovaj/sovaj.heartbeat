
package com.ypg.car.monitoring.api;

import com.ypg.car.monitoring.api.impl.MonitoringSession;

/**
 * Service permettant d'ex�cuter une suite de Monitors et de retourner un flux XML
 * conforme au format attendu et d�crit dans le XSD.
 */
public interface IMonitoringService {

    /**
     * Ajout d'un test � ex�cuter, dans la cat�gorie des tests techniques.
     * @param mon test � ajouter.
     * Note : ici, on viole la r�gle CheckStyle suivante : "Declaring variables, return values 
     * or parameters of type 'AbstractMonitor' is not allowed." Cependant, nous sommes oblig�s
     * de le faire, pour les raisons mentionn�es dans la doc de la classe AbstractMonitor.
     */
    void addTechnicalTest(IMonitor mon);

    /**
     * Ajout d'un test � ex�cuter, dans la cat�gorie des tests de d�pendances.
     * @param mon test � ajouter.
     * Note : ici, on viole la r�gle CheckStyle suivante : "Declaring variables, return values 
     * or parameters of type 'AbstractMonitor' is not allowed." Cependant, nous sommes oblig�s
     * de le faire, pour les raisons mentionn�es dans la doc de la classe AbstractMonitor.
     */
    void addDependencyTest(IMonitor mon);

    /**
     * Ex�cution des tests.
     * @return flux XML r�sultant.
     */
    String runTests(boolean technical, boolean functional);

    /**
     * @return the webAppName
     */
    String getWebAppName();

    /**
     * @param webAppName the webAppName to set
     */
    void setWebAppName(String webAppName);

    /**
     * @return the nodeName
     */
    String getNodeName();

    /**
     * @param nodeName the nodeName to set
     */
    void setNodeName(String nodeName);

    /**
     * @return the appServerName
     */
    String getAppServerName();

    /**
     * @param appServerName the appServerName to set
     */
    void setAppServerName(String appServerName);

    MonitoringSession getMonitoringSession() ;

}
