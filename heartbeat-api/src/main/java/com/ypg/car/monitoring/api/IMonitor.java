package com.ypg.car.monitoring.api;

/**
 * Interface dont h�ritent tous les Monitors. Un Monitor est une classe qui
 * proc�de � l'ex�cution d'un test et produit un objet {@link TestElement}
 * correspondant au r�sultat du test. Les monitors sont de diff�rents types, par
 * exemple : test de JNDI, test de base de donn�es, etc. Les Monitors concrets
 * impl�mentent la m�thode {@link #doMonitor(TestElement)} - pattern "Template
 * Method".
 */
public interface IMonitor {

    /**
     * M�thode � appeler pour proc�der � l'ex�cution du test.
     * 
     * @return objet {@link TestElement} rempli.
     */
    TestElement monitor();

    /**
     * M�thode � impl�menter par le Monitor concret. Cette m�thode prend en
     * param�tre un {@link TestElement}. Elle <strong>doit
     * obligatoirement</strong> appeler une m�thode "setTestIsXXX" pour marquer
     * la fin de l'ex�cution du test (en succ�s ou en erreur).
     * 
     * @param monitoredElement {@link TestElement}
     */
    void doMonitor(TestElement monitoredElement);

    /**
     * Nom du monitor.
     * 
     * @return the name
     */
    String getName();

    /**
     * Description du monitor.
     * 
     * @return the description
     */
    String getDescription();

}
