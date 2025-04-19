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
import de.p2tools.atplayer.controller.StringLists;
import de.p2tools.atplayer.controller.data.ReplaceList;
import de.p2tools.atplayer.controller.data.blackdata.BlackList;
import de.p2tools.atplayer.controller.data.blackdata.BlackListFilter;
import de.p2tools.atplayer.controller.data.download.DownloadInfos;
import de.p2tools.atplayer.controller.data.download.DownloadList;
import de.p2tools.atplayer.controller.data.downloaderror.DownloadErrorList;
import de.p2tools.atplayer.controller.filter.AudioFilterRunner;
import de.p2tools.atplayer.controller.filter.FilterWorker;
import de.p2tools.atplayer.controller.history.HistoryList;
import de.p2tools.atplayer.controller.starter.StartDownload;
import de.p2tools.atplayer.controller.worker.Busy;
import de.p2tools.atplayer.controller.worker.Worker;
import de.p2tools.atplayer.gui.AudioGuiController;
import de.p2tools.atplayer.gui.DownloadGuiController;
import de.p2tools.atplayer.gui.chart.ChartData;
import de.p2tools.atplayer.gui.dialog.QuitDialogController;
import de.p2tools.p2lib.atdata.AudioList;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPane;
import de.p2tools.p2lib.p2event.P2EventHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.Stage;

public class ProgData {
    private static ProgData instance;
    public static Busy busy;

    // flags
    public static boolean debug = false; // Debugmodus
    public static boolean duration = false; // Duration ausgeben
    public static boolean reset = false; // Programm auf Starteinstellungen zurücksetzen
    public static boolean firstProgramStart = false; // ist der allererste Programmstart: Init wird gemacht
    public static BooleanProperty AUDIOLIST_IS_DOWNLOADING = new SimpleBooleanProperty(Boolean.FALSE); // dann wird eine Audioliste geladen

    // Infos
    public static String configDir = ""; // Verzeichnis zum Speichern der Programmeinstellungen

    public PShortcut pShortcut; // verwendete Shortcuts
    public FilterWorker filterWorker; // gespeicherte Filterprofile
    public AudioFilterRunner audioFilterRunner;
    public DownloadList downloadList; // Filme die als "Download" geladen werden sollen

    public static BooleanProperty AUDIO_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty DOWNLOAD_TAB_ON = new SimpleBooleanProperty(Boolean.FALSE);

    // Gui
    public Stage primaryStage = null;
    public P2MaskerPane maskerPane = new P2MaskerPane();
    public ATPlayerController atPlayerController = null;
    public AudioGuiController audioGuiController = null; // Tab mit den Audios
    public DownloadGuiController downloadGuiController = null; // Tab mit den Audios
    public QuitDialogController quitDialogController = null;
    public final ChartData chartData;

    // Worker
    public Worker worker; // Liste aller Sender, Themen, ...

    // Programmdaten
    public StartDownload startDownload; // Klasse zum Ausführen der Programme (für die Downloads): VLC, ...
    public AudioList audioList; // ist die komplette Audioliste
    public AudioList audioListFiltered; // nach der Blacklist
    public StringLists stringListsLists; // sind die Text-Filter in den CBO's

    public DownloadInfos downloadInfos;
    public ReplaceList replaceList;
    public HistoryList historyList; // alle angesehenen Filme
    public HistoryList historyListBookmarks; // markierte Filme
    public BlackList blackList;
    public final BlackListFilter blackListFilterBlackList;
    public DownloadErrorList downloadErrorList;
    public P2EventHandler pEventHandler;

    private ProgData() {
        pEventHandler = new P2EventHandler(false);

        busy = new Busy();
        pShortcut = new PShortcut();
        replaceList = new ReplaceList();

        filterWorker = new FilterWorker();
        audioList = new AudioList();
        audioListFiltered = new AudioList();
        stringListsLists = new StringLists();
        downloadErrorList = new DownloadErrorList();

        historyList = new HistoryList(ProgConst.FILE_HISTORY,
                ProgInfos.getSettingsDirectory_String(), false);
        historyListBookmarks = new HistoryList(ProgConst.FILE_BOOKMARKS,
                ProgInfos.getSettingsDirectory_String(), true);
        blackList = new BlackList(this);
        blackListFilterBlackList = new BlackListFilter();
        downloadList = new DownloadList(this);

        chartData = new ChartData();
        startDownload = new StartDownload(this);
        downloadInfos = new DownloadInfos(this);
        audioFilterRunner = new AudioFilterRunner(this);
        worker = new Worker(this);
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
}
