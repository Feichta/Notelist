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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ffeichta.notenliste.model.DBZugriffHelper;
import com.ffeichta.notenliste.model.Fach;

import java.util.ArrayList;

/**
 * Hauptactivity der App, welche die einzelnen Fächer in einer ListView
 * darstellt und die Möglichkeit bietet, Fächer zu erstellen, bearbeiten und
 * löschen. Zudem wird der Durchschnitt aller Fächer angezeigt
 *
 * @author Feichter Fabian
 */
public class NotenAppActivity extends Activity {

    // GUI Komponenten
    protected ListView listeFaecherListView = null;
    protected Button neuesFachButton = null;
    protected TextView durchschnittTextView = null;

    // Beinhaltet alle aktuellen Fächer
    protected ArrayList<Fach> faecher = null;

    // Speichert die Position des Items in der ListView sobald lange darauf
    // geklickt wird
    protected int contextMenuPosition;

    /**
     * Wird aufgerufen, sobald diese Activity erstellt wird
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notenappmain);
        getActionBar().setTitle(R.string.notenappmain_titel);

        // Die Liste der Fächer muss sich anpassen
        faecher = DBZugriffHelper.getInstance(this).getFaecher();
        if (faecher == null) {
            faecher = new ArrayList();
        }

        // Custom ArrayAdapter
        NotenAppAdapter adapter = new NotenAppAdapter(this, faecher);

        neuesFachButton = (Button) (findViewById(R.id.notenappmain_neuesFach));
        neuesFachButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(NotenAppActivity.this,
                        FachNeuAendernActivity.class);
                startActivityForResult(i, R.layout.fachneuaendern);
            }
        });

        listeFaecherListView = (ListView) findViewById(R.id.notenappmain_liste);
        listeFaecherListView.setAdapter(adapter);
        registerForContextMenu(listeFaecherListView);
        // Wird aufgerufen, wenn einmal kurz auf ein Item in der ListView
        // geklickt wird
        listeFaecherListView
                .setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent i = new Intent(NotenAppActivity.this,
                                FachNotenActivity.class);
                        // Übergibt der aufrufenden Activity die Nummer des
                        // ausgewählten Faches
                        i.putExtra("nummerFach", faecher.get(position)
                                .getNummer());
                        // Activity wird gestartet. Warten auf das Ergebnis
                        startActivityForResult(i, R.layout.notenappmain);
                    }
                });
        // Wird aufgerufen, wenn in der ListView gescrollt wird
        listeFaecherListView
                .setOnScrollListener(new AbsListView.OnScrollListener() {
                    int letztesItem;

                    public void onScroll(AbsListView view,
                                         int firstVisibleItem, int visibleItemCount,
                                         int totalItemCount) {
                        if (firstVisibleItem + visibleItemCount == totalItemCount
                                && visibleItemCount < totalItemCount) {
                            neuesFachButton.setVisibility(View.GONE);
                        } else {
                            neuesFachButton.setVisibility(View.VISIBLE);
                        }
                        letztesItem = firstVisibleItem + visibleItemCount - 1;
                    }

                    public void onScrollStateChanged(AbsListView view,
                                                     int scrollState) {
                        // Do nothing
                    }
                });

        // Gibt den Durchschnitt aller Fächer in einer TextView aus
        setDurchschnittText(faecher);
    }

    /**
     * Wird insgesamt nur einmal aufgerufen und baut das Menü auf
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notenappmenu, menu);
        return true;
    }

    /**
     * Wird aufgerufen, wenn Menüpunkt ausgewählt wurde
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = false;
        switch (item.getItemId()) {
            case R.id.notenappmenu_faecher_loeschen:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.warnung);
                dialog.setMessage(R.string.notenappmainmenu_alle_faecher_loeschen);
                dialog.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DBZugriffHelper.getInstance(NotenAppActivity.this)
                                        .loeschenAlleFaecher();
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

            case R.id.notenappmenu_exportieren:
                int erfolgExport = DBZugriffHelper.getInstance(
                        NotenAppActivity.this).exportDB();
                switch (erfolgExport) {
                    case 0:
                        String msg = getResources().getString(
                                R.string.notenappmain_backup_erfolgreich)
                                + " " + DBZugriffHelper.DATENBANK_BACKUP_PFAD;
                        Toast.makeText(NotenAppActivity.this, msg, Toast.LENGTH_SHORT)
                                .show();
                        break;

                    case -1:
                        Toast.makeText(NotenAppActivity.this,
                                R.string.notenappmain_backup_berechtigung,
                                Toast.LENGTH_SHORT).show();
                        break;

                    case -2:
                        Toast.makeText(NotenAppActivity.this, R.string.error,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

            case R.id.notenappmenu_importieren:
                AlertDialog.Builder dialogImport = new AlertDialog.Builder(this);
                dialogImport.setTitle(R.string.warnung);
                dialogImport.setMessage(R.string.notenappmainmenu_importieren);
                dialogImport.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int erfolgImport = DBZugriffHelper.getInstance(
                                        NotenAppActivity.this).importDB();
                                switch (erfolgImport) {
                                    case 0:
                                        Toast.makeText(
                                                NotenAppActivity.this,
                                                R.string.notenappmain_import_erfolgreich,
                                                Toast.LENGTH_SHORT).show();
                                        break;

                                    case -1:
                                        Toast.makeText(
                                                NotenAppActivity.this,
                                                getResources()
                                                        .getString(
                                                                R.string.notenappmain_backup_nicht_vorhanden)
                                                        + " "
                                                        + DBZugriffHelper.DATENBANK_BACKUP_PFAD,
                                                Toast.LENGTH_SHORT).show();
                                        break;

                                    case -2:
                                        Toast.makeText(NotenAppActivity.this,
                                                R.string.error, Toast.LENGTH_SHORT)
                                                .show();
                                        break;

                                }
                                // Activity wird neu erstellt um die Änderungen
                                // sichtbar zu machen
                                onCreate(null);
                            }
                        });
                dialogImport.setNegativeButton(R.string.abbrechen, null);
                dialogImport.setIconAttribute(android.R.attr.alertDialogIcon);
                dialogImport.show();
                break;

            case R.id.notenappmenu_ueber:
                Intent i = new Intent(NotenAppActivity.this, UeberActivity.class);
                startActivityForResult(i, R.layout.notenappmain);
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

        // Die Liste der Fächer muss sich anpassen
        faecher = DBZugriffHelper.getInstance(this).getFaecher();
        if (faecher == null) {
            faecher = new ArrayList<Fach>();
        }

        NotenAppAdapter adapter = new NotenAppAdapter(this, faecher);
        listeFaecherListView.setAdapter(adapter);

        // Gibt den Durchschnitt aller Fächer in einer TextView aus
        setDurchschnittText(faecher);
    }

    /**
     * Erstellt das ContextMenu nachdem lange auf ein Item in der ListView
     * geklickt wurde
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        if (view.getId() == R.id.notenappmain_liste) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.fachaendernloeschenmenu, menu);
            // Holt die Position des Faches auf den das ContextMenu erstellt
            // wird
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
            case R.id.fachaendernloeschenmenu_fach_aendern:
                Intent i = new Intent(NotenAppActivity.this,
                        FachNeuAendernActivity.class);
                // Übergibt der aufrufenden Activity die Nummer des ausgewählten
                // Faches
                i.putExtra("nummerFach", faecher.get(contextMenuPosition)
                        .getNummer());
                // Activity wird gestartet. Warten auf das Ergebnis
                startActivityForResult(i, R.layout.fachneuaendern);
                // Activity wird neu erstellt um die Änderungen sichtbar zu machen
                onCreate(null);
                ret = true;
                break;

            case R.id.fachaendernloeschenmenu_fach_loeschen:
                // Die Liste der Fächer muss sich anpassen
                faecher = DBZugriffHelper.getInstance(this).getFaecher();
                int erfolg = DBZugriffHelper.getInstance(NotenAppActivity.this)
                        .loeschenFach(faecher.get(contextMenuPosition));
                // Die Liste der Fächer muss sich anpassen
                faecher = DBZugriffHelper.getInstance(this).getFaecher();
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
     * Berechnet den Durchschnitt aller Fächer und gibt diesen in der TextView
     * am unteren Rand aus. Es wird beachtet, dass es Fächer ohne Noten geben
     * kann
     *
     * @param faecher
     */
    public void setDurchschnittText(ArrayList<Fach> faecher) {
        durchschnittTextView = (TextView) findViewById(R.id.notenappmain_durchschnittAlleFaecher);
        String avg = "N/A";
        double sum = 0;
        if (faecher.size() != 0) {
            int anzahl = 0;
            for (Fach f : faecher) {
                if (f.getDurchschnittNichtGerundet() != -1) {
                    sum += f.getDurchschnittNichtGerundet();
                    anzahl++;
                }
            }
            if (anzahl > 0) {
                avg = String.valueOf(Math.round(sum / anzahl * 100.0) / 100.0);
            }
        }
        durchschnittTextView.setText(getResources().getString(
                R.string.notenappmain_durchschnitt)
                + " " + avg);
    }
}