/*
 * P2Tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.atplayer.controller.downloadtools;

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.controller.data.download.DownloadData;
import javafx.beans.property.LongProperty;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Timer;

public class DownloadMp3 {
    private ProgData progData;
    private DownloadData downloadData;

    public DownloadMp3(ProgData progData,
                       DownloadData downloadData) {
        this.progData = progData;
        this.downloadData = downloadData;
    }

    public void download(HttpURLConnection conn, Timer bandwidthCalculationTimer,
                         File file, LongProperty downloaded) throws Exception {

//
//        downloadData.getDownloadStartDto().setInputStream(new MLInputStream(conn.getInputStream(),
//                bandwidthCalculationTimer,
//                new SimpleIntegerProperty(MLBandwidthTokenBucket.BANDWIDTH_RUN_FREE)/* ProgConfig.DOWNLOAD_MAX_BANDWIDTH_BYTE*/,
//                ProgData.AUDIOLIST_IS_DOWNLOADING));
//
//        FileOutputStream fos = new FileOutputStream(file, (downloaded.get() != 0));
//        downloadData.getDownloadSize().addActFileSize(downloaded.get());
//        final byte[] buffer = new byte[MLBandwidthTokenBucket.DEFAULT_BUFFER_SIZE];
//        double percent, ppercent = DownloadConstants.PROGRESS_WAITING, startPercent = DownloadConstants.PROGRESS_NOT_STARTED;
//        int len;
//        long aktBandwidth = 0, aktSize = 0;
//
//        while ((len = downloadData.getDownloadStartDto().getInputStream().read(buffer)) != -1 && (!downloadData.isStateStopped())) {
//            downloaded.set(downloaded.get() + len);
//            fos.write(buffer, 0, len);
//            downloadData.getDownloadSize().addActFileSize(len);
//
//            // für die Anzeige prüfen ob sich was geändert hat
//            if (aktSize != downloadData.getDownloadSize().getActuallySize()) {
//                aktSize = downloadData.getDownloadSize().getActuallySize();
//            }
//            if (downloadData.getDownloadSize().getTargetSize() > 0) {
//                percent = 1.0 * aktSize / downloadData.getDownloadSize().getTargetSize();
//                if (startPercent == DownloadConstants.PROGRESS_NOT_STARTED) {
//                    startPercent = percent;
//                }
//
//                // percent muss zwischen 0 und 1 liegen
//                if (percent == DownloadConstants.PROGRESS_WAITING) {
//                    percent = DownloadConstants.PROGRESS_STARTED;
//                } else if (percent >= DownloadConstants.PROGRESS_FINISHED) {
//                    percent = DownloadConstants.PROGRESS_NEARLY_FINISHED;
//                }
//                downloadData.setProgress(percent);
//                if (percent != ppercent) {
//                    ppercent = percent;
//
//                    // Restzeit ermitteln
//                    if (percent > (DownloadConstants.PROGRESS_STARTED) && percent > startPercent) {
//                        // sonst macht es noch keinen Sinn
//                        long timeLeft = 0;
//                        long sizeLeft = downloadData.getDownloadSize().getTargetSize() - downloadData.getDownloadSize().getActuallySize();
//                        if (sizeLeft <= 0) {
//                            timeLeft = 0;
//                        } else if (aktBandwidth > 0) {
//                            timeLeft = sizeLeft / aktBandwidth;
//                        }
//                        downloadData.getDownloadStartDto().setTimeLeftSeconds(timeLeft);
//
//                        // anfangen zum Schauen kann man, wenn die Restzeit kürzer ist
//                        // als die bereits geladene Speilzeit des Films
//                        HttpDownloadFactory.canAlreadyStarted(downloadData);
//                    }
//                }
//            }
//            aktBandwidth = downloadData.getDownloadStartDto().getInputStream().getBandwidth(); // bytes per second
//            if (aktBandwidth != downloadData.getDownloadStartDto().getBandwidth()) {
//                downloadData.getDownloadStartDto().setBandwidth(aktBandwidth);
//            }
//        }
//
//        if (!downloadData.isStateStopped()) {
//            StringProperty strErrorMsg = new SimpleStringProperty();
//            if (StarterClass.check(progData, downloadData, strErrorMsg)) {
//                // Anzeige ändern - fertig
//                downloadData.setStateFinished();
//            } else {
//                // Anzeige ändern - bei Fehler fehlt der Eintrag
//                downloadData.setStateError(strErrorMsg.getValueSafe());
//            }
//        }
//        if (fos != null) {
//            fos.close();
//        }
    }
}
