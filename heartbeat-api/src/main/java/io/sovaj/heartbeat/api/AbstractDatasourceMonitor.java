package io.sovaj.heartbeat.api;

/**
 * Created with IntelliJ IDEA.
 * User: ZOuarab1
 * Date: 3/11/14
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDatasourceMonitor extends AbstractMonitor {

    protected boolean isTomcatCtx = false;

    /**
     * Construction � partir d'un nom et d'un type de test
     *
     * @param aName nom de test
     * @param aType type de test
     */
    public AbstractDatasourceMonitor(String aName, Type aType) {
        super(aName, aType);
    }

    /**
     * Construction � partir d'un nom et d'un type de test
     *
     * @param aName nom de test
     * @param aType type de test
     * @param aIsTomcatCtx  environment du test(Tomcat, ou autre)
     */
    public AbstractDatasourceMonitor(String aName, Type aType, boolean aIsTomcatCtx) {
        super(aName, aType);
        this.isTomcatCtx = aIsTomcatCtx;

    }

    /**
     * Construction � partir d'un nom, d'un type et d'une description de test
     *
     * @param aName        nom de test
     * @param aType        type de test
     * @param aDescription description du test
     */
    public AbstractDatasourceMonitor(String aName, Type aType, String aDescription) {
        super(aName, aType, aDescription);
    }

    /**
     * Construction � partir d'un nom, d'un type et d'une description de test
     *
     * @param aName        nom de test
     * @param aType        type de test
     * @param aDescription description du test
     * @param aIsTomcatCtx environment du test(Tomcat, ou autre)
     */
    public AbstractDatasourceMonitor(String aName, Type aType, String aDescription, boolean aIsTomcatCtx) {
        super(aName, aType, aDescription);
        this.isTomcatCtx = aIsTomcatCtx;
    }

    public void setTomcatCtx(boolean tomcatCtx) {
        isTomcatCtx = tomcatCtx;
    }
}
