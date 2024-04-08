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

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.config.ProgIcons;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.atplayer.controller.downloadtools.DownloadProgParameterFactory;
import de.p2tools.atplayer.controller.downloadtools.RuntimeExec;
import de.p2tools.atplayer.controller.history.HistoryList;
import de.p2tools.atplayer.gui.dialog.downloadadd.DownloadAddDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.atdata.AudioDataXml;
import de.p2tools.p2lib.atdata.AudioList;
import de.p2tools.p2lib.guitools.P2Open;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static de.p2tools.atplayer.controller.downloadtools.RuntimeExec.TRENNER_PROG_ARRAY;

public class AudioTools {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);

    public static void playAudio() {
        final Optional<AudioData> filmSelection = ProgData.getInstance().audioGuiController.getSel();
        if (filmSelection.isEmpty()) {
            return;
        }
        playAudio(filmSelection.get());
    }

    public static void playAudio(AudioData audioData) {
        if (audioData == null) {
            return;
        }

        final String url = audioData.getUrl();
        String strProgCallArray = "";
        strProgCallArray = DownloadProgParameterFactory.getProgParameterArray(url);
        String[] arrProgCallArray = strProgCallArray.split(TRENNER_PROG_ARRAY);

        P2Open.playStoredFilm(arrProgCallArray, ProgConfig.SYSTEM_PROG_PLAY,
                url, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());

        List<AudioData> list = new ArrayList<>();
        list.add(audioData);
        ProgData.getInstance().historyList.addFilmDataListToHistory(list);
    }

    public static void playAllAudios() {
        List<AudioData> audioDataList = ProgData.getInstance().audioGuiController.getSelList();
        if (audioDataList.isEmpty()) {
            return;
        }

        final String url = buildUrl(audioDataList);
        String strProgCallArray = "";
        strProgCallArray = DownloadProgParameterFactory.getProgParameterArray(url);
        String[] arrProgCallArray = strProgCallArray.split(TRENNER_PROG_ARRAY);

        P2Open.playStoredFilm(arrProgCallArray, ProgConfig.SYSTEM_PROG_PLAY,
                url, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());

        ProgData.getInstance().historyList.addFilmDataListToHistory(audioDataList);
    }

    private static String buildUrl(List<AudioData> films) {
        // die URL bauen
        if (films.size() <= 1) {
            return films.get(0).getUrl();
        }

        StringBuilder url = new StringBuilder();
        boolean append = false;
        for (AudioData audioData : films) {
            String u = audioData.getUrl();
            if (!append) {
                append = true;
            } else {
                url.append(RuntimeExec.TRENNER_PROG_ARRAY);
            }
            url.append(u);
        }
        return url.toString();
    }

    public static void playAudio(DownloadData downloadData) {
        String strProgCallArray = "";
        strProgCallArray = DownloadProgParameterFactory.getProgParameterArray(downloadData.getDestPathFile());
        String[] arrProgCallArray = strProgCallArray.split(TRENNER_PROG_ARRAY);

        P2Open.playStoredFilm(arrProgCallArray, ProgConfig.SYSTEM_PROG_PLAY,
                downloadData.getDestPathFile(), ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());

        List<DownloadData> list = new ArrayList<>();
        list.add(downloadData);
        ProgData.getInstance().historyList.addDownloadDataListToHistory(list);
    }

    public static void bookmarkFilmList(ArrayList<AudioData> filmArrayList, boolean bookmark) {
        if (bookmark) {
            ProgData.getInstance().historyListBookmarks.addFilmDataListToHistory(filmArrayList);
        } else {
            ProgData.getInstance().historyListBookmarks.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void changeBookmarkFilm(AudioData film) {
        if (film.isBookmark()) {
            // dann ausschalten
            ArrayList<AudioData> filmArrayList = new ArrayList<>(1);
            filmArrayList.add(film);
            bookmarkFilmList(filmArrayList, false);
        } else {
            ArrayList<AudioData> filmArrayList = new ArrayList<>(1);
            filmArrayList.add(film);
            bookmarkFilmList(filmArrayList, true);
        }
    }

    public static void setFilmShown(ArrayList<AudioData> filmArrayList, boolean setShown) {
        if (setShown) {
            ProgData.getInstance().historyList.addFilmDataListToHistory(filmArrayList);
        } else {
            ProgData.getInstance().historyList.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static void markShownAndBookmarks() {
        AudioList audioList = ProgData.getInstance().audioList;
        HistoryList bookmarks = ProgData.getInstance().historyListBookmarks;
        audioList.forEach(audioData -> {
            audioData.setShown(ProgData.getInstance().historyList.checkIfUrlAlreadyIn(audioData.getUrl()));
            if (bookmarks.checkIfUrlAlreadyIn(audioData.getUrl())) {
                audioData.setBookmark(true);
            }
        });
    }

    public static synchronized String getStatusInfosAudio() {
        String textLinks;
        final int sumFilmlist = ProgData.getInstance().audioList.size();
        final int sumFilmShown = ProgData.getInstance().audioGuiController.getFilmCount();

        String sumFilmlistStr = numberFormat.format(sumFilmShown);
        String sumFilmShownStr = numberFormat.format(sumFilmlist);

        // Anzahl der
        if (sumFilmShown == 1) {
            textLinks = "1 Beitrag";
        } else {
            textLinks = sumFilmlistStr + " Beiträge";
        }
        if (sumFilmlist != sumFilmShown) {
            textLinks += " (Insgesamt: " + sumFilmShownStr + " )";
        }
        return textLinks;
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

    public static void clearAllBookmarks() {
        AudioList audioList = ProgData.getInstance().audioList;
        audioList.forEach(audioData -> audioData.setBookmark(false));
    }
}


