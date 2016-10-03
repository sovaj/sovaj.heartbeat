
package io.sovaj.heartbeat.monitors;

import io.sovaj.heartbeat.api.AbstractDatasourceMonitor;
import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

/**
 * Cette classe teste la pr�sence d'un nom JNDI.
 */
public class JNDIMonitor extends AbstractDatasourceMonitor {

    /**
     * Message d'erreur.
     */
    private static final String MSG_JNDI_NAME_NOT_FOUND = "JNDI name not found: '%s'";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NO_JNDI_NAME = "no JNDI name set for JNDIMonitor";

    /**
     * Nom JNDI � tester.
     */
    private String jndiName;


    /**
     * Constructeur par d�faut. Il n'est pas conseill� de l'utiliser, sauf via
     * Spring.
     */
    public JNDIMonitor() {
        super("JNDI lookup", Type.JNDI);
    }

    /**
     * Cr�ation d'un {@link JNDIMonitor}
     *
     * @param aJndiName nom JNDI � tester
     */
    public JNDIMonitor(String aJndiName) {
        this(aJndiName, null);
    }

    /**
     * Cr�ation d'un {@link JNDIMonitor}
     *
     * @param aJndiName nom JNDI � tester
     * @param desc      description du test
     */
    public JNDIMonitor(String aJndiName, String desc) {
        super("JNDI lookup,URL=" + aJndiName, Type.JNDI, desc);
        this.jndiName = aJndiName;
    }

    public JNDIMonitor(String aJndiName, String desc, boolean isTomcatCtx) {
        super("JNDI lookup,URL=" + aJndiName, Type.JNDI, desc,isTomcatCtx);
        this.jndiName = aJndiName;
    }

    /**
     * {@inheritDoc}
     */
    public void doMonitor(TestElement monitoredElement) {

        // V�rification du param�trage du monitor :

        if (this.jndiName == null || this.jndiName.length() == 0) {
            monitoredElement.setTestIsKo(MSG_NO_JNDI_NAME);
        } else {
            // Appel JNDI :
            try {
                //              JndiLocatorDelegate ctx = new JndiLocatorDelegate();
                Context ctx = new InitialContext();
                if (isTomcatCtx)
                    ctx = (Context) ctx.lookup("java:/comp/env");

                final Object jndiObj = ctx.lookup(jndiName);
                if (jndiObj == null) {
                    monitoredElement.setTestIsKo(getNameNotFoundErrorMsg());
                } else {
                    monitoredElement.setTestIsOk();
                }
            } catch (NameNotFoundException ex) {
                monitoredElement.setTestIsKo(getNameNotFoundErrorMsg());
            } catch (NamingException ex) {
                monitoredElement.setTestIsKo(ex);
            }
        }
    }

    /**
     * Construction d'un message d'erreur de type "name not found".
     *
     * @return message d'erreur
     */
    private String getNameNotFoundErrorMsg() {
        return String.format(MSG_JNDI_NAME_NOT_FOUND, jndiName);
    }

    /**
     * @param myJndiName the jndiName to set
     */
    public void setJndiName(String myJndiName) {
        this.jndiName = myJndiName;
    }


}
