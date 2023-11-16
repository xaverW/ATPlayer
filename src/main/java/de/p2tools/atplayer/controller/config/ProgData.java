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


package de.p2tools.atplayer.controller.config;

import de.p2tools.atplayer.ATPlayerController;
import de.p2tools.atplayer.controller.data.ReplaceList;
import de.p2tools.atplayer.controller.data.download.DownloadInfos;
import de.p2tools.atplayer.controller.data.download.DownloadList;
import de.p2tools.atplayer.controller.filter.ActFilterWorker;
import de.p2tools.atplayer.controller.filter.AudioFilterRunner;
import de.p2tools.atplayer.controller.history.HistoryList;
import de.p2tools.atplayer.controller.starter.StarterClass;
import de.p2tools.atplayer.controller.worker.CheckForNewFilmlist;
import de.p2tools.atplayer.controller.worker.Worker;
import de.p2tools.atplayer.gui.AudioGuiController;
import de.p2tools.atplayer.gui.dialog.QuitDialogController;
import de.p2tools.p2lib.atdata.AudioList;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPane;
import de.p2tools.p2lib.tools.duration.PDuration;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgData {
    private static ProgData instance;

    // flags
    public static boolean debug = false; // Debugmodus
    public static boolean duration = false; // Duration ausgeben
    public static boolean reset = false; // Programm auf Starteinstellungen zurücksetzen
    public static boolean firstProgramStart = false; // ist der allererste Programmstart: Init wird gemacht
    public static BooleanProperty AUDIOLIST_IS_DOWNLOADING = new SimpleBooleanProperty(Boolean.FALSE); // dann wird eine Audioliste geladen

    // Infos
    public static String configDir = ""; // Verzeichnis zum Speichern der Programmeinstellungen

    public PShortcut pShortcut; // verwendete Shortcuts
    public ActFilterWorker actFilterWorker; // gespeicherte Filterprofile
    public AudioFilterRunner audioFilterRunner;
    public DownloadList downloadList; // Filme die als "Download" geladen werden sollen
    public StarterClass starterClass; // Klasse zum Ausführen der Programme (für die Downloads): VLC, flvstreamer, ...

    // Gui
    public Stage primaryStage = null;
    public P2MaskerPane maskerPane = new P2MaskerPane();
    public ATPlayerController ATPlayerController = null;
    public AudioGuiController audioGuiController = null; // Tab mit den Audios
    public QuitDialogController quitDialogController = null;

    // Worker
    public Worker worker; // Liste aller Sender, Themen, ...
    public CheckForNewFilmlist checkForNewFilmlist;

    // Programmdaten
    public AudioList audioList; // ist die komplette Audioliste

    public DownloadInfos downloadInfos;
    public ReplaceList replaceList;
    public HistoryList historyList; // alle angesehenen Filme
    public HistoryList historyListBookmarks; // markierte Filme

    boolean oneSecond = false;

    private ProgData() {
        pShortcut = new PShortcut();
        replaceList = new ReplaceList();

        actFilterWorker = new ActFilterWorker(this);
        audioList = new AudioList();

        historyList = new HistoryList(ProgConst.FILE_HISTORY,
                ProgInfos.getSettingsDirectory_String(), false);
        historyListBookmarks = new HistoryList(ProgConst.FILE_BOOKMARKS,
                ProgInfos.getSettingsDirectory_String(), true);
        downloadList = new DownloadList(this);
        starterClass = new StarterClass(this);
        downloadInfos = new DownloadInfos(this);
        audioFilterRunner = new AudioFilterRunner(this);
        worker = new Worker(this);
        checkForNewFilmlist = new CheckForNewFilmlist();

    }

    public synchronized static final ProgData getInstance(String dir) {
        if (!dir.isEmpty()) {
            configDir = dir;
        }
        return getInstance();
    }

    public synchronized static final ProgData getInstance() {
        return instance == null ? instance = new ProgData() : instance;
    }

    public void startTimer() {
        // extra starten, damit er im Einrichtungsdialog nicht dazwischen funkt
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(500), ae -> {

            oneSecond = !oneSecond;
            if (oneSecond) {
                doTimerWorkOneSecond();
            }
            doTimerWorkHalfSecond();

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setDelay(Duration.seconds(5));
        timeline.play();
        PDuration.onlyPing("Timer gestartet");
    }

    private void doTimerWorkOneSecond() {
        PListener.notify(PListener.EVENT_TIMER, ProgData.class.getName());
    }

    private void doTimerWorkHalfSecond() {
        PListener.notify(PListener.EVENT_TIMER_HALF_SECOND, ProgData.class.getName());
    }
}
