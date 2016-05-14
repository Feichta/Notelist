package com.ffeichta.notenliste.gui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ffeichta.notenliste.model.Item;

import java.util.ArrayList;

/**
 * Activity, welche Informationen über die App darstellt
 */
public class UeberActivity extends Activity {

    // GUI Komponenten
    protected ListView listeListView = null;

    /**
     * Wird aufgerufen, sobald diese Activity erstellt wird
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uebermain);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Erstellt die Inhalte der Item Objeckte, welche in der Activity in den
        // Items einer ListView angezeigt werden
        ArrayList<Item> items = new ArrayList<Item>();
        items.add(new Item(getResources().getString(
                R.string.ueberactivity_version), getResources().getString(
                R.string.version)));
        items.add(new Item(getResources().getString(
                R.string.ueberactivity_entwickler),
                "Fabian Feichter, TFO \"Max Valier\", Bozen"));
        items.add(new Item(getResources().getString(
                R.string.ueberactivity_website), "www.ffeichta.com"));
        items.add(new Item(getResources().getString(
                R.string.ueberactivity_email), "fabian.feichter@ffeichta.com"));
        items.add(new Item(getResources().getString(
                R.string.ueberactivity_bewerten), "Google Play"));
        items.add(new Item(getResources().getString(
                R.string.ueberactivity_lizenz), "Icon: iconfinder.com / Ivan Boyko / CC " +
                "BY 3.0 / used with changes"));

        // Custom ArrayAdapter
        UeberAdapter adapter = new UeberAdapter(this, items);

        listeListView = (ListView) findViewById(R.id.uebermain_liste);
        listeListView.setAdapter(adapter);
        listeListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        switch (position) {
                            case 2:
                                try {
                                    Intent website = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://www.ffeichta.com"));
                                    startActivity(website);
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    // Wenn ActivityNotFoundException geworfen wird,
                                    // dann ist kein Browser auf dem Gerät
                                    // installiert, in diesem Fall wird nichts
                                    // gemacht
                                }
                                break;

                            case 3:
                                try {
                                    Intent mail = new Intent(Intent.ACTION_SENDTO);
                                    mail.setType("text/plain");
                                    mail.setData(Uri
                                            .parse("mailto:fabian.feichter@ffeichta.com"));
                                    mail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mail.putExtra(Intent.EXTRA_SUBJECT,
                                            "Notenliste");
                                    startActivity(mail);
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    // Wenn ActivityNotFoundException geworfen wird,
                                    // dann ist kein E-Mail Client auf dem Gerät
                                    // installiert, in diesem Fall wird nichts
                                    // gemacht
                                }
                                break;

                            case 4:
                                try {
                                    Intent googlePlayDirect = new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=com.ffeichta" +
                                                    ".notenliste"));
                                    startActivity(googlePlayDirect);
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    // Wenn ActivityNotFoundException geworfen wird,
                                    // dann ist der PlayStore auf dem Gerät nicht
                                    // installiert, der Link wird dann im Browser
                                    // geöffnet
                                    Intent googlePlayBrowser = new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google" +
                                                    ".com/store/apps/details?id=com.ffeichta" +
                                                    ".notenliste"));
                                    startActivity(googlePlayBrowser);
                                }
                                break;

                            case 5:
                                try {
                                    Intent website = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("https://creativecommons.org/licenses/by/3" +
                                                    ".0/"));
                                    startActivity(website);
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    // Wenn ActivityNotFoundException geworfen wird,
                                    // dann ist kein Browser auf dem Gerät
                                    // installiert, in diesem Fall wird nichts
                                    // gemacht
                                }
                                break;

                            default:
                                break;

                        }
                    }
                });
    }

    /**
     * Wird aufgerufen, wenn Menüpunkt ausgewählt wurde
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}