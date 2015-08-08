package com.ffeichta.notenliste.gui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ffeichta.notenliste.model.Item;

import java.util.ArrayList;

/**
 * Diese Klasse legt fest, welche Eigenschaften eines Objektes vom Typ Item wo
 * im Item einer ListView angezeigt werden
 *
 * @author Feichter Fabian
 */
public class UeberAdapter extends ArrayAdapter<Item> {

    /**
     * Konstruktor
     */
    public UeberAdapter(Activity context, ArrayList<Item> items) {
        super(context, R.layout.ueberitem, items);
    }

    /**
     * Diese Methode legt fest, welche TextViews des Layouts welche Inhalte der
     * Item-Eigenschaften darstellen sollen
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        UeberHolder holder = null;
        if (ret == null) {
            // Baut aus Layout-Datei die View auf
            LayoutInflater inflater = ((Activity) getContext())
                    .getLayoutInflater();
            ret = inflater.inflate(R.layout.ueberitem, parent, false);
            holder = new UeberHolder();
            holder.titel = (TextView) ret.findViewById(R.id.ueberitem_titel);
            holder.beschreibung = (TextView) ret
                    .findViewById(R.id.ueberitem_beschreibung);
            ret.setTag(holder);
        } else
            holder = (UeberHolder) ret.getTag();

        // Holt ein Item und gibt dessen Werte in die View
        Item i = getItem(position);
        holder.titel.setText(i.getTitel());
        holder.beschreibung.setText(i.getBeschreibung());
        return ret;
    }

    // Beinhaltet die GUI Komponenten
    private class UeberHolder {
        TextView titel = null;
        TextView beschreibung = null;
    }
}