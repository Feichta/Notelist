package com.ffeichta.notenliste.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Eine Note hat eine Nummer, eine Beschreibung, ein Datum, die numerische Note,
 * deren Gewichtung sowie eine Referenz auf das Fach zu dem die Note gehört
 *
 * @author Wild Michael & Feichter Fabian
 */
public class Note {
    /*
     * Fehlerkonstanten
     */
    public static final int NOTE_BESCHREIBUNG_NICHT_GESETZT = -1;
    public static final int NOTE_DATUM_NICHT_GESETZT = -2;
    public static final int NOTE_NOTE_NICHT_GESETZT = -3;
    public static final int NOTE_NOTE_ZU_HOCH = -4;

    protected int nummer = -1;
    protected String beschreibung = null;
    protected long datum = 0;
    protected double note = -1.0;
    protected int gewichtung = 0;
    protected Fach fach = null;

    /*
     * HashTable nimmt die Fehler pro Noteneigenschaft auf
     */
    protected Hashtable<String, Integer> fehler = null;

    /*
     * Konstruktoren
     */
    public Note() {
        super();
    }

    public Note(int nummer, String beschreibung, long datum, double note,
                int gewichtung) {
        this.nummer = nummer;
        this.beschreibung = beschreibung;
        this.datum = datum;
        this.note = note;
        this.gewichtung = gewichtung;
    }

    public Note(int nummer, String beschreibung, long datum, double note,
                int gewichtung, Fach fach) {
        this(nummer, beschreibung, datum, note, gewichtung);
        this.fach = fach;
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

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public long getDatum() {
        return datum;
    }

    public void setDatum(long datum) {
        this.datum = datum;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public int getGewichtung() {
        return gewichtung;
    }

    public void setGewichtung(int gewichtung) {
        this.gewichtung = gewichtung;
    }

    public Fach getFach() {
        return fach;
    }

    public void setFach(Fach fach) {
        this.fach = fach;
    }

    public Hashtable<String, Integer> getFehler() {
        return this.fehler;
    }

	/*
     * Eigene Getter- und Settermethoden
	 */

    /**
     * Liefert das Datum als String in der Form tt.mm.jjjj
     *
     * @return
     */
    public String getDatumString() {
        String ret = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        ret = sdf.format(new java.util.Date(this.datum));
        return ret;
    }

    /**
     * Liefert das Datum als Calendar-Objekt zurück, welches in ein
     * DatePicker-Objekt anhand der Methode updateDate() geschrieben werden kann
     *
     * @return
     */
    public Calendar getDatumCalendar() {
        Calendar ret = Calendar.getInstance();
        ret.setTimeInMillis(this.datum);
        return ret;
    }

    /**
     * Schafft die Möglichkeit, einen Fehler in der Note zu setzen
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
     * Eine Note ist dann korrekt, wenn sie eine Beschreibung, ein Datum (> 0)
     * und eine Note (> 0.0) besitzt. Diese Methode kontrolliert eine Note auf
     * ihre Korrektheit und füllt gegebenenfalls die Fehlernummern pro
     * Eigenschaft in die HashTable des Fehlers
     */
    public void validiere() {
        // WICHTIG: Fehlerobjekt muss gelöscht werden um bereits eingetragene
        // Fehler zu verlieren
        this.fehler = null;
        if (this.beschreibung == null || this.beschreibung.length() <= 0) {
            setFehler(
                    "beschreibung",
                    com.ffeichta.notenliste.model.Note.NOTE_BESCHREIBUNG_NICHT_GESETZT);
        }
        if (datum <= 0) {
            setFehler("datum",
                    com.ffeichta.notenliste.model.Note.NOTE_DATUM_NICHT_GESETZT);
        }
        if (note < 0.0) {
            setFehler("note",
                    com.ffeichta.notenliste.model.Note.NOTE_NOTE_NICHT_GESETZT);
        }
        if (note > 10.0) {
            setFehler("note",
                    com.ffeichta.notenliste.model.Note.NOTE_NOTE_ZU_HOCH);
        }
    }

    /**
     * Liefert true, falls die Note negativ ist. Eine Note gild als negativ,
     * wenn ihr Durchschnitt < 5.5 ist
     *
     * @return
     */
    public boolean istNegativ() {
        return this.note < 5.5;
    }
}