package com.ffeichta.notenliste.gui;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ffeichta.notenliste.model.Fach;

import java.util.ArrayList;

/**
 * Diese Klasse legt fest, welche Eigenschaften eines Faches wo im Item einer
 * ListView angezeigt werden
 *
 * @author Wild Michael & Feichter Fabian
 */
public class NotenAppAdapter extends ArrayAdapter<Fach> {

    /*
     * Konstruktor
     */
    public NotenAppAdapter(Activity context, ArrayList<Fach> faecher) {
        super(context, R.layout.notenappitem, faecher);
    }

    /**
     * Diese Methode legt fest, welche Textfelder des Layouts welche Inhalte der
     * Fach-Eigenschaften darstellen sollen
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        FachHolder holder = null;
        if (ret == null) {
            // Baut aus Layout-Datei die View auf
            LayoutInflater inflater = ((Activity) getContext())
                    .getLayoutInflater();
            ret = inflater.inflate(R.layout.notenappitem, parent, false);
            holder = new FachHolder();
            holder.name = (TextView) ret.findViewById(R.id.notenappitem_name);
            holder.lehrperson = (TextView) ret
                    .findViewById(R.id.notenappitem_lehrperson);
            holder.notenString = (TextView) ret
                    .findViewById(R.id.notenappitem_notenstring);
            holder.durchschnitt = (TextView) ret
                    .findViewById(R.id.notenappitem_durchschnitt);
            ret.setTag(holder);
        } else
            holder = (FachHolder) ret.getTag();

        // Holt ein Fach aus der ArrayList und gibt dessen Werte in die View
        Fach f = getItem(position);
        holder.name.setText(f.getName());
        holder.lehrperson.setText(f.getLehrperson());
        holder.notenString.setText(f.getNotenString());
        // Hat ein Fach keine Noten, wird kein Durchschnitt ausgegeben
        if (f.getDurchschnittGerundet() > -1) {
            holder.durchschnitt.setText(String.valueOf(f
                    .getDurchschnittGerundet()));
            if (f.istAvgNegativ()) {
                holder.durchschnitt.setTextColor(Color.RED);
            } else {
                holder.durchschnitt.setTextColor(parent.getResources().getColor(R.color.text_primary));
            }
        } else {
            holder.durchschnitt.setText("N/A");
            holder.durchschnitt.setTextColor(parent.getResources().getColor(R.color.text_primary));
        }
        return ret;
    }

    // Beinhaltet die GUI Komponenten
    private class FachHolder {
        TextView name = null;
        TextView lehrperson = null;
        TextView notenString = null;
        TextView durchschnitt = null;
    }
}