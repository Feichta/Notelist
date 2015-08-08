package com.ffeichta.notenliste.model;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Ein Fach hat eine Nummer, einen Namen, den Namen einer Lehrperson und viele
 * Noten
 *
 * @author Wild Michael & Feichter Fabian
 */
public class Fach {
    /*
     * Fehlerkonstanten
     */
    public static final int FACH_NAME_NICHT_GESETZT = -1;
    public static final int FACH_LEHRPERSON_NICHT_GESETZT = -2;
    public static final int FACH_FACHNAME_BEREITS_VORHANDEN = -3;

    protected int nummer = -1;
    protected String name = null;
    protected String lehrperson = null;
    protected ArrayList<com.ffeichta.notenliste.model.Note> noten = null;

    /*
     * HashTable nimmt die Fehler pro Facheigenschaft auf
     */
    protected Hashtable<String, Integer> fehler = null;

    /*
     * Konstruktoren
     */
    public Fach() {
        super();
    }

    public Fach(int nummer, String name, String lehrperson) {
        this.nummer = nummer;
        this.name = name;
        this.lehrperson = lehrperson;
    }

    /*
     * Automatisch generierte Getter- und Settermethoden
     */
    public int getNummer() {
        return nummer;
    }

    public void setNummer(int nummer) {
        this.nummer = nummer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLehrperson() {
        return lehrperson;
    }

    public void setLehrperson(String lehrperson) {
        this.lehrperson = lehrperson;
    }

    public ArrayList<com.ffeichta.notenliste.model.Note> getNoten() {
        return noten;
    }

    public void setNoten(ArrayList<com.ffeichta.notenliste.model.Note> noten) {
        this.noten = noten;
    }

    public Hashtable<String, Integer> getFehler() {
        return this.fehler;
    }

	/*
     * Eigene Getter- und Settermethoden
	 */

    /**
     * Liefert alle Noten des Faches in einen String zurück
     *
     * @return
     */
    public String getNotenString() {
        String ret = null;
        if (this.noten != null) {
            for (int i = 0; i < this.noten.size(); i++) {
                if (i == 0)
                    ret = "" + this.noten.get(i).getNote();
                else
                    ret += " | " + this.noten.get(i).getNote();
            }
        }
        return ret;
    }

    /**
     * Liefert die Durchschnittsnote des Faches auf zwei Kommastellen gerundet,
     * wobei die Gewichtung jeder Note berücksichtigt wird. Falls keine Noten
     * für das Fach vorhanden sind, wird -1.0 zurück geliefert
     *
     * @return
     */
    public double getDurchschnittGerundet() {
        double ret = 0.0;
        if (this.noten == null || this.noten.size() == 0)
            ret = -1.0;
        else {
            double summeNotenMalGewicht = 0;
            int summeGewichtungen = 0;

            for (int i = 0; i < this.noten.size(); i++) {
                Note n = this.noten.get(i);
                summeNotenMalGewicht += n.getNote() * n.getGewichtung();
                summeGewichtungen += n.getGewichtung();
            }
            // Runden auf zwei Kommastellen
            ret = Math.round(summeNotenMalGewicht / summeGewichtungen * 100.0) / 100.0;
        }
        return ret;
    }

    /**
     * Liefert die Durchschnittsnote des Faches, nicht gerundet, wobei die
     * Gewichtung jeder Note berücksichtigt wird. Falls keine Noten für das Fach
     * vorhanden sind, wird -1.0 zurück geliefert
     *
     * @return
     */
    public double getDurchschnittNichtGerundet() {
        double ret = 0.0;
        if (this.noten == null || this.noten.size() == 0)
            ret = -1.0;
        else {
            double summeNotenMalGewicht = 0;
            int summeGewichtungen = 0;

            for (int i = 0; i < this.noten.size(); i++) {
                Note n = this.noten.get(i);
                summeNotenMalGewicht += n.getNote() * n.getGewichtung();
                summeGewichtungen += n.getGewichtung();
            }
            // Runden auf zwei Kommastellen
            ret = summeNotenMalGewicht / summeGewichtungen;
        }
        return ret;
    }

    /**
     * Liefert true, wenn das Fach negativ ist. Eine Fach gilt als negativ, wenn
     * sein Durchschnitt < 5.5 ist
     *
     * @return
     */
    public boolean istAvgNegativ() {
        return getDurchschnittGerundet() < 5.5;
    }

    /**
     * Schafft die Möglichkeit, einen Fehler im Fach zu setzen
     *
     * @param key
     * @param value
     */
    public void setFehler(String key, Integer value) {
        if (key != null && key.length() > 0 && value != null) {
            if (this.fehler == null)
                this.fehler = new Hashtable<String, Integer>();
            this.fehler.put(key, value);
        }
    }

	/*
     * Methoden
	 */

    /**
     * Ein Fach ist dann korrekt, wenn es einen Namen und eine Lehrperson
     * besitzt. Diese Methode kontrolliert ein Fach auf seine Korrektheit und
     * füllt gegebenenfalls die Fehlernummern pro Eigenschaft in die HashTable
     * des Fehlers
     */
    public void validiere() {
        // WICHTIG: Fehlerobjekt muss gelöscht werden um bereits eingetragene
        // Fehler
        // zu verlieren
        this.fehler = null;
        if (this.name == null || this.name.length() == 0)
            setFehler("name", Fach.FACH_NAME_NICHT_GESETZT);
        if (this.lehrperson == null || this.lehrperson.length() == 0)
            setFehler(
                    "lehrperson",
                    com.ffeichta.notenliste.model.Fach.FACH_LEHRPERSON_NICHT_GESETZT);
    }
}