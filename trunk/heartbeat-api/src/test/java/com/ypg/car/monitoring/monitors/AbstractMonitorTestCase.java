package com.ypg.car.monitoring.monitors;

import com.ypg.car.monitoring.monitors.mock.MockInitialContextFactory;
import junit.framework.TestCase;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Socle pour les tests des monitors pr�cod�s du package com.ypg.car.monitoring.monitors.
 */
public abstract class AbstractMonitorTestCase extends TestCase {

    /**
     * Contexte JNDI. Utilis� en bouchon du JNDI du serveur d'app.
     */
    protected Context ctx;

    /**
     * Initialisation de chaque test avant son ex�cution.
     * 
     * @throws Exception tout type d'exception
     */
    public void setUp() throws Exception {
        super.setUp();

        // Cr�ation d'un contexte JNDI. Ce sera en fait un bouchon, c.a.d
        // fondamentalement
        // un simple Hashmap static. Java s�lectionne notre impl�mentation
        // bouchon gr�ce
        // � un fichier jndi.properties pr�sent dans les ressources de test. Ce
        // fichier d�signe
        // une factory de contexte JNDI qui est n�tre, et qui renvoie toujours
        // une r�f�rence
        // vers notre bouchon.

        setCtx(new InitialContext());
    }

    /**
     * M�nage apr�s l'ex�cution de chaque test.
     * 
     * @throws Exception tout type d'exception
     */
    public void tearDown() throws Exception {
        super.tearDown();

        // Vide le contexte JNDI. Nec�ssaire pour que les diff�rents tests
        // aient des contextes JNDI s�par�s.
        MockInitialContextFactory.init();
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public Context getCtx() {
        return ctx;
    }

}
