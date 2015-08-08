package com.ffeichta.notenliste.gui;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ffeichta.notenliste.model.Note;

import java.util.ArrayList;

/**
 * Diese Klasse legt fest, welche Eigenschaften einer Note wo im Item einer
 * ListView angezeigt werden
 *
 * @author Wild Michael & Feichter Fabian
 */
public class FachNotenAdapter extends ArrayAdapter<Note> {

    /*
     * Konstruktor
     */
    public FachNotenAdapter(Activity context, ArrayList<Note> noten) {
        super(context, R.layout.fachnotenitem, noten);
    }

    /**
     * Diese Methode legt fest, welche Textfelder des Layouts welche Inhalte der
     * Note-Eigenschaften darstellen sollen
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = convertView;
        FachHolder holder = null;
        if (ret == null) {
            // Baut aus Layout-Datei die View auf
            LayoutInflater inflater = ((Activity) getContext())
                    .getLayoutInflater();
            ret = inflater.inflate(R.layout.fachnotenitem, parent, false);
            holder = new FachHolder();
            holder.typ = (TextView) ret.findViewById(R.id.fachnotenitem_typ);
            holder.gewichtung = (TextView) ret
                    .findViewById(R.id.fachnotenitem_gewichtung);
            holder.note = (TextView) ret.findViewById(R.id.fachnotenitem_note);

            ret.setTag(holder);
        } else {
            holder = (FachHolder) ret.getTag();
        }

        // Holt eine Note aus der ArrayList und gibt dessen Werte in die View
        Note n = getItem(position);
        holder.typ.setText(n.getBeschreibung());
        holder.gewichtung.setText(String.valueOf(n.getGewichtung()) + "%");
        holder.note.setText(String.valueOf(n.getNote()));
        Log.d("HALLO", String.valueOf(n.getNote()) + ":\t\t\t\t " + position);
        if (n.istNegativ()) {
            holder.note.setTextColor(Color.RED);
        } else {
            holder.note.setTextColor(parent.getResources().getColor(R.color.text_primary));
        }
        return ret;
    }

    // Beinhaltet die GUI Komponenten
    private class FachHolder {
        TextView typ = null;
        TextView gewichtung = null;
        TextView note = null;
    }
}