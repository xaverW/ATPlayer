/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
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

package de.p2tools.atplayer.controller.history;

import de.p2tools.atplayer.controller.audio.AudioTools;
import de.p2tools.atplayer.controller.config.PListener;
import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.atdata.AudioData;
import de.p2tools.p2lib.mtfilm.film.FilmDataXml;
import de.p2tools.p2lib.tools.date.P2DateConst;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class HistoryList extends SimpleListProperty<HistoryData> {

    private final HashSet<String> urlHash = new HashSet<>();
    private final String settingsDir;
    private final String fileName;
    private FilteredList<HistoryData> filteredList = null;
    private SortedList<HistoryData> sortedList = null;
    private final BooleanProperty isWorking = new SimpleBooleanProperty(false);
    private final boolean bookmark;
    private boolean found = false;

    public HistoryList(String fileName, String settingsDir, boolean bookmark) {
        super(FXCollections.observableArrayList());
        this.settingsDir = settingsDir;
        this.fileName = fileName;
        this.bookmark = bookmark;
    }

    public void loadList() {
        // beim Programmstart laden
        P2Duration.counterStart("loadList");
        HistoryFactory.readHistoryDataFromFile(settingsDir, fileName, this);
        fillUrlHash();
        P2Duration.counterStop("loadList");
    }

    public SortedList<HistoryData> getSortedList() {
        filteredList = getFilteredList();
        if (sortedList == null) {
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<HistoryData> getFilteredList() {
        if (filteredList == null) {
            filteredList = new FilteredList<>(this, p -> true);
        }
        return filteredList;
    }

    public synchronized void filteredListSetPredicate(Predicate<HistoryData> predicate) {
        filteredList.setPredicate(predicate);
    }

    public synchronized void filteredListSetPredFalse() {
        filteredList.setPredicate(p -> false);
    }

    public synchronized void filteredListSetPredTrue() {
        filteredList.setPredicate(p -> true);
    }

    //===============
    public synchronized void clearAll(Stage stage) {
        final int size = this.size();
        final String title;
        if (bookmark) {
            title = "Bookmarks";
        } else {
            title = "Filme";
        }

        if (size <= 1 || P2Alert.showAlertOkCancel(stage, "Löschen", title + " löschen",
                "Soll die gesamte Liste " +
                        "(" + size + " " + title + ")" +
                        " gelöscht werden?")) {
            clearList();
            HistoryFactory.deleteHistoryFile(settingsDir, fileName);
            if (bookmark) {
                AudioTools.clearAllBookmarks();
            }
            PListener.notify(PListener.EVENT_HISTORY_CHANGED, HistoryList.class.getSimpleName());
        }
    }

    //===============
    public synchronized boolean checkIfUrlAlreadyIn(String urlFilm) {
        // wenn url gefunden, dann true zurück
        return urlHash.contains(urlFilm);
    }


    //===============
    //ADD
    //===============
    public synchronized void addHistoryDataToHistory(String theme, String title, String url) {
        // einen Film in die History schreiben
        if (checkIfUrlAlreadyIn(url)) {
            return;
        }

        P2Duration.counterStart("addHistoryDataToHistory");
        final ArrayList<HistoryData> list = new ArrayList<>();
        final String datum = P2DateConst.F_FORMAT_dd_MM_yyyy.format(new Date());
        HistoryData historyData = new HistoryData(datum, theme, title, url);
        addToThisList(historyData);
        list.add(historyData);

        writeToFile(list, true);
        P2Duration.counterStop("addHistoryDataToHistory");
    }

    public synchronized void addFilmDataListToHistory(List<AudioData> filmList) {
        // eine Liste Filme in die History schreiben
        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        final ArrayList<HistoryData> list = new ArrayList<>(filmList.size());
        final String datum = P2DateConst.F_FORMAT_dd_MM_yyyy.format(new Date());

        P2Duration.counterStart("addFilmDataToHistory");
        for (final AudioData film : filmList) {
            if (bookmark) {
                film.setBookmark(true);
            } else {
                // auch wenn schon in der History, dann doch den Film als gesehen markieren
                film.setShown(true);
            }

            if (checkIfUrlAlreadyIn(film.getUrl())) {
                continue;
            }

            HistoryData historyData = new HistoryData(datum, film.arr[FilmDataXml.FILM_THEME], film.arr[FilmDataXml.FILM_TITLE], film.getUrl());
            addToThisList(historyData);
            list.add(historyData);
        }

        writeToFile(list, true);
        P2Duration.counterStop("addFilmDataToHistory");
    }

    public synchronized void addDownloadDataListToHistory(List<DownloadData> downloadList) {
        // eine Liste Downloads in die History schreiben
        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        final ArrayList<HistoryData> list = new ArrayList<>(downloadList.size());
        final String datum = P2DateConst.F_FORMAT_dd_MM_yyyy.format(new Date());

        P2Duration.counterStart("addDownloadDataListToHistory");
        for (final DownloadData download : downloadList) {
            // auch wenn schon in der History, dann doch den Film als gesehen markieren
            if (bookmark && download.getFilm() != null) {
                download.getFilm().setBookmark(true);

            } else if (download.getFilm() != null) {
                download.getFilm().setShown(true);
            }

            if (checkIfUrlAlreadyIn(download.getFilmUrl())) {
                continue;
            }

            HistoryData historyData = new HistoryData(datum, download.getTheme(), download.getTitle(), download.getFilmUrl());
            addToThisList(historyData);
            list.add(historyData);
        }

        writeToFile(list, true);
        P2Duration.counterStop("addDownloadDataListToHistory");
    }


    //===============
    //remove
    //===============
    public synchronized void removeHistoryDataFromHistory(ArrayList<HistoryData> historyDataList) {
        // Historydaten aus der History löschen und File wieder schreiben
        if (historyDataList == null || historyDataList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(historyDataList.size() + 1, 0.75F);
        for (HistoryData historyData : historyDataList) {
            hash.add(historyData.getUrl());
        }

        // in den Filmen für die zu löschenden URLs history löschen
        ProgData.getInstance().audioList.forEach(audioData -> {
            if (hash.contains(audioData.getUrl())) {
                audioData.setShown(false);
            }
        });

        removeFromHistory(hash);
        hash.clear();
        P2Duration.counterStop("History: removeDataFromHistory");
    }

    public synchronized void removeFilmDataFromHistory(ArrayList<AudioData> filmList) {
        // eine Liste Filme aus der History löschen und File wieder schreiben
        if (filmList == null || filmList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(filmList.size() + 1, 0.75F);
        filmList.forEach(film -> {
            if (bookmark) {
                film.setBookmark(false);

            } else {
                film.setShown(false); // todo mal vormerken ob evtl. die ganze Filmliste nach dieser URL durchsucht werden soll, wird sonst erst beim nächsten Start angezeigt
            }

            hash.add(film.getUrl());
        });

        removeFromHistory(hash);
        hash.clear();
        P2Duration.counterStop("History: removeDataFromHistory");
    }

    public synchronized void removeDownloadDataFromHistory(List<DownloadData> downloadList) {
        // eine Liste Downloads aus der History löschen und File wieder schreiben
        if (downloadList == null || downloadList.isEmpty()) {
            return;
        }

        P2Duration.counterStart("History: removeDataFromHistory");
        final HashSet<String> hash = new HashSet<>(downloadList.size() + 1, 0.75F);
        downloadList.forEach(download -> {
            if (bookmark && download.getFilm() != null) {
                download.getFilm().setBookmark(false);

            } else if (download.getFilm() != null) {
                download.getFilm().setShown(false);
            }

            hash.add(download.getFilmUrl());
        });

        removeFromHistory(hash);
        hash.clear();
        P2Duration.counterStop("History: removeDataFromHistory");
    }

    private void removeFromHistory(HashSet<String> removeUrlHash) {
        final ArrayList<HistoryData> newHistoryList = new ArrayList<>();
        found = false;
        P2Duration.counterStart("History: removeFromHistory");
        P2Log.sysLog("Aus Historyliste löschen: " + removeUrlHash.size() + ", löschen aus: " + fileName);

        waitWhileWorking(); // wird diese Liste abgesucht

        this.forEach(historyData -> {
            if (removeUrlHash.contains(historyData.getUrl())) {
                // nur dann muss das Logfile auch geschrieben werden
                found = true;
            } else {
                // kommt wieder in die history
                newHistoryList.add(historyData);
            }
        });

        if (found) {
            // und nur dann wurde was gelöscht und muss geschrieben werden
            replaceThisList(newHistoryList);
            writeToFile(newHistoryList, false);
        }

        P2Duration.counterStop("History: removeFromHistory");
    }

    //===============
    private void writeToFile(List<HistoryData> list, boolean append) {
        waitWhileWorkingAndSetWorking();

        try {
            Thread th = new Thread(new HistoryWriteToFile(settingsDir, fileName, list, append, isWorking));
            th.setName("HistoryWriteToFile");
            th.start();
            // th.run();
        } catch (Exception ex) {
            P2Log.errorLog(912030254, ex, "writeToFile");
            isWorking.setValue(false);
        }
    }

    private void waitWhileWorking() {
        while (isWorking.get()) {
            // sollte nicht passieren, aber wenn ..
            P2Log.errorLog(741025896, "waitWhileWorking: write to history file");

            try {
                wait(100);
            } catch (final Exception ex) {
                P2Log.errorLog(915236547, ex, "waitWhileWorking");
                isWorking.setValue(false);
            }
        }

    }

    private void waitWhileWorkingAndSetWorking() {
        waitWhileWorking();
        isWorking.setValue(true);
    }

    //===============
    private void clearList() {
        urlHash.clear();
        this.clear();
    }

    private void addToThisList(HistoryData historyData) {
        this.add(historyData);
        urlHash.add(historyData.getUrl());
    }

    private void replaceThisList(List<HistoryData> historyData) {
        clearList();
        this.addAll(historyData);
        fillUrlHash();
    }

    private void fillUrlHash() {
        urlHash.clear();
        this.forEach(h -> urlHash.add(h.getUrl()));
    }
}
