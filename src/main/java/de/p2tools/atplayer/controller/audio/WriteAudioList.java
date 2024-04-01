/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.p2tools.atplayer.controller.audio;

import com.fasterxml.jackson.core.JsonGenerator;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.atdata.AudioList;
import de.p2tools.p2lib.atdata.WriteAudioListJson;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;

public class WriteAudioList {

    private final ArrayList<String> logList = new ArrayList<>();

    public WriteAudioList() {
    }

    public synchronized void writeData(Path path, AudioList audioList) {

        logList.add("Audioliste schreiben");
        logList.add("   --> Schreiben nach: " + path.toString());
        try {
            final File file = path.toFile();
            final File dir = new File(file.getParent());
            if (!dir.exists() && !dir.mkdirs()) {
                P2Log.errorLog(932102478, "Kann den Pfad nicht anlegen: " + dir.toString());
                Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                        "Der Pfad zum Schreiben der Audioliste kann nicht angelegt werden: " +
                                P2LibConst.LINE_SEPARATOR + path));
                return;
            }

            logList.add("   --> Anzahl Audios: " + audioList.size());
            if (!write(path.toString(), audioList, logList)) {
                Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                        "Die Audioliste konnte nicht geschrieben werden:" + P2LibConst.LINE_SEPARATOR + path));
            }
            logList.add("   --> geschrieben!");
        } catch (final Exception ex) {
            logList.add("   --> Fehler, nicht geschrieben!");
            P2Log.errorLog(931201478, ex, "nach: " + path);
            Platform.runLater(() -> PAlert.showErrorAlert("Fehler beim Schreiben",
                    "Die Audioliste konnte nicht geschrieben werden:" + P2LibConst.LINE_SEPARATOR + path));
        }

        P2Log.sysLog(logList);
    }

    private boolean write(String file, AudioList audioList, ArrayList<String> logList) {
        try (FileOutputStream fos = new FileOutputStream(file);
             JsonGenerator jg = new WriteAudioListJson().getJsonGenerator(fos)) {
            new WriteAudioListJson().writeJson(jg, audioList);
            return true;
        } catch (Exception ex) {
            logList.add("Schreiben der Datei fehlgeschlagen: " + ex.getLocalizedMessage());
            P2Log.errorLog(846930145, ex, "nach: " + file);
        }
        return false;
    }
}