package io.sovaj.heartbeat.api;

/**
 * Classe dont h�ritent tous les Monitors. Un Monitor est une classe qui proc�de
 * � l'ex�cution d'un test et produit un objet {@link TestElement} correspondant
 * au r�sultat du test. Les monitors sont de diff�rents types, par exemple :
 * test de JNDI, test de base de donn�es, etc. Les Monitors concrets
 * impl�mentent la m�thode {@link #doMonitor(TestElement)} - pattern "Template
 * Method".
 */
public abstract class AbstractMonitor implements IMonitor {

    // NOTE : on pourrait avoir une interface Monitor au lieu d'une classe
    // abstraite, et avoir l'interface en argument des m�thodes addXXX.
    // Cependant, le fait d'imposer cette classe abstraite garantit que les
    // projets vont utiliser cette impl�mentation qui offre des garanties,
    // comme par ex. le fait de mettre le test � KO si oubli d'appel de m�thode
    // setTestXXX, etc. Mais l'inconv�nient (assum�) est une API un peu moins
    // "clean".

    /**
     * Nom du test.
     */
    private String name;

    /**
     * Type du test.
     */
    private final Type type;

    /**
     * Description �ventuelle du test.
     */
    private String description;

    /**
     * Construction � partir d'un nom et d'un type de test
     * 
     * @param aName nom de test
     * @param aType type de test
     */
    public AbstractMonitor(String aName, Type aType) {
        this(aName, aType, null);
    }

    /**
     * Construction � partir d'un nom, d'un type et d'une description de test
     * 
     * @param aName nom de test
     * @param aType type de test
     * @param aDescription description du test
     */
    public AbstractMonitor(String aName, Type aType, String aDescription) {
        this.name = aName;
        this.type = aType;
        this.description = aDescription;
    }

    /**
     * M�thode � appeler pour proc�der � l'ex�cution du test.
     * 
     * @return objet {@link TestElement} rempli.
     */
    @SuppressWarnings("PMD.AvoidCatchingException")
    public final TestElement monitor() {
        final TestElement monitorElement = new TestElement(name, type, description);
        try {
            // Appel de l'impl�mentation concr�te.
            doMonitor(monitorElement);
            if (monitorElement.getStop() == null) {
                // La classe de monitoring n'a pas appel� m.setTestIs...
                monitorElement.setTestIsKo("test failed to run");
            }

        } catch (Exception e) {
            // Le test n'a pas pu s'ex�cuter.
            // Note : catcher Exception est, dans le cas g�n�ral, � proscrire.
            // Cependant, l'utilisation ici est justifi�e car c'est le seul
            // moyen de garantir que, dans tous les cas, le Monitor renvoie un
            // test KO au lieu d'une exception, m�me si l'impl�mentation de
            // classe fille du Monitor est d�faillante.
            monitorElement.setTestIsKo(e);
        }
        return monitorElement;
    }

    /**
     * M�thode � impl�menter par le Monitor concret. Cette m�thode prend en
     * param�tre un {@link TestElement}. Elle <strong>doit
     * obligatoirement</strong> appeler une m�thode "setTestIsXXX" pour marquer
     * la fin de l'ex�cution du test (en succ�s ou en erreur).
     * 
     * @param monitoredElement {@link TestElement}
     */
    public abstract void doMonitor(TestElement monitoredElement);

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param myName the name to set
     */
    public void setName(String myName) {
        this.name = myName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param myDescription the description to set
     */
    public void setDescription(String myDescription) {
        this.description = myDescription;
    }

}
