
package com.ypg.car.monitoring.monitors.mock;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

/**
 * Provides a JNDI initial context factory for the MockContext. <strong>N'EST
 * PAS THREAD-SAFE</strong>
 */
public class MockInitialContextFactory implements InitialContextFactory {

    /**
     * Contexte bouchon. Est statique car il s'agit en fait d'un annuaire JNDI
     * stock� en RAM et partag� entre les diff�rents appels.
     */
    private static MockContext ctx;

    static {
        init();
    }

    /**
     * Initialisation du contexte.
     */
    public static void init() {
        ctx = new MockContext();
    }

    /**
     * {@inheritDoc}
     */
    public Context getInitialContext(Hashtable props) throws NamingException {
        return ctx;
    }

}
