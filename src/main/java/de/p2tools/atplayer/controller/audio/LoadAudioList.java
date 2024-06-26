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


import de.p2tools.atplayer.controller.config.ProgInfos;
import de.p2tools.p2lib.atdata.AudioListFactory;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadListener;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadNotifier;
import de.p2tools.p2lib.tools.date.P2LDateFactory;
import de.p2tools.p2lib.tools.duration.P2Duration;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadAudioList {

    private static final AtomicBoolean stop = new AtomicBoolean(false); // damit kann das Laden gestoppt werden kann
    public final P2LoadNotifier p2LoadNotifier = new P2LoadNotifier();
    private final BooleanProperty propLoadAudiolist = new SimpleBooleanProperty(false);

    public LoadAudioList() {
    }

    public void setStart(P2LoadEvent event) {
        p2LoadNotifier.notifyEvent(P2LoadNotifier.NOTIFY.START, event);
    }

    public void setProgress(P2LoadEvent event) {
        p2LoadNotifier.notifyEvent(P2LoadNotifier.NOTIFY.PROGRESS, event);
    }

    public void setLoaded(P2LoadEvent event) {
        // das wird öfters aufgerufen
        p2LoadNotifier.notifyEvent(P2LoadNotifier.NOTIFY.LOADED, event);
    }

    public void setFinished(P2LoadEvent event) {
        p2LoadNotifier.notifyEvent(P2LoadNotifier.NOTIFY.FINISHED, event);
    }

    public void setFinished() {
        p2LoadNotifier.notifyFinishedOk();
    }

    public synchronized boolean isStop() {
        return stop.get();
    }

    public synchronized void setStop(boolean set) {
        stop.set(set);
    }

    public synchronized void setMax(int max) {
    }

    public boolean getPropLoadAudiolist() {
        return propLoadAudiolist.get();
    }

    public BooleanProperty propLoadAudiolistProperty() {
        return propLoadAudiolist;
    }

    public void setPropLoadAudiolist(boolean propLoadAudiolist) {
        this.propLoadAudiolist.set(propLoadAudiolist);
    }

    public void loadNewListFromWeb() {
        // aus dem Menü oder Button in den Einstellungen
        setPropLoadAudiolist(true);
        setStart(new P2LoadEvent("Audioliste aus dem Web laden",
                P2LoadListener.PROGRESS_INDETERMINATE, 0, false));

        new Thread(() -> {
            final List<String> logList = new ArrayList<>();
            P2Duration.counterStart("loadNewListFromWeb");

            //damit wird eine neue Liste (Web) geladen UND auch gleich im Config-Ordner gespeichert
            logList.add("");
            logList.add("## " + P2Log.LILNE1);
            logList.add("## Audioliste aus dem Web laden - start");
            logList.add("## Alte Liste erstellt  am: " + LoadAudioFactoryDto.audioListDate);
            logList.add("##            Anzahl Beiträge: " + LoadAudioFactoryDto.audioListAkt.size());
            logList.add("##            Anzahl  Neue: " + AudioListFactory.countNewAudios(LoadAudioFactoryDto.audioListAkt));
            logList.add("##");

            new ReadAudioList().readDb(false, ProgInfos.getAndMakeAudioListFile());
            afterLoading(logList);

            logList.add("## Audioliste aus dem Web laden - ende");
            logList.add("## " + P2Log.LILNE1);
            logList.add("");
            P2Log.sysLog(logList);

            setPropLoadAudiolist(false);
            P2Duration.counterStop("loadNewListFromWeb");
        }).start();
    }

    /**
     * Audioliste beim Programmstart laden
     */
    public void loadAtProgStart() {
        // nur einmal direkt nach dem Programmstart
        setPropLoadAudiolist(true);
        setStart(new P2LoadEvent("Programmstart, Liste laden",
                P2LoadListener.PROGRESS_INDETERMINATE, 0, false));

        new Thread(() -> {
            final List<String> logList = new ArrayList<>();
            P2Duration.counterStart("loadAudioListProgStart");

            logList.add("");
            logList.add("## " + P2Log.LILNE1);
            logList.add("## Audioliste beim **Programmstart** laden - start");

            loadAtProgStart(logList);
            afterLoading(logList);

            logList.add("## Audioliste beim Programmstart laden - ende");
            logList.add("## " + P2Log.LILNE1);
            logList.add("");
            P2Log.sysLog(logList);

            setPropLoadAudiolist(false);
            P2Duration.counterStop("loadAudioListProgStart");
        }).start();
    }

    /**
     * Audioliste beim Programmstart laden
     */
    private void loadAtProgStart(List<String> logList) {
        // einer der ZWEI Einstiegspunkte zum Laden: ProgStart / sofort Web
        // hier wird die gespeicherte Audioliste geladen und wenn zu alt, wird eine neue aus
        // dem Web geladen
        boolean audioListTooOld = false;
        if (LoadAudioFactoryDto.firstProgramStart) {
            // gespeicherte Audioliste laden, macht beim ersten Programmstart keinen Sinn
            logList.add("## Erster Programmstart -> Liste aus dem Web laden");
            new ReadAudioList().readDb(false, ProgInfos.getAndMakeAudioListFile());
            P2Duration.onlyPing("Erster Programmstart: Neu Audioliste aus dem Web geladen");

        } else {
            // dann ist ein normaler Start mit vorhandener Audioliste, muss auf jeden Fall geladen werden > Hash
            logList.add("## Beim Programmstart soll keine neue Liste geladen werden");
            logList.add("## Programmstart: Gespeicherte Liste aus laden");
            new ReadAudioList().readDb(true, ProgInfos.getAndMakeAudioListFile());
            logList.add("## Programmstart: Gespeicherte Liste geladen");

            if (LoadAudioFactoryDto.loadNewAudioListOnProgramStart) {
                //dann wird eine neue Liste aus dem Web beim Programmstart geladen, wenn nötig
                if (LoadAudioFactory.isNotFromToday(LoadAudioFactoryDto.audioListDate.getValueSafe())) {
                    //gespeicherte Liste zu alt
                    logList.add("## Gespeicherte Audioliste ist zu alt: " + LoadAudioFactoryDto.audioListDate.getValueSafe());
                    audioListTooOld = true;

                } else {
                    logList.add("## Gespeicherte Audioliste ist nicht zu alt: " + LoadAudioFactoryDto.audioListDate.getValueSafe());
                }

                if (LoadAudioFactoryDto.audioListNew.isEmpty()) {
                    //dann ist sie leer
                    logList.add("## Gespeicherte Audioliste ist leer, neue Audioliste aus dem Web laden");
                    logList.add("## " + P2Log.LILNE3);
                }

                if (audioListTooOld || LoadAudioFactoryDto.audioListNew.isEmpty()) {
                    //dann war sie zu alt oder ist leer
                    setProgress(new P2LoadEvent("Audioliste ist zu alt, eine neue laden",
                            P2LoadListener.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

                    logList.add("## Programmstart: Neue Liste aus dem Web laden");
                    new ReadAudioList().readDb(false, ProgInfos.getAndMakeAudioListFile());
                    P2Duration.onlyPing("Programmstart: Neu Audioliste aus dem Web geladen");
                }
            }

            if (LoadAudioFactoryDto.audioListNew.isEmpty()) {
                // dann hat das alles nicht geklappt??
                logList.add("## Das Laden der Liste hat nicht geklappt");
                logList.add("## Noch ein Versuch: Gespeicherte Liste aus laden");
                new ReadAudioList().readDb(true, ProgInfos.getAndMakeAudioListFile());
                logList.add("## Gespeicherte Liste geladen");
            }
        }

        setLoaded(new P2LoadEvent("Audios verarbeiten",
                P2LoadListener.PROGRESS_INDETERMINATE, 0, false/* Fehler */));
    }

    // #######################################
    // #######################################
    private void afterLoading(List<String> logList) {
        logList.add("##");
        logList.add("## Jetzige Liste erstellt am: " + P2LDateFactory.getNowString());
        logList.add("##   Anzahl Audios: " + LoadAudioFactoryDto.audioListNew.size());
        logList.add("##");
        logList.add("## " + P2Log.LILNE2);
        logList.add("##");

        setLoaded(new P2LoadEvent("Audios markieren, Themen suchen",
                P2LoadListener.PROGRESS_INDETERMINATE, 0, false/* Fehler */));
        LoadAudioFactoryDto.audioListNew.loadSenderAndGenre();

        setLoaded(new P2LoadEvent("Audios in Downloads eingetragen",
                P2LoadListener.PROGRESS_INDETERMINATE, 0, false/* Fehler */));
        logList.add("## Audios in Downloads eingetragen");

        //die List wieder füllen
        logList.add("## ==> und jetzt die Audioliste wieder füllen :)");
        Platform.runLater(() -> {
            LoadAudioFactoryDto.audioListAkt.sender = LoadAudioFactoryDto.audioListNew.sender;
            LoadAudioFactoryDto.audioListAkt.genre = LoadAudioFactoryDto.audioListNew.genre;
            LoadAudioFactoryDto.audioListAkt.metaData = LoadAudioFactoryDto.audioListNew.metaData;
            LoadAudioFactoryDto.audioListAkt.setAll(LoadAudioFactoryDto.audioListNew);
            LoadAudioFactoryDto.audioListNew.clear();
            setFinished();
        });
    }
}