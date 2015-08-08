package com.ffeichta.notenliste.model;

/**
 * Stellt ein Item aus einer ListView einer Activity dar, welche Informationen
 * über die App bereitstellt. Ein Item besteht aus einem Titel und einer
 * Beschreibung
 *
 * @author Feichter Fabian
 */
public class Item {

    protected String titel = null;
    protected String beschreibung = null;

    public Item(String titel, String beschreibung) {
        this.titel = titel;
        this.beschreibung = beschreibung;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}
