
package com.ypg.car.monitoring.monitors;

import com.ypg.car.monitoring.api.AbstractMonitor;
import com.ypg.car.monitoring.api.TestElement;
import com.ypg.car.monitoring.api.Type;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Cette classe teste la pr�sence d'une queue JMS.
 */
public class JMSQueueMonitor extends AbstractMonitor {

    /**
     * Message d'erreur.
     */
    private static final String MSG_QUEUE_NOT_FOUND = "JMS Queue not found: ";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NO_QUEUE_NAME = "no JMS Queue name set for JMSQueueMonitor";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NO_ENVIRONMENT_PROPERTIES = "no environment properties set for JMSQueueMonitor";

    /**
     * Nom JNDI de la queue JMS � tester.
     */
    private String queueName;

    /**
     * Propri�t�s d'environnement de la queue
     */
    private Properties environment;

    /**
     * Constructeur par d�faut. Il n'est pas conseill� de l'utiliser, sauf via
     * Spring.
     */
    public JMSQueueMonitor() {
        super("JMS Queue lookup", Type.JMS);
    }

    /**
     * Cr�ation d'un {@link JMSQueueMonitor}
     * 
     * @param aQueueName nom JNDI de la queue JMS � tester
     * @param aEnvironment proprit�t�s d'environnement de la queue JMS
     */
    public JMSQueueMonitor(String aQueueName, Properties aEnvironment) {
        this(aQueueName, aEnvironment, null);
    }

    /**
     * Cr�ation d'un {@link JMSQueueMonitor}
     * 
     * @param aQueueName nom JNDI de la queue JMS � tester
     * @param aEnvironment proprit�t�s d'environnement de la queue JMS
     * @param desc description du test
     */
    public JMSQueueMonitor(String aQueueName, Properties aEnvironment, String desc) {
        super("JMS Queue lookup,URL=" + aQueueName, Type.JMS, desc);
        this.queueName = aQueueName;
        this.environment = aEnvironment;
    }

    /**
     * {@inheritDoc}
     */
    public void doMonitor(TestElement monitoredElement) {

        // V�rification du param�trage du monitor :

        if (this.queueName == null || this.queueName.length() == 0) {
            monitoredElement.setTestIsKo(MSG_NO_QUEUE_NAME);
        } else if (this.environment == null || this.environment.isEmpty()) {
            monitoredElement.setTestIsKo(MSG_NO_ENVIRONMENT_PROPERTIES);
        } else {

            // Appel JNDI avec les param�tres d'environnement de la queue :

            try {
                final Context ctx = new InitialContext(environment);
                final Object res = ctx.lookup(queueName);
                if (res == null) {
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
        return String.format(MSG_QUEUE_NOT_FOUND + "'%s'", queueName);
    }

    /**
     * @param queueName the queueName to set
     */
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    /**
     * @param environment the environment to set
     */
    public void setEnvironment(Properties environment) {
        this.environment = environment;
    }
}
