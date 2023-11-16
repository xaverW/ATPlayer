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


package de.p2tools.atplayer.controller.data.download;

import de.p2tools.atplayer.controller.config.ProgData;
import de.p2tools.atplayer.gui.dialog.DeleteAudioFileDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.mtdownload.MTInfoFile;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import de.p2tools.p2lib.tools.log.PLog;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.nio.file.Path;

public class DownloadDataFactory {

    public static final String DOWNLOAD_PREFIX = "http";
    public static final String DOWNLOAD_SUFFIX_MP4 = "mp4,mp3,m4v,m4a";
    public static final String DOWNLOAD_SUFFIX_U3U8 = "m3u8";

    private DownloadDataFactory() {
    }

    public static void deleteAudioFile(DownloadData download) {
        // Download nur löschen, wenn er nicht läuft
        if (download == null) {
            return;
        }

        if (download.isStateStartedRun()) {
            PAlert.showErrorAlert("Audio löschen", "Download läuft noch", "Download erst stoppen!");
        }


        try {
            // Film
            File filmFile = new File(download.getDestPathFile());
            if (!filmFile.exists()) {
                PAlert.showErrorAlert("Audio löschen", "", "Die Datei existiert nicht!");
                return;
            }

            // Infofile
            File infoFile = null;
            if (download.isInfoFile()) {
                Path infoPath = MTInfoFile.getInfoFilePath(download.getFileNameWithoutSuffix());
                if (infoPath != null) {
                    infoFile = infoPath.toFile();
                }
            }

            String downloadPath = download.getDestPath();
            new DeleteAudioFileDialogController(downloadPath, filmFile, infoFile);


        } catch (Exception ex) {
            PAlert.showErrorAlert("Audio löschen", "Konnte die Datei nicht löschen!", "Fehler beim löschen von:" + P2LibConst.LINE_SEPARATORx2 +
                    download.getDestPathFile());
            PLog.errorLog(915236547, "Fehler beim löschen: " + download.getDestPathFile());
        }
    }

    public static boolean checkDownloadDirect(String url) {
        //auf direkte prüfen, pref oder suf: wenn angegeben dann muss es stimmen
        if (testPrefix(DOWNLOAD_PREFIX, url, true)
                && testPrefix(DOWNLOAD_SUFFIX_MP4, url, false)) {
            return true;
        }
        return false;
    }

    public static boolean checkDownloadM3U8(String url) {
        //auf direkte prüfen, pref oder suf: wenn angegeben dann muss es stimmen
        if (testPrefix(DOWNLOAD_PREFIX, url, true)
                && testPrefix(DOWNLOAD_SUFFIX_U3U8, url, false)) {
            return true;
        }
        return false;
    }

    public static boolean testPrefix(String str, String uurl, boolean prefix) {
        //prüfen ob url beginnt/endet mit einem Argument in str
        //wenn str leer dann true
        boolean ret = false;
        final String url = uurl.toLowerCase();
        String s1 = "";
        if (str.isEmpty()) {
            ret = true;
        } else {
            for (int i = 0; i < str.length(); ++i) {
                if (str.charAt(i) != ',') {
                    s1 += str.charAt(i);
                }
                if (str.charAt(i) == ',' || i >= str.length() - 1) {
                    if (prefix) {
                        //Präfix prüfen
                        if (url.startsWith(s1.toLowerCase())) {
                            ret = true;
                            break;
                        }
                    } else //Suffix prüfen
                        if (url.endsWith(s1.toLowerCase())) {
                            ret = true;
                            break;
                        }
                    s1 = "";
                }
            }
        }
        return ret;
    }

    /**
     * Entferne verbotene Zeichen aus Dateiname.
     *
     * @param name        Dateiname
     * @param isPath
     * @param userReplace
     * @param onlyAscii
     * @return Bereinigte Fassung
     */
    public static String replaceEmptyFileName(String name, boolean isPath, boolean userReplace, boolean onlyAscii) {
        String ret = name;
        boolean isWindowsPath = false;
        if (SystemUtils.IS_OS_WINDOWS && isPath && ret.length() > 1 && ret.charAt(1) == ':') {
            // damit auch "d:" und nicht nur "d:\" als Pfad geht
            isWindowsPath = true;
            ret = ret.replaceFirst(":", ""); // muss zum Schluss wieder rein, kann aber so nicht ersetzt werden
        }

        // zuerst die Ersetzungstabelle mit den Wünschen des Users
        if (userReplace) {
            ret = ProgData.getInstance().replaceList.replace(ret, isPath);
        }

        // und wenn gewünscht: "NUR Ascii-Zeichen"
        if (onlyAscii) {
            ret = FileNameUtils.convertToASCIIEncoding(ret, isPath);
        } else {
            ret = FileNameUtils.convertToNativeEncoding(ret, isPath);
        }

        if (isWindowsPath) {
            // c: wieder herstellen
            if (ret.length() == 1) {
                ret = ret + ":";
            } else if (ret.length() > 1) {
                ret = ret.charAt(0) + ":" + ret.substring(1);
            }
        }
        return ret;
    }
}
