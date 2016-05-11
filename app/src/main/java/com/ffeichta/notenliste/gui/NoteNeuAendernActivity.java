package com.ffeichta.notenliste.gui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ffeichta.notenliste.model.DBZugriffHelper;
import com.ffeichta.notenliste.model.Note;

import java.util.Calendar;
import java.util.Hashtable;

/**
 * Activity, welche die Mˆglichkeit bietet, Noten zu erstellen und bearbeiten
 *
 * @author Feichter Fabian
 */
public class NoteNeuAendernActivity extends Activity {

    // GUI Komponenten
    protected EditText beschreibungEditText = null;
    protected EditText noteEditText = null;
    protected TextView gewichtungProzentTextView = null;
    protected SeekBar gewichtungSeekBar = null;
    protected DatePicker datePickerDatePicker = null;
    protected TextView beschreibungFehlerTextView = null;
    protected TextView noteFehlerTextView = null;

    // Enth‰lt jene Note, welche erstellt/bearbeitet wird
    protected Note note = null;

    // Nummer des aktuellen Faches
    protected int nummerFach;

    // ‹ber Calendar Objekt wird das Datum einer bestehenden Note in die
    // TextViews ausgegeben oder das eingegebene Datum wird zu einer Note
    // hinzugef¸gt
    protected Calendar cal = null;

    // Standard Hintergrund eines EditText
    private Drawable originalBackground = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noteneuaendern);

        beschreibungEditText = (EditText) findViewById(R.id.noteneuaendern_beschreibung);
        beschreibungEditText.setFocusableInTouchMode(true);
        beschreibungEditText.requestFocus();
        noteEditText = (EditText) findViewById(R.id.noteneuaendern_note);
        gewichtungProzentTextView = (TextView) findViewById(R.id.noteneuaendern_prozent);
        gewichtungSeekBar = (SeekBar) findViewById(R.id.noteneuaendern_gewichtung);
        datePickerDatePicker = (DatePicker) findViewById(R.id.noteneuaendern_date);
        beschreibungFehlerTextView = (TextView) findViewById(R.id
                .noteneuaendern_fehler_beschreibung);
        noteFehlerTextView = (TextView) findViewById(R.id.noteneuaendern_fehler_note);

        int nummerNote = getIntent().getIntExtra("nummerNote", -1);
        nummerFach = getIntent().getIntExtra("nummerFach", -1);

        if (nummerNote == -1) {
            // Neue Note wird eingegeben
            note = new Note();
            getActionBar().setTitle(getResources().getString(R.string.noteneuaendern_neue_note) +
                    DBZugriffHelper.getInstance(NoteNeuAendernActivity.this)
                            .getFach(nummerFach).getName());
        } else {
            // Bestehende Note wird ge‰ndert
            getActionBar().setTitle(getResources().getString(R.string.noteneuaendern_note_aendern) +
                    DBZugriffHelper.getInstance(NoteNeuAendernActivity.this)
                            .getFach(nummerFach).getName());
            // Hole zu ‰ndernde Note aus der Datenbank
            note = DBZugriffHelper.getInstance(this).getNote(nummerNote);

            // F¸lle Eingabefelder mit der Note
            cal = note.getDatumCalendar();
            datePickerDatePicker.updateDate(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            beschreibungEditText.setText(note.getBeschreibung());
            noteEditText.setText(String.valueOf(note.getNote()));
            gewichtungSeekBar.setProgress(note.getGewichtung() / 5 - 1);
            gewichtungProzentTextView.setText(note.getGewichtung() + "%");
        }

        gewichtungSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar,
                                                  int progress, boolean fromUser) {
                        gewichtungProzentTextView.setText(String
                                .valueOf((progress + 1) * 5) + "%");
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // Do nothing
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Do nothing
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fertigmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fertigmenu_fertig:
                if (originalBackground == null) {
                    originalBackground = beschreibungEditText.getBackground();
                }
                // Fehlermeldungen werden ausgeblendet und EditText Objekte
                // werden auf das Standard Design gesetzt
                beschreibungEditText.setBackgroundColor(Color.TRANSPARENT);
                beschreibungEditText.setBackgroundDrawable(originalBackground);
                beschreibungFehlerTextView.setVisibility(View.GONE);
                noteEditText.setBackgroundColor(Color.TRANSPARENT);
                noteEditText.setBackgroundDrawable(originalBackground);
                noteFehlerTextView.setVisibility(View.GONE);

                // Das eingegebene Datum wird in Calendar Object geschrieben
                cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, datePickerDatePicker.getYear());
                cal.set(Calendar.MONTH, datePickerDatePicker.getMonth());
                cal.set(Calendar.DAY_OF_MONTH,
                        datePickerDatePicker.getDayOfMonth());
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                // Die eingegebenen Werte werden in das Fach-Objekt geschrieben
                note.setBeschreibung(String.valueOf(beschreibungEditText
                        .getText()));
                note.setDatum(cal.getTimeInMillis());
                note.setGewichtung(Integer.valueOf((gewichtungSeekBar
                        .getProgress() + 1) * 5));
                note.setFach(DBZugriffHelper.getInstance(
                        NoteNeuAendernActivity.this).getFach(nummerFach));
                if (noteEditText.getText() != null
                        && noteEditText.getText().length() > 0) {
                    note.setNote(Double.valueOf(noteEditText.getText()
                            .toString()));
                } else {
                    note.setNote(-1);
                }

                int erfolg = 0;
                if (note.getNummer() < 0) {
                    // Aufnehmen der neuen Note in die Datenbank
                    erfolg = DBZugriffHelper.getInstance(
                            NoteNeuAendernActivity.this).hinzufuegenNote(note);
                } else {
                    // ƒndern der bestehenden Note in der Datenbank
                    erfolg = DBZugriffHelper.getInstance(
                            NoteNeuAendernActivity.this).aendernNote(note);
                }
                if (erfolg != 0) {
                    // Note ist fehlerhaft, Anzeigen der Fehlermeldungen
                    Hashtable<String, Integer> fehler = note.getFehler();
                    if (fehler != null && fehler.get("beschreibung") != null) {
                        beschreibungFehlerTextView.setVisibility(View.VISIBLE);
                        if (fehler.get("beschreibung") == Note.NOTE_BESCHREIBUNG_NICHT_GESETZT) {
                            beschreibungEditText.setBackgroundColor(Color
                                    .parseColor("#FFC8C8"));
                            beschreibungFehlerTextView
                                    .setText(R.string.fehler_eingabe_erforderlich);
                        }
                    }
                    if (fehler != null && fehler.get("note") != null) {
                        noteFehlerTextView.setVisibility(View.VISIBLE);
                        if (fehler.get("note") == Note.NOTE_NOTE_NICHT_GESETZT) {
                            noteEditText.setBackgroundColor(Color
                                    .parseColor("#FFC8C8"));
                            noteFehlerTextView
                                    .setText(R.string.fehler_eingabe_erforderlich);
                        }
                        if (fehler.get("note") == Note.NOTE_NOTE_ZU_HOCH) {
                            noteEditText.setBackgroundColor(Color
                                    .parseColor("#FFC8C8"));
                            noteFehlerTextView
                                    .setText(R.string.fehler_note_zu_hoch);
                        }
                    }
                } else {
                    // Schlieﬂen der Activity
                    NoteNeuAendernActivity.this.setResult(RESULT_OK);
                    NoteNeuAendernActivity.this.finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}