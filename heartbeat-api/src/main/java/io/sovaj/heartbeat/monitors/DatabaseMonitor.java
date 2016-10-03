package io.sovaj.heartbeat.monitors;

import io.sovaj.heartbeat.api.AbstractDatasourceMonitor;
import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Surveille une base de donn�es en ex�cutant une requ�te SQL sur une
 * datasource. Le r�sultat de la requ�te n'est absolument pas pris en compte. On
 * v�rifie uniquement la capacit� � pouvoir ex�cuter cette requ�te.
 */
public class DatabaseMonitor extends AbstractDatasourceMonitor {

    /**
     * Message d'erreur.
     */
    private static final String MSG_NO_DATASOURCE_SET = "no jndiName / no datasource set for DatabaseMonitor";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NO_SQL_SET = "no SQL query set for DatabaseMonitor";

    /**
     * Message d'erreur.
     */
    private static final String MSG_NOT_A_DATASOURCE = "JNDI object is not a datasource: ";

    /**
     * Message d'erreur.
     */
    private static final String MSG_JNDI_NAME_NOT_FOUND = "JNDI name not found: ";

    /**
     * Nom JNDI de la datasource. Facultatif si a sp�cifi� directement un objet
     * Datasource dans le constructeur.
     */
    private String jndiName;

    /**
     * Requ�te SQL � ex�cuter.
     */
    private String sql;

    /**
     * Datasource � utiliser.
     */
    private DataSource datasource;

    /**
     * Constructeur par d�faut. Il n'est pas conseill� de l'utiliser, sauf via
     * Spring.
     */
    public DatabaseMonitor() {
        super("Database test", Type.DATABASE);
    }

    /**
     * Construction � partir d'un nom JNDI et d'une requ�te SQL.
     * 
     * @param aJndiName nom JNDI
     * @param aSql requ�te SQL
     */
    public DatabaseMonitor(String aJndiName, String aSql) {
        super("Database test,DS=" + aJndiName, Type.DATABASE);
        jndiName = aJndiName;
        sql = aSql;
    }

    /**
     * Construction � partir d'un nom JNDI et d'une requ�te SQL.
     *
     * @param aJndiName nom JNDI
     * @param aSql requ�te SQL
     * @param isTomcatCtx
     */
    public DatabaseMonitor(String aJndiName, String aSql,boolean isTomcatCtx) {
        super("Database test,DS=" + aJndiName, Type.DATABASE,isTomcatCtx);
        jndiName = aJndiName;
        sql = aSql;
    }

    /**
     * Construction � partir d'une datasource et d'une requ�te SQL.
     * 
     * @param aDatasource datasource
     * @param aSql requ�te SQL
     */
    public DatabaseMonitor(DataSource aDatasource, String aSql) {
        this();
        datasource = aDatasource;
        sql = aSql;
    }

    /**
     * {@inheritDoc}
     */
    public void doMonitor(TestElement monitoredElement) {
        if (configIsOk(monitoredElement)) {
            // R�cup datasource :
            if (datasource == null) {
                datasource = getDatasource(monitoredElement);
            }
            if (!monitoredElement.getTestIsKo()) {
                // Ex�cution de la requ�te SQL :
                executePingQuery(monitoredElement);
                if (!monitoredElement.getTestIsKo()) {
                    monitoredElement.setTestIsOk();
                }
            }
        }
    }

    /**
     * Test de validit� de la configuration.
     * 
     * @param monitoredElement �l�ment
     * @return true si config OK
     */
    private boolean configIsOk(TestElement monitoredElement) {
        boolean result = true;
        if (isBlank(jndiName) && datasource == null) {
            monitoredElement.setTestIsKo(MSG_NO_DATASOURCE_SET);
            result = false;
        }
        if (isBlank(sql)) {
            monitoredElement.setTestIsKo(MSG_NO_SQL_SET);
            result = false;
        }
        return result;
    }

    /**
     * Teste si une cha�ne est null ou vide.
     * 
     * @param str cha�ne � tester
     * @return true si null ou vide
     */
    private boolean isBlank(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * R�cup�ration de la datasource.
     * 
     * @param monitoredElement monitored element, cette m�thode peut faire
     *            �voluer son status (par ex. si JNDI est KO, etc.)
     * @return datasource trouv�e
     */
    private DataSource getDatasource(TestElement monitoredElement) {
        try {

            Context ctx = new InitialContext();
            if (isTomcatCtx)
                ctx = (Context) ctx.lookup("java:/comp/env");

            final DataSource foundDataSource = (DataSource ) ctx.lookup(jndiName);
            if (foundDataSource == null) {
                monitoredElement.setTestIsKo(MSG_JNDI_NAME_NOT_FOUND + jndiName);
            }
            return foundDataSource;
        } catch (NameNotFoundException ex) {
            monitoredElement.setTestIsKo(MSG_JNDI_NAME_NOT_FOUND + jndiName);
        } catch (NamingException ex) {
            monitoredElement.setTestIsKo(ex);
        } catch (ClassCastException ex) {
            monitoredElement.setTestIsKo(MSG_NOT_A_DATASOURCE + jndiName);
        }
        return null;
    }

    /**
     * Ex�cution de la requ�te de test.
     * 
     * @param monitoredElement monitored element, cette m�thode peut faire
     *            �voluer son status (par ex. si base de donn�es est KO, etc.)
     */
    private void executePingQuery(TestElement monitoredElement) {
        Connection conn = null;
        PreparedStatement statement = null;
        try {
            conn = datasource.getConnection();
            // Nota : ici est lev�e une alerte FidnBugs "A prepared statement is generated from a nonconstant String"
            // Cette alerte est pr�vue pour �viter des constructions de SQL dynamiques sans utilisation de
            // prepared statement, c'est en effet g�n�ralement peu recommand� pour des raisons de perfs et de s�curit�
            // (SQL injection...). Ici, il est de la responsabilit� de l'application utilisatrice de notre composant
            // de fournir le SQL, nous n'avons pas prise sur cela.
            statement = conn.prepareStatement(sql);
            statement.execute();
        } catch (SQLException ex) {
            monitoredElement.setTestIsKo("SQL query execution failed: database might be down: " + ex.getMessage(), ex);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e1) {
                    if (!monitoredElement.getTestIsKo()) {
                        monitoredElement.setTestIsKo(e1);
                    }
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    if (!monitoredElement.getTestIsKo()) {
                        monitoredElement.setTestIsKo(e);
                    }
                }
            }
        }
    }

    /**
     * @param myJndiName the jndiName to set
     */
    public void setJndiName(String myJndiName) {
        this.jndiName = myJndiName;
    }

    /**
     * @param mySql the sql to set
     */
    public void setSql(String mySql) {
        this.sql = mySql;
    }

    /**
     * @param myDatasource the datasource to set
     */
    public void setDatasource(DataSource myDatasource) {
        this.datasource = myDatasource;
    }

}
