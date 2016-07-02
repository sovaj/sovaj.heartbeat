
package com.ypg.car.monitoring.api;

import java.util.Date;

/**
 * Element � monitorer dans une application. Correspond � un �l�ment
 * &lt;test&gt; dans le flux XML.
 */
public class TestElement {

    /**
     * Type du test.
     */
    private Type type;

    /**
     * Nom du test.
     */
    private String name;

    /**
     * Statut du test.
     */
    private Status status;

    /**
     * Eventuel message d'erreur associ� au test.
     */
    private String errorMessage;

    /**
     * Eventuelle description.
     */
    private String description;

    /**
     * Eventuelle exception associ�e au test.
     */
    private Throwable exception;

    /**
     * Date de d�marrage du test.
     */
    private Date start;

    /**
     * Date de fin du test.
     */
    private Date stop;

    /**
     * Constructeur.
     * 
     * @param aName nom du test
     * @param aType type du test
     */
    public TestElement(String aName, Type aType) {
        this(aName, aType, null);
    }

    /**
     * Constructeur.
     * 
     * @param aName nom du test
     * @param aType type du test
     * @param aDesc description du test
     */
    public TestElement(String aName, Type aType, String aDesc) {
        this.name = aName;
        this.type = aType;
        this.description = aDesc;
        startMonitoring();
    }

    /**
     * R�cup�ration du nom du tes
     * 
     * @return nom
     */
    public String getName() {
        return name;
    }

    /**
     * R�cup�ration du type du test
     * 
     * @return type du test
     */
    public Type getType() {
        return type;
    }

    /**
     * R�cup�ration de la description du test
     * 
     * @return description du test
     */
    public String getDescription() {
        return description;
    }

    /**
     * R�cup�ration du message d'erreur du test
     * 
     * @return message d'erreur du test
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * R�cup�ration de l'exception associ�e au test
     * 
     * @return exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Le test est-il � l'�tat OK (succ�s) ?
     * 
     * @return oui/non
     */
    public boolean getTestIsOk() {
        return Status.STATUS_OK.equals(this.status);
    }

    /**
     * Le test est-il � l'�tat KO (erreur) ?
     * 
     * @return oui/non
     */
    public boolean getTestIsKo() {
        return Status.STATUS_KO.equals(this.status);
    }

    /**
     * Fait passer le test � l'�tat STATUS_OK et enregistre la date de fin du
     * test.
     */
    public void setTestIsOk() {
        this.status = Status.STATUS_OK;
        stopMonitoring();
    }

    /**
     * Fait passer le test en erreur et enregistre la date de fin du test.
     * 
     * @param anErrorMessage message d'erreur
     */
    public void setTestIsKo(String anErrorMessage) {
        this.status = Status.STATUS_KO;
        this.errorMessage = anErrorMessage;
        stopMonitoring();
    }

    /**
     * Fait passer le test en erreur et enregistre la date de fin du test.
     * 
     * @param anErrorMessage message d'erreur
     * @param anException exception � l'origine de l'�chec du test
     */
    public void setTestIsKo(String anErrorMessage, Throwable anException) {
        this.status = Status.STATUS_KO;
        this.errorMessage = anErrorMessage;
        this.exception = anException;
        stopMonitoring();
    }

    /**
     * Fait passer le test en erreur et enregistre la date de fin du test.
     * 
     * @param anException exception � l'origine de l'�chec du test
     */
    public void setTestIsKo(Throwable anException) {
        this.status = Status.STATUS_KO;
        this.errorMessage = anException.getMessage();
        this.exception = anException;
        stopMonitoring();
    }

    /**
     * Obtention de la date de d�marrage du test.
     * 
     * @return date
     */
    public Date getStart() {
        // Renvoie une copie de la date, car Date n'est pas immutable.
        if (start == null) {
            return null;
        } else {
            return new Date(start.getTime());
        }
    }

    /**
     * Obtention de la date de fin du test.
     * 
     * @return date
     */
    public Date getStop() {
        // Renvoie une copie de la date, car Date n'est pas immutable.
        if (stop == null) {
            return null;
        } else {
            return new Date(stop.getTime());
        }
    }

    /**
     * D�marrage du test : met la date de d�marrage � maintenant.
     */
    private void startMonitoring() {
        start = new Date();
    }

    /**
     * Arr�t du test : met la date de fin � maintenant.
     */
    private void stopMonitoring() {
        stop = new Date();
    }

    /**
     * R�cup�ration de l'�tat du test.
     * 
     * @return the status
     */
    public Status getStatus() {
        return status;
    }
}
