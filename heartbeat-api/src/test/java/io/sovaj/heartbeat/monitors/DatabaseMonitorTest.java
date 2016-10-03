package io.sovaj.heartbeat.monitors;

import io.sovaj.heartbeat.api.TestElement;
import io.sovaj.heartbeat.api.Type;
import io.sovaj.heartbeat.monitors.mock.MockContext;
import org.apache.commons.dbcp.BasicDataSource;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Test de {@link DatabaseMonitor}. Utilise pour cela une datasource locale
 * (gr�ce � DBCP) vers une base de donn�es locale en RAM (HSQLDB).
 */
public class DatabaseMonitorTest extends AbstractMonitorTestCase {

    /**
     * Message d'erreur attendu.
     */
    private static final String MSG_NO_DATASOURCE = "no jndiName / no datasource set for DatabaseMonitor";

    /**
     * Message d'erreur attendu.
     */
    private static final String MSG_NO_SQL = "no SQL query set for DatabaseMonitor";

    /**
     * Nom JNDI de la datasource de test.
     */
    private static final String DATASOURCE_NAME = "/myDataSource";

    /**
     * Requ�te SQL ex�cut�e pour effectuer le test de la base de donn�es.
     */
    private static final String PING_SQL = "select * from TOTO";

    /**
     * Datasource.
     */
    private DataSource datasource;

    /**
     * Initialisation de chaque test.
     * 
     * @throws Exception tout type d'exception
     */
    public void setUp() throws Exception {
        super.setUp();

        // Cr�ation de la datasource de test :
        datasource = setupDataSource();

        // Placement de la datasource dans notre JNDI local :
        getCtx().bind(DATASOURCE_NAME, datasource);

        // Initialisation de notre base de donn�es de test :
        initDb(datasource);
    }

    /**
     * Initialisation de la base de donn�es de test.
     * 
     * @param ds datasource
     * @throws SQLException si pb SQL
     */
    private void initDb(DataSource ds) throws SQLException {

        final Connection c = ds.getConnection();

        // Est-ce qu'on n'a pas d�j� cr�� la table ?
        final ResultSet rs = c.getMetaData().getTables(null, null, "TOTO", null);
        final boolean hasTable = rs.next();
        rs.close();

        if (!hasTable) {
            // Cr�ation de la table :
            final PreparedStatement st = c.prepareStatement("create table TOTO ( col char(20) )");
            st.execute();
            st.close();
        }

        c.close();
    }

    /**
     * Cr�ation d'une datasource vers une base de test HSQLDB.
     * 
     * @return datasource
     */
    private DataSource setupDataSource() {
        final BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUsername("sa");
        ds.setPassword("");
        ds.setUrl("jdbc:hsqldb:mem:testdb");
        return ds;
    }

    /**
     * Test sans avoir sp�cifi� de nom JNDI de datasource.
     * 
     * @throws Exception tout type d'exception
     */
    public void testNoJndiName() throws Exception {
        final TestElement testElem = new DatabaseMonitor((String ) null, PING_SQL).monitor();
        assertMissingConfigParam(testElem, MSG_NO_DATASOURCE);
    }

    /**
     * Test sans avoir sp�cifi� de datasource.
     * 
     * @throws Exception tout type d'exception
     */
    public void testNoDs() throws Exception {
        final TestElement testElem = new DatabaseMonitor((DataSource ) null, PING_SQL).monitor();
        assertMissingConfigParam(testElem, MSG_NO_DATASOURCE);
    }

    /**
     * Test sans avoir sp�cifi� de requ�te SQL � ex�cuter.
     * 
     * @throws Exception tout type d'exception
     */
    public void testNoSQL() throws Exception {
        final TestElement testElem = new DatabaseMonitor(DATASOURCE_NAME, null).monitor();
        assertMissingConfigParam(testElem, MSG_NO_SQL);
    }

    /**
     * @param testElem �l�ment
     * @param expectedMsg message attendu
     */
    private void assertMissingConfigParam(TestElement testElem, String expectedMsg) {
        assertTrue("testIsKo", testElem.getTestIsKo());
        assertEquals("errorMessage", expectedMsg, testElem.getErrorMessage());
        assertNull("exception", testElem.getException());
        assertNotNull("startDate", testElem.getStart());
        assertNotNull("stopDate", testElem.getStop());
    }

    /**
     * Test avec datasource introuvable.
     * 
     * @throws Exception tout type d'exception
     */
    public void testDatasourceNotFound() throws Exception {

        // Ex�cution du test :
        final String jndiName = "/myDataSourceNotFound";
        final TestElement elem = new DatabaseMonitor(jndiName, PING_SQL).monitor();

        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsKo", elem.getTestIsKo());
        assertEquals("errorMessage", "JNDI name not found: " + jndiName, elem.getErrorMessage());
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "Database test,DS=" + jndiName, elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    /**
     * Test avec nom JNDI obtenu qui n'est pas une datasource.
     * 
     * @throws Exception tout type d'exception
     */
    public void testNotADataSource() throws Exception {
        // Ajout d'un objet dans l'annuaire JNDI de test :
        final String jndiName = "/myString";
        getCtx().bind(jndiName, "I'm not a datasource");

        // Ex�cution du test :
        final TestElement elem = new DatabaseMonitor(jndiName, PING_SQL).monitor();

        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsKo", elem.getTestIsKo());
        assertTrue("errorMessage", elem.getErrorMessage().indexOf("is not a datasource") >= 0);
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "Database test,DS=" + jndiName, elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    /**
     * Test avec nom JNDI obtenu dont la valeur correspondante est null.
     * 
     * @throws Exception tout type d'exception
     */
    public void testJNDINull() throws Exception {
        final String jndiName = "/myNull";
        getCtx().bind(jndiName, null);

        // Ex�cution du test :
        final TestElement elem = new DatabaseMonitor(jndiName, PING_SQL).monitor();

        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsKo", elem.getTestIsKo());
        assertTrue("errorMessage", elem.getErrorMessage().indexOf("name not found") >= 0);
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "Database test,DS=" + jndiName, elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    /**
     * Test avec nom JNDI obtenu donnant une NamingException.
     * 
     * @throws Exception tout type d'exception
     */
    public void testNamingException() throws Exception {
        final String jndiName = DATASOURCE_NAME;
        final Exception ex = new NamingException("myException");
        getCtx().bind(MockContext.EXCEPTION, ex);

        // Ex�cution du test :
        final TestElement elem = new DatabaseMonitor(jndiName, PING_SQL).monitor();

        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsKo", elem.getTestIsKo());
        assertTrue("errorMessage", elem.getErrorMessage().indexOf("myException") >= 0);
        assertSame("exception", ex, elem.getException());
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "Database test,DS=" + jndiName, elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    /**
     * Test cas nominal.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOK() throws Exception {

        final String jndiName = DATASOURCE_NAME;

        // Ex�cution du test :
        final TestElement elem = new DatabaseMonitor(jndiName, PING_SQL).monitor();

        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsOk", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "Database test,DS=" + jndiName, elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    /**
     * Test cas nominal avec surcharge du nom par d�faut.
     * 
     * @throws Exception tout type d'exception
     */
    public void testSurchargeNom() throws Exception {

        final String jndiName = DATASOURCE_NAME;

        // Ex�cution du test :
        final DatabaseMonitor mon = new DatabaseMonitor();
        mon.setJndiName(jndiName);
        mon.setSql(PING_SQL);
        mon.setName("nom surcharg�");
        final TestElement elem = mon.monitor();

        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsOk", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "nom surcharg�", elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    /**
     * Test cas nominal avec constructeur par d�faut.
     * 
     * @throws Exception tout type d'exception
     */
    public void testDefaultConstructor() throws Exception {

        final String jndiName = DATASOURCE_NAME;

        // Ex�cution du test :
        final DatabaseMonitor mon = new DatabaseMonitor();
        mon.setJndiName(jndiName);
        mon.setSql(PING_SQL);
        final TestElement elem = mon.monitor();

        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsOk", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "Database test", elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    /**
     * Test cas nominal, on fournir directement l'objet Datasource.
     * 
     * @throws Exception tout type d'exception
     */
    public void testOKSupplyDatasource() throws Exception {

        // Ex�cution du test :
        final TestElement elem = new DatabaseMonitor(datasource, PING_SQL).monitor();

        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsOk", elem.getTestIsOk());
        assertNull("errorMessage", elem.getErrorMessage());
        assertNull("exception", elem.getException());
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "Database test", elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    /**
     * Test erreur SQL.
     * 
     * @throws Exception tout type d'exception
     */
    public void testSQLError() throws Exception {

        // Ex�cution du test :
        final String jndiName = DATASOURCE_NAME;
        final TestElement elem = new DatabaseMonitor(jndiName, "select * from z").monitor();
        System.out.println(elem.getErrorMessage());
        // V�rification des r�sultats :
        assertNotNull("null result", elem);
        assertTrue("testIsKo", elem.getTestIsKo());
        assertTrue("errorMessage", elem.getErrorMessage().indexOf("not found") >= 0);
        assertTrue("exception", elem.getException() instanceof SQLException);
        assertEquals("type", Type.DATABASE, elem.getType());
        assertEquals("name", "Database test,DS=" + jndiName, elem.getName());
        assertNotNull("startDate", elem.getStart());
        assertNotNull("stopDate", elem.getStop());
    }

    // TODO Test erreur SQL lev�e par le close du Statement.
    // TODO Test erreur SQL lev�e par le close de la Connection.
}
