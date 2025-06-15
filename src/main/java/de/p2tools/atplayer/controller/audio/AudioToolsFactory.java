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

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.history.HistoryList;
import de.p2tools.p2lib.mediathek.audiodata.AudioData;
import de.p2tools.p2lib.mediathek.audiodata.AudioList;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AudioToolsFactory {

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY);

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

    public static void clearAllBookmarks() {
        AudioList audioList = ProgData.getInstance().audioList;
        audioList.forEach(audioData -> audioData.setBookmark(false));
    }

    public static void setFilmShown(ArrayList<AudioData> filmArrayList, boolean setShown) {
        if (setShown) {
            ProgData.getInstance().historyList.addFilmDataListToHistory(filmArrayList);
        } else {
            ProgData.getInstance().historyList.removeFilmDataFromHistory(filmArrayList);
        }
    }

    public static synchronized String getStatusInfosAudio() {
        String textLinks;
        final int sumFilmlist = ProgData.getInstance().audioList.size();
        final int sumFilmShown = ProgData.getInstance().audioGuiController.getCount();

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
}



