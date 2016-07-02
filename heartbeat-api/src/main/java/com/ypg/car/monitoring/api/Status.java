
package com.ypg.car.monitoring.api;

/**
 * Etats possibles que peut prendre un test de monitoring.
 */
public final class Status {

    /**
     * Etat STATUS_OK : le test est un succ�s.
     */
    public static final Status STATUS_OK = new Status("OK");

    /**
     * Etat STATUS_KO : le test est en erreur.
     */
    public static final Status STATUS_KO = new Status("KO");

    /**
     * Libell� correspondant au status.
     */
    private String label;

    /**
     * Constructeur par d�faut private pour �viter la cr�ation d'instances.
     */
    private Status() {

    }

    /**
     * Construction � partir d'un libell�.
     * @param aLabel libell�.
     */
    private Status(String aLabel) {
        this.label = aLabel;
    }

    /**
     * Repr�sentation sous forme de cha�ne de caract�res.
     * @return cha�ne
     */
    public String toString() {
        return label;
    }
}
