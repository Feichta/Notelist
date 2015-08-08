package com.ffeichta.notenliste.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Klasse, welche nur die Methode copyFile() in sich hat, welche für das
 * Importieren der Datenbank verwendet wird
 *
 * @author Feichter Fabian
 */
public class File {

    /**
     * Kopiert den Inhalt einer Datei in eine andere Datei. Die Methode bekommt
     * zwei FileInputStreams von zwei Dateien, wobei <code>fromFile</code> die Quelle und
     * <code>toFile</code> das Ziel darstellt. Der Name und der Pfad der Zieldatei
     * entsprechen der Quelldatei. Ist die Zieldatei nicht vorhanden, wird sie
     * erstellt
     *
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copyFile(FileInputStream fromFile,
                                FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }
}