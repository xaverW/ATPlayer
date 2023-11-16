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


package de.p2tools.atplayer.gui.dialog.downloadadd;

import de.p2tools.atplayer.controller.config.ProgConfig;
import de.p2tools.atplayer.controller.data.download.DownloadDataFactory;
import de.p2tools.p2lib.mtdownload.SizeTools;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class DownloadAddDialogFactory {

    private DownloadAddDialogFactory() {
    }

    public static Text getText(String text) {
        Text t = new Text(text);
        t.setFont(Font.font(null, FontWeight.BOLD, -1));
        if (ProgConfig.SYSTEM_DARK_THEME.getValue()) {
            t.setFill(Color.rgb(31, 162, 206));
        } else {
            t.setFill(Color.BLUE);
        }
        return t;
    }

    public static boolean getTime(String name, FastDateFormat format) {
        String ret = "";
        Date d;
        try {
            ret = name.substring(name.lastIndexOf(File.separator) + 1);
            d = new Date(format.parse(ret).getTime());
        } catch (Exception ignore) {
            d = null;
        }

        if (d != null && format.getPattern().length() == ret.length()) {
            return true;
        }
        return false;
    }

    public static String getNextName(String stdPath, String actDownPath, String theme) {
        String ret = actDownPath;

        theme = DownloadDataFactory.replaceEmptyFileName(theme,
                false /* pfad */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        if (actDownPath.endsWith(File.separator)) {
            ret = actDownPath.substring(0, actDownPath.length() - File.separator.length());
        }

        try {
            final String date = DateFactory.F_FORMAT_yyyyMMdd.format(new Date());
            final boolean isDate = DownloadAddDialogFactory.getTime(ret, DateFactory.F_FORMAT_yyyyMMdd);
            final boolean isTheme = ret.endsWith(theme) && !theme.isEmpty();
            final boolean isStandard = actDownPath.equals(stdPath);

            if (isStandard) {
                Path path = Paths.get(stdPath, (theme.isEmpty() ? date : theme));
                ret = path.toString();

            } else if (isTheme) {
                Path path = Paths.get(stdPath, date);
                ret = path.toString();

            } else if (isDate) {
                Path path = Paths.get(stdPath);
                ret = path.toString();

            } else {
                Path path = Paths.get(stdPath);
                ret = path.toString();

            }
        } catch (Exception ex) {
            PLog.errorLog(978451203, ex);
            ret = stdPath;
        }
        return ret;
    }

    /**
     * Calculate free disk space on volume and checkIfExists if the movies can be safely downloaded.
     */
    public static void calculateAndCheckDiskSpace(String path, Label lblFree, AddDownloadData addDownloadData) {
        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            String noSize = "";
            String sizeFree = "";

            long usableSpace = DownloadAddDialogFactory.getFreeDiskSpace(path);
            if (usableSpace > 0) {
                sizeFree = SizeTools.humanReadableByteCount(usableSpace, true);
            }

            // jetzt noch prüfen, obs auf die Platte passt
            usableSpace /= 1_000_000;
            if (usableSpace <= 0) {
                lblFree.setText("");

            } else {
                int size;
                if (!addDownloadData.download.getDownloadSize().getTargetSizeMBStr().isEmpty()) {
                    size = Integer.parseInt(addDownloadData.download.getDownloadSize().getTargetSizeMBStr());
                    if (size > usableSpace) {
                        noSize = ", nicht genug";
                    }
                }

                if (noSize.isEmpty()) {
                    lblFree.setText(" [ noch frei: " + sizeFree + " ]");
                } else {
                    lblFree.setText(" [ noch frei: " + sizeFree + noSize + " ]");
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the free disk space for a selected path.
     *
     * @return Free disk space in bytes.
     */
    private static long getFreeDiskSpace(final String strPath) {
        long usableSpace = 0;
        if (!strPath.isEmpty()) {
            try {
                Path path = Paths.get(strPath);
                if (!Files.exists(path)) {
                    path = path.getParent();
                }
                final FileStore fileStore = Files.getFileStore(path);
                usableSpace = fileStore.getUsableSpace();
            } catch (final Exception ignore) {
            }
        }
        return usableSpace;
    }

//    public static String getFilmSize(AddDownloadData addDownloadData) {
//        switch (addDownloadData.resolution) {
//            case FilmDataMTP.RESOLUTION_HD:
//                return addDownloadData.fileSize_HD;
//
//            case FilmDataMTP.RESOLUTION_SMALL:
//                return addDownloadData.fileSize_small;
//
//            case FilmDataMTP.RESOLUTION_NORMAL:
//            default:
//                return addDownloadData.fileSize_high;
//        }
//    }

    public static boolean checkPathWritable(String path) {
        boolean ret = false;
        final File testPath = new File(path);
        try {
            if (!testPath.exists()) {
                testPath.mkdirs();
            }
            if (path.isEmpty()) {
            } else if (!testPath.isDirectory()) {
            } else if (testPath.canWrite()) {
                final File tmpFile = File.createTempFile("mtplayer", "tmp", testPath);
                tmpFile.delete();
                ret = true;
            }
        } catch (final Exception ignored) {
        }
        return ret;
    }


}