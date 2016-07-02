
package com.ypg.car.monitoring.api;

/**
 * Type possible que peut prendre un test de monitoring
 */
public final class Type {

    /**
     * Type : test de base de donn�es.
     */
    public static final Type DATABASE = new Type("Database");

    /**
     * Type : test d'une requ�te LDAP.
     */
    public static final Type LDAP = new Type("LDAP");

    /**
     * Type : test de la CTG.
     */
    public static final Type CTG = new Type("CTG");

    /**
     * Type : test d'envoi de mail.
     */
    public static final Type SMTP = new Type("SMTP");

    /**
     * Type : test de file system.
     */
    public static final Type FILE_SYSTEM = new Type("File System");

    /**
     * Type : test d'appel de webservice.
     */
    public static final Type WEB_SERVICE = new Type("Web Service");

    /**
     * Type : test d'appel d'EJB.
     */
    public static final Type EJB = new Type("EJB");

    /**
     * Type : test de pr�sence d'un nom JNDI.
     */
    public static final Type JNDI = new Type("JNDI");

    /**
     * Type : test de pr�sence d'une queue JMS.
     */
    public static final Type JMS = new Type("JMS");

    /**
     * Type : test de version de JVM.
     */
    public static final Type JVM = new Type("JVM");

    /**
     * Type : test de Servlet.
     */
    public static final Type SERVLET = new Type("Servlet");

    /**
     * Type : test de JSP.
     */
    public static final Type JSP = new Type("JSP");

    /**
     * Type : test d'appel d'une URL.
     */
    public static final Type URL = new Type("URL");

    /**
     * Type : test de SCORT.
     */
    public static final Type SCORT = new Type("Scort");

    /**
     * Type : test de type autre.
     */
    public static final Type OTHER = new Type("Other");

    /**
     * Libell� correspondant au type.
     */
    private String label;

    /**
     * Constructeur par d�faut private pour �viter la cr�ation d'instances.
     */
    private Type() {

    }

    /**
     * Construction � partir d'un libell�.
     * @param aLabel libell�.
     */
    private Type(String aLabel) {
        this.label = aLabel;
    }

    /**
     * Repr�sentation sous forme de cha�ne de caract�res.
     * 
     * @return cha�ne
     */
    public String toString() {
        return label;
    }
}
