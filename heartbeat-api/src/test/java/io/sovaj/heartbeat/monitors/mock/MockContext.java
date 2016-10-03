
package io.sovaj.heartbeat.monitors.mock;

import javax.naming.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Bouchon de contexte JNDI. Alimenter le contexte en ajoutant des objets dans
 * boundedObjects. Si un objet de type NamingException ou RuntimeException est
 * pr�sent au chemin "exception", alors la m�thode lookup va lever cette
 * exception. Seule 2 m�thodes sont impl�ment�es :
 * <ul>
 * <li>{@link #bind(String, Object)}</li>
 * <li>{@link #lookup(String)}</li>
 * </ul>
 */
public class MockContext implements Context {

    /**
     * Nom JNDI particulier qui a pour objet associ� une exception � lever par
     * {@link #lookup(String)}.
     */
    public static final String EXCEPTION = "exception";

    /**
     * Map servant � stocker les objets bind�s.
     */
    private Map boundedObjects;

    /**
     * Constructeur.
     */
    public MockContext() {
        boundedObjects = Collections.synchronizedMap(new HashMap());
    }

    /**
     * {@inheritDoc}
     */
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        boundedObjects.put(propName, propVal);
        return boundedObjects;
    }

    /**
     * {@inheritDoc}
     */
    public void bind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public void bind(String name, Object obj) throws NamingException {
        boundedObjects.put(name, obj);
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws NamingException {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public Name composeName(Name name, Name prefix) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String composeName(String name, String prefix) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Context createSubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Context createSubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void destroySubcontext(Name name) throws NamingException {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public void destroySubcontext(String name) throws NamingException {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public Hashtable getEnvironment() throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getNameInNamespace() throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NameParser getNameParser(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NameParser getNameParser(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration list(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration list(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration listBindings(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public NamingEnumeration listBindings(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Object lookup(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Object lookup(String name) throws NamingException {

        // Tout d'abord : sommes-nous cens�s lever une exception ?
        final Object ex = boundedObjects.get(EXCEPTION);
        if (ex != null) {
            if (ex instanceof NamingException) {
                throw (NamingException ) ex;
            } else {
                throw (RuntimeException ) ex;
            }
        }

        // R�cup�ration de l'objet au chemin sp�cifi�, ou bien
        // renvoie une NameNotFoundException si pas trouv�.
        if (boundedObjects.containsKey(name)) {
            return boundedObjects.get(name);
        } else {
            throw new NameNotFoundException(name);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object lookupLink(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public Object lookupLink(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void rebind(Name name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public void rebind(String name, Object obj) throws NamingException {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public Object removeFromEnvironment(String propName) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void rename(Name oldName, Name newName) throws NamingException {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public void rename(String oldName, String newName) throws NamingException {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    public void unbind(Name name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public void unbind(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the boundedObjects
     */
    public Map getBoundedObjects() {
        return boundedObjects;
    }

}
