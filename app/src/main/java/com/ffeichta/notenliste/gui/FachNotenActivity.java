package com.ffeichta.notenliste.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ffeichta.notenliste.model.DBZugriffHelper;
import com.ffeichta.notenliste.model.Fach;
import com.ffeichta.notenliste.model.Note;

import java.util.ArrayList;

/**
 * Activity, welche die Noten eines Faches in einer ListView darstellt und die
 * Möglichkeit bietet, Noten zu erstellen, bearbeiten und löschen. Zudem wird
 * der Durchschnitt des besagten Faches angezeigt
 *
 * @author Feichter Fabian
 */
public class FachNotenActivity extends Activity {

    // GUI Komponenten
    protected ListView listeNotenListView = null;
    protected Button neueNoteButton = null;
    protected TextView durchschnittTextView = null;

    // Beinhaltet alle Noten des aktuellen Faches
    protected ArrayList<Note> noten = null;

    // Nummer des aktuellen Faches
    protected int nummerFach;

    // Speichert die Position des Items in der ListView sobald lange darauf
    // geklickt wird
    protected int contextMenuPosition;

    /**
     * Wird aufgerufen, sobald diese Activity erstellt wird
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachnotenmain);

        listeNotenListView = (ListView) findViewById(R.id.fachnotenmain_liste);
        neueNoteButton = (Button) findViewById(R.id.fachnotenmain_neueNote);

        // Holt die Nummer des Faches
        nummerFach = getIntent().getIntExtra("nummerFach", -1);

        // Gibt den Namen des Faches aus
        Fach f = DBZugriffHelper.getInstance(this).getFach(nummerFach);
        getActionBar().setTitle(f.getName());

        // Die Liste der Noten muss sich anpassen
        noten = f.getNoten();
        if (noten == null) {
            noten = new ArrayList();
        }

        // Custom ArrayAdapter
        FachNotenAdapter adapter = new FachNotenAdapter(this, noten);

        neueNoteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Neue Note wird eingegeben. Aufrufende Activity braucht die
                // Nummer des Faches
                Intent i = new Intent(FachNotenActivity.this,
                        NoteNeuAendernActivity.class);
                i.putExtra("nummerFach", nummerFach);
                // Activity wird gestartet. Warten auf das Ergebnis
                startActivityForResult(i, R.layout.noteneuaendern);
            }
        });

        listeNotenListView.setAdapter(adapter);
        registerForContextMenu(listeNotenListView);
        // Wird aufgerufen, wenn einmal kurz auf ein Item in der ListView
        // geklickt wird
        listeNotenListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Die Note wird geändert. Aufrufende Activity braucht die
                // Nummer der Note und des Faches
                Intent i = new Intent(FachNotenActivity.this,
                        NoteNeuAendernActivity.class);
                i.putExtra("nummerNote", noten.get(position).getNummer());
                i.putExtra("nummerFach", nummerFach);
                // Activity wird gestartet. Warten auf das Ergebnis
                startActivityForResult(i, R.layout.notenappitem);
            }
        });
        // Wird aufgerufen, wenn in der ListView gescrollt wird
        listeNotenListView
                .setOnScrollListener(new AbsListView.OnScrollListener() {
                    int letztesItem;

                    public void onScroll(AbsListView view,
                                         int firstVisibleItem, int visibleItemCount,
                                         int totalItemCount) {
                        if (firstVisibleItem + visibleItemCount == totalItemCount
                                && visibleItemCount < totalItemCount) {
                            neueNoteButton.setVisibility(View.GONE);
                        } else {
                            neueNoteButton.setVisibility(View.VISIBLE);
                        }
                        letztesItem = firstVisibleItem + visibleItemCount - 1;
                    }

                    public void onScrollStateChanged(AbsListView view,
                                                     int scrollState) {
                        // Do nothing
                    }
                });

        // Gibt den Durchschnitt dieses Faches in einer TextView aus
        setDurchschnittText(noten);
    }

    /**
     * Wird insgesamt nur einmal aufgerufen und baut das Menü auf
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fachnotenmenu, menu);
        return true;
    }

    /**
     * Wird aufgerufen, wenn Menüpunkt ausgewählt wurde
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = false;
        switch (item.getItemId()) {
            case R.id.fachnotenmenu_noten_loeschen:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.warnung);
                dialog.setMessage(R.string.fachnotenmainmenu_alle_noten_loeschen);
                dialog.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DBZugriffHelper.getInstance(FachNotenActivity.this)
                                        .loescheAlleNotenVonFach(
                                                DBZugriffHelper.getInstance(
                                                        FachNotenActivity.this)
                                                        .getFach(nummerFach));
                                // Activity wird neu erstellt um die Änderungen
                                // sichtbar zu machen
                                onCreate(null);
                            }
                        });
                dialog.setNegativeButton(R.string.abbrechen, null);
                dialog.setIconAttribute(android.R.attr.alertDialogIcon);
                dialog.show();
                ret = true;
                break;

            default:
                ret = super.onOptionsItemSelected(item);
        }
        return ret;
    }

    /**
     * Methode wird aufgerufen, wenn eine von dieser Activity aufgerufene
     * Activity geschlossen wird, egal ob das Schließen mit dem Fertig-Knopf
     * oder den Zurück-Knopf gemacht wurde
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Die Liste der Noten muss sich anpassen
        noten = DBZugriffHelper.getInstance(this).getFach(nummerFach)
                .getNoten();
        if (noten == null) {
            noten = new ArrayList<Note>();
        }

        FachNotenAdapter adapter = new FachNotenAdapter(this, noten);
        listeNotenListView.setAdapter(adapter);

        // Gibt den Durchschnitt dieses Faches in einer TextView aus
        setDurchschnittText(noten);
    }

    /**
     * Erstellt das ContextMenu nachdem lange auf ein Item in der ListView
     * geklickt wurde
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId() == R.id.fachnotenmain_liste) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.noteaendernloeschenmenu, menu);
            // Holt die Position der Note auf den das ContextMenu erstellt wird
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            contextMenuPosition = info.position;
        }
    }

    /**
     * Wird aufgerufen, wenn Menüpunkt aus dem ContextMenu ausgewählt wurde
     */
    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        boolean ret = false;
        switch (item.getItemId()) {
            case R.id.noteaendernloeschenmenu_note_aendern:
                Intent i = new Intent(FachNotenActivity.this,
                        NoteNeuAendernActivity.class);
                // Übergibt der aufrufenden Activity die Nummer der ausgewählten
                // Note
                i.putExtra("nummerNote", noten.get(contextMenuPosition).getNummer());
                i.putExtra("nummerFach", nummerFach);
                // Activity wird gestartet. Warten auf das Ergebnis
                startActivityForResult(i, R.layout.fachneuaendern);
                // Activity wird neu erstellt um die Änderungen sichtbar zu machen
                onCreate(null);
                ret = true;
                break;

            case R.id.noteaendernloeschenmenu__note_loeschen:
                int erfolg = DBZugriffHelper.getInstance(FachNotenActivity.this)
                        .loeschenNote(noten.get(contextMenuPosition));
                // Activity wird neu erstellt um die Änderungen sichtbar zu machen
                onCreate(null);
                ret = true;
                break;

            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }
        return ret;
    }

    /**
     * Berechnet den Durchschnitt dieses Faches und gibt diesen in der TextView
     * am unteren Rand aus. Es wird beachtet, dass das Fach keine Noten haben
     * kann
     *
     * @param noten
     */
    public void setDurchschnittText(ArrayList<Note> noten) {
        durchschnittTextView = (TextView) findViewById(R.id.fachnotenmain_durchschnittFach);
        String avg = String.valueOf(DBZugriffHelper
                .getInstance(FachNotenActivity.this).getFach(nummerFach)
                .getDurchschnittGerundet());
        if (avg.equals("-1.0")) {
            avg = "N/A";
        }
        Fach f = DBZugriffHelper.getInstance(this).getFach(nummerFach);
        durchschnittTextView.setText(getResources().getString(
                R.string.fachnotenmain_durchschnitt)
                + " " + f.getName() + ": " + avg);
    }
}