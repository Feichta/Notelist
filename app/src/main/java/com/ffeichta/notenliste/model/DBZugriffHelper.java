package com.ffeichta.notenliste.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Schreibt und liesst die Objekte des Modells in bzw. aus der Datenbank. Klasse
 * implementiert das Singleton-Pattern. Das bedeutet, dass es immer nur eine
 * Instanz des Objektes gibt, welches auf die Datenbank zugreift. Die Klasse
 * ermöglicht es die Datenbank zu Testzwecken mit Beispieldatensätzen zu füllen.
 * Weiters kann die Datenbank importiert bzw. exportiert werden
 *
 * @author Wild Michael & Feichter Fabian
 */
public class DBZugriffHelper extends SQLiteOpenHelper {

    // TAG wird für das Erstellen der Log Dateien benötigt
    private static final String TAG = com.ffeichta.notenliste.model.DBZugriffHelper.class
            .getSimpleName();

    /*
     * Datenbankdefinitionen
     */
    // Name der Datei, welche Datenbank beinhaltet
    private static final String DATENBANK_NAME = "noten.db";
    // Name der Datei, welche die exportierte Datenbank beinhaltet
    private static final String DATENBANK_BACKUP_NAME = "notenliste_backup.db";
    // Name des absoluten Pfades, in welchem die exportierte Datenbank
    // gespeichert wird
    public static final String DATENBANK_BACKUP_PFAD = "/sdcard/com.ffeichta.notenliste/"
            + DATENBANK_BACKUP_NAME;
    // Names des Pfades, in welchem Android standardmaessig Datenbanken dieser
    // App speichert
    private static final String DATENBANK_PFAD = "//data//com.ffeichta.notenliste//databases//"
            + DATENBANK_NAME;
    // Gleich wie DATENBANK_PFAD, jedoch mit einem weiteren Verzeichnis, ohne
    // das es nicht möglich ist, die Datenbank zu importieren
    private static final String DATENBANK_PFAD_IMPORT = "/data/data/com.ffeichta.notenliste/databases/"
            + DATENBANK_NAME;
    // Wird Versionsnummer geändert, so wird automatisch die Datenbank neu
    // angelegt
    private static final int DATENBANK_VERSION = 7;

    private static String DROP_FAECHER = "DROP TABLE IF EXISTS faecher";
    private static String CREATE_FAECHER = "CREATE TABLE faecher( "
            + "  fanummer INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + "  faname VARCHAR(100) NOT NULL UNIQUE, "
            + "  falehrperson VARCHAR(100) NOT NULL);";

    private static String DROP_NOTEN = "DROP TABLE IF EXISTS noten;";
    private static String CREATE_NOTEN = "CREATE TABLE noten( "
            + "  nonummer INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + "  nobeschreibung VARCHAR(100) NOT NULL, "
            + "  nodatum INTEGER NOT NULL, " + "  nonote FLOAT NOT NULL, "
            + "  nogewicht INTEGER NOT NULL, "
            + "  fanummer INTEGER NOT NULL, " + "  FOREIGN KEY (fanummer) "
            + "    REFERENCES faecher(fanummer) "
            + "    ON DELETE CASCADE ON UPDATE RESTRICT);";

    /*
     * Beispielinhalte der Datenbank
     */
    private static String INSERT_FACH1 = "INSERT INTO faecher(fanummer, faname, falehrperson) "
            + "  VALUES(1, \"Deutsch\", \"Dorothea Dorn\");";
    private static String INSERT_FACH2 = "INSERT INTO faecher(fanummer, faname, falehrperson) "
            + "  VALUES(2, \"Mathematik\", \"Martha Molling\");";
    private static String INSERT_FACH3 = "INSERT INTO faecher(fanummer, faname, falehrperson) "
            + "  VALUES(3, \"Englisch\", \"Emma Smith\");";
    private static String INSERT_FACH1_NOTE1 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Schularbeit\", 365*24*60*60*43*1000, 4.0, 100, 1);";
    private static String INSERT_FACH1_NOTE2 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Prüfen\", 365*24*60*60*43*1000 + 24*60*60*1000, 5.5, 90, 1);";
    private static String INSERT_FACH1_NOTE3 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Hausaufgabe\", 365*24*60*60*43*1000  + 24*60*60*1000 + 24*60*60*1000, 6, 50, 1);";
    private static String INSERT_FACH2_NOTE1 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Schularbeit\", 365*24*60*60*43*1000, 10.0, 50, 2);";
    private static String INSERT_FACH2_NOTE2 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Prüfen\", 365*24*60*60*43*1000 + 24*60*60*1000, 8.5, 50, 2);";
    private static String INSERT_FACH2_NOTE3 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Hausaufgabe\", 365*24*60*60*43*1000  + 24*60*60*1000 + 24*60*60*1000, 7, 50, 2);";
    private static String INSERT_FACH3_NOTE1 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Schularbeit\", 365*24*60*60*43*1000, 5.0, 50, 3);";
    private static String INSERT_FACH3_NOTE2 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Prüfen\", 365*24*60*60*43*1000 + 24*60*60*1000, 6.5, 50, 3);";
    private static String INSERT_FACH3_NOTE3 = "INSERT INTO noten(nobeschreibung, nodatum, nonote, nogewicht, fanummer) "
            + "  VALUES(\"Hausaufgabe\", 365*24*60*60*43*1000  + 24*60*60*1000 + 24*60*60*1000, 8, 90, 3);";

    private static com.ffeichta.notenliste.model.DBZugriffHelper instance = null;

	/*
     * Methoden des SQLiteOpenHelper
	 */

    /**
     * Muss private sein wegen Singleton-Pattern
     *
     * @param context
     */
    private DBZugriffHelper(Context context) {
        super(context, DATENBANK_NAME, null, DATENBANK_VERSION);
    }

    /**
     * Diese Methode stellt ein Objekt zur Verfügung, durch welches man auf die
     * Datenbank zugreifen kann
     *
     * @param context
     * @return
     */
    public static com.ffeichta.notenliste.model.DBZugriffHelper getInstance(
            Context context) {
        if (instance == null)
            instance = new com.ffeichta.notenliste.model.DBZugriffHelper(
                    context);
        return instance;
    }

    /**
     * Wird aufgerufen, wenn sich DB-Version ändert. Dadurch können Änderungen
     * an der Datenbankstruktur gemacht werden
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_NOTEN);
        db.execSQL(DROP_FAECHER);
        Log.d(TAG, "DB gelöscht");
        onCreate(db);
    }

    /**
     * Wird nur dann aufgerufen, wenn Datenbank nicht existiert. Methode sorgt
     * bei Testzwecken dafür, dass neue Datenbank mit Beispielhinhalten gefüllt
     * wird
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAECHER);
        db.execSQL(CREATE_NOTEN);
        Log.d(TAG, "DB angelegt");
        // Wird nur für Testzwecke benötigt
        // fuellenDB(db);
    }

    /**
     * Füllt die Datenbank mit beispielhaften Werten
     */
    private void fuellenDB(SQLiteDatabase db) {
        Log.d(TAG, "fuellenDB() gestartet");
        db.execSQL(INSERT_FACH1);
        db.execSQL(INSERT_FACH2);
        db.execSQL(INSERT_FACH3);
        db.execSQL(INSERT_FACH1_NOTE1);
        db.execSQL(INSERT_FACH1_NOTE2);
        db.execSQL(INSERT_FACH1_NOTE3);
        db.execSQL(INSERT_FACH2_NOTE1);
        db.execSQL(INSERT_FACH2_NOTE2);
        db.execSQL(INSERT_FACH2_NOTE3);
        db.execSQL(INSERT_FACH3_NOTE1);
        db.execSQL(INSERT_FACH3_NOTE2);
        db.execSQL(INSERT_FACH3_NOTE3);
    }

	/*
     * Methoden zur Manipulation von Fächern
	 */

    /**
     * Holt alle Fächer mit den Noten aus der Datenbank
     *
     * @return null falls keine Fächer auffindbar sind
     */
    public ArrayList<com.ffeichta.notenliste.model.Fach> getFaecher() {
        ArrayList<com.ffeichta.notenliste.model.Fach> ret = null;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * " + "  FROM faecher;", null);
            while (c.moveToNext()) {
                if (ret == null)
                    ret = new ArrayList<com.ffeichta.notenliste.model.Fach>();
                ret.add(new com.ffeichta.notenliste.model.Fach(c.getInt(0), c
                        .getString(1), c.getString(2)));
            }
            if (ret != null)
                // Falls Fächer gefunden wurden, werden deren Noten geholt
                for (int i = 0; i < ret.size(); i++)
                    ret.get(i).setNoten(getNoten(ret.get(i)));
        } catch (SQLiteException e) {
            Log.d(TAG, "Fehler in getFaecher(): " + e.getMessage());
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                ;
            }
            try {
                db.close();
            } catch (Exception e) {
                ;
            }
        }
        if (ret != null)
            Log.d(TAG, "getFaecher() erfolgreich");
        return ret;
    }

    /**
     * Holt das Fach mit allen seinen Noten aus der Datenbank
     *
     * @param nummer
     * @return
     */
    public com.ffeichta.notenliste.model.Fach getFach(int nummer) {
        com.ffeichta.notenliste.model.Fach ret = null;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * " + "  FROM faecher "
                            + "  WHERE fanummer = ?;",
                    new String[]{String.valueOf(nummer)});
            if (c.moveToFirst())
                ret = new com.ffeichta.notenliste.model.Fach(c.getInt(0),
                        c.getString(1), c.getString(2));
            if (ret != null)
                // Falls Fach gefunden wurden, werden dessen Noten geholt
                ret.setNoten(getNoten(ret));
        } catch (SQLiteException e) {
            Log.d(TAG, "Fehler in getFach(): " + e.getMessage());
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                ;
            }
            try {
                db.close();
            } catch (Exception e) {
                ;
            }
        }
        if (ret != null)
            Log.d(TAG, "getFach() erfolgreich");
        return ret;
    }

    /**
     * Fügt das übergebene Fach in die Datenbank ein. Kontrolliert vorher aber,
     * ob das Fach korrekt ist
     *
     * @param f
     * @return 0 falls erfolgreich. -1 falls Fach nicht eingetragen werden
     * konnte, beispielsweise weil Fach fehlerhaft ist
     */
    public int hinzufuegenFach(com.ffeichta.notenliste.model.Fach f) {
        int ret = 0;
        if (f == null)
            ret = -1;
        else {
            // Kontrolliere ob Fach korrekt ist
            f.validiere();
            if (f.getFehler() != null)
                ret = -1;
            else {
                // Es wird versucht, Fach in die Datenbank aufzunehmen
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    ContentValues werte = new ContentValues(2);
                    werte.put("faname", f.getName());
                    werte.put("falehrperson", f.getLehrperson());
                    ret = (int) db.insertOrThrow("faecher", null, werte);
                    if (ret >= 0) {
                        // Schreibt den neuen Primärschlüssel in das Fach-Objekt
                        f.setNummer(ret);
                        ret = 0;
                    } else {
                        ret = -1;
                    }
                } catch (SQLiteConstraintException e) {
                    // Fach mit selben Namen bereits vorhanden
                    f.setFehler(
                            "name",
                            com.ffeichta.notenliste.model.Fach.FACH_FACHNAME_BEREITS_VORHANDEN);
                    ret = -1;
                } catch (SQLiteException e) {
                    Log.d(TAG, "Fehler in hinzufuegenFach(): " + e.getMessage());
                    ret = -1;
                } finally {
                    try {
                        db.close();
                    } catch (Exception e) {
                        ;
                    }
                }
            }
        }
        if (ret == 0)
            Log.d(TAG, "hinzufuegenFach() erfolgreich");
        return ret;
    }

    /**
     * Ändert das bereits in der Datenbank vorhandene Fach indem der Fachname
     * und die Lehrperson geändert wird. Kontrolliert vorher aber, ob das Fach
     * korrekt ist
     *
     * @param f
     * @return
     */
    public int aendernFach(com.ffeichta.notenliste.model.Fach f) {
        int ret = 0;
        if (f == null)
            ret = -1;
        else {
            // Kontrolliere ob Fach korrekt ist
            f.validiere();
            if (f.getFehler() != null)
                ret = -1;
            else {
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    ContentValues werte = new ContentValues(2);
                    werte.put("faname", f.getName());
                    werte.put("falehrperson", f.getLehrperson());
                    if ((int) db.update("faecher", werte, "fanummer = ?",
                            new String[]{String.valueOf(f.getNummer())}) == 0)
                        ret = -1;
                } catch (SQLiteConstraintException e) {
                    // Es existiert bereits Fach mit dem Namen
                    f.setFehler(
                            "name",
                            com.ffeichta.notenliste.model.Fach.FACH_FACHNAME_BEREITS_VORHANDEN);
                    ret = -1;
                } catch (SQLiteException e) {
                    Log.d(TAG, "Fehler in aendernFach(): " + e.getMessage());
                    ret = -1;
                } finally {
                    try {
                        db.close();
                    } catch (Exception e) {
                        ;
                    }
                }

            }
        }
        if (ret == 0)
            Log.d(TAG, "aendernFach() erfolgreich");
        return ret;
    }

    /**
     * Löscht alle Fächer mit ihren Noten aus der Datenbank
     *
     * @return
     */
    public int loeschenAlleFaecher() {
        int ret = 0;
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.delete("faecher", null, null);
        } catch (SQLiteException e) {
            Log.d(TAG, "Fehler in loeschenAlleFaecher(): " + e.getMessage());
            ret = -1;
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                ;
            }
        }
        if (ret == 0)
            Log.d(TAG, "loeschenAlleFaecher() erfolgreich");
        return ret;
    }

    /**
     * Löscht das Fach mit allen seinen Noten
     *
     * @param f
     * @return
     */
    public int loeschenFach(com.ffeichta.notenliste.model.Fach f) {
        int ret = 0;
        if (f == null)
            ret = -1;
        else {
            SQLiteDatabase db = null;
            try {
                db = getWritableDatabase();
                if (db.delete("faecher", "fanummer = ?",
                        new String[]{String.valueOf(f.getNummer())}) != 1)
                    ret = -1;
            } catch (SQLiteException e) {
                Log.d(TAG, "Fehler in loeschenAlleFaecher(): " + e.getMessage());
                ret = -1;
            } finally {
                try {
                    db.close();
                } catch (Exception e) {
                    ;
                }
            }
        }
        if (ret == 0)
            Log.d(TAG, "loeschenFach() erfolgreich");
        return ret;
    }

	/*
     * Methoden zur Manipulation von Noten
	 */

    /**
     * Ermittelt zum übergebenen Fach alle Noten, die zu diesem Fach gehören.
     * Dabei wird der Note auch die Referenz auf das Fach übergeben, zu dem die
     * Note gehört
     *
     * @param f
     * @return
     */
    public ArrayList<Note> getNoten(Fach f) {
        ArrayList<Note> ret = null;
        if (f != null) {
            SQLiteDatabase db = null;
            Cursor c = null;
            try {
                db = getWritableDatabase();
                c = db.rawQuery("SELECT * " + "  FROM noten "
                                + "  WHERE fanummer = ? " + "  ORDER BY nodatum;",
                        new String[]{String.valueOf(f.getNummer())});
                while (c.moveToNext()) {
                    if (ret == null)
                        ret = new ArrayList<Note>();
                    ret.add(new Note(c.getInt(0), c.getString(1), c.getLong(2),
                            c.getDouble(3), c.getInt(4), f));
                }
            } catch (SQLiteException e) {
                Log.d(TAG, "Fehler in getNoten(): " + e.getMessage());
            } finally {
                try {
                    c.close();
                } catch (Exception e) {
                    ;
                }
                try {
                    db.close();
                } catch (Exception e) {
                    ;
                }
            }
        }
        if (ret != null)
            Log.d(TAG, "getNoten() erfolgreich");
        return ret;
    }

    /**
     * Ermittelt zur Nummer der übergebenen Note alle Daten zur Note. Dabei wird
     * zur Note aber nicht das Fach gesetzt
     *
     * @param nummer
     * @return
     */
    public Note getNote(int nummer) {
        Note ret = null;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getWritableDatabase();
            c = db.rawQuery("SELECT * " + "  FROM noten "
                            + "  WHERE nonummer = ?;",
                    new String[]{String.valueOf(nummer)});
            if (c.moveToFirst())
                ret = new Note(c.getInt(0), c.getString(1), c.getLong(2),
                        c.getDouble(3), c.getInt(4));
        } catch (SQLiteException e) {
            Log.d(TAG, "Fehler in getNote(): " + e.getMessage());
        } finally {
            try {
                c.close();
            } catch (Exception e) {
                ;
            }
            try {
                db.close();
            } catch (Exception e) {
                ;
            }
        }
        if (ret != null)
            Log.d(TAG, "getNote() erfolgreich");
        return ret;
    }

    /**
     * Fügt neue Note in die Datenbank ein. Dabei muss die Note korrekt und zur
     * Note das Fach bekannt sein
     *
     * @param n
     * @return
     */
    public int hinzufuegenNote(Note n) {
        int ret = 0;
        if (n == null || n.getFach() == null)
            ret = -1;
        else {
            // Kontrolliere ob Fach korrekt ist
            n.validiere();
            if (n.getFehler() != null)
                ret = -1;
            else {
                // Es wird versucht, Note in die Datenbank aufzunehmen
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    ContentValues werte = new ContentValues(5);
                    werte.put("nobeschreibung", n.getBeschreibung());
                    werte.put("nodatum", n.getDatum());
                    werte.put("nonote", n.getNote());
                    werte.put("nogewicht", n.getGewichtung());
                    werte.put("fanummer", n.getFach().getNummer());
                    ret = (int) db.insertOrThrow("noten", null, werte);
                    if (ret >= 0) {
                        // Schreibt den neuen Primärschlüssel in das
                        // Noten-Objekt
                        n.setNummer(ret);
                        ret = 0;
                    } else {
                        ret = -1;
                    }
                } catch (SQLiteException e) {
                    Log.d(TAG, "Fehler in hinzufuegenNote(): " + e.getMessage());
                    ret = -1;
                } finally {
                    try {
                        db.close();
                    } catch (Exception e) {
                        ;
                    }
                }
            }
        }
        if (ret == 0)
            Log.d(TAG, "hinzufuegenNote() erfolgreich");
        return ret;
    }

    /**
     * Ändert die Note ab. Dabei wird die Note mit der übergebenen Notennummer
     * gesucht
     *
     * @param n
     * @return
     */
    public int aendernNote(Note n) {
        int ret = 0;
        if (n == null)
            ret = -1;
        else {
            // Kontrolliere ob Note korrekt ist
            n.validiere();
            if (n.getFehler() != null)
                ret = -1;
            else {
                SQLiteDatabase db = null;
                try {
                    db = getWritableDatabase();
                    ContentValues werte = new ContentValues(4);
                    werte.put("nobeschreibung", n.getBeschreibung());
                    werte.put("nodatum", n.getDatum());
                    werte.put("nonote", n.getNote());
                    werte.put("nogewicht", n.getGewichtung());
                    if ((int) db.update("noten", werte, "nonummer = ?",
                            new String[]{String.valueOf(n.getNummer())}) == 0)
                        ret = -1;
                } catch (SQLiteException e) {
                    Log.d(TAG, "Fehler in aendernNote(): " + e.getMessage());
                    ret = -1;
                } finally {
                    try {
                        db.close();
                    } catch (Exception e) {
                        ;
                    }
                }

            }
        }
        if (ret == 0)
            Log.d(TAG, "aendernNote() erfolgreich");
        return ret;
    }

    /**
     * Löscht die übergebene Note aus der Datenbank
     *
     * @param n
     * @return
     */
    public int loeschenNote(Note n) {
        int ret = 0;
        if (n == null)
            ret = -1;
        else {
            SQLiteDatabase db = null;
            try {
                db = getWritableDatabase();
                if (db.delete("noten", "nonummer = ?",
                        new String[]{String.valueOf(n.getNummer())}) != 1)
                    ret = -1;
            } catch (SQLiteException e) {
                Log.d(TAG, "Fehler in loeschenNote(): " + e.getMessage());
                ret = -1;
            } finally {
                try {
                    db.close();
                } catch (Exception e) {
                    ;
                }
            }
        }
        if (ret == 0)
            Log.d(TAG, "loeschenNote() erfolgreich");
        return ret;
    }

    /**
     * Löscht alle Noten vom übergebenen Fach
     *
     * @param f
     * @return
     */
    public int loescheAlleNotenVonFach(Fach f) {
        int ret = 0;
        if (f == null) {
            ret = -1;
        } else {
            SQLiteDatabase db = null;
            try {
                db = getWritableDatabase();
                if (db.delete("noten", "fanummer = ?",
                        new String[]{String.valueOf(f.getNummer())}) != -1) {
                    ret = -1;
                }
            } catch (SQLiteException e) {
                Log.d(TAG,
                        "Fehler in loescheAlleNotenVonFach(): "
                                + e.getMessage());
                ret = -1;
            } finally {
                try {
                    db.close();
                } catch (Exception e) {
                    ;
                }
            }
        }
        if (ret == 0) {
            Log.d(TAG, "loescheAlleNotenVonFach() erfolgreich");
        }
        return ret;
    }

    /**
     * Exportiert die Datenbank mit dem Namen "noten.db" in das Verzeichnis
     * /sdcard/com.ffeichta.notenliste/notenliste_backup.db. Bei Erfolg wird 0
     * zurückgeliefert, fehlt die Berechtigung um auf den Speicher zu schreiben
     * -1 und in allen anderen Fällen -2
     */
    public int exportDB() {
        int ret = 0;
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                File direct = new File(
                        Environment.getExternalStorageDirectory()
                                + "/com.ffeichta.notenliste");
                if (!direct.exists()) {
                    if (!direct.mkdir()) {
                        ret = -2;
                    }
                }
                String backupDBPath = "/com.ffeichta.notenliste/notenliste_backup.db";
                File currentDB = new File(data, DATENBANK_PFAD);
                File backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB)
                            .getChannel();
                    FileChannel dst = new FileOutputStream(backupDB)
                            .getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            } else {
                ret = -1;
            }
        } catch (IOException e) {
            Log.d(TAG, "Fehler in exportDB(): " + e.getMessage());
            ret = -2;
        }
        if (ret == 0) {
            Log.d(TAG, "exportDB() erfolgreich");
        }
        return ret;
    }

    /**
     * Importiert die Datenbank, indem die exportierte Datei
     * "notenliste_backup.db" durch die Datei "noten.db" ersetzt wird. Bei
     * Erfolg wird 0 zurückgeliefert, wenn es kein Backup gibt -1 und in allen
     * anderen Fällen -2
     */
    public int importDB() {
        int ret = 0;
        try {
            close();
            File quelle = new File(DATENBANK_BACKUP_PFAD);
            File ziel = new File(DATENBANK_PFAD_IMPORT);
            if (quelle.exists()) {
                com.ffeichta.notenliste.file.File.copyFile(new FileInputStream(
                        quelle), new FileOutputStream(ziel));
                getWritableDatabase().close();
            } else {
                ret = -1;
            }
        } catch (IOException e) {
            Log.d(TAG, "Fehler in importDB(): " + e.getMessage());
            ret = -2;
        }
        if (ret == 0) {
            Log.d(TAG, "importDB() erfolgreich");
        }
        return ret;
    }
}