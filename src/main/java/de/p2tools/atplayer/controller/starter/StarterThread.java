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


package de.p2tools.atplayer.controller.starter;

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import de.p2tools.atplayer.controller.data.download.DownloadFactoryStarts;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class StarterThread {
    // ********************************************
    // Hier wird dann gestartet
    // ewige Schleife, die die Downloads startet
    // ********************************************
    private DownloadData download;
    private final java.util.Timer bandwidthCalculationTimer;
    private ProgData progData;
    private final BooleanProperty paused;
    private final BooleanProperty searchFilms;
    private final BooleanProperty checkQuitAfterDownload = new SimpleBooleanProperty(false); // Prüfen, ob autoMode aktiv ist


    public StarterThread(ProgData progData,
                         BooleanProperty paused, BooleanProperty searchFilms) {
        super();
        this.progData = progData;
        this.paused = paused;
        this.searchFilms = searchFilms;

        bandwidthCalculationTimer = new java.util.Timer("BandwidthCalculationTimer");
    }

    public synchronized void run() {
        try {
            if (searchFilms.getValue()) {
                // vorher und während des Suchens der Filmliste machmer nix
                return;
            }

            if ((download = getNextStart()) != null) {
                startDownload(download);
            }

        } catch (final Exception ex) {
            P2Log.errorLog(613822015, ex);
        }
    }

    private synchronized DownloadData getNextStart() throws InterruptedException {
        // ersten passenden Download der Liste zurückgeben oder null
        if (paused.getValue()) {
            //beim Löschen der Downloads kann das Starten etwas "pausiert" werden
            //damit ein zu löschender Download nicht noch schnell gestartet wird
            paused.setValue(false);
            return null;
        }
        return DownloadFactoryStarts.getNextStart(progData.downloadList);
    }

    /**
     * This will start the download process.
     *
     * @param download The {@link DownloadData} info object for download.
     */
    public void startDownload(DownloadData download) {
        download.getDownloadStartDto().startDownload();
        Thread downloadThread;
        downloadThread = new DownloadDirectHttp(progData, download, bandwidthCalculationTimer);
        downloadThread.start();
    }
}
