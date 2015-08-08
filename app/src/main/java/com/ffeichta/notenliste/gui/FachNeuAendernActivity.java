package com.ffeichta.notenliste.gui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ffeichta.notenliste.model.DBZugriffHelper;
import com.ffeichta.notenliste.model.Fach;

import java.util.Hashtable;

/**
 * Activity, welche die Möglichkeit bietet, Fächer zu erstellen und bearbeiten
 *
 * @author Wild Michael & Feichter Fabian
 */
public class FachNeuAendernActivity extends Activity {

    // GUI Komponenten
    protected TextView titelTextView = null;
    protected Button fertigButton = null;
    protected Button abbrechenTextView = null;
    protected EditText nameEditText = null;
    protected EditText lehrpersonEditText = null;
    protected TextView nameFehlerTextView = null;
    protected TextView lehrpersonFehlerTextView = null;

    // Enthält jenes Fach, welches erstellt/bearbeitet wird
    protected Fach fach = null;

    // Standard Hintergrund eines EditText
    private Drawable originalBackground = null;

    /**
     * Wird aufgerufen, sobald diese Activity erstellt wird
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachneuaendern);

        titelTextView = (TextView) findViewById(R.id.fachneuaendern_titel);
        fertigButton = (Button) findViewById(R.id.fachneuaendern_fertig);
        abbrechenTextView = (Button) findViewById(R.id.fachneuaendern_abbrechen);
        nameEditText = (EditText) findViewById(R.id.fachneuaendern_name);
        lehrpersonEditText = (EditText) findViewById(R.id.fachneuaendern_lehrperson);
        nameFehlerTextView = (TextView) findViewById(R.id.fachneuaendern_name_fehler);
        lehrpersonFehlerTextView = (TextView) findViewById(R.id.fachneuaendern_lehrperson_fehler);

        // Kontrolliere ob neues Fach eingegeben oder Fach geändert werden soll.
        // Soll ein neues Fach eingegeben werden, wird der Activity kein Integer
        // übergeben und 'nummerFach' wird auf -1 gesetzt
        int nummerFach = getIntent().getIntExtra("nummerFach", -1);
        if (nummerFach == -1) {
            // Neues Fach wird eingegeben
            titelTextView.setText(R.string.fachneuaendern_fachhinzufuegen);
            fach = new Fach();
        } else {
            // Bestehendes Fach wird geändert
            titelTextView.setText(R.string.fachneuaendern_fachaendern);
            // Das Fach muss sich anpassen
            fach = DBZugriffHelper.getInstance(this).getFach(nummerFach);
            // Fülle Eingabefelder mit dem Fach
            nameEditText.setText(this.fach.getName());
            lehrpersonEditText.setText(this.fach.getLehrperson());
        }

        fertigButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (originalBackground == null) {
                    originalBackground = nameEditText.getBackground();
                }
                // Fehlermeldungen werden ausgeblendet und EditText Objekte
                // werden auf das
                // Standard Design gesetzt
                nameEditText.setBackgroundColor(Color.TRANSPARENT);
                nameEditText.setBackgroundDrawable(originalBackground);
                nameFehlerTextView.setVisibility(View.GONE);
                lehrpersonFehlerTextView.setVisibility(View.GONE);
                lehrpersonEditText.setBackgroundColor(Color.TRANSPARENT);
                lehrpersonEditText.setBackgroundDrawable(originalBackground);

                // Die eingegebenen Werte werden in das Fach-Objekt geschrieben
                fach.setName(nameEditText.getText().toString());
                fach.setLehrperson(lehrpersonEditText.getText().toString());

                int erfolg = 0;
                if (fach.getNummer() < 0)
                    // Das Fach wird in die Datenbank gespeichert
                    erfolg = DBZugriffHelper.getInstance(
                            FachNeuAendernActivity.this).hinzufuegenFach(fach);
                else
                    // Das Fach wird in der Datenbank geändert
                    erfolg = DBZugriffHelper.getInstance(
                            FachNeuAendernActivity.this).aendernFach(fach);
                if (erfolg != 0) {
                    // Das Fach ist fehlerhaft, Fehlermeldungen werden angezeigt
                    Hashtable<String, Integer> fehler = fach.getFehler();
                    if (fehler != null && fehler.get("name") != null) {
                        nameFehlerTextView.setVisibility(View.VISIBLE);
                        if (fehler.get("name") == Fach.FACH_NAME_NICHT_GESETZT) {
                            nameEditText.setBackgroundColor(Color
                                    .parseColor("#FFC8C8"));
                            nameFehlerTextView
                                    .setText(R.string.fehler_eingabe_erforderlich);
                        }
                        if (fehler.get("name") == Fach.FACH_FACHNAME_BEREITS_VORHANDEN) {
                            nameEditText.setBackgroundColor(Color
                                    .parseColor("#FFC8C8"));
                            nameFehlerTextView
                                    .setText(R.string.fehler_fachname_bereits_vorhanden);
                        }
                    }
                    if (fehler != null && fehler.get("lehrperson") != null) {
                        lehrpersonFehlerTextView.setVisibility(View.VISIBLE);
                        if (fehler.get("lehrperson") == Fach.FACH_LEHRPERSON_NICHT_GESETZT)
                            lehrpersonEditText.setBackgroundColor(Color
                                    .parseColor("#FFC8C8"));
                        lehrpersonFehlerTextView
                                .setText(R.string.fehler_eingabe_erforderlich);
                    }
                } else {
                    // Activity wird geschlossen
                    FachNeuAendernActivity.this.setResult(RESULT_OK);
                    FachNeuAendernActivity.this.finish();
                }
            }
        });

        abbrechenTextView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Activity wird geschlossen
                finish();
            }
        });
    }
}