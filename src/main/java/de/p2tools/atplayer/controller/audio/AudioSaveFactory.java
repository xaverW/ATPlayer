/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.atplayer.gui.dialog.downloadadd.DownloadAddDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.atdata.AudioDataXml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AudioSaveFactory {
    private AudioSaveFactory() {
    }

    public static void saveAudio() {
        final Optional<AudioData> optional = ProgData.getInstance().audioGuiController.getSel();
        if (optional.isEmpty()) {
            return;
        }
        saveAudio(optional.get());
    }


    public static void saveAudio(AudioData audioData) {
        ProgData progData = ProgData.getInstance();
        // erst mal schauen obs den schon gibt
        DownloadData download = progData.downloadList.getDownloadUrlFilm(audioData.arr[AudioDataXml.AUDIO_URL]);
        if (download != null) {
            // dann ist der Film schon in der Downloadliste
            P2Alert.BUTTON answer = P2Alert.showAlert_yes_no("Anlegen?", "Nochmal anlegen?",
                    "Download für den Film existiert bereits:" + P2LibConst.LINE_SEPARATORx2 +
                            audioData.getTitle() + P2LibConst.LINE_SEPARATORx2 +
                            "Nochmal anlegen?");
            switch (answer) {
                case NO:
                    // alles Abbrechen
                    return;
            }
        }
        ArrayList<AudioData> list = new ArrayList<>();
        list.add(audioData);
        new DownloadAddDialogController(progData, list, null);
    }

    public static void saveAllAudios() {
        List<AudioData> list = ProgData.getInstance().audioGuiController.getSelList();
        if (list.isEmpty()) {
            return;
        }

        ProgData progData = ProgData.getInstance();
        ArrayList<AudioData> filmsAddDownloadList = new ArrayList<>();

        for (final AudioData audioData : list) {
            // erst mal schauen obs den schon gibt
            DownloadData download = progData.downloadList.getDownloadUrlFilm(audioData.arr[AudioDataXml.AUDIO_URL]);
            if (download == null) {
                filmsAddDownloadList.add(audioData);
            } else {
                // dann ist der Film schon in der Downloadliste
                if (list.size() <= 1) {
                    P2Alert.BUTTON answer = P2Alert.showAlert_yes_no("Anlegen?", "Nochmal anlegen?",
                            "Download für den Film existiert bereits:" + P2LibConst.LINE_SEPARATORx2 +
                                    audioData.getTitle() + P2LibConst.LINE_SEPARATORx2 +
                                    "Nochmal anlegen?");
                    switch (answer) {
                        case NO:
                            // alles Abbrechen
                            return;
                        case YES:
                            filmsAddDownloadList.add(audioData);
                            break;
                    }

                } else {
                    P2Alert.BUTTON answer = P2Alert.showAlert_yes_no_cancel("Anlegen?", "Nochmal anlegen?",
                            "Download für den Film existiert bereits:" + P2LibConst.LINE_SEPARATORx2 +
                                    audioData.getTitle() + P2LibConst.LINE_SEPARATORx2 +
                                    "Nochmal anlegen (Ja / Nein)?" + P2LibConst.LINE_SEPARATOR +
                                    "Oder alles Abbrechen?");
                    switch (answer) {
                        case CANCEL:
                            // alles Abbrechen
                            return;
                        case NO:
                            continue;
                        case YES:
                            filmsAddDownloadList.add(audioData);
                            break;
                    }
                }
            }
        }
        if (!filmsAddDownloadList.isEmpty()) {
            new DownloadAddDialogController(progData, filmsAddDownloadList, null);
        }
    }
}
